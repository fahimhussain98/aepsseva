package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.BbpsReportAdapter;
import com.wts.aepssevaa.models.AllReportsModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewUtilityReportsActivity extends AppCompatActivity {

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    String userId, fromDate = "", toDate = "", searchBy;

    ImageView imgNoDataFound;
    TextView tvNoDataFound,title;
    RecyclerView allReportsRecycler;

    ArrayList<AllReportsModel> allReportsModelArrayList;

    String transactionId, operatorName, number, amount, commission, cost, balance, dateTime, status,stype,tokenKey;

    String deviceId,deviceInfo;
    ImageView backImg;
    String strTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_utility_reports);
        inhitViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewUtilityReportsActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        strTitle=getIntent().getStringExtra("title");
        searchBy=getIntent().getStringExtra("service");
        title.setText(strTitle);

        backImg.setOnClickListener(view ->
        {
            finish();
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(NewUtilityReportsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(NewUtilityReportsActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        getReports(userId, searchBy, fromDate, toDate);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(NewUtilityReportsActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        getReports(userId, searchBy, fromDate, toDate);
                    }

                } else {
                    showSnackbar();
                }
            }
        });

    }

    private void getReports(String userId, String searchBy, String fromDate, String toDate) {

        ProgressDialog pDialog = new ProgressDialog(NewUtilityReportsActivity.this, R.style.MyTheme);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getBBPSReport(ApiController.Auth_key,userId,deviceId,deviceInfo,fromDate,toDate
                ,searchBy);
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
                            allReportsModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                AllReportsModel allReportsModel = new AllReportsModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                transactionId = jsonObject.getString("TransactionID");
                                operatorName = jsonObject.getString("OperatorName");
                                number = jsonObject.getString("ConsumerNo");
                                amount = jsonObject.getString("Amount");
                                commission = jsonObject.getString("Commission");
                                cost = jsonObject.getString("PayableAmount");
                                balance = jsonObject.getString("ClosingBalance");
                                dateTime = jsonObject.getString("CreateDate");
                                status = jsonObject.getString("Status");

                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");

                                stype = jsonObject.getString("ServiceType");
                                allReportsModel.setsType(stype);

                                allReportsModel.setTransactionId(transactionId);
                                allReportsModel.setOperatorName(operatorName);
                                allReportsModel.setNumber(number);
                                allReportsModel.setAmount(amount);
                                allReportsModel.setCommission(commission);
                                allReportsModel.setCost(cost);
                                allReportsModel.setBalance(balance);
                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = dateTime.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    allReportsModel.setDate(outputDate);
                                    allReportsModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                allReportsModel.setStatus(status);

                                allReportsModelArrayList.add(allReportsModel);

                            }


                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(NewUtilityReportsActivity.this,
                                    RecyclerView.VERTICAL, false));

                            BbpsReportAdapter bbpsReportAdapter = new BbpsReportAdapter(allReportsModelArrayList,NewUtilityReportsActivity.this
                                    ,NewUtilityReportsActivity.this,userId,"",deviceId
                                    ,deviceInfo,false);
                            allReportsRecycler.setAdapter(bbpsReportAdapter);
                            pDialog.dismiss();
                        } else  {
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


    private void inhitViews() {

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        allReportsRecycler = findViewById(R.id.all_report_recycler);
        title = findViewById(R.id.title_reports);

        backImg = findViewById(R.id.backImg);
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) NewUtilityReportsActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Toast.makeText(NewUtilityReportsActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
    }


}