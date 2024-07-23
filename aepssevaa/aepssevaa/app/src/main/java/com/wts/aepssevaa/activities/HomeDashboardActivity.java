package com.wts.aepssevaa.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.wts_aeps.ui.WtsAepsHome;
import wts.com.wts_aeps.utils.WtsAepsConstants;


public class HomeDashboardActivity extends AppCompatActivity {
    public static final String EXTRA_NAME = "com.wts.aepssevaa.com.extra.NAME";
    public static final String EXTRA_SERVICE_NAME = "com.wts.aepssevaa.com.extra.ServiceNAME";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle; // except this all view are defined in xml
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;
    ImageSlider imageSlider;
    String storeDevice, username, userPassword,deviceInfo,userid,deviceId,userTypeId,userMobileNum ,loginUser,userType;
    String userBal;
    LinearLayout  prepaidLayout, dthLayout,dmtLayout,reportsLayoutBottom , walletLayoutBottom,supportLayoutBottom,addMoneyOnline,aeps2Layout,aeps1Layout,electricityLayout,
            moveToBankLayout,bottomNavBar,gasLayout,waterBillLayout,postpaidLayout,fasTagLayout,loanLayout,nsdlLayout,insuranceLayout,distributorLayout;
    LinearLayout creditLayoutDash,debitLayoutDash ,addUserLayoutDash ,viewUserLayoutDash ,creditReportLayoutDash ,debitReportLayoutDash , fundTransferLayoutDash ,myCommissionLayoutDash,walletEnquiryLayoutDash,utiPancardLayout;
    TextView tvWalletMainBalance,tvAepsBalance;
    EditText oldPassword, newPasswordRecover, confirmPassword;
    Button changePassword_btn;
    TextView changePasswordCancel,newsTv;
    AlertDialog alertDialog;
    String enteredOldPassword, enteredNewPassword;
    ConstraintLayout dashboardLayout;
    ImageView imgMainRefresh,imgMainRefresh2,imgWhatsapp,imgInquiry;
    Animation rotateAnimation;
    boolean isUp; //  for bottomNavigation auto hide and show
    String iMage1,iMage2,iMage3;
    String supportNumber =null;
    String aepsBalance="0.00";
    //below for aeps2
    String lat = "0.0", longi = "0.0";
    FusedLocationProviderClient mFusedLocationClient;
    String pan;
    int PERMISSION_ID = 44;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            launchNewWtsAeps();
        }
    };
    //aeps2 end
    LinearLayout  homeLayoutBottom;
    ImageView imgMenu ,imgHomeBtm, imgWalletBtm,imgCallSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this);
        storeDevice = sharedPreferences.getString("deviceInfo", null);
        username = sharedPreferences.getString("loginUsername", null);
        userPassword = sharedPreferences.getString("loginPassword", null);
        userid = sharedPreferences.getString("userid", null); // Save User id which we get from response
        deviceId = sharedPreferences.getString("deviceId",null);
        userTypeId = sharedPreferences.getString("userTypeId",null);
        userMobileNum = sharedPreferences.getString("mobileNo",null);
        loginUser = sharedPreferences.getString("username",null);
        userType = sharedPreferences.getString("usertype", null);
        pan = sharedPreferences.getString("panCard", null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(HomeDashboardActivity.this);

        rotateAnimation= AnimationUtils.loadAnimation(HomeDashboardActivity.this,R.anim.rotate);

        if (userTypeId.equalsIgnoreCase("6")) {

            distributorLayout.setVisibility(View.GONE);

        } else {

            distributorLayout.setVisibility(View.VISIBLE);

        }

        handleNavigationMenu();

        /// Go to Mobile recharge  screen after click on Mobile recharge icon
        prepaidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkServiceStatus("22");

            }
        });

        // added by shuaib for auto hide and show bottom Navigation  when click on dashboard
        // bottomNavBar.setVisibility(View.INVISIBLE);
        // isUp = false;

        dashboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*

                if (isUp) {
                    slideDown(bottomNavBar);

                } else {
                    slideUp(bottomNavBar);
                }
                isUp = !isUp;

                 */

            }
        });


        // End bottom navigation auto hide and show when click on dashboard

        electricityLayout.setOnClickListener(view -> {
            /*

           checkServiceStatus("24");

             */


        });

        moveToBankLayout.setOnClickListener(view -> {
            /*

          // checkServiceStatus("59");
            checkServiceStatus("3043");   // paysprint settlement

             */

        });

        fasTagLayout.setOnClickListener(view -> {

            /*
            Intent intent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "FASTAG");
            intent.putExtra("serviceId", "17");
            startActivity(intent);
             */

        });

        insuranceLayout.setOnClickListener(view -> {

          //  checkServiceStatus("72");

        });


        loanLayout.setOnClickListener(v->
        {
            /*

            Intent intent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
            intent.putExtra("service", "Loan Repayment");
            intent.putExtra("serviceId", "9");
            startActivity(intent);

            */

        });

        nsdlLayout.setOnClickListener(view ->
        {
            /*

            Intent intent = new Intent(HomeDashboardActivity.this, NsdlPanCardActivity.class);
            intent.putExtra("service", "Nsdl_PanCard");
            intent.putExtra("serviceId", "0");
            startActivity(intent);

            */

        });

        imgWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+918210347376&text= Hello ,need help "));
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
            /*
        imgInquiry.setOnClickListener(view ->
        {
            showSupportDialog();
        });
        */


        /// Go to DTH recharge  screen after click on Dth recharge icon
        dthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkServiceStatus("32");
            }
        });

        dmtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*

             //  checkServiceStatus("3061");  //  paysprint dmt
                checkServiceStatus("23");  //  paysprint dmt

                 */
            }
        });

        gasLayout.setOnClickListener(view -> {

          //  checkServiceStatus("71");

        });

        waterBillLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*

                Intent intent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
                intent.putExtra("service", "Water");
                intent.putExtra("serviceId", "16");
                startActivity(intent);
                 */
            }
        });

        postpaidLayout.setOnClickListener(view -> {

           // checkServiceStatus("25");
        });

        homeLayoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(HomeDashboardActivity.this, "Home", Toast.LENGTH_SHORT).show();

            }

        });

        reportsLayoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(HomeDashboardActivity.this, AllReportsActivity.class));

              //  startActivity(new Intent(HomeDashboardActivity.this, ReportsActivity.class));

            }
        });

        walletLayoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeDashboardActivity.this, WalletActivity.class));

            }
        });


        supportLayoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSupportDialog();

            }
        });

        // Go to Add Money Online screen  Generate QrCode after click on addMoneyOnline  icon
        addMoneyOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  checkServiceStatus("3057");

            }
        });

        aeps2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  checkServiceStatus("3031");

              //  checkServiceStatus("3156");  //  Ecko aeps with kyc

                checkServiceStatus("4026");  //  credo aeps working

            }
        });

        aeps1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*  //  partner key and partner id need
                // // aeps1 not add , we add QrCodeScanActivity .
                // Aeps1 need to be implemented .

              //  startActivity(new Intent(HomeDashboardActivity.this, QrCodeScannerActivity.class));

                checkServiceStatus("3022");  //  paysprint aeps with kyc
                */

            }
        });

        creditLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userType.equalsIgnoreCase("retailer")) {

                    Intent intent = new Intent(HomeDashboardActivity.this, CreditDebitBalanceActivity.class);
                    intent.putExtra("title", "Credit Balance");
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeDashboardActivity.this, "Retailer cannot use this service.", Toast.LENGTH_LONG).show();
                }

            }
        });

        debitLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userType.equalsIgnoreCase("retailer")) {

                    Intent intent = new Intent(HomeDashboardActivity.this, CreditDebitBalanceActivity.class);
                    intent.putExtra("title", "Debit Balance");
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeDashboardActivity.this, "Retailer cannot use this service.", Toast.LENGTH_LONG).show();
                }
            }
        });

        addUserLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashboardActivity.this, AddUserActivity.class));
                }
                else {
                    Toast.makeText(HomeDashboardActivity.this, "Retailer cannot use this service.", Toast.LENGTH_LONG).show();

                }

            }
        });

        viewUserLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashboardActivity.this, ViewCustomerActivity.class));

                }
                else {
                    Toast.makeText(HomeDashboardActivity.this, "Retailer cannot use this service.", Toast.LENGTH_LONG).show();

                }

            }
        });

        creditReportLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeDashboardActivity.this, CreditDebitReportActivity.class);
                intent.putExtra("title","Credit Report");
                startActivity(intent);

            }
        });

        debitReportLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(HomeDashboardActivity.this, CreditDebitReportActivity.class);
                intent.putExtra("title","Debit Report");
                startActivity(intent);
            }
        });

        fundTransferLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeDashboardActivity.this, FundTransferActivity.class));
            }
        });

        myCommissionLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(HomeDashboardActivity.this, MyCommissionActivity.class);
                intent.putExtra("service","all");
                startActivity(intent);

            }
        });

        utiPancardLayout.setOnClickListener(view ->
        {
          //  getUpiPancardStatus();
        });

        walletEnquiryLayoutDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeDashboardActivity.this, WalletActivity.class));

            }
        });
       /*
        imgMainRefresh.setColorFilter(Color.WHITE);
        imgMainRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMainRefresh.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    imgMainRefresh.setColorFilter(Color.YELLOW);
                    getWalletBalance();
                } else {
                    showSnackbar();
                }
            }
        });
        imgMainRefresh2.setColorFilter(Color.WHITE);
        imgMainRefresh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMainRefresh2.startAnimation(rotateAnimation);
                if (checkInternetState()) {
                    imgMainRefresh2.setColorFilter(Color.YELLOW);
                    getAepsBalance();
                } else {
                    showSnackbar();
                }
            }
        });

        */
    }

    private void getUpiPancardStatus() {

            ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Please wait ....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.show();

            Call<JsonObject> call = ApiController.getInstance().getApi().utiPancardStatus(ApiController.Auth_key, userid, storeDevice, deviceId);

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {

                        pDialog.dismiss();

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(String.valueOf(response.body()));

                            String statuscode = jsonObject.getString("statuscode");

                            if (statuscode.equalsIgnoreCase("TXN")) {

                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                JSONObject dataObject = dataArray.getJSONObject(0);
                                String vleId = dataObject.getString("VleId");
                                String vleName = dataObject.getString("VleName");

                                Intent in = new Intent(HomeDashboardActivity.this, GenerateUtiPancardActivity.class);
                                in.putExtra("vleId", vleId);
                                in.putExtra("vleName", vleName);
                                in.putExtra("psaCreated", "psaCreated");
                                startActivity(in);

                            } else if (statuscode.equalsIgnoreCase("ERR")) {

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Alert")
                                        .setMessage(jsonObject.getString("data"))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent in = new Intent(HomeDashboardActivity.this, GenerateUtiPancardActivity.class);
                                                in.putExtra("psaCreated", "psaNotCreated");
                                                startActivity(in);

                                            }
                                        }).show();

                            } else {
                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setMessage("Something went wrong.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }

                        } catch (JSONException e) {
                            new AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    pDialog.dismiss();
                    new AlertDialog.Builder(HomeDashboardActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
    }

    private void showSupportDialog() {

            final View view1 = LayoutInflater.from(HomeDashboardActivity.this).inflate(R.layout.support_dialog, null, false);
            final AlertDialog builder = new AlertDialog.Builder(HomeDashboardActivity.this).create();
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.setView(view1);
            builder.show();

            LinearLayout callLayoutOne, callLayoutTwo, callLayoutThree, whatsAppLayout, mailLayout, wwwLayout;
            callLayoutOne = view1.findViewById(R.id.call_layout1);
            callLayoutTwo = view1.findViewById(R.id.call_layout2);
            callLayoutThree = view1.findViewById(R.id.call_layout3);
            whatsAppLayout = view1.findViewById(R.id.whats_app_layout);
            mailLayout = view1.findViewById(R.id.mail_layout);
            wwwLayout = view1.findViewById(R.id.www_layout);

            callLayoutOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportNumber ="+91-7779822793";
                    openCaller(supportNumber);
                }
            });

            callLayoutTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportNumber ="+91-7779822793";
                    openCaller(supportNumber);
                }
            });

            callLayoutThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportNumber ="+91-7779822793";
                    openCaller(supportNumber);
                }
            });

            whatsAppLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+918210347376&text= Hello ,need help "));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            mailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] emailArray={"aepsseva@yahoo.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL,emailArray);
                    intent.setType("message/rfs822");
                    startActivity(Intent.createChooser(intent,"Choose Email Client"));
                }
            });

        wwwLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://aepsseva.in/"));
                startActivity(browserIntent);
                builder.dismiss();
            }
        });
        }

    private  void openCaller(String supportNumber) {
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+supportNumber));
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkCredentialUser();
        getWalletBalance();
        setNews(userTypeId);
        newsTv.setSelected(true);
        setSlider();
        getAepsBalance();
        // getAllNotification();
    }
    private void getAepsBalance() {

        Call<JsonObject> call =ApiController.getInstance().getApi().getAepsBalance(ApiController.Auth_key,userid,deviceId,storeDevice,"Login","");
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
                            aepsBalance = jsonObject.getString("userBalance");
                            tvAepsBalance.setText("₹ " + aepsBalance +"/-");

                        }  else {
                            tvAepsBalance.setText("₹ 00.00"+ "/-");
                        }

                    } catch (JSONException e) {

                        tvAepsBalance.setText("₹ 00.00"+ "/-");
                        e.printStackTrace();
                    }

                } else {
                    tvAepsBalance.setText("₹ 00.00"+ "/-");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                tvAepsBalance.setText(" ₹ 00.00"+ "/-");
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                lat = location.getLatitude() + "";
                                longi = location.getLongitude() + "";
                                launchNewWtsAeps();
                            }
                        }
                );
            } else {
                Toast.makeText(HomeDashboardActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(HomeDashboardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private void setNews(String userTypeId) {
        Call<JsonObject> call = ApiController.getInstance().getApi().getNews(ApiController.Auth_key,userid,deviceId,storeDevice,userTypeId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try{
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String news = jsonObject.getString("News");
                            newsTv.setText(news);

                        }else{

                            newsTv.setText(" You Can Update Latest News Here : Welcome to AepsSeva ....");

                        }

                    }catch (JSONException e) {

                        e.printStackTrace();
                        newsTv.setText(" You Can Update Latest News Here : Welcome to AepsSeva ....");

                    }
                }else
                {
                    newsTv.setText(" You Can Update Latest News Here : Welcome to AepsSeva ....");
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                newsTv.setText(" You Can Update Latest News Here : Welcome to AepsSeva ....");
            }
        });
    }
    private void initViews() {

        prepaidLayout = findViewById(R.id.prepaid_layout);
        dthLayout = findViewById(R.id.dth_layout);
        tvWalletMainBalance = findViewById(R.id.tv_bal);
        imageSlider = findViewById(R.id.image_slider);
        newsTv = findViewById(R.id.news_header);
        imgMenu = findViewById(R.id.img_menu);
        homeLayoutBottom = findViewById(R.id.home_layout_bottom);
        imgHomeBtm  = findViewById(R.id.imgHomeBtm);
        imgWalletBtm = findViewById(R.id.imgWalletBtm);
        imgCallSupport = findViewById(R.id.imgCallSupport);
        dmtLayout = findViewById(R.id.dmt_layout);
        reportsLayoutBottom = findViewById(R.id.reports_layout_bottom);
        walletLayoutBottom = findViewById(R.id.wallet_layout_bottom);
        supportLayoutBottom = findViewById(R.id.support_layout_bottom);
        tvAepsBalance = findViewById(R.id.tv_aeps_balance);
        addMoneyOnline = findViewById(R.id.add_moneyqrcode_layout_bottom);
        aeps2Layout    = findViewById(R.id.fino_aeps_layout);
        electricityLayout = findViewById(R.id.electricity_layout);
        moveToBankLayout = findViewById(R.id.settlement_layout);
        bottomNavBar     = findViewById(R.id.bottom_nav_bar);
        dashboardLayout = findViewById(R.id.dashboard_layout);
        gasLayout =       findViewById(R.id.gas_layout);
        waterBillLayout   = findViewById(R.id.water_layout);
        postpaidLayout = findViewById(R.id.postpaid_layout);
        fasTagLayout = findViewById(R.id.fastag_layout);
        loanLayout   =findViewById(R.id.loan_layout);
        creditLayoutDash  =findViewById(R.id.credit_layout_dash);
        debitLayoutDash  =findViewById(R.id.debit_layout_dash);
        addUserLayoutDash  =findViewById(R.id.add_user_layout_dash);
        viewUserLayoutDash  =findViewById(R.id.view_user_layout_dash);
        creditReportLayoutDash  =findViewById(R.id.credit_report_layout_dash);
        debitReportLayoutDash  =findViewById(R.id.debit_report_layout_dash);
        fundTransferLayoutDash  =findViewById(R.id.fund_transfer_layout_dash);
        myCommissionLayoutDash  =findViewById(R.id.my_commission_layout_dash);
        walletEnquiryLayoutDash  =findViewById(R.id.wallet_enquiries);
        utiPancardLayout = findViewById(R.id.utiPancardLY);
      //  imgMainRefresh = findViewById(R.id.img_refresh);
      //  imgMainRefresh2 = findViewById(R.id.img_refresh2);
        imgWhatsapp = findViewById(R.id.img_whatsapp);
       // imgInquiry = findViewById(R.id.img_faq);
        aeps1Layout    = findViewById(R.id.icici_aeps_layout);
        nsdlLayout = findViewById(R.id.nsdlPancardLY);
        insuranceLayout =findViewById(R.id.insurance_layout);
        distributorLayout = findViewById(R.id.distributor_layout);

    }
    private void setSlider() {
        Call<JsonObject> call = ApiController.getInstance().getApi().getBanner(ApiController.Auth_key,userid,deviceId,storeDevice);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try{
                    JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                    String statusCode = jsonObject.getString("statuscode");
                    if (statusCode.equalsIgnoreCase("TXN")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<SlideModel> slideModels = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            iMage1 =  jsonObject1.getString("Banner1").replaceAll("~","");
                            iMage2 =  jsonObject1.getString("Banner2").replaceAll("~","");;
                            iMage3 =  jsonObject1.getString("Banner3").replaceAll("~","");;
                        }

                        if (iMage1.equalsIgnoreCase("N/A"))
                        {
                            slideModels.add(new SlideModel(R.drawable.banner_latest, ScaleTypes.FIT));
                        }
                        else
                        {
                            slideModels.add(new SlideModel("http://www.login.aepsseva.in/"+iMage1, ScaleTypes.FIT));
                        }
                        if (iMage2.equalsIgnoreCase("N/A"))
                        {
                            slideModels.add(new SlideModel(R.drawable.aepsbanner, ScaleTypes.FIT));
                        }
                        else
                        {
                            slideModels.add(new SlideModel("http://www.login.aepsseva.in/"+iMage2, ScaleTypes.FIT));
                        }

                        if (iMage3.equalsIgnoreCase("N/A"))
                        {
                            slideModels.add(new SlideModel(R.drawable.aeps_baneer_new, ScaleTypes.FIT));
                        }
                        else
                        {
                            slideModels.add(new SlideModel("http://www.login.aepsseva.in/"+iMage3, ScaleTypes.FIT));
                        }
                        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    }
                    else
                    {
                        List<SlideModel> slideModels = new ArrayList<>();
                        slideModels.add(new SlideModel(R.drawable.banner_latest, ScaleTypes.FIT));
                        slideModels.add(new SlideModel(R.drawable.aepsbanner, ScaleTypes.FIT));
                        slideModels.add(new SlideModel(R.drawable.aeps_baneer_new, ScaleTypes.FIT));
                        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {


            }
        });
    }

    public void getWalletBalance() {
        Call<JsonObject> call = ApiController.getInstance().getApi().getBalance(ApiController.Auth_key, userid, deviceId, storeDevice, "Login", "0");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONObject responseObject = jsonObject.getJSONObject("data");

                            userBal = responseObject.getString("userBalance");
                            tvWalletMainBalance.setText("₹ " + userBal + "/-");
                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            String data = jsonObject.getString("data");
                            new android.app.AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setMessage(data)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this).edit();
                                            editor.clear();
                                            editor.apply();

                                            Intent intent = new Intent(HomeDashboardActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();
                        }

                        else {

                            tvWalletMainBalance.setText("₹" + 0.00 + "/-");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvWalletMainBalance.setText("₹" + 0.00 + "/-");
                    }
                } else {
                    tvWalletMainBalance.setText("₹" + 0.00 + "/-");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                tvWalletMainBalance.setText("₹" + 0.00 + "/-");
            }
        });
    }

    private void checkCredentialUser() {

        ProgressDialog progressDialog = new ProgressDialog(HomeDashboardActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait ....");
        progressDialog.show();

        Call<JsonObject> call = ApiController.getInstance()
                .getApi().checkCredential(ApiController.Auth_key, username, userPassword, storeDevice);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String newUserId = jsonObject1.getString("userID");
                            SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this);
                            SharedPreferences.Editor editor = shp.edit();
                            editor.putString("userid", newUserId);
                            editor.apply();

                        } else // if another user Login then checkCredential() give  statuscode is "ERR" and we automatically Logout from first user
                        {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Another  User Login , Please Login Again ...")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this).edit();
                                            editor.clear();
                                            editor.apply();

                                            Intent intent = new Intent(HomeDashboardActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Alert")
                                .setMessage("Another  User Login , Please Login Again ...")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this).edit();
                                        editor.clear();
                                        editor.apply();

                                        Intent intent = new Intent(HomeDashboardActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                    }


                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(HomeDashboardActivity.this)
                            .setTitle("Alert")
                            .setMessage("Another  User Login , Please Login Again ...")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this).edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(HomeDashboardActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(HomeDashboardActivity.this)
                        .setTitle("Alert")
                        .setMessage("Another  User Login , Please Login Again ...")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this).edit();
                                editor.clear();
                                editor.apply();

                                Intent intent = new Intent(HomeDashboardActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();

            }
        });


    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.dashboard_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void changePassword() {
        View v = LayoutInflater.from(HomeDashboardActivity.this).inflate(R.layout.change_password_status_dialog, null, false);

        oldPassword = v.findViewById(R.id.oldPassword);
        newPasswordRecover = v.findViewById(R.id.newPasswordRecover);
        confirmPassword = v.findViewById(R.id.confirmPassword);
        changePassword_btn = v.findViewById(R.id.changePassword_btn);
        changePasswordCancel = v.findViewById(R.id.changePasswordCancel);

        alertDialog = new AlertDialog.Builder(HomeDashboardActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        alertDialog.show();

        changePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        changePassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changePasswordCheckInput()) {

                    enteredOldPassword = oldPassword.getText().toString().trim();
                    enteredNewPassword = newPasswordRecover.getText().toString();
                    changePasswordRecover(enteredOldPassword, enteredNewPassword);
                }
            }
        });
    }

    private void changePasswordRecover(String enteredOldPassword, String enteredNewPassword) {
        ProgressDialog pd = new ProgressDialog(HomeDashboardActivity.this);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.setMessage("Loading ...");
        pd.show();
        Call<JsonObject> call = ApiController.getInstance()
                .getApi().changePassword(ApiController.Auth_key,userid,deviceId,storeDevice,enteredOldPassword,enteredNewPassword);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try{
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject.getString("statuscode");
                        String status = jsonObject.getString("status");
                        String data = jsonObject.getString("data");

                        if(statusCode.equalsIgnoreCase("TXN"))
                        {
                            pd.dismiss();

                            new AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setTitle(status)
                                    .setMessage(data)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            recreate();
                                        }
                                    })
                                    .show();
                        }
                        else

                        {
                            pd.dismiss();
                            new AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .show();
                    }
                }
                else
                {
                    pd.dismiss();
                    new AlertDialog.Builder(HomeDashboardActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
                            .show();
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pd.dismiss();

                new AlertDialog.Builder(HomeDashboardActivity.this)
                        .setTitle("Alert")
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(HomeDashboardActivity.this)
                .setMessage("Do you want to logout?")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor ed = sharedPreferences.edit();
                        ed.clear();
                        ed.apply();
                        startActivity(new Intent(HomeDashboardActivity.this, LoginActivity.class));
                        finish();
                    }
                }).show();
    }

    public boolean changePasswordCheckInput() {

        if (!TextUtils.isEmpty(oldPassword.getText())) {
            if (!TextUtils.isEmpty(newPasswordRecover.getText())) {

                if (!TextUtils.isEmpty(confirmPassword.getText())) {
                    if (newPasswordRecover.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
                        return true;
                    } else {
                        newPasswordRecover.setError( "Password and Confirm Password must be same");
                        Toast.makeText(this, "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    confirmPassword.setError("Please enter Confirm Password.");
                    return false;
                }
            } else {
                newPasswordRecover.setError("Please enter New Password.");
                return false;
            }
        } else {
            oldPassword.setError("Please enter Password.");
            return false;
        }
    }
    public void checkServiceStatus(String servicePermissionId) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please Wait ....");
        pd.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pd.setCancelable(false);
        pd.show();

        Call<JsonObject> call = ApiController.getInstance()
                .getApi().checkServiceStatus(ApiController.Auth_key,userid,deviceId,storeDevice,servicePermissionId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            switch (servicePermissionId) {

                                case "22":
                                    pd.dismiss();

                                    Intent intent = new Intent(HomeDashboardActivity.this, RechargeActivity.class);
                                    intent.putExtra("service", "Mobile");
                                    startActivity(intent);

                                    break;

                                case "32":
                                    pd.dismiss();

                                    Intent dthIntent = new Intent(HomeDashboardActivity.this, RechargeActivity.class);
                                    dthIntent.putExtra("service", "Dth");
                                    startActivity(dthIntent);
                                    break;

                                case "3057":
                                    pd.dismiss();

                                    startActivity(new Intent(HomeDashboardActivity.this, AddMoneyOnlineActivity.class));
                                    break;

                                case "3031":
                                    pd.dismiss();
                                    getLastLocation();
                                    break;

                                case "24":

                                    pd.dismiss();
                                    Intent electricityIntent = new Intent(HomeDashboardActivity.this , ElectricityActivity.class);
                                    electricityIntent.putExtra("service", "Electricity");
                                    electricityIntent.putExtra("serviceId", "5");   //online
                                    startActivity(electricityIntent);
                                    break;

                                case "23":
                                    pd.dismiss();
                                    /*
                                    Intent dmtIntent = new Intent(HomeDashboardActivity.this, SenderValidationActivity.class);
                                    dmtIntent.putExtra("title", "Money Transfer");
                                    startActivity(dmtIntent);
                                    */
                                    Intent dmtPaysIntent = new Intent(HomeDashboardActivity.this, PaysprintSenderValidationActivity.class);
                                    dmtPaysIntent.putExtra("title", "Money Transfer3");
                                    startActivity(dmtPaysIntent);


                                    break;


                                case "3061":
                                    pd.dismiss();

                                    /*
                                    Intent dmtPaysIntent = new Intent(HomeDashboardActivity.this, PaysprintSenderValidationActivity.class);
                                    dmtPaysIntent.putExtra("title", "Money Transfer3");
                                    startActivity(dmtPaysIntent);
                                     */

                                    break;

                                case "71":
                                    pd.dismiss();
                                    Intent gastagintent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
                                    gastagintent.putExtra("service", "GAS");
                                    gastagintent.putExtra("serviceId", "12");
                                    startActivity(gastagintent);
                                    break;

                                case "72":
                                    pd.dismiss();
                                    Intent healthintent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
                                    healthintent.putExtra("service", "HEALTH INSURANCE");
                                    healthintent.putExtra("serviceId", "13");
                                    startActivity(healthintent);
                                    break;

                                case "25":
                                    pd.dismiss();
                                    Intent PostpaidIntent = new Intent(HomeDashboardActivity.this, ElectricityActivity.class);
                                    PostpaidIntent.putExtra("service", "PostPaid");
                                    PostpaidIntent.putExtra("serviceId", "6");
                                    startActivity(PostpaidIntent);
                                    break;

                                case "59":

                                    pd.dismiss();
                                    startActivity(new Intent(HomeDashboardActivity.this , SettlementActivity.class));
                                    break;

                                case "3022":
                                    pd.dismiss();

                                    checkKycStatus();

                                    break;

                                case "4026":  //  credo aeps
                                    pd.dismiss();

                                    checkKycCredoStatus();

                                    break;

                                case "3156":
                                    pd.dismiss();

                                    checkEkoKycStatus();  //  Eko Aeps new

                                    break;

                                case "3043":

                                    pd.dismiss();
                                    // paysprint settlement added changes BankName parameter extra added in "paySprintMoveToBank" controller
                                    // backup from pvmss but change moveToBank to paySprintMoveToBank (backup from PayETSuperAdmin)
                                    startActivity(new Intent(HomeDashboardActivity.this, PayspirntSettlementActivity.class));
                                    break;

                                default:
                                    pd.dismiss();

                                    Toast.makeText(HomeDashboardActivity.this, "Default", Toast.LENGTH_SHORT).show();

                                    break;

                            }

                        } else {
                            pd.dismiss();
                            new AlertDialog.Builder(HomeDashboardActivity.this)
                                    .setTitle("Service Permission")
                                    .setMessage(jsonObject.getString("data"))
                                    .setPositiveButton("OK", null).show();
                        }

                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Service Permission")
                                .setMessage("Something went wrong")
                                .setPositiveButton("OK", null).show();
                    }
                } else {
                    pd.dismiss();
                    new AlertDialog.Builder(HomeDashboardActivity.this)
                            .setTitle("Service Permission")
                            .setMessage("Something went wrong")
                            .setPositiveButton("OK", null).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pd.dismiss();
                new AlertDialog.Builder(HomeDashboardActivity.this)
                        .setTitle("Service Permission")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null).show();
            }
        });
    }

    private void checkKycCredoStatus() {
            ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = ApiController.getInstance().getApi().checkCredoserKycStatus(ApiController.Auth_key, userid, deviceId, storeDevice);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("data");

                            Intent intent;
                            if (responseCode.equalsIgnoreCase("true")) {

                                pDialog.dismiss();

                                checkTwofactorAuthenticationCredo();


                            } else if (responseCode.equalsIgnoreCase("false")) {

                                String message = responseObject.getString("status");
                                /*

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Message")
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show();
                                */

                                Intent intentCredo = new Intent(HomeDashboardActivity.this, CredoOnboardActivity.class);
                                intentCredo.putExtra("service", "CredoOnBoard");
                                startActivity(intentCredo);

                                pDialog.dismiss();
                            }

                            else if (responseCode.equalsIgnoreCase("DO_KYC")) {

                                String message = responseObject.getString("status");
                                /*

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Message")
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show();
                                */

                                Intent intentCredo = new Intent(HomeDashboardActivity.this, CredoAepsKycActivity.class);
                                intentCredo.putExtra("service", "CredoAepsKyc");
                                startActivity(intentCredo);

                                pDialog.dismiss();
                            }
                            // startActivity(intent);

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Message")
                                .setMessage(response.message())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                }
            });





    }

    private void checkTwofactorAuthenticationCredo() {

            ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = ApiController.getInstance().getApi().checkTwoFactorCredoAuthStatus(ApiController.Auth_key, userid, deviceId, storeDevice);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");
                            String responsedata = responseObject.getString("data");

                            if(responseCode.equalsIgnoreCase("TXN")){

                                pDialog.dismiss();

                                if (responsedata.equalsIgnoreCase("true")) {

                                    Intent intent = new Intent(HomeDashboardActivity.this, PaySprintActivityBank2.class);
                                    //  intent.putExtra("balance", userBal);
                                    intent.putExtra("balance", aepsBalance);
                                    startActivity(intent);


                                } else if(responsedata.equalsIgnoreCase("false")){

                                    pDialog.dismiss();

                                    Intent intent = new Intent(HomeDashboardActivity.this, TwoFactorAuthenticationOnBoardBankActivity.class);
                                    intent.putExtra("balance", aepsBalance);
                                    intent.putExtra("title","TwoFactorAuthDaily");
                                    intent.putExtra("appId","");
                                    startActivity(intent);

                                }


                            } else if(responseCode.equalsIgnoreCase("ERR")){

                                pDialog.dismiss();

                                String message = responseObject.getString("status");
                                String data = responseObject.getString("data");

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setCancelable(false)
                                        .setTitle(data)
                                        .setMessage(message)
                                        .setPositiveButton("Okay",null)
                                        .show();

                            }
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Message")
                                .setMessage(response.message())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                }
            });


    }


    private void checkEkoKycStatus() {

            ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = ApiController.getInstance().getApi().checkUserKycEkoStatus(ApiController.Auth_key, userid, deviceId, storeDevice);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("data");

                            Intent intent;
                            if (responseCode.equalsIgnoreCase("true")) {

                                intent = new Intent(HomeDashboardActivity.this, EkoAepsActivity.class);
                                intent.putExtra("balance", aepsBalance);
                                // intent.putExtra("balance", balance);
                                startActivity(intent);

                            } else {

                                // String message = responseObject.getString("data");

                                String message = responseObject.getString("status");
                                pDialog.dismiss();
                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Message")
                                        .setMessage("First OnBoard From Web  ")
                                        .setPositiveButton("OK", null)
                                        .show();

                            }

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Message")
                                .setMessage(response.message())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                }
            });


    }


    private void checkKycStatus() {

        ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().checkUserKycStatus(ApiController.Auth_key, userid, deviceId, storeDevice);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("data");

                        Intent intent;
                        if (responseCode.equalsIgnoreCase("true")) {
                                /*
                                intent = new Intent(HomeDashboardActivity.this, PaySprintActivity.class);
                                intent.putExtra("balance", aepsBalance);
                                // intent.putExtra("balance", balance);
                                startActivity(intent);
                                */

                            // below is for Two Factor Authentication for Paysprint Aeps before doing Transaction
                            pDialog.dismiss();

                            checkTwofactorAuthentication();


                        } else {
                            intent = new Intent(HomeDashboardActivity.this, OnBoardPaySprintActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
                        }
                        // startActivity(intent);

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(HomeDashboardActivity.this)
                            .setTitle("Message")
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
            }
        });


    }

    private void checkTwofactorAuthentication() {

            ProgressDialog pDialog = new ProgressDialog(HomeDashboardActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = ApiController.getInstance().getApi().checkTwoFactorAuthStatus(ApiController.Auth_key, userid, deviceId, storeDevice);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");
                            String responsedata = responseObject.getString("data");

                            if(responseCode.equalsIgnoreCase("TXN")){

                                if (responsedata.equalsIgnoreCase("true")) {

                                    pDialog.dismiss();
                                    Intent intent = new Intent(HomeDashboardActivity.this, PaySprintActivity.class);
                                    intent.putExtra("balance", aepsBalance);
                                    startActivity(intent);


                                } else if(responsedata.equalsIgnoreCase("false")){

                                    pDialog.dismiss();
                                    Intent intent = new Intent(HomeDashboardActivity.this, TwoFactorAuthenticationActivity.class);
                                    intent.putExtra("balance", aepsBalance);
                                    intent.putExtra("title","TwoFactorOnboard");
                                    intent.putExtra("appId","");
                                    startActivity(intent);

                                }
                                else  if (responsedata.equalsIgnoreCase("TFA"))
                                {
                                    String message = responseObject.getString("status");
                                    String data = responseObject.getString("data");

                                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashboardActivity.this)
                                            .setCancelable(false)
                                            .setTitle(data+" Please Proceed ..")
                                            .setNegativeButton("No", null)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Intent intent = new Intent(HomeDashboardActivity.this, TwoFactorAuthenticationActivity.class);
                                                    intent.putExtra("balance", aepsBalance);
                                                    intent.putExtra("title","TwoFactorAuth");
                                                    intent.putExtra("appId","");
                                                    startActivity(intent);

                                                }
                                            }).show();
                                    pDialog.dismiss();

                                }


                            } else if(responseCode.equalsIgnoreCase("ERR")){

                                pDialog.dismiss();

                                String message = responseObject.getString("status");
                                String data = responseObject.getString("data");

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setCancelable(false)
                                        .setTitle(data)
                                        .setMessage(message)
                                        .setPositiveButton("Okay",null)
                                        .show();

                            }
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(HomeDashboardActivity.this)
                                .setTitle("Message")
                                .setMessage(response.message())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                }
            });

    }


    @SuppressLint("SetTextI18n")
    private void handleNavigationMenu() {

        Dialog navMenuDialog = new Dialog(HomeDashboardActivity.this, R.style.DialogTheme);
        navMenuDialog.setContentView(R.layout.navigation_menu);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.72);
      //  int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 1.0);

        navMenuDialog.getWindow().setLayout(width, height);

        navMenuDialog.getWindow().setGravity(Gravity.START);
        navMenuDialog.getWindow().setWindowAnimations(R.style.SlidingNavDialog);

        imgMenu.setOnClickListener(view -> {

            if (!navMenuDialog.isShowing()) {
                navMenuDialog.show();
            }
            TextView tvOwnerName = navMenuDialog.findViewById(R.id.tv_owner_name);
            TextView tvUserType = navMenuDialog.findViewById(R.id.tv_userType);
            Button btnLogout = navMenuDialog.findViewById(R.id.btn_logout);
            LinearLayout changePasswordLayout = navMenuDialog.findViewById(R.id.change_password_layout);
            LinearLayout profileLayout = navMenuDialog.findViewById(R.id.profile_layout);
            LinearLayout creditLayout = navMenuDialog.findViewById(R.id.credit_layout);
            LinearLayout debitLayout = navMenuDialog.findViewById(R.id.debit_layout);
            LinearLayout reportLayout = navMenuDialog.findViewById(R.id.nav_RechargeReport_layout);
            LinearLayout fundRequestLayout = navMenuDialog.findViewById(R.id.fund_transfer_layout);
            LinearLayout addUserLayout = navMenuDialog.findViewById(R.id.add_user_layout);
            LinearLayout viewUserLayout = navMenuDialog.findViewById(R.id.view_user_layout);
            LinearLayout homeLayout = navMenuDialog.findViewById(R.id.home_layout);
            LinearLayout debitReportLayout = navMenuDialog.findViewById(R.id.nav_debitReport_layout);
            LinearLayout creditReportLayout = navMenuDialog.findViewById(R.id.nav_creditReport_layout);
            LinearLayout ledgerReportLayout = navMenuDialog.findViewById(R.id.nav_ledgerReport_layout);
            LinearLayout myCommissionLayout = navMenuDialog.findViewById(R.id.nav_my_commission_layout);
            LinearLayout bankDetailsLayout = navMenuDialog.findViewById(R.id.bank_details_layout);
            LinearLayout adminQrCodeLayout = navMenuDialog.findViewById(R.id.admin_qrcode_layout);

            ImageView imgBack = navMenuDialog.findViewById(R.id.back_img);

            tvOwnerName.setText(loginUser);
            tvUserType.setText(userType);

            homeLayout.setOnClickListener(v->
            {
                navMenuDialog.dismiss();
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            });

            changePasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    changePassword();

                }
            });
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navMenuDialog.dismiss();
                }
            });

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashboardActivity.this, ProfileActivity.class));

                }
            });
            reportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashboardActivity.this, ReportsActivity.class));

                }
            });

            if (userType.equalsIgnoreCase("retailer")) {
                addUserLayout.setVisibility(View.GONE);
                creditLayout.setVisibility(View.GONE);
                debitLayout.setVisibility(View.GONE);
                viewUserLayout.setVisibility(View.GONE);
            }

            addUserLayout.setOnClickListener(v ->
            {
                navMenuDialog.dismiss();
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashboardActivity.this, AddUserActivity.class));
                }
            });

            viewUserLayout.setOnClickListener(v ->
            {
                navMenuDialog.dismiss();
                if (!userType.equalsIgnoreCase("retailer")) {
                    startActivity(new Intent(HomeDashboardActivity.this, ViewCustomerActivity.class));

                }
            });

            creditLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();

                    if (!userType.equalsIgnoreCase("retailer")) {

                        Intent intent = new Intent(HomeDashboardActivity.this, CreditDebitBalanceActivity.class);
                        intent.putExtra("title", "Credit Balance");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeDashboardActivity.this, "You can not use this service.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            debitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    if (!userType.equalsIgnoreCase("retailer")) {

                        Intent intent = new Intent(HomeDashboardActivity.this, CreditDebitBalanceActivity.class);
                        intent.putExtra("title", "Debit Balance");
                        startActivity(intent);
                    } else {

                        Toast.makeText(HomeDashboardActivity.this, "You can not use this service.", Toast.LENGTH_LONG).show();

                    }
                }
            });


            fundRequestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    startActivity(new Intent(HomeDashboardActivity.this, FundTransferActivity.class));
                }
            });

            debitReportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(HomeDashboardActivity.this, CreditDebitReportActivity.class);
                    intent.putExtra("title","Debit Report");
                    startActivity(intent);

                }
            });

            creditReportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(HomeDashboardActivity.this, CreditDebitReportActivity.class);
                    intent.putExtra("title","Credit Report");
                    startActivity(intent);
                }
            });

            ledgerReportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(HomeDashboardActivity.this, LedgerReportActivity.class);
                    intent.putExtra("title","Ledger Report");
                    startActivity(intent);
                }
            });

            myCommissionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();
                    Intent intent=new Intent(HomeDashboardActivity.this, MyCommissionActivity.class);
                    intent.putExtra("service","all");
                    startActivity(intent);
                }
            });

            bankDetailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navMenuDialog.dismiss();

                    startActivity(new Intent(HomeDashboardActivity.this, BankDetailsActivity.class));

                }
            });

            adminQrCodeLayout.setOnClickListener(v -> {
                navMenuDialog.dismiss();
                Intent intent = new Intent(HomeDashboardActivity.this, AdminQrCodeActivity.class);
                startActivity(intent);
            });



            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navMenuDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashboardActivity.this)
                            .setCancelable(false)
                            .setMessage("Do you want to logout?")
                            .setTitle("User Logout")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashboardActivity.this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(HomeDashboardActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).show();
                }
            });
        });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(HomeDashboardActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }
    public void launchNewWtsAeps() {
        Intent intent = new Intent(HomeDashboardActivity.this, WtsAepsHome.class );
        intent.putExtra(WtsAepsConstants.AEPS_BALANCE, aepsBalance);
        intent.putExtra(WtsAepsConstants.API_KEY, "/7NnOH+cKHMB5rJTKdsGEeJ1u0oX1kHEyhR2hwnx3n1g97aXiN+8Aw=="); // given by anas
        intent.putExtra(WtsAepsConstants.SECURITY_KEY, "nVdpaIqZLcrKjfQKL/xkI+jlr5VH3X415Eucy5Wjpl0="); // give by anas
        intent.putExtra(WtsAepsConstants.PAN_NO, pan);
        intent.putExtra(WtsAepsConstants.MERCHANT_CODE, userMobileNum);
        intent.putExtra(WtsAepsConstants.APP_ID, "236"); // given by anas
        intent.putExtra(WtsAepsConstants.LATITUDE, lat);
        intent.putExtra(WtsAepsConstants.LONGITUDE, longi);
        startActivity(intent);
    }
    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.dashboard_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}