package com.hiwot.digitalmediaplus.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class APIUtil {
    static ArrayList<Dialog> dialogs = new ArrayList<>();
    public static AlertDialog loadingDialog;
    private static MediaPlayer mMediaPlayer;

    public static ArrayList<Dialog> getDialogs(int count) {
        return dialogs;
    }

    public static Call postWithAttachments(HashMap<String, String> map, HashMap<String, Bitmap> imgData, String endpoint) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Bitmap> bitmapEntry : imgData.entrySet()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmapEntry.getValue().compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] data = bos.toByteArray();
            builder.addFormDataPart(bitmapEntry.getKey(), "image" + new Random().nextInt(1000), RequestBody.create(MediaType.parse("image/jpeg"), data));
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cookie manager
        CookieManager manager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(manager))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    public static Call postImage(HashMap<String, String> map, String imageName, Bitmap bitmap, String endpoint) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] data = bos.toByteArray();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        builder.addFormDataPart(imageName, "image" + new Random().nextInt(1000), RequestBody.create(MediaType.parse("image/jpeg"), data));
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cookie manager
        CookieManager manager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(manager))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }
    public static Call postRawData(Context context, HashMap<String, String> map, String imageName, byte[] data, String endpoint) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        if(data.length>0)
            builder.addFormDataPart("image", imageName, RequestBody.create(MediaType.parse("application/octet-stream"), data));
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cookie manager
        CookieManager manager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(manager))
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    public static Call postWithFile(Context context, HashMap<String, String> map, String endpoint, File file, String fileName) {
        String API_SECRET = "api_lokdon";// + CipherControl.getInstance().encryptGenericData("FLDSaJL923749571234sjljasdfjFFasdfll");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        builder.addFormDataPart("API_SECRET", API_SECRET);
        builder.addFormDataPart(fileName, fileName, RequestBody.create(MediaType.parse("raw"), file));
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cookie manager
        CookieManager manager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(manager))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    public static Call post(Context context, HashMap<String, String> map, String endpoint) {
        String API_SECRET = "api_lokdon";// + CipherControl.getInstance().encryptGenericData("FLDSaJL923749571234sjljasdfjFFasdfll");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
            Log.d("validide", "post, " + entry.getKey() + ": " + entry.getValue());
        }
        builder.addFormDataPart("API_SECRET", API_SECRET);
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        Log.e("hiwot","requestBody: "+ Objects.requireNonNull(request.body()).toString());
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cookie manager  `
        CookieManager manager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(manager))
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        return client.newCall(request);
    }

    public static void toast(Context context, String message, Exception ex) {
        if (ex != null)
            Log.e("com/odinaala/lcn/schat", message, ex);
        else
            Log.d("com/odinaala/lcn/schat", message);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });

    }


    public static String decryptData(String cipher) {
        try {
            return "";//CipherControl.getInstance().decryptGenericData(cipher);
        } catch (Exception ex) {
            Log.e("lokdon", "cipher might be in plain-text");
        }
        return cipher;
    }

}
