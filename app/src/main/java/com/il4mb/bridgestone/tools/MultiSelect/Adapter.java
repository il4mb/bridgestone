package com.il4mb.bridgestone.tools.MultiSelect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.gson.Gson;
import com.il4mb.bridgestone.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter {

    int activeColor;
    List<?> objectList;
    Context context;

    List<Object> selected = new ArrayList<>();

    public Adapter (Context context, List<?> objectList) {
        this.context = context;
        this.objectList = objectList;

        activeColor = ResourcesCompat.getColor(context.getResources(), R.color.primary, null);
    }
    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public Object getItem(int i) {
        return objectList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return objectList.indexOf(objectList.get(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);


        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,12, 0,12);

        TextView text = new TextView(context);
        text.setText(String.valueOf(getItem(i)));
        text.setTextSize(17f);
        text.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat));
        text.setPadding(25, 25, 18, 25);
        text.setTextColor(Color.WHITE);
        text.setBackground(new ColorDrawable(Color.TRANSPARENT));
        text.setLayoutParams(layoutParams);


        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_check_circle_outline_24, null);

        DrawableCompat.setTint(drawable, activeColor);
        ImageView image = new ImageView(context);
        image.setImageDrawable(drawable);
        image.setVisibility(View.GONE);
        image.setLayoutParams(new FrameLayout.LayoutParams(65, 65));


        layout.setOnClickListener((v) -> onClickListener(getItem(i), layout, image, text));

        layout.addView(image);
        layout.addView(text);


        for (Object object: selected) {
            if(String.valueOf(object).equals(String.valueOf(getItem(i)))) {
                image.setVisibility(View.VISIBLE);
                text.setTextColor(activeColor);
            }
        }


        return layout;
    }


    public void onClickListener(Object obj, LinearLayout layout, ImageView image, TextView text) {
        int index = selected.indexOf(obj);

        if(index < 0) {
            selected.add(obj);
            image.setVisibility(View.VISIBLE);
            text.setTextColor(activeColor);
        } else {
            selected.remove(obj);
            image.setVisibility(View.GONE);
            text.setTextColor(Color.WHITE);
        }
    }
}
