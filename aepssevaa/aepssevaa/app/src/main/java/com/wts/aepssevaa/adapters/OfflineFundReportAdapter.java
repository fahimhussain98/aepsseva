package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.models.OfflineFundModel;

import java.util.ArrayList;

public class OfflineFundReportAdapter extends RecyclerView.Adapter<OfflineFundReportAdapter.Viewholder> {

    ArrayList<OfflineFundModel> offlineFundModelArrayList;

    public OfflineFundReportAdapter(ArrayList<OfflineFundModel> offlineFundModelArrayList) {
        this.offlineFundModelArrayList = offlineFundModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fund_report,parent,false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String custId=offlineFundModelArrayList.get(position).getCustId();
        String amount=offlineFundModelArrayList.get(position).getAmount();
        String status=offlineFundModelArrayList.get(position).getStatus();
        String date=offlineFundModelArrayList.get(position).getDate();
        String bankRefNo=offlineFundModelArrayList.get(position).getBankRefNo();
        String remarks=offlineFundModelArrayList.get(position).getRemarks();

        holder.tvCustId.setText(custId);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvStatus.setText(status);
        holder.tvDate.setText(date);
        holder.tvRemarks.setText(remarks);
        holder.tvBankRefNo.setText(bankRefNo);

    }

    @Override
    public int getItemCount() {
        return offlineFundModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvCustId,tvAmount,tvStatus,tvDate,tvRemarks,tvBankRefNo;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvCustId=itemView.findViewById(R.id.tv_cust_id);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvStatus=itemView.findViewById(R.id.tv_status);
            tvRemarks=itemView.findViewById(R.id.tv_remarks);
            tvBankRefNo=itemView.findViewById(R.id.tv_ref_no);
        }
    }
}