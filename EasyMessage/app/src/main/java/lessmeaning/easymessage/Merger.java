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

    private volatile boolean running;
    private volatile LocalDataBase localdb;
    private final String SERVER_NAME = "http://e-chat.h1n.ru";

    @Override
    public void onCreate() {
        super.onCreate();
        localdb = new LocalDataBase(this);
        new Thread(this).start();
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                checkLocal();
                checkServer();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkServer() {
        String username = localdb.getUsername();
        if (username == null || username.equals("")) return;
        ArrayList<Conversation> convs = ServerConnection.getConversations(localdb.getUsername(),
                localdb.getLastTimeConv());
        if (convs != null && convs.size() > 0) {
            localdb.addConversations(convs);
            Conversation co = convs.get(convs.size() - 1);
            sendMsgToUpd("",co.getFriend(), (int) co.getConversationID(), true);
        }
        ArrayList<Row> rows = ServerConnection.getMessages(localdb.getUsername(),
                localdb.getLastTimeMess());
        if (rows != null && rows.size() > 0) {
            localdb.addApproved(rows);
            Row row = rows.get(rows.size() - 1).clone();
            row.decrypt();
            sendMsgToUpd(row.getContent(), row.getUserSender(), (int) row.getConversationID(), false);
        }
    }

    private String generateLink(long convID, String msg) throws UnsupportedEncodingException {
        String utf = "UTF-8";
        return SERVER_NAME + "/send.php?username=" +
                URLEncoder.encode(localdb.getUsername(), utf) + "&conversationID=" +
                URLEncoder.encode(String.valueOf(convID), utf)
                + "&content=" + URLEncoder.encode(msg.trim(), utf);
    }

    private boolean sendToServer(long convID, String msg) {
        String lnk = null;
        try {
            lnk = generateLink(convID, msg);
        } catch (UnsupportedEncodingException e) {
            try {
                lnk = generateLink(-999, "UNSUPPORTED_ENCODING_PAY_ATTENTION_USER_IS_"
                        + localdb.getUsername());
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException("UNREAL SITUATION");
            }
        }
        boolean success = false;
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
//            Log.d(TAG, "sendToServer: malformed");
        } catch (IOException e) {
            e.printStackTrace();
//            Log.d(TAG, "sendToServer: ioex");
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

    private void sendMsgToUpd(String msg, String sender, int conversationId, boolean isConv) {
        String user = localdb.getUsername();
        if (user == null) return;
        Intent intent = new Intent(LocalCore.BROADCAST);
        intent.putExtra(LocalCore.IS_CONVERSATION, isConv);
        sendBroadcast(intent);
        if (sender != null && sender.equals(user)) return;
        intent = new Intent(this, MessageReceiver.class);
        intent.putExtra(MessageReceiver.IS_CONVERSATION, isConv);
        intent.putExtra(MessageReceiver.MESSAGE, msg);
        intent.putExtra(MessageReceiver.CONVERSATION_ID, conversationId);
        intent.putExtra(MessageReceiver.SENDER_NAME, sender);
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