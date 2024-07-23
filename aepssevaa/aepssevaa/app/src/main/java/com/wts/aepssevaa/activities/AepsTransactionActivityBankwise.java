package com.wts.aepssevaa.activities;

import static com.wts.aepssevaa.activities.PaySprintActivityBank2.selectedTransactionType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wts.aepssevaa.R;

import java.io.ByteArrayOutputStream;

public class AepsTransactionActivityBankwise extends AppCompatActivity {

    String transactionType, bankName, responseMobileNumber, responseAadharNumber, responseBankRRN, transactionId, status, responseAmount, outputDate,
            outputTime, accountBalance, message;

    TextView tvTransactionType, tvBankName, tvMobileNumber, tvAadharNumber, tvBankRRN, tvTransactionId, tvStatus, tvAmount, tvDate, tvTime, tvAccountBalance,
            tvMessage;

    AppCompatButton btnDone;
    ImageView imgShare, imgAeps, imgClose;
    Button btnRetry;
    int FILE_PERMISSION = 45;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_transaction_bankwise);

        initViews();

        transactionType = getIntent().getStringExtra("transactionType");
        bankName = getIntent().getStringExtra("bankName");
        responseMobileNumber = getIntent().getStringExtra("responseMobileNumber");
        responseAadharNumber = getIntent().getStringExtra("responseAadharNumber");
        responseBankRRN = getIntent().getStringExtra("responseBankRRN");
        transactionId = getIntent().getStringExtra("transactionId");
        status = getIntent().getStringExtra("status");
        responseAmount = getIntent().getStringExtra("responseAmount");
        outputDate = getIntent().getStringExtra("outputDate");
        outputTime = getIntent().getStringExtra("outputTime");
        accountBalance = getIntent().getStringExtra("accountBalance");
        message = getIntent().getStringExtra("message");

        tvTransactionType.setText(transactionType);
        tvBankName.setText(bankName);
        tvMobileNumber.setText(responseMobileNumber);
        tvAadharNumber.setText(responseAadharNumber);
        tvBankRRN.setText(responseBankRRN);
        tvTransactionId.setText(transactionId);
        tvStatus.setText(status);
        tvAmount.setText("₹ " + responseAmount);
        tvDate.setText(outputDate);
        tvTime.setText(outputTime);
        tvAccountBalance.setText("₹ " + accountBalance);
        tvMessage.setText(message);


        btnDone.setOnClickListener(view -> {
//            finish();
//            activity.finish();

            startActivity(new Intent(AepsTransactionActivityBankwise.this , HomeDashboardActivity.class));

        });
        imgClose.setOnClickListener(view -> {
//            finish();
//            activity.finish();

            startActivity(new Intent(AepsTransactionActivityBankwise.this , HomeDashboardActivity.class));

        });

        btnRetry.setOnClickListener(view->{
            // finish();

            if(selectedTransactionType.equalsIgnoreCase("cw")){


                Toast.makeText(this, "Merchant 2FA Require", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AepsTransactionActivityBankwise.this, TwoFactorAuthenticationCWActivity.class);
                intent.putExtra("title","TwoFactorAuth");
                intent.putExtra("appId","");

                startActivity(intent);
                finish();

            }
            else {
                finish();
            }
        });

        imgShare.setOnClickListener(view -> {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

        });
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(AepsTransactionActivityBankwise.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(AepsTransactionActivityBankwise.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(AepsTransactionActivityBankwise.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            imgShare.setVisibility(View.GONE);
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ConstraintLayout shareReportLayout = findViewById(R.id.share_report_layout);
            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            imgShare.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgShare.setVisibility(View.VISIBLE);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(AepsTransactionActivityBankwise.this);
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

    private void initViews() {
        tvTransactionType = findViewById(R.id.tv_transaction_type);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvAadharNumber = findViewById(R.id.tv_aadhar_no);
        tvBankRRN = findViewById(R.id.tv_rrn);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvStatus = findViewById(R.id.tv_status);
        tvAmount = findViewById(R.id.tv_amount);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvAccountBalance = findViewById(R.id.tv_account_balance);
        tvMessage = findViewById(R.id.tv_message);

        btnDone = findViewById(R.id.btn_done);

        imgShare = findViewById(R.id.img_share);
        btnRetry = findViewById(R.id.btnRetry);
        imgAeps = findViewById(R.id.img_aeps);
        imgClose = findViewById(R.id.imgClose);
    }
}