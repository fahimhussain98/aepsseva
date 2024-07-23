package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.ViewPagerAdapter;
import com.wts.aepssevaa.fragments.PaysprintAddBeneFragment;
import com.wts.aepssevaa.fragments.PaysprintBeneficariesFragment;
import com.wts.aepssevaa.models.RecipientModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PaysprintNewMoneyTransferActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    TextView tvName,tvAvailableLimit,tvConsumedLimit,tvTotalLimit;
    public static String senderMobileNumber;
    public static String senderName,availableLimit,totalLimit,consumedLimit, remitterId;
    public String selectedTextMode;
    String deviceId,deviceInfo;
    String userId;
    SharedPreferences sharedPreferences;

    public static ArrayList<RecipientModel> recipientModelArrayList ;
    public static boolean isBeneCountZero=true;

    public static boolean shouldRefreshActivity=true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paysprint_new_money_transfer);
        inhitViews();

        shouldRefreshActivity=true;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(PaysprintNewMoneyTransferActivity.this);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        userId=sharedPreferences.getString("userid",null);

        senderName=getIntent().getStringExtra("senderName");
        senderMobileNumber=getIntent().getStringExtra("senderMobileNumber");
        selectedTextMode=getIntent().getStringExtra("mode");
        availableLimit=getIntent().getStringExtra("availableLimit");
        consumedLimit=getIntent().getStringExtra("consumedLimit");
        totalLimit=getIntent().getStringExtra("totalLimit");
        remitterId=getIntent().getStringExtra("remitterId");

        tvName.setText(senderName+" ("+senderMobileNumber+")");
        tvAvailableLimit.setText("Available Limit\n"+"₹"+availableLimit);
        tvConsumedLimit.setText("Consumed Limit\n"+"₹"+consumedLimit);
        tvTotalLimit.setText("Total Limit\n"+"₹"+totalLimit);

        //setUpViewPager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRefreshActivity)
        {
            if (PaysprintSenderValidationActivity.title.equalsIgnoreCase("Money Transfer"))
            {
                // isSenderValidate();
            }
            else if (PaysprintSenderValidationActivity.title.equalsIgnoreCase("Money Transfer2"))
            {
                //  isSenderValidate2();
            }
            else
            {
                isSenderValidate3();
            }
        }

    }


    private void isSenderValidate3() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setTitle("wait");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().isUserValidate3(ApiController.Auth_key,userId, deviceId, deviceInfo,  PaysprintSenderValidationActivity.senderMobileNumber);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject;


                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject.getString("statuscode");


                        if (statusCode.equalsIgnoreCase("TXN"))
                        {
                            JSONArray beneListArray= jsonObject.getJSONArray("benelist");

                            recipientModelArrayList = new ArrayList<>();

                            for (int i=0; i<beneListArray.length(); i++)
                            {
                                RecipientModel recipientModel = new RecipientModel();

                                JSONObject beneListObject=beneListArray.getJSONObject(i);

                                String bankAccountNumber = beneListObject.getString("accountNo");
                                String bankName = beneListObject.getString("bankName");
                                String ifsc = beneListObject.getString("ifscCode");
                                String recipientId = beneListObject.getString("beneficiaryId");
                                String recipientName = beneListObject.getString("beneficiaryName");
                                //   String beneMobileNo = beneListObject.getString("Mobileno");

                                recipientModel.setBankAccountNumber(bankAccountNumber);
                                recipientModel.setBankName(bankName);
                                recipientModel.setIfsc(ifsc);
                                recipientModel.setRecipientId(recipientId);
                                recipientModel.setRecipientName(recipientName);
                                //   recipientModel.setMobileNumber(beneMobileNo);
                                recipientModelArrayList.add(recipientModel);
                            }
                            isBeneCountZero=false;

                        }

                        else
                        {
                            isBeneCountZero=true;
                        }

                        setUpViewPager();

                        pDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(PaysprintNewMoneyTransferActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(PaysprintNewMoneyTransferActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(PaysprintNewMoneyTransferActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                        .show();
            }
        });

    }



    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (isBeneCountZero) {
            //viewPagerAdapter.addFragments(new BeneficariesFragment(), "Beneficiary");
            //viewPagerAdapter.addFragments(new TransactionsFragment(), "Transactions");
            viewPagerAdapter.addFragments(new PaysprintAddBeneFragment(), "Add");
        }
        else
        {

            viewPagerAdapter.addFragments(new PaysprintBeneficariesFragment(), "Beneficiary");
            viewPagerAdapter.addFragments(new PaysprintAddBeneFragment(), "Add");
            //viewPagerAdapter.addFragments(new TransactionsFragment(), "Transactions");
        }

//        if (SenderValidationActivity.title.equalsIgnoreCase("Money Transfer"))
//        {
//            viewPagerAdapter.addFragments(new AddUpiFragment(), "Add UPI");
//        }
//        viewPagerAdapter.addFragments(new AddBeneFragment(), "Add");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if (isBeneCountZero)
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.add_bene);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
            // Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);
        }
        else
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);
            // Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.add_bene);
        }


    }

    private void inhitViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tvName = findViewById(R.id.tv_name);
        tvAvailableLimit = findViewById(R.id.tv_available_limit);
        tvConsumedLimit = findViewById(R.id.tv_consumed_limit);
        tvTotalLimit = findViewById(R.id.tv_total_limit);
    }
}