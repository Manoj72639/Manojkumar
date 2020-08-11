package com.biztechsoftsys.neoesh.API;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class NetworkClass {


    // String BASE_URL = "https://ardhas.tk/event/v1/event-api/eve_attendance/Attendance/";
    //String BASE_URL = "http://13.126.29.69/EventManagementDoha/event-api/eve_attendance/Attendance/";
    //String BASE_URL = "http://192.168.0.250/neoesh/dev/v1/neoehs-api/";
    // String BASE_URL = "http://192.168.0.250/neoesh/dev/current_demo/neoehs-api/";
    // String BASE_URL = "http://103.88.129.32/neoesh/dev/current_demo/neoehs-api/";
    //String BASE_URL = "http://52.66.184.170/dev/current_audit/current_demo/neoehs-api/";
    //String BASE_URL = "http://52.66.184.170/dev/current_demo/neoehs-api/";
   // String BASE_URL = "http://52.66.184.170/dev_demo/v1/neoehs-api/";
    // String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/current_demo/neoehs-api/";
  //  String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/construction_neoehs/current_demo/neoehs-api/";
   // String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/demo_bal/neoehs-api/";

   // String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/neoehs_ban_airport/neoehs-api/";

  //  String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/current_demo/neoehs-api/";
    String BASE_URL = "http://52.66.184.170/dev_demo/current_demo/current_demo_maly/neoehs-api/";



    //dev/current_demo/
    //String BASE_URL = "http://52.66.184.170/dev/demo_app/current_demo/neoehs-api/";
    // String BASE_URL = "http://52.66.184.170/dev/current_demo/neoehs-api/";

    //String BASE_URL = "http://13.126 .106.36/dev_demo/v1/neoehs-api/";
    // String BASE_URL = "http://13.126.106.36/dev_demo/v1/neoehs-api/";
    //String BASE_URL = "http://192.168.0.250/neoesh/dev/vdemo/neoehs-api/";

    //String BASE_URL = "http://13.126.29.69/event/demo/event-api/eve_attendance/Attendance/";
    //  String BASE_URL = "http://52.66.175.90/event/v1/event-api/eve_attendance/Attendance/";
    // String BASE_URL = "http://13.126.29.69/event/v1/event-api/eve_attendance/Attendance/";

    public static Retrofit retrofitRef;

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkInfo typemo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo tywi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        NetworkInfo tywifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (netInfo != null && netInfo.isConnectedOrConnecting() || typemo != null && typemo.isConnectedOrConnecting() || tywi != null && tywi.isConnectedOrConnecting() || tywifi != null && tywifi.isConnectedOrConnecting()) {

            return true;
        } else {

            return false;
        }
    }

    public Retrofit callretrofit() {


        OkHttpClient okClient = new OkHttpClient.Builder().connectTimeout(2000, TimeUnit.SECONDS).readTimeout(2000, TimeUnit.SECONDS).writeTimeout(2000, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request().newBuilder().build();
                //  Default  Details
                // .addHeader("Accept","Application/JSON").build();
                return chain.proceed(request);
            }
        }).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofitRef = new Retrofit.Builder().baseUrl(BASE_URL).client(okClient).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson))

                .build();
        // End
        return retrofitRef;

    }


    public static String getApplicationVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

