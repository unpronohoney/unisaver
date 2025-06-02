package com.unisaver.unisaver;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManuelHesap extends Activity {
    //ca-app-pub-3940256099942544/9214589741        denemek için
    //ca-app-pub-7577324739927592/5937038172        benimki
    private static final String AD_UNIT_ID = "ca-app-pub-7577324739927592/5937038172";
    private static final String TAG = "ManuelHesap";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView;
    private FrameLayout adContainerView;
    private int dersNolar = 1;
    private TableAdapterManuel adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manuel);

        adContainerView = findViewById(R.id.ad_view_container);

        MobileAds.initialize(this, initializationStatus -> {});

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        new Handler(Looper.getMainLooper()).post(() -> {
            googleMobileAdsConsentManager =
                    GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
            googleMobileAdsConsentManager.gatherConsent(
                    this,
                    consentError -> {
                        if (consentError != null) {
                            // Consent not obtained in current session.
                            Log.w(
                                    TAG,
                                    String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                        }

                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            initializeMobileAdsSdk();
                        }

                        if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                            // Regenerate the options menu to include a privacy setting.
                            invalidateOptionsMenu();
                        }
                    });

            // This sample attempts to load ads using consent obtained in the previous session.
            if (googleMobileAdsConsentManager.canRequestAds()) {
                initializeMobileAdsSdk();
            }
        });

        String[] eskiharfler = {"Yok","AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};
        String[] yeniharfler = {"AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};

        Button dersGirisineGec = findViewById(R.id.gec1);
        Button yenile = findViewById(R.id.yenile1);
        Button dersEkle = findViewById(R.id.addDers1);
        ImageButton mainMenu = findViewById(R.id.anaSayfaDon1);

        EditText agno = findViewById(R.id.agno1);
        EditText kredi = findViewById(R.id.kredis1);
        EditText dersKredi = findViewById(R.id.dersKredi1);
        EditText dersAdi = findViewById(R.id.dersAdi);

        Spinner eskiHarf = findViewById(R.id.eskiHarf);
        Spinner yeniHarf = findViewById(R.id.yeniHarf);

        RecyclerView tablo = findViewById(R.id.tablo);

        TextView info = findViewById(R.id.hesaplananAgno1);
        TextView derseGecis = findViewById(R.id.derseGecis);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, eskiharfler);
        adapter1.setDropDownViewResource(R.layout.spinner_layout_list);
        eskiHarf.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, yeniharfler);
        adapter2.setDropDownViewResource(R.layout.spinner_layout_list);
        yeniHarf.setAdapter(adapter2);

        mainMenu.setOnClickListener(view -> {
            info.setText(getString(R.string.agnoShow));
            adapter = null;
            dersNolar = 1;
            MainActivity.resetBuDonem(false);
            finish();
        });

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        yenile.setOnClickListener(v -> {
            derseGecis.setText(getString(R.string.dersBekle));
            info.setText(getString(R.string.agnoShow));
            adapter.reset();
            dersNolar = 1;
            dersAdi.setText("");
            agno.setText("");
            kredi.setText("");
            dersKredi.setText("");
            MainActivity.resetBuDonem(false);
        });

        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher(() -> {
            info.setText(getString(R.string.agnoShow));
            adapter = null;
            dersNolar = 1;
            MainActivity.resetBuDonem(false);
            finish();
        });

        dersGirisineGec.setOnClickListener(v -> {
            if (agno.getText().length() == 0 || kredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(String.valueOf(agno.getText())) > 4.0 || Integer.parseInt(String.valueOf(kredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), "Agno 4'ten küçük olamaz, kredi 0'dan küçük olamaz.", Toast.LENGTH_LONG).show();
            } else {
                MainActivity.setBuDonem(false, new ThisTerm(new GenelNot(Integer.parseInt(String.valueOf(kredi.getText())),
                        Double.parseDouble(String.valueOf(agno.getText())))));
                MainActivity.getBuDonem(false).yuvarlamaPaylari();
                if (adapter == null) {
                    adapter = new TableAdapterManuel(info);
                }
                tablo.setLayoutManager(new LinearLayoutManager(this));
                tablo.setAdapter(adapter);
                dersNolar = 1;
                Toast.makeText(MainActivity.getAppContext(), "Ders girebilirsiniz.", Toast.LENGTH_SHORT).show();
                derseGecis.setText(getString(R.string.dersEkleyebilirsin));
            }
        });

        dersEkle.setOnClickListener(v -> {
            if (MainActivity.getBuDonem(false) == null) {
                Toast.makeText(MainActivity.getAppContext(), "Önce temel bilgilerinizi girin.", Toast.LENGTH_SHORT).show();
            } else if (dersAdi.getText().length() == 0 || dersKredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(String.valueOf(dersKredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), "Kredi 0'dan küçük olamaz.", Toast.LENGTH_SHORT).show();
            } else {
                Ders ders = new Ders(dersNolar, Integer.parseInt(String.valueOf(dersKredi.getText())),
                        eskiHarf.getSelectedItem().toString(), String.valueOf(dersAdi.getText()) , yeniHarf.getSelectedItem().toString());
                adapter.dersEkle(ders);
                dersNolar++;
                Toast.makeText(MainActivity.getAppContext(), "Ders eklendi.", Toast.LENGTH_SHORT).show();
                info.setText(MainActivity.getBuDonem(false).getAgnoInfo());
            }
        });
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void loadBanner() {
        // [START create_ad_view]
        // Create a new ad view.
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adView.setAdSize(getAdSize());

        // Replace ad container with new ad view.
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        // [END create_ad_view]

        // [START load_ad]
        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // [END load_ad]
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Set your test devices.
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList(MainActivity.TEST_DEVICE_HASHED_ID))
                        .build());

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});

                    // Load an ad on the main thread.
                    runOnUiThread(this::loadBanner);
                })
                .start();
    }

    // [START get_ad_size]
    // Get the ad size with screen width.
    public AdSize getAdSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

}
