package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.AepsReportAdapter;
import com.wts.aepssevaa.models.AepsModel;
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


public class AepsReportActivity extends AppCompatActivity {

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;
    ImageView backImg;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    String userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<AepsModel> allReportsModelArrayList;

    String deviceId,deviceInfo;

    boolean isInitialReport = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_report);

        inhitViews();

        backImg.setOnClickListener(view ->
        {
            finish();
        });

        ////////////////////////////////////Select from date, to date and search by

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(AepsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(AepsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AepsReportActivity.this);
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
            getReports(userId,  fromDate, toDate, isInitialReport);

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
                        new AlertDialog.Builder(AepsReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        isInitialReport = false;
                        getReports(userId,  fromDate, toDate, isInitialReport);
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
        backImg = findViewById(R.id.backImgaeps);

        allReportsRecycler = findViewById(R.id.all_report_recycler);
    }

    private void getReports(String userId, String fromDate, String toDate, final boolean isInitialReport) {

        pDialog = new ProgressDialog(this, R.style.MyTheme);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = ApiController.getInstance().getApi().getAepsReport(ApiController.Auth_key,userId,deviceId,deviceInfo,fromDate,toDate);
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
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            allReportsModelArrayList = new ArrayList<AepsModel>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                AepsModel aepsModel = new AepsModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String transactionId=jsonObject.getString("TransactionId");
                                //String ownerName=jsonObject.getString("OwnerName");
                                //String bcId=jsonObject.getString("BcId");
                                String amount=jsonObject.getString("Amount");
                                String comm=jsonObject.getString("Commission");
                                //String cost=jsonObject.getString("cost");
                                String newBalance=jsonObject.getString("ClosingBal");
                                String dateTime=jsonObject.getString("CreatedOn");
                                String status=jsonObject.getString("Status");


                                aepsModel.setTransactionId(transactionId);
                                //aepsModel.setOwenername(ownerName);
                                //aepsModel.setBcId(bcId);
                                aepsModel.setAmount(amount);
                                aepsModel.setComm(comm);
                                //aepsModel.setCost(cost);
                                aepsModel.setNewbalance(newBalance);
                                aepsModel.setTimestamp(dateTime);
                                aepsModel.setTxnStatus(status);

                                allReportsModelArrayList.add(aepsModel);
                            }
                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(AepsReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            AepsReportAdapter allReportAdapter = new AepsReportAdapter(allReportsModelArrayList, isInitialReport
                                    ,AepsReportActivity.this);
                            allReportsRecycler.setAdapter(allReportAdapter);
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.aeps_reports_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}