package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wts.aepssevaa.R;
import com.wts.aepssevaa.retrofit.ApiController;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdminQrCodeActivity extends AppCompatActivity {

    String userId,deviceId, deviceInfo;
    SharedPreferences sharedPreferences;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qr_code);
        qrImage = findViewById(R.id.img_qrcode);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AdminQrCodeActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        getQRImage();

    }

    private void getQRImage() {
        final ProgressDialog pDialog = new ProgressDialog(AdminQrCodeActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = ApiController.getInstance().getApi().getQRImage(ApiController.Auth_key);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            String image = responseObject.getString("data");
                            Picasso.get().load(image).into(qrImage);


                        } else {
                            pDialog.dismiss();

                        }

                    } catch (Exception e) {
                        pDialog.dismiss();

                    }
                } else {
                    pDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

            }
        });
    }
}