package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.jaredrummler.android.device.DeviceName;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    EditText et_loginUsername, et_loginPassword;
    Button loginBtn;
    String userid, usertype, mobileNo, panCard, aadhaarCard, emailId, firmName, address, dob, userTypeId, city, username;
    String loginUserName, loginPassword;
    String deviceId;
    TextView showHide_tv,signUp,forgetPassword_tv,tvWebLink,tvCallLink;
    String deviceInfo;
    Boolean mPinResponse;
    String supportNumber = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_loginUsername = findViewById(R.id.user_name);
        et_loginPassword = findViewById(R.id.login_password);
        tvWebLink= findViewById(R.id.tv_web_link);
        tvCallLink = findViewById(R.id.tv_call_link);

        loginBtn = findViewById(R.id.login_submit);
        showHide_tv = findViewById(R.id.showHideBtn);
        forgetPassword_tv = findViewById(R.id.forget_password);
        signUp= findViewById(R.id.signUp_tv);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        showHide_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide_tv.getText().toString().equals("Show Password")){
                    // show password
                    et_loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHide_tv.setText("Hide Password");

                } else{
                    // hide password
                    et_loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHide_tv.setText("Show Password");

                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(LoginActivity.this, SignUpActivity.class);
                in.putExtra("title", "SignUp");
                startActivity(in);

            }
        });

        forgetPassword_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetState()) {
                    startActivity(new Intent(getApplicationContext(),ForgetPasswordActivity.class));
                } else {
                    showSnackBar();
                }
            }
        });

        tvWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://aepsseva.in/"));
                startActivity(browserIntent);

            }
        });

        tvCallLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supportNumber = "+91-7779822793";
                openCaller(supportNumber);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetState()) {
                    if (checkInputs()) {

                        DeviceName.init(LoginActivity.this);
                        DeviceName.with(LoginActivity.this).request(new DeviceName.Callback() {
                            @Override
                            public void onFinished(DeviceName.DeviceInfo info, Exception error) {

                                String manufacturer = info.manufacturer;
                                String name = info.marketName;
                                String model = info.model;
                                String codename = info.codename;
                                String deviceName = info.getName();
                                deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
                                processLogin();
                            }
                        });
                    }
                } else {
                    showSnackBar();
                }
            }
        });
    }
    private void processLogin() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Logging in");
        pd.setMessage("Please wait while logging in");
        pd.setCancelable(false);
        pd.show();
        loginUserName = et_loginUsername.getText().toString().trim();
        loginPassword = et_loginPassword.getText().toString().trim();

        Call<JsonObject> call = ApiController.getInstance()
                .getApi()
                .getlogin(ApiController.Auth_key, loginUserName, loginPassword, deviceId, deviceInfo);
        call.enqueue((new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            userid = jsonObject1.getString("UserID");
                            username = jsonObject1.getString("name");
                            usertype = jsonObject1.getString("userType");
                            mobileNo = jsonObject1.getString("mobileNo");
                            panCard = jsonObject1.getString("panCardNo");
                            aadhaarCard = jsonObject1.getString("aadharNo");
                            emailId = jsonObject1.getString("emailID");
                            firmName = jsonObject1.getString("companyName");
                            address = jsonObject1.getString("address");
                            dob = jsonObject1.getString("dob");
                            userTypeId = jsonObject1.getString("userTypeId");
                            city = jsonObject1.getString("cityname");
                            mPinResponse = jsonObject1.getBoolean("mpin");

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("loginUsername", loginUserName);
                            editor.putString("loginPassword", loginPassword);
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("usertype", usertype);
                            editor.putString("mobileNo", mobileNo);
                            editor.putString("panCard", panCard);
                            editor.putString("aadhaarCard", aadhaarCard);
                            editor.putString("emailId", emailId);
                            editor.putString("firmName", firmName);
                            editor.putString("address", address);
                            editor.putString("dob", dob);
                            editor.putString("userTypeId", userTypeId);
                            editor.putString("city", city);
                            editor.putString("deviceInfo",deviceInfo);
                            editor.putBoolean("mPin",mPinResponse);
                            editor.putString("deviceId",deviceId);
                            editor.apply();

                            pd.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeDashboardActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            finish();

                        }
                        else if (statusCode.equalsIgnoreCase("ERR")) {
                            pd.dismiss();
                            String errorMessage = jsonObject.getString("data");
                            String errorTitle = jsonObject.getString("status");

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle(errorTitle)
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                        else {
                            pd.dismiss();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    }
                    catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Something went wrong!!!")
                                .setPositiveButton("Ok", null)
                                .show();

                    }

                } else {
                    pd.dismiss();
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Something went wrong!!!")
                            .setPositiveButton("Ok", null)
                            .show();
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pd.dismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(t.getMessage())
                        .setTitle("Alert")
                        .setPositiveButton("Ok", null).show();
            }
        }));

    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(et_loginUsername.getText())) {
            if (!TextUtils.isEmpty(et_loginPassword.getText())) {

                return true;
            } else {
                et_loginPassword.setError("Password can't be empty.");
                return false;

            }
        } else {

            et_loginUsername.setError("User Name can't be empty.");
            return false;
        }
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void openCaller(String supportNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supportNumber));
        startActivity(intent);
    }




}

/*
public class LoginActivity extends AppCompatActivity {
    TextView tvSignUp,tvForgetPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvSignUp = findViewById(R.id.signUp_tv);
        btnLogin = findViewById(R.id.login_submit);
        tvForgetPassword= findViewById(R.id.forget_password);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,HomeDashboardActivity.class));

            }
        });
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));

            }
        });

    }
}
*/
