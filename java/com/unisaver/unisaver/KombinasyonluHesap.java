package com.unisaver.unisaver;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class KombinasyonluHesap extends Activity {

    //ca-app-pub-3940256099942544/9214589741        denemek için
    //ca-app-pub-7577324739927592/9335489026        benimki

    private static final String AD_UNIT_ID = "ca-app-pub-7577324739927592/9335489026";
    private static final String TAG = "KombinasyonluHesap";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView;
    private FrameLayout adContainerView;
    private IhtimallerDizisi tutucu2 = null;
    private GenelNot tutucu1 = null;
    ArrayAdapter<String> adapter4 = null;
    private ArrayList<Ders> dersler = new ArrayList<>();
    private int dersSayac = 0;
    private int dersKrediTop = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kombinasyonlu);

        adContainerView = findViewById(R.id.ad_view_container);

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

        String[] harfler = {"Yok","AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};
        String[] minHarfler = {"AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "Limitsiz"};
        String[] maxHarfler = {"Limitsiz", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};

        Button dersGirisineGec = findViewById(R.id.gec1);
        Button yenile = findViewById(R.id.yenile1);
        Button dersEkle = findViewById(R.id.addDers1);
        Button dersBitti = findViewById(R.id.finishAdding);
        Button ksh = findViewById(R.id.ksh);
        Button komGor = findViewById(R.id.olasGoruntule);
        ImageButton mainMenu = findViewById(R.id.anaSayfaDon1);

        EditText agno = findViewById(R.id.agno1);
        EditText kredi = findViewById(R.id.kredis1);
        EditText dersKredi = findViewById(R.id.dersKredi1);
        EditText minAgno = findViewById(R.id.minAgno);
        EditText maxAgno = findViewById(R.id.maxAgno);
        EditText komSayi = findViewById(R.id.olasilikSayisi);
        EditText dersAdi = findViewById(R.id.dersAdi);

        TextView komBilgi = findViewById(R.id.olasBilgi);
        TextView dersSayaci = findViewById(R.id.dersSayaci);
        TextView derseGecis = findViewById(R.id.derseGecis);

        dersSayaci.setText(getString(R.string.derssayar, dersSayac));

        Spinner dersHarf = findViewById(R.id.harfNotu1);
        Spinner minHarf = findViewById(R.id.minHarf);
        Spinner maxHarf = findViewById(R.id.maxHarf);
        Spinner komlar = findViewById(R.id.olasiliklar);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, harfler);
        adapter1.setDropDownViewResource(R.layout.spinner_layout_list);
        dersHarf.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, minHarfler);
        adapter2.setDropDownViewResource(R.layout.spinner_layout_list);
        minHarf.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, maxHarfler);
        adapter3.setDropDownViewResource(R.layout.spinner_layout_list);
        maxHarf.setAdapter(adapter3);

        mainMenu.setOnClickListener(view -> {
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            MainActivity.resetBuDonem(true);
            adapter4 = null;
            finish();
        });

        yenile.setOnClickListener(v -> {
            derseGecis.setText(getString(R.string.dersBekle));
            dersAdi.setText("");
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            dersSayaci.setText(getString(R.string.derssayar, dersSayac));
            agno.setText("");
            kredi.setText("");
            dersKredi.setText("");
            minAgno.setText("");
            maxAgno.setText("");
            komSayi.setText("");
            komBilgi.setText("");
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            adapter4 = null;
            MainActivity.resetBuDonem(true);
        });

        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher(() -> {
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            MainActivity.resetBuDonem(true);
            adapter4 = null;
            finish();
        });

        dersGirisineGec.setOnClickListener(v -> {
            if (agno.getText().length() == 0 || kredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(String.valueOf(agno.getText())) > 4.0 || Integer.parseInt(String.valueOf(kredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), "Agno 4'ten küçük olamaz, kredi 0'dan küçük olamaz.", Toast.LENGTH_LONG).show();
            } else {
                tutucu1 = new GenelNot(Integer.parseInt(String.valueOf(kredi.getText())), Double.parseDouble(String.valueOf(agno.getText())));
                Toast.makeText(MainActivity.getAppContext(), "Ders girebilirsiniz.", Toast.LENGTH_SHORT).show();
                derseGecis.setText(getString(R.string.dersEkleyebilirsin));
            }
        });

        dersEkle.setOnClickListener(v -> {
            if (tutucu1 == null) {
                Toast.makeText(MainActivity.getAppContext(), "Önce temel bilgilerinizi girin.", Toast.LENGTH_SHORT).show();
            } else if (dersKredi.getText().length() == 0 || Integer.parseInt(String.valueOf(dersKredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), "Lütfen krediyi girin ve 0'dan küçük olamaz.", Toast.LENGTH_SHORT).show();
            } else {
                if (!dersHarf.getSelectedItem().toString().equals("Yok")) {
                    dersKrediTop += Integer.parseInt(String.valueOf(dersKredi.getText()));
                }
                boolean hata = false;
                if (dersKrediTop > tutucu1.getKrediSayisi()) {
                    hata = true;
                }
                if (!hata) {
                    if (dersAdi.getText().length() > 0) {
                        dersler.add(new Ders(Integer.parseInt(String.valueOf(dersKredi.getText())),
                                dersHarf.getSelectedItem().toString(), String.valueOf(dersAdi.getText())));
                    } else {
                        dersler.add(new Ders(Integer.parseInt(String.valueOf(dersKredi.getText())), dersHarf.getSelectedItem().toString()));
                    }
                    dersSayac++;
                    dersSayaci.setText(getString(R.string.derssayar, dersSayac));
                } else {
                    Toast.makeText(MainActivity.getAppContext(), "Önceki harf notu olan derslerin kredisi toplam krediden fazla olamaz.", Toast.LENGTH_LONG).show();
                }
            }
        });

        dersBitti.setOnClickListener(v -> {
            if (dersler.isEmpty()) {
                Toast.makeText(MainActivity.getAppContext(), "Hiç ders girmediniz.", Toast.LENGTH_SHORT).show();
            } else {
                dersSayac = 0;
                dersSayaci.setText(getString(R.string.tekrarDers));
                MainActivity.setBuDonem(true, new ThisTerm(dersler.size(), tutucu1, true));
                MainActivity.getBuDonem(true).dersGirisiArr(dersler);
                MainActivity.getBuDonem(true).yuvarlamaPaylari();
                MainActivity.getBuDonem(true).ihtimallerDizisi();
                dersler.clear();
                dersKrediTop = 0;
            }
        });

        ksh.setOnClickListener(v -> {
            if (MainActivity.getBuDonem(true) == null) {
                Toast.makeText(MainActivity.getAppContext(), "Dönem bilgilerinizi girmediniz.", Toast.LENGTH_SHORT).show();
            } else if (minAgno.getText().length() == 0 || komSayi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), "Lütfen gerekli alanları doldurun.", Toast.LENGTH_SHORT).show();
            } else if (minHarf.getSelectedItemPosition() < maxHarf.getSelectedItemPosition()) {
                Toast.makeText(MainActivity.getAppContext(), "Min harf, max harften büyük olamaz.", Toast.LENGTH_SHORT).show();
            } else {
                double maxGno = -1;
                double minGno = Double.parseDouble(String.valueOf(minAgno.getText()));
                boolean deneme = true;
                if (maxAgno.getText().length() != 0) {
                    maxGno = Double.parseDouble(String.valueOf(maxAgno.getText()));
                    if (maxGno < minGno) {
                        deneme = false;
                    }
                }
                if (deneme) {
                    int kombinasyonlar = Integer.parseInt(String.valueOf(komSayi.getText()));
                    boolean oldumu = MainActivity.getBuDonem(true).newKsh(minGno, maxGno,
                            (String) minHarf.getSelectedItem(), (String) maxHarf.getSelectedItem(), kombinasyonlar);
                    tutucu2 = MainActivity.getBuDonem(true).getPossibilities();
                    if (oldumu) {
                        tutucu2.listByAgno();
                        Toast.makeText(MainActivity.getAppContext(), "Kombinasyonlar başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                        adapter4 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, olasilik());
                        adapter4.setDropDownViewResource(R.layout.spinner_layout_list);
                        komlar.setAdapter(adapter4);
                    } else {
                        if (tutucu2.getCounter() == 0) {
                            Toast.makeText(MainActivity.getAppContext(), "Kombinasyon dizisi oluşturulamadı.", Toast.LENGTH_SHORT).show();
                        } else {
                            tutucu2.listByAgno();
                            Toast.makeText(MainActivity.getAppContext(), tutucu2.getCounter() +" kombinasyon oluşturuldu.", Toast.LENGTH_SHORT).show();
                            adapter4 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, olasilik());
                            adapter4.setDropDownViewResource(R.layout.spinner_layout_list);
                            komlar.setAdapter(adapter4);
                        }
                    }
                }
            }
        });

        komGor.setOnClickListener(v -> {
            if (tutucu2 != null) {
                if (tutucu2.getCounter() > 0) {
                    komBilgi.setText(Html.fromHtml(MainActivity.getBuDonem(true).getPossibilities().toStringOnePossibility(komlar.getSelectedItemPosition()+1), Html.FROM_HTML_MODE_LEGACY));
                }
            }
        });

    }

    public String[] olasilik() {
        String[] olasilikGor = new String[tutucu2.getEskiAgnolar().size()];
        int j = 0;
        for (Integer i : tutucu2.getEskiAgnolar().keySet()) {
            String sapma;
            if (tutucu2.getSapma(i).equals("")) {
                sapma = "";
            } else {
                sapma = tutucu2.getSapma(i).substring(0, 1);
                sapma += tutucu2.getSapmaDegeri(i);
            }
            olasilikGor[j] = getString(R.string.spinPoss, i, tutucu2.getAgno(i)+sapma);
            j++;
        }
        return olasilikGor;
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
