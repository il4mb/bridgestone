package com.il4mb.bridgestone.app.ui.Form;

import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.SupportApp;
import com.il4mb.bridgestone.app.api.Callback;
import com.il4mb.bridgestone.app.model.Services;
import com.il4mb.bridgestone.app.model.Category;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.databinding.ActivityFormBinding;
import com.il4mb.bridgestone.tools.Alert;
import com.il4mb.bridgestone.tools.MultiSelect.MultipleSelect;
import com.il4mb.co2.util.Theme;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Response;

public class FormActivity extends SupportApp {

    public static List<Category> categoryModelList;
    public static List<Services> servicesList;


    ActivityFormBinding binding;

    public static final String[] REQUEST_TYPE = {"Drop of", "Pick Up"};

    public List<Services> selectedListService = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // select category
        ArrayAdapter<Category> categoriest_adapter =
                new ArrayAdapter<>(this, R.layout._spinner, categoryModelList);
        binding.selectCategory.setAdapter(categoriest_adapter);


        // select type request
        ArrayAdapter<String> request_type_adapter =
                new ArrayAdapter<>(this, R.layout._spinner, REQUEST_TYPE);
        binding.selectServiceType.setAdapter(request_type_adapter);
        binding.selectServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextInputLayout parent_input_address = binding.inputPickupAddress;

                int index = binding.selectServiceType.getSelectedItemPosition();
                String selected = REQUEST_TYPE[index];

                if (selected.equals("Pick Up")) {
                    parent_input_address.setVisibility(View.VISIBLE);
                } else {
                    parent_input_address.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        MultipleSelect.Builder builder = new MultipleSelect.Builder();
        builder.setListData(FormActivity.servicesList)
                .setContext(getApplicationContext());
        builder.setListener(selectedList -> {

            if (selectedList.size() <= 0) {

                selectedListService = new ArrayList<>();

            } else {

                final Type typeServices = new TypeToken<List<Services>>() {
                }.getType();
                selectedListService = new Gson().fromJson(new Gson().toJson(selectedList), typeServices);
            }

            controlSelectedListService();
        });

        MultipleSelect multipleSelect = new MultipleSelect(builder);
        binding.selectedListServices.setOnClickListener((view -> multipleSelect.start(getSupportFragmentManager())));

        controlSelectedListService();

        binding.submitButton.setOnClickListener((v) -> submitPrepare());
    }

    public void controlSelectedListService() {

        FlexboxLayout root = binding.selectedListServices;
        root.removeAllViews();

        if (selectedListService.size() <= 0) {

            TextView emptyText = new TextView(getApplicationContext());
            emptyText.setText("Click Here To Select Services");
            emptyText.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat));
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
            emptyText.setTextColor(Color.WHITE);
            emptyText.setTextSize(17f);

            root.addView(emptyText);

