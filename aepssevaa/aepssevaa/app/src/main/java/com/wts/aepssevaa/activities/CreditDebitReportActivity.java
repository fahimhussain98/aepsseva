package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.CreditDebitAdapter;
import com.wts.aepssevaa.models.CreditDebitModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreditDebitReportActivity extends AppCompatActivity {

    /////////////////////////////////////////toolbar
    String title;
    TextView activityTitle;
    CardView backButton;
    /////////////////////////////////////////toolbar

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;
    Spinner spinner;
    //String[] searchByArray = {"ALL", "DATE"};
    String[] searchByArray = {"DATE"};
    String userId, searchBy = "DATE", fromDate = "", toDate = "";
    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    String drUser, crUser, amount, paymentType, paymentDate, remarks;
    ArrayList<CreditDebitModel> creditDebitModelArrayList;
    RecyclerView creditDebitRecycler;
    Call<JsonObject> call;
    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    public static String cRDrType="";
    String oldBal,newBal,crDrBy1;

    boolean isInitialReport = true,isCreditReport;

    String deviceId,deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_debit_report);

        inhitViews();

        //////////////////////////////////////////////////////////////////Toolbar
        title = getIntent().getStringExtra("title");
        activityTitle.setText(title);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////Toolbar

        ////////////////////////////////////Select from date, to date and search by
        HintSpinner<String> hintSpinner = new HintSpinner<>(spinner, new HintAdapter<String>(CreditDebitReportActivity.this,
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
                fromDatePicker = new DatePickerDialog(CreditDebitReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(CreditDebitReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreditDebitReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////Select from date, to date and search by

        ////////////////////////////////////////////////////////////////////////////

        Calendar fromCalender = Calendar.getInstance();
        //fromCalender.add(Calendar.MONTH, -1);
        fromDate = webServiceDateFormat.format(fromCalender.getTime());
        tvFromdate.setText(simpleDateFormat.format(fromCalender.getTime()));

        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());
        tvToDate.setText(simpleDateFormat.format(fromCalender.getTime()));


        if (checkInternetState()) {

            if (title.equalsIgnoreCase("Credit Report")) {

                cRDrType = "Credit";
                // call = ApiController.getInstance().getApi().getCreditReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"Date",fromDate,toDate);
                call = ApiController.getInstance().getApi().getCreditDebitReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"Credit",fromDate,toDate);
                isCreditReport=true;
                getReport(isInitialReport);
            }
            if (title.equalsIgnoreCase("Debit Report")) {
                cRDrType = "Debit";
                // call = ApiController.getInstance().getApi().getDebitReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"Date",fromDate,toDate);
                call = ApiController.getInstance().getApi().getCreditDebitReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"Debit",fromDate,toDate);
                isCreditReport=false;
                getReport(isInitialReport);
            }

        } else {
            showSnackbar();
        }

        ////////////////////////////////////////////////////////////////////////////

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInitialReport = false;
                if (checkInternetState()) {

                    if (title.equalsIgnoreCase("Credit Report")) {
                        isCreditReport=true;

                        if (searchBy.equalsIgnoreCase("Select")) ///////////////////if user did not select search by
                        {
                            new AlertDialog.Builder(CreditDebitReportActivity.this).setMessage("Please select filter type")
                                    .setPositiveButton("Ok", null).show();
                        } else if (searchBy.equalsIgnoreCase("DATE")) //////////////////if user did not select from and to date
                        {
                            if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                                    tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                                new AlertDialog.Builder(CreditDebitReportActivity.this).setMessage("Please select both From date and To Date")
                                        .setPositiveButton("Ok", null).show();
                            } else {
                                // call = ApiController.getInstance().getApi().getCreditReport(ApiController.Auth_key,userId,deviceId,deviceInfo,searchBy,fromDate,toDate);
                                call = ApiController.getInstance().getApi().getCreditDebitReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"Credit",fromDate,toDate);

                                getReport(isInitialReport);

                            }
                        } else {

                            //  call = ApiController.getInstance().getApi().getCreditReport(ApiController.Auth_key,userId,deviceId,deviceInfo,"ALL","","");
                            //  getReport(isInitialReport);

                        }
                    } else if (title.equalsIgnoreCase("Debit Report")) {
                        isCreditReport=false;
                        if (searchBy.equalsIgnoreCase("Select")) ///////////////////if user did not select search by
                        {
                            new AlertDialog.Builder(CreditDebitReportActivity.this).setMessage("Please select filter type")
                                    .setPositiveButton("Ok", null).show();
                        } else if (searchBy.equalsIgnoreCase("Date")) //////////////////if user did not select from and to date
                        {
                            if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                                    tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {

                                new AlertDialog.Builder(CreditDebitReportActivity.this).setMessage("Please select both From date and To Date")
                                        .setPositiveButton("Ok", null).show();
                            }
                            else {

                                call = ApiController.getInstance().getApi().getCreditDebitReport(ApiController.Auth_key, userId, deviceId, deviceInfo, "Debit", fromDate, toDate);
                                getReport(isInitialReport);
                            }
                        } else {
//                            call = ApiController.getInstance().getApi().getDebitReport(ApiController.Auth_key, userId,deviceId,deviceInfo, "ALL", "", "");
//                            getReport(isInitialReport);
                        }
                    }

                } else {
                    showSnackbar();
                }
            }
        });
    }

    private void getReport(final boolean isInitialReport) {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

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
                            creditDebitModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                CreditDebitModel creditDebitModel = new CreditDebitModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                /*
                                //drUser = jsonObject.getString("DrUser");
                                crUser = jsonObject.getString("CreditUser");
                                //id = jsonObject.getInt("id") + "";
                                amount = jsonObject.getDouble("Amount") + "";
                                //paymentType = jsonObject.getString("PaymentType");
                                paymentDate = jsonObject.getString("TransactionDate");
                                //remarks = jsonObject.getString("Remarks");

                                String shopName = jsonObject.getString("ShopeName"); //  added by shuaib client want
                                String mobileNum = jsonObject.getString("MobileNo"); //  added by shuaib client want


                                creditDebitModel.setDrUser(drUser);
                                creditDebitModel.setCrUser(crUser);
                                //creditDebitModel.setId(id);
                                creditDebitModel.setAmount(amount);
                                creditDebitModel.setPaymentType(paymentType);
                                creditDebitModel.setShopName(shopName);
                                creditDebitModel.setMobileNum(mobileNum);

                                */

                                drUser = jsonObject.getString("crDrUser");
                                crUser = jsonObject.getString("crDrBy");
                                paymentDate = jsonObject.getString("transactionDate");
                                amount = jsonObject.getDouble("amount") + "";
                                oldBal = jsonObject.getDouble("oldBalance") + "";
                                newBal = jsonObject.getDouble("newBalance") + "";
                                crDrBy1 = jsonObject.getString("CrDrBy1");




                                creditDebitModel.setDrUser(drUser);
                                creditDebitModel.setCrUser(crUser);
                                creditDebitModel.setAmount(amount);
                                creditDebitModel.setOldBal(oldBal);
                                creditDebitModel.setNewBal(newBal);
                                creditDebitModel.setCrDrBy1(crDrBy1);
                                creditDebitModel.setMobileNum(drUser);



                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = paymentDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    creditDebitModel.setDate(outputDate);
                                    creditDebitModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                creditDebitModel.setRemarks(remarks);

                                creditDebitModelArrayList.add(creditDebitModel);
                            }

                            creditDebitRecycler.setLayoutManager(new LinearLayoutManager(CreditDebitReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            /*  //  comment by shuaib

                            int numberOfColumns = 2;
                            creditDebitRecycler.setLayoutManager(new GridLayoutManager(CreditDebitReportActivity.this, numberOfColumns));
                             */

                            CreditDebitAdapter creditDebitAdapter = new CreditDebitAdapter(creditDebitModelArrayList, isInitialReport,isCreditReport,CreditDebitReportActivity.this);
                            creditDebitRecycler.setAdapter(creditDebitAdapter);
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
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title2);

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        spinner = findViewById(R.id.spinner);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        creditDebitRecycler = findViewById(R.id.credit_debit_recycler);
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.credit_debit_report_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}