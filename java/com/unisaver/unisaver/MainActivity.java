package com.unisaver.unisaver;

import android.app.NotificationChannel;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";
    private final int[] months = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.MAY, Calendar.AUGUST, Calendar.NOVEMBER};
    private final int[] days = {4, 10, 20, 15, 20, 20};
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 101;
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_STATUS = "status";
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_COMPLETE = 2;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Context appContext;
    private static ThisTerm buDonem1 = null;
    private static ThisTerm buDonem2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.post(() -> {
            View fillView = findViewById(R.id.fillSpaceView);
            View content = findViewById(R.id.scrollContent);
            int screenHeight = scrollView.getHeight();
            int contentHeight = content.getHeight();

            if (contentHeight < screenHeight) {
                // Boşluk varsa doldur
                int extraSpace = screenHeight - contentHeight;
                ViewGroup.LayoutParams params = fillView.getLayoutParams();
                params.height = extraSpace;
                fillView.setLayoutParams(params);
            } else {
                // İçerik zaten taşmış, 40dp olarak kalacak
                ViewGroup.LayoutParams params = fillView.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 40, scrollView.getResources().getDisplayMetrics());
                fillView.setLayoutParams(params);
            }
        });

        TextView silme = findViewById(R.id.gbsilme);

        TextView gizli = findViewById(R.id.gizlilik);

        TextView bilgiAlma = findViewById(R.id.bilgiAlma);

        ImageButton menu = findViewById(R.id.menu);

        menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v, Gravity.END);

            // Menü şişirme
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // Menü öğelerine tıklama
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.notlariOzellestir) {
                    Intent intent = new Intent(MainActivity.this, NotOzellestir.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.gb) {
                    Intent intent = new Intent(MainActivity.this, GeriBildirimActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        bilgiAlma.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });

        silme.setOnClickListener(v -> {
            // Mail gönderme sayfasını açacak e-posta adresi
            String email = "unisaversilme@gmail.com";

            // E-posta göndermek için bir Intent oluşturun
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));  // E-posta adresini belirtiyoruz

            // Kullanıcıya bir e-posta uygulaması seçme imkanı sağlıyoruz
            startActivity(Intent.createChooser(intent, "Send email"));
        });

        gizli.setOnClickListener(v -> {
            // Açılacak URL
            String url = "https://sites.google.com/view/unisaverapp/ana-sayfa";  // Buraya açmak istediğiniz URL'yi yazın

            // URL'yi tarayıcıda açmak için bir Intent oluşturun
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);  // Intent ile tarayıcıyı açın
        });

        Button hesapAct1 = findViewById(R.id.agnoHesapBtn);
        hesapAct1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ManuelHesap.class);
            startActivity(intent);
        });
        Button hesapAct2 = findViewById(R.id.agnoKurtarBtn);
        hesapAct2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, KombinasyonluHesap.class);
            startActivity(intent);
        });
        Button hesapAct3 = findViewById(R.id.transBttn);
        hesapAct3.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FragmentTrans.class);
            startActivity(intent);
        });


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Token alımı başarısız", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d("FCM Token", token);
        });

        if (getStatus() == 0 && !MainActivity.this.isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                new Thread(() -> {
                    GradingSystemDao dao = AppDataBase.getInstance(appContext).gradingSystemDao();
                    List<GradingSystemEntity> all = dao.getAllSystems();
                    boolean exists = false;
                    for (GradingSystemEntity sys : all) {
                        if (sys.isDefault) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        GradingSystemEntity defaultSystem = new GradingSystemEntity();
                        defaultSystem.isSelected = true;
                        defaultSystem.name = "Uygulama Sistemi";
                        defaultSystem.isDefault = true;
                        long systemId = dao.insertSystem(defaultSystem);

                        List<GradeMappingEntity> mappings = new ArrayList<>();
                        mappings.add(new GradeMappingEntity((int) systemId, "AA", 4.0d));
                        mappings.add(new GradeMappingEntity((int) systemId, "BA", 3.5d));
                        mappings.add(new GradeMappingEntity((int) systemId, "BB", 3.0d));
                        mappings.add(new GradeMappingEntity((int) systemId, "CB", 2.5d));
                        mappings.add(new GradeMappingEntity((int) systemId, "CC", 2.0d));
                        mappings.add(new GradeMappingEntity((int) systemId, "DC", 1.5d));
                        mappings.add(new GradeMappingEntity((int) systemId, "DD", 1.0d));
                        mappings.add(new GradeMappingEntity((int) systemId, "FD", 0.5d));
                        mappings.add(new GradeMappingEntity((int) systemId, "FF", 0.0d));

                        dao.insertMappings(mappings);
                    }
                }).start();


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // İzin verilmemişse, izin istemek için kullanıcıya bildirim iznini sor
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_CODE_NOTIFICATION_PERMISSION);
                }
            }
            nextStatus();
        } else if (getStatus() == 1 && areNotificationsEnabled()) {
            sendOneTimeNotification();
            for (int i = 0; i < months.length; i++) {
                startWorker(months[i], days[i], i + 1);
            }
            nextStatus();
        }
    }

    private void startWorker(int month, int day, int workerId) {
        // Worker'ın başlatılacağı tarihi ayarlıyoruz
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        // Zamanı gecikme olarak hesapla
        long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delayMillis < 0) {
            // Eğer tarih geçmişse, bir sonraki yıl için ayarla
            calendar.add(Calendar.YEAR, 1);
            delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        }

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
        WorkManager.getInstance(getAppContext()).enqueue(workerRequest);

        Log.d("MainActivity", "Worker " + workerId + " scheduled for " + calendar.getTime());
    }

    public void setStatus(int status) {
        editor.putInt(KEY_STATUS, status);
        editor.apply();
    }

    // 🌟 Geçerli Durumu Alma
    public int getStatus() {
        return preferences.getInt(KEY_STATUS, STATUS_IDLE); // Varsayılan olarak 0 (STATUS_IDLE)
    }

    // 🌟 Bir Sonraki Duruma Geç
    public void nextStatus() {
        int currentStatus = getStatus();
        if (currentStatus < STATUS_COMPLETE) {
            setStatus(currentStatus + 1);
        }
    }

    private boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    private void sendOneTimeNotification() {
        // Android Oreo ve üzeri için bildirim kanalı oluşturulur
        NotificationChannel channel = new NotificationChannel(
                "default_channel",
                "Varsayılan Kanal",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Açıklama");
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.setLightColor(ContextCompat.getColor(this, R.color.mor));

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        } else {
            System.out.println("HATTTTAAAA");
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //.setSmallIcon(android.R.drawable.ic_dialog_info)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.ic_small_noti)
                .setColor(ContextCompat.getColor(this, R.color.white))
                .setContentIntent(pendingIntent)
                .setContentTitle("SEEELLLLAMMM ! ! !")
                .setContentText("UniSaver'ı kullandığın için teşekkür ederiz. Mükemmelsin!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        System.out.println("Bildirim geldi mi?");
        assert notificationManager != null;
        notificationManager.notify(1001, builder.build());

    }
    public static ThisTerm getBuDonem(boolean i) {
        if (i)
            return buDonem2;
        return buDonem1;
    }

    public static void resetBuDonem(boolean i) {
        if (i)
            buDonem2 = null;
        buDonem1 = null;
    }

    public static void setBuDonem(boolean i, ThisTerm buDonem) {
        if (i)
            buDonem2 = buDonem;
        buDonem1 = buDonem;
    }

    public static Context getAppContext() {
        return appContext;
    }
}