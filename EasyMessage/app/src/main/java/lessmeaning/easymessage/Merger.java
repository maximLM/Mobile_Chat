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

    @Override
    public void onCreate() {
        super.onCreate();
        localdb = new LocalDataBase(this);
        new Thread(this).start();
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    checkLocal();
                }
            }
        });
    }

    @Override
    public void run() {
        while (running) {
            String msg = checkServer();
            if (msg != null) {
                sendMsgToUpd(msg);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String checkServer() {
        String res = getConversations();
        if (res != null) return res;
        res = getMessages();
        return res;
    }

    private String getRaw(String lnk) {
        BufferedReader in = null;
        HttpURLConnection conn = null;
        String rawInput = null;
        try {
            conn = (HttpURLConnection) new URL(lnk).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setDoOutput(true);

            int respondseCode = conn.getResponseCode();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            rawInput = buffer.toString();
            Log.d(TAG, "checkServer: responseCode = " + respondseCode);
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
        return rawInput;
    }

    private String getConversations() {
        String lnk = null;
        long time = localdb.getLastTimeConv();
        try {
            lnk = "http://e-chat.h1n.ru/getconversations.php?user=" +
                    URLEncoder.encode(localdb.getUserName(), "UTF-8")
                    + "&time=" + URLEncoder.encode(String.valueOf(time), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String rawInput = getRaw(lnk);
        if (rawInput == null) return null;
        try {
            ArrayList<Conversation> convs = convertConversation(rawInput);
            if (convs == null || convs.size() == 0) return null;
            localdb.addConversations(convs);
            return "New conversation with " + convs.get(0).getFriend();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Conversation> convertConversation(String rawInput) throws JSONException {
        ArrayList<Conversation> res = new ArrayList<>();
        JSONArray array = new JSONArray(rawInput);
        JSONObject row;
        int len = array.length();
        for (int i = 0; i < len; i++) {
            row = array.getJSONObject(i);
            res.add(new Conversation(row.getLong(Fields.CONVERSATION + ""),
                    row.getString(Fields.AUTHOR + ""),
                    row.getLong(Fields.TIME + "")));
            Log.d(TAG, "convertConversation: conv is " + res.get(res.size() - 1).getFriend());
        }
        Collections.sort(res);
        return res;
    }

    private String getMessages() {
        long time = localdb.getLastTimeMess();
        String lnk = null;
        try {
            lnk = "http://e-chat.h1n.ru/getmessages.php?user=" +
                    URLEncoder.encode(localdb.getUserName(), "UTF-8")
            +"&time=" + URLEncoder.encode(String.valueOf(time), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String rawInput = getRaw(lnk);
        if (rawInput == null || rawInput.equals("")) return null;
        try {
            ArrayList<Row> rows = convertMessage(rawInput);
            if (rows == null || rows.size() == 0)
                return null;
            localdb.addApproved(rows);
            return rows.get(0).getContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateLink(long convID, String msg) throws UnsupportedEncodingException {
        return SERVER_NAME + "/send.php?user=" +
                URLEncoder.encode(localdb.getUserName(), "UTF-8") + "&conversationID=" +
                URLEncoder.encode(String.valueOf(convID))
                + "&message=" + URLEncoder.encode(msg.trim(), "UTF-8");
    }

    private ArrayList<Row> convertMessage(String rawInput) throws JSONException {
        ArrayList<Row> res = new ArrayList<>();
        JSONArray array = new JSONArray(rawInput);
        JSONObject row;
        int len = array.length();
        for (int i = 0; i < len; i++) {
            row = array.getJSONObject(i);
            res.add(new Row(row.getLong(Fields.CONVERSATION + ""),
                    row.getString(Fields.AUTHOR + ""),
                    row.getString(Fields.MESSAGE + ""),
                    row.getLong(Fields.TIME + "")));
            Log.d(TAG, "convertMessage: row is " + res.get(res.size() - 1).getContent());
        }
        Collections.sort(res);
        return res;
    }


    private boolean sendToServer(long convID, String msg) {
        String lnk = null;
        try {
            lnk = generateLink(convID, msg);
        } catch (UnsupportedEncodingException e) {
            try {
                lnk = generateLink(localdb.getUserName(), "unsupportedencodingwashere");
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException("UNREAL SITUATION");
            }
        }
        boolean success = false;
        Log.d(TAG, "sendToServer: " + lnk);
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            conn = (HttpURLConnection) new URL(lnk).openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            success = HttpURLConnection.HTTP_OK == conn.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "sendToServer: malformed");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "sendToServer: ioex");
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
        Log.d(TAG, "sendToServer: success = " + success);
        return success;
    }



    private void checkLocal() {
        ArrayList<TempRow> freshRows = localdb.getTemp();
        int len = freshRows.size();
        if (len == 0) return;
        boolean success = true;
        for (int i = 0; i < len && success; i++) {
            success = sendToServer(freshRows.get(i).getConversationID(),
                    freshRows.get(i).getContent());
            if (success) {
                localdb.deleteTemp(freshRows.get(i).getID());
            }
        }
    }

    private void sendMsgToUpd(String msg) {
        sendBroadcast(new Intent(LocalCore.BROADCAST));
        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra(MessageReceiver.MESSAGE, msg);
        sendBroadcast(intent);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            running = false;
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
    }

}
