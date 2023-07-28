package com.il4mb.bridgestone.app.model;

import androidx.annotation.NonNull;

public class Category {
    public int id;
    public String category;
    public int status;
    public String date_created;

    @NonNull
    @Override
    public String toString() {
        return category;
    }
}
