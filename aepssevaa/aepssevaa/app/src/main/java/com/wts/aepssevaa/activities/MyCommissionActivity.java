package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.MyPagerAdapter;
import com.wts.aepssevaa.fragments.AepsFragment;
import com.wts.aepssevaa.fragments.DthFragment;
import com.wts.aepssevaa.fragments.MobileFragment;
import com.wts.aepssevaa.fragments.MoneyTransferFragment;
import com.wts.aepssevaa.fragments.PayoutFragment;
import com.wts.aepssevaa.models.MyCommissionModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCommissionActivity extends AppCompatActivity {

    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;
    public static ArrayList<MyCommissionModel> mobileCommissionList, postpaidCommissionList, dthCommissionList, electricityCommissionList, landlineCommissionList, addMoneyOnlineList, waterCommissionList,
            utiCommissionList, gasCommissionList,payOutCommissionList,aepsCommissionList,moneyTransferCommissionList;

    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView backButton;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commission);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyCommissionActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        backButton = findViewById(R.id.back_button_mycommission);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (checkInternetState()) {
            getMyCommission();
        } else {
            showSnackbar();
        }
    }

    private void getMyCommission() {
        final ProgressDialog pDialog = new ProgressDialog(this, R.style.MyTheme);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getMyCommission(ApiController.Auth_key,userId,deviceId,deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            mobileCommissionList = new ArrayList<>();
                         //   postpaidCommissionList = new ArrayList<>();
                            dthCommissionList = new ArrayList<>();
                         //   electricityCommissionList = new ArrayList<>();
                         //   landlineCommissionList = new ArrayList<>();
                         //   addMoneyOnlineList = new ArrayList<>();
                         //   waterCommissionList = new ArrayList<>();
                         //   utiCommissionList = new ArrayList<>();
                         //   gasCommissionList = new ArrayList<>();
                            payOutCommissionList = new ArrayList<>();
                            aepsCommissionList = new ArrayList<>();
                            moneyTransferCommissionList = new ArrayList<>();


                            myPagerAdapter.addFragment(new PayoutFragment(),"Payout");
                            myPagerAdapter.addFragment(new AepsFragment(),"Aeps");
                            myPagerAdapter.addFragment(new MoneyTransferFragment(),"Money Transfer");
                            myPagerAdapter.addFragment(new MobileFragment(),"Mobile");
                            myPagerAdapter.addFragment(new DthFragment(),"DTH");
                         //   myPagerAdapter.addFragment(new PostpaidFragment(),"Postpaid");
                         //   myPagerAdapter.addFragment(new ElectricityFragment(),"Electricity");
                         //   myPagerAdapter.addFragment(new GasFragment(),"Gas");
                         //   myPagerAdapter.addFragment(new LandlineFragment(),"Landline");
                         //   myPagerAdapter.addFragment(new AddMoneyOnlineFragment(),"Add Money Online");
                         //   myPagerAdapter.addFragment(new WaterFragment(),"Water");
                         //   myPagerAdapter.addFragment(new UtiFragment(),"UTI");

                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String service = transactionObject.getString("ServiceName");
                                String operator=transactionObject.getString("OperatorName");
                                String commPer=transactionObject.getString("Commission");
                                String chargePer=transactionObject.getString("Surcharge");


//                                if (service.equalsIgnoreCase("Gas")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    gasCommissionList.add(myCommissionModel);
//
//                                }

//                                if (service.equalsIgnoreCase("LANDLINE")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    landlineCommissionList.add(myCommissionModel);
//
//
//                                }

                                if (service.equalsIgnoreCase("MOBILE")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    mobileCommissionList.add(myCommissionModel);

                                }

                                if (service.equalsIgnoreCase("DTH")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    dthCommissionList.add(myCommissionModel);


                                }

                                if (service.equalsIgnoreCase("Payouts")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    payOutCommissionList.add(myCommissionModel);


                                }

                                if (service.equalsIgnoreCase("AEPS")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    aepsCommissionList.add(myCommissionModel);


                                }

                                if (service.equalsIgnoreCase("Money Transfer")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    moneyTransferCommissionList.add(myCommissionModel);


                                }

//                                if (service.equalsIgnoreCase("PostPaid(BBPS)")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    postpaidCommissionList.add(myCommissionModel);
//
//
//                                }

//                                if (service.equalsIgnoreCase("Electricity")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    electricityCommissionList.add(myCommissionModel);
//
//
//
//                                }

//                                if (service.equalsIgnoreCase("AddMoneyOnline")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    addMoneyOnlineList.add(myCommissionModel);
//
//
//                                }

//                                if (service.equalsIgnoreCase("Water")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    waterCommissionList.add(myCommissionModel);
//
//
//
//                                }

//                                if (service.equalsIgnoreCase("UTI online")) {
//                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
//                                    myCommissionModel.setOperator(operator);
//                                    myCommissionModel.setChargePer(chargePer);
//                                    myCommissionModel.setCommPer(commPer);
//
//                                    utiCommissionList.add(myCommissionModel);
//
//
//
//                                }
                                pDialog.dismiss();
                                viewPager.setAdapter(myPagerAdapter);
                                tabLayout.setupWithViewPager(viewPager);

                            }
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(MyCommissionActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            Toast.makeText(MyCommissionActivity.this, "Commission not set ", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(MyCommissionActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(MyCommissionActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
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

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_commission_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}