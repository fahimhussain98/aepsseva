package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wts.aepssevaa.R;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class ShareDmtReportActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button btnShare;
    File imagePath;
    int FILE_PERMISSION = 45;
    ImageView imgClose;
    Bitmap bitmap;
    TextView tvShopDetails;
    TextView tvAmount, tvAccountNumber, tvBeniname, ttvBankName, tvIfsc, tvDateTime, tvTransactionId, tvStatus;
    String amount, accountNumber, beniName, bankName, ifsc, dateTime, transactionId, status;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_dmt_report);

        inhitViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareDmtReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileNo", null);
        String role = sharedPreferences.getString("usertype", null);
        tvShopDetails.setText("Name " + ownerName + "(" + role + ")" + "\n" + "Contact No. " + mobile);

        amount = getIntent().getStringExtra("amount");
        accountNumber = getIntent().getStringExtra("accountNumber");
        beniName = getIntent().getStringExtra("beniName");
        bankName = getIntent().getStringExtra("bank");
        ifsc = getIntent().getStringExtra("ifsc");
        dateTime = getIntent().getStringExtra("date");
        transactionId = getIntent().getStringExtra("transactionId");
        status = getIntent().getStringExtra("status");

        tvAmount.setText(amount);
        tvAccountNumber.setText(accountNumber);
        tvBeniname.setText(beniName);
        ttvBankName.setText(bankName);
        tvIfsc.setText(ifsc);
        tvDateTime.setText(dateTime);
        tvTransactionId.setText(transactionId);
        tvStatus.setText(status);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

            }
        });
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareDmtReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            btnShare.setVisibility(View.GONE);
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.share_report_layout);
            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            btnShare.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnShare.setVisibility(View.VISIBLE);
        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareDmtReportActivity.this);
                permissionDialog.setTitle("Permission Required");
                permissionDialog.setMessage("You can set permission manually." + "\n" + "Settings-> App Permission -> Allow Storage permission.");
                permissionDialog.setCancelable(false);
                permissionDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                permissionDialog.show();

            }
        }
    }

    private void inhitViews() {

        tvShopDetails = findViewById(R.id.tv_shop_details);
        btnShare = findViewById(R.id.btn_share);
        imgClose = findViewById(R.id.img_close);
        tvAmount = findViewById(R.id.tv_amount);
        tvAccountNumber = findViewById(R.id.tv_account_number);
        tvBeniname = findViewById(R.id.tv_beni_name);
        ttvBankName = findViewById(R.id.tv_bank_name);
        tvIfsc = findViewById(R.id.tv_ifsc);
        tvDateTime = findViewById(R.id.tv_all_report_date_time);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvStatus = findViewById(R.id.tv_all_report_status);
    }
}