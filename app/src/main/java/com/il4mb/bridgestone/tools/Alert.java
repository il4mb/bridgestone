package com.il4mb.bridgestone.tools;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.il4mb.bridgestone.R;
import com.il4mb.co2.util.Theme;

import java.util.List;

public class Alert extends DialogFragment {

    private static String TITLE = "Alert Title";
    private static String MESSAGE = "Alert Message";
    private static com.il4mb.co2.util.Theme Theme;


    public static Alert newInstance(com.il4mb.co2.util.Theme theme) {

        Alert.Theme = theme;
        return new Alert();
    }

    public static Alert newInstance() {

        Alert.Theme = com.il4mb.co2.util.Theme.PRIMARY();
        return new Alert();
    }


    public void setTitle(String title) {
        Alert.TITLE = title;
    }

    public void setText(String text) {
        Alert.MESSAGE = text;
    }

    public void setTheme(com.il4mb.co2.util.Theme theme) {
        Alert.Theme = theme;
    }

    ImageButton closeToggle;
    TextView titleView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(new ColorDrawable(Color.TRANSPARENT));
        root.setGravity(Gravity.BOTTOM);
        root.setPadding(25, 25, 25, 75);


        LinearLayout head = new LinearLayout(getContext());
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setWeightSum(1);
        head.setBackground(new ColorDrawable(Color.TRANSPARENT));


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        titleView = new TextView(getContext());
        titleView.setText(TITLE);
        titleView.setTextColor(Theme.Title);
        titleView.setTextSize(18f);
        titleView.setLayoutParams(layoutParams);

        titleView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat), Typeface.BOLD);

        closeToggle = new ImageButton(getContext());
        closeToggle.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_close_24, null));
        closeToggle.setColorFilter(Theme.Title);
        closeToggle.setBackground(new ColorDrawable(Color.TRANSPARENT));



        TextView bodyText = new TextView(getContext());
        bodyText.setTextSize(15f);
        bodyText.setText(MESSAGE);
        bodyText.setTextColor(Theme.Text);
        bodyText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));

        head.addView(titleView);
        head.addView(closeToggle);
        root.addView(head);
        root.addView(bodyText);

        return root;
    }


    Dialog dialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeToggle.setOnClickListener((v) -> {
            this.dismiss();
        });

        dialog = getDialog();


        if(dialog != null) {

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                Color x     = Color.valueOf(Theme.Text);
                float r     = (x.red()   * 0.5f);
                float g     = (x.green() * 0.5f);
                float b     = (x.blue()  * 0.5f);

                Color c = Color.valueOf(r, g, b);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(c.toArgb()));
            }


            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            layoutParams.gravity = Gravity.BOTTOM;

            dialog.getWindow().setAttributes(layoutParams);

        }
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {

        if(manager.findFragmentByTag(tag) != null) {
            // manager.beginTransaction().remove(manager.findFragmentByTag(tag)).commit();
            return;
        }
        super.show(manager, tag);
    }
}
