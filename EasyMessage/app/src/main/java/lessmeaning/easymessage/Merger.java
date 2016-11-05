package lessmeaning.easymessage;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class Merger extends Service implements Runnable {

    private volatile boolean running;
    private LocalDataBase localdb;
    private final String SERVER_NAME = "there_will_be_a_server_name";
    private final String API_NAME = "there_will_be_api_name";

    @Override
    public void onCreate() {
        super.onCreate();
        localdb = new LocalDataBase(this);
        new Thread(this).start();
        running = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        running = false;
        if (Build.VERSION.SDK_INT == 19) {
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
    }

    @Override
    public void run() {
        while (running) {
            checkLocal();
            String msg = checkServer();
            if (msg != null && !msg.equals("")) {
                sendMsgToUpd(msg);
            }
        }
    }

    private String checkServer() {
        long time = localdb.getLastTime();

        String lnk = generateLink(time);

        BufferedReader in = null;
        HttpURLConnection conn = null;
        String rawInput = null;
        try {
            conn = (HttpURLConnection) new URL(lnk).openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder buffer = new StringBuilder();
            while (in.ready()) {
                buffer.append(in.readLine());
            }
            rawInput = buffer.substring(0, buffer.indexOf("]"));
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (rawInput == null || rawInput.equals("")) return null;


        try {
            ArrayList<Row> rows = convertToNormal(rawInput);
            if (rows == null || rows.size() == 0) return null;
            localdb.addApproved(rows);
            return rows.get(0).getContent();
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("JSON not correct it is : \n" + rawInput);
        }
        return null;
    }

    private String generateLink(long time) {
        return SERVER_NAME +
                "/" +
                API_NAME +
                "?" +
                Fields.ACTION +
                "=" +
                Actions.GET +
                "&" +
                Fields.TIME +
                "=" +
                time;
    }
    private String generateLink(String msg) {
        return SERVER_NAME +
                "/" +
                API_NAME +
                "?" +
                Fields.ACTION +
                "=" +
                Actions.SEND +
                "&" +
                Fields.MESSAGE +
                "=" +
                msg;
    }

    private ArrayList<Row> convertToNormal(String rawInput) throws JSONException {
        ArrayList<Row> res = new ArrayList<>();
        JSONArray array = new JSONArray(rawInput);
        JSONObject row;
        int len = array.length();
        for (int i = 0; i < len; i++) {
            row = array.getJSONObject(i);
            res.add(new Row(row.getString(Fields.MESSAGE + ""),
                    row.getLong(Fields.TIME + "")));
        }
        Collections.sort(res);
        return res;
    }


    private boolean sendToServer(String msg) {
        String lnk = generateLink(msg);
        HttpURLConnection conn = null;
        BufferedReader in = null;
        boolean success = false;
        try {
            conn = (HttpURLConnection) new URL(lnk).openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while (in.ready() && !success) {
                success =  in.readLine().contains(Fields.SUCCESS + "");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }



    private void checkLocal() {
        ArrayList<Pair<String, Integer>> freshRows = localdb.getTemp();
        int len = freshRows.size();
        if (len == 0) return;
        boolean success = true;
        for (int i = 0; i < len && success; i++) {
            success = sendToServer(freshRows.get(i).first);
            if (success) {
                localdb.deleteTempRow(freshRows.get(i).second);
            }
        }
    }

    private void sendMsgToUpd(String msg) {
        sendBroadcast(new Intent(LocalCore.BROADCAST));
        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra(MessageReceiver.MESSAGE, msg);
        sendBroadcast(intent);

    }

    private boolean checkIsActivityAlive(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
