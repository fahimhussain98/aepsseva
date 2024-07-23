package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.MyPagerAdapter;
import com.wts.aepssevaa.models.PlansModel;
import com.wts.aepssevaa.plansFragments.BsnlValidExtentionFragment;
import com.wts.aepssevaa.plansFragments.ComboFragment;
import com.wts.aepssevaa.plansFragments.Data2Fragment;
import com.wts.aepssevaa.plansFragments.DataFragment;
import com.wts.aepssevaa.plansFragments.FrcFragment;
import com.wts.aepssevaa.plansFragments.FullTTFragment;
import com.wts.aepssevaa.plansFragments.RateCutterFragment;
import com.wts.aepssevaa.plansFragments.RoamingFragment;
import com.wts.aepssevaa.plansFragments.SMSFragment;
import com.wts.aepssevaa.plansFragments.StvFragment;
import com.wts.aepssevaa.plansFragments.TopUpFragment;
import com.wts.aepssevaa.plansFragments.TwoGFragment;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlansActivity extends AppCompatActivity {
    String operator, commcircle, userId, deviceId, deviceInfo;
    public static ArrayList<PlansModel> topUpArrayList;
    public static ArrayList<PlansModel> dataArrayList;
    public static ArrayList<PlansModel> data2ArrayList;
    public static ArrayList<PlansModel> smsArrayList;
    public static ArrayList<PlansModel> comboArrayList;
    public static ArrayList<PlansModel> rateCutterArrayList;
    public static ArrayList<PlansModel> roamingArrayList;
    public static ArrayList<PlansModel> fullTTArrayList;
    public static ArrayList<PlansModel> twoGArrayList;
    public static ArrayList<PlansModel> frcArrayList;
    public static ArrayList<PlansModel> stvArrayList;
    public static ArrayList<PlansModel> bsnlValidExtArrayList;

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    ProgressDialog pDialog;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PlansActivity.this, R.color.teal_700));
        //////CHANGE COLOR OF STATUS BAR

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        operator = getIntent().getStringExtra("operator");
        commcircle = getIntent().getStringExtra("commcircle");
        userId = getIntent().getStringExtra("userId");
        deviceId = getIntent().getStringExtra("deviceId");
        deviceInfo = getIntent().getStringExtra("deviceInfo");

        topUpArrayList = new ArrayList<>();
        dataArrayList = new ArrayList<>();
        data2ArrayList = new ArrayList<>();
        smsArrayList = new ArrayList<>();
        comboArrayList = new ArrayList<>();
        rateCutterArrayList = new ArrayList<>();
        roamingArrayList = new ArrayList<>();
        fullTTArrayList = new ArrayList<>();
        frcArrayList = new ArrayList<>();
        stvArrayList = new ArrayList<>();
        bsnlValidExtArrayList =new ArrayList<>();



        if (checkInternetState()) {
            getPlans();
        } else {
            showSnackbar();
        }
    }
    private void getPlans() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance()
                .getApi().getPlans(ApiController.Auth_key,userId,deviceId,deviceInfo,operator,commcircle);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {


                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String data = responseObject.getString("data");
                        JSONObject dataObject = new JSONObject(data);


                        JSONObject recordsObject = dataObject.getJSONObject("RDATA");

                        if (recordsObject.has("TOPUP")) {

                            JSONArray topUPArray = recordsObject.getJSONArray("TOPUP");
                            myPagerAdapter.addFragment(new TopUpFragment(), "Top Up");
                            for (int i = 0; i < topUPArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = topUPArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");


                                plansModel.setRs(rs);
                                plansModel.setValidityText("Validity");
                                plansModel.setDesc(desc);
                                plansModel.setValidity(validity);

                                topUpArrayList.add(plansModel);

                            }
                        }


                        try {
                            {
                                if (recordsObject.has("FULLTT")) {
                                    myPagerAdapter.addFragment(new FullTTFragment(), "Full TT");
                                    JSONArray fullTTArray = recordsObject.getJSONArray("FULLTT");
                                    for (int i = 0; i < fullTTArray.length(); i++) {
                                        PlansModel plansModel = new PlansModel();

                                        JSONObject topUpData = fullTTArray.getJSONObject(i);
                                        String rs = topUpData.getString("rs");
                                        String desc = topUpData.getString("desc");
                                        String validity = topUpData.getString("validity");

                                        plansModel.setRs(rs);
                                        plansModel.setDesc(desc);
                                        plansModel.setValidityText("Validity");
                                        plansModel.setValidity(validity);

                                        fullTTArrayList.add(plansModel);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            {
                                if (recordsObject.has("2G")) {
                                    myPagerAdapter.addFragment(new TwoGFragment(), "2G");
                                    JSONArray fullTTArray = recordsObject.getJSONArray("2G");
                                    for (int i = 0; i < fullTTArray.length(); i++) {
                                        PlansModel plansModel = new PlansModel();

                                        JSONObject topUpData = fullTTArray.getJSONObject(i);
                                        String rs = topUpData.getString("rs");
                                        String desc = topUpData.getString("desc");
                                        String validity = topUpData.getString("validity");

                                        plansModel.setRs(rs);
                                        plansModel.setDesc(desc);
                                        plansModel.setValidityText("Validity");
                                        plansModel.setValidity(validity);

                                        twoGArrayList.add(plansModel);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }




                        try {
                            {
                                if (recordsObject.has("DATA")) {
                                    JSONArray dataArray = recordsObject.getJSONArray("DATA");
                                    myPagerAdapter.addFragment(new DataFragment(), "DATA");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        PlansModel plansModel = new PlansModel();

                                        JSONObject topUpData = dataArray.getJSONObject(i);
                                        String rs = topUpData.getString("rs");
                                        String desc = topUpData.getString("desc");
                                        String validity = topUpData.getString("validity");

                                        plansModel.setRs(rs);
                                        plansModel.setDesc(desc);
                                        plansModel.setValidityText("Validity");
                                        plansModel.setValidity(validity);

                                        dataArrayList.add(plansModel);

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (recordsObject.has("3G/4G")) {
                                myPagerAdapter.addFragment(new Data2Fragment(), "3G/4G");
                                JSONArray data2Array = recordsObject.getJSONArray("3G/4G");
                                for (int i = 0; i < data2Array.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = data2Array.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    data2ArrayList.add(plansModel);

                                }
                            }
                        } catch (Exception e) {

                        }

                        try {
                            if (recordsObject.has("Romaing")) {
                                myPagerAdapter.addFragment(new RoamingFragment(), "Roaming");
                                JSONArray roamingArray = recordsObject.getJSONArray("Romaing");
                                for (int i = 0; i < roamingArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = roamingArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    roamingArrayList.add(plansModel);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        try {
                            if (recordsObject.has("SMS")) {
                                myPagerAdapter.addFragment(new SMSFragment(), "SMS");
                                JSONArray data2Array = recordsObject.getJSONArray("SMS");
                                for (int i = 0; i < data2Array.length(); i++) {
                                    PlansModel plansModel = new PlansModel();
                                    JSONObject topUpData = data2Array.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    smsArrayList.add(plansModel);

                                }
                            }
                        } catch (Exception e) {

                        }


                        try {
                            if (recordsObject.has("RATE CUTTER")) {
                                myPagerAdapter.addFragment(new RateCutterFragment(), "Rate Cutter");

                                JSONArray rateCutterArray = recordsObject.getJSONArray("RATE CUTTER");

                                for (int i = 0; i < rateCutterArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = rateCutterArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    rateCutterArrayList.add(plansModel);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (recordsObject.has("COMBO")) {
                                myPagerAdapter.addFragment(new ComboFragment(), "Combo Offer");
                                JSONArray comboArray = recordsObject.getJSONArray("COMBO");
                                for (int i = 0; i < comboArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = comboArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    comboArrayList.add(plansModel);
                                }
                            }
                        } catch (Exception e) {

                        }


                        try {
                            if (recordsObject.has("FRC")) {
                                myPagerAdapter.addFragment(new FrcFragment(), "FRC");
                                JSONArray comboArray = recordsObject.getJSONArray("FRC");
                                for (int i = 0; i < comboArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = comboArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    frcArrayList.add(plansModel);
                                }
                            }
                        } catch (Exception e) {

                        }


                        try {
                            if (recordsObject.has("STV")) {
                                myPagerAdapter.addFragment(new StvFragment(), "STV");
                                JSONArray comboArray = recordsObject.getJSONArray("STV");
                                for (int i = 0; i < comboArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = comboArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    stvArrayList.add(plansModel);
                                }
                            }
                        } catch (Exception e) {

                        }

                        try {
                            if (recordsObject.has("BSNLValidExtension")) {
                                myPagerAdapter.addFragment(new BsnlValidExtentionFragment(), "BSNLValidExt.");
                                JSONArray comboArray = recordsObject.getJSONArray("BSNLValidExtension");
                                for (int i = 0; i < comboArray.length(); i++) {
                                    PlansModel plansModel = new PlansModel();

                                    JSONObject topUpData = comboArray.getJSONObject(i);
                                    String rs = topUpData.getString("rs");
                                    String desc = topUpData.getString("desc");
                                    String validity = topUpData.getString("validity");

                                    plansModel.setRs(rs);
                                    plansModel.setDesc(desc);
                                    plansModel.setValidityText("Validity");
                                    plansModel.setValidity(validity);

                                    bsnlValidExtArrayList.add(plansModel);
                                }
                            }
                        } catch (Exception e) {

                        }

                        viewPager.setAdapter(myPagerAdapter);

                        tabLayout.setupWithViewPager(viewPager);

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(PlansActivity.this)
                                .setCancelable(false)
                                .setMessage("No Plans Found")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(PlansActivity.this)
                            .setCancelable(false)
                            .setMessage("No Plans Found")
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
                new AlertDialog.Builder(PlansActivity.this)
                        .setCancelable(false)
                        .setMessage("No Plans Found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.plans_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
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
}