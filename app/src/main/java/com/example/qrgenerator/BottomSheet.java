package com.example.qrgenerator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {

    private sheetListener listener;

    Button copyBtn;
    TextView qrText;

    String qrResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet,container,false);

        qrResult = ScanActivity.QR_RESULT;

        copyBtn = view.findViewById(R.id.button3);
        qrText = view.findViewById(R.id.qr_text);
        qrText.setText(qrResult);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBtnClick(qrResult);
                dismiss();
            }
        });

        return view;
    }

    public interface sheetListener{
        void onBtnClick(String text);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (sheetListener) context;
    }
}
