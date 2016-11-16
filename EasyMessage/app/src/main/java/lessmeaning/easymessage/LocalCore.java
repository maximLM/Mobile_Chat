package lessmeaning.easymessage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Максим on 03.11.2016.
 */
public class LocalCore {

    private LocalDataBase db;
    private BroadcastReceiver brv;
    private Activity activity;
    private Class clazz;
    private final int convID;
    public static final String BROADCAST = "LESSMEANING.CHATMOBILE.HEY";
    public static final String IS_CONVERSATION = "ITISCONVERSATION";

    public LocalCore(Activity activity, int convID) {
        this.activity = activity;
        this.convID = convID;
        clazz = activity.getClass();
        db = new LocalDataBase(activity);
        brv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConv = intent.getBooleanExtra(IS_CONVERSATION, false);
                if (isConv && clazz == ConversationActivity.class){
                    sendConversations();
                } else if (!isConv && clazz == MessagesActivity.class) {
                    sendApproved();
                }
            }
        };
        if (clazz == MessagesActivity.class)
            sendApproved();
        else if (clazz == ConversationActivity.class)
            sendConversations();
        connectToService();
    }

    public LocalCore(Activity activity) {
        this(activity, 0);
    }

    public void addTemp(String inf) {
        db.addTemp(new TempRow(convID, inf, -124));
    }

    public void sendConversations() {
        ArrayList<Conversation> convs = db.getConversation();
        Collections.sort(convs);
        ((ConversationActivity)activity).reloadList(convs);
    }

    public void sendApproved() {
        if (clazz != MessagesActivity.class) return;
        ArrayList<Row> rows = db.getApproved(convID);
        Collections.sort(rows);

        ((MessagesActivity)activity).reloadList(rows);
    }

    private boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void connectToService() {
        try {
            if (brv != null) activity.unregisterReceiver(brv);
        } catch (IllegalArgumentException e) { }
        if (clazz == MessagesActivity.class || clazz == ConversationActivity.class)
            activity.registerReceiver(brv, new IntentFilter(BROADCAST));
        if (isServiceRunning(Merger.class, activity)) {
            Log.d("supertesting", "connectToService: already running");
            return;
        } else {
            Log.d("supertesting", "connectToService: starrt");
            Intent intent = new Intent(activity, Merger.class);
            activity.startService(intent);
        }
    }


    public void disconnectService() {
        try {
            if (brv != null) activity.unregisterReceiver(brv);
        } catch (IllegalArgumentException e) { }
    }

    public void logOut() {
        db.deleteEverything();
        activity.startActivity(new Intent(activity, SignInActivity.class));
    }

    public void signin(final String username,final String password) {
        if (clazz != SignInActivity.class) return;
        if (db.getUsername() != null)
            ((SignInActivity)activity).fail("You are already logged");
        if (!ServerConnection.checkConnection(activity))
            ((SignInActivity)activity).fail("No Connection");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String utf = "UTF-8";
                String lnk = null;
                try {
                    lnk = "http://e-chat.h1n.ru/signin.php?username="
                            + URLEncoder.encode(username, utf)
                            + "&password="
                            + URLEncoder.encode(password, utf);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
                String success = ServerConnection.executeQuery(lnk);
                String fail = "Password incorrect or user does not exists";
                if (success.contains("success")) {
                    signedIn(username, fail);
                } else {
                    signedIn(null, fail);
                }
            }
        }).start();
    }

    public void createConversation(final String username) {
        if (clazz != ConversationActivity.class) return;
        if (db.getUsername() != null)
            ((ConversationActivity)activity).fail("You are already logged");
        if (!ServerConnection.checkConnection(activity))
            ((ConversationActivity)activity).fail("No Connection");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String utf = "UTF-8";
                String lnk = null;
                try {
                    lnk = "http://e-chat.h1n.ru/createconversation.php?user1="
                            + URLEncoder.encode(db.getUsername(), utf)
                            + "&user2="
                            + URLEncoder.encode(username, utf);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
                String success = ServerConnection.executeQuery(lnk);
                String fail = "This user doesnot exist";
                if (!success.contains("fail")) {
                    signedIn(username, fail);
                } else {
                    signedIn(null, fail);
                }
            }
        }).start();
    }

    private void signedIn(String username, final String fail) {
        if (username == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SignInActivity) activity).fail(fail);
                }
            });
        }
        ArrayList<Conversation> convs = new ArrayList<>();
        convs.add(new Conversation(-12, username, 0));
        convs.addAll(ServerConnection.getConversations(username, 0));
        try {
            db.setConversations(convs);
            db.setApproved(ServerConnection.getMessages(username, 0));
        } catch (UnsupportedOperationException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SignInActivity) activity).fail("set gave exception");
                }
            });
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, ConversationActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    public void signup(final String username, final String password) {
        if (clazz != SignInActivity.class) return;
        if (db.getUsername() != null)
            ((SignInActivity)activity).fail("You are already logged");
        if (!ServerConnection.checkConnection(activity))
            ((SignInActivity)activity).fail("No Connection");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String utf = "UTF-8";
                String lnk = null;
                try {
                    lnk = "http://e-chat.h1n.ru/signup.php?username="
                            + URLEncoder.encode(username, utf)
                            + "&password="
                            + URLEncoder.encode(password, utf);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
                String success = ServerConnection.executeQuery(lnk);
                String fail = "User already exists";
                if (success.contains("success")) {
                    signedIn(username, fail);
                } else {
                    signedIn(null, fail);
                }
            }
        }).start();
    }

}
