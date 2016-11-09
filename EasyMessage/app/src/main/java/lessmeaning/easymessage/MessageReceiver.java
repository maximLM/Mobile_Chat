package lessmeaning.easymessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class MessageReceiver extends BroadcastReceiver {

    public static final String MESSAGE = "MEESSSSSS";

    @Override
    public void onReceive(Context context, Intent intent) {
        BeautifulNotification.showMessageNotification(context, intent.getStringExtra(MESSAGE), "friend");
    }
}

