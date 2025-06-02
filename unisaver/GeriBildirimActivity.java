package com.unisaver.unisaver;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
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

public class GeriBildirimActivity extends AppCompatActivity {

    //ca-app-pub-3940256099942544/9214589741        denemek için

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String TAG = "GeriBildirimActivty";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView;
    private FrameLayout adContainerView;
    private ActivityResultLauncher<Intent> pdfForGB;
    private File pdfFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.geri_bildirim);

        adContainerView = findViewById(R.id.ad_view_container3);

        MobileAds.initialize(this, initializationStatus -> {});

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

        Typeface typeface3 = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.alexandria);

        EditText gbKonu = findViewById(R.id.gbKonu);
        EditText gbAcik = findViewById(R.id.gbAciklama);
        gbAcik.setTypeface(typeface3);
        TextView gbDos = findViewById(R.id.dosyaAdi);
        Button gbTrans = findViewById(R.id.gbTrans);
        Button gbGonder = findViewById(R.id.gbGonder);

        ImageButton anaMenu = findViewById(R.id.anaSayfaDon3);

        anaMenu.setOnClickListener(view -> finish());

        pdfForGB = registerForActivityResult(
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
                                        pdfFile = getFileFromUri(uri);
                                        String str = getFileName(uri);
                                        gbDos.setText(str);
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

        gbTrans.setOnClickListener(v -> openFileForGB());
        gbGonder.setOnClickListener(v -> {
            if (gbKonu.getText().length() != 0 && gbAcik.getText().length() != 0) {
                gbGonder.setText(R.string.sending);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    GeriBildirim geriBildirim = new GeriBildirim(gbKonu.getText().toString(), gbAcik.getText().toString(), pdfFile);
                    // UI thread'e geri dön ve mesaj göster
                    new Handler(Looper.getMainLooper()).post(() -> {
                        pdfFile = null;
                        gbDos.setText("");
                        gbGonder.setText(R.string.send);
                        Toast.makeText(this, geriBildirim.getMessage(), Toast.LENGTH_LONG).show();
                    });
                });
            }
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

    private void openFileForGB() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf"); // Sadece PDF dosyalarını gösterir
        pdfForGB.launch(intent);
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
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adView.setAdSize(getAdSize());

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

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
