package com.il4mb.bridgestone.app.api;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.il4mb.bridgestone.app.SupportApp;
import com.il4mb.bridgestone.tools.Alert;
import com.il4mb.co2.util.Theme;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class Callback implements okhttp3.Callback {

    SupportApp supportApp;

    public Callback(SupportApp supportApp) {

        this.supportApp = supportApp;
        this.supportApp.showLoader();
    }

    public Callback(SupportApp supportApp,  @Nullable Boolean loader) {

        this.supportApp = supportApp;
    }



    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {

        try {

            Alert alert = Alert.newInstance(new Theme(Color.parseColor("#ff9d96")));
            alert.setTitle("Fatal Failure");
            alert.setText(e.getMessage());

            FragmentManager fmm = supportApp.getSupportFragmentManager();
            if (!fmm.isDestroyed()) {
                alert.show(fmm, "alert");
            }

        } catch (Exception _e) {

            _e.printStackTrace();

        } finally {

            supportApp.hideLoader();
            e.printStackTrace();

        }


    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

        try {

            onResponse(response);

        } catch (Exception e) {

            String message = e.getMessage();
            if(message != "closed") {

                Alert alert = Alert.newInstance(new Theme(Color.parseColor("#ff9d96")));
                alert.setTitle("Fatal Error");
                alert.setText(message);

                FragmentManager fmm = supportApp.getSupportFragmentManager();
                if(!fmm.isDestroyed()) {
                    alert.show(fmm, "alert");
                }
            }

            e.printStackTrace();

        } finally {

            supportApp.hideLoader();

        }
    }

    public void onResponse(Response response) throws IOException {
    }
}
