package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.adapters.MyPagerAdapter;
import com.wts.aepssevaa.reportsFragment.DthReportFragment;
import com.wts.aepssevaa.reportsFragment.ElectricityReportsFragment;
import com.wts.aepssevaa.reportsFragment.PrepaidReportFragment;


public class ReportsActivity extends AppCompatActivity {

   /* ConstraintLayout rechargeReportsLayout,aepsReportsLayout,aepsLedgerLayout,payoutReportLayout,ledgerReportLayout,moneyTransferReportLayout
            ,creditReportLayout,debitReportLayout;*/


    ImageView backButton;
    TextView activityTitle;
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReportsActivity.this, R.color.blue));
        //////CHANGE COLOR OF STATUS BAR

        inhitViews();
        activityTitle.setText("Reports");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // myPagerAdapter.addFragment(new GeneralFragment(),"General");
        // myPagerAdapter.addFragment(new AepsDmtFragment(),"AEPS/DMT");
        myPagerAdapter.addFragment(new PrepaidReportFragment(),"Prepaid");
        myPagerAdapter.addFragment(new DthReportFragment(),"DTH");
         myPagerAdapter.addFragment(new ElectricityReportsFragment(),"Electricity");
      //  myPagerAdapter.addFragment(new PostpaidReportFragment(),"Postpaid");

        viewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void inhitViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title_report);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
    }
}