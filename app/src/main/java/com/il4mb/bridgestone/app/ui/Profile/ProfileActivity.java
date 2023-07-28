package com.il4mb.bridgestone.app.ui.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.app.SupportApp;
import com.il4mb.bridgestone.app.api.Callback;
import com.il4mb.bridgestone.app.api.ResApi;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.app.ui.Login.LoginActivity;
import com.il4mb.bridgestone.app.ui.Panel.PanelActivity;
import com.il4mb.bridgestone.databinding.ActivityProfileBinding;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ProfileActivity extends SupportApp {

    ActivityProfileBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.home.setOnClickListener(v-> goHome());
        binding.logout.setOnClickListener(view -> logout());

        prepareData();
    }


    public void prepareData() {
        getResApi(new Callback(this) {
            @Override
            public void onResponse(Response response) throws IOException {
                super.onResponse(response);



                String responseBody = response.body().string();

                System.out.println(responseBody);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        ResponseModel responseData = new Gson().fromJson(responseBody, ResponseModel.class);
                        Type type = new TypeToken<Map<String, String>>(){}.getType();
                        Map<String, String> mapData = new Gson().fromJson(new Gson().toJson(responseData.data), type);

                        binding.firstName.setText(mapData.get("firstname"));
                        binding.lastName.setText(mapData.get("lastname"));
                        binding.username.setText(mapData.get("username"));

                        if(mapData.get("avatar") != null) {

                            String avaUrlStr = null;

                            try {

                                avaUrlStr = SupportApp.getAbsoluteUrl(ResApi.BASE_URL, mapData.get("avatar"));
                                URL avaUrl = new URL(avaUrlStr);
                                Bitmap bitmap = BitmapFactory.decodeStream(avaUrl.openConnection().getInputStream());
                                binding.profileImage.setImageBitmap(bitmap);

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }

                    }
                });


            }
        }).getUserProfile();
    }
    public void logout() {

        getPreferences().edit().remove(LoginActivity.cookie_name).apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(intent);
    }

    public void goHome() {
        this.finish();
    }


}
