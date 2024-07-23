package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GenerateUtiPancardActivity extends AppCompatActivity {

    ImageView backImg;
    TextView tvTitle;
    LinearLayout mainLayout;
    EditText etName, etMobile, etEmail, etShop, etPinCode, etPan, etAadhar;
    TextView tvStateName;
    Button btnSubmit;

    ArrayList<String> stateList, stateIdList;
    String selectedState = "select", selectedStateId;
    String strName, strVleId, strMobile, strEmail, strShop, strPincode, strPan ,strAadhar, strQuantity, strTxnAmount;

    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo;

    //  purchase Layout

    LinearLayout purchaseLayout;
    EditText etQuantity;
    TextView tvFees;
    AutoCompleteTextView autoCouponType;
    Button btnPurchaseCoupon;

    ArrayList<String> operatorList, operatorIdList;
    String selectedOperator = "select", selectedOperatorId;

    String psaCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_uti_pancard);

        initViews();

        psaCreated = getIntent().getStringExtra("psaCreated");

        strVleId = getIntent().getStringExtra("vleId");
        strName = getIntent().getStringExtra("vleName");

        if (psaCreated.equalsIgnoreCase("psaCreated"))
        {
            mainLayout.setVisibility(View.GONE);
            purchaseLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            mainLayout.setVisibility(View.VISIBLE);
            purchaseLayout.setVisibility(View.GONE);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        strName = sharedPreferences.getString("username", null);
        strMobile = sharedPreferences.getString("mobileNo", null);
        strEmail = sharedPreferences.getString("emailId", null);
        strShop = sharedPreferences.getString("firmName", null);
        strPan = sharedPreferences.getString("panCard", null);
        strAadhar = sharedPreferences.getString("aadhaarCard", null);

        etName.setText(strName);
        etMobile.setText(strMobile);
        etEmail.setText(strEmail);
        etShop.setText(strShop);
        etPan.setText(strPan);
        etAadhar.setText(strAadhar);

        getState();
        getOperators();

        backImg.setOnClickListener(view ->
        {
            finish();
        });

        btnSubmit.setOnClickListener(view ->
        {
            if (checkInternet())
            {
                if (checkInput())
                {
                    createUtiPancard();
                }
            }
            else
            {
                new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                        .setTitle("Alert")
                        .setMessage("Please connect with Internet")
                        .setIcon(R.drawable.warning)
                        .setPositiveButton("OK", null)
                        .show();

            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!selectedOperator.equalsIgnoreCase("select"))
                {
                    getFees(etQuantity.getText().toString());
                }

            }
        });

        btnPurchaseCoupon.setOnClickListener(view ->
        {
            if (checkInternet())
            {
                purchaseCoupon();
            }
        });

    }

    public void createUtiPancard() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        strName = etName.getText().toString();
        strMobile = etMobile.getText().toString();
        strEmail = etEmail.getText().toString();
        strShop = etShop.getText().toString();
        strPincode = etPinCode.getText().toString();
        strPan = etPan.getText().toString();
        strAadhar = etAadhar.getText().toString();

        Call<JsonObject> call = ApiController.getInstance().getApi().createUtiPancard(ApiController.Auth_key, userId, deviceInfo, deviceId, strName, strMobile, strEmail,
                strShop, selectedState, strPincode, strPan, strAadhar, selectedStateId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    pDialog.dismiss();

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray transactionArray = responseObject.getJSONArray("data");

                            JSONObject transactionObject = transactionArray.getJSONObject(0);

                            strName = transactionObject.getString("VleName");
                            strVleId = transactionObject.getString("VleId");

                            mainLayout.setVisibility(View.GONE);
                            purchaseLayout.setVisibility(View.VISIBLE);

                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {

                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    public void purchaseCoupon() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        strTxnAmount = tvFees.getText().toString();
        strQuantity = etQuantity.getText().toString();

        Call<JsonObject> call = ApiController.getInstance().getApi().purchaseUtiPancardCoupon(ApiController.Auth_key, userId, deviceInfo, deviceId, strVleId, strName, selectedOperator, strQuantity,strTxnAmount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    pDialog.dismiss();

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray transactionArray = responseObject.getJSONArray("data");

                            JSONObject transactionObject = transactionArray.getJSONObject(0);

                            strName = transactionObject.getString("VleName");
                            strVleId = transactionObject.getString("VleId");


                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {

                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    public void getFees(String strQuantity) {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getFees(ApiController.Auth_key, userId, deviceInfo, deviceId,selectedOperatorId, strQuantity);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    pDialog.dismiss();

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            tvFees.setText(responseObject.getString("data"));

                        } else {

                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void getState() {

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getStateForUtiPancard(ApiController.Auth_key);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            stateList = new ArrayList<>();
                            stateIdList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String stateName = transactionObject.getString("StatenAME");
                                String stateId = transactionObject.getString("StateId");
                                stateList.add(stateName);
                                stateIdList.add(stateId);

                            }

                            tvStateName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(GenerateUtiPancardActivity.this, stateList, "Select State", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvStateName.setText(item);
                                            selectedStateId = stateIdList.get(position);
                                            selectedState = stateList.get(position);

                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                    .setTitle(responseCode)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(GenerateUtiPancardActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void getOperators() {
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getOperators(ApiController.Auth_key, deviceId, deviceInfo ,userId, "8");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");
                        if (statuscode.equalsIgnoreCase("TXN")) {

                            operatorList = new ArrayList<>();
                            operatorIdList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String operatorName = jsonObject.getString("OperatorName");
                                String operatorId = jsonObject.getString("ID");

                                operatorList.add(operatorName);
                                operatorIdList.add(operatorId);

                            }

                            ArrayAdapter<String> operatorAdapter = new ArrayAdapter<String>(GenerateUtiPancardActivity.this, android.R.layout.simple_list_item_1, operatorList);
                            autoCouponType.setAdapter(operatorAdapter);
                            autoCouponType.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    autoCouponType.showDropDown();
                                }
                            });
                            autoCouponType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    selectedOperator = operatorList.get(i);
                                    selectedOperatorId = operatorIdList.get(i);
                                }
                            });

                            pDialog.dismiss();
                        }

                        else
                        {
                            pDialog.dismiss();
                            new AlertDialog.Builder(GenerateUtiPancardActivity.this).setTitle("Alert")
                                    .setTitle(statuscode)
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

                    catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(GenerateUtiPancardActivity.this).setTitle("Alert")
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
                else
                {
                    pDialog.dismiss();
                    new AlertDialog.Builder(GenerateUtiPancardActivity.this).setTitle("Alert")
                            .setCancelable(false)
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
                new AlertDialog.Builder(GenerateUtiPancardActivity.this).setTitle("Alert")
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

    public boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null)
        {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public boolean checkInput() {
        if (!etName.getText().toString().isEmpty())
        {
            if (!etMobile.getText().toString().isEmpty())
            {
                if (!etMobile.getText().toString().isEmpty())
                {
                    if (!etShop.getText().toString().isEmpty())
                    {
                        if (!selectedState.equalsIgnoreCase("Select"))
                        {
                            if (!etPinCode.getText().toString().isEmpty())
                            {
                                if (!etPan.getText().toString().isEmpty())
                                {
                                    if (!etAadhar.getText().toString().isEmpty())
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        etAadhar.setError("Enter Aadhar");
                                        return false;
                                    }
                                }
                                else
                                {
                                    etPan.setError("Enter Pan no");
                                    return false;
                                }
                            }
                            else
                            {
                                etPinCode.setError("Enter Pincode");
                                return false;
                            }
                        }
                        else
                        {
                            tvStateName.setError("Select State");
                            return false;
                        }
                    }
                    else
                    {
                        etShop.setError("Enter Shop Name");
                        return false;
                    }
                }
                else
                {
                    etEmail.setError("Enter email");
                    return false;
                }

            }
            else
            {
                etMobile.setError("Enter Mobile No");
                return false;
            }
        }
        else
        {
            etName.setError("Enter Name");
            return false;
        }
    }

    public void initViews() {
        backImg = findViewById(R.id.back_button);
        tvTitle = findViewById(R.id.activity_title);
        mainLayout = findViewById(R.id.mainLayout);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etShop = findViewById(R.id.etShop);
        etPinCode = findViewById(R.id.etPincode);
        etPan = findViewById(R.id.etPanNo);
        etAadhar = findViewById(R.id.et_aadhar_number);
        tvStateName = findViewById(R.id.tvState);
        btnSubmit = findViewById(R.id.btnSubmit);

        purchaseLayout = findViewById(R.id.purchaseLayout);
        autoCouponType = findViewById(R.id.autoCouponType);
        etQuantity = findViewById(R.id.etQuantity);
        tvFees = findViewById(R.id.tvFees);
        btnPurchaseCoupon = findViewById(R.id.btnPurchaseCoupon);
    }

}