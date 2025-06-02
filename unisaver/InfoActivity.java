package com.unisaver.unisaver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class InfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.application_info);

        TextView baslik = findViewById(R.id.textView2);
        TextView text1 = findViewById(R.id.textView3);
        TextView text2 = findViewById(R.id.textView7);
        TextView text3 = findViewById(R.id.textView8);
        TextView text4 = findViewById(R.id.textView9);
        TextView text5 = findViewById(R.id.textView10);
        TextView text6 = findViewById(R.id.textView11);
        TextView text7 = findViewById(R.id.textView12);
        TextView text8 = findViewById(R.id.textView13);
        TextView text9 = findViewById(R.id.textView14);
        TextView text14 = findViewById(R.id.textView4);

        Typeface typeface = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.acme);
        Typeface typeface2 = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.basic);
        Typeface typeface3 = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.alexandria);

        baslik.setTypeface(typeface, Typeface.BOLD);
        text1.setTypeface(typeface2);
        text2.setTypeface(typeface3, Typeface.BOLD);
        text3.setTypeface(typeface2);
        text4.setTypeface(typeface3, Typeface.BOLD);
        text5.setTypeface(typeface2);
        text6.setTypeface(typeface3, Typeface.BOLD);
        text7.setTypeface(typeface2);
        text8.setTypeface(typeface3, Typeface.BOLD);
        text9.setTypeface(typeface2);
        text14.setTypeface(typeface2, Typeface.BOLD);

        ImageButton backButton = findViewById(R.id.anaSayfaDonIn);
        backButton.setOnClickListener(view -> finish());
    }
}
