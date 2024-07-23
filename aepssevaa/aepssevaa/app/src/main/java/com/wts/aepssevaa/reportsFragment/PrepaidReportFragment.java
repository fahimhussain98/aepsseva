package com.wts.aepssevaa.reportsFragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.AllReportAdapter;
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

public class PrepaidReportFragment extends Fragment {

    LinearLayout fromDateLayout, toDateLayout;
    Button btnSubmit, btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    String userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<AllReportsModel> allReportsModelArrayList;

    String transactionId,liveID, operatorName, number, amount, commission, cost, balance, dateTime, status, stype, tokenKey;

    String deviceId, deviceInfo;
    String mobileNumber = "", searchAmount = "", searchStatus = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prepaid_report, container, false);

        inhitViews(view);

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
                fromDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////Select from date, to date and search by

        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE
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

        // toDate = webServiceDateFormat.format(newDate1.getTime());

        */

        getReports();

        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE
        btnSubmit.setOnClickListener(v -> {
            if (checkInternetState()) {

                if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                        tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                    new AlertDialog.Builder(getContext()).setMessage("Please select both From date and To Date")
                            .setPositiveButton("Ok", null).show();
                } else {
                    mobileNumber = "";
                    searchAmount = "";
                    searchStatus = "";
                    getReports();
                }

            } else {
                showSnackbar();
            }
        });

        btnFilter.setOnClickListener(v ->
        {
            final android.app.AlertDialog filterDialog = new android.app.AlertDialog.Builder(getContext()).create();
            View convertView = getLayoutInflater().inflate(R.layout.filter_dialog, null);
            filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            filterDialog.setView(convertView);
            filterDialog.setCancelable(false);
            filterDialog.show();


            EditText etMobileNumber = convertView.findViewById(R.id.et_mobile_number);
            EditText etAmount = convertView.findViewById(R.id.et_amount);
            AppCompatButton btnCancel = convertView.findViewById(R.id.btn_cancel);
            AppCompatButton btnSearch = convertView.findViewById(R.id.btn_search);
            RadioButton rdbSuccess = convertView.findViewById(R.id.rdb_success);
            RadioButton rdbPending = convertView.findViewById(R.id.rdb_pending);
            RadioButton rdbFailed = convertView.findViewById(R.id.rdb_failed);


            btnCancel.setOnClickListener(v1 ->
            {
                filterDialog.dismiss();
            });

            btnSearch.setOnClickListener(v2 ->
            {
                if (!TextUtils.isEmpty(etMobileNumber.getText().toString())) {
                    mobileNumber = etMobileNumber.getText().toString().trim();
                } else {
                    mobileNumber = "";
                }

                if (rdbSuccess.isChecked()) {
                    searchStatus = "Success";
                } else if (rdbPending.isChecked()) {
                    searchStatus = "Pending";
                } else if (rdbFailed.isChecked()) {
                    searchStatus = "Failed";
                } else {
                    searchStatus = "";
                }

                if (!TextUtils.isEmpty(etAmount.getText().toString())) {
                    searchAmount = etAmount.getText().toString().trim();
                } else {
                    searchAmount = "";
                }

                getReports();
                filterDialog.dismiss();

            });
        });
        return view;
    }

    private void getReports() {
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getReport(ApiController.Auth_key, userId, deviceId, deviceInfo, "", searchAmount, searchStatus,
                "", mobileNumber, fromDate, toDate, "", "", "1");
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
                                transactionId = jsonObject.getString("TxnID");
                                liveID = jsonObject.getString("LiveID");
                                String uniqueId = jsonObject.getString("UniqueID");
                                operatorName = jsonObject.getString("OperatorName");
                                number = jsonObject.getString("Number");
                                amount = jsonObject.getString("Amount");
                                commission = jsonObject.getString("Commission");
                                cost = jsonObject.getString("PayableAmount");
                                balance = jsonObject.getString("ClosingBalance");
                                dateTime = jsonObject.getString("TxnDate");
                                status = jsonObject.getString("STATUS");
                                stype = jsonObject.getString("SType");
                                String openingBalance = jsonObject.getString("OpeningBalance");
                                String closingBalance = jsonObject.getString("ClosingBalance");
                                String operatorUrl = jsonObject.getString("OPImage");


                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");

                                allReportsModel.setTransactionId(transactionId);
                                allReportsModel.setLiveId(liveID);
                                allReportsModel.setUniqueId(uniqueId);
                                allReportsModel.setOperatorName(operatorName);
                                allReportsModel.setNumber(number);
                                allReportsModel.setAmount(amount);
                                allReportsModel.setCommission(commission);
                                allReportsModel.setCost(cost);
                                allReportsModel.setBalance(balance);
                                allReportsModel.setsType(stype);
                                allReportsModel.setOpeningBalance(openingBalance);
                                allReportsModel.setClosingBalance(closingBalance);
                                allReportsModel.setImageUrl("http://login.aepsseva.in/" + operatorUrl);


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


                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            AllReportAdapter allReportAdapter = new AllReportAdapter(allReportsModelArrayList,
                                    getContext(), userId, getActivity());
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

    private void inhitViews(View view) {

        fromDateLayout = view.findViewById(R.id.from_date_layout);
        toDateLayout = view.findViewById(R.id.to_date_layout);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnFilter = view.findViewById(R.id.btn_filter);

        tvFromdate = view.findViewById(R.id.tv_from_date);
        tvToDate = view.findViewById(R.id.tv_to_date);

        imgNoDataFound = view.findViewById(R.id.img_no_data_found);
        tvNoDataFound = view.findViewById(R.id.tv_no_data_found);

        allReportsRecycler = view.findViewById(R.id.all_report_recycler);
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
    }
}
