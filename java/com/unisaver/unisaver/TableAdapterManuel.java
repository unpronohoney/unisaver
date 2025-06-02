package com.unisaver.unisaver;

import android.content.Context;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TableAdapterManuel extends RecyclerView.Adapter<TableAdapterManuel.TableViewHolderMan>{

    private List<Ders> data = new ArrayList<>();
    private TextView results;
    private Collection<String> validNotes;
    private Collection<String> validEskiNotes;
    private int selectedPosition = -1;
    private boolean isScrolled = false;

    public TableAdapterManuel(TextView txt) {
        this.results = txt;
        this.validNotes = Ders.harfNotlari.keySet();
        this.validEskiNotes = new HashSet<>(Ders.harfNotlari.keySet());
        validEskiNotes.add("Yok");
    }

    @NonNull
    @Override
    public TableAdapterManuel.TableViewHolderMan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_item_hesap, parent, false);
        return new TableAdapterManuel.TableViewHolderMan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableAdapterManuel.TableViewHolderMan holder, int position) {
        Ders ders = data.get(position);
        holder.dersNo.setText(String.valueOf(ders.getDersNo()));
        holder.dersAdi.setText(ders.getIsim());
        Typeface typeface = ResourcesCompat.getFont(MainActivity.getAppContext(), R.font.anybody);
        holder.dersAdi.setTypeface(typeface);
        holder.kredi.setText(String.valueOf(ders.getCredit()));
        holder.eskiHarf.setText(ders.getHarfNotu());
        holder.yeniHarf.setText(ders.getYeniHarf());

        if (position == selectedPosition) {
            holder.yeniHarf.requestFocus();
        } else {
            holder.yeniHarf.clearFocus();
        }

        holder.kredi.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    int newKredi = Integer.parseInt(holder.kredi.getText().toString());
                    if (newKredi > 0) {
                        MainActivity.getBuDonem(false).dersKrediGuncelle(ders, newKredi);
                        ders.setCredit(newKredi);
                        results.setText(MainActivity.getBuDonem(false).getAgnoInfo());
                    } else {
                        Toast.makeText(MainActivity.getAppContext(), "Geçersiz kredi!", Toast.LENGTH_SHORT).show();
                        holder.kredi.setText(ders.getCredit());
                    }
                }
            }
        });

        holder.kredi.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Klavye kapat
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Focus'u kaldır
                v.clearFocus();
                return true;
            }
            return false;
        });

        holder.eskiHarf.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    String eskiHarfGuncel = holder.eskiHarf.getText().toString();
                    boolean isValid = true;
                    double point = Ders.getPoint(eskiHarfGuncel);
                    if (point == 5) {
                        isValid = false;
                    }
                    if (!isValid) {
                        Toast.makeText(MainActivity.getAppContext(), "Geçersiz ders notu!", Toast.LENGTH_SHORT).show();
                        holder.eskiHarf.setText(ders.getHarfNotu());
                    } else {
                        MainActivity.getBuDonem(false).dersEskiHarfGuncelle(ders, eskiHarfGuncel);
                        ders.setHarfNotu(eskiHarfGuncel);
                        results.setText(MainActivity.getBuDonem(false).getAgnoInfo());
                    }
                }
            }
        });

        holder.eskiHarf.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Klavye kapat
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Focus'u kaldır
                v.clearFocus();
                return true;
            }
            return false;
        });

        holder.yeniHarf.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    String yeniHarfGuncel = holder.yeniHarf.getText().toString();
                    boolean isValid = true;
                    double point = Ders.getPoint(yeniHarfGuncel);
                    if (point == 5) {
                        isValid = false;
                    }
                    if (!isValid) {
                        Toast.makeText(MainActivity.getAppContext(), "Geçersiz ders notu!", Toast.LENGTH_SHORT).show();
                        holder.yeniHarf.setText(ders.getYeniHarf());
                    } else {
                        MainActivity.getBuDonem(false).dersYeniHarfGuncelle(ders, yeniHarfGuncel);
                        ders.setYeniHarf(yeniHarfGuncel);
                        results.setText(MainActivity.getBuDonem(false).getAgnoInfo());
                    }
                }
            }
        });

        holder.yeniHarf.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Klavye kapat
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Focus'u kaldır
                v.clearFocus();
                return true;
            }
            return false;
        });

        holder.kredi.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.kredi.requestFocus();
        });

        holder.eskiHarf.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.eskiHarf.requestFocus();
        });

        holder.yeniHarf.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.yeniHarf.requestFocus();
        });

        holder.silme.setOnClickListener(v -> {
            MainActivity.getBuDonem(false).dersSilme(ders);
            data.remove(position);
            notifyItemRemoved(position);
            for (int i = position; i < data.size(); i++) {
                data.get(i).setDersNo(i+1);
                notifyItemChanged(i);
            }
            results.setText(MainActivity.getBuDonem(false).getAgnoInfo());
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class TableViewHolderMan extends RecyclerView.ViewHolder {
        TextView dersNo, dersAdi;
        EditText kredi, eskiHarf, yeniHarf;
        ImageButton silme;
        public TableViewHolderMan(@NonNull View itemView) {
            super(itemView);
            dersNo = itemView.findViewById(R.id.dersNo);
            dersAdi = itemView.findViewById(R.id.dersAdi);
            kredi = itemView.findViewById(R.id.dersKredi);
            eskiHarf = itemView.findViewById(R.id.dersNotuEski);
            yeniHarf = itemView.findViewById(R.id.dersNotuYeni);
            silme = itemView.findViewById(R.id.dersSilButton);
        }
    }

    public void reset() {
        data.clear();
        notifyDataSetChanged();
    }

    public void isPositionVisible(RecyclerView recyclerView) {
        if (selectedPosition == -1) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        isScrolled = true;
        recyclerView.clearFocus();
        selectedPosition = -1;
    }

    public void dersEkle(Ders ders) {
        if (MainActivity.getBuDonem(false).getPastTerms().getKrediSayisi() > Integer.MAX_VALUE - ders.getCredit()) {
            Toast.makeText(MainActivity.getAppContext(), "Çok yüksek değerler girmeyin lütfen!", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.getBuDonem(false).manuelDersGirisi(ders);
            data.add(ders);
            notifyItemInserted(data.size() - 1);
            results.setText(MainActivity.getBuDonem(false).getAgnoInfo());
        }
    }
}
