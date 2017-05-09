package anson.std.medical.dealer.support;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.MainActivity;

/**
 * Created by anson on 17-5-9.
 */

public class NotificationUtil {

    public static Notification generateNotification(Context context, String title) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        return new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(context.getText(R.string.medical_service_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();
    }

    public static void updateNotification(Context context, int notificationId, String title) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        System.out.println(title);
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        notificationManager.notify(notificationId, generateNotification(context, title));
    }
}
