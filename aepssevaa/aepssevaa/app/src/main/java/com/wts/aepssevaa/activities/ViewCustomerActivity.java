package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.ViewCustomersAdapter;
import com.wts.aepssevaa.models.ViewUsersModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewCustomerActivity extends AppCompatActivity {

    ImageView imgClose;
    RecyclerView recyclerView;
    String userid;
    SharedPreferences sharedPreferences;
    ArrayList<ViewUsersModel> viewUsersModelArrayList;
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);

        initViews();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewCustomerActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        if (checkInternetState()) {
            getUsers();

        } else {
            showSnackbar("No Internet");
        }
    }

    private void getUsers() {


        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getUsers(ApiController.Auth_key, deviceId, deviceInfo, userid, "ALL");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            viewUsersModelArrayList = new ArrayList<>();
                            JSONArray transactionArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < transactionArray.length(); i++) {
                                ViewUsersModel viewUsersModel = new ViewUsersModel();

                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String name = transactionObject.getString("UserName");
                                String userId = transactionObject.getString("ID");
                                String userType = transactionObject.getString("UserType1");
                                String number = transactionObject.getString("MobileNo");
                                String date = transactionObject.getString("CreatedDate");
                                String userBalance = transactionObject.getString("WalletBalance");


                                String[] dateArr = date.split("T");
                                date = dateArr[0];

                                viewUsersModel.setName(name);
                                viewUsersModel.setUserid(userId);
                                viewUsersModel.setMobileNo(number);
                                viewUsersModel.setDate(date);
                                viewUsersModel.setUserType(userType);
                                viewUsersModel.setUserBalance(userBalance);

                                viewUsersModelArrayList.add(viewUsersModel);

                            }

                            pDialog.dismiss();

                            recyclerView.setLayoutManager(new LinearLayoutManager(ViewCustomerActivity.this,
                                    RecyclerView.VERTICAL, false));

                            ViewCustomersAdapter viewCustomersAdapter = new ViewCustomersAdapter(viewUsersModelArrayList);
                            recyclerView.setAdapter(viewCustomersAdapter);


                        } else {
                            pDialog.dismiss();
                            showSnackbar("No User Found!!!");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackbar("No User Found!!!");

                    }
                } else {
                    pDialog.dismiss();
                    showSnackbar("No User Found!!!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("No User Found!!!");
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

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_users_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void initViews() {
        imgClose = findViewById(R.id.img_close);
        recyclerView = findViewById(R.id.recycler_view);
    }
}