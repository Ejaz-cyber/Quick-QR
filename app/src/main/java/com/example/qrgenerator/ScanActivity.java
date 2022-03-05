package com.example.qrgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScanActivity extends AppCompatActivity implements BottomSheet.sheetListener{

    CodeScanner codeScanner;
    BottomSheet sheet;
    TextView qrText;
    static String QR_RESULT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        qrText = findViewById(R.id.qr_text);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this,scannerView);
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.dismiss();
                codeScanner.startPreview();
            }
        });

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        qrText.setText(result.getText());
                        showBottomSheet();
                        QR_RESULT = result.getText();
//                        Toast.makeText(ScanActivity.this, result.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();

    }

    private void showBottomSheet(){
        sheet = new BottomSheet();
        sheet.show(getSupportFragmentManager(),"Sheet");
    }


    @Override
    public void onBtnClick(String text) {

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label",text);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(ScanActivity.this, "Copied", Toast.LENGTH_SHORT).show();



    }
}