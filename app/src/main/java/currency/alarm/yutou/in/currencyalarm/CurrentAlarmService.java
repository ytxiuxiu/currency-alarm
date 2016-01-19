package currency.alarm.yutou.in.currencyalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cz.msebera.android.httpclient.Header;


/**
 * Created by xiuxiu on 15/01/2016.
 */
public class CurrentAlarmService extends Service {

    private Double lastSelling = 8000d;

    private CurrencyAlarmNotification notification = new CurrencyAlarmNotification(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

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
                                    Double selling = Double.parseDouble(currency.select("td").get(4).text());
                                    Double buying = Double.parseDouble(currency.select("td").get(6).text());
                                    String time = currency.select("td").get(8).text();

                                    notification.notify(selling, buying, time, lastSelling);

                                    lastSelling = selling;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                handler.postDelayed(this, 1000 * 60 * 1);
            }
        };
        handler.postDelayed(runnable, 1000 * 1);
    }

}
