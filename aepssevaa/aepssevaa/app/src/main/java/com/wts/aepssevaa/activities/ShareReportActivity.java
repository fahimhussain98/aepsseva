package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShareReportActivity extends AppCompatActivity {
    String txnId, liveId,uniqueId, number, amount, commission, cost, balance, date,time, status, operator, imgUrl;
    SharedPreferences sharedPreferences;
    TextView tvTxnId, tvLiveId, tvNumber, tvAmount, tvCommission, tvCost, tvBalance, tvDateTime, tvStatus, tvOperator, tvShopDetails,tvTxnStatus,tvTime;
    AppCompatButton btnShare, btnComplain;
    int FILE_PERMISSION = 45;
    ImageView imgClose;

    String userId,deviceId,deviceInfo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_report);

        inhitViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareReportActivity.this);

        userId = sharedPreferences.getString("userid", null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileNo", null);
        String role = sharedPreferences.getString("usertype", null);

        txnId = getIntent().getStringExtra("txnId");
        liveId = getIntent().getStringExtra("liveId");
        uniqueId = getIntent().getStringExtra("uniqueId");
        number = getIntent().getStringExtra("number");
        amount = getIntent().getStringExtra("amount");
        commission = getIntent().getStringExtra("commission");
        cost = getIntent().getStringExtra("cost");
        balance = getIntent().getStringExtra("balance");
        date = getIntent().getStringExtra("date");
        status = getIntent().getStringExtra("status");
        operator = getIntent().getStringExtra("operator");
        imgUrl = getIntent().getStringExtra("imgUrl");
        time = getIntent().getStringExtra("time");

        tvTxnId.setText(txnId);
        tvLiveId.setText(liveId);
        tvNumber.setText(number);
        tvAmount.setText(amount);
        tvCommission.setText(commission);
        tvCost.setText(cost);
        tvBalance.setText(balance);
        tvDateTime.setText(date);
        tvStatus.setText(status);
        tvTxnStatus.setText(status);
        tvOperator.setText(operator);
        tvTime.setText(time);

        if(!(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful")))
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back2));
        }

        tvShopDetails.setText("Name  :  " + ownerName + "(" + role + ")" + "\n" + "Contact No.  :  " + mobile);

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

        btnComplain.setOnClickListener(view -> {
            final View complaintView = LayoutInflater.from(ShareReportActivity.this).inflate(R.layout.complaint_layout, null, false);
            final AlertDialog builderMakeComplaint = new AlertDialog.Builder(ShareReportActivity.this).create();
            builderMakeComplaint.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builderMakeComplaint.setCancelable(false);
            builderMakeComplaint.setView(complaintView);
            builderMakeComplaint.show();

            final EditText etRemarks = complaintView.findViewById(R.id.et_remarks);
            Button btnCancel = complaintView.findViewById(R.id.btn_cancel);
            Button btnSubmit = complaintView.findViewById(R.id.btn_submit);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builderMakeComplaint.dismiss();
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etRemarks.getText())) {
                        String remarks = etRemarks.getText().toString().trim();
                        makeComplaint(remarks, uniqueId, liveId);
                        builderMakeComplaint.dismiss();
                    } else {
                        Toast.makeText(ShareReportActivity.this, "Remarks!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }

    private void makeComplaint(String remarks, String uniqueId, String liveID) {

        final ProgressDialog pDialog = new ProgressDialog(ShareReportActivity.this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().makeComplaint(ApiController.Auth_key, userId,deviceId, deviceInfo, uniqueId, remarks, "NA", liveID,"Recharge");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String transaction = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(ShareReportActivity.this).
                                    setMessage(transaction)
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String transaction = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(ShareReportActivity.this).
                                    setMessage(transaction)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ShareReportActivity.this).
                                    setMessage("Something went wrong!!!")
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ShareReportActivity.this).
                                setMessage("Something went wrong!!!")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ShareReportActivity.this).
                            setMessage("Something went wrong!!!")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ShareReportActivity.this).
                        setMessage("Something went wrong!!!")
                        .show();
            }
        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
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
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title"+System.currentTimeMillis(), null);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareReportActivity.this);
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
        tvTxnId = findViewById(R.id.tv_all_report_txnid);
        tvLiveId = findViewById(R.id.tv_all_report_liveId);
        tvNumber = findViewById(R.id.tv_all_report_number);
        tvAmount = findViewById(R.id.tv_all_report_amount);
        tvCommission = findViewById(R.id.tv_all_report_commission);
        tvCost = findViewById(R.id.tv_all_report_cost);
        tvBalance = findViewById(R.id.tv_all_report_balance);
        tvDateTime = findViewById(R.id.tv_all_report_date_time);
        tvTime = findViewById(R.id.tv_time);
        tvStatus = findViewById(R.id.tv_status);
        tvOperator = findViewById(R.id.tv_all_report_operator_name);
        tvShopDetails = findViewById(R.id.tv_shop_details);
        btnShare = findViewById(R.id.btn_share);
        btnComplain = findViewById(R.id.btn_raiseComplain);
        imgClose = findViewById(R.id.img_close);
        tvTxnStatus = findViewById(R.id.tv_txn_status);
    }

}