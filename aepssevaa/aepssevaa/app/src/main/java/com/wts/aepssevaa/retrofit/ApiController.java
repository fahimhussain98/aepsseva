package com.wts.aepssevaa.retrofit;

// Manage API Controller Object and Call Interface

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiController
{
    static {
        System.loadLibrary("native-lib");
    }

    public native static String getAPIHost();
    public native static String getBasicAuth();
    public native static String getAPIHostSecond();

    public static final String url = getAPIHost();
    public static final String urlGet = getAPIHostSecond();
    public static final String Auth_key =getBasicAuth();

    private static ApiController clientObject;
    private static Retrofit retrofit;
    private static Retrofit retrofitGet;


    ApiController()
    {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(7000, TimeUnit.SECONDS)
                .readTimeout(7000, TimeUnit.SECONDS)
                .build();

        retrofit=new Retrofit.Builder()
                .baseUrl(url).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitGet=new Retrofit.Builder()
                .baseUrl(urlGet).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public static synchronized ApiController getInstance()
    {
        if(clientObject ==null)
            clientObject =new ApiController();  //create Retrofit Object because call constructor
        return clientObject;
    }

    //  return all methods in apiset
    public apiset getApi()
    {
        return retrofit.create(apiset.class);   //return all methods in apiset
    }

    public apiset getApiSecond() {

        return retrofitGet.create(apiset.class);
    }
}
