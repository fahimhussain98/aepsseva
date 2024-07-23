package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WalletActivity extends AppCompatActivity {

    TextView tvMainWallet, tvAepsWallet;
    ImageView imgMainRefresh, imgAepsRefresh;
    String balance = "00.00";
    String userid;
    SharedPreferences sharedPreferences;
    Animation rotateAnimation;

    String  deviceId,deviceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        inhitViews();

        if (checkInternetState()) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WalletActivity.this);
            userid = sharedPreferences.getString("userid", null);
            deviceId = sharedPreferences.getString("deviceId", null);
            deviceInfo = sharedPreferences.getString("deviceInfo", null);
            rotateAnimation= AnimationUtils.loadAnimation(WalletActivity.this,R.anim.rotate);
            getBalance();
            getAepsBalance();
        }
        else {
            showSnackbar();
        }

        imgMainRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMainRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    getBalance();
                } else {
                    showSnackbar();
                }
            }
        });

        imgAepsRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAepsRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    getAepsBalance();
                } else {
                    showSnackbar();
                }
            }
        });

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

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.wallet_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void getBalance() {

        final ProgressDialog pDialog = new ProgressDialog(this, R.style.MyTheme);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getBalance(ApiController.Auth_key,userid,deviceId,deviceInfo,"Login","0");

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


                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String balance = jsonObject.getString("userBalance");
                            tvMainWallet.setText("₹ " + balance);



                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            tvMainWallet.setText("₹ 00.00");


                        } else {
                            pDialog.dismiss();
                            tvMainWallet.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        tvMainWallet.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    tvMainWallet.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

                tvMainWallet.setText("₹ " + "00.00");

            }
        });
    }

    private void getAepsBalance() {

        final ProgressDialog pDialog = new ProgressDialog(this, R.style.MyTheme);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getAepsBalance(ApiController.Auth_key,userid,deviceId,deviceInfo,"Login","0");

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

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String balance = jsonObject.getString("userBalance");
                            tvAepsWallet.setText("₹ " + balance);



                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            tvAepsWallet.setText("₹ 00.00");


                        } else {
                            pDialog.dismiss();
                            tvAepsWallet.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        tvAepsWallet.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    tvAepsWallet.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

                tvAepsWallet.setText("₹ " + "00.00");

            }
        });
    }

    private void inhitViews() {
        tvMainWallet = findViewById(R.id.tv_main_wallet);
        tvAepsWallet = findViewById(R.id.tv_aeps_wallet);
        imgMainRefresh = findViewById(R.id.img_refresh);
        imgAepsRefresh = findViewById(R.id.img_aeps_refresh);
    }
}