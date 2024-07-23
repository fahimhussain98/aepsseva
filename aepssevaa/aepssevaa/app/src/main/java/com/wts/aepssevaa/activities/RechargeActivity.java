package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.CircleAdapter;
import com.wts.aepssevaa.adapters.OperatorAdapter;
import com.wts.aepssevaa.models.OperatorModel;
import com.wts.aepssevaa.myInterface.CircleInterface;
import com.wts.aepssevaa.myInterface.OperatorInterface;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RechargeActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 120;
    ConstraintLayout rechargeNumberContainer;
    TextView tvTitle;
    EditText etRechargeNumber, etAmount;
    LinearLayout mobileOperatorContainer;
    ConstraintLayout dthOperatorContainer;
    TextView tvDthOperator;
    String service;
    LinearLayout operatorLayout, circleLayout;
    String selectedOperatorId;
    String selectedOperatorName = "Select Operator", selectedStateName = "select", selectedOperatorImage;
    TextView tvOperator, tvCircle;
    Button btnRecharge;
    String userid, deviceInfo, deviceId, userMobile;
    SharedPreferences sharedPreferences;
    String responseNumber, responseAmount, responseStatus, responseTransactionId, responseLiveId, responseDateTime, responseOperator;
    Dialog operatorDialog, circleDialog;
    ImageView imgDirector;
    boolean isBrowsePlans = false, isCustomerInfo = false;
    String rechargeNumber;
    TextView tvViewPlans;
    AppCompatButton btnMyInfo, btnBrowsePlans;
    String monthlyRecharge, customerName, status, nextRecharge, lastRechargeAmount, planName, balance;
    TextView tvMonthlyRecharge, tvBalance, tvCustomerName, tvStatus, tvNextRechargeDate, tvLastRechargeAmount, tvPlanName;
    Button btnOk;
    String fromDate, toDate;
    SimpleDateFormat webServiceDateFormat;


    String[] stateNameArray = {"Assam", "Bihar Jharkhand", "Chennai", "Delhi NCR", "Gujarat", "Haryana",
            "Himachal Pradesh", "Jammu Kashmir", "Karnataka", "Kerala", "Kolkata", "Madhya Pradesh Chhattisgarh", "Maharashtra Goa",
            "Mumbai", "North East", "Orissa", "Punjab", "Rajasthan", "Tamil Nadu", "UP East", "UP West", "West Bengal"};

    String selectedState = "select", selectedStateId; //  new mplan free wala
    ArrayList<String> circleNameList, circleIdList; //  new circle get fo rmplan free wala
    Spinner spinnerState; // new fo mpla free wala

    SpinnerDialog operatorDialogNew;
    TextView tvStateNew;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(RechargeActivity.this, R.color.blue));

        //////CHANGE COLOR OF STATUS BAR

        initViews();
        service = getIntent().getStringExtra("service");

        setViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        userid = sharedPreferences.getString("userid", null);

        userMobile = sharedPreferences.getString("mobileNo", null);

        getService(service);
        setCircle();
        //getCircleCode();

        getCircleApi(); // when use plan api free mplan

        operatorLayout.setOnClickListener(view -> operatorDialog.show());

        dthOperatorContainer.setOnClickListener(view -> operatorDialog.show());
        circleLayout.setOnClickListener(view -> {
                    circleDialog.show();
                }
        );

        btnRecharge.setOnClickListener(v -> {

            if (service.equalsIgnoreCase("Dth")) {
                if (checkInputsForDTH()) {

                    rechargeConfirmationDialog();
                }
            } else if (service.equalsIgnoreCase("Mobile")){
                if (checkInputs()) {
//
                    rechargeConfirmationDialog();
                }
            }
            else
            {
                if (!etAmount.getText().toString().isEmpty())
                {
                    //    showTpinDialog(); // comment by shuaib (22-8-2022)
                    rechargeConfirmationDialog();  // add by shuaib
                }
                else
                {
                    etAmount.setError("Enter Amount");
                }
            }
        });

        imgDirector.setOnClickListener(view -> {
            @SuppressLint("IntentReset") Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, PICK_CONTACT);
        });

        etRechargeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    if (service.equalsIgnoreCase("Mobile") || service.equalsIgnoreCase("Postpaid")) {
                        if (checkInternetState()) {
                            isBrowsePlans = false;
                            rechargeNumber = etRechargeNumber.getText().toString();
                            hideKeyBoard();
                            getOperatorAndCirce();
                        } else {
                            hideKeyBoard();
                            showSnackbar("No Internet");
                        }
                    }
                }


            }
        });


        tvViewPlans.setOnClickListener(view -> {
            /* comment by shuaib for Rudra

            // uncomment by shuaib
            if (service.equalsIgnoreCase("DTH")) {
                if (checkInternetState()) {
                    if (checkDthNumberForPlans()) {
                        Intent intent = new Intent(RechargeActivity.this, DthPlansActivity.class);
                        intent.putExtra("operator", selectedOperatorName);
                        intent.putExtra("userId", userid);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceInfo", deviceInfo);
                        startActivityForResult(intent, 2);

                    }
                } else {
                    showSnackbar("No Internet");
                }
            } else {

                if (checkInternetState()) {
                    if (checkInputNumber()) {
                        Intent intent = new Intent(RechargeActivity.this, PlansActivity.class);
                        intent.putExtra("operator", selectedOperatorName);
                        intent.putExtra("commcircle", selectedStateName);
                        intent.putExtra("userId", userid);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceInfo", deviceInfo);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    showSnackbar("No Internet");
                }
            }
            */

            if(service.equalsIgnoreCase("MOBILE")){
                if(checkInternetState()){
                    if(checkInputsmPlanCircle()){

                        Intent intent = new Intent(RechargeActivity.this, PlansActivity.class);
                        if (selectedOperatorName.equalsIgnoreCase("VI"))
                        {
                            selectedOperatorName = "VODAFONE";
                        }

                        intent.putExtra("operator", selectedOperatorName);
                        intent.putExtra("commcircle", selectedStateId);
                        intent.putExtra("userId", userid);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceInfo", deviceInfo);
                        startActivityForResult(intent, 1);

                    }

                }else {
                    showSnackbar("No Internet");
                }
            }

        });

        btnMyInfo.setOnClickListener(v -> {


            // uncomment by shuaib
            if (checkInternetState()) {
                if (!TextUtils.isEmpty(etRechargeNumber.getText().toString())) {

                    if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                        getDthUserInfo();
                        isCustomerInfo = true;
                        //getDthOperator();
                    } else {
                        new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                                .setPositiveButton("Ok", null).show();

                    }

                }
                else {
                    etRechargeNumber.setError("Enter Number.");
                }
            } else {
                showSnackbar("No Internet");
            }




        });

        btnBrowsePlans.setOnClickListener(v -> {
            /* // comment by shuaib for rudra
            // uncomment by shuaib
            if (checkInternetState()) {
                if (checkInputsForMyPlans()) {
                    String mobile = etRechargeNumber.getText().toString().trim();

                    Intent intent = new Intent(RechargeActivity.this, MplanActivity.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("operator", selectedOperatorName);
                    intent.putExtra("userId", userid);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("deviceInfo", deviceInfo);
                    startActivityForResult(intent, 1);
                }
            } else {
                showSnackbar("No Internet");
            }
            */

            if (checkInternetState()) {
                if (checkInputsForMyPlans()) {
                    String mobile = etRechargeNumber.getText().toString().trim();

                    if (selectedOperatorName.equalsIgnoreCase("VI"))
                    {
                        selectedOperatorName = "VODAFONE";
                    }
                    Intent intent = new Intent(RechargeActivity.this, MplanActivity.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("operator", selectedOperatorName);
                    intent.putExtra("userId", userid);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("deviceInfo", deviceInfo);
                    startActivityForResult(intent, 1);
                }
            } else {
                showSnackbar("No Internet");
            }

        });


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fromDate = webServiceDateFormat.format(newDate1.getTime());
        toDate = webServiceDateFormat.format(newDate1.getTime());


    }

    private void getOperatorAndCirce() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();


        rechargeNumber = etRechargeNumber.getText().toString();

        Call<JsonObject> call = ApiController.getInstance().getApi().getOperatorCircle(ApiController.Auth_key, userid, deviceId, deviceInfo, rechargeNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        JSONObject recordsObject = responseObject.getJSONObject("data");

                        selectedOperatorName = recordsObject.getString("OperatorName");
                        selectedOperatorId = recordsObject.getString("OperatorId");
                        //  selectedStateName = recordsObject.getString("ComCirle");
                        selectedState = recordsObject.getString("Circle");  //  because plan api free wala lga  hai
                        selectedStateId = recordsObject.getString("ComCirle");  //  because plan api free wala lga  hai
                        selectedOperatorImage = recordsObject.getString("Image");


                        tvOperator.setText(selectedOperatorName);
                        // tvOperatorNew.setText(selectedStateName);
                        tvStateNew.setText(selectedState);

                        /*if (isBrowsePlans) {
                            Intent intent = new Intent(RechargeActivity.this, PlansActivity.class);
                            intent.putExtra("operator", selectedOperatorName);
                            intent.putExtra("commcircle", selectedOperatorName);
                            startActivityForResult(intent, 1);

                        }*/

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {

                    pDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });


    }

    private void getCircleApi() {

        Call<JsonObject> call = ApiController.getInstance().getApi().getCircle(ApiController.Auth_key, userid, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            circleNameList = new ArrayList<>();
                            circleIdList = new ArrayList<>();

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //  OperatorsModel operatorsModel = new OperatorsModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String CircleName = jsonObject.getString("CircleName");
                                String CircleId = jsonObject.getString("CircleId");

                                circleNameList.add(CircleName);
                                circleIdList.add(CircleId);
                            }
                                /*
                                HintSpinner<String> hintSpinner = new HintSpinner<>(
                                        spinnerState,
                                        new HintAdapter<String>(RechargeActivity.this, "Select Circle", circleNameList),
                                        new HintSpinner.Callback<String>() {
                                            @Override
                                            public void onItemSelected(int position, String itemAtPosition) {
                                                selectedState = circleNameList.get(position);
                                                selectedStateId = circleIdList.get(position);
                                            }
                                        });

                                hintSpinner.init();

                                 */
                            //
                            tvStateNew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    operatorDialogNew = new SpinnerDialog(RechargeActivity.this, circleNameList, "Select Circle", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialogNew.setCancellable(true); // for cancellable
                                    operatorDialogNew.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialogNew.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            selectedState = circleNameList.get(position);
                                            selectedStateId = circleIdList.get(position);
                                            tvStateNew.setText(item);

                                        }
                                    });

                                    operatorDialogNew.showSpinerDialog();
                                }
                            });


                            //


                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            String errorMessage = jsonObject1.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                        } else {
                            new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });





    }

    private boolean checkInputsmPlanCircle() {

        if (!selectedOperatorName.equalsIgnoreCase("Select Operator"))
        {
            if (!selectedState.equalsIgnoreCase("select"))
            {
                return true;
            }else
            {
                new android.app.AlertDialog.Builder(RechargeActivity.this)
                        .setMessage("Please Select Circle")
                        .setCancelable(false)
                        .setPositiveButton("Ok", null)
                        .show();
                return false;
            }
        }else
        {
            new android.app.AlertDialog.Builder(RechargeActivity.this)
                    .setMessage("Please Select Operator")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null)
                    .show();
            return false;
        }


    }


    public void rechargeConfirmationDialog() {
        if (checkInternetState()) {

            String number = etRechargeNumber.getText().toString().trim();
            final String amount = etAmount.getText().toString().trim();
            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.confirm_dialog, null, false);
            final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.setView(view1);
            builder.setCancelable(false);
            builder.show();

            TextView tvNumber = view1.findViewById(R.id.tv_number);
            TextView tvAmount = view1.findViewById(R.id.tv_amount);
            TextView tvOperator = view1.findViewById(R.id.tv_operator);
            TextView tvConfirmRecharge = view1.findViewById(R.id.tv_confirm_recharge);
            ImageView imgOperator = view1.findViewById(R.id.img_operator);
            ImageView imgClose = view1.findViewById(R.id.img_close);

            tvNumber.setText("Number  :  " + number);
            tvAmount.setText("Amount  :  " + amount);
            tvOperator.setText("Operator  :  " + selectedOperatorName);
            /////////addded by shuaib 22-8-2022
            if (service.equalsIgnoreCase("Google Play")){
                selectedOperatorImage = "http://login.aepsseva.in/ImageUpload/927963profile_vrt_raw_bytes_1587515358_10512.png";

                //  tvNumber.setText("Number  :  " + userMobile);
                Picasso.get().load(selectedOperatorImage).into(imgOperator);
                tvOperator.setText("Operator  :  " + "GooglePlay");
            }

            /////////////////////////////////// comment end by shuaib 22-8-2022


            Picasso.get().load(selectedOperatorImage).into(imgOperator);

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });

            tvConfirmRecharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doRecharge(); // uncomment by shuaib
                    // showTpinDialog(); // comment by shuaib

                    builder.dismiss();
                }
            });
        } else {
            showSnackbar("No Internet");
        }
    }

    private void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

    }



    private void setCircle() {
        circleDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
        circleDialog.setContentView(R.layout.circle_dialog);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

        circleDialog.getWindow().setLayout(width, height);

        circleDialog.getWindow().setGravity(Gravity.BOTTOM);
        circleDialog.getWindow().setBackgroundDrawableResource(R.drawable.border4);
        circleDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);

        RecyclerView rv = circleDialog.findViewById(R.id.recyclerView);
        ImageView cancelImg = circleDialog.findViewById(R.id.cancelImg);
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleDialog.dismiss();
            }
        });

        CircleAdapter circleAdapter = new CircleAdapter(stateNameArray);
        rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
        rv.setAdapter(circleAdapter);

        circleAdapter.setMyInterface(new CircleInterface() {
            @Override
            public void circleData(String circleName) {
                circleDialog.dismiss();
                selectedStateName = circleName;
                tvCircle.setText(circleName);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private boolean checkInputNumber() {

        if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {

            if (!selectedStateName.equalsIgnoreCase("select")) {

                return true;
            } else {
                new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Circle")
                        .setPositiveButton("Ok", null).show();
                return false;
            }

        } else {
            new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                    .setPositiveButton("Ok", null).show();
            return false;

        }
    }
    private void getDthOperator() {
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        String dthNumber = etRechargeNumber.getText().toString().trim();
        Call<JsonObject> call = ApiController.getInstance().getApi().getDthOperator(ApiController.Auth_key, userid, deviceId, deviceInfo, dthNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        JSONObject dataObject = responseObject.getJSONObject("data");

                        selectedOperatorImage = dataObject.getString("Image");
                        selectedOperatorName = dataObject.getString("OperatorName");
                        selectedOperatorId = dataObject.getString("OperatorId");

                        tvDthOperator.setText(selectedOperatorName);

                        pDialog.dismiss();

                        if (isCustomerInfo) {
                            getDthUserInfo();
                        } else {
                            //doRecharge();
                            //  showTpinDialog(); // comment by shuaib
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });

    }


    private void getDthUserInfo() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();


        String number = etRechargeNumber.getText().toString().trim();
        Call<JsonObject> call = ApiController.getInstance().getApi().getDthUserInfo(ApiController.Auth_key,userid, deviceId, deviceInfo, selectedOperatorName, number);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String data = responseObject.getString("data");
                        JSONObject dataObject = new JSONObject(data);
                        JSONObject innerObject = dataObject.getJSONObject("DATA");
//                        JSONArray recordsArray = dataObject.getJSONArray("records");
//                        for (int i = 0; i < recordsArray.length(); i++) {
//                            JSONObject recordsObject = recordsArray.getJSONObject(i);


                        try {
                            monthlyRecharge = innerObject.getString("Monthly");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            balance = innerObject.getString("Balance");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            customerName = innerObject.getString("Name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            status = innerObject.getString("status");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            nextRecharge = innerObject.getString("Next Recharge Date");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                            try {
//                                lastRechargeAmount = innerObject.getString("lastrechargeamount");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

                        try {
                            planName = innerObject.getString("planname");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //  }

                        final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.dth_user_info_dialog, null, false);
                        final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setCancelable(false);
                        builder.setView(view1);

                        tvMonthlyRecharge = view1.findViewById(R.id.tv_monthly_recharge);
                        tvBalance = view1.findViewById(R.id.tv_balance);
                        tvCustomerName = view1.findViewById(R.id.tv_customer_name);
                        tvStatus = view1.findViewById(R.id.tv_status);
                        tvNextRechargeDate = view1.findViewById(R.id.tv_next_recharge_date);
                        tvLastRechargeAmount = view1.findViewById(R.id.tv_last_recharge_amount);
                        tvPlanName = view1.findViewById(R.id.tv_plan_name);
                        btnOk = view1.findViewById(R.id.btn_ok);

                        tvMonthlyRecharge.setText("₹ " + monthlyRecharge);
                        tvBalance.setText("₹ " + balance);
                        tvCustomerName.setText(customerName);
                        tvStatus.setText(status);
                        tvNextRechargeDate.setText(nextRecharge);
                        //  tvLastRechargeAmount.setText("₹ " + lastRechargeAmount);
                        tvPlanName.setText(planName);

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builder.dismiss();
                                pDialog.dismiss();
                            }
                        });

                        builder.show();
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                                .setTitle("Alert!!!")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {

                    new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                            .setTitle("Alert!!!")
                            .setPositiveButton("OK", null)
                            .show();
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                        .setTitle("Alert!!!")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

    }



    private boolean checkDthNumberForPlans() {
        if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
            return true;
        } else {
            new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }

    }

    private boolean checkInputsForMyPlans() {
        if (etRechargeNumber.length() == 10) {

            if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                return true;
            } else {
                new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                        .setPositiveButton("Ok", null).show();
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private void doRecharge() {
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        String number = etRechargeNumber.getText().toString().trim();
        final String amount = etAmount.getText().toString().trim();

        Call<JsonObject> call = null;
        if (service.equalsIgnoreCase("DTH")) {
            call = ApiController.getInstance().getApi().doDthRecharge(ApiController.Auth_key, userid, deviceId, deviceInfo, selectedOperatorId, amount, number,
                    service
            );
        } else if (service.equalsIgnoreCase("Google Play")) {
            call = ApiController.getInstance().getApi().doRecharge(ApiController.Auth_key, userid, deviceId, deviceInfo, "290", amount,number , "MOBILE");
        } else
        {
            call = ApiController.getInstance().getApi().doRecharge(ApiController.Auth_key, userid, deviceId, deviceInfo, selectedOperatorId, amount, number,
                    service);
        }

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statuscode = jsonObject1.getString("statuscode");


                        if (statuscode.equalsIgnoreCase("TXN")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                responseNumber = jsonObject.getString("NUMBER");
                                responseStatus = jsonObject.getString("Status");
                                responseAmount = jsonObject.getString("Amount");
                                responseTransactionId = jsonObject.getString("TxnID");
                                responseLiveId = jsonObject.getString("LiveID");
                                responseDateTime = jsonObject.getString("TxnDate");
                                responseOperator = jsonObject.getString("OperatorName");
                            }


                            @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                            String[] splitDate = responseDateTime.split("T");
                            try {
                                Date date = inputDateFormat.parse(splitDate[0]);
                                Date time = simpleDateFormat.parse(splitDate[1]);


                                String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                String outputTime = new SimpleDateFormat("hh:mm a").format(time);
                                Intent intent = new Intent(RechargeActivity.this, RechargeStatusActivity.class);
                                intent.putExtra("responseStatus", responseStatus);
                                intent.putExtra("responseNumber", responseNumber);
                                intent.putExtra("responseAmount", responseAmount);
                                intent.putExtra("responseTransactionId", responseTransactionId);
                                intent.putExtra("responseLiveId", responseLiveId);
                                intent.putExtra("responseOperator", responseOperator);
                                intent.putExtra("outputDate", outputDate);
                                intent.putExtra("outputTime", outputTime);
                                startActivity(intent);
                                finish();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String status = jsonObject1.getString("data");
                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);
                            builder.show();

                            ImageView imgRechargeDialogIcon = view1.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = view1.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogStatus = view1.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = view1.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = view1.findViewById(R.id.btn_recharge_dialog);

                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                            tvRechargeDialogStatus.setTextColor(Color.RED);
                            tvRechargeDialogStatus.setText("Status : " + status);
                            imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .setPositiveButton("Ok", null).show();
                    }

                }

                else
                {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setMessage("something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.recharge_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInputsForDTH() {
        if (!TextUtils.isEmpty(etRechargeNumber.getText())) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;
                } else {
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("First Select Operator")
                            .setPositiveButton("Ok", null).show();
                    isCustomerInfo = false;
                    getDthOperator();
                    return false;
                }
            } else {
                etAmount.setError("Amount can't be empty.");
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etRechargeNumber.getText())) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;
                } else {
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etAmount.setError("Amount can't be empty.");
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private void getService(final String serviceName) {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getServices(ApiController.Auth_key, deviceId, deviceInfo, userid);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));


                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String serviceid = jsonObject.getString("ID");
                                String responseServiceName = jsonObject.getString("ServiceName");

                                if (responseServiceName.equalsIgnoreCase(serviceName)) {

                                    pDialog.dismiss();
                                    getOperators(serviceid);
                                    break;
                                }
                            }
                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            String errorMessage = jsonObject1.getString("data");

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            pDialog.dismiss();

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();

                        new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                .setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
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
                new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                        .setTitle("Alert")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        });

    }

    private void getOperators(String serviceid) {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getOperators(ApiController.Auth_key, deviceId, deviceInfo, userid, serviceid);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        JSONArray jsonArray = jsonObject1.getJSONArray("data");

                        ArrayList<OperatorModel> operatorModelArrayList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            OperatorModel operatorModel = new OperatorModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            String operatorName = jsonObject.getString("OperatorName");

                            String operatorId = jsonObject.getString("ID");
                            String operatorImage = jsonObject.getString("OPImage");

                            operatorModel.setOperatorName(operatorName);
                            operatorModel.setOperatorId(operatorId);
                            operatorModel.setOperatorImg("http://login.aepsseva.in" + operatorImage);

                            operatorModelArrayList.add(operatorModel);

                        }

                        operatorDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
                        operatorDialog.setContentView(R.layout.operator_dialog);

                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
                        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

                        operatorDialog.getWindow().setLayout(width, height);

                        operatorDialog.getWindow().setGravity(Gravity.BOTTOM);
                        operatorDialog.getWindow().setBackgroundDrawableResource(R.drawable.border4);
                        operatorDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);


                        RecyclerView rv = operatorDialog.findViewById(R.id.recyclerView);
                        ImageView cancelImg = operatorDialog.findViewById(R.id.cancelImg);
                        cancelImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                operatorDialog.dismiss();
                            }
                        });
                        OperatorAdapter recyclerViewItemAdapter = new OperatorAdapter(operatorModelArrayList, RechargeActivity.this);
                        rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
                        rv.setAdapter(recyclerViewItemAdapter);

                        recyclerViewItemAdapter.setMyInterface(new OperatorInterface() {
                            @Override
                            public void operatorData(String operatorName, String operatorId, String operatorImage) {
                                operatorDialog.dismiss();
                                selectedOperatorId = operatorId;
                                selectedOperatorName = operatorName;
                                selectedOperatorImage = operatorImage;
                                if (service.equalsIgnoreCase("DTH")) {
                                    tvDthOperator.setText(selectedOperatorName);
                                } else {
                                    tvOperator.setText(operatorName);

                                }
                            }
                        });

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
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
                new AlertDialog.Builder(RechargeActivity.this).setTitle("Failed")
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setViews() {
        if (service.equalsIgnoreCase("Mobile")) {
            dthOperatorContainer.setVisibility(View.GONE);
            btnMyInfo.setVisibility(View.GONE);
            tvTitle.setText("Mobile Recharge\nPrepaid");
            etRechargeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        } else if (service.equalsIgnoreCase("Dth")) {
            mobileOperatorContainer.setVisibility(View.GONE);
            btnBrowsePlans.setVisibility(View.GONE);
            tvTitle.setText("DTH Recharge");
            etRechargeNumber.setHint("Enter Subscriber I'd");
            //   spinnerState.setVisibility(View.GONE);  //  added by shuaib when i implement Plan api free wala
            tvStateNew.setVisibility(View.GONE);   //  added by shuaib when i implement Plan api free wala
            tvViewPlans.setVisibility(View.GONE);   //  added by shuaib when i implement Plan api free wala


        } else if (service.equalsIgnoreCase("Google Play")) {
            dthOperatorContainer.setVisibility(View.GONE);
            mobileOperatorContainer.setVisibility(View.GONE);
            btnBrowsePlans.setVisibility(View.GONE);
            btnMyInfo.setVisibility(View.GONE);
            tvViewPlans.setVisibility(View.GONE);
            // rechargeNumberContainer.setVisibility(View.GONE);
            rechargeNumberContainer.setVisibility(View.VISIBLE);
            tvTitle.setText("Google Play Recharge");

            tvStateNew.setVisibility(View.GONE);  //  added by shuaib after implement Google play

        } else {
            btnMyInfo.setVisibility(View.GONE);
            etRechargeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            dthOperatorContainer.setVisibility(View.GONE);
            tvTitle.setText("Mobile Recharge\nPostpaid");

        }
    }

    private void initViews() {
        rechargeNumberContainer = findViewById(R.id.rechargeNumberContainer);
        tvTitle = findViewById(R.id.tv_title);
        etRechargeNumber = findViewById(R.id.et_recharge_number);
        mobileOperatorContainer = findViewById(R.id.mobile_operator_container);
        dthOperatorContainer = findViewById(R.id.dth_operator_container);
        operatorLayout = findViewById(R.id.operator_layout);
        circleLayout = findViewById(R.id.circle_layout);
        tvOperator = findViewById(R.id.tv_operator);
        tvDthOperator = findViewById(R.id.tv_dth_operator);
        btnRecharge = findViewById(R.id.btn_recharge);
        etAmount = findViewById(R.id.et_amount);
        tvCircle = findViewById(R.id.tv_circle);
        imgDirector = findViewById(R.id.img_directory);
        tvViewPlans = findViewById(R.id.tv_view_plan);
        btnMyInfo = findViewById(R.id.btn_my_info);
        btnBrowsePlans = findViewById(R.id.btn_browse_plans);
        // spinnerState = findViewById(R.id.spinner_state); //for  plan api free wal
        tvStateNew = findViewById(R.id.tv_state_new);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                if (c != null && c.moveToFirst()) {
                    @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (number.contains("+91")) {
                        number = number.replace("+91", "");
                    }
                    if (number.contains(" ")) {
                        number = number.replace(" ", "");
                    }
                    number = number.trim();
                    etRechargeNumber.setText(number);

                }
            }
        }

        if (requestCode == 1 || requestCode == 2) {
            if (data != null) {
                String amountPlan = data.getStringExtra("amount");
                etAmount.setText(amountPlan);
            }
        }
    }
}