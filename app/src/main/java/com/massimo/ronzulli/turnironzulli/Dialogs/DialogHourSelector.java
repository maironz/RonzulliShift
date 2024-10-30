package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.Right;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.makeStringFromTimepicker;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

public class DialogHourSelector  extends Dialog {
    SavesEnum savesEnum=new SavesEnum();
    public Activity activity;
    public Context context;
    private Button bt_dhs_start,bt_dhs_end,bt_dhs_save;
    private String hourStart,hourEnd;
    public DialogHourSelector(Activity activit) {
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hour_selector);
        removeSinglePref(context ,"{temp hour start}", savesEnum.appSettings.APP_SETTINGS);
        removeSinglePref(context ,"{temp hour end}", savesEnum.appSettings.APP_SETTINGS);
        bt_dhs_start=findViewById(R.id.bt_dhs_start);
        bt_dhs_end=findViewById(R.id.bt_dhs_end);
        bt_dhs_save=findViewById(R.id.bt_dhs_save);
        bt_dhs_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String tempHourStart=makeStringFromTimepicker( hourOfDay, minute);
                        bt_dhs_start.setText(tempHourStart, TextView.BufferType.NORMAL);
                        setHourStart(tempHourStart);
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(context.getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });
        bt_dhs_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourEnd=makeStringFromTimepicker( hourOfDay, minute);
                        bt_dhs_end.setText(hourEnd, TextView.BufferType.NORMAL);
                        setHourEnd(hourEnd);
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(context.getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });
        bt_dhs_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_dhs_end.getText().toString()==context.getString(R.string.dhs_start_title) || bt_dhs_start.getText().toString()==context.getString(R.string.dhs_start_title)){
                    Toast.makeText(context, context.getString(R.string.set_hour),Toast.LENGTH_SHORT).show();
                } else {
                    putSinglePref(context ,"{temp hour start}", hourStart,savesEnum.appSettings.APP_SETTINGS);
                    putSinglePref(context ,"{temp hour end}", hourEnd,savesEnum.appSettings.APP_SETTINGS);
                    dismiss();
                }
            }
        });
    }
    private void setHourStart(String hourStart){
        this.hourStart=hourStart;
    }
    private void setHourEnd(String hourEnd){
        this.hourEnd=hourEnd;
    }
}
