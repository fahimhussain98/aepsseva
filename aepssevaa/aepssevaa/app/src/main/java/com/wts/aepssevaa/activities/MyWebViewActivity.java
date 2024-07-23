package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.wts.aepssevaa.R;


public class MyWebViewActivity extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);
        url = getIntent().getStringExtra("url");

        webView = findViewById(R.id.webView);

        progressDialog = new ProgressDialog(MyWebViewActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Connect to internet");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
        webView.loadUrl(url);
    }

    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
