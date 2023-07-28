package com.il4mb.bridgestone.app.ui.Panel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.gson.reflect.TypeToken;
import com.il4mb.bridgestone.R;
import com.il4mb.bridgestone.app.model.ServiceRequest;
import com.il4mb.bridgestone.app.model.Services;
import com.il4mb.co2.Layer;

import java.security.Provider;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    public Context context;
    public List<ServiceRequest> requestList;

    public ListAdapter(Context context, List<ServiceRequest> requestList) {

        this.context        = context;
        this.requestList    = requestList;
    }
    @Override
    public int getCount() {
        return this.requestList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.requestList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.requestList.indexOf(this.requestList.get(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ServiceRequest request = (ServiceRequest) getItem(i);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);

        String status = "Unknown";
        String hexCol = "#000000";
        switch (request.status) {
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



        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        TextView ownerView          = createTextView(request.owner_name);
        TextView statusView         = createTextView(status);
        TextView serviceTypeView    = createTextView(request.service_type);
        TextView dateCreateView     = createTextView(request.date_created);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(hexCol));
        bg.setCornerRadius(12f);
        statusView.setBackground(bg);
        statusView.setTextSize(12f);
        statusView.setPadding(18, 8, 18, 8);


        ownerView.setLayoutParams(layoutParams);
        ownerView.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat), Typeface.BOLD);
        serviceTypeView.setLayoutParams(layoutParams);


        dateCreateView.setTextSize(12f);


        LinearLayout row1 = new LinearLayout(context);
        LinearLayout row2 = new LinearLayout(context);


        row1.setWeightSum(1);
        row1.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        row2.setPadding(35, 0,0,0);


        row1.addView(ownerView);
        row1.addView(statusView);
        row2.addView(serviceTypeView);
        row2.addView(dateCreateView);


        root.addView(row1);
        root.addView(row2);

        return root;
    }


    private TextView createTextView(String text) {

        TextView e = new TextView(context);
        e.setText(text);
        e.setTextSize(17f);
        e.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat));
        e.setPadding(25, 25, 18, 25);
        e.setTextColor(Color.WHITE);

        return e;
    }
}
