package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettlementActivity extends AppCompatActivity {

    Spinner spinnerPaymentType, spinnerAccountType, spinnerTransactionType, spinnerBankName;
    ArrayList<String> paymentTypeList, accountTypeList, transactionTypeList, bankNameList, accountHolderNameList, ifscCodeList, accountNumberList;
    String selectedPaymentType = "select", selectedTransactionType = "select", selectedAccountType = "select", userId, userName, selectedBankName = "select";
    ConstraintLayout bankDetailsContainer;
    boolean isWallet = true;
    ImageView imgBack;
    EditText etAmount, etAccountHolderName, etAccountNumber, etIfscNumber;
    Button btnSubmit, btnAddMoreBanks;
    SharedPreferences sharedPreferences;
    String amount, selectedAccountHolderName, bankName, selectedAccountNumber, selectedIfscNumber, panCard, mobileNo;
    SimpleDateFormat webServiceDateFormat;
    String currentDate;
    String deviceId, deviceInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);

        inhitViews();
        setViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettlementActivity.this);
        userId = sharedPreferences.getString("userid", null);
        panCard = sharedPreferences.getString("panCard", null);
        mobileNo = sharedPreferences.getString("mobileNo", null);
        userName = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        currentDate = webServiceDateFormat.format(newDate1.getTime());


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                      //  showTpinDialog(); // comment by shuaib
                        doSettlement();
                    } else {
                        showSnackbar("Select Above Details");
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
        });

        btnAddMoreBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(SettlementActivity.this, AddBankDetailActivity.class));
            }
        });

    }

    private void getAccountDetails() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getAccountDetails(ApiController.Auth_key, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            bankNameList = new ArrayList<>();
                            accountHolderNameList = new ArrayList<>();
                            accountNumberList = new ArrayList<>();
                            ifscCodeList = new ArrayList<>();

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String bankName = dataObject.getString("BankName");
                                String accountHolderName = dataObject.getString("AccountHolderName");
                                String accountNo = dataObject.getString("AccountNo");
                                String ifscCode = dataObject.getString("IfscCode");

                                bankNameList.add(bankName);
                                accountHolderNameList.add(accountHolderName);
                                accountNumberList.add(accountNo);
                                ifscCodeList.add(ifscCode);

                            }

                            if (bankNameList.size() < 3) {
                                btnAddMoreBanks.setVisibility(View.VISIBLE);
                            }

                            HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerBankName, new HintAdapter<String>(SettlementActivity.this, "Bank Name", bankNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankNameList.get(position);
                                            selectedAccountHolderName = accountHolderNameList.get(position);
                                            selectedAccountNumber = accountNumberList.get(position);
                                            selectedIfscNumber = ifscCodeList.get(position);

                                            etAccountHolderName.setText(selectedAccountHolderName);
                                            etAccountNumber.setText(selectedAccountNumber);
                                            etIfscNumber.setText(selectedIfscNumber);

                                            etAccountHolderName.setEnabled(false);
                                            etAccountNumber.setEnabled(false);
                                            etIfscNumber.setEnabled(false);
                                        }
                                    });
                            hintSpinner1.init();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                    .setMessage("Please add Bank Details first.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(SettlementActivity.this, AddBankDetailActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void doSettlement() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        if (selectedTransactionType.equalsIgnoreCase("IMPS"))
            selectedTransactionType = "IFS";
        else if (selectedTransactionType.equalsIgnoreCase("RTGS"))
            selectedTransactionType = "RGS";
        else if (selectedTransactionType.equalsIgnoreCase("NEFT"))
            selectedTransactionType = "NEFT";

        if (selectedPaymentType.equalsIgnoreCase("WALLET"))
            selectedPaymentType = "2";
        else
            selectedPaymentType = "1";


        Call<JsonObject> call = ApiController.getInstance().getApiSecond().moveToBank(ApiController.Auth_key, userId, deviceId, deviceInfo, selectedPaymentType, amount, selectedAccountNumber,
                selectedIfscNumber, selectedAccountType, selectedTransactionType, selectedAccountHolderName,"22");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN") || responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();


                            String status = responseObject.getString("data");
                            new AlertDialog.Builder(SettlementActivity.this)
                                    .setTitle("Transaction Status")
                                    //.setMessage("Your transaction is " + status.toLowerCase() + " for amount ₹ " + amount )
                                    .setMessage(status)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(SettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong." + t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
    /*

    @SuppressLint("SetTextI18n")
    private void showTpinDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(SettlementActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        TextView tvVerificationText = addSenderDialogView.findViewById(R.id.text_user_registration);
        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        final EditText etTpin = addSenderDialogView.findViewById(R.id.et_otp);
        final EditText etMpin = addSenderDialogView.findViewById(R.id.et_generateMpin);
        final EditText etConfirmMpin = addSenderDialogView.findViewById(R.id.et_confirmMpin);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setVisibility(View.GONE);
        etMpin.setVisibility(View.GONE);
        etConfirmMpin.setVisibility(View.GONE);

        etTpin.setHint("TPIN");
        tvVerificationText.setText("TPIN Verification");

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
                    new AlertDialog.Builder(SettlementActivity.this)
                            .setMessage("Something went wrong.")
                            .show();
                }
            }
        });

    }
      /*
    private void checkTpin(String tpin) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SettlementActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();


        Call<JsonObject> call = ApiController.getInstance().getApi().checkMpinOrTPIN(ApiController.Auth_key, userId
                , deviceId, deviceInfo, "tpin", tpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            doSettlement();
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
    } */

    private boolean checkInputs() {

        if (!selectedPaymentType.equalsIgnoreCase("select")) {
            if (isWallet) {
                selectedAccountHolderName = "NA";
                selectedAccountType = "NA";
                bankName = "NA";
                selectedAccountNumber = "NA";
                selectedIfscNumber = "NA";

                amount = etAmount.getText().toString().trim();

                return !TextUtils.isEmpty(etAmount.getText());
            } else {
                if (!TextUtils.isEmpty(etAmount.getText()) && !selectedTransactionType.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountHolderName.getText()) && !selectedBankName.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscNumber.getText())
                        && !selectedAccountType.equalsIgnoreCase("select")) {
                    amount = etAmount.getText().toString().trim();
                    selectedAccountHolderName = etAccountHolderName.getText().toString().trim();
                    selectedAccountNumber = etAccountNumber.getText().toString().trim();
                    selectedIfscNumber = etIfscNumber.getText().toString().trim();
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            return false;
        }
    }

    private void setViews() {
        paymentTypeList = new ArrayList<>();
        accountTypeList = new ArrayList<>();
        transactionTypeList = new ArrayList<>();

        paymentTypeList.add("WALLET");
        paymentTypeList.add("BANK");

        accountTypeList.add("Saving");
        accountTypeList.add("Current");

        transactionTypeList.add("IMPS");
        transactionTypeList.add("NEFT");
        transactionTypeList.add("RTGS");


        HintSpinner<String> hintSpinner = new HintSpinner<>(spinnerPaymentType, new HintAdapter<String>(SettlementActivity.this, "Payment Mode", paymentTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPaymentType = paymentTypeList.get(position);
                        if (selectedPaymentType.equalsIgnoreCase("WALLET")) {
                            isWallet = true;
                            bankDetailsContainer.setVisibility(View.GONE);
                        } else {
                            getAccountDetails();
                            isWallet = false;
                            bankDetailsContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
        hintSpinner.init();


        HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerAccountType, new HintAdapter<String>(SettlementActivity.this, "Account type", accountTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedAccountType = itemAtPosition;
                    }
                });
        hintSpinner1.init();


        HintSpinner<String> hintSpinner3 = new HintSpinner<>(spinnerTransactionType, new HintAdapter<String>(SettlementActivity.this, "Transaction Mode", transactionTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedTransactionType = itemAtPosition;

                    }
                });
        hintSpinner3.init();

    }

    private void inhitViews() {

        etAmount = findViewById(R.id.et_amount);
        etAccountHolderName = findViewById(R.id.et_account_holder_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etIfscNumber = findViewById(R.id.et_ifsc_number);
        imgBack = findViewById(R.id.img_back);
        spinnerPaymentType = findViewById(R.id.payment_type_spinner);
        spinnerAccountType = findViewById(R.id.account_type_spinner);
        spinnerTransactionType = findViewById(R.id.transaction_type_spinner);
        spinnerBankName = findViewById(R.id.bank_name_spinner);
        bankDetailsContainer = findViewById(R.id.bank_details_container);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddMoreBanks = findViewById(R.id.btn_add_banks);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.settlement_layout), message, Snackbar.LENGTH_LONG);
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

}