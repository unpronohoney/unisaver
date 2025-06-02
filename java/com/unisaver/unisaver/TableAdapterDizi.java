package com.unisaver.unisaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableAdapterDizi extends RecyclerView.Adapter<TableAdapterDizi.TableViewHolderAdapterDizi> {

    private List<Ders> data = new ArrayList<>();
    private int selectedPosition = -1;
    private boolean isScrolled = false;

    public TableAdapterDizi() {

    }

    @NonNull
    @Override
    public TableAdapterDizi.TableViewHolderAdapterDizi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notlar_row, parent, false);
        return new TableAdapterDizi.TableViewHolderAdapterDizi(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableAdapterDizi.TableViewHolderAdapterDizi holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class TableViewHolderAdapterDizi extends RecyclerView.ViewHolder {
        EditText notNicki, notEtkisi;
        ImageButton silme;
        public TableViewHolderAdapterDizi(@NonNull View itemView) {
            super(itemView);
            notNicki = itemView.findViewById(R.id.notNicki);
            notEtkisi = itemView.findViewById(R.id.notEtkisi);
            silme = itemView.findViewById(R.id.silme);
        }
    }

    public void isPositionVisible(RecyclerView recyclerView) {
        if (selectedPosition == -1) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        isScrolled = true;
        recyclerView.clearFocus();
        selectedPosition = -1;
    }

}
