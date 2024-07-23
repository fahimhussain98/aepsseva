package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ElectricityActivity extends AppCompatActivity {
    EditText etConsumerNumber, etAccountNumber;
    TextView tvOperator;
    Button btnProceed, btnCancel;

    ImageView imgBack;
    TextView tvTitle;

    SharedPreferences sharedPreferences;
    Button btnPay;
    TextView tvCostumerName1, tvBillNumber1, tvBillAmount1, tvBilldate, tvBillPeriod, tvBillDueDate, tvWalletBalance;
    ConstraintLayout billPaymentLayout;
    String userid;
    String selectedOperatorId = "select";
    String operatorName, operatorId;
    ArrayList<String> operatorNameList, operatorIdList, idList;
    String billNumber, dueDate, customerName, amount, requestId, billerInfo, inputInfo;
    SpinnerDialog operatorDialog;
    String serviceType, serviceId;
    String deviceId, deviceInfo;
    String mobileNumber;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);

        inhitViews();
        serviceType = getIntent().getStringExtra("service");
        serviceId = getIntent().getStringExtra("serviceId");
        tvTitle.setText(serviceType);

        if (serviceType.equalsIgnoreCase("Electricity")) {
            etConsumerNumber.setHint("Customer Number");
        } else if (serviceType.equalsIgnoreCase("Gas")) {
            etConsumerNumber.setHint("Subscriber Id");
        } else if (serviceType.equalsIgnoreCase("Broadband")) {
            etConsumerNumber.setHint("Customer no.");
        } else if (serviceType.equalsIgnoreCase("Water")) {
            etConsumerNumber.setHint("Connection no.");
        } else if (serviceType.equalsIgnoreCase("LANDLINE")) {
            etConsumerNumber.setHint("Account no.");
        }
        else if (serviceType.equalsIgnoreCase("PostPaid")){
            etConsumerNumber.setHint("Customer Number");

        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricityActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNumber = sharedPreferences.getString("mobileNo", null);


        imgBack.setOnClickListener(v ->
        {
            finish();
        });

        getOperators();

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                        //showMpinDialog();
                        fetchBill();
                        //showTpinDialog();
                    } else {
                        showSnackbar("Above fields are mandatory.");
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetState()) {
                    //payBill();
                    final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_dialog_layout, null, false);
                    final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                    builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    builder.setCancelable(false);
                    builder.setView(view1);

                    TextView tvCostumerName = view1.findViewById(R.id.tv_customer_name);
                    final TextView tvBillDate = view1.findViewById(R.id.tv_bill_date);
                    TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                    final TextView tvDueDate = view1.findViewById(R.id.tv_due_date);
                    TextView tvCaNumber = view1.findViewById(R.id.tv_tel);
                    Button btnProceedToPay = view1.findViewById(R.id.btn_proceed_to_pay);
                    Button btnCancel = view1.findViewById(R.id.btn_cancel);

                    tvCostumerName.setText(customerName);
                    tvBillDate.setText(dueDate);
                    tvBillAmount.setText("₹ " + amount);
                    tvDueDate.setText(dueDate);
                    tvCaNumber.setText(billNumber);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            billPaymentLayout.setVisibility(View.GONE);

                            builder.dismiss();
                        }
                    });

                    btnProceedToPay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payBill();
                            builder.dismiss();

                        }
                    });
                    builder.show();
                } else {
                    showSnackbar("No Internet");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showTpinDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ElectricityActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        final EditText etTpin = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etTpin.getText())) {
                    String tpin = etTpin.getText().toString().trim();
                    checkTpin(tpin);
                    addSenderDialog.dismiss();
                } else {
                    etTpin.setError("Required");
                }
            }
        });

    }

    private void checkTpin(String tpin) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();


        Call<JsonObject> call = ApiController.getInstance().getApi().checkMpinOrTPIN(ApiController.Auth_key, userid, deviceId, deviceInfo, "tpin", tpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            fetchBill();
                        } else {
                            pDialog.dismiss();
                            String transaction = responseObject.getString("status");
                            showSnackbar(transaction);

                        }


                    } catch (Exception e) {
                        pDialog.dismiss();
                        showSnackbar("Something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackbar("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("Something went wrong");
            }
        });
    }

    private void fetchBill() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();

        Call<JsonObject> call = ApiController.getInstance().getApi().fetchBill(ApiController.Auth_key, userid, deviceId, deviceInfo, serviceType, serviceId, selectedOperatorId,
                consumerNumber, mobileNumber, "NA");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONObject dataObject = responseObject.getJSONObject("data");

                            amount = dataObject.getString("billAmount");
                            dueDate = dataObject.getString("dueDate");
                            customerName = dataObject.getString("consumerName");
                            billNumber = dataObject.getString("billNo");
                            requestId = dataObject.getString("requestid");
                            billerInfo = dataObject.getString("billerinfo");
                            inputInfo = dataObject.getString("inputinfo");


                            pDialog.dismiss();

                            billPaymentLayout.setVisibility(View.VISIBLE);
                            tvBillAmount1.setText("₹ " + amount);
                            tvBillNumber1.setText("Bill Number. " + billNumber);
                            tvCostumerName1.setText("Name:- " + customerName);
                            //tvBillPeriod.setText("Bill Period:- " + billPeriod);
                            tvBillDueDate.setText("Due Date:- " + dueDate);
                            btnPay.setText("PAY ₹" + amount);

                           /* final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_dialog_layout, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvCostumerName = view1.findViewById(R.id.tv_customer_name);
                            final TextView tvBillDate = view1.findViewById(R.id.tv_bill_date);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            final TextView tvDueDate = view1.findViewById(R.id.tv_due_date);
                            TextView tvCaNumber = view1.findViewById(R.id.tv_tel);
                            Button btnProceedToPay = view1.findViewById(R.id.btn_proceed_to_pay);
                            Button btnCancel = view1.findViewById(R.id.btn_cancel);

                            tvCostumerName.setText(customerName);
                            tvBillDate.setText(dueDate);
                            tvBillAmount.setText("₹ " + amount);
                            tvDueDate.setText(dueDate);
                            tvCaNumber.setText(billNumber);

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);

                                    builder.dismiss();
                                }
                            });

                            btnProceedToPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.VISIBLE);
                                    tvBillAmount1.setText("₹ " + amount);
                                    tvBillNumber1.setText("Bill Number. " + billNumber);
                                    tvCostumerName1.setText("Name:- " + customerName);
                                    //tvBillPeriod.setText("Bill Period:- " + billPeriod);
                                    tvBillDueDate.setText("Due Date:- " + dueDate);
                                    btnPay.setText("PAY ₹" + amount);
                                    builder.dismiss();

                                }
                            });
                            builder.show();*/

                        } else {
                            pDialog.dismiss();
                            billPaymentLayout.setVisibility(View.GONE);
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            finish();

                                        }
                                    }).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        billPaymentLayout.setVisibility(View.GONE);
                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                .setTitle("Message")
                                .setMessage("Something went wrong\nPlease Try After Sometime")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    billPaymentLayout.setVisibility(View.GONE);
                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                            .setTitle("Message")
                            .setMessage("Something went wrong\nPlease Try After Sometime")
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
                billPaymentLayout.setVisibility(View.GONE);
                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                        .setTitle("Message")
                        .setMessage("Something went wrong\nPlease Try After Sometime")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void payBill() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();

        /*Call<JsonObject> call = RetrofitClient.getInstance().getApi().payElectricityBill(userid, consumerNumber, mobileNumber, customerName, amount, billNumber, inputPar1,
                "103.82.190.195", "Electricity", macAddress);*/

        Call<JsonObject> call = ApiController.getInstance().getApi().payBill(ApiController.Auth_key, userid, deviceId, deviceInfo, serviceType, selectedOperatorId,
                consumerNumber, mobileNumber, customerName, amount, dueDate, serviceId, billerInfo, inputInfo, requestId);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN") || responseCode.equalsIgnoreCase("ERR")) {
