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
    public static final String CONVERSATION_ID = "CONVERSATION_ID";
    public static final String SENDER_NAME = "SEINDER_NAME";
    public static final String IS_CONVERSATION = "ISITREALLYCONVERSATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(IS_CONVERSATION, false)) {
            BeautifulNotification.showConversationNotification(context,
                    intent.getStringExtra(SENDER_NAME));
        } else {
            BeautifulNotification.showMessageNotification(context,
                    intent.getStringExtra(MESSAGE),
                    intent.getStringExtra(SENDER_NAME),
                    intent.getIntExtra(CONVERSATION_ID, 0));
        }
    }
}

