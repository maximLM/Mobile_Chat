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
        makeNotification(context, intent.getStringExtra(MESSAGE));
    }

    private void makeNotification(Context context, String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Message from friend")
                        .setContentText(msg);

        Intent startMyActivity = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(startMyActivity);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager m = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(11,mBuilder.build());
    }
}

