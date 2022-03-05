package com.example.qrgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_TAG";
    EditText editText;
    Button btn;
    ImageView img;
    Bitmap bitmap;
    Uri bitmapUri = null;
    Button gotoScanBtn;
    private static final int CAMERA_CODE = 100;
    boolean cameraPermission = false;

    Slider slider;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        btn = findViewById(R.id.button);
        img = findViewById(R.id.imageView);

        askPermission();
        
        slider = findViewById(R.id.slider);
        
        gotoScanBtn = findViewById(R.id.button2);
        gotoScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });

        img.setVisibility(View.GONE);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();

                // initialize multiformat writter
                MultiFormatWriter writer = new MultiFormatWriter();

                // initialize bit matrix
                try {
                    BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 250, 250);

                    // initilize barcode encoder
                    BarcodeEncoder encoder = new BarcodeEncoder();

                    // initilize bitmap
                    bitmap = encoder.createBitmap(matrix);

                    // set Bitmap to imageview
                    img.setVisibility(View.VISIBLE);
                    img.setImageBitmap(bitmap);



                    InputMethodManager manager = null;
                    // to hide soft keyboard
                    // initilize input manager
                    manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // hide soft keyboard

                    manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);


                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });


        registerForContextMenu(img);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // you can set menu header with title icon etc
        menu.setHeaderTitle("Choose ");
        menu.setHeaderIcon(R.drawable.ic_launcher_foreground);
        // add menu items

        menu.add(0,v.getId(),0,"Share").setIcon(R.drawable.ic_baseline_share_24);
        menu.add(0,v.getId(),0,"Reset").setIcon(R.drawable.ic_baseline_layers_clear_24);
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveImageExternal(Bitmap image) {
        //TODO - Should be processed in another thread
//        Uri uri = null;
//        try {
//            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
//            FileOutputStream stream = new FileOutputStream(file);
//            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            stream.close();
//            bitmapUri = Uri.fromFile(file);

        int quality = (int) slider.getValue();

            //TODO - Should be processed in another thread
            File imagesFolder = new File(getCacheDir(), "images");
//            Uri uri = null;
            try {
                imagesFolder.mkdirs();
                File file = new File(imagesFolder, "shared_image.png");

                FileOutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, quality, stream);
                stream.flush();
                stream.close();
                bitmapUri = FileProvider.getUriForFile(this, "com.example.qrgenerator", file);

            } catch (IOException e) {
                Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent,"share via"));


//        } catch (IOException e) {
//            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
//        }
    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() == "Share") {

            // check storage accesibility
            if (!isExternalStorageWritable()){
                return true;
            }

            Toast.makeText(MainActivity.this, "Sharing...", Toast.LENGTH_SHORT).show();

//             bitmapUri = saveImageExternal(bitmap);
            saveImageExternal(bitmap);
            
        }else if(item.getTitle() == "Reset"){
//            img.setImageDrawable(null);
            img.setVisibility(View.GONE);
            editText.setText(null);
            Toast.makeText(MainActivity.this, "nic2e", Toast.LENGTH_SHORT).show();

        }

        return true;
    }


    private void askPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            // request for permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA_CODE);

        }
//        else{
//            gotoScanBtn.setEnabled(true);
//            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                cameraPermission = true;
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            } else {
                gotoScanBtn.setEnabled(false);
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }
}