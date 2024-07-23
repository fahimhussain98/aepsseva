package com.wts.aepssevaa.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.NewMoneyTransferActivity;
import com.wts.aepssevaa.activities.SenderValidationActivity;
import com.wts.aepssevaa.activities.ShareDmtReportActivity;
import com.wts.aepssevaa.activities.SharePayoutReportActivity;
import com.wts.aepssevaa.models.RecipientModel;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.Viewholder> {

    Context context;
    ArrayList<RecipientModel> recipientModelArrayList;
    String mobileNumber, selectedTextMode, userId, userName;
    Activity activity;
    String responseBeniName, responseAccountNo, responseBankName, responseIfsc, bankRefId, transactionId, responseAmount, date, status,comm,
            surcharge,closingBalance,cost;
    String deviceId, deviceInfo;

    String whichButtonClicked = "upiORdmt";

    public RecipientAdapter(Context context, Activity activity, ArrayList<RecipientModel> recipientModelArrayList,
                            String mobileNumber, String userId, String userName, String deviceId, String deviceInfo) {
        this.context = context;
        this.activity = activity;
        this.recipientModelArrayList = recipientModelArrayList;
        this.mobileNumber = mobileNumber;
        this.selectedTextMode = selectedTextMode;
        this.userId = userId;
        this.userName = userName;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_recipient_list, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, @SuppressLint("RecyclerView") int position) {

        final String recipientName = recipientModelArrayList.get(position).getRecipientName();
        final String bankName = recipientModelArrayList.get(position).getBankName();
        final String recipientId = recipientModelArrayList.get(position).getRecipientId();
        final String bankAccountNumber = recipientModelArrayList.get(position).getBankAccountNumber();
        final String ifsc = recipientModelArrayList.get(position).getIfsc();
        final String benificaryMobileNumber = recipientModelArrayList.get(position).getMobileNumber();


        holder.tvRecipientName.setText(recipientName);
        holder.tvAccountNumber.setText(bankAccountNumber);
        holder.tvBankName.setText(bankName);
        holder.tvIfsc.setText(ifsc);

        if (bankName.equalsIgnoreCase("N/A")) {
            holder.imgBank.setVisibility(View.GONE);
            holder.imgIfsc.setVisibility(View.GONE);
            holder.tvBankName.setVisibility(View.GONE);
            holder.tvIfsc.setVisibility(View.GONE);
            holder.btnPay.setVisibility(View.GONE);
            holder.btnUpiPay.setVisibility(View.VISIBLE);
        } else {
            holder.imgBank.setVisibility(View.VISIBLE);
            holder.imgIfsc.setVisibility(View.VISIBLE);
            holder.tvBankName.setVisibility(View.VISIBLE);
            holder.tvIfsc.setVisibility(View.VISIBLE);
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnUpiPay.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("Confirmation")
                        .setMessage("Do you want to delete this beneficiary ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBene(recipientId,position);
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        });

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichButtonClicked = "dmtButtonClicked";

                final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_benificary_layout,
                        null, false);
                final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addSenderOTPDialog.setCancelable(false);
                addSenderOTPDialog.setView(addSenderOTPDialogView);
                addSenderOTPDialog.show();

                final EditText etAmount = addSenderOTPDialog.findViewById(R.id.et_amount);
                final EditText etTpin = addSenderOTPDialog.findViewById(R.id.et_mpin);
                Button btnProceed = addSenderOTPDialog.findViewById(R.id.btn_proceed);
                Button btnCancel = addSenderOTPDialog.findViewById(R.id.btn_cancel);
                TextView tvBeniName = addSenderOTPDialog.findViewById(R.id.tv_beni_name);
                TextView tvBankName = addSenderOTPDialog.findViewById(R.id.tv_bank_name);
                TextView tvAccountNumber = addSenderOTPDialog.findViewById(R.id.tv_account_number);
                TextView tvIfsc = addSenderOTPDialog.findViewById(R.id.tv_ifsc);

                tvBeniName.setText(recipientName);
                tvBankName.setText(bankName);
                tvAccountNumber.setText(bankAccountNumber);
                tvIfsc.setText(ifsc);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSenderOTPDialog.dismiss();
                    }
                });

                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(etAmount.getText())) {

                          //  if (!TextUtils.isEmpty(etTpin.getText()))
                          //  {



                                addSenderOTPDialog.dismiss();
                                String amount = etAmount.getText().toString().trim();
                                String tpin = etTpin.getText().toString().trim();
                                payBenificary(recipientId, amount, bankAccountNumber, bankName, ifsc, recipientName,benificaryMobileNumber);
                                // checkTpin(tpin, amount, recipientId, bankAccountNumber, bankName, ifsc, recipientName,benificaryMobileNumber);
                           /*
                            }


                            else
                            {
                                etTpin.setError("Required");
                            }

                            */


                        } else {
                            etAmount.setError("Enter Amount");
                        }
                    }
                });


            }
        });

        holder.btnUpiPay.setOnClickListener(v -> {
//            accountNo = arrayList.get(position).getAccountNo();
//            ifscCode = arrayList.get(position).getIfscCode();
//            bankName = arrayList.get(position).getBankName();
//            beneName = arrayList.get(position).getBeneficiaryName();
//            beneficiaryID = arrayList.get(position).getBeneficiaryID();
//            beneMobileNo = arrayList.get(position).getBeneMobileNo();

            whichButtonClicked = "upiButtonClicked";

            final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_upi_layout,
                    null, false);
            final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
            addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addSenderOTPDialog.setCancelable(false);
            addSenderOTPDialog.setView(addSenderOTPDialogView);
            addSenderOTPDialog.show();

            final EditText etAmount = addSenderOTPDialog.findViewById(R.id.et_amount);
            final EditText etTpin = addSenderOTPDialog.findViewById(R.id.et_tpin);
            AppCompatButton btnProceed = addSenderOTPDialog.findViewById(R.id.btn_proceed);
            Button btnCancel = addSenderOTPDialog.findViewById(R.id.btn_cancel);
            TextView tvBeniName = addSenderOTPDialog.findViewById(R.id.tv_beni_name);
            TextView tvUpiId = addSenderOTPDialog.findViewById(R.id.tv_upi_id);

            tvBeniName.setText(recipientName);
            tvUpiId.setText(bankAccountNumber);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSenderOTPDialog.dismiss();
                }
            });

            btnProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etAmount.getText())) {
                     //   if (!TextUtils.isEmpty(etTpin.getText())) {

                            addSenderOTPDialog.dismiss();
                            String amount = etAmount.getText().toString().trim();
                            String tpin = etTpin.getText().toString().trim();


                              payViaUpi(recipientId, amount, bankAccountNumber, recipientName, benificaryMobileNumber);
                           // checkTpin(tpin, amount, recipientId, bankAccountNumber, bankName, ifsc, recipientName,benificaryMobileNumber);
                      /*  }
                        else
                        {
                            etTpin.setError("Required");
                        }

                       */


                    } else {
                        etAmount.setError("Enter Amount");
                    }
                }
            });
        });

    }

    private void checkTpin(String tpin, String amount, String recipientId, String bankAccountNumber, String bankName,
                           String ifsc, String benificaryName,String benificaryMobileNumber) {


        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("");
        pDialog.setMessage("Please wait..");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().checkMpinOrTPIN(ApiController.Auth_key,userId,deviceId,deviceInfo,"tpin",tpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            if (whichButtonClicked.equalsIgnoreCase("dmtButtonClicked"))
                            {
                                payBenificary(recipientId,amount,bankAccountNumber,bankName,ifsc,benificaryName,benificaryMobileNumber);
                            }
                            else
                            {
                                payViaUpi(recipientId, amount, bankAccountNumber, benificaryName, benificaryMobileNumber);
                            }


                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String transaction = responseObject.getString("status");
                            new AlertDialog.Builder(context)
                                    .setMessage(transaction)
                                    .show();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setMessage("Something went wrong.Please try after sometime.")
                                    .show();
                        }


                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage("Something went wrong.Please try after sometime.")
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setMessage("Something went wrong.Please try after sometime.")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setMessage("Something went wrong.Please try after sometime.")
                        .show();
            }
        });
    }

    private void payBenificary(String recipientId, final String amount, String bankAccountNumber, final String bankName, final String ifsc,
                               final String benificaryName,String beneMobileNumber) {


        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("");
        pDialog.setMessage("Please wait ");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().payBeneficiary(ApiController.Auth_key,userId,deviceId,deviceInfo,amount,recipientId,"NA",
                SenderValidationActivity.sendername,SenderValidationActivity.senderMobileNumber,bankAccountNumber,benificaryName,bankName,ifsc,
                SenderValidationActivity.senderMobileNumber,"ifs","NA");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String respoonseCode = jsonObject.getString("statuscode");

                        if (respoonseCode.equalsIgnoreCase("TXN")) {

                            JSONArray dataArray=jsonObject.getJSONArray("data");
                            JSONObject dataObject=dataArray.getJSONObject(0);


                            responseBeniName =dataObject.getString("BenificiaryName");
                            responseAccountNo =dataObject.getString("AccountNo");
                            responseBankName=dataObject.getString("BankName");
                            responseIfsc=dataObject.getString("IfscCode");
                            bankRefId=dataObject.getString("BankRrnNo");
                            transactionId=dataObject.getString("TransactionID");
                            responseAmount=dataObject.getString("Amount");
                            date=dataObject.getString("UpdateDate");
                            status=dataObject.getString("Status");
                            comm=dataObject.getString("Commission");
                            surcharge=dataObject.getString("Surcharge");
                            closingBalance=dataObject.getString("ClosingBalance");
                            cost=dataObject.getString("PayableAmount");


                            final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_bene_status_dialog_layout, null, false);
                            final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                            addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addSenderOTPDialog.setCancelable(false);
                            addSenderOTPDialog.setView(addSenderOTPDialogView);
                            addSenderOTPDialog.show();

                            TextView tvbeniName,tvAccountNumber,tvBankName,tvIfsc,tvBankRefId,tvTransactionId,tvAmount,tvSurcharge,
                                    tvCommission,tvDate,tvStatus;
                            Button btnOk;

                            tvbeniName=addSenderOTPDialogView.findViewById(R.id.tv_beni_name);
                            tvAccountNumber=addSenderOTPDialogView.findViewById(R.id.tv_account_number);
                            tvBankName=addSenderOTPDialogView.findViewById(R.id.tv_bank_name);
                            tvIfsc=addSenderOTPDialogView.findViewById(R.id.tv_ifsc);
                            tvBankRefId=addSenderOTPDialogView.findViewById(R.id.tv_bank_ref_id);
                            tvTransactionId=addSenderOTPDialogView.findViewById(R.id.tv_transaction_id);
                            tvAmount=addSenderOTPDialogView.findViewById(R.id.tv_amount);
                            tvSurcharge=addSenderOTPDialogView.findViewById(R.id.tv_surcharge);
                            tvCommission=addSenderOTPDialogView.findViewById(R.id.tv_commission);
                            tvDate=addSenderOTPDialogView.findViewById(R.id.tv_date);
                            tvStatus=addSenderOTPDialogView.findViewById(R.id.tv_status);
                            btnOk=addSenderOTPDialogView.findViewById(R.id.btn_ok);


                            tvbeniName.setText("Beneficiary Name : "+ responseBeniName);
                            tvAccountNumber.setText("Account Number : "+ responseAccountNo);
                            tvBankName.setText("Bank Name : "+bankName);
                            tvIfsc.setText("Ifsc : "+responseIfsc);
                            tvBankRefId.setText("Bank Ref. ID : "+bankRefId);
                            tvTransactionId.setText("Transaction Id : "+transactionId);
                            tvAmount.setText("Amount : ₹ "+responseAmount);
                            tvDate.setText("Date and Time : "+date);
                            tvStatus.setText("Status : "+status);
                            tvCommission.setText("Comm. : ₹ "+comm);
                            tvSurcharge.setText("Surcharge : ₹ "+surcharge);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addSenderOTPDialog.dismiss();

                                    Intent intent1 = new Intent(context, SharePayoutReportActivity.class);
                                    intent1.putExtra("transactionId",transactionId);
                                    intent1.putExtra("amount",amount);
                                    intent1.putExtra("comm",comm);
                                    intent1.putExtra("balance",closingBalance);
                                    intent1.putExtra("dateTime",date);
                                    intent1.putExtra("status",status);
                                    intent1.putExtra("accountName",benificaryName);
                                    intent1.putExtra("accountNo", responseAccountNo);
                                    intent1.putExtra("bankName",bankName);
                                    intent1.putExtra("surcharge",surcharge);
                                    intent1.putExtra("cost",cost);
                                    intent1.putExtra("dateTime",date);
                                    intent1.putExtra("serviceType","Money");

                                    intent1.putExtra("transactionType","only for not null value");

                                    context.startActivity(intent1);
                                }
                            });

                            pDialog.dismiss();


                        }

                        else if (respoonseCode.equalsIgnoreCase("ERR")){
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                        else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage("Transaction Failed")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).setTitle("Message")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    //bankAccountNumber==UPI ID
    private void payViaUpi(String recipientId, String amount, String upiId, String benificaryName, String benificaryMobileNumber) {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().payUpi(ApiController.Auth_key, userId, deviceId, deviceInfo, amount, recipientId,
                NewMoneyTransferActivity.senderName, NewMoneyTransferActivity.senderMobileNumber, upiId, benificaryName, benificaryMobileNumber,
                "UPI", SenderValidationActivity.lat, SenderValidationActivity.longi);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);


                            responseBeniName = dataObject.getString("BenificiaryName");
                            responseAccountNo = dataObject.getString("AccountNo");
                            responseBankName = dataObject.getString("BankName");
                            responseIfsc = dataObject.getString("IfscCode");
                            bankRefId = dataObject.getString("BankRrnNo");
                            transactionId = dataObject.getString("TransactionID");
                            responseAmount = dataObject.getString("Amount");
                            date = dataObject.getString("UpdateDate");
                            status = dataObject.getString("Status");
                            comm = dataObject.getString("Commission");
                            surcharge = dataObject.getString("Surcharge");


                            final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_bene_status_dialog_layout, null, false);
                            final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                            addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addSenderOTPDialog.setCancelable(false);
                            addSenderOTPDialog.setView(addSenderOTPDialogView);
                            addSenderOTPDialog.show();

                            TextView tvbeniName, tvAccountNumber, tvBankName, tvIfsc, tvBankRefId, tvTransactionId, tvAmount, tvSurcharge,
                                    tvCommission, tvDate, tvStatus;
                            Button btnOk;

                            tvbeniName = addSenderOTPDialogView.findViewById(R.id.tv_beni_name);
                            tvAccountNumber = addSenderOTPDialogView.findViewById(R.id.tv_account_number);
                            tvBankName = addSenderOTPDialogView.findViewById(R.id.tv_bank_name);
                            tvIfsc = addSenderOTPDialogView.findViewById(R.id.tv_ifsc);
                            tvBankRefId = addSenderOTPDialogView.findViewById(R.id.tv_bank_ref_id);
                            tvTransactionId = addSenderOTPDialogView.findViewById(R.id.tv_transaction_id);
                            tvAmount = addSenderOTPDialogView.findViewById(R.id.tv_amount);
                            tvSurcharge = addSenderOTPDialogView.findViewById(R.id.tv_surcharge);
                            tvCommission = addSenderOTPDialogView.findViewById(R.id.tv_commission);
                            tvDate = addSenderOTPDialogView.findViewById(R.id.tv_date);
                            tvStatus = addSenderOTPDialogView.findViewById(R.id.tv_status);
                            btnOk = addSenderOTPDialogView.findViewById(R.id.btn_ok);

                            tvBankName.setVisibility(View.GONE);
                            tvIfsc.setVisibility(View.GONE);


                            tvbeniName.setText("Beneficiary Name : " + responseBeniName);
                            tvAccountNumber.setText("UPI ID : " + responseAccountNo);
                            tvBankRefId.setText("Bank Ref. ID : " + bankRefId);
                            tvTransactionId.setText("Transaction Id : " + transactionId);
                            tvAmount.setText("Amount : ₹ " + responseAmount);
                            tvDate.setText("Date and Time : " + date);
                            tvStatus.setText("Status : " + status);
                            tvCommission.setText("Comm. : ₹ " + comm);
                            tvSurcharge.setText("Surcharge : ₹ " + surcharge);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addSenderOTPDialog.dismiss();
                                    Intent intent = new Intent(context, ShareDmtReportActivity.class);
                                    intent.putExtra("amount", responseAmount);
                                    intent.putExtra("accountNumber", responseAccountNo);
                                    intent.putExtra("beniName", benificaryName);
                                    intent.putExtra("bank", responseBankName);
                                    intent.putExtra("ifsc", responseIfsc);
                                    intent.putExtra("date", date);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("status", status);
                                    context.startActivity(intent);
                                }
                            });

                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).setTitle("Message")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }


    private void deleteBene(String recipientId,int position) {



        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("");
        pDialog.setMessage("Please wait");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().deleteBeneficiary(ApiController.Auth_key, deviceId, deviceInfo, userId, recipientId, mobileNumber,
                SenderValidationActivity.senderId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            String transaction = jsonObject.getString("data");


                            new AlertDialog.Builder(context)
                                    .setTitle("Status")
                                    .setCancelable(false)
                                    .setMessage(transaction)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            context.startActivity(activity.getIntent());

                                        }
                                    })
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return recipientModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ImageView imgBank, imgIfsc;
        TextView tvRecipientName, tvAccountNumber, tvBankName, tvIfsc;
        Button btnPay,btnUpiPay, btnDelete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imgBank = itemView.findViewById(R.id.img_bank);
            imgIfsc = itemView.findViewById(R.id.img_ifsc);

            tvRecipientName = itemView.findViewById(R.id.tv_recipient_name);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvBankName = itemView.findViewById(R.id.tv_bank_name);
            btnPay = itemView.findViewById(R.id.btn_pay);

            btnUpiPay = itemView.findViewById(R.id.btn_upiPay);

            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvIfsc = itemView.findViewById(R.id.tv_ifsc);
        }
    }
}
