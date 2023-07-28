package com.il4mb.bridgestone.app.ui.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.SupportApp;
import com.il4mb.bridgestone.app.api.Callback;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.app.ui.Panel.PanelActivity;
import com.il4mb.bridgestone.databinding.ActivityMainBinding;
import com.il4mb.bridgestone.tools.Alert;
import com.il4mb.co2.Message;
import com.il4mb.co2.util.Theme;

import java.io.IOException;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class LoginActivity extends SupportApp {

    public static final String cookie_name = "auth_client";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.buttonMasuk.setOnClickListener((e) -> login());

        String cookie = getPreferences().getString(cookie_name, null);
        if(cookie != null) {
            validateLogin();
        }
    }

    private void login() {

        String username = binding.inputUsername.getText().toString();
        String password = binding.inputPassword.getText().toString();

        if (username.length() <= 5) {
            Message message = new Message(getApplicationContext());
            message.text = "Please input username minimum 5 character";
            message.title = "Invalid Input";
            message.setTheme(Theme.DANGER());
            message.show();
            return;
        }

        if (password.length() <= 5) {
            Message message = new Message(getApplicationContext());
            message.text = "Please input password minimum 5 character";
            message.title = "Invalid Input";
            message.setTheme(Theme.DANGER());
            message.show();
            return;
        }

        getResApi(
                new Callback(this) {

                    @Override
                    public void onResponse(Response response) throws IOException {

                        // GET COOKIE FROM REQUEST
                        HttpUrl url = response.networkResponse().request().url();
                        List<Cookie> Listcookie = Cookie.parseAll(url, response.headers());

                        for (Cookie cookie: Listcookie) {
                            if(cookie.name().equals(cookie_name)) {

                                getPreferences().edit().putString(cookie_name, cookie.toString()).apply();
                                break;
                            }
                        }
                        // GET COOKIE FROM REQUEST


                        // GET RESPONSE FROM REQ
                        ResponseModel dataModel = new Gson().fromJson(response.body().string(), ResponseModel.class);

                        if (dataModel.code == 1) {

                            validateLogin();

                        } else {

                            Alert alert = Alert.newInstance();
                            alert.setTheme(new Theme(Color.parseColor("#FF6B61")));
                            alert.setTitle("Response Error");
                            alert.setText(dataModel.message);
                            alert.show(getSupportFragmentManager(), "alert");

                        }
                        // GET RESPONSE FROM REQ
                    }
                })
                .login(username, password);
    }
    private void validateLogin() {

        getResApi(new Callback(this) {
            @Override
            public void onResponse(Response response) throws IOException {
                super.onResponse(response);

                ResponseModel dataModel = new Gson().fromJson(response.body().string(), ResponseModel.class);

                Alert alert = Alert.newInstance();

                if (dataModel.code == 1) {

                    alert.setTheme(new Theme(ResourcesCompat.getColor(getResources(), R.color.succcess, null)));
                    alert.setTitle("Successful");
                    alert.setText(dataModel.message);

                    Intent i = new Intent(getApplicationContext(), PanelActivity.class);
                    startActivity(i);
                    LoginActivity.this.finish();

                } else {

                    getPreferences().edit().remove(cookie_name).apply();

                    alert.setTheme(new Theme(Color.parseColor("#FF6B61")));
                    alert.setTitle("Response Error");
                    alert.setText(dataModel.message);
                }

                FragmentManager fmm = getSupportFragmentManager();
                if(! fmm.isDestroyed()) {
                    alert.show(fmm, "alert");
                }
            }
        }).loginValidate();

    }
}
