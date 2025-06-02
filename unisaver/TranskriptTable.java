package com.unisaver.unisaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;

public class TranskriptTable extends Activity {

    private BelgeliHesap bh = null;
    private TableAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_table_shower);

        RecyclerView recyclerView = findViewById(R.id.tablo);
        int dividerColor = ContextCompat.getColor(this, android.R.color.black);
        int dividerSize = 4; // Çizgi kalınlığı (dp)
        recyclerView.addItemDecoration(new TableItemDecoration(dividerColor, dividerSize));

        ArrayAdapter<String> adap;

        TextView agno = findViewById(R.id.agnoBilgisi);
        TextView kredis = findViewById(R.id.krediBilgisi);
        Button buttonDersEkle = findViewById(R.id.dersEkle);
        Button showNotes = findViewById(R.id.notSystem);
        Typeface typeface3 = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.alexandria);
        EditText editTextDersAdi = findViewById(R.id.addDersAdi), editTextKredi = findViewById(R.id.addDersCred);
        editTextDersAdi.setTypeface(typeface3);
        editTextKredi.setTypeface(typeface3);
        Spinner spinnerNotlar = findViewById(R.id.addDersSpinNot);
        Button sonSil = findViewById(R.id.sonSilme);
        ImageButton anaMenu = findViewById(R.id.anaSayfayaDon);
        Button basaAl = findViewById(R.id.basaAl);

        anaMenu.setOnClickListener(view -> finish());
        sonSil.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.sonSilineniGeriAl();
            }
        });
        basaAl.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.basaAl();
            }
        });

        try {
            String str = getIntent().getStringExtra("text");
            bh = new BelgeliHesap(str);
            adapter = new TableAdapter(bh, agno, kredis);
            String[] strArr = new String[bh.getMachine().getNotes().keySet().size()];
            int i = 0;
            for (String a : bh.getMachine().getNotes().keySet()) {
                strArr[i] = a;
                i++;
            }
            adap = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_layout, strArr);
            adap.setDropDownViewResource(R.layout.spinner_layout_list);
            spinnerNotlar.setAdapter(adap);

            String message = "";
            if (Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0 != bh.getMachine().getAgno()) {
                message += "Girdiğiniz belgeye göre güncel AGNO(GNO) notunuz: "+bh.getMachine().getAgno()+ " iken,\n" +
                        "Belgedeki ders bilgilerine göre hesaplanan AGNO(GNO) notu: "+Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0+
                        " bulunmuştur.\nYapacağınız değişiklikler "+ Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0 + " AGNO notu üzerinden yapılacaktır.\n";
            }
            if (!message.isEmpty()) {
                message += "\n---ve---\n";
            }
            if (bh.getMachine().getComputedCred() != bh.getMachine().getCred()) {
                message += "Girdiğiniz belgeye göre tamamladığınız kredi sayısı: " + bh.getMachine().getCred() + " iken,\n" +
                        "Belgedeki ders bilgilerine göre hesaplanan kredi sayısı: " + bh.getMachine().getComputedCred() + " bulunmuştur.\n" +
                        "Yapacağınız değişiklikler " + bh.getMachine().getComputedCred() + " kredi sayısı üzerinden hesaplanacaktır.\n";
            }

            if (!message.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Eşitsizlik Bilgilendirme")
                        .setMessage(message)
                        .setPositiveButton("Tamam", (dialog, which) -> {
                        })
                        .show();
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            Toast.makeText(this, "Bir hata meydana geldi. Lütfen geri bildirin.", Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        buttonDersEkle.setOnClickListener(v -> {
            if (editTextDersAdi.getText().length() != 0 && editTextKredi.getText().length() != 0) {
                String dersAdi = editTextDersAdi.getText().toString();
                int kredi = Integer.parseInt(editTextKredi.getText().toString());
                String dersNotu = spinnerNotlar.getSelectedItem().toString();
                boolean cont = true;
                if (kredi < 0) {
                    cont = false;
                    Toast.makeText(this, "Dersin kredisi 0'dan küçük olamaz.", Toast.LENGTH_SHORT).show();
                }
                if (cont && adapter == null) {
                    Toast.makeText(this, "Bir hata meydana geldi. Lütfen geri bildirin.", Toast.LENGTH_SHORT).show();
                } else if (cont) {
                    adapter.addDers(dersAdi, kredi, dersNotu);
                }
            }
        });

        showNotes.setOnClickListener(v -> {
            if (bh != null) {
                StringBuilder message = new StringBuilder("Tabloya ders eklerken not seçmenize yardımcı olmak için" +
                        " üniversitenizde kullanılan harf notları ve karşılıkları:\n\n");
                for (String not : bh.getMachine().getNotes().keySet()) {
                    message.append(not).append("  -->  ").append(bh.getMachine().getNotes().get(not)).append("\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notlar ve Karşılıkları")
                        .setMessage(message.toString())
                        .setPositiveButton("Tamam", (dialog, which) -> {
                        })
                        .show();
            } else {
                Toast.makeText(MainActivity.getAppContext(), "Bir hata meydana geldi. Lütfen geri bildirin.", Toast.LENGTH_SHORT).show();
            }

        });

    }
}
