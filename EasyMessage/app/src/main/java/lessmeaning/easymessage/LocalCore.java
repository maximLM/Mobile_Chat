package lessmeaning.easymessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Максим on 03.11.2016.
 */
public class LocalCore {

    private LocalDataBase db;
    private BroadcastReceiver brv;
    private MainActivity activity;
    public static final String BROADCAST = "LESSMEANING.CHATMOBILE.HEY";

    public LocalCore(MainActivity activity) {
        this.activity = activity;
        db = new LocalDataBase(activity);
        connectToService();
        brv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendApproved();
            }
        };
    }

    public void addTemp(String inf) {
        db.addTemp(inf);
    }

    public void sendApproved() {
        ArrayList<Row> rows = db.getApproved();
        Collections.sort(rows);
        int len = rows.size();
        String values[] = new String[len];
        for (int i = 0; i < len; i++) {
            values[i] = rows.get(i).content;
        }
        activity.reloadList(values);
    }

    private void connectToService() {
        Intent intent = new Intent(activity, Merger.class);
        activity.startService(intent);
        activity.registerReceiver(brv, new IntentFilter(BROADCAST));
    }

    public void disconnectService() {
        activity.unregisterReceiver(brv);
    }
}
