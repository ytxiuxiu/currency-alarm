package currency.alarm.yutou.in.currencyalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by xiuxiu on 16/01/2016.
 */
public class CurrencyAlarmNotification {

    private Context context;

    public CurrencyAlarmNotification(Context context) {
        this.context = context;
    }

    public void notify(Double selling, Double buying, String time, Double lastSelling) {
        Intent notificationIntent = new Intent();
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("AUD -> CNY")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("S:" + selling + ", B:" + buying + " " +
                        (selling > lastSelling ? "⬆️" : selling < lastSelling ? "⬇" : "→")
                        + " (" + time + ")")
                .setContentIntent(notificationPendingIntent)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        if (Math.abs(selling - lastSelling) >= 1) {
            notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.coins);
            notification.priority = Notification.PRIORITY_HIGH;
        }
        notificationManager.notify(0, notification);
    }

}
