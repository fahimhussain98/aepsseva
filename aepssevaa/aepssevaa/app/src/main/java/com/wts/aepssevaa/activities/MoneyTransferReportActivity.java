package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.MoneyReportAdapter;
import com.wts.aepssevaa.models.MoneyReportModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoneyTransferReportActivity extends AppCompatActivity {

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;
    Spinner spinner;
    String[] searchByArray = {"ALL", "DATE"};
    String userId, searchBy = "Select", fromDate = "", toDate = "";
    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    RecyclerView creditDebitRecycler;
    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    CardView backCardView;

    String deviceId, deviceInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer_report);

        inhitViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MoneyTransferReportActivity.this, R.color.blue));
        //////CHANGE COLOR OF STATUS BAR

        HintSpinner<String> hintSpinner = new HintSpinner<>(spinner, new HintAdapter<String>(MoneyTransferReportActivity.this,
                "Select", Arrays.asList(searchByArray)), new HintSpinner.Callback<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                searchBy = itemAtPosition;

            }
        });
        hintSpinner.init();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(MoneyTransferReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));

                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);

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
                fromDatePicker = new DatePickerDialog(MoneyTransferReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);

                fromDatePicker.show();

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MoneyTransferReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        /*

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));
        fromDate = webServiceDateFormat.format(newDate1.getTime());
        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

        toDate = webServiceDateFormat.format(newDate1.getTime());

         */


        if (checkInternetState()) {

                getReport();

        } else {
            showSnackbar();
        }

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetState()) {
                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date"))
                    {
                        new AlertDialog.Builder(MoneyTransferReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {

                        getReport();
                    }
                } else {
                    showSnackbar();
                }
            }
        });

        backCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getReport() {


        final ProgressDialog pDialog = new ProgressDialog(MoneyTransferReportActivity.this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getDmtReport(ApiController.Auth_key, userId, deviceId, deviceInfo, fromDate, toDate, "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            creditDebitRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);


                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            moneyReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MoneyReportModel moneyReportModel = new MoneyReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String amount = jsonObject.getString("Amount");
                                String cost = jsonObject.getString("PayableAmount");
                                String balance = jsonObject.getString("ClosingBalance");
                                String tdatetime = jsonObject.getString("CreateDate");
                                String accountno = jsonObject.getString("AccountNo");
                                String beniname = jsonObject.getString("BenificiaryName");
                                String bank = jsonObject.getString("BankName");
                                String ifsc = jsonObject.getString("IfscCode");
                                String status = jsonObject.getString("status");
                                String transactionid = jsonObject.getString("TransactionID");
                                String comm = jsonObject.getString("Commission");
                                String surcharge = jsonObject.getString("Surcharge");

                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");



                                moneyReportModel.setAmount(amount);
                                moneyReportModel.setCost(cost);
                                moneyReportModel.setBalance(balance);
                                moneyReportModel.setDate(tdatetime);
                                moneyReportModel.setAccountNumber(accountno);
                                moneyReportModel.setBeniName(beniname);
                                moneyReportModel.setBank(bank);
                                moneyReportModel.setIfsc(ifsc);
                                moneyReportModel.setStatus(status);
                                moneyReportModel.setTransactionId(transactionid);
                                moneyReportModel.setComm(comm);
                                moneyReportModel.setSurcharge(surcharge);

                                moneyReportModelArrayList.add(moneyReportModel);
                            }


                            creditDebitRecycler.setLayoutManager(new LinearLayoutManager(MoneyTransferReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            MoneyReportAdapter moneyReportAdapter = new MoneyReportAdapter(moneyReportModelArrayList, MoneyTransferReportActivity.this);
                            creditDebitRecycler.setAdapter(moneyReportAdapter);
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();

                            creditDebitRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        creditDebitRecycler.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    creditDebitRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pDialog.dismiss();
                creditDebitRecycler.setVisibility(View.GONE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                imgNoDataFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private void inhitViews() {
        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        spinner = findViewById(R.id.spinner);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        creditDebitRecycler = findViewById(R.id.credit_debit_recycler);
        backCardView = findViewById(R.id.dmt_reportsBackCard);
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.money_report_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}