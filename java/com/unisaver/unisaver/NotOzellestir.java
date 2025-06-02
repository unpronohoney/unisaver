package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotOzellestir extends Activity {

    private TableAdapterDizi adapter = null;
    private String diziIsim;
    private boolean duzenlemeMi = false;
    private List<String> isimler = new ArrayList<>();
    private List<Double> notlar = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.harf_notu_olusturma);

        Intent intent = getIntent();
        diziIsim = intent.getStringExtra("diziIsim");

        if (diziIsim.length() == 0) {
            duzenlemeMi = true;
            diziIsim = intent.getStringExtra("diziIsimDuzenleme");
        }

        adapter = new TableAdapterDizi();

        ImageButton anaSayfa = findViewById(R.id.anaSayfaDon3);

        anaSayfa.setOnClickListener(view -> {
            isimler = new ArrayList<>();
            notlar = new ArrayList<>();
            adapter = null;
            duzenlemeMi = false;
            finish();
        });

        RecyclerView tablo = findViewById(R.id.tablo);

        tablo.setAdapter(adapter);
        tablo.setLayoutManager(new LinearLayoutManager(this));

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        Button bitir = findViewById(R.id.harfDizisiOlustur);

        bitir.setOnClickListener(v -> {
            //değiştirilmiş diziyi güncelleme ya da oluşturulmuş diziyi kaydetme
            if (duzenlemeMi) {

            } else {
                new Thread(() -> {
                    AppDataBase db = AppDataBase.getInstance(MainActivity.getAppContext());
                    GradingSystemDao dao = db.gradingSystemDao();

                    // Yeni sistem oluştur
                    GradingSystemEntity newSystem = new GradingSystemEntity();
                    newSystem.name = diziIsim;
                    newSystem.isDefault = false;

                    long systemId = dao.insertSystem(newSystem);

                    // Not karşılıklarını oluştur
                    List<GradeMappingEntity> mappings = new ArrayList<>();
                    for (int i = 0; i < notlar.size(); i++) {
                        mappings.add(new GradeMappingEntity((int) systemId, isimler.get(i), notlar.get(i)));
                    }

                    dao.insertMappings(mappings);
                }).start();

            }
            isimler = new ArrayList<>();
            notlar = new ArrayList<>();
            adapter = null;
            duzenlemeMi = false;
            finish();
        });

        EditText isim = findViewById(R.id.notDizisiIsmi);
        isim.setText(diziIsim);

        EditText harfIsim = findViewById(R.id.notIsmi);
        EditText harfEtki = findViewById(R.id.notunEtkisi);

        Button notuEkle = findViewById(R.id.notEkle);

        notuEkle.setOnClickListener(v -> {
            if (harfIsim.getText().length() == 0 || harfEtki.getText().length() == 0) {
                Toast.makeText(this, "Lütfen alanları boş bırakmayın.", Toast.LENGTH_SHORT).show();
            } else {
                double not = Double.parseDouble(String.valueOf(harfEtki.getText()));
                String harf = harfIsim.getText().toString();
                isimler.add(harf);
                notlar.add(not);
            }
        });

    }

}
