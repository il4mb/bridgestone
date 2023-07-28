package com.il4mb.bridgestone.app.background;

import static com.il4mb.bridgestone.app.SupportApp.CHANNEL_ID;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.api.ResApi;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.app.model.ServiceRequest;
import com.il4mb.bridgestone.app.ui.Panel.PanelActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RequestControl implements BackgroundTask {

    Context context;
    ResApi resApi;
    String cookie;
    List<ServiceRequest> tempData = new ArrayList<>();
    public RequestControl(Context context) {
        this.context = context;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public List<ServiceRequest> getTempData() {
        return tempData;
    }

    @Override
    public void onExecute() {
        if(cookie != null) {
            resApi = new ResApi(cookie);
        } else {
            resApi = null;
        }

        if(resApi != null) {
            resApi.callback = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    ResponseModel responseModel = new Gson().fromJson(response.body().string(), ResponseModel.class);
                    String dataJson = new Gson().toJson(responseModel.data);

                    Type typeListReq = new TypeToken<List<ServiceRequest>>(){}.getType();
                    List<ServiceRequest> requestList = new Gson().fromJson(dataJson, typeListReq);

                    compareData(tempData, requestList);
                }
            };

            resApi.getUserRequest();
        }
    }


    public void compareData(List<ServiceRequest> oldData, List<ServiceRequest> newData) {

        for (int k1 = 0; k1 < newData.size(); k1++) {

            ServiceRequest data1 = newData.get(k1);
            boolean isNewData = true;

            if(oldData.size() <= 0) {
                 isNewData = false;
            }
            for (ServiceRequest data2 : oldData) {
                if (data1.status == data2.status) {
                    isNewData = false;
                    break;
                }
            }

            if (isNewData) {
                String judul = "Update On Request";
                String body =  String.format("Dear to the owner %s, the request has been updated please click to check", data1.owner_name);
                showDefaultNotification(data1.id, judul, body);
            }
        }
        tempData = newData;
    }

    public void showDefaultNotification(int notificationId, String textTitle, String textDescription) {

        Intent callBack = new Intent(this.context, PanelActivity.class);
        callBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, callBack, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bridgestone_logo_transparent)
                .setContentTitle(textTitle)
                .setContentText(textDescription)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

}
