package anson.std.medical.dealer.support;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import anson.std.medical.dealer.MedicalApplication;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.MainActivity;

/**
 * Created by anson on 17-5-9.
 */

public class NotificationUtil {

    private static Context context = MedicalApplication.getMedicalApplicationContext();

    public static Notification generateNotification(String title) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        return new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(context.getText(R.string.medical_service_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();
    }

    public static void updateNotification(int notificationId, String title) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, generateNotification(title));
    }
}
