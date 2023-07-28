package com.il4mb.bridgestone.app.ui.Panel;

import static com.il4mb.bridgestone.app.ui.Login.LoginActivity.cookie_name;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.SupportApp;
import com.il4mb.bridgestone.app.api.Callback;
import com.il4mb.bridgestone.app.background.BackgroundService;
import com.il4mb.bridgestone.app.background.BackgroundTask;
import com.il4mb.bridgestone.app.background.RequestControl;
import com.il4mb.bridgestone.app.model.ServiceRequest;
import com.il4mb.bridgestone.app.model.Services;
import com.il4mb.bridgestone.app.model.Category;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.app.ui.Form.FormActivity;
import com.il4mb.bridgestone.app.ui.Login.LoginActivity;
import com.il4mb.bridgestone.app.ui.Profile.ProfileActivity;
import com.il4mb.bridgestone.databinding.ActivityPanelBinding;
import com.il4mb.co2.Layer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Response;

public class PanelActivity extends SupportApp {

    public static int BG_TASK_ID = 0021;
    ActivityPanelBinding binding;
    PanelViewModel viewModel;

    private BackgroundTaskServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PanelViewModel.class);
        binding = ActivityPanelBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.addNewRequest.setOnClickListener((e) -> prepareServiceRequest());
        // binding.swiperefresh.setOnRefreshListener(this::updateLiveData);
        binding.profileImage.setOnClickListener(v->showProfile());

        viewModel.getListLiveData().observe(this, serviceRequests -> {

            if(serviceRequests != null) {

                binding.emptyMessage.setVisibility(View.GONE);
                ListAdapter adapter = new ListAdapter(PanelActivity.this, serviceRequests);
                binding.listView.setAdapter(adapter);

            } else {
                binding.emptyMessage.setVisibility(View.VISIBLE);
            }
        });

        binding.listView.setOnItemClickListener((adapterView, view, i, l) -> detailRequest((ServiceRequest) adapterView.getItemAtPosition(i)));

        serviceConnection = new BackgroundTaskServiceConnection();
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void detailRequest(ServiceRequest request) {

        getResApi(new Callback(this) {
            @Override
            public void onResponse(Response response) throws IOException {
                super.onResponse(response);

                ResponseModel responseData = new Gson().fromJson(response.body().string(), ResponseModel.class);

                Type typeof = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> mapData = new Gson().fromJson(new Gson().toJson(responseData.data), typeof);

                showDetailRequest(mapData);
            }
        }).getRequestDetail(request.id);
    }

    public void showDetailRequest(Map<String, Object> stringMap) {

        try {

            int bgColor = Color.parseColor("#212336");
            Layer layer = new Layer(getApplicationContext());
            layer.layout.setBackgroundColor(bgColor);
            Layer.customView(layer.layout, bgColor, Color.TRANSPARENT);
            layer.title.setText("Request detail");
            layer.title.setTextColor(Color.WHITE);
            layer.title.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat), Typeface.BOLD);


            SortedSet<String> keys = new TreeSet<>(stringMap.keySet());

            ScrollView sl = new ScrollView(getApplicationContext());
            sl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            sl.setPadding(0, 0, 0, 250);

            LinearLayout ly = new LinearLayout(getApplicationContext());
            ly.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ly.setOrientation(LinearLayout.VERTICAL);
            ly.setWeightSum(1);
            sl.addView(ly);

            for (String key : keys) {

                Object val = stringMap.get(key);

                key = key.replaceAll("[^a-zA-Z0-9]+", " ");


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                LinearLayout l = new LinearLayout(getApplicationContext());

                if(key.equals("services")) {

                    l.setOrientation(LinearLayout.VERTICAL);

                    TextView k = new TextView(getApplicationContext());

                    k.setText(key);
                    k.setTextSize(14f);
                    k.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat), Typeface.BOLD);
                    k.setPadding(25, 25, 18, 25);
                    k.setTextColor(Color.WHITE);

                    FlexboxLayout f = new FlexboxLayout(getApplicationContext());
                    f.setFlexWrap(FlexWrap.WRAP);
                    f.setAlignItems(AlignItems.STRETCH);
                    f.setAlignItems(AlignItems.STRETCH);
                    f.setPadding(35,0, 0, 15);


                    assert val != null;
                    if(val.getClass().equals(ArrayList.class)) {

                        ArrayList<String> servicesList = (ArrayList<String>) val;

                        for (String service: servicesList) {

                            GradientDrawable bg = new GradientDrawable();
                            bg.setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null));
                            bg.setCornerRadius(8f);

                            ViewGroup.MarginLayoutParams layoutParams2 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams2.setMargins(5, 5, 5, 5);

                            TextView textItem = new TextView(getApplicationContext());
                            textItem.setText(String.valueOf(service));
                            textItem.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat));
                            textItem.setGravity(Gravity.CENTER);
                            textItem.setTextColor(Color.WHITE);
                            textItem.setTextSize(14f);
                            textItem.setPadding(16, 8, 16, 8);
                            textItem.setLayoutParams(layoutParams2);
                            textItem.setBackground(bg);

                            f.addView(textItem);
                        }
                    }

                    FrameLayout d= new FrameLayout(getApplicationContext());
                    d.setBackground(new ColorDrawable(Color.WHITE));
                    d.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

                    l.addView(k);
                    l.addView(f);
                    l.addView(d);

                }
                else {

                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setWeightSum(2);

                    TextView k = new TextView(getApplicationContext());
                    TextView e = new TextView(getApplicationContext());

                    k.setText(key);
                    k.setTextSize(14f);
                    k.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat));
                    k.setPadding(25, 25, 18, 25);
                    k.setTextColor(Color.WHITE);

                    e.setText(String.valueOf(val));
                    e.setTextSize(14f);
                    e.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat));
                    e.setPadding(25, 25, 18, 25);
                    e.setTextColor(Color.WHITE);
                    e.setGravity(Gravity.END);

                    k.setLayoutParams(layoutParams);
                    e.setLayoutParams(layoutParams);


                    if(key.equals("status")) {

                        String status = "Unknown";
                        String hexCol = "#000000";
                        switch (Integer.parseInt(String.valueOf(val))) {
                            case 0:
                                status = "pending";
                                hexCol = "#7e7f80";
                                break;

                            case 1:
                                status = "follow-up";
                                hexCol = "#277ed6";
                                break;
                            case 2:
                                status = "confirmed";
                                hexCol = "#277ed6";
                                break;

                            case 3:
                                status = "on-progress";
                                hexCol = "#d4aa2f";
                                break;

                            case 4:
                                status = "done";
                                hexCol = "#3cb031";
                                break;

                            case 5:
                                status = "canceled";
                                hexCol = "#d11b33";
                        }

                        GradientDrawable bg = new GradientDrawable();
                        bg.setColor(Color.parseColor(hexCol));
                        bg.setCornerRadius(12f);
                        e.setBackground(bg);
                        e.setTextSize(12f);
                        e.setPadding(18, 8, 18, 8);
                        e.setText(status);

                        e.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                        l.setWeightSum(1);
                    }

                    l.addView(k);
                    l.addView(e);
                }

                ly.addView(l);
            }


            LinearLayout container = layer.getContainer();
            container.setWeightSum(1);
            container.addView(sl);
            container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            layer.show(getSupportFragmentManager(), "layer");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void prepareServiceRequest() {

        getResApi(new Callback(this) {
            @Override
            public void onResponse(Response response) throws IOException {
                super.onResponse(response);

                ResponseModel responseData = new Gson().fromJson(response.body().string(), ResponseModel.class);

                if (responseData.code == 1) {


                    final Type typeData = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> data = new Gson().fromJson(new Gson().toJson(responseData.data), typeData);


                    final Type typeCategory = new TypeToken<List<Category>>() {
                    }.getType();
                    FormActivity.categoryModelList = new Gson().fromJson(new Gson().toJson(data.get("categories")), typeCategory);


                    final Type typeServices = new TypeToken<List<Services>>() {
                    }.getType();
                    FormActivity.servicesList = new Gson().fromJson(new Gson().toJson(data.get("services")), typeServices);

                    Intent i = new Intent(getApplicationContext(), FormActivity.class);
                    startActivity(i);

                } else {

                    throw new IOException(responseData.message);
                }
            }
        }).serviceReqPrepare();

    }


    private void showProfile() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    private BackgroundService backgroundService;
    private class BackgroundTaskServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Retrieve the service instance from the binder
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            backgroundService = binder.getService();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    backgroundService.requestControl.setCookie(getPreferences().getString(cookie_name, null));
                    viewModel.listLiveData.postValue(backgroundService.requestControl.getTempData());
                }
            }, 0, 1000);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Handle the case when the service connection is disconnected
            backgroundService = null;
        }
    }
}
