package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.massimo.ronzulli.turnironzulli.R;

public class DialogYearSpinner extends Dialog {
    public Activity activity;
    public Context context;
    private static final int MAX_YEAR = 2040;
    private static final int MIN_YEAR = 1995;
    public int year=0;
    private NumberPicker picker_year;
    private Button btn_ok;

    public DialogYearSpinner(Activity activit) {
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_year_spinner);
        picker_year=findViewById(R.id.picker_year);
        btn_ok=findViewById(R.id.btn_ok);
        picker_year.setMaxValue(MAX_YEAR);
        picker_year.setMinValue(MIN_YEAR);
        picker_year.setWrapSelectorWheel(true);
        Calendar calendar= Calendar.getInstance();
        int now =  calendar.get(Calendar.YEAR);
        picker_year.setValue(now);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=picker_year.getValue();
                dismiss();
            }
        });

    }
}
