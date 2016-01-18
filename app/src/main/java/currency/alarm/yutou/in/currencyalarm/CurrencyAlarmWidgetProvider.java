package currency.alarm.yutou.in.currencyalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Debug;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.RemoteViews;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cz.msebera.android.httpclient.Header;

/**
 * Created by xiuxiu on 13/01/2016.
 */
public class CurrencyAlarmWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        final Context ctxt = context;

        Intent serviceIntent = new Intent(ctxt, CurrentAlarmService.class);
        ctxt.startService(serviceIntent);

        for (int i = 0; i < appWidgetIds.length; i++) {
            final int widgetId = appWidgetIds[i];

            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get("http://fx.cmbchina.com/hq/", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String html = new String(responseBody);
                    Document doc = Jsoup.parse(html);

                    Elements currencies = doc.select("table tr");
                    for (Element currency : currencies) {
                        Elements tds = currency.select("td");
                        if (tds != null && tds.size() > 0) {
                            if (currency.select("td").get(0).text().equals("澳大利亚元")) {
                                String selling = currency.select("td").get(4).text();
                                String buying = currency.select("td").get(6).text();
                                String time = currency.select("td").get(8).text();

                                RemoteViews remoteViews = new RemoteViews(ctxt.getPackageName(), R.layout.currency_alarm_widget);
                                remoteViews.setTextViewText(R.id.button_update, "S:" + selling + ", B:" + buying + " (" + time + ")");

                                Intent intent = new Intent(ctxt, CurrencyAlarmWidgetProvider.class);
                                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                intent.putExtra(appWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(ctxt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                remoteViews.setOnClickPendingIntent(R.id.button_update, pendingIntent);
                                appWidgetManager.updateAppWidget(widgetId, remoteViews);


                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });



        }
    }
}
