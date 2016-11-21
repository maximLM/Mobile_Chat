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
import java.util.Comparator;

/**
 * Created by Максим on 03.11.2016.
 */
public class LocalCore {

//    left right



    private static final String TAG = "newITIS";
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
//        Log.d(TAG, "LocalCore: username is " + db.getUsername());
        brv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConv = intent.getBooleanExtra(IS_CONVERSATION, false);
                if (isConv && clazz == ConversationActivity.class) {
                    sendConversations();
                } else if (!isConv ) {
                    if (clazz == MessagesActivity.class) {
                        sendApproved();
                    } else if (clazz == ConversationActivity.class) {
                        sendConversations();
                    }
                }
            }
        };
        if (clazz == MessagesActivity.class) {
//            String user;  TODO stoppede here
            String user = db.getUsername();
            Log.d(TAG, "user is  " + user);
            ((MessagesActivity) activity).setUsername(user);
            sendApproved();
        } else if (clazz == ConversationActivity.class) {
            sendConversations();
        }
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
        if (convs == null) return;
        if (convs.size() > 0)
            convs.remove(0);
        Collections.sort(convs, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation conve, Conversation conve2) {
                Row r1 = conve.getRow();
                Row r2 = conve2.getRow();
                Log.d(TAG, "in compare : ");
                Log.d(TAG, "compare: " + conve.getTime());
                Log.d(TAG, "compare: " + conve2.getTime());
                if (r1 == null && r2 == null)
                    return (int) -(conve.getTime() - conve2.getTime());
                else if (r1 == null)
                    return (int) -(conve.getTime() - r2.getTime());
                else if (r2 == null)
                    return (int) -(r1.getTime() - conve2.getTime());
                else 
                    return -r1.compareTo(r2);
                
            }
        });
        for(Conversation c : convs) {
            Log.d(TAG, c.getFriend() + "  " + c.getRow().getTime());
        }
        ((ConversationActivity)activity).reloadList(convs);
    }

    public void sendApproved() {
        if (clazz != MessagesActivity.class) return;
        ArrayList<Row> rows = db.getApproved(convID);
        Collections.sort(rows);

        ((MessagesActivity) activity).reloadList(rows);
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
        } catch (IllegalArgumentException e) {
        }
        if (clazz == MessagesActivity.class || clazz == ConversationActivity.class)
            activity.registerReceiver(brv, new IntentFilter(BROADCAST));
        if (!isServiceRunning(Merger.class, activity) || clazz == ConversationActivity.class) {
            Log.d("supertesting", "connectToService: starrt");
            Intent intent = new Intent(activity, Merger.class);
            activity.startService(intent);
        } else {
            Log.d(TAG, "not needed to update");
        }
    }


    public void disconnectService() {
        try {
            if (brv != null) activity.unregisterReceiver(brv);
        } catch (IllegalArgumentException e) {
        }
    }

    public void logOut() {
        db.deleteEverything();
        activity.startActivity(new Intent(activity, SignInActivity.class));
    }

    public void signin(String input, final String password) {
        if (clazz != SignInActivity.class) return;
        if (db.getUsername() != null) {
            ((SignInActivity) activity).fail("You are already logged");
            return;
        }
        if (!ServerConnection.checkConnection(activity)) {
            ((SignInActivity) activity).fail("No Connection");
            return;
        }
        final String username = toUsername(input);
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
                if (success != null && success.contains("success")) {
                    signedIn(username, fail);
                } else {
                    if (success == null) {
                        signedIn(null, "Trouble with connection");
                    } else {
                        signedIn(null, fail);
                    }
                }
            }
        }).start();
    }

    private String toUsername(String input) {
        return input.toLowerCase().replaceAll(" ", "");
    }

    public void createConversation(String input) {
        if (clazz != ConversationActivity.class) return;
        if (db.getUsername() == null) {
            ((ConversationActivity) activity).fail("You are not logged");
            return;
        }
        if (!ServerConnection.checkConnection(activity)) {
            ((ConversationActivity) activity).fail("No Connection");
            return;
        }
        final String username = toUsername(input);
        if (db.haveConversation(username)) {
            ((ConversationActivity) activity)
                    .fail("You already created conversation with this friend");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MIMI", "create dialog on server");
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
                final String fail = ServerConnection.executeQuery(lnk);
                if (fail == null) {
                    ((ConversationActivity) activity).fail("trouble with connection");
                } else if (fail.contains("fail")) {
                    ((ConversationActivity) activity).fail("user does not exists");
                }
            }
        }).start();
    }

    private void signedIn(String username, final String fail) {
        if (clazz != SignInActivity.class) {
//            Log.d(TAG, "signedIn: error activity is not signin");
            return;
        }
        if (username == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SignInActivity) activity).fail(fail);
                }
            });
            return;
        }
        ArrayList<Conversation> convs = new ArrayList<>();
        convs.add(new Conversation(-12, username, 0));
        ArrayList<Conversation> fromServer = ServerConnection.getConversations(username, 0);
        if (fromServer != null)
            convs.addAll(fromServer);
        try {
            db.setConversations(convs);
            ArrayList<Row> rowsFromServer = ServerConnection.getMessages(username, 0);
            if (rowsFromServer != null)
                db.setApproved(rowsFromServer);
        } catch (UnsupportedOperationException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SignInActivity) activity).fail("you are already logged");
                }
            });
//            Log.d(TAG, "signedIn: set gave exeption");
            return;
        }
//        Log.d(TAG, "signedIn: db.getUserName() = " + db.getUsername());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, ConversationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public void signup(String input, final String password) {
        if (clazz != SignInActivity.class) return;
        if (db.getUsername() != null) {
            ((SignInActivity) activity).fail("You are already logged");
            return;
        }
        if (!ServerConnection.checkConnection(activity)) {
            ((SignInActivity) activity).fail("No Connection");
            return;
        }
        final String username = toUsername(input);
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
                if (success != null && success.contains("success")) {
                    signedIn(username, fail);
                } else {
                    signedIn(null, fail);
                }
            }
        }).start();
    }

    public boolean checkAuthorization() {
        String username = db.getUsername();
        return username != null && !username.equals("");
    }
}
