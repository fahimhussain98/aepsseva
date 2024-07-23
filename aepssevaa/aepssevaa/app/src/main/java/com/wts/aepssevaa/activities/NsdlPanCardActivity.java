package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NsdlPanCardActivity extends AppCompatActivity {
    Spinner spinnerGender,spinner_application_type,spinner_pancard_type,spinner_pancard_mode;
    ArrayList<String> genderList,applicationTypeList,panCardTypeList,panCardModeList;
    String selectedGender="Select Gender",selectedApplicationType="Select Application Type",selectedPanCardType="Select PanCard Type";
    String selectedPanCardMode="Select PanCard Mode";
    String serviceType, serviceId;
    SharedPreferences panShp;
    String userid,deviceInfo,deviceId,userType,mobileNo,panNo,emailId,aadharNo,name,address;
    String selectedGenderId="",selectedApplicationTypeId="",selectedPanCardTypeId="",selectedPanCardModeId="";
    Button panBtn;
    EditText etTitle,etFirstName,etMiddleName,etLastName,etMobile,etPanEmailId;
    ImageView panBackImg;
    String receivedStatus,receivedMessage,authorization;
    String OperatorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsdl_pan_card);
        initViews();

        serviceType = getIntent().getStringExtra("service");
        serviceId = getIntent().getStringExtra("serviceId");

        panShp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userid = panShp.getString("userid", null);
        deviceInfo = panShp.getString("deviceInfo", null);
        deviceId = panShp.getString("deviceId", null);
        userType = panShp.getString("usertype", null);
        mobileNo = panShp.getString("mobileNo", null);
        panNo = panShp.getString("panCard", null);
        emailId = panShp.getString("emailId", null);
        aadharNo = panShp.getString("aadhaarCard", null);
        name = panShp.getString("username", null);
        address = panShp.getString("address", null);

        panBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        genderList =new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Transgender");

        HintSpinner<String> hintSpinner2 = new HintSpinner<>(
                spinnerGender,
                new HintAdapter<String>(NsdlPanCardActivity.this, "Select Gender", genderList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedGender = genderList.get(position);

                        if (selectedGender.equalsIgnoreCase("Male")) {
                            selectedGenderId= "M";

                        }
                        else
                        if (selectedGender.equalsIgnoreCase("Female")) {
                            selectedGenderId= "F";

                        }
                        else if(selectedGender.equalsIgnoreCase("Transgender")){
                            selectedGenderId= "T";

                        }

                    }
                });
        hintSpinner2.init();

        applicationTypeList =new ArrayList<>();
        applicationTypeList.add("PAN-India Citizen(Form49A)");
        applicationTypeList.add("Changes/Correction/Reprint PAN Application");


        HintSpinner<String> hintSpinner3 = new HintSpinner<>(
                spinner_application_type,
                new HintAdapter<String>(NsdlPanCardActivity.this, "Select Application Type", applicationTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedApplicationType = applicationTypeList.get(position);

                        if (selectedApplicationType.equalsIgnoreCase("PAN-India Citizen(Form49A)")) {
                            selectedApplicationTypeId= "49A";

                        }
                        else
                        if (selectedApplicationType.equalsIgnoreCase("Changes/Correction/Reprint PAN Application")) {
                            selectedApplicationTypeId= "CR";

                        }

                    }
                });
        hintSpinner3.init();

        panCardTypeList =new ArrayList<>();
        panCardTypeList.add("Both Physical PAN and e-PAN");
        panCardTypeList.add("Only e-PAN");


        HintSpinner<String> hintSpinner4 = new HintSpinner<>(
                spinner_pancard_type,
                new HintAdapter<String>(NsdlPanCardActivity.this, "Select PanCard Type", panCardTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPanCardType = panCardTypeList.get(position);

                        if (selectedPanCardType.equalsIgnoreCase("Both Physical PAN and e-PAN")) {

                            selectedPanCardTypeId= "Y";

                        }
                        else
                        if (selectedPanCardType.equalsIgnoreCase("Only e-PAN")) {
                            selectedPanCardTypeId= "N";

                        }

                    }
                });
        hintSpinner4.init();

        panCardModeList =new ArrayList<>();
        panCardModeList.add("Instant PAN Application through e-Kyc");
        panCardModeList.add("Scan based Pan application");


        HintSpinner<String> hintSpinner5 = new HintSpinner<>(
                spinner_pancard_mode,
                new HintAdapter<String>(NsdlPanCardActivity.this, "Select PanCard Mode", panCardModeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPanCardMode = panCardModeList.get(position);

                        if (selectedPanCardMode.equalsIgnoreCase("Instant PAN Application through e-Kyc")) {
                            selectedPanCardModeId= "EKYC";
                            //  OperatorId ="1258"; //  anas said
                            OperatorId ="314"; //  anas said


                        }
                        else
                        if (selectedPanCardMode.equalsIgnoreCase("Scan based Pan application")) {
                            selectedPanCardModeId= "ESIGN";
                            // OperatorId ="1259"; //  anas said
                            OperatorId ="315"; //  anas said

                        }

                    }
                });
        hintSpinner5.init();

        panBtn.setOnClickListener(v -> {
            if (checkInternetState()) {
                if (checkInputs()) {

                    requestPanRegistration();

                }
                else {
                    showSnackbar("All fields are mandatory.");
                }
            } else {
                showSnackbar("No Internet");
            }
        });


    }

    private void requestPanRegistration() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading ....");
        pd.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pd.setCancelable(false);
        pd.show();

        String title = etTitle.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String mobileNum = etMobile.getText().toString().trim();
        String   emailId = etPanEmailId.getText().toString().trim();

        Call<JsonObject> call = ApiController.getInstance().getApi().requestNsdlPanCard(ApiController.Auth_key,userid,deviceId,deviceInfo,title,firstName,middleName,lastName,selectedApplicationTypeId,selectedPanCardTypeId,selectedPanCardModeId,selectedGenderId,emailId,mobileNum,OperatorId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        String status = jsonObject.getString("status");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            receivedStatus = jsonObject.getString("status");
                            authorization = jsonObject.getString("data");

                            pd.dismiss();
                            openNsdlApp();
//                            new AlertDialog.Builder(NsdlPanCardActivity.this)
//                                    .setTitle(receivedStatus)
//                                    .setMessage(authorization)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            finish();
//                                        }
//                                    })
//                                    .setCancelable(false)
//                                    .show();



                        }

                        else if (statusCode.equalsIgnoreCase("ERR"))
                        {
                            pd.dismiss();
                            new AlertDialog.Builder(NsdlPanCardActivity.this)
                                    .setTitle("Message")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .setMessage(jsonObject.getString("data"))
                                    .setCancelable(false)
                                    .show();
                        }

                        else {
                            pd.dismiss();
                            new AlertDialog.Builder(NsdlPanCardActivity.this)
                                    .setTitle("Message")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .setMessage("Something went wrong")
                                    .setCancelable(false)
                                    .show();
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                        new AlertDialog.Builder(NsdlPanCardActivity.this)
                                .setTitle("Something went wrong")
                                .setMessage(e.getMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }

                }
                else
                {
                    pd.dismiss();
                    new AlertDialog.Builder(NsdlPanCardActivity.this)
                            .setMessage("Something went wrong ")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setTitle("Message")
                            .setCancelable(false)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                pd.dismiss();
                new AlertDialog.Builder(NsdlPanCardActivity.this)
                        .setMessage("Something went wrong ")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setTitle("Message")
                        .setCancelable(false)
                        .show();

            }
        });
    }


    private void initViews() {
        spinnerGender = findViewById(R.id.spinner_gender);
        spinner_application_type = findViewById(R.id.spinner_application_type);
        spinner_pancard_type = findViewById(R.id.spinner_pancard_type);
        spinner_pancard_mode = findViewById(R.id.spinner_pancard_mode);
        panBtn = findViewById(R.id.panBtn);
        etTitle = findViewById(R.id.userpan_title_et);
        etFirstName = findViewById(R.id.username_first_et);
        etMiddleName = findViewById(R.id.username_middle_et);
        etLastName = findViewById(R.id.username_last_et);
        etMobile = findViewById(R.id.panmobile_et);
        etPanEmailId = findViewById(R.id.pan_emailId_et);
        panBackImg =findViewById(R.id.panBackImg);

    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.NsdlLayout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private boolean checkInputs() {
        return  !TextUtils.isEmpty(etMobile.getText()) && !TextUtils.isEmpty(etFirstName.getText())
                &&!TextUtils.isEmpty(etPanEmailId.getText());

    }

    private void openNsdlApp(){
        try {
            Intent intent = new Intent();
            intent.setClassName("com.nsdl.panservicedriver",
                    "com.nsdl.panservicedriver.MainActivity");

            intent.putExtra("authorization", authorization);
            intent.putExtra("show_receipt", true);
            startActivityForResult(intent, 1001); //By the help of android startActivityForResult() method, we can get result from another activity
        }catch (Exception e){
            appNotInstall();
        }

    }

    private void appNotInstall() {

        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(NsdlPanCardActivity.this).create();
        final LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.device_not_connected, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout ic_close = convertView.findViewById(R.id.ic_close);
        TextView tag_line = convertView.findViewById(R.id.tag_line);
        TextView device_name = convertView.findViewById(R.id.device_name);
        Button done_btn = convertView.findViewById(R.id.done_btn);
        ImageView image_set = convertView.findViewById(R.id.image_set);
        tag_line.setText("Application not installed!");
        device_name.setText("Nsdl PanService" + "application has not been installed in your phone.Please install this  application for Pan Service.");
        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        done_btn.setText("Download App");
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://play.google.com/store/apps/details?id=com.nsdl.panservicedriver";

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(convertView);
        alertDialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // override the onActivityResult method that is invoked automatically when second activity returns result
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();

//                if (data != null) {
//                        String rdata = data.getStringExtra("data");
//                   //   String rdata = data.getStringExtra("encrypted_data");
//                   //   Bundle bundle = data.getExtras();
//                  }

                if (bundle != null) {

                    String rdata = bundle.getString("data","");
                    String rdata2 = bundle.getString("encrypted_data","");

                    Toast.makeText(this, rdata, Toast.LENGTH_SHORT).show();

                    new android.app.AlertDialog.Builder(NsdlPanCardActivity.this)
                            .setTitle("Message")
                            .setMessage("Response : "+rdata)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();

                }

            }
        }

    }

}