/*
                                JSONObject dataObject = responseObject.getJSONObject("data");

                                custId = transactionObject.getString("custid");
                                tokenKey = transactionObject.getString("TOKENKEY");
                                sType = transactionObject.getString("Stype");
                                number = transactionObject.getString("number");
                                amount = transactionObject.getString("amount");
                                comm = transactionObject.getString("comm");
                                surcharge = transactionObject.getString("surcharge");
                                cost = transactionObject.getString("cost");
                                balance = transactionObject.getString("balance");
                                tDateTime = transactionObject.getString("tdatetime");
                                accountNo = transactionObject.getString("accountno");
                                status = transactionObject.getString("status");
                                transactionId = transactionObject.getString("transactionid");
                                brId = transactionObject.getString("brid");
                                opName = transactionObject.getString("opname");*/
                            String status = responseObject.getString("status");

                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(billNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);
                                    builder.dismiss();
                                    finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this)
                                    .setTitle("Message!!!")
                                    .setCancelable(false)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this)
                                .setTitle("Message!!!")
                                .setCancelable(false)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this)
                            .setTitle("Message!!!")
                            .setCancelable(false)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this)
                        .setTitle("Message!!!")
                        .setCancelable(false)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private void getOperators() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = ApiController.getInstance().getApi().getOperators(ApiController.Auth_key, deviceId, deviceInfo, userid, serviceId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJsonObject = new JSONObject(String.valueOf(response.body()));

                        operatorIdList = new ArrayList<>();
                        operatorNameList = new ArrayList<>();
                        idList = new ArrayList<>();

                        JSONArray dataArray = responseJsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);

                            operatorName = dataObject.getString("OperatorName");
                            operatorNameList.add(operatorName);
                            operatorId = dataObject.getString("ID");
                            operatorIdList.add(operatorId);

                        }

                        tvOperator.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                operatorDialog = new SpinnerDialog(ElectricityActivity.this, operatorNameList, "Select Operator", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                operatorDialog.setCancellable(true); // for cancellable
                                operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String item, int position) {
                                        tvOperator.setText(item);
                                        selectedOperatorId = operatorIdList.get(position);

                                    }
                                });

                                operatorDialog.showSpinerDialog();
                            }
                        });


                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();

                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                .setTitle("Message")
                                .setMessage("Please try after sometime")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();

                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                            .setTitle("Message")
                            .setMessage("Please try after sometime")
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

                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                        .setTitle("Alert")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void inhitViews() {
        billPaymentLayout = findViewById(R.id.bill_payment_layout);

        tvCostumerName1 = findViewById(R.id.tv_customer_name1);
        tvBillNumber1 = findViewById(R.id.tv_bill_number1);
        tvBillAmount1 = findViewById(R.id.tv_bill_amount1);
        btnPay = findViewById(R.id.btn_pay);
        etConsumerNumber = findViewById(R.id.et_consumer_number);
        etAccountNumber = findViewById(R.id.et_account_number);
        tvOperator = findViewById(R.id.tv_operator_name);
        btnProceed = findViewById(R.id.btn_proceed);
        btnCancel = findViewById(R.id.btn_cancel);
        tvTitle = findViewById(R.id.activity_title);
        tvBilldate = findViewById(R.id.tv_bill_date);
        tvBillPeriod = findViewById(R.id.tv_bill_period);
        tvBillDueDate = findViewById(R.id.tv_bill_due_date);
        tvWalletBalance = findViewById(R.id.tv_balance);
        imgBack = findViewById(R.id.back_button);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.electricity_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private boolean checkInputs() {
        return !TextUtils.isEmpty(etConsumerNumber.getText()) &&
                !selectedOperatorId.equalsIgnoreCase("select");
    }
}