            return;
        }

        for (Object object : selectedListService) {

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null));
            bg.setCornerRadius(8f);

            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 5, 5, 5);

            TextView textItem = new TextView(getApplicationContext());
            textItem.setText(String.valueOf(object));
            textItem.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat));
            textItem.setGravity(Gravity.CENTER);
            textItem.setTextColor(Color.WHITE);
            textItem.setTextSize(16f);
            textItem.setPadding(18, 12, 18, 12);
            textItem.setLayoutParams(layoutParams);
            textItem.setBackground(bg);

            root.addView(textItem);

        }
    }

    public String[] getSelectedListServices() {

        List<String> values = new ArrayList<>();

        for (Services serviceData : selectedListService) {
            values.add(String.valueOf(serviceData));
        }
        return values.toArray(new String[values.size()]);
    }

    public String findCategoryId(String category) {
        String value = null;
        for (Category categoryModel : categoryModelList) {
            if(categoryModel.category.equals(category)) {
                value = String.valueOf(categoryModel.id);
                break;
            }
        }
        return value;
    }


    public void submitPrepare() {

        String srCategory = binding.selectCategory.getSelectedItem().toString();
        String srMachineName = binding.inputMachineName.getEditText().getText().toString();
        String srMachineModel = binding.inputMachineModel.getEditText().getText().toString();
        String srMachineRegisterNumber = binding.inputMachineRegistrationNumber.getEditText().getText().toString();
        String srClientFullName = binding.inputClientFullName.getEditText().getText().toString();
        String srContact = binding.inputContact.getEditText().getText().toString();
        String srEmail = binding.inputEmail.getEditText().getText().toString();
        String srAddress = binding.inputAddress.getEditText().getText().toString();
        String[] srServices = getSelectedListServices();
        String srServiceType = binding.selectServiceType.getSelectedItem().toString();
        String srPickupAddress = binding.inputPickupAddress.getEditText().getText().toString();

        try {

            if (srCategory.length() <= 0) {
                throw new Exception("Please select type!");
            }
            if (srMachineName.length() <= 0) {
                throw new Exception("Please enter machine name column!");
            }
            if (srMachineModel.length() <= 0) {
                throw new Exception("Please enter machine model column!");
            }
            if (srMachineRegisterNumber.length() <= 0) {
                throw new Exception("Please enter machine registration number column!");
            }
            if (srClientFullName.length() <= 0) {
                throw new Exception("Please enter client full name column!");
            }
            if (srContact.length() <= 0) {
                throw new Exception("Please enter contact column!");
            }
            if (srEmail.length() <= 0) {
                throw new Exception("Please enter email column!");
            }
            if (srAddress.length() <= 0) {
                throw new Exception("Please enter address column!");
            }
            if (srServices.length <= 0) {
                throw new Exception("Please select services!");
            }
            if (srServiceType.length() <= 0) {
                throw new Exception("Please select request type!");
            }
            if (!srServiceType.equals("Pick Up")) {

                srPickupAddress = "";

            } else {

                if (srPickupAddress.length() <= 0) {
                    throw new Exception("Please enter pick-up address!");
                }

            }

            FormBody.Builder requestBody = new FormBody.Builder();

            // for `service_requests` table
            requestBody.add("owner_name", srClientFullName);
            requestBody.add("category_id", findCategoryId(srCategory));
            requestBody.add("service_type", srServiceType);


            // for meta `service_requests` table
            for (Services services: selectedListService) {
                requestBody.add("meta[service_id][]", String.valueOf(services.id));
            }
            requestBody.add("meta[machine_name]", srMachineName);
            requestBody.add("meta[machine_model]", srMachineModel);
            requestBody.add("meta[machine_registration_number]", srMachineRegisterNumber);
            requestBody.add("meta[contact]", srContact);
            requestBody.add("meta[email]", srEmail);
            requestBody.add("meta[address]", srAddress);
            requestBody.add("meta[pickup_address]", srPickupAddress);





            getResApi(new Callback(this) {
                @Override
                public void onResponse(Response response) throws IOException {
                    super.onResponse(response);

                    ResponseModel responseData = new Gson().fromJson(response.body().string(), ResponseModel.class);

                    if(responseData.code == 1)
                    {


                        Alert alert = Alert.newInstance();
                        alert.setTheme(new Theme(Color.parseColor("#32A852")));
                        alert.setTitle("Request Send");
                        alert.setText(responseData.message);
                        alert.show(getSupportFragmentManager(), "alert");

                        FormActivity.this.finish();

                    } else {

                        throw new IOException(responseData.message);
                    }
                }
            }).sendServicesRequest(requestBody.build());


        } catch (Exception e) {

            Alert alert = Alert.newInstance();
            alert.setTheme(new Theme(Color.parseColor("#FF584D")));
            alert.setTitle("Caught Error");
            alert.setText(e.getMessage());
            alert.show(getSupportFragmentManager(), "alert");
        }
    }
}