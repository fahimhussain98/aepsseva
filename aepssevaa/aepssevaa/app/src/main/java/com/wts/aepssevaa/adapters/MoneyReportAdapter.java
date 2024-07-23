package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.SharePayoutReportActivity;
import com.wts.aepssevaa.models.MoneyReportModel;

import java.util.ArrayList;

public class MoneyReportAdapter extends RecyclerView.Adapter<MoneyReportAdapter.ViewHolder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;

    public MoneyReportAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList, Context context) {
        this.moneyReportModelArrayList = moneyReportModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_money_report_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String amount=moneyReportModelArrayList.get(position).getAmount();
        final String accountNumber=moneyReportModelArrayList.get(position).getAccountNumber();
        final String beniName=moneyReportModelArrayList.get(position).getBeniName();
        final String bank=moneyReportModelArrayList.get(position).getBank();
        final String ifsc=moneyReportModelArrayList.get(position).getIfsc();
        String date=moneyReportModelArrayList.get(position).getDate();
        String status=moneyReportModelArrayList.get(position).getStatus();

        if (status.equalsIgnoreCase("SUCCESS") || status.equalsIgnoreCase("Successful"))
        {
            holder.tvStatus.setBackground(context.getDrawable(R.drawable.button_back_green));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        else if (status.equalsIgnoreCase("pending"))
        {
            holder.tvStatus.setBackground(context.getDrawable(R.drawable.button_back));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }

        else if (status.equalsIgnoreCase("failure") || status.equalsIgnoreCase("Failed"))
        {
            holder.tvStatus.setBackground(context.getDrawable(R.drawable.button_back2));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.tvAmount.setText("₹ "+amount);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvBeniName.setText(beniName);
        holder.tvBank.setText(bank);
        holder.tvIfsc.setText(ifsc);
        holder.tvDate.setText(date);
        holder.tvStatus.setText(status);

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SharePayoutReportActivity.class);
                intent.putExtra("transactionId",moneyReportModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",moneyReportModelArrayList.get(position).getAmount());
                intent.putExtra("comm",moneyReportModelArrayList.get(position).getComm());
                intent.putExtra("balance",moneyReportModelArrayList.get(position).getBalance());
                intent.putExtra("dateTime",moneyReportModelArrayList.get(position).getDate());
                intent.putExtra("status",moneyReportModelArrayList.get(position).getStatus());
                intent.putExtra("accountName",moneyReportModelArrayList.get(position).getBeniName());
                intent.putExtra("accountNo",moneyReportModelArrayList.get(position).getAccountNumber());
                intent.putExtra("bankName",moneyReportModelArrayList.get(position).getBank());
                intent.putExtra("surcharge",moneyReportModelArrayList.get(position).getSurcharge());
                intent.putExtra("cost",moneyReportModelArrayList.get(position).getCost());
                intent.putExtra("status",moneyReportModelArrayList.get(position).getStatus());
                intent.putExtra("dateTime",moneyReportModelArrayList.get(position).getDate());
                intent.putExtra("serviceType","Money");

                intent.putExtra("transactionType","only for not null value");

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moneyReportModelArrayList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        TextView tvAmount,tvAccountNumber,tvBeniName,tvBank,tvIfsc,tvDate,tvStatus;
        AppCompatButton imgShare;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvAccountNumber=itemView.findViewById(R.id.tv_account_number);
            tvBeniName=itemView.findViewById(R.id.tv_beni_name);
            tvBank=itemView.findViewById(R.id.tv_bank_name);
            tvIfsc=itemView.findViewById(R.id.tv_ifsc);
            tvDate=itemView.findViewById(R.id.tv_all_report_date_time);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
            imgShare = itemView.findViewById(R.id.img_share);
        }
    }
}
