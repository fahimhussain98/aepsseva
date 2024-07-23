package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wts.aepssevaa.R;

public class AllReportsActivity extends AppCompatActivity {
    ConstraintLayout aepsReportLayout, settlementReportLayout, sellReportLayout, addMoneyReportLayout,dmtReportLayout,commissionReportLayout,dayBookReportLayout,recahrgeReportLayout,fundReportLayout,otherReportLayout;
    CardView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);
        initViews();

        dmtReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AllReportsActivity.this, MoneyTransferReportActivity.class);
                startActivity(intent);

            }
        });

        addMoneyReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent in = new Intent(new Intent(AllReportsActivity.this, AddMoneyReportActivity.class));
                startActivity(in);
                 */
            }
        });

        aepsReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AllReportsActivity.this, AepsReportActivity.class));

            }
        });

        settlementReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AllReportsActivity.this, SettlementReportActivity.class));

            }
        });

        recahrgeReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllReportsActivity.this, ReportsActivity.class));
            }
        });

        sellReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AllReportsActivity.this, SellReportActivity.class));

            }
        });

        dayBookReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllReportsActivity.this, DaybookReportActivity.class);
                startActivity(intent);
            }
        });

        fundReportLayout.setOnClickListener(view -> {
            Intent intent = new Intent(AllReportsActivity.this, OfflineFundReportActivity.class);
            startActivity(intent);
        });

        otherReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllReportsActivity.this, OtherReportsActivity.class));
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {

            aepsReportLayout = findViewById(R.id.aeps_reports_layout);
            settlementReportLayout = findViewById(R.id.settlement_reports_layout);
            sellReportLayout = findViewById(R.id.sell_reports_layout);
            addMoneyReportLayout =findViewById(R.id.addMoneyReport_layout);
            dmtReportLayout = findViewById(R.id.dmt_reports_layout);
            commissionReportLayout = findViewById(R.id.commission_reports_layout);
            dayBookReportLayout = findViewById(R.id.daybook_reports_layout);
            recahrgeReportLayout = findViewById(R.id.recharge_reports_layout);
            fundReportLayout = findViewById(R.id.fund_report_layout);
            otherReportLayout = findViewById(R.id.other_reports_layout);
            backButton = findViewById(R.id.all_reportsBackCard);
    }
}