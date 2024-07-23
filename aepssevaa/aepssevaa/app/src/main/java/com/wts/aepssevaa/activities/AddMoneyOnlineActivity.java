package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMoneyOnlineActivity extends AppCompatActivity {

    ImageView imgBack;
    TextView tvName, tvMobile, tvEmail;
    EditText etAmount;
    AppCompatButton btnSubmit;
    SharedPreferences sharedPreferences;
    String name, mobile, email, userId, deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_online);
        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddMoneyOnlineActivity.this);
        name = sharedPreferences.getString("username", null);
        mobile = sharedPreferences.getString("mobileNo", null);
        email = sharedPreferences.getString("emailId", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        tvName.setText(name);
        tvMobile.setText(mobile);
        tvEmail.setText(email);

        imgBack.setOnClickListener(view ->
        {
            finish();
        });

        btnSubmit.setOnClickListener(v ->
        {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                getQrCode();
            } else {
                etAmount.setError("amount can't be empty");
            }
        });
    }
    private void getQrCode() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String amount = etAmount.getText().toString().trim();
        Call<JsonObject> call = ApiController.getInstance()
                .getApi().upiGateway(ApiController.Auth_key,userId,deviceId,deviceInfo,amount,name,email,mobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        String data = responseObject.getString("data");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            Intent intent = new Intent(AddMoneyOnlineActivity.this, MyWebViewActivity.class);
                            intent.putExtra("url", data);
                            startActivity(intent);
                            finish();

                        } else {
                            new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                                    .setMessage(data)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
    private void initViews() {
        imgBack = findViewById(R.id.addMoneyOnlineBackImg);
        tvName = findViewById(R.id.username_tv);
        tvMobile = findViewById(R.id.mobile_tv);
        tvEmail = findViewById(R.id.email_tv);
        etAmount = findViewById(R.id.addMoneyAmount_et);
        btnSubmit = findViewById(R.id.generateQrCode_btn);
    }
}