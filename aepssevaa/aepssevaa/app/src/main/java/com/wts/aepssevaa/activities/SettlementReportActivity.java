package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.SettlementReportAdapter;
import com.wts.aepssevaa.models.SettlementReportModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettlementReportActivity extends AppCompatActivity {

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    String userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound,backImg;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<SettlementReportModel> settlementReportModelArrayList;

    boolean isInitialReport = true;

    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_report);

        inhitViews();

        ////////////////////////////////////Select from date, to date and search by

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(SettlementReportActivity.this, R.color.blue));
        //////CHANGE COLOR OF STATUS BAR

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(SettlementReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));

                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

        toDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(SettlementReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettlementReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////Select from date, to date and search by

        //////////////////////////////////////////////////////////////////
        /*
        Calendar fromCalender = Calendar.getInstance();
        fromDate = webServiceDateFormat.format(fromCalender.getTime());

        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());

         */


        if (checkInternetState()) {
            getReports(userId, fromDate, toDate, isInitialReport);

        } else {
            showSnackbar();
        }
        //////////////////////////////////////////////////////////////////
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(SettlementReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        isInitialReport = false;
                        getReports(userId, fromDate, toDate, isInitialReport);
                    }

                } else {
                    showSnackbar();
                }
            }
        });

    }

    private void inhitViews() {

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        allReportsRecycler = findViewById(R.id.all_report_recycler);
        backImg = findViewById(R.id.backImgSettlement);

    }

    private void getReports(String userId, String fromDate, String toDate, final boolean isInitialReport) {


        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = ApiController.getInstance().getApi().getSettlementReport(ApiController.Auth_key,userId,deviceId,deviceInfo,
                fromDate,toDate,"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            allReportsRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);

                            JSONObject dataObject=jsonObject1.getJSONObject("data");
                            JSONArray jsonArray = dataObject.getJSONArray("Table");
                            settlementReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                SettlementReportModel settlementReportModel = new SettlementReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("AccountHolderName");
                                String amount = jsonObject.getString("Amount");
                                String paymentType = jsonObject.getString("WalletType");
                                String accountno = jsonObject.getString("AccountNumber");
                                String status = jsonObject.getString("Status");
                                String date = jsonObject.getString("CreatedOn");
                                String transactionid = jsonObject.getString("UniqueTransactionId");
                                String surcharge = jsonObject.getString("Surcharge");
                                String oldBalance = jsonObject.getString("OpeningBal");
                                String newBalance = jsonObject.getString("ClosingBal");
                                String bankName = jsonObject.getString("BankName");
                                String comm = jsonObject.getString("Commission");

                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");


                                settlementReportModel.setName(name);
                                settlementReportModel.setAmount(amount);
                                settlementReportModel.setPaymentType(paymentType);
                                settlementReportModel.setAccountNo(accountno);
                                settlementReportModel.setStatus(status);
                                settlementReportModel.setReqDate(date);
                                settlementReportModel.setSurcharge(surcharge);
                                settlementReportModel.setTransactionId(transactionid);
                                settlementReportModel.setOldBalance(oldBalance);
                                settlementReportModel.setNewBalance(newBalance);
                                settlementReportModel.setBankName(bankName);
                                settlementReportModel.setComm(comm);


                                settlementReportModelArrayList.add(settlementReportModel);

                            }


                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(SettlementReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            SettlementReportAdapter settlementReportAdapter = new SettlementReportAdapter(settlementReportModelArrayList, isInitialReport
                                    , SettlementReportActivity.this);
                            allReportsRecycler.setAdapter(settlementReportAdapter);
                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            allReportsRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        allReportsRecycler.setVisibility(View.GONE);
                    }
                } else {
                    pDialog.dismiss();
                    allReportsRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                imgNoDataFound.setVisibility(View.VISIBLE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                allReportsRecycler.setVisibility(View.GONE);
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.settlement_lay), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}