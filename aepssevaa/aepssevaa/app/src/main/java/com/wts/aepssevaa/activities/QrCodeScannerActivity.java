package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wts.aepssevaa.R;


public class QrCodeScannerActivity extends AppCompatActivity {

    AppCompatButton scanbtn;
    public static TextView scantext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        scantext= findViewById(R.id.scantext);
        scanbtn=  findViewById(R.id.scanbtn);

        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),scannerViewshuaib.class));
            }
        });


    }
}