package com.unisaver.unisaver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotDizisiOlustur extends AppCompatActivity {

    private TableAdapterNotDizileri adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.harf_notlari_act);

        adapter = new TableAdapterNotDizileri(this);

        ImageButton anaSayfa = findViewById(R.id.anaSayfaDon);

        anaSayfa.setOnClickListener(view -> {
            adapter = null;
            finish();
        });

        RecyclerView tablo = findViewById(R.id.harfTablo);

        Button tamam = findViewById(R.id.tamam);

        tamam.setOnClickListener(v -> {
            //seçileni uygula
            adapter = null;
            finish();
        });

        ImageButton diziEkle = findViewById(R.id.harfNotlariEkle);
        diziEkle.setOnClickListener(v -> {
            NameInputBottomSheet bottomSheet = new NameInputBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "NameInputBottomSheet");
        });

        tablo.setAdapter(adapter);
        tablo.setLayoutManager(new LinearLayoutManager(this));

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

    }

}
