package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.models.LedgerReportModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LedgerReportAdapter extends RecyclerView.Adapter<LedgerReportAdapter.ViewHolder> {

    ArrayList<LedgerReportModel> ledgerReportModelArrayList;
    Context context;
    boolean isIntialReport;



    public LedgerReportAdapter(ArrayList<LedgerReportModel> ledgerReportModelArrayList, Context context, boolean isInitialReport) {
        this.ledgerReportModelArrayList = ledgerReportModelArrayList;
        this.context=context;
        this.isIntialReport=isInitialReport;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//     View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ledger_list,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ledger_report_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String txnid,userId,oldBalance,newBalance,transactionType,
                transactionDate,ipAddress,crDrType,amount,surcharge,tds,commission;

        String outputDate, outPutTime, dateTime = null;

        txnid=ledgerReportModelArrayList.get(position).getBalanceId();
        userId=ledgerReportModelArrayList.get(position).getUserId();
        oldBalance=ledgerReportModelArrayList.get(position).getOldBalance();
        newBalance=ledgerReportModelArrayList.get(position).getNewBalance();
        transactionType=ledgerReportModelArrayList.get(position).getTransactionType();
        transactionDate=ledgerReportModelArrayList.get(position).getTransactionDate();
        ipAddress=ledgerReportModelArrayList.get(position).getIpAddress();
        crDrType=ledgerReportModelArrayList.get(position).getCrDrType();
        amount=ledgerReportModelArrayList.get(position).getAmount();
        surcharge=ledgerReportModelArrayList.get(position).getSurcharge();
        tds =ledgerReportModelArrayList.get(position).getTds();
        commission =ledgerReportModelArrayList.get(position).getCommission();

        @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String[] splitDate = transactionDate.split("T");
        try {
            Date date = inputDateFormat.parse(splitDate[0]);
            Date time = simpleDateFormat.parse(splitDate[1]);
            outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
            outPutTime = new SimpleDateFormat("hh:mm a").format(time);

            dateTime= outputDate+" , "+outPutTime;

            //   holder.tvTransactionDate.setText(outputDate+ " , "+outPutTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setdata(txnid,userId,oldBalance,newBalance,crDrType,dateTime, ipAddress,crDrType,
                amount);

        if (crDrType.equalsIgnoreCase("Debit"))
        {
            holder.tvTransactionType.setTextColor(context.getResources().getColor(R.color.red));
        }
        else
        {
            holder.tvTransactionType.setTextColor(context.getResources().getColor(R.color.green));
        }

        String finalDateTime = dateTime;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final View view1 = LayoutInflater.from(context).inflate(R.layout.view_more_dialog_layout, null, false);
                final AlertDialog builder = new AlertDialog.Builder(context).create();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setCancelable(false);
                builder.setView(view1);
                builder.show();

                Button btnGoToBack=view1.findViewById(R.id.btn_dialog_back_button);

                TextView tvDialogBalanceId=view1.findViewById(R.id.tv_dialog_balance_id);
                TextView tvDialogTransType=view1.findViewById(R.id.tv_dialog_trans_type);
                TextView tvDialogCrDrType=view1.findViewById(R.id.tv_dialog_cr_dr_type);
                TextView tvDialogTransDate=view1.findViewById(R.id.tv_dialog_trans_date);
                TextView tvDialogTransAmount=view1.findViewById(R.id.tv_dialog_trans_amount);
                TextView tvDialogOldbal=view1.findViewById(R.id.tv_dialog_old_bal);
                TextView tvDialogNewbal=view1.findViewById(R.id.tv_dialog_new_bal);
                TextView tvSurcharge=view1.findViewById(R.id.tv_surcharge);
                TextView tvTds=view1.findViewById(R.id.tv_tds);
                TextView tvCommission=view1.findViewById(R.id.tv_commission);

                tvDialogBalanceId.setText(txnid);
                tvDialogTransType.setText(transactionType);
                tvDialogCrDrType.setText(crDrType);
                tvDialogTransDate.setText(finalDateTime);
                tvDialogTransAmount.setText("₹ "+amount);
                tvDialogOldbal.setText("₹ "+oldBalance);
                tvDialogNewbal.setText("₹ "+newBalance);
                tvSurcharge.setText("₹ "+surcharge);
                tvTds.setText("₹ "+tds);
                tvCommission.setText("₹ "+commission);


                btnGoToBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        if (isIntialReport) {
            if (ledgerReportModelArrayList.size()<=10)
                return ledgerReportModelArrayList.size();
            else  {
                return 10;
            }
        } else {
            return ledgerReportModelArrayList.size();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvTransactionType,tvTransactionDate, tvOldBalance,tvNewBalance,tvTransactionAmount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_txnId);
            tvTransactionType=itemView.findViewById(R.id.tv_crDrType);
            tvTransactionDate=itemView.findViewById(R.id.tv_dateTime);
            tvOldBalance =itemView.findViewById(R.id.tv_oldBal);
            tvNewBalance=itemView.findViewById(R.id.tv_newBal);
            tvTransactionAmount=itemView.findViewById(R.id.tv_transactionAmount);
            //   btnViewMore=itemView.findViewById(R.id.btn_view_more);
        }


        public void setdata(String txnId, String userId, String oldBalance, String newBalance,
                            String transactionType, String transactionDate, String ipAddress,
                            String crDrType,  String amount) {
            tvTransactionId.setText(txnId);
            tvTransactionType.setText(transactionType);
            tvTransactionDate.setText(transactionDate);
            tvOldBalance.setText("\u20b9 "+oldBalance);
            tvNewBalance.setText("\u20b9 "+newBalance);
            tvTransactionAmount.setText("\u20b9 "+amount);

        }
    }
}

