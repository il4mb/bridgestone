package com.il4mb.bridgestone.app.ui.Panel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.app.api.ResponseModel;
import com.il4mb.bridgestone.app.model.ServiceRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelViewModel extends ViewModel {

    MutableLiveData<List<ServiceRequest>> listLiveData;

    public PanelViewModel() {

        listLiveData = new MutableLiveData<>();
        listLiveData.setValue(new ArrayList<>());

    }

    public MutableLiveData<List<ServiceRequest>> getListLiveData() {
        return listLiveData;
    }
}
