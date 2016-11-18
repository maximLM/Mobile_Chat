package lessmeaning.easymessage;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by пользователь on 08.11.2016.
 */

public class BeautifulNotification  {

    public static void showMessageNotification (Context context, String msg, String sender, int conversationID) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(ConversationActivity.CONVERSATION_ID, conversationID);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(sender)
                .setContentText(msg)
                .setTicker(sender + ": " + msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, notification);


    }

    public static void showConversationNotification(Context context, String friendname) {
        friendname = "Created new conversation with " + friendname;
        Intent intent = new Intent(context, MessagesActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(friendname)
                .setContentText("")
                .setTicker(friendname)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, notification);
    }
}
