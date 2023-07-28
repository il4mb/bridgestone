package com.il4mb.bridgestone.tools.MultiSelect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.il4mb.bridgestone.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MultipleSelect extends DialogFragment {

    public static interface Listener {
        void onConfirm(List<Object> selectedList);
    }
    public static class Builder {
        public String title = "Multiple Select Modal";
        public List listData;
        public Context context;
        public Listener listener;
        public Adapter adapter;

        public Builder() {
            listData = new ArrayList();
            listData.add("Data 1");
            listData.add("Data 2");
            listData.add("Data 3");
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setListData(List listData) {
            this.listData = listData;

            if(this.context != null) {
                adapter = new Adapter(this.context, this.listData);
            }
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            if(this.listData != null) {
                adapter = new Adapter(this.context, this.listData);
            }
            return this;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }
    }

    public static String DIALOG_TAG = "multiple_select";
    Builder builder;
    ListView listView;
    RelativeLayout rootView;
    TextView titleView;
    LinearLayout footerView;

    public MultipleSelect(Builder builder) {
        this.builder = builder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if(builder == null) {
            builder = new Builder();
        }

        rootView   = new RelativeLayout(getContext());
        listView   = new ListView(getContext());
        titleView  = new TextView(getContext());
        footerView = new LinearLayout(getContext());


        titleView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat), Typeface.BOLD);
        titleView.setText(builder.title);
        titleView.setTextSize(18f);
        titleView.setPadding(12, 22, 12, 35);


        listView.setMinimumHeight(200);


        TextView cancel  = new TextView(getContext());
        TextView confirm = new TextView(getContext());

        cancel.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));
        cancel.setText("Cancel");
        cancel.setPadding(22, 22, 35, 22);
        cancel.setTextSize(17);
        cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.animate()
                            .scaleXBy(0.2f)
                            .scaleYBy(0.2f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    view.setScaleX(1);
                                    view.setScaleY(1);
                                }
                            }).start();
                }
                return false;
            }
        });
        cancel.setOnClickListener(this::cancelHandler);

        confirm.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));
        confirm.setText("Confirm");
        confirm.setPadding(35, 22, 22, 22);
        confirm.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));
        confirm.setTextSize(17);
        confirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.animate()
                            .scaleXBy(0.2f)
                            .scaleYBy(0.2f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    view.setScaleX(1);
                                    view.setScaleY(1);
                                }
                            }).start();
                }
                return false;
            }
        });
        confirm.setOnClickListener(this::confirmHandler);

        footerView.setGravity(Gravity.END);
        footerView.setPadding(0, 35, 0, 15);
        footerView.addView(cancel);
        footerView.addView(confirm);


        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 25, 25, 25);


        layout.addView(titleView);
        layout.addView(listView);
        layout.addView(footerView);


        rootView.addView(layout);

        return rootView;
    }

    Dialog dialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = getDialog();

        if(dialog != null) {

            dialog.getWindow().setDimAmount(0.5f);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(builder.adapter != null) {
                listView.setAdapter(builder.adapter);
            }
        }
    }

    public void start(FragmentManager fmm) {

        if(! fmm.isDestroyed()) {

           DialogFragment fragment = (DialogFragment) fmm.findFragmentByTag(DIALOG_TAG);

           if(fragment != null) {
               fmm.beginTransaction().remove(fragment).commit();
           }

           this.show(fmm, DIALOG_TAG);
        }
    }

    public void cancelHandler(View view) {

        this.dismiss();
    }

    public void confirmHandler(View view) {

        if(builder.listener != null && builder.adapter != null) {

            builder.listener.onConfirm(builder.adapter.selected);
        }
        this.dismiss();
    }
}
