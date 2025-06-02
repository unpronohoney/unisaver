package com.unisaver.unisaver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BildirimWorker extends Worker {

    private static final String CHANNEL_ID = "app_notification_channel";
    private final int[] months = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.MAY, Calendar.AUGUST, Calendar.NOVEMBER};
    private final int[] days = {4, 10, 20, 15, 20, 20};
    private final String[] pool1 = {"Finalleri sona güne bıraktıysan, içini rahatlatmak için buradayım. \uD83D\uDE0A",
            "Bazı dersler de büte bırakılmalı dimi?¿ \uD83E\uDD20", "Kafadan planlamak zor gibi \uD83D\uDE44. Yine de ben buradayım!"};
    private final String[] pool2 = {"Ders seçimine de yardımcı olabileceğimi unutma. \uD83D\uDE0A", "Güz'ü Bahar'da toplarızzzzz. \uD83D\uDE09",
            "Bir sene değil de yarım dönem uzatmana yardımcı olmak için buradayım. \uD83D\uDE12"};
    private final String[] pool3 = {"Çok ders var hangisine odaklanacaksın? Gel beraber öğrenelim.",
            "Bazen vizeler de sallanabilir. \uD83E\uDD20", "Vizelerden planlarsan final haftasında yatarsın. \uD83D\uDE09"};
    private final String[] pool4 = {"Final haftasında herkes telaş yaparken sen UniSaver kullan!",
            "Finaller bitsin süper bi tatil yaparız. Ama önce planlayalım. \uD83D\uDE0A", "ÇOK MU ZOR YA! UniSaver'ı aç içini rahatlat."};
    private final String[] pool5 = {"Yeni seneyi UniSaver'da düşün, kafan rahat olsun.", "Tatil modundan çıkma vakti birazcık. \uD83E\uDD0F",
            "Ders seçiminde de UniSaver!!!"};
    private final String[] pool6 = {"Hangi dersi bırakacaksın, gel birlikte seçelim. \uD83D\uDC7F", "Ne ara vizeler geldi ya, canın sıkıldıysa ben buradayım. ✋",
            "Diferansiyel Denklemler'de Boğaziçili, vizelerde hangisine odaklanacağında UniSaver."};
    private final String[] titles = {
            "BÜT MÜ¿ FİNAL Mİ? \uD83E\uDD28\uD83E\uDD28\uD83E\uDD28",
            "Bahar'a Birlikte Ders Seçelim \uD83D\uDE43",
            "Fiyuu. Vizeler geliyor!",
            "FİNALSSSS",
            "Yeni Seneye Hazır Mıyızzz??",
            "Vizeler Yakın..."
    };

    public BildirimWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int workerId = getInputData().getInt("workerId", -1);
        switch (workerId) {
            case 1:
                runWorkerJan();
                break;
            case 2:
                runWorkerFeb();
                break;
            case 3:
                runWorkerMar();
                break;
            case 4:
                runWorkerMay();
                break;
            case 5:
                runWorkerAug();
                break;
            case 6:
                runWorkerNov();
                break;
            default:
                return Result.failure();
        }

        return Result.success();
    }

    private void runWorkerJan() {
        sendNotification(titles[0], getRandomMessageForDate(pool1), 1);
        restartWorker(months[0], days[0], 1);
    }

    private void runWorkerFeb() {
        sendNotification(titles[1], getRandomMessageForDate(pool2), 2);
        restartWorker(months[1], days[1], 2);
    }
    private void runWorkerMar() {
        sendNotification(titles[2], getRandomMessageForDate(pool3), 3);
        restartWorker(months[2], days[2], 3);
    }
    private void runWorkerMay() {
        sendNotification(titles[3], getRandomMessageForDate(pool4), 4);
        restartWorker(months[3], days[3], 4);
    }
    private void runWorkerAug() {
        sendNotification(titles[4], getRandomMessageForDate(pool5), 5);
        restartWorker(months[4], days[4], 5);
    }
    private void runWorkerNov() {
        sendNotification(titles[5], getRandomMessageForDate(pool6), 6);
        restartWorker(months[5], days[5], 6);
    }

    private void restartWorker(int month, int day, int workerId) {
        // Worker'ın başlatılacağı tarihi ayarlıyoruz
        Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.YEAR, 1);

        // Zamanı gecikme olarak hesapla
        long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();

        // WorkData ile worker'a veri gönderme (workerId)
        Data data = new Data.Builder()
                .putInt("workerId", workerId)
                .build();

        // Worker'ı başlatma
        OneTimeWorkRequest workerRequest = new OneTimeWorkRequest.Builder(BildirimWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        // WorkManager ile iş kuyruğa alınır
        WorkManager.getInstance(getApplicationContext()).enqueue(workerRequest);

        Log.d("MainActivity", "Worker " + workerId + " scheduled for " + calendar.getTime());
    }

    // Bildirim Gönderme
    private void sendNotification(String title, String message, int id) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "App Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getAppContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_small_noti)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(id, notification);
    }

    public String getRandomMessageForDate(String[] pool) {

        // 📌 Olasılık Dağılımı (%70 Normal, %20 Özel, %10 EasterEgg)
        double[] probabilities = {0.7, 0.2, 0.1};

        return getRandomElementWithProbability(pool, probabilities);
    }

    // 📌 BELİRLİ OLASILIKLARLA MESAJ SEÇME
    private static String getRandomElementWithProbability(String[] array, double[] probabilities) {
        Random random = new Random();
        double randomValue = random.nextDouble(); // 0.0 - 1.0 arası rastgele sayı

        System.out.println(randomValue);

        double cumulativeProbability = 0.0;
        for (int i = 0; i < array.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return array[i];
            }
        }
        return array[array.length - 1]; // Eğer hata olursa sonuncuyu seç
    }

}
