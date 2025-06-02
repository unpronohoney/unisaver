package com.unisaver.unisaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentTrans extends AppCompatActivity {

    //ca-app-pub-3940256099942544/1033173712        deneme tam ekran reklam
    //ca-app-pub-3940256099942544/9214589741        denemek için
    //ca-app-pub-7577324739927592/6751664074        benimki banner
    //ca-app-pub-7577324739927592/1204318764        benimki gecis
    private static final String AD_UNIT_ID = "ca-app-pub-7577324739927592/6751664074";
    private static final String AD_UNIT_ID_2 = "ca-app-pub-7577324739927592/1204318764";
    private static final String TAG = "FragmentTrans";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private FrameLayout adContainerView;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private File pdfFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hesaplama_activity_3);

        adContainerView = findViewById(R.id.ad_view_container3);

        new Handler(Looper.getMainLooper()).post(this::loadInterstitialAd);

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

        Button transSecici = findViewById(R.id.transBelgesi);
        PDFBoxResourceLoader.init(MainActivity.getAppContext());

        ImageButton anaMenu = findViewById(R.id.anaSayfaDon3);

        anaMenu.setOnClickListener(view -> finish());

        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            if (uri != null) {
                                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                                if (inputStream != null) {
                                    PDDocument document = PDDocument.load(inputStream);
                                    PDFTextStripper stripper = new PDFTextStripper();
                                    String text = stripper.getText(document);
                                    document.close();
                                    inputStream.close();
                                    if (control(text)) {
                                        Toast.makeText(this, "Belgeniz okunamadı...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Belgeniz okundu.", Toast.LENGTH_SHORT).show();
                                        if (mInterstitialAd != null) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                mInterstitialAd.show(this); // Geçiş reklamını göster
                                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        // Reklam kapatıldığında yeni Activity'e geçiş
                                                        Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                                        intent.putExtra("text", text);
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        // Eğer reklam gösterilemezse, hemen yeni Activity açılır
                                                        Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                                        intent.putExtra("text", text);
                                                        startActivity(intent);
                                                    }
                                                });
                                            });
                                        } else {
                                            // Eğer reklam yüklenmemişse, doğrudan geçiş yap
                                            Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                            intent.putExtra("text", text);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Belgeniz okunamadı...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("PDF_Text", "text");
                        }

                    }
                }
        );

        transSecici.setOnClickListener(v -> openFilePicker());

        Button degerlendir = findViewById(R.id.degerlendirme);

        degerlendir.setOnClickListener(v -> {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                    flow.addOnCompleteListener(flowTask -> {
                        Toast.makeText(this, "İlginiz için teşekkür ederiz!!!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Eğer başarısız olursa burada fallback yapabilirsin (örn. Play Store'a yönlendirme)
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        });


    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            // getContext().getCacheDir() kullan çünkü Fragment içindesin
            file = File.createTempFile("temp_pdf", ".pdf", this.getCacheDir());

            // Dosya içeriğini kopyala
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            if (inputStream != null) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
            } else {
                outputStream.close();
                Toast.makeText(this, "Belgeniz okunamadı...", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Belgeniz okunamadı...", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = null;

        try {
            // ContentResolver ile dosyanın meta verilerini alın
            ContentResolver contentResolver = this.getContentResolver();
            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex); // Dosya adını alın
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Belgeniz okunamadı...", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close(); // Cursor'u kapatmayı unutmayın
            }
        }
        return fileName;
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf"); // Sadece PDF dosyalarını gösterir
        pdfPickerLauncher.launch(intent);
    }
    public boolean control(String text) {
        String cont = "yükleyeceğiniz e-Devlet Kapısına ait Barkodlu Belge Doğrulama veya YÖK Mobil uygulaması vasıtası ile yandaki karekod";
        return !text.contains(cont);
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

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, AD_UNIT_ID_2, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                mInterstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                mInterstitialAd = null; // Reklam yüklenemedi
            }
        });
    }
}
