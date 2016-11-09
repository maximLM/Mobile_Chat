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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class Merger extends Service implements Runnable {

    private static final String TAG = "supertesting";
    private volatile boolean running;
    private LocalDataBase localdb;
    private final String SERVER_NAME = "http://e-chat.h1n.ru";
    private final String API_NAME = "chat.php";

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
        Log.d(TAG, "run: inLoooop");
        while (true) { // TODO replace with running
            checkLocal();
            String msg = checkServer();
//            Log.d(TAG, "run: msg = " + msg);
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
            conn.setReadTimeout(15000);
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
            rawInput = buffer.toString();

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

//        Log.d(TAG, "checkServer: rawinput = " + rawInput);
        if (rawInput == null || rawInput.equals("")) return null;

        try {
            ArrayList<Row> rows = convertToNormal(rawInput);

            if (rows == null || rows.size() == 0)
                return null;
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
    private String generateLink(String msg) throws UnsupportedEncodingException {
        String utf = "UTF-8";
        return SERVER_NAME + "/chat.php?action=send&message=" + URLEncoder.encode(msg.trim(), "UTF-8");
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
            Log.d(TAG, "convertToNormal: row is " + res.get(res.size() - 1).getContent());
        }
        Collections.sort(res);
        return res;
    }


    private boolean sendToServer(String msg) {
        String lnk = null;
        try {
            lnk = generateLink(msg);
//            lnk = URLEncoder.encode(lnk, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                lnk = generateLink("unsupportedencodingwashere");
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException("UNREAL SITUATION");
            }
        }
        Log.d(TAG, "sendToServer: " + lnk);
        HttpURLConnection conn = null;
        BufferedReader in = null;
        boolean success = false;
        try {
            conn = (HttpURLConnection) new URL(lnk).openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
//        Log.d(TAG, "sendToServer: success = " + success);
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
