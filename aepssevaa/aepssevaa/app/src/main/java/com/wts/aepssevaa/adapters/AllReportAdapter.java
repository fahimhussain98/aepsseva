package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.ShareReportActivity;
import com.wts.aepssevaa.models.AllReportsModel;

import java.util.ArrayList;

public class AllReportAdapter extends RecyclerView.Adapter<AllReportAdapter.ViewHolder> {

    ArrayList<AllReportsModel> allReportsModelArrayList;
    String transactionId,liveId,operatorName,imageUrl, number, amount, commission, cost, balance, date,time, status,stype;
    Context context;
    String userId;
    String deviceId,deviceInfo;
    SharedPreferences sharedPreferences;
    Activity activity;

    public AllReportAdapter(ArrayList<AllReportsModel> allReportsModelArrayList, Context context, String userId,Activity activity) {
        this.allReportsModelArrayList = allReportsModelArrayList;
        this.context=context;
        this.userId=userId;
        this.activity=activity;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        transactionId = allReportsModelArrayList.get(position).getTransactionId();
        liveId = allReportsModelArrayList.get(position).getLiveId();
        operatorName = allReportsModelArrayList.get(position).getOperatorName();
        number = allReportsModelArrayList.get(position).getNumber();
        amount = allReportsModelArrayList.get(position).getAmount();
        commission = allReportsModelArrayList.get(position).getCommission();
        cost = allReportsModelArrayList.get(position).getCost();
        balance = allReportsModelArrayList.get(position).getBalance();
        date = allReportsModelArrayList.get(position).getDate();
        time = allReportsModelArrayList.get(position).getTime();
        status = allReportsModelArrayList.get(position).getStatus();
        stype = allReportsModelArrayList.get(position).getsType();
        imageUrl = allReportsModelArrayList.get(position).getImageUrl();
        String openingBalance = allReportsModelArrayList.get(position).getOpeningBalance();
        String closingBalance = allReportsModelArrayList.get(position).getClosingBalance();


        holder.tvTransactionId.setText(transactionId);
        holder.tvLiveId.setText(liveId);
        holder.tvDate.setText(date+","+time);
        holder.tvNumber.setText(number);
        holder.tvOpeningBalance.setText("₹ "+openingBalance);
        holder.tvClosingBalance.setText("₹ "+closingBalance);
        holder.tvComm.setText("₹ "+commission);
        holder.tvStatus.setText(status);
        holder.tvAmount.setText("₹ "+amount);

        if (status.equalsIgnoreCase("SUCCESS"))
        {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back_green));
        }
        else
        {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back2));
        }

        Picasso.get().load(imageUrl).into(holder.imgOperator);

        holder.imgShare.setOnClickListener(v->
        {
            Intent intent = new Intent(context, ShareReportActivity.class);
            intent.putExtra("number", allReportsModelArrayList.get(position).getNumber());
            intent.putExtra("amount", "₹ " + allReportsModelArrayList.get(position).getAmount());
            intent.putExtra("commission", "₹ " + allReportsModelArrayList.get(position).getCommission());
            intent.putExtra("cost", "₹ " + allReportsModelArrayList.get(position).getCost());
            intent.putExtra("balance", "₹ " + allReportsModelArrayList.get(position).getBalance());
            intent.putExtra("date", allReportsModelArrayList.get(position).getDate());
            intent.putExtra("time", allReportsModelArrayList.get(position).getTime());
            intent.putExtra("status", allReportsModelArrayList.get(position).getStatus());
            intent.putExtra("operator", allReportsModelArrayList.get(position).getOperatorName());
            intent.putExtra("txnId", allReportsModelArrayList.get(position).getTransactionId());
            intent.putExtra("liveId", allReportsModelArrayList.get(position).getLiveId());
            intent.putExtra("uniqueId", allReportsModelArrayList.get(position).getUniqueId());
            context.startActivity(intent);

        });

    }
    @Override
    public int getItemCount() {
        return allReportsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTransactionId,tvLiveId,tvDate,tvNumber,tvAmount,tvOpeningBalance,tvComm,tvClosingBalance,tvStatus;
        ImageView imgOperator,imgShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvLiveId=itemView.findViewById(R.id.tv_all_report_live_id);
            tvDate=itemView.findViewById(R.id.tv_date_time);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvOpeningBalance = itemView.findViewById(R.id.tv_opening_balance);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvComm = itemView.findViewById(R.id.tv_commission);
            tvClosingBalance = itemView.findViewById(R.id.tv_closing_balance);
            tvStatus = itemView.findViewById(R.id.tv_status);
            imgShare = itemView.findViewById(R.id.img_share);
            imgOperator = itemView.findViewById(R.id.img_operator);

        }
    }
}
