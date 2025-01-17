package com.wts.aepssevaa.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.wts_aeps.scannerDevice.Opts;
import wts.com.wts_aeps.scannerDevice.PidOptions;


public class PaySprintActivityBank2 extends AppCompatActivity {

    LinearLayout transactionTypeLayout, userDetailLayout, deviceLayout;

    String userId, mobileNo, lat = "0.0", longi = "0.0", deviceId, deviceInfo;

    FusedLocationProviderClient mFusedLocationClient;

    TextView tvBalance;

    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
    LinearLayout cashWithDrawLayout, balanceEnquiryLayout, miniStateLayout, aadharPayLayout;
    ImageView imgCashWithdraw, imgBalanceEnquiry, imgMiniStatement, imgAadharPay;
    TextView tvCashWithdraw, tvBalanceEnquiry, tvMiniStatement, tvAadharPay;
    public static String selectedTransactionType = "select";
    AppCompatButton btnProceedTransactionType;
    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////
    RadioGroup rgFirst, rgSecond;
    String selectedBankName = "select", selectedBankIIN;
    RadioButton rbSbi, rbPnb, rbAxis, rbIcici, rbHdfc, rbUnion, rbKotak, rbYesBank;
    TextView tvBankName, tv500, tv1000, tv2000, tv3000, tv5000;
    EditText etAmount, etMobile, etAadharCard;
    Button btnProceedUserDetail;
    CheckBox ckbTermsAndCondition;
    LinearLayout fastAmountLayout;
    TextInputLayout tilAmount;

    ArrayList<String> bankNameArrayList, bankIINArrayList;
    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    ImageView imgMorpho, imgStartek, imgMantra, imgEvolute,imgVriddhi;
    LinearLayout morphoLayout, startekLayout, mantraLayout, evoluteLayout,vriddhiLayout;
    TextView tvMorpho, tvMantra, tvStartek, tvEvolute,tvVriddhi;
    AppCompatButton btnProceedDeviceLayout;

    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

