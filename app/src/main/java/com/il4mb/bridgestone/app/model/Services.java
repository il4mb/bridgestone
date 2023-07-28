package com.il4mb.bridgestone.app.model;

import androidx.annotation.NonNull;

public class Services {
    public int id;
    public String service;
    public String description;
    public int status;
    public String date_created;

    @NonNull
    @Override
    public String toString() {
        return service;
    }
}
