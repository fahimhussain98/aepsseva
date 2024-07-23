package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.BankDetailsAdapter;
import com.wts.aepssevaa.models.BankDetailsModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BankDetailsActivity extends AppCompatActivity {

    String userId,deviceId, deviceInfo;
    SharedPreferences sharedPreferences;

    ArrayList<BankDetailsModel> bankDetailsModelArrayList;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        recyclerView=findViewById(R.id.recycler_view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BankDetailsActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        getBankList();

    }
    private void getBankList() {

        final ProgressDialog pDialog = new ProgressDialog(BankDetailsActivity.this);
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
                            bankDetailsModelArrayList = new ArrayList<>();
                            for (int i = 0; i < transactionArray.length(); i++) {

                                BankDetailsModel bankDetailsModel=new BankDetailsModel();

                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String bankName = transactionObject.getString("BankName");
                                String accountName = transactionObject.getString("AccountName");
                                String accountNumber = transactionObject.getString("AccountNumber");
                                String ifsc = transactionObject.getString("IFSC");
                                String branch = transactionObject.getString("Branch");

                                bankDetailsModel.setBankName(bankName);
                                bankDetailsModel.setAccountName(accountName);
                                bankDetailsModel.setAccountNumber(accountNumber);
                                bankDetailsModel.setIfscCode(ifsc);
                                bankDetailsModel.setBranch(branch);
                                bankDetailsModelArrayList.add(bankDetailsModel);
                            }

                            BankDetailsAdapter bankDetailsAdapter = new BankDetailsAdapter(bankDetailsModelArrayList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(BankDetailsActivity.this,
                                    RecyclerView.VERTICAL, false));
                            recyclerView.setAdapter(bankDetailsAdapter);

                            pDialog.dismiss();
                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            new AlertDialog.Builder(BankDetailsActivity.this)
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
                            new AlertDialog.Builder(BankDetailsActivity.this)
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
                        new AlertDialog.Builder(BankDetailsActivity.this)
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
                    new AlertDialog.Builder(BankDetailsActivity.this)
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
                new AlertDialog.Builder(BankDetailsActivity.this)
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


}