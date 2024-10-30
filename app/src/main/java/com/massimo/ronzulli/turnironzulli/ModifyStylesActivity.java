package com.massimo.ronzulli.turnironzulli;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogColorSelector;
import com.massimo.ronzulli.turnironzulli.GR.ColorPickerDialog;
import com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding;

import java.util.Arrays;

import static com.massimo.ronzulli.turnironzulli.GR.Graphic.changeBackgroundTextView;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.Left;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.Right;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.makeStringFromTimepicker;

public class ModifyStylesActivity extends AppCompatActivity {
    SavesEnum savesEnum=new SavesEnum();
    private Context context;
    private final String TAG=getClass().getName();
    private Button bt_msa_shift_start, bt_msa_shift_end,bt_msa_background_color,bt_msa_text_color,bt_msa_border_color;
    private CheckBox cb_msa_not_stat,cb_msa_with_break;
    private Button bt_msa_break_start,bt_msa_break_end;
    private EditText et_msa_short_desc,et_msa_long_desc;
    private TextView tv_long_text1,tv_range1;
    private EditText tv_short_text1;
    private LinearLayout ll_msa_break_start,ll_msa_break_end;
    private ColorPickerDialog colorPickerDialog;
    public int borderColor=Color.BLACK;//Color.rgb(135, 0, 0) red
    public int fillColor=Color.parseColor("#efefef");
    public int shiftHourStart=0,shiftMinStart=0,shiftHourEnd=0,shiftMinEnd=0;
    public int dotColor=Color.BLACK;
    public int textColor=Color.BLACK;
    public boolean notStats=false;
    public boolean breakState=false;
    private int indexof=0;
    private int orderStyle=0;
    private int totalStyles;
    private int lastIdStyles;
    private Activity activity;

    public int breakHourStart=0,breakMinStart=0,breakHourEnd=0,breakMinEnd=0;

