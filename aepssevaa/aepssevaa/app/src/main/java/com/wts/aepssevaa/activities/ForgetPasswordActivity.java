package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    TextView cancel_tv;
    Button resetBtn;
    EditText userNameRecoverEt,mobileNumberRecoverEt;
    String fUsername, fMobileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        cancel_tv = findViewById(R.id.cancel_tv);
        resetBtn  = findViewById(R.id.reset_password);
        userNameRecoverEt = findViewById(R.id.userName_recover);
        mobileNumberRecoverEt = findViewById(R.id.mobileNumber_recover);

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forgetPasswordCheckInputs()) {
                    fUsername = userNameRecoverEt.getText().toString().trim();
                    fMobileNumber= mobileNumberRecoverEt.getText().toString().trim();
                    forgetPasswordRecover(fUsername, fMobileNumber);
                }
            }
        });
    }

    private void forgetPasswordRecover(String fUsername, String fMobileNumber) {
        ProgressDialog pd = new ProgressDialog(ForgetPasswordActivity.this);
        pd.setMessage("Please wait while we are processing your request .....");
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.setCancelable(false);
        pd.show();
        Call<JsonObject> call = ApiController.getInstance()
                .getApi()
                .forgetPassword(ApiController.Auth_key,fUsername,fMobileNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        String status = jsonObject.getString("status");
                        String responseMessage = jsonObject.getString("data");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            new AlertDialog.Builder(ForgetPasswordActivity.this)
                                    .setTitle(status)
                                    .setMessage(responseMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));

                                        }
                                    }).show();
                            pd.dismiss();

                        }
                        else {
                            pd.dismiss();
                            new AlertDialog.Builder(ForgetPasswordActivity.this)
                                    .setTitle(status)
                                    .setMessage(statusCode)
                                    .setPositiveButton("OK", null).show();
                        }


                    }catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();

                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .setPositiveButton("Ok", null).show();
                    }
                }
                else {
                    pd.dismiss();

                    new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pd.dismiss();

                new AlertDialog.Builder(ForgetPasswordActivity.this)
                        .setTitle("Failed")
                        .setMessage("Something went wrong")
                        .setPositiveButton("Ok", null).show();
            }
        });

    }

    public boolean forgetPasswordCheckInputs() {
        if (!TextUtils.isEmpty(userNameRecoverEt.getText())) {
            if (!TextUtils.isEmpty(mobileNumberRecoverEt.getText())) {
                return true;

            }
            else
            {
                mobileNumberRecoverEt.setError("Please Enter MobileNo");
                return false;
            }
        }
        else
        {
            userNameRecoverEt.setError("Please Enter UserName");
            return false;
        }

    }
}