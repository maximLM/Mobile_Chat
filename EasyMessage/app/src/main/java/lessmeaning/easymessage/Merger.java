package lessmeaning.easymessage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
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
            checkServer();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkServer() {
        ArrayList<Conversation> convs = ServerConnection.getConversations(localdb.getUsername(), localdb.getLastTimeConv());
        if (convs != null && convs.size() > 0) {
            localdb.addConversations(convs);
            sendMsgToUpd("New Conversation with " + convs.get(convs.size() - 1).getFriend(), true);
        }
        ArrayList<Row> rows = ServerConnection.getMessages(localdb.getUsername(), localdb.getLastTimeMess());
        if (rows != null && rows.size() > 0) {
            localdb.addApproved(rows);
            sendMsgToUpd(rows.get(rows.size() - 1).getContent(), false);
        }
    }

    private String generateLink(long convID, String msg) throws UnsupportedEncodingException {
        return SERVER_NAME + "/send.php?user=" +
                URLEncoder.encode(localdb.getUsername(), "UTF-8") + "&conversationID=" +
                URLEncoder.encode(String.valueOf(convID))
                + "&message=" + URLEncoder.encode(msg.trim(), "UTF-8");
    }

    private boolean sendToServer(long convID, String msg) {
        String lnk = null;
        try {
            lnk = generateLink(convID, msg);
        } catch (UnsupportedEncodingException e) {
            try {
                lnk = generateLink(-999, "UNSUPPORTED_ENCODING_PAY_ATTENTION_USER_IS"
                        + localdb.getUsername());
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
                localdb.deleteTemp((int) freshRows.get(i).getId());
            }
        }
    }

    private void sendMsgToUpd(String msg, boolean isConv) {
        Intent intent = new Intent(LocalCore.BROADCAST);
        intent.putExtra(LocalCore.IS_CONVERSATION, isConv);
        sendBroadcast(intent);
        intent = new Intent(this, MessageReceiver.class);
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