    ////////////////FINGER PRINT DATA/////////////////////
    Serializer serializer = null;
    String pidData = null;
    ////////////////FINGER PRINT DATA/////////////////////
    SharedPreferences sharedPreferences;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_sprint_bank2);

        initViews();

        //////CHANGE COLOR OF STATUS BAR///////////////
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PaySprintActivityBank2.this, R.color.black));
        //////CHANGE COLOR OF STATUS BAR///////////////

        //////////////////////////////////////////////////////////////////

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PaySprintActivityBank2.this);
        userId = sharedPreferences.getString("userid", null);
        mobileNo = sharedPreferences.getString("mobileNo", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
//        String balance = getIntent().getStringExtra("balance");
//        tvBalance.setText("₹ " + balance);

        activity = PaySprintActivityBank2.this;

        etMobile.setText(mobileNo);
        //////////////////////////////////////////////////////////////////

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PaySprintActivityBank2.this);
        getBankList();
        transactionTypeLayoutListeners();
        userDetailLayoutListeners();
        deviceLayoutListeners();

        //////////////////////////////////////////////
        serializer = new Persister();
        //////////////////////////////////////////////
    }

    /**
     * FIRST STEP
     * selects transaction type(Cash withdraw,Balance enquiry etc. and then proceed to Second Step.
     * after proceed hide TRANSACTION TYPE LAYOUT and show USER DETAILS LAYOUT
     */
    private void transactionTypeLayoutListeners() {
        cashWithDrawLayout.setOnClickListener(view -> {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_selected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.teal_200));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));

            selectedTransactionType = "cw";

        });

        balanceEnquiryLayout.setOnClickListener(view -> {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_selected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.teal_200));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));
            selectedTransactionType = "be";
        });

        miniStateLayout.setOnClickListener(view -> {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_selected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.teal_200));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));

            selectedTransactionType = "ms";
        });

        aadharPayLayout.setOnClickListener(view -> {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_selected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.teal_200));

            selectedTransactionType = "m";
        });

        btnProceedTransactionType.setOnClickListener(view -> {
            if (!selectedTransactionType.equalsIgnoreCase("select")) {
                transactionTypeLayout.setVisibility(View.GONE);
                userDetailLayout.setVisibility(View.VISIBLE);
                if (selectedTransactionType.equalsIgnoreCase("be") || selectedTransactionType.equalsIgnoreCase("ms")) {
                    tilAmount.setVisibility(View.GONE);
                    fastAmountLayout.setVisibility(View.GONE);
                } else {
                    tilAmount.setVisibility(View.VISIBLE);
                    fastAmountLayout.setVisibility(View.VISIBLE);
                }

            } else {
                showMessageDialog("Hey!!! You forgot to select transaction type.");
            }
        });
    }

    /**
     * SECOND STEP
     * get Aadhar details from user input and then proceed to third step.
     * after proceed hide USER DETAIL LAYOUT and show DEVICE LAYOUT
     */
    private void userDetailLayoutListeners() {

        rgFirst.clearCheck();
        rgSecond.clearCheck();

        rgFirst.setOnCheckedChangeListener(checkChangeListenerFirst);
        rgSecond.setOnCheckedChangeListener(checkChangeListenerSecond);

        setFastSelectAmount();

        btnProceedUserDetail.setOnClickListener(v ->
        {
            if (checkUserDetails()) {
                userDetailLayout.setVisibility(View.GONE);

                // below added by shuaib for each transaction 2FA 8 - 1- 24
                if(selectedTransactionType.equalsIgnoreCase("cw")){


                    // below changes after javed sir said 10 -2- 24

                    Toast.makeText(this, "Merchant 2FA Require", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaySprintActivityBank2.this, TwoFactorAuthenticationCWActivity.class);
                    intent.putExtra("title","TwoFactorAuth");
                    intent.putExtra("appId","");

                    startActivity(intent);

                    deviceLayout.setVisibility(View.VISIBLE);

                }
                else {

                    deviceLayout.setVisibility(View.VISIBLE);
                }

                /// 10 - 1- 24


                deviceLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * THIRD STEP
     * get device name from user and get pid data from RD Service
     * after that do final transaction
     */
    private void deviceLayoutListeners() {

        mantraLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_selected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.teal_200));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Mantra";
        });

        startekLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_selected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.teal_200));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Startek";
        });

        morphoLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_selected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.teal_200));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "Morpho";
        });

        evoluteLayout.setOnClickListener(view ->
        {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_selected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.teal_200));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "Evolute";
        });

        vriddhiLayout.setOnClickListener(view ->
        {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_selected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.teal_200));

            // selectedDevice = "Vriddhi";
            selectedDevice = "MantraL1";
        });


        btnProceedDeviceLayout.setOnClickListener(view -> {
            if (!selectedDevice.equalsIgnoreCase("select")) {
                //startActivity(new Intent(PaySprintActivity.this,AepsTransactionActivity.class));
                String packageName = null;
                if (selectedDevice.equalsIgnoreCase("Morpho"))
                    packageName = "com.scl.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Startek"))
                    packageName = "com.acpl.registersdk";
                else if (selectedDevice.equalsIgnoreCase("Mantra"))
                    packageName = "com.mantra.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Evolute"))
                    packageName = "com.evolute.rdservice";


                else if (selectedDevice.equalsIgnoreCase("MantraL1"))
                    // packageName = "com.nextbiometrics.onetouchrdservice";
                    packageName =  "com.mantra.mfs110.rdservice";

                try {

                    String pidOption = getPIDOptions();
                    Intent intent2 = new Intent();
                    intent2.setPackage(packageName);
                    intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                    intent2.putExtra("PID_OPTIONS", pidOption);
                    startActivityForResult(intent2, 1);
                } catch (Exception e) {
                    showMessageDialog("Please install " + selectedDevice + " Rd Service first.");
                }
            } else {
                showMessageDialog("Please Select Your Device");
            }
        });
    }

    private String getPIDOptions() {

        try {
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "2";
            opts.format = "0";
            opts.timeout = "15000";
            opts.wadh = "";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.pidVer = "2.0";
            opts.env = "P";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }

    private void getBankList() {
        ProgressDialog progressDialog = new ProgressDialog(PaySprintActivityBank2.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getBankForAeps(ApiController.Auth_key, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            bankNameArrayList = new ArrayList<>();
                            bankIINArrayList = new ArrayList<>();

                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String bankName = transactionObject.getString("BankName");
                                String bankIIN = transactionObject.getString("IinNo");

                                bankNameArrayList.add(bankName);
                                bankIINArrayList.add(bankIIN);
                            }

                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog bankDialog = new SpinnerDialog(PaySprintActivityBank2.this, bankNameArrayList, "Select Bank", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    bankDialog.setCancellable(true); // for cancellable
                                    bankDialog.setShowKeyboard(false);// for open keyboard by default
                                    bankDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            selectedBankIIN = bankIINArrayList.get(position);

                                            rgFirstChangeCheckedState();
                                            rgSecondChangeCheckedState();
                                        }
                                    });

                                    bankDialog.showSpinerDialog();
                                }
                            });

                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(PaySprintActivityBank2.this)
                                    .setCancelable(false)
                                    .setMessage("Please Try After Sometime.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(PaySprintActivityBank2.this)
                                .setCancelable(false)
                                .setMessage("Please Try After Sometime.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(PaySprintActivityBank2.this)
                            .setCancelable(false)
                            .setMessage("Please Try After Sometime.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(PaySprintActivityBank2.this)
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private boolean checkUserDetails() {
        if (etMobile.getText().toString().length() == 10) {
            if (!selectedBankName.equalsIgnoreCase("select")) {
                if (etAadharCard.getText().toString().trim().length() == 12) {
                    if (selectedTransactionType.equalsIgnoreCase("be") || selectedTransactionType.equalsIgnoreCase("ms")) {
                        if (ckbTermsAndCondition.isChecked()) {
                            hideKeyBoard();
                            return true;
                        } else {
                            showMessageDialog("Please accept terms and condition to continue.");
                            return false;
                        }
                    } else {
                        if (!TextUtils.isEmpty(etAmount.getText())) {
                            if (ckbTermsAndCondition.isChecked()) {
                                hideKeyBoard();
                                return true;
                            } else {
                                showMessageDialog("Please accept terms and condition to continue.");
                                return false;
                            }
                        } else {
                            showMessageDialog("Please Enter Amount");
                            return false;
                        }
                    }

                } else {
                    showMessageDialog("Please enter valid aadhar number.");
                    return false;
                }
            } else {
                showMessageDialog("Please select your bank.");
                return false;
            }
        } else {
            showMessageDialog("Please enter valid mobile number.");
            return false;
        }
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFastSelectAmount() {
        tv500.setOnClickListener(view -> {
            etAmount.setText("500");
        });

        tv1000.setOnClickListener(view -> {
            etAmount.setText("1000");
        });

        tv2000.setOnClickListener(view -> {
            etAmount.setText("2000");
        });

        tv3000.setOnClickListener(view -> {
            etAmount.setText("3000");
        });

        tv5000.setOnClickListener(view -> {
            etAmount.setText("5000");
        });
    }

    @SuppressLint("SetTextI18n")
    private void getBankNameFromRadioButtons() {
        if (rbSbi.isChecked()) {
            selectedBankName = "SBI";
            tvBankName.setText("State Bank Of India");
            selectedBankIIN = "607094";

        } else if (rbPnb.isChecked()) {
            selectedBankName = "PNB";
            tvBankName.setText("Punjab National Bank");
            selectedBankIIN = "607027";
        } else if (rbAxis.isChecked()) {
            selectedBankName = "AXIS";
            tvBankName.setText("AXIS Bank");
            selectedBankIIN = "607153";
        } else if (rbIcici.isChecked()) {
            selectedBankName = "ICICI";
            tvBankName.setText("ICICI Bank");
            selectedBankIIN = "508534";

        } else if (rbHdfc.isChecked()) {
            selectedBankName = "HDFC";
            tvBankName.setText("HDFC Bank");
            selectedBankIIN = "607152";
        } else if (rbUnion.isChecked()) {
            selectedBankName = "Union";
            tvBankName.setText("Union Bank Of India");
            selectedBankIIN = "607161";
        } else if (rbKotak.isChecked()) {
            selectedBankName = "Kotak";
            tvBankName.setText("Kotak Mahindra");
            selectedBankIIN = "990309";
        } else if (rbYesBank.isChecked()) {
            selectedBankName = "Yes Bank";
            tvBankName.setText("Yes Bank");
            selectedBankIIN = "607618";
        }

    }

    private final RadioGroup.OnCheckedChangeListener checkChangeListenerFirst = (radioGroup, checkedId) -> {
        if (checkedId != -1) {
            rgFirstChangeCheckedState();

        }
        getBankNameFromRadioButtons();
    };

    private void rgFirstChangeCheckedState() {
        rgSecond.setOnCheckedChangeListener(null);
        rgSecond.clearCheck();
        rgSecond.setOnCheckedChangeListener(checkChangeListenerSecond);
    }


    private final RadioGroup.OnCheckedChangeListener checkChangeListenerSecond = (radioGroup, checkedId) -> {
        if (checkedId != -1)
            rgSecondChangeCheckedState();
        getBankNameFromRadioButtons();

    };

    private void rgSecondChangeCheckedState() {
        rgFirst.setOnCheckedChangeListener(null);
        rgFirst.clearCheck();
        rgFirst.setOnCheckedChangeListener(checkChangeListenerFirst);
    }

    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(PaySprintActivityBank2.this).create();
        final LayoutInflater inflater = LayoutInflater.from(PaySprintActivityBank2.this);
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCancelable(false);
        messageDialog.setView(convertView);

        ImageView imgClose = convertView.findViewById(R.id.img_close);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        Button btnTryAgain = convertView.findViewById(R.id.btn_try_again);

        imgClose.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        btnTryAgain.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        tvMessage.setText(message);

        messageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result;
        //List<Param> params = new ArrayList<>();
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    result = data.getStringExtra("PID_DATA");
                    if (result.contains("Device not ready")) {
                        showMessageDialog("Device Not Connected");
                    } else {
                        //if (result.contains("srno") && result.contains("rdsId") && result.contains("rdsVer")) {
                        if (result.contains("rdsId") && result.contains("rdsVer")) {
                            try {
                                pidData = result;

                                getLastLocation();
                               /* pidDataStr = pidData._Data.value;
                                Log.d("xml_data_show", pidDataStr);
                                sessionKey = pidData._Skey.value;
                                hmac = pidData._Hmac;
                                dpId = pidData._DeviceInfo.dpId;
                                rdsId = pidData._DeviceInfo.rdsId;
                                rdsVer = pidData._DeviceInfo.rdsVer;
                                dc = pidData._DeviceInfo.dc;
                                mc = pidData._DeviceInfo.mc;
                                mi = pidData._DeviceInfo.mi;
                                errCode = pidData._Resp.errCode;
                                errInfo = pidData._Resp.errInfo;
                                errCode = pidData._Resp.errCode;
                                fcount = pidData._Resp.fCount;
                                qScore = pidData._Resp.qScore;
                                nmPoints = pidData._Resp.nmPoints;
                                ci = pidData._Skey.ci;
                                params = pidData._DeviceInfo.add_info.params;
                                for (int i = 0; i < params.size(); i++) {
                                    String name = params.get(i).name;
                                    if (name.equalsIgnoreCase("srno")) {
                                        serialNo = params.get(i).value;
                                        Log.e("serialNu", serialNo);
                                    } else if (name.equalsIgnoreCase("sysid")) {
                                        String systemId = params.get(i).value;
                                        Log.e("systemId", systemId);
                                    }
                                }
                                //getTransactionNow();
                                tvData.setText(result);
                                Toast.makeText(MainActivity.this, "Data Collected Successfully", Toast.LENGTH_SHORT).show();*/
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessageDialog("There are some issues please contact to administration.");
                            }
                        } else {
                            showMessageDialog("Device Not Connected");
                        }
                    }

                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude() + "";
                                    longi = location.getLongitude() + "";
                                    //launchWTSAEPS();
                                    doAepsTransaction();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(PaySprintActivityBank2.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(PaySprintActivityBank2.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(PaySprintActivityBank2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(PaySprintActivityBank2.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PaySprintActivityBank2.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            //launchWTSAEPS();
            doAepsTransaction();

        }
    };

    private void doAepsTransaction() {
        ProgressDialog progressDialog = new ProgressDialog(PaySprintActivityBank2.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...Don't press back button");
        progressDialog.show();

        String aadharNo = etAadharCard.getText().toString().trim();
        String amount ;  //  changes by shuaib after sudheer said 11 - 5- 24
        String custMobile = etMobile.getText().toString().trim();  //  added by shuaib client want cust Mobile

        if (selectedTransactionType.equalsIgnoreCase("ms"))   //  added by shuaib after sudheer said 11- 5- 24
        {

             amount = "0";  // when we select txn type ms the amount = 0

            //  when Use mini statement in aeps credo pay aeps use ms = 0  amount is zero and aadhar pay not use in credo  aeps

        }
        else
        {
             amount = etAmount.getText().toString().trim();
        }

        Call<JsonObject> call = ApiController.getInstance().getApi().doAepsTransactionCredo(ApiController.Auth_key, userId, aadharNo, selectedBankIIN, amount,
                selectedTransactionType, pidData, lat, longi, custMobile, deviceId, deviceInfo);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            String responseAmount = null;
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String transactionType = transactionObject.getString("transactionType");
                                String bankName = transactionObject.getString("bankname");
                                String responseMobileNumber = transactionObject.getString("mobileno");
                                String responseAadharNumber = transactionObject.getString("aadharno");
                                String responseBankRRN = transactionObject.getString("bankRrno");
                                String transactionId = transactionObject.getString("agentId");
                                String status = transactionObject.getString("transactionStatus");
                                String message = transactionObject.getString("message");

                                if (!transactionType.equalsIgnoreCase("MINI STATEMENT"))
                                    responseAmount = transactionObject.getString("transactionAmount");
                                String transactionDate = transactionObject.getString("transactiondate");

                                String outputDate = null;
                                String outputTime = null;
                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = transactionDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    outputTime = new SimpleDateFormat("hh:mm ").format(time);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                if (transactionType.equalsIgnoreCase("MINI STATEMENT")) {

                                    String accountBalance = transactionObject.getString("accountBalance");

                                    String miniStatement = transactionObject.getString("ministatement");

                                    Intent intent = new Intent(PaySprintActivityBank2.this, MiniStatementPaySprintActivity.class);
                                    intent.putExtra("transactionType", transactionType);
                                    intent.putExtra("bankName", bankName);
                                    intent.putExtra("responseMobileNumber", responseMobileNumber);
                                    intent.putExtra("responseAadharNumber", responseAadharNumber);
                                    intent.putExtra("responseBankRRN", responseBankRRN);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    intent.putExtra("miniStatement", miniStatement);
                                    intent.putExtra("accountBalance", accountBalance);
                                    intent.putExtra("message", message);

                                    startActivity(intent);
                                    //   finish();

                                } else {
                                    String accountBalance = transactionObject.getString("accountBalance");
                                    Intent intent = new Intent(PaySprintActivityBank2.this, AepsTransactionActivityBankwise.class);
                                    // for virddhi printer
                                    //  Intent intent = new Intent(PaySprintActivity.this, AepsTransactionActivityPrint.class);
                                    intent.putExtra("transactionType", transactionType);
                                    intent.putExtra("bankName", bankName);
                                    intent.putExtra("responseMobileNumber", responseMobileNumber);
                                    intent.putExtra("responseAadharNumber", responseAadharNumber);
                                    intent.putExtra("responseBankRRN", responseBankRRN);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    intent.putExtra("accountBalance", accountBalance);
                                    intent.putExtra("message", message);

                                    startActivity(intent);
                                    //  finish();
                                }


                                progressDialog.dismiss();

                            }
                        } else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            showMessageDialog(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        showMessageDialog("Something went wrong.");
                    }
                } else {
                    progressDialog.dismiss();
                    showMessageDialog(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                showMessageDialog(t.getMessage());

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (userDetailLayout.getVisibility() == View.VISIBLE) {
            /*
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));

            selectedTransactionType = "select";
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

             */


            userDetailLayout.setVisibility(View.GONE);
            transactionTypeLayout.setVisibility(View.VISIBLE);
        } else if (deviceLayout.getVisibility() == View.VISIBLE) {
            deviceLayout.setVisibility(View.GONE);
            userDetailLayout.setVisibility(View.VISIBLE);
            /*

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);

            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvMantra.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "select";
            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////

             */


        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
        cashWithDrawLayout = findViewById(R.id.cash_withdraw_layout);
        balanceEnquiryLayout = findViewById(R.id.balance_enquiry_layout);
        miniStateLayout = findViewById(R.id.mini_statement_layout);
        aadharPayLayout = findViewById(R.id.aadhar_pay_layout);

        imgCashWithdraw = findViewById(R.id.img_cash_withdraw);
        imgBalanceEnquiry = findViewById(R.id.img_balance_enquiry);
        imgMiniStatement = findViewById(R.id.img_mini_statement);
        imgAadharPay = findViewById(R.id.img_aadhar_pay);

        tvCashWithdraw = findViewById(R.id.tv_cash_withdraw);
        tvBalanceEnquiry = findViewById(R.id.tv_balance_enquiry);
        tvMiniStatement = findViewById(R.id.tv_mini_statement);
        tvAadharPay = findViewById(R.id.tv_aadhar_pay);

        transactionTypeLayout = findViewById(R.id.transaction_type_layout);

        btnProceedTransactionType = findViewById(R.id.btn_proceed_transaction_type);
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

        userDetailLayout = findViewById(R.id.user_detail_layout);
        rgFirst = findViewById(R.id.rg_first);
        rgSecond = findViewById(R.id.rg_second);


        rbSbi = findViewById(R.id.rb_sbi);
        rbPnb = findViewById(R.id.rb_pnb);
        rbAxis = findViewById(R.id.rb_axis);
        rbIcici = findViewById(R.id.rb_icici);
        rbHdfc = findViewById(R.id.rb_hdfc);
        rbUnion = findViewById(R.id.rb_union);
        rbKotak = findViewById(R.id.rb_kotak);
        rbYesBank = findViewById(R.id.rb_yes_bank);

        tvBankName = findViewById(R.id.tv_bank);

        tv500 = findViewById(R.id.tv_500);
        tv1000 = findViewById(R.id.tv_1000);
        tv2000 = findViewById(R.id.tv_2000);
        tv3000 = findViewById(R.id.tv_3000);
        tv5000 = findViewById(R.id.tv_5000);

        etAmount = findViewById(R.id.et_amount);
        etMobile = findViewById(R.id.et_mobile_number);
        etAadharCard = findViewById(R.id.et_aadhar_number);

        tilAmount = findViewById(R.id.til_amount);

        ckbTermsAndCondition = findViewById(R.id.ckb_terms_condition);

        fastAmountLayout = findViewById(R.id.fast_amount_layout);

        btnProceedUserDetail = findViewById(R.id.btn_proceed_user_details);

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////


        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
        deviceLayout = findViewById(R.id.device_layout);

        imgMorpho = findViewById(R.id.img_morpho);
        imgMantra = findViewById(R.id.img_mantra);
        imgStartek = findViewById(R.id.img_startek);
        imgEvolute = findViewById(R.id.img_evolute);
        imgVriddhi = findViewById(R.id.img_vriddhi);

        morphoLayout = findViewById(R.id.morpho_layout);
        mantraLayout = findViewById(R.id.mantra_layout);
        startekLayout = findViewById(R.id.startek_layout);
        evoluteLayout = findViewById(R.id.evolute_layout);
        vriddhiLayout = findViewById(R.id.vriddhi_layout);

        tvStartek = findViewById(R.id.tv_startek);
        tvMorpho = findViewById(R.id.tv_morpho);
        tvMantra = findViewById(R.id.tv_mantra);
        tvEvolute = findViewById(R.id.tv_evolute);
        tvVriddhi = findViewById(R.id.tv_vriddhi);

        btnProceedDeviceLayout = findViewById(R.id.btn_proceed_device);
        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

        tvBalance = findViewById(R.id.tv_aeps_balance);


    }

    private void getAepsBalance() {


        Call<JsonObject> call = ApiController.getInstance().getApi().getAepsBalance(ApiController.Auth_key, userId, deviceId, deviceInfo, "Login", "");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String aepsBalance = jsonObject.getString("userBalance");
                            tvBalance.setText("₹ " + aepsBalance);

                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void getBalance() {

        Call<JsonObject> call = ApiController.getInstance().getApi().getBalance(ApiController.Auth_key, userId, deviceId, deviceInfo, "Login", "0");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {


                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String balance = jsonObject.getString("userBalance");
                            tvBalance.setText("\u20b9"+balance);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAepsBalance();
        //  getBalance();
    }



}