package com.massimo.ronzulli.turnironzulli.Dialogs;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

public class DialogFontSizeSpinner extends Dialog {
    public Activity activity;
    public Context context;
    private static final int MAX_SIZE = 30;
    private static final int MIN_SIZE = 5;
    public int size=0;
    private NumberPicker picker_size;
    private Button btn_ok;

    public DialogFontSizeSpinner(Activity activit) {
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_font_size_spinner);
        picker_size=findViewById(R.id.picker_size);
        btn_ok=findViewById(R.id.btn_ok);
        picker_size.setMaxValue(MAX_SIZE);
        picker_size.setMinValue(MIN_SIZE);
        picker_size.setWrapSelectorWheel(true);
        SavesEnum savesEnum=new SavesEnum();
        String sizeStr=getSinglePref(context, "Default size font", savesEnum.appSettings.GENERAL_SETTINGS);
        if(sizeStr.length()>0) {
            size = Integer.parseInt(sizeStr);
            picker_size.setValue(size);
        } else {
            picker_size.setValue(12);
        }

        btn_ok.setOnClickListener(v -> {
            size=picker_size.getValue();
            dismiss();
        });

    }
}