    int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_styles);
        context = getApplicationContext();
        indexof=getIntent().getIntExtra("index of element", 0);
        //funziona ugualmente
        //indexof= Integer.parseInt(getSinglePref(getApplicationContext(), savesEnum.appSettings.SELECTED_STYLE, savesEnum.appSettings.APP_SETTINGS));
        totalStyles = Integer.parseInt(getSinglePref(context, savesEnum.appSettings.TOTAL_STYLES, savesEnum.appSettings.APP_SETTINGS));
        lastIdStyles= Integer.parseInt(getSinglePref(context, savesEnum.appSettings.LAST_ID_STYLE, savesEnum.appSettings.APP_SETTINGS));
        String retval=getSinglePref(context,"{order}{".concat(String.valueOf(indexof)).concat("}"), savesEnum.appSettings.STYLE_SETTINGS);
        if (retval.length()>0){
            orderStyle= Integer.parseInt(retval);
        }
        et_msa_short_desc=findViewById(R.id.et_msa_short_desc);
        et_msa_long_desc=findViewById(R.id.et_msa_long_desc);

        tv_short_text1=findViewById(R.id.tv_short_text1);
        tv_long_text1=findViewById(R.id.tv_long_text1);
        tv_range1=findViewById(R.id.tv_range1);

        bt_msa_shift_start=findViewById(R.id.bt_msa_shift_start);
        bt_msa_shift_end=findViewById(R.id.bt_msa_shift_end);
        bt_msa_background_color=findViewById(R.id.bt_msa_background_color);
        bt_msa_text_color=findViewById(R.id.bt_msa_text_color);
        bt_msa_border_color=findViewById(R.id.bt_msa_border_color);
        cb_msa_not_stat=findViewById(R.id.cb_msa_not_stat);
        cb_msa_with_break=findViewById(R.id.cb_msa_with_break);
        bt_msa_break_start=findViewById(R.id.bt_msa_break_start);
        bt_msa_break_end=findViewById(R.id.bt_msa_break_end);
        ll_msa_break_start=findViewById(R.id.ll_msa_break_start);
        ll_msa_break_end=findViewById(R.id.ll_msa_break_end);

        bt_msa_shift_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        bt_msa_shift_start.setText(makeStringFromTimepicker( hourOfDay, minute), TextView.BufferType.NORMAL);
                        saveHourStartShift(hourOfDay, minute);
                        shiftHourStart=hourOfDay;
                        shiftMinStart=minute;
                        String tempText= tv_range1.getText().toString();
                        tempText=Right(tempText,6);
                        tv_range1.setText(makeStringFromTimepicker(hourOfDay, minute) .concat( tempText));
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });
        bt_msa_shift_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        bt_msa_shift_end.setText(makeStringFromTimepicker( hourOfDay, minute), TextView.BufferType.NORMAL);
                        String tempText= tv_range1.getText().toString();
                        tempText=Left(tempText,6);
                        shiftHourEnd=hourOfDay;
                        shiftMinEnd=minute;
                        tv_range1.setText(tempText.concat(makeStringFromTimepicker(hourOfDay, minute)));

                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });
        bt_msa_background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(1,fillColor);
            }
        });

        et_msa_short_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeSd();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_msa_long_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_long_text1.setText(et_msa_long_desc.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        bt_msa_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                colorPickerDialog=new ColorPickerDialog(v.getContext(), new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(String key, int color) {
                        textColor=color;
                        tv_short_text1.setTextColor(color);
                    }
                });
                colorPickerDialog.show();*/
                setColor(2,textColor);

            }
        });
        bt_msa_border_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                colorPickerDialog=new ColorPickerDialog(v.getContext(), new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(String key, int color) {
                        borderColor=color;
                        changeBackgroundTextView(tv_short_text1, borderColor,fillColor,dotColor);
                    }
                });
                colorPickerDialog.show();*/
                setColor(3,borderColor);

            }
        });
        cb_msa_not_stat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notStats=isChecked;
            }
        });
        cb_msa_with_break.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                breakState=isChecked;
                if(isChecked){
                    ll_msa_break_start.setVisibility(View.VISIBLE);
                    ll_msa_break_end.setVisibility(View.VISIBLE);
                } else {
                    ll_msa_break_start.setVisibility(View.GONE);
                    ll_msa_break_end.setVisibility(View.GONE);
                }
            }
        });
        bt_msa_break_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker= new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        bt_msa_break_start.setText(makeStringFromTimepicker( hourOfDay, minute), TextView.BufferType.NORMAL);
                        breakHourStart=hourOfDay;
                        breakMinStart=minute;
                    }
                }, 0, 0, true);
                mTimePicker.setTitle(getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });
        bt_msa_break_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        bt_msa_break_end.setText(makeStringFromTimepicker( hourOfDay, minute), TextView.BufferType.NORMAL);
                        breakHourEnd=hourOfDay;
                        breakMinEnd=minute;
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.msa_time_start_title));
                mTimePicker.show();
            }
        });

        ll_msa_break_start.setVisibility(View.GONE);
        ll_msa_break_end.setVisibility(View.GONE);
        //se il click deriva da un elemento carica l'elemento
        if(indexof>0){ getStyle();}
        tv_short_text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, savesEnum.appSettings.DEFAULT_CHAR_SIZE);
        //region make toolbar back button
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        Toolbar toolbar = binding.appBarMain.toolbarNew;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //endregion

    }
    @Override
    public boolean onSupportNavigateUp() {
        backToPreviousActivity();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mofify_styles, menu);
        return true;
    }
    private void saveHourStartShift(int hour, int min){
        shiftHourStart=hour;
        shiftMinStart=min;
    }
    private void deleteStyle() {
        boolean statusOk=false;
        Toast.makeText(this, getString(R.string.msa_toast_wait_deleting),Toast.LENGTH_SHORT).show();
        if (indexof!=0){//se l'elemento non è stato appena creato oppure non è stato salvato
            try {
                //tre possibilità
                if(orderStyle==totalStyles && totalStyles>1){//ultimo elemento
                    //elimino l'id e scalo l'ordinamento e il numero totale
                    if(deleteStyleById(indexof)){
                        putSinglePref(getApplicationContext(),savesEnum.appSettings.LAST_ID_STYLE, String.valueOf(lastIdStyles-1),savesEnum.appSettings.APP_SETTINGS);
                        putSinglePref(getApplicationContext(),savesEnum.appSettings.TOTAL_STYLES, String.valueOf(totalStyles-1),savesEnum.appSettings.APP_SETTINGS);
                        statusOk=true;
                    } else {
                        statusOk=false;
                    }
                } else if(orderStyle<totalStyles){//elemnto intermedio
                    //elimino l'intermedio scalo gli altri e riduco di uno il totale
                    if(deleteStyleById(indexof)){
                        for (int i = 2; i <= lastIdStyles; i++) {//riordino gli elementi
                            String retval=getSinglePref(getApplicationContext(),"{order}{".concat(String.valueOf(i)).concat("}"), savesEnum.appSettings.STYLE_SETTINGS);
                            if (retval.length()>0){
                                int orderTemp=Integer.parseInt(retval);
                                if(orderTemp>orderStyle){
                                    orderTemp-=1;
                                    putSinglePref(getApplicationContext(),"{order}{".concat(String.valueOf(i)).concat("}"), String.valueOf(orderTemp), savesEnum.appSettings.STYLE_SETTINGS);
                                }
                            }
                        }
                        putSinglePref(getApplicationContext(),savesEnum.appSettings.TOTAL_STYLES, String.valueOf(totalStyles-1),savesEnum.appSettings.APP_SETTINGS);
                        statusOk=true;
                    } else {
                        statusOk=false;
                    }
                } else if (totalStyles==1){//primo elemento
                    //vietato cancellare
                    Toast.makeText(this, getString(R.string.msa_toast_deleting_na),Toast.LENGTH_LONG).show();
                    statusOk=true;
                }
            } catch (Exception e){
                statusOk=false;
            }
            if (statusOk){
                Toast.makeText(this, getString(R.string.msa_toast_deleting_ok),Toast.LENGTH_SHORT).show();
                backToPreviousActivity();
            } else {
                Toast.makeText(this, getString(R.string.msa_toast_deleting_nok),Toast.LENGTH_LONG).show();
            }
        } else {
            //cancellazione non ammessa (primo elemento)
        }
    }
    private boolean deleteStyleById(int id){
        boolean retval=false;
        try {
            removeSinglePref(this,"{order}{".concat(String.valueOf(id)).concat("}"), savesEnum.appSettings.STYLE_SETTINGS);
            retval=true;
        } catch (Exception e){
            retval=false;
        }
        return retval;
    }
    private void getStyle(){
        int id=indexof;
        orderStyle=Integer.parseInt(getSinglePref(getApplicationContext(),"{order}{".concat(String.valueOf(id)).concat("}"),"styles"));
        String strval;
        strval=getSinglePref(getApplicationContext(),"{sd}{".concat(String.valueOf(id)).concat("}"),"styles");
        tv_short_text1.setText("--\n".concat(strval));
        et_msa_short_desc.setText(strval);
        strval=getSinglePref(getApplicationContext(),"{ld}{".concat(String.valueOf(id)).concat("}"),"styles");
        tv_long_text1.setText(strval);
        et_msa_long_desc.setText(strval);
        int hs,ms,he,me;
        shiftHourStart= Integer.parseInt(getSinglePref(getApplicationContext(),"{hs}{".concat(String.valueOf(id)).concat("}"),"styles"));
        shiftMinStart= Integer.parseInt(getSinglePref(getApplicationContext(),"{ms}{".concat(String.valueOf(id)).concat("}"),"styles"));
        shiftHourEnd= Integer.parseInt(getSinglePref(getApplicationContext(),"{he}{".concat(String.valueOf(id)).concat("}"),"styles"));
        shiftMinEnd= Integer.parseInt(getSinglePref(getApplicationContext(),"{me}{".concat(String.valueOf(id)).concat("}"),"styles"));
        strval=makeStringFromTimepicker(shiftHourStart,shiftMinStart);
        bt_msa_shift_start.setText(strval);
        String strval1=makeStringFromTimepicker(shiftHourEnd,shiftMinEnd);
        bt_msa_shift_end.setText(strval1);
        tv_range1.setText(new StringBuilder().append(strval).append("-").append(strval1).toString());
        textColor= Integer.parseInt(getSinglePref(getApplicationContext(),"{tc}{".concat(String.valueOf(id)).concat("}"),"styles"));//text color
        fillColor= Integer.parseInt(getSinglePref(getApplicationContext(),"{bc}{".concat(String.valueOf(id)).concat("}"),"styles"));//background color
        borderColor= Integer.parseInt(getSinglePref(getApplicationContext(),"{bbc}{".concat(String.valueOf(id)).concat("}"),"styles"));//border color
        dotColor= Integer.parseInt(getSinglePref(getApplicationContext(),"{dc}{".concat(String.valueOf(id)).concat("}"),"styles"));//dot color
        notStats= Boolean.parseBoolean(getSinglePref(getApplicationContext(),"{ns}{".concat(String.valueOf(id)).concat("}"),"styles"));//not statistic
        breakState= Boolean.parseBoolean(getSinglePref(getApplicationContext(),"{wb}{".concat(String.valueOf(id)).concat("}"),"styles"));//with break
        cb_msa_not_stat.setChecked(notStats);
        cb_msa_with_break.setChecked(breakState);
        tv_short_text1.setTextColor(textColor);
        changeBackgroundTextView(tv_short_text1, borderColor,fillColor,dotColor,0);
        breakHourStart= Integer.parseInt(getSinglePref(getApplicationContext(),"{bhs}{".concat(String.valueOf(id)).concat("}"),"styles"));
        breakMinStart= Integer.parseInt(getSinglePref(getApplicationContext(),"{bms}{".concat(String.valueOf(id)).concat("}"),"styles"));
        breakHourEnd= Integer.parseInt(getSinglePref(getApplicationContext(),"{bhe}{".concat(String.valueOf(id)).concat("}"),"styles"));
        breakMinEnd= Integer.parseInt(getSinglePref(getApplicationContext(),"{bme}{".concat(String.valueOf(id)).concat("}"),"styles"));
        bt_msa_break_start.setText(makeStringFromTimepicker(breakHourStart,breakMinStart));
        bt_msa_break_end.setText(makeStringFromTimepicker(breakHourEnd,breakMinEnd));
    }
    private void saveStyle(){
        String strval;
        Toast.makeText(this, getString(R.string.msa_toast_saving_wait),Toast.LENGTH_SHORT).show();
        try {
            if (indexof == 0){//se stiamo aggiungendo direttamente un elemento
                //aggiungo 1 al totale degli elementi e all'ultimo id
                indexof= Integer.parseInt(getSinglePref(getApplicationContext(), savesEnum.appSettings.LAST_ID_STYLE, savesEnum.appSettings.APP_SETTINGS))+1;
                putSinglePref(getApplicationContext(),savesEnum.appSettings.LAST_ID_STYLE, String.valueOf(indexof),savesEnum.appSettings.APP_SETTINGS);
                totalStyles +=1;
                putSinglePref(getApplicationContext(),savesEnum.appSettings.TOTAL_STYLES, String.valueOf(totalStyles),savesEnum.appSettings.APP_SETTINGS);
                putSinglePref(getApplicationContext(),"{order}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(totalStyles),savesEnum.appSettings.STYLE_SETTINGS);
            }
            strval=tv_short_text1.getText().toString().substring(3);
            putSinglePref(getApplicationContext(),"{id}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(indexof),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{sd}{".concat(String.valueOf(indexof)).concat("}"),strval,savesEnum.appSettings.STYLE_SETTINGS);
            strval=tv_long_text1.getText().toString();
            putSinglePref(getApplicationContext(),"{ld}{".concat(String.valueOf(indexof)).concat("}"),strval,savesEnum.appSettings.STYLE_SETTINGS);

            putSinglePref(getApplicationContext(),"{hs}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(shiftHourStart),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{ms}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(shiftMinStart),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{he}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(shiftHourEnd),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{me}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(shiftMinEnd),savesEnum.appSettings.STYLE_SETTINGS);

            putSinglePref(getApplicationContext(),"{tc}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(textColor),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{bc}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(fillColor),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{bbc}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(borderColor),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{dc}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(dotColor),savesEnum.appSettings.STYLE_SETTINGS);

            putSinglePref(getApplicationContext(),"{ns}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(notStats),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{wb}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(breakState),savesEnum.appSettings.STYLE_SETTINGS);

            putSinglePref(getApplicationContext(),"{bhs}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(breakHourStart),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{bms}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(breakMinStart),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{bhe}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(breakHourEnd),savesEnum.appSettings.STYLE_SETTINGS);
            putSinglePref(getApplicationContext(),"{bme}{".concat(String.valueOf(indexof)).concat("}"), String.valueOf(breakMinEnd),savesEnum.appSettings.STYLE_SETTINGS);

            Toast.makeText(this, getString(R.string.msa_toast_saving_ok),Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(this, getString(R.string.msa_toast_saving_nok),Toast.LENGTH_LONG).show();
        }
    }
    private void changeSd(){
        String tempText=et_msa_short_desc.getText().toString();
        tv_short_text1.setText("--\n".concat(tempText));
    }
    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(context, StylesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_left_in, R.anim.swipe_right_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "backToPreviousActivity: " + e.getMessage());
                Log.e(TAG, "backToPreviousActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }
    public void setColor(int type, int color){
        //type :1 =background 2=text 3=border
        DialogColorSelector dialogColorSelector=new DialogColorSelector(this,color);
        dialogColorSelector.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int tempColor;
                String state=getSinglePref(context ,"{temp color state}", savesEnum.appSettings.APP_SETTINGS);
                if (state.equals("inactive")){
                    tempColor=Integer.parseInt( getSinglePref(context ,"{temp color}", savesEnum.appSettings.APP_SETTINGS));
                    getColorOnDismiss(type,tempColor);
                }
            }
        });
        dialogColorSelector.show();
    }
    public void getColorOnDismiss(int type,int color){
        //type :1 =background 2=text 3=border
        switch (type){
            case 1:
                fillColor=color;
                break;
            case 2:
                textColor=color;
                break;
            case 3:
                borderColor=color;
                break;
        }
        changeBackgroundTextView(tv_short_text1, borderColor,fillColor,dotColor,0);
        tv_short_text1.setTextColor(textColor);
    }

    public void onClickSave(MenuItem item) {
        saveStyle();
    }

    public void onClickDelete(MenuItem item) {
        deleteStyle();
    }
}