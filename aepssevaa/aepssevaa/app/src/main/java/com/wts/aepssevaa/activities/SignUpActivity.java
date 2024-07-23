package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends AppCompatActivity {
    EditText etOwnerName, etShopName, etAddress, etEmail, etMobileNumber,  etPinCode, etRemarks,signUp_password_et,etAadharcard,etPancard;
    TextView tvDob,alreadyRegister_tv;
    Spinner stateId_spinner,cityId_spinner;
    Button btnProceed;
    MaterialCardView signUp_dobCardView;
    SharedPreferences signUpShp;
    String user_id,deviceInfo,deviceId;
    ArrayList<String> stateNameList , stateIdList ;
    String stateId,stateName,rcityName,rCityId;
    ImageView imgBack;
    ArrayAdapter<String> stateListAdapter,cityListAdapter;
    ArrayList<String> rcityNameList,rCityIdList;
    String selectedDob = "",selectedStateId="",selectedCityId="";

    Call<JsonObject> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();

        signUpShp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_id = signUpShp.getString("userid", null);
        deviceInfo = signUpShp.getString("deviceInfo", null);
        deviceId = signUpShp.getString("deviceId", null);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        alreadyRegister_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        //



        //

        signUp_dobCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        Calendar fromdate1 = Calendar.getInstance();
                        fromdate1.set(i, i1, i2);
                        tvDob.setText(simpleDateFormat.format(fromdate1.getTime()));
                        selectedDob = apiDateFormat.format(fromdate1.getTime());
                    }
                }, year, month, day).show();

            }
        });

        myGetState();
        btnProceed.setOnClickListener(v -> {
            if (checkInternetState()) {
                checkAddUser();
            } else {
                showSnackbar("No Internet");
            }
        });
    }
    private void checkAddUser() {
        if (checkInputs()) {

            String ownerName = etOwnerName.getText().toString().trim();
            String mobile = etMobileNumber.getText().toString().trim();
            String emailId = etEmail.getText().toString().trim();
            String shopName = etShopName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String pinCode = etPinCode.getText().toString().trim();
            String remarks = etRemarks.getText().toString().trim();
            String password = signUp_password_et.getText().toString().trim();
            String aadharcard = etAadharcard.getText().toString().trim();
            String panCard = etPancard.getText().toString().trim();

            call = ApiController.getInstance()
                    .getApi().newUserSignUp(ApiController.Auth_key,ownerName,selectedDob,shopName,mobile,emailId,address,pinCode,selectedStateId,selectedCityId,remarks,"NA",password,aadharcard,panCard);

            signUpUser();

        } else {

            showSnackbar("All fields are mandatory.");

        }
    }

    private void signUpUser() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading ....");
        pd.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pd.setCancelable(false);
        pd.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            String responseMessage = responseObject.getString("data");
                            pd.dismiss();
                            new android.app.AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Status")
                                    .setMessage(responseMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pd.dismiss();
                            new android.app.AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Status !!!")
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {
                            pd.dismiss();
                            new android.app.AlertDialog.Builder(SignUpActivity.this)
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
                        pd.dismiss();
                        new android.app.AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pd.dismiss();
                    new android.app.AlertDialog.Builder(SignUpActivity.this)
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
                pd.dismiss();
                new android.app.AlertDialog.Builder(SignUpActivity.this)
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
    private boolean checkInputs() {
        return !TextUtils.isEmpty(etOwnerName.getText())&& !TextUtils.isEmpty(etEmail.getText())
                && !TextUtils.isEmpty(etMobileNumber.getText()) && !TextUtils.isEmpty(etShopName.getText()) && (etMobileNumber.getText().toString().length() == 10)
                && !TextUtils.isEmpty(etAddress.getText()) && !TextUtils.isEmpty(etPinCode.getText())
                && !selectedDob.equalsIgnoreCase("")
                && !selectedCityId.equalsIgnoreCase("") && !selectedStateId.equalsIgnoreCase("")&& !TextUtils.isEmpty(signUp_password_et.getText());
    }
    private void myGetState() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading ....");
        pd.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pd.setCancelable(false);
        pd.show();

        Call<JsonObject> call = ApiController.getInstance()
                .getApi().getState(ApiController.Auth_key,user_id,deviceId,deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            stateNameList = new ArrayList<>();
                            stateIdList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                stateId = jsonObject1.getString("StateId");
                                stateName = jsonObject1.getString("StateName");
                                stateNameList.add(stateName);
                                stateIdList.add(stateId);
                                pd.dismiss();
                            }
                            /*
                            stateListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , stateNameList);
                            stateListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            stateId_spinner.setAdapter(stateListAdapter);

                            stateId_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    selectedStateId=stateIdList.get(position);
                                    getCityList();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                             */

                            /////////////////////////////////////////////////////////////////////
                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    stateId_spinner,
                                    new HintAdapter<String>(SignUpActivity.this, "Select State", stateNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedStateId=stateIdList.get(position);
                                            getCityList();
                                        }
                                    });
                            hintSpinner.init();

                            /////////////////////////////////////////////////////////////////////

                        }
                        else {
                            pd.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("No State Found")
                                    .setCancelable(false)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle("No State Found")
                                .setMessage(e.getMessage())
                                .setCancelable(false)
                                .show();
                    }
                }else {
                    pd.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("No State Found")
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pd.dismiss();
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("No State Found")
                        .setCancelable(false)
                        .show();

            }
        });

    }

    private void getCityList() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading ....");
        pd.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pd.setCancelable(false);
        pd.show();
        Call<JsonObject> call = ApiController.getInstance()
                .getApi().getCity(ApiController.Auth_key,user_id,deviceId,deviceInfo,selectedStateId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            rcityNameList = new ArrayList<>();
                            rCityIdList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                rcityName = jsonObject1.getString("CityName");
                                rCityId   = jsonObject1.getString("CityId");

                                rcityNameList.add(rcityName);
                                rCityIdList.add(rCityId);

                            }
                            pd.dismiss();
                            /*
                            cityListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , rcityNameList);
                            cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            cityId_spinner.setAdapter(cityListAdapter);
                            pd.dismiss();

                            cityId_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    selectedCityId=rCityIdList.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                              */

                            /////////////////////////////////////////////////////////////////////
                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    cityId_spinner,
                                    new HintAdapter<String>(SignUpActivity.this, "Select City", rcityNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedCityId=rCityIdList.get(position);
                                        }
                                    });
                            hintSpinner.init();

                            /////////////////////////////////////////////////////////////////////


                        }
                        else {
                            pd.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("No City Found")
                                    .setCancelable(false)
                                    .show();
                        }


                    }catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle("No City Found")
                                .setMessage(e.getMessage())
                                .setCancelable(false)
                                .show();

                    }

                }
                else {
                    pd.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setTitle("Alert")
                            .setMessage("No City Found")
                            .setCancelable(false)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pd.dismiss();

                new AlertDialog.Builder(SignUpActivity.this)
                        .setTitle("Alert")
                        .setMessage("No City Found")
                        .setCancelable(false)
                        .show();

            }
        });


    }



    private void initViews() {

        etOwnerName = findViewById(R.id.username_et);
        etShopName = findViewById(R.id.signUpShopName_et);
        etEmail = findViewById(R.id.signUpemailId_et);
        etMobileNumber = findViewById(R.id.signUpmobile_et);
        tvDob = findViewById(R.id.signUpUserDob_tv);
        etAddress = findViewById(R.id.signUpAddress_et);
        etPinCode = findViewById(R.id.signUp_Pincode_et);
        etRemarks = findViewById(R.id.signUpRemarks_et);
        btnProceed = findViewById(R.id.signUpBtn);
        imgBack = findViewById(R.id.signUpBackImg);
        cityId_spinner = findViewById(R.id.signUpcity_spinner);
        stateId_spinner =findViewById(R.id.signUpstate_spinner);
        signUp_dobCardView = findViewById(R.id.signUp_dobCardView);
        signUp_password_et = findViewById(R.id.signUp_password_et);
        alreadyRegister_tv = findViewById(R.id.already_register_tv);

        etAadharcard = findViewById(R.id.aadharcard_et);
        etPancard   = findViewById(R.id.pancard_et);



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
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}