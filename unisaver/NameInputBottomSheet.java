package com.unisaver.unisaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NameInputBottomSheet extends BottomSheetDialogFragment {

    private EditText nameEditText;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_name_input, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                // Yeni aktiviteye geç
                Intent intent = new Intent(getActivity(), NotOzellestir.class);
                intent.putExtra("diziIsim", name);
                startActivity(intent);

                // BottomSheet'i kapat
                dismiss();
            } else {
                nameEditText.setError("İsim boş olamaz");
            }
        });

        return view;
    }
}