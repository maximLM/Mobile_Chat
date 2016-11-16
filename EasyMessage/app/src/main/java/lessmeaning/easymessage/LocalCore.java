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
import java.util.Date;

/**
 * Created by Максим on 03.11.2016.
 */
public class LocalCore {

    private LocalDataBase db;
    private BroadcastReceiver brv;
    private Activity activity;
    private Class clazz;
    public static final String TAG = "supertesting";
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
        connectToService();
    }

    public LocalCore(Activity activity) {
        this(activity, 0);
    }

    public void addTemp(String inf) {
        Log.d(TAG, "addTemp: inf is " + inf);
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

    public void signin(final String username,final String password) {
        if (clazz != SignInActivity.class) return;
        signedIn(username, "fail");
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
        String names[] = new String[] {"Shmidt", "Max", "Timur", "Vlad", "Sonya"};
        for (int i = 0; i < names.length; i++) {
            convs.add(new Conversation(i, names[i], (long) (i * i * Math.PI)));
        }
        ArrayList<Row> rows = new ArrayList<>();
        final int SIZE = 34;
        for(Conversation conv : convs) {
            for (int i = 0; i < SIZE; i++) {
                rows.add(new Row(conv.getConversationID(),
                        i % 2 == 0 ? username : conv.getFriend(),
                        "HEy i is " + i, new Date(System.currentTimeMillis() * i / SIZE).getTime()));
            }
        }
        try {
            db.setConversations(convs);
            db.setApproved(rows);
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
        signedIn(username, "Sign UP failed");
    }

    public void createConversation(String username) {
        ArrayList<Conversation> res = new ArrayList<Conversation>();
        res.add(new Conversation(1, username, new Date().getTime()));
        db.addConversations(res);
        if (clazz == ConversationActivity.class) sendConversations();
    }
}
