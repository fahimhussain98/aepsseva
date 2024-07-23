package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SellReportActivity extends AppCompatActivity {

    ImageView backImg;
    TextView tvOpeningBal, tvClosingBal, tvCreditBal, tvEarning, tvTxn, tvSuccessRecharge, tvFailedRecharge, tvRefundRecharge, tvAd, tvMd, tvRetailer;

    SharedPreferences sharedPreferences;
    String userid, deviceId, deviceInfo, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_report);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SellReportActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        userType = sharedPreferences.getString("usertype", null);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSalesReport();

    }

    private void getSalesReport() {
        ProgressDialog progressDialog = new ProgressDialog(SellReportActivity.this, R.style.MyTheme);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Call<JsonObject> call = ApiController.getInstance().getApi().getSalesReport(ApiController.Auth_key, userid, deviceId, deviceInfo);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray = jsonObject1.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String openingBal = dataObject.getString("Openingbal");
                            String closingBal = dataObject.getString("Closingbal");
                            String creditBal = dataObject.getString("CreditBalance");
                            String totalEarning = dataObject.getString("TotalEarning");
                            String totalTxn = dataObject.getString("Totaltxncount");
                            String successRecharge = dataObject.getString("successRecharge");
                            String failedRecharge = dataObject.getString("FailedRecharge");
                            String refundRecharge = dataObject.getString("RefundRecharge");
                            String totalAd = dataObject.getString("TotalAd");
                            String totalMd = dataObject.getString("TotalMd");
                            String totalRetailer = dataObject.getString("TotalRt");

                            tvOpeningBal.setText("\u20b9 "+openingBal);
                            tvClosingBal.setText("\u20b9 "+closingBal);
                            tvCreditBal.setText("\u20b9 "+creditBal);
                            tvEarning.setText("\u20b9 "+totalEarning);
                            tvTxn.setText(totalTxn);
                            tvSuccessRecharge.setText(successRecharge);
                            tvFailedRecharge.setText(failedRecharge);
                            tvRefundRecharge.setText(refundRecharge);
                            tvAd.setText(totalAd);
                            tvMd.setText(totalMd);
                            tvRetailer.setText(totalRetailer);


                        } else {

                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {

                        progressDialog.dismiss();
                        e.printStackTrace();
                    }

                } else {

                    progressDialog.dismiss();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                progressDialog.dismiss();

            }
        });
    }


    private void initViews() {

        backImg = findViewById(R.id.backImg);
        tvOpeningBal = findViewById(R.id.tv_openingBalance);
        tvClosingBal = findViewById(R.id.tv_closingBalance);
        tvCreditBal = findViewById(R.id.tv_creditBalance);
        tvEarning = findViewById(R.id.tv_totalEarning);
        tvTxn = findViewById(R.id.tv_totalTransaction);
        tvSuccessRecharge = findViewById(R.id.tv_successRecharge);
        tvFailedRecharge = findViewById(R.id.tv_failedRecharge);
        tvRefundRecharge = findViewById(R.id.tv_refundRecharge);
        tvAd = findViewById(R.id.tv_totalAd);
        tvMd = findViewById(R.id.tv_totalMd);
        tvRetailer = findViewById(R.id.tv_totalRetailer);

    }
}