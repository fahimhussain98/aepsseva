package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wts.aepssevaa.R;


public class OtherReportsActivity extends AppCompatActivity {
    ConstraintLayout electricityReportLayout, postpaidReportLayout, gasReportLayout, waterReportLayout,fastagReportLayout,insuranceReportLayout;
    CardView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_reports);
        initViews();

        electricityReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "Electricity");
                otherIntent.putExtra("title", "Electricity Report");
                startActivity(otherIntent);

            }
        });

        postpaidReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "PostPaid");
                otherIntent.putExtra("title", "Postpaid Report");
                startActivity(otherIntent);

            }
        });

        gasReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "GAS");
                otherIntent.putExtra("title", "Gas Report");
                startActivity(otherIntent);

            }
        });

        waterReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "Water");
                otherIntent.putExtra("title", "Water Report");
                startActivity(otherIntent);


            }
        });

        fastagReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "FASTAG");
                otherIntent.putExtra("title", "Fastag Report");
                startActivity(otherIntent);


            }
        });
        insuranceReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otherIntent = new Intent(OtherReportsActivity.this , NewUtilityReportsActivity.class);
                otherIntent.putExtra("service", "HEALTH INSURANCE");
                otherIntent.putExtra("title", "Insurance Report");
                startActivity(otherIntent);

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

        electricityReportLayout = findViewById(R.id.electricityReport_layout);
        postpaidReportLayout = findViewById(R.id.postpaidReport_layout);
        gasReportLayout = findViewById(R.id.gasReport_layout);
        waterReportLayout =findViewById(R.id.waterReport_layout);
        fastagReportLayout = findViewById(R.id.fastagReport_layout);
        insuranceReportLayout = findViewById(R.id.insuranceReport_layout);

        backButton = findViewById(R.id.all_reportsBackCard);
    }
}