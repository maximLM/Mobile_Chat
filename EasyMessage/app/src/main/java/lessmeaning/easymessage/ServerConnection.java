package lessmeaning.easymessage;

import android.content.Context;
import android.net.ConnectivityManager;
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
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by Максим on 13.11.2016.
 */
public class ServerConnection {

    public static String executeQuery(String lnk) {
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


    public static ArrayList<Row> getMessages(String username, long time) {
        String lnk = null;
        try {
            String utf = "UTF-8";
            lnk = "http://e-chat.h1n.ru/getmessages.php?username=" +
                    URLEncoder.encode(username, utf)
                    +"&time=" + URLEncoder.encode(String.valueOf(time), utf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String rawInput = executeQuery(lnk);
        if (rawInput == null || rawInput.equals("")) return null;
        try {
            ArrayList<Row> rows = convertMessage(rawInput);
            if (rows == null || rows.size() == 0)
                return null;
            return rows;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<Conversation> getConversations(String username, long time) {
        String lnk = null;
        try {
            String utf = "UTF-8";
            lnk = "http://e-chat.h1n.ru/getconversations.php?username=" +
                    URLEncoder.encode(username, utf)
                    + "&time=" + URLEncoder.encode(String.valueOf(time), utf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String rawInput = executeQuery(lnk);
        if (rawInput == null) return null;
        try {
            ArrayList<Conversation> convs = convertConversation(rawInput, username);
            if (convs == null || convs.size() == 0) return null;
            return convs;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    private static ArrayList<Row> convertMessage(String rawInput) throws JSONException {
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
        }
        Collections.sort(res);
        return res;
    }

    private static ArrayList<Conversation> convertConversation(String rawInput, String username) throws JSONException {
        ArrayList<Conversation> res = new ArrayList<>();
        JSONArray array = new JSONArray(rawInput);
        JSONObject row;
        HashSet<Long> ids = new HashSet<>();
        int len = array.length();
        for (int i = 0; i < len; i++) {
            row = array.getJSONObject(i);
            long id = row.getLong(Fields.CONVERSATION + "");
            String friend = row.getString(Fields.AUTHOR + "");
            if (ids.contains(id) || friend.equals(username)) continue;
            ids.add(id);
            res.add(new Conversation(id,
                    friend,
                    row.getLong(Fields.TIME + "")));
        }
        Collections.sort(res, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation conversation, Conversation t1) {
                return (int) (conversation.getTime() - t1.getTime());
            }
        });
        return res;
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
