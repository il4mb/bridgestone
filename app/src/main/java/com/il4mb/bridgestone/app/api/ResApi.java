package com.il4mb.bridgestone.app.api;

import static com.il4mb.bridgestone.app.ui.Login.LoginActivity.cookie_name;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.il4mb.bridgestone.app.SupportApp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResApi {
    public static final String BASE_URL = "https://bridgestone-sumberlawan.com/api/v1";
    public OkHttpClient client;
    public Callback callback;

    public ResApi(SupportApp supportApp) {

        client = new OkHttpClient();


        String cookieData = supportApp.getPreferences().getString(cookie_name, null);
        if (cookieData != null) {

            client = new OkHttpClient().newBuilder()
                    .addInterceptor(chain -> {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookieData)
                                .build();
                        return chain.proceed(authorized);
                    })
                    .build();

        }
    }

    public ResApi(String cookie) {
        client = new OkHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    final Request original = chain.request();
                    final Request authorized = original.newBuilder()
                            .addHeader("Cookie", cookie)
                            .build();
                    return chain.proceed(authorized);
                })
                .build();
    }

    /**
     * @param username - adalah id dari user
     * @param password - adalah kata sandi user
     * @return
     * @throws Exception
     */
    public void login(String username, String password) {

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * di gunakan setelah berhasil login
     */
    public void loginValidate() {

        Request request = new Request.Builder()
                .url(BASE_URL + "/login-validate")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void serviceReqPrepare() {

        Request request = new Request.Builder()
                .url(BASE_URL + "/service-req-prepare")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void sendServicesRequest(RequestBody requestBody) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/service-req-submit")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getUserRequest() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/service-req")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getUserProfile() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/profile")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getRequestDetail(int id) {

        RequestBody requestBody = new FormBody.Builder()
                .add("request_id", String.valueOf(id))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/service-req-detail")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
