package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FundTransferActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1;
    private static final int FILE_PERMISSION = 2;
    String title;
    TextView activityTitle;
    ImageView backButton;

    String[] paymentModeArr = {"BY IMPS", "BY NEFT", "BY RTGS", "BY WALLET", "BY CASH IN BANK", "UPI PAYMENT", "BY ATM TRANSFER"};
    String[] requestToArr = {"PARENT", "ADMIN"};
    Spinner spinner, spinnerBank, spinnerRequestTo;
    String selectedPaymentMode = "Select Payment Mode", selectedBankName = "select", selectRequestTo = "select";

    EditText etAmount, etRefrenceNumber, etRemarks;
    TextView tvDate, tvUploadReceipt;
    Button btnSubmit;
    SharedPreferences sharedPreferences;
    String userId;
    ArrayList<String> bankList;
    String amount, comment, bankRefNo, date = "select";
    ArrayList<String> photoPaths;
    private String fileUrl = "NA";
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transfer);

        inhitViews();

        //////////////////////////////////////////////////////////////////Toolbar
        title = getIntent().getStringExtra("title");
        activityTitle.setText(title);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////Toolbar
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FundTransferActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        getBankList();

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinner,
                new HintAdapter<String>(FundTransferActivity.this, "Select Payment Mode", Arrays.asList(paymentModeArr)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPaymentMode = paymentModeArr[position];
                    }
                });
        hintSpinner.init();

        HintSpinner<String> hintSpinnerRequestTo = new HintSpinner<>(
                spinnerRequestTo,
                new HintAdapter<String>(FundTransferActivity.this, "Request To", Arrays.asList(requestToArr)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectRequestTo = requestToArr[position];
                    }
                });
        hintSpinnerRequestTo.init();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        /*String date = day + "-" + (month + 1) + "-" + year + "";
        tvDate.setText(date);*/


        tvDate.setOnClickListener(v ->
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            DatePickerDialog datePickerDialog = new DatePickerDialog(FundTransferActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar date1 = Calendar.getInstance();
                    date1.set(year, month, dayOfMonth);

                    tvDate.setText(simpleDateFormat.format(date1.getTime()));
                    date = webServiceDateFormat.format(date1.getTime());

                }
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        btnSubmit.setOnClickListener(v -> {
            if (checkInternetState()) {
                if (checkInputs()) {
                    amount = etAmount.getText().toString().trim();
                    comment = etRemarks.getText().toString().trim();
                    bankRefNo = etRefrenceNumber.getText().toString().trim();
                    doPaymentRequest(userId, amount, comment, bankRefNo, selectedPaymentMode);
                }
            } else {
                showSnackbar("No Internet");
            }
        });

        tvUploadReceipt.setOnClickListener(v ->
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {

        if (ContextCompat.checkSelfPermission(FundTransferActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(FundTransferActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(FundTransferActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            chooseImage();
        }
    }

        public void chooseImage() {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, 1);
        }



    private void getBankList() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getBankList(ApiController.Auth_key, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            bankList = new ArrayList<>();
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String bankName = transactionObject.getString("BankName");

                                bankList.add(bankName);

                            }

                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    spinnerBank,
                                    new HintAdapter<String>(FundTransferActivity.this, "Select Bank", bankList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankList.get(position);
                                        }
                                    });
                            hintSpinner.init();

                            pDialog.dismiss();
                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            new AlertDialog.Builder(FundTransferActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(FundTransferActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(FundTransferActivity.this)
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(FundTransferActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(FundTransferActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void doPaymentRequest(String userid, String amount, String comment, String bankRefNo, String selectedPaymentMode) {


        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().doPaymentRequest(ApiController.Auth_key,userId,deviceId,deviceInfo,date,bankRefNo,selectedBankName,
                selectedPaymentMode,comment,fileUrl,selectRequestTo,amount);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));


                        String statuscode = jsonObject1.getString("statuscode");
                        String responseMessage = jsonObject1.getString("data");
                        String transactions = jsonObject1.getString("status");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            final View view = LayoutInflater.from(FundTransferActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                            final AlertDialog rechargeStatusDialog = new AlertDialog.Builder(FundTransferActivity.this).create();
                            rechargeStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            rechargeStatusDialog.setCancelable(false);
                            rechargeStatusDialog.setView(view);
                            rechargeStatusDialog.show();

                            ImageView imgRechargeDialogIcon = rechargeStatusDialog.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogTitle = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialog_title);
                            TextView tvRechargeDialogStatus = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = rechargeStatusDialog.findViewById(R.id.btn_recharge_dialog);


                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);
                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);

                            imgRechargeDialogIcon.setImageResource(R.drawable.success);
                            tvRechargeDialogStatus.setText("Status :" + responseMessage);
                            tvRechargeDialogTitle.setText("Status");

                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rechargeStatusDialog.dismiss();
                                    finish();
                                }
                            });
                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String status = jsonObject1.getString("transactions");
                            final Dialog rechargeStatusDialog = new Dialog(FundTransferActivity.this);
                            rechargeStatusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            rechargeStatusDialog.setCancelable(false);
                            rechargeStatusDialog.setContentView(R.layout.recharge_status_layout);
                            rechargeStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            rechargeStatusDialog.show();

                            ImageView imgRechargeDialogIcon = rechargeStatusDialog.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogStatus = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = rechargeStatusDialog.findViewById(R.id.btn_recharge_dialog);

                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                            tvRechargeDialogStatus.setTextColor(Color.RED);
                            tvRechargeDialogStatus.setText("Status : " + status);
                            imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rechargeStatusDialog.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(FundTransferActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(FundTransferActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(FundTransferActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(FundTransferActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private boolean checkInputs() {

        if (!selectedPaymentMode.equalsIgnoreCase("Select Payment Mode")) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!TextUtils.isEmpty(etRefrenceNumber.getText())) {
                    if (!TextUtils.isEmpty(etRemarks.getText())) {
                        if (!date.equalsIgnoreCase("select")) {
                            if (!selectedBankName.equalsIgnoreCase("select")) {
                                if (!selectRequestTo.equalsIgnoreCase("select")) {

                                    return true;
                                } else {
                                    showSnackbar("select Request To");
                                    return false;
                                }
                            } else {
                                showSnackbar("select bank");
                                return false;
                            }
                        } else {
                            showSnackbar("select date");
                            return false;
                        }

                    } else {
                        etRemarks.setError("Enter Remarks");
                        return false;
                    }
                } else {
                    etRefrenceNumber.setError("Enter Reference Number");
                    return false;
                }
            } else {
                etAmount.setError("Enter Amount");
                return false;
            }
        } else {
            new AlertDialog.Builder(FundTransferActivity.this).setMessage("Select Payment Mode")
                    .setPositiveButton("OK", null).show();
            return false;
        }

    }

    private void inhitViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);
        etAmount = findViewById(R.id.et_amount);
        etRefrenceNumber = findViewById(R.id.et_reference_number);
        etRemarks = findViewById(R.id.et_remarks);
        tvDate = findViewById(R.id.tv_date);
        tvUploadReceipt = findViewById(R.id.tv_upload_receipt);
        spinner = findViewById(R.id.spinner);
        spinnerBank = findViewById(R.id.spinner_bank);
        spinnerRequestTo = findViewById(R.id.spinner_request_to);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fund_transfer_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void serverUpload(File myfile) {


        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (myfile.toString().contains(".pdf")) {
            reqFile = RequestBody.create(MediaType.parse("application/pdf"), myfile);
        } else if (myfile.toString().contains(".jpg") || myfile.toString().contains(".jpeg") || myfile.toString().contains(".png")) {
            reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        } else {
            pDialog.dismiss();
            Toast.makeText(this, "Please select only image file.", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);


        Call<JsonObject> call = ApiController.getInstance().getApi().uploadfile(ApiController.Auth_key,body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("statuscode");

                        if (code.equalsIgnoreCase("TXN")) {
                            tvUploadReceipt.setText("File Uploaded");
                            fileUrl = jsonObject.getString("data");
                            pDialog.dismiss();

                        } else if (code.equalsIgnoreCase("ERR")) {
                            showSnackbar("Try again.");
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            showSnackbar("Try again.");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackbar("Try again.");
                    }

                } else {
                    pDialog.dismiss();
                    showSnackbar("Try again.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("Try again.");
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (1 == requestCode && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mediaPath = cursor.getString(columnIndex);
                File file = new File(mediaPath);
                serverUpload(file);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}