package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.models.Styles;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class DialogColorSelector extends Dialog {
    SavesEnum savesEnum=new SavesEnum();
    public Styles styles;
    public Activity activity;
    public Context context;
    public Button dc_button_ok;
    public int startColor=0xFF8A80;
    public int defColorA,defColorR,defColorG,defColorB;
    private SeekBar dc_seekBar_red,dc_seekBar_green,dc_seekBar_blue;
    private Button[] dc_button;
    private TextView dc_tv_color;
    public DialogColorSelector(Activity activit,int startColor){
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
        this.startColor=startColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color_selector);

        dc_button_ok=findViewById(R.id.dc_button_ok);
        dc_seekBar_red=findViewById(R.id.dc_seekBar_red);
        dc_seekBar_green=findViewById(R.id.dc_seekBar_green);
        dc_seekBar_blue=findViewById(R.id.dc_seekBar_blue);
        dc_tv_color=findViewById(R.id.dc_tv_color);
        //region ImageButton declaration
        dc_button=new Button[33];
        dc_button[1]=findViewById(R.id.dc_button_1);
        dc_button[2]=findViewById(R.id.dc_button_2);
        dc_button[3]=findViewById(R.id.dc_button_3);
        dc_button[4]=findViewById(R.id.dc_button_4);
        dc_button[5]=findViewById(R.id.dc_button_5);
        dc_button[6]=findViewById(R.id.dc_button_6);
        dc_button[7]=findViewById(R.id.dc_button_7);
        dc_button[8]=findViewById(R.id.dc_button_8);
        dc_button[9]=findViewById(R.id.dc_button_9);
        dc_button[10]=findViewById(R.id.dc_button_10);
        dc_button[11]=findViewById(R.id.dc_button_11);
        dc_button[12]=findViewById(R.id.dc_button_12);
        dc_button[13]=findViewById(R.id.dc_button_13);
        dc_button[14]=findViewById(R.id.dc_button_14);
        dc_button[15]=findViewById(R.id.dc_button_15);
        dc_button[16]=findViewById(R.id.dc_button_16);
        dc_button[17]=findViewById(R.id.dc_button1_1);
        dc_button[18]=findViewById(R.id.dc_button1_2);
        dc_button[19]=findViewById(R.id.dc_button1_3);
        dc_button[20]=findViewById(R.id.dc_button1_4);
        dc_button[21]=findViewById(R.id.dc_button1_5);
        dc_button[22]=findViewById(R.id.dc_button1_6);
        dc_button[23]=findViewById(R.id.dc_button1_7);
        dc_button[24]=findViewById(R.id.dc_button1_8);
        dc_button[25]=findViewById(R.id.dc_button1_9);
        dc_button[26]=findViewById(R.id.dc_button1_10);
        dc_button[27]=findViewById(R.id.dc_button1_11);
        dc_button[28]=findViewById(R.id.dc_button1_12);
        dc_button[29]=findViewById(R.id.dc_button1_13);
        dc_button[30]=findViewById(R.id.dc_button1_14);
        dc_button[31]=findViewById(R.id.dc_button1_15);
        dc_button[32]=findViewById(R.id.dc_button1_16);
        //endregion
        for (int i = 1; i <=32 ; i++) {
            final int j=i;
            dc_button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int color=Color.WHITE;
                    //prelevo il bacground
                    Drawable background=dc_button[j].getBackground();
                    //se è di tipo color
                    if (background instanceof ColorDrawable){
                        //prelevo il colore
                        color = ((ColorDrawable) background).getColor();
                    }
                    defColorR=Color.red(color);
                    defColorG=Color.green(color);
                    defColorB=Color.blue(color);
                    dc_seekBar_red.setProgress(defColorR);
                    dc_seekBar_green.setProgress(defColorG);
                    dc_seekBar_blue.setProgress(defColorB);
                }
            });
        }
        defColorR=Color.red(startColor);
        defColorG=Color.green(startColor);
        defColorB=Color.blue(startColor);
        //defColorA=Color.alpha(startColor);
        dc_seekBar_red.setProgress(defColorR);
        dc_seekBar_green.setProgress(defColorG);
        dc_seekBar_blue.setProgress(defColorB);
        dc_tv_color.setBackgroundColor(Color.rgb(defColorR, defColorG, defColorB));
        dc_seekBar_red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defColorR=progress;
                dc_tv_color.setBackgroundColor(Color.rgb(defColorR, defColorG, defColorB));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        dc_seekBar_green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defColorG=progress;
                dc_tv_color.setBackgroundColor(Color.rgb(defColorR, defColorG, defColorB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dc_seekBar_blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defColorB=progress;
                dc_tv_color.setBackgroundColor(Color.rgb(defColorR, defColorG, defColorB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        putSinglePref(context ,"{temp color state}", "active",savesEnum.appSettings.APP_SETTINGS);
        dc_button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempColor=Color.rgb(dc_seekBar_red.getProgress(),dc_seekBar_green.getProgress(),dc_seekBar_blue.getProgress());
                putSinglePref(context ,"{temp color state}", "inactive",savesEnum.appSettings.APP_SETTINGS);
                putSinglePref(context ,"{temp color}", String.valueOf(tempColor),savesEnum.appSettings.APP_SETTINGS);
                startColor=tempColor;
                dismiss();
            }
        });
    }
}
