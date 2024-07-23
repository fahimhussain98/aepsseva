package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.models.AepsModel;

import java.util.ArrayList;

public class AepsReportAdapter extends RecyclerView.Adapter<AepsReportAdapter.ViewHolder>
{
    ArrayList<AepsModel> aepsModelArrayList;
    boolean isInitialReport;
    Context context;

    public AepsReportAdapter(ArrayList<AepsModel> aepsModelArrayList, boolean isInitialReport, Context context) {
        this.aepsModelArrayList = aepsModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
    }


    @NonNull
    @Override
    public AepsReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_aeps_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AepsReportAdapter.ViewHolder holder, int position) {
        String transactionId=aepsModelArrayList.get(position).getTransactionId();
        //String ownerName=aepsModelArrayList.get(position).getOwenername();
        //String bcId=aepsModelArrayList.get(position).getBcId();
        String amount=aepsModelArrayList.get(position).getAmount();
        String comm=aepsModelArrayList.get(position).getComm();
        //String cost=aepsModelArrayList.get(position).getCost();
        String balance=aepsModelArrayList.get(position).getNewbalance();
        String dateTime=aepsModelArrayList.get(position).getTimestamp();
        String status=aepsModelArrayList.get(position).getTxnStatus();



        holder.tvTransactionId.setText(transactionId);
        //holder.tvOwnerName.setText(ownerName);
        //holder.tvBcId.setText(bcId);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvComm.setText("₹ "+comm);
        //holder.tvCost.setText(cost);
        holder.tvBalance.setText("₹ "+balance);
        holder.tvDateTime.setText(dateTime);
        holder.tvStatus.setText(status);

        if(status.equalsIgnoreCase("FAILED")||status.equalsIgnoreCase("FAILURE")){

            holder.tvStatus.setBackgroundResource(R.color.red);
        }
        else {
            holder.tvStatus.setBackgroundResource(R.color.green);
        }

    }

    @Override
    public int getItemCount() {
        return aepsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvOwnerName,tvBcId,tvAmount,tvComm,tvCost,tvBalance,tvDateTime,tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_all_report_transaction_id);
            tvOwnerName=itemView.findViewById(R.id.tv_owner_name);
            tvBcId=itemView.findViewById(R.id.tv_bc_id);
            tvAmount=itemView.findViewById(R.id.tv_all_report_amount);
            tvComm=itemView.findViewById(R.id.tv_all_report_commission);
            tvCost=itemView.findViewById(R.id.tv_all_report_cost);
            tvBalance=itemView.findViewById(R.id.tv_new_balance);
            tvDateTime=itemView.findViewById(R.id.tv_all_report_date_time);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
        }
    }
}
