package com.massimo.ronzulli.turnironzulli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOkCancel;
import com.massimo.ronzulli.turnironzulli.models.Styles;

import java.util.ArrayList;
import java.util.Arrays;

import static com.massimo.ronzulli.turnironzulli.GR.Graphic.changeBackgroundTextView;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class MakeShiftActivity extends AppCompatActivity{
    private View view;
    private Context context;
    //general settings
    SavesEnum savesEnum=new SavesEnum();
    private Styles styles;
    private int rotationProfile;
    //sounds vars
    private MediaPlayer mp;
    private Uri notification;
    private Ringtone r;
    private String sound;
    int soundVal;
    private boolean showButtonCenterLeftMessage=true;

    //display dimensions
    private int layoutWidth;
    private int layoutHeight;
    private float factor;


    //right scroll
    private TextView[] tv_short_text;
    private TextView[] tv_long_text;
    private TextView[] tv_range;
    private LinearLayout[] ll_cell_style_new,ll_internal,ll_internal2;
    private LinearLayout styles_ll;
    private LinearLayout.LayoutParams lp,tv;
    private LinearLayoutCompat ms_ll_scroll;

    //left scroll
    private ScrollView ms_sv_left;
    private int totalStyles,totalRotationElements;
    private String TAG=getClass().getName(),strRotationElements;
    private ArrayList<String> listRotationElements;

    private TextView[] tv_short_text_left;
    private TextView[] tv_long_text_left;
    private TextView[] tv_range_left;
    private LinearLayout[] ll_cell_style_new_left,ll_internal_left,ll_internal2_left;
    private LinearLayout styles_ll_left;

    private EditText et_make_shift_description;
    private String titleDescription,sequence;
    private Button sa_buttonDateStart,sa_buttonDateEnd;
    private DatePickerDialog datePickerDialog;
    private int yearStart,monthStart,dayStart;
    private int yearEnd,monthEnd,dayEnd;
    private Calendar calendarStart;
    private Calendar calendarEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_shift);
        context = getApplicationContext();
        view = findViewById(R.id.activity_make_shift);
        //region declarations
        ms_ll_scroll=findViewById(R.id.ms_ll_scroll);
        sa_buttonDateStart=findViewById(R.id.sa_buttonDateStart);
        sa_buttonDateEnd=findViewById(R.id.sa_buttonDateEnd);
        et_make_shift_description=findViewById(R.id.et_make_shift_description);
        rotationProfile=1;
        calendarStart=Calendar.getInstance();
        calendarStart.set(2021,5,10);
        calendarEnd=Calendar.getInstance();
        calendarEnd.set(2021,5,11);
        //endregion
        //region date settings
        String retStartDate=getSinglePref(getApplicationContext(),"{start date}{"
                    .concat(String.valueOf(rotationProfile))
                    .concat("}")
                ,savesEnum.appSettings.ROTATION_SETTINGS);
        String retEndDate=getSinglePref(getApplicationContext(),"{end date}{".concat(String.valueOf(rotationProfile)).concat("}"),savesEnum.appSettings.ROTATION_SETTINGS);
        if (retStartDate.length()!=0){
            calendarStart.set(Integer.parseInt(retStartDate.substring(6, 10)),Integer.parseInt(retStartDate.substring(3, 5))-1,Integer.parseInt(retStartDate.substring(0, 2)));
        } else {
            retStartDate =getResources().getString(R.string.string_start_date);
        }
        if (retEndDate.length()!=0){
            calendarEnd.set(Integer.parseInt(retEndDate.substring(6, 10)),Integer.parseInt(retEndDate.substring(3, 5))-1,Integer.parseInt(retEndDate.substring(0, 2)));
        } else {
            retEndDate =getResources().getString(R.string.string_end_date);
        }
        yearStart=calendarStart.get(Calendar.YEAR);
        monthStart=calendarStart.get(Calendar.MONTH);
        dayStart=calendarStart.get(Calendar.DAY_OF_MONTH);
        yearEnd=calendarEnd.get(Calendar.YEAR);
        monthEnd=calendarEnd.get(Calendar.MONTH);
        dayEnd=calendarEnd.get(Calendar.DAY_OF_MONTH);

        sa_buttonDateStart.setText(retStartDate);
        sa_buttonDateEnd.setText(retEndDate);
        //endregion
        //region sound value setting
        String sound=getSinglePref(context,savesEnum.appSettings.DEFAULT_SOUND,savesEnum.appSettings.APP_SETTINGS);
        if(sound.length()==0){
            soundVal=0;//default sound
            mp = MediaPlayer.create(context, R.raw.click1);
        } else {
            soundVal= Integer.parseInt(sound);
            switch (soundVal){
                case 0:
                    mp = MediaPlayer.create(context, R.raw.click1);
                    break;
                case 1:
                    mp = MediaPlayer.create(context, R.raw.click);
                    break;
                case 2:
                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    break;
            }
        }

        //endregion
        //region display resize
        //conversione da pixel a DP
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        //int densityDpi = metrics.densityDpi;
        //float density = metrics.density;
        factor = this.getResources().getDisplayMetrics().density;
        layoutWidth = (int) widthPixels;//dp
        //densityDpi=160*density
        //440=160*2.75
        //remove the paddings
        layoutWidth-=4*3*factor;
        //make the size correct
        layoutWidth =layoutWidth/2;
        ms_sv_left=findViewById(R.id.ms_sv_left);
        LinearLayout.LayoutParams lpscroll = new LinearLayoutCompat.LayoutParams( layoutWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        lpscroll.setMargins((int)(5*factor),0,(int)(3*factor),(int)(3*factor));
        ms_sv_left.setLayoutParams(lpscroll);
        //endregion
        //region scroll right
        String retval=getSinglePref(context,savesEnum.appSettings.FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY,savesEnum.appSettings.APP_SETTINGS);
        //todo:uncomment the next line to initialise first 3 elements
        //retval="";
        if (retval.length()==0){
            totalStyles=Styles.makeStandardStyle(getApplicationContext(),savesEnum);
            putSinglePref(getApplicationContext(),savesEnum.appSettings.FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY,"false",savesEnum.appSettings.APP_SETTINGS);
        } else {
            totalStyles= Integer.parseInt(getSinglePref(getApplicationContext(),savesEnum.appSettings.TOTAL_STYLES,savesEnum.appSettings.APP_SETTINGS));
        }

        styles= new Styles(context);

        styles_ll=findViewById(R.id.styles_ll);
        lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv = new LinearLayout.LayoutParams( (int)(50*factor),(int)(50*factor));
        tv.setMargins((int)(3*factor),(int)(3*factor),(int)(3*factor),(int)(3*factor));
        tv.gravity= Gravity.CENTER;

        ll_cell_style_new=new LinearLayout[totalStyles+1];
        tv_short_text=new TextView[totalStyles+1];
        ll_internal=new LinearLayout[totalStyles+1];
        ll_internal2=new LinearLayout[totalStyles+1];
        tv_long_text=new TextView[totalStyles+1];
        tv_range=new TextView[totalStyles+1];
        int i=0;
        for(i=0; i<=totalStyles; i++)
        {
            //region creazione layout a codice
            ll_cell_style_new[i]=new LinearLayout(context);
            ll_cell_style_new[i].setOrientation(LinearLayout.VERTICAL);
            ll_cell_style_new[i].setLayoutParams(lp);

            ll_internal[i]=new LinearLayout(context);
            ll_internal[i].setClickable(true);
            ll_internal[i].setFocusable(true);
            ll_internal[i].setPadding((int)(4*factor),(int)(4*factor),(int)(4*factor),(int)(4*factor));
            ll_internal[i].setLayoutParams(lp);

            tv_short_text[i]=new TextView(context);
            tv_short_text[i].setLayoutParams(tv);
            tv_short_text[i].setWidth((int)(50*factor));
            tv_short_text[i].setHeight((int)(50*factor));
            tv_short_text[i].setBackground(getDrawable(R.drawable.calendar_border_square_rounded));
            tv_short_text[i].setTextAppearance(context, android.R.style.TextAppearance_Small);
            tv_short_text[i].setClickable(true);
            tv_short_text[i].setFocusable(true);
            tv_short_text[i].setGravity(Gravity.CENTER);
            tv_short_text[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv_short_text[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tv_short_text[i].setVisibility(View.VISIBLE);

            ll_internal2[i]=new LinearLayout(context);
            ll_internal2[i].setOrientation(LinearLayout.VERTICAL);
            ll_internal2[i].setLayoutParams(lp);

            tv_long_text[i]=new TextView(context);
            tv_long_text[i].setLayoutParams(lp);
            tv_long_text[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv_long_text[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            tv_range[i]=new TextView(context);
            tv_range[i].setLayoutParams(lp);

            ll_internal2[i].addView(tv_long_text[i]);
            ll_internal2[i].addView(tv_range[i]);
            ll_internal[i].addView(tv_short_text[i]);
            ll_internal[i].addView(ll_internal2[i]);
            ll_internal[i].setAddStatesFromChildren(true);
            ll_cell_style_new[i].addView(ll_internal[i]);
            styles_ll.addView(ll_cell_style_new[i]);
            // endregion
            tv_short_text[i].setText("--\n".concat(styles.style[i].sd));
            tv_long_text[i].setText(styles.style[i].ld);
            tv_range[i].setText(styles.style[i].range);
            int borderColor=styles.style[i].bbc;//Color.rgb(135, 0, 0) red
            int fillColor=styles.style[i].bc;
            int dotColor=Color.BLACK;
            int var=styles.style[i].id;
            int var1=styles.style[i].order;
            tv_short_text[i].setTextColor(styles.style[i].tc);
            changeBackgroundTextView(tv_short_text[i], borderColor,fillColor,dotColor,0);
            //trasferisce l'id del rigo all'intent
            ll_internal[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        addRotationElement(var1);
                    } catch (Exception e){

                    }

                }
            });
        }
        //endregion
        //region scroll left
        String retvalLeft=getSinglePref(context,savesEnum.appSettings.FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY,savesEnum.appSettings.APP_SETTINGS);
        strRotationElements=getSinglePref(getApplicationContext(), "{sequence}{".concat(String.valueOf(rotationProfile)).concat("}"),savesEnum.appSettings.ROTATION_SETTINGS);
        if (strRotationElements.startsWith("111")){
            retvalLeft="";
        }
        //todo:uncomment the next line to initialise first sequence
        //retvalLeft="";
        if (retvalLeft.length()==0){
            initializeList();
        }
        listRotationElements = new ArrayList<>(Arrays.asList(strRotationElements.split("-")));
        titleDescription=getSinglePref(getApplicationContext(),"{description}{".concat(String.valueOf(rotationProfile)).concat("}"),savesEnum.appSettings.ROTATION_SETTINGS);
        et_make_shift_description.setText(titleDescription);

        //region creazione layout a codice
        styles_ll_left=findViewById(R.id.styles_ll_left);
        ll_cell_style_new_left=new LinearLayout[listRotationElements.size()];
        tv_short_text_left=new TextView[listRotationElements.size()];
        ll_internal_left=new LinearLayout[listRotationElements.size()];
        ll_internal2_left=new LinearLayout[listRotationElements.size()];
        tv_long_text_left=new TextView[listRotationElements.size()];
        tv_range_left=new TextView[listRotationElements.size()];
        try {
            addRotationElements(0);
        }catch (Exception e){
            initializeList();
            try {
            addRotationElements(0);
            }catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        //endregion
        //endregion
        //region events
        sa_buttonDateStart.setOnClickListener(v -> {
            initDatePickerDate(v, sa_buttonDateStart);
            datePickerDialog.show();

        });
        sa_buttonDateEnd.setOnClickListener(v -> {
            initDatePickerDate(v, sa_buttonDateEnd);
            datePickerDialog.show();
        });
        //region make toolbar back button
        com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding binding = com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding.inflate(getLayoutInflater());
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
    private boolean showDialolgOkCancel(){
        String mess=getResources().getString(R.string.string_hint_rotation);
        String title=getResources().getString(R.string.txt_make_shift_title);
        DialogOkCancel dialogOkCancel=new DialogOkCancel(this,title,mess);
        dialogOkCancel.setOnDismissListener(dialog -> {
            String retVal=getSinglePref(context,"{dialog_ok}","appSettings");
            if (retVal.length()!=0){
                play();
                saveShifts();
            }
        });
        long timeStart= calendarStart.getTimeInMillis();
        long timeEnd= calendarEnd.getTimeInMillis();
        if(sa_buttonDateStart.getText().toString().equals(getResources().getString(R.string.string_start_date)) || sa_buttonDateEnd.getText().toString().equals(getResources().getString(R.string.string_end_date)) || timeStart>timeEnd ){
            Toast.makeText(this, getString(R.string.string_check_dates),Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dialogOkCancel.show();
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_shift, menu);
        return true;
    }
    private void play(){
        switch (soundVal){
            case 0:
            case 1:
                mp.start();
                break;
            case 2:
                r.play();
                break;
        }
    }

    //preimposta il calendar picker con la data in yearG,monthG,dayG
    private void initDatePickerDate(View view,Button button) {
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //month +=1;
                String date=makeDateString(dayOfMonth, month,year);
                button.setText(date);
                if(button==sa_buttonDateStart){
                    yearStart=year;
                    monthStart=month;
                    dayStart=dayOfMonth;
                    calendarStart.set(yearStart,monthStart,dayStart);
                } else {
                    yearEnd=year;
                    monthEnd=month;
                    dayEnd=dayOfMonth;
                    calendarEnd.set(yearEnd,monthEnd,dayEnd);
                }
            }
        };
        Calendar calendar=Calendar.getInstance();
        if(button==sa_buttonDateStart){
            calendar.set(yearStart,monthStart,dayStart);
            //datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_AppCompat_NoActionBar, dateSetListener,  yearStart,  monthStart, dayStart);
            //todo: da verificare
            datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_TurniRonzulli_NoActionBar, dateSetListener,  yearStart,  monthStart, dayStart);
        } else {
            calendar.set(yearEnd,monthEnd,dayEnd);
            //todo: da verificare
            datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_TurniRonzulli_NoActionBar, dateSetListener,  yearEnd,  monthEnd, dayEnd);
        }
    }
    //realizza la stringa da inserire come testo nel pulsante
    private String makeDateString(int dayOfMonth, int month, int year) {
        return makeDate(dayOfMonth)  .concat( "/" ).concat( makeDate((int)(month+1))) .concat( "/" ).concat(String.valueOf(year));
    }
    private String makeDate(int dayOfMonth){
        String varTemp= String.valueOf(dayOfMonth);
        if (varTemp.length()>1){
            return varTemp;
        } else {
            return "0".concat(varTemp);
        }
    }

    //rimuove il singolo elemento sostituisce "D" alla stringa del cancellato
    private void deleteRotationElement(int id){
        try{
            //creo l'array di elementi
            listRotationElements.set(id,"D");
            strRotationElements= listRotationElements.get(0);
            for (int i = 0; i <listRotationElements.size() ; i++) {
                strRotationElements = strRotationElements.concat("-") .concat( listRotationElements.get(i));
            }
            //Log.e(TAG, "deleteRotationElement: strRotationElements="+strRotationElements );
            //Log.e(TAG, "deleteRotationElement: id="+id );

            ll_cell_style_new_left[id].removeView(ll_internal_left[id]);
        } catch (Exception e){
            //if (BuildConfig.DEBUG){
            //Log.e(TAG, "deleteRotationElement: error="+e.getMessage());
            //Log.e(TAG, "deleteRotationElement: strRotationElements="+strRotationElements + " id=" + id);
            //}
        }
    }
    //aggiunge il singolo elemento
    private void addRotationElement(int id) throws Exception{
        strRotationElements = strRotationElements.concat("-").concat(String.valueOf(id));
        listRotationElements.add(String.valueOf(id));
        addRotationElements(listRotationElements.size()-1);
        ms_sv_left.postDelayed(new Runnable() {
            @Override
            public void run() {
                ms_sv_left.fullScroll(View.FOCUS_DOWN);
            }
        }, 300);
    }
    //crea la lista di rotazione
    private void addRotationElements(int start) throws Exception{
        ll_cell_style_new_left=Arrays.copyOf(ll_cell_style_new_left,listRotationElements.size());
        tv_short_text_left=Arrays.copyOf(tv_short_text_left,listRotationElements.size());
        ll_internal_left=Arrays.copyOf(ll_internal_left,listRotationElements.size());
        ll_internal2_left=Arrays.copyOf(ll_internal2_left,listRotationElements.size());
        tv_long_text_left=Arrays.copyOf(tv_long_text_left,listRotationElements.size());
        tv_range_left=Arrays.copyOf(tv_range_left,listRotationElements.size());

        for(int i=start; i< listRotationElements.size(); i++)
        {
            //int idElem= Integer.parseInt(strRotationElements.substring(i-1, i));
            String item =listRotationElements.get(i);
            if(!item.equals("D")){
                int idElem= Integer.parseInt(listRotationElements.get(i));
                //Log.e(TAG, "addRotationElements: strRotationElements="+strRotationElements );
                //region creazione layout a codice
                ll_cell_style_new_left[i]=new LinearLayout(context);
                ll_cell_style_new_left[i].setOrientation(LinearLayout.VERTICAL);
                ll_cell_style_new_left[i].setLayoutParams(lp);

                ll_internal_left[i]=new LinearLayout(context);
                ll_internal_left[i].setClickable(true);
                ll_internal_left[i].setFocusable(true);
                ll_internal_left[i].setPadding((int)(4*factor),(int)(4*factor),(int)(4*factor),(int)(4*factor));
                ll_internal_left[i].setLayoutParams(lp);

                tv_short_text_left[i]=new TextView(context);
                tv_short_text_left[i].setLayoutParams(tv);
                tv_short_text_left[i].setWidth((int)(50*factor));
                tv_short_text_left[i].setHeight((int)(50*factor));
                tv_short_text_left[i].setBackground(getDrawable(R.drawable.calendar_border_square_rounded));
                tv_short_text_left[i].setTextAppearance(context, android.R.style.TextAppearance_Small);
                tv_short_text_left[i].setClickable(true);
                tv_short_text_left[i].setFocusable(true);
                tv_short_text_left[i].setGravity(Gravity.CENTER);
                tv_short_text_left[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tv_short_text_left[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv_short_text_left[i].setVisibility(View.VISIBLE);

                ll_internal2_left[i]=new LinearLayout(context);
                ll_internal2_left[i].setOrientation(LinearLayout.VERTICAL);
                ll_internal2_left[i].setLayoutParams(lp);

                tv_long_text_left[i]=new TextView(context);
                tv_long_text_left[i].setLayoutParams(lp);
                tv_long_text_left[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                tv_long_text_left[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                tv_range_left[i]=new TextView(context);
                tv_range_left[i].setLayoutParams(lp);

                ll_internal2_left[i].addView(tv_long_text_left[i]);
                ll_internal2_left[i].addView(tv_range_left[i]);
                ll_internal_left[i].addView(tv_short_text_left[i]);
                ll_internal_left[i].addView(ll_internal2_left[i]);
                ll_internal_left[i].setAddStatesFromChildren(true);
                ll_cell_style_new_left[i].addView(ll_internal_left[i]);
                styles_ll_left.addView(ll_cell_style_new_left[i]);
                // endregion

                tv_short_text_left[i].setText("--\n".concat(styles.style[idElem].sd));
                tv_long_text_left[i].setText(styles.style[idElem].ld);
                tv_range_left[i].setText(styles.style[idElem].range);
                int borderColor=styles.style[idElem].bbc;//Color.rgb(135, 0, 0) red
                int fillColor=styles.style[idElem].bc;
                int dotColor=Color.BLACK;
                tv_short_text_left[i].setTextColor(styles.style[idElem].tc);
                changeBackgroundTextView(tv_short_text_left[i], borderColor,fillColor,dotColor,0);
                int var=i;
                ll_internal_left[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRotationElement(var);
                    }
                });
            }
        }

    }
    private void initializeList(){
        if (totalStyles>=3){
            putSinglePref(getApplicationContext(),"{sequence}{1}","1-1-1-1-1-4-5-3-3-3-3-3-4-5-2-2-2-2-2-4-5",savesEnum.appSettings.ROTATION_SETTINGS);
            strRotationElements="1-1-1-1-1-4-5-3-3-3-3-3-4-5-2-2-2-2-2-4-5";
        } else if (totalStyles==2){
            putSinglePref(getApplicationContext(),"{sequence}{1}","1-1-1-1-1-4-5-3-3-3-3-3-4-5",savesEnum.appSettings.ROTATION_SETTINGS);
            strRotationElements="1-1-1-1-1-4-5-3-3-3-3-3-4-5";
        } else if (totalStyles==1){
            putSinglePref(getApplicationContext(),"{sequence}{1}","1-1-1-1-1-4-5",savesEnum.appSettings.ROTATION_SETTINGS);
            strRotationElements="1-1-1-1-1-4-5";
        }
        putSinglePref(getApplicationContext(),"{description}{1}", getString(R.string.et_make_shift_description),savesEnum.appSettings.ROTATION_SETTINGS);
        putSinglePref(getApplicationContext(),savesEnum.appSettings.FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY,"false",savesEnum.appSettings.APP_SETTINGS);
        titleDescription=getString(R.string.et_make_shift_description);

    }
    //salva la stringa di sequenza
    private boolean save(boolean saveMess){
        boolean retval=false;
        long timeStart= calendarStart.getTimeInMillis();
        long timeEnd= calendarEnd.getTimeInMillis();
        if(sa_buttonDateStart.getText().toString().equals(getResources().getString(R.string.string_start_date)) || sa_buttonDateEnd.getText().toString().equals(getResources().getString(R.string.string_end_date)) || timeStart>timeEnd ){
            Toast.makeText(this, getString(R.string.string_check_dates),Toast.LENGTH_SHORT).show();
            return retval;
        }
        Toast.makeText(this, getString(R.string.msa_toast_saving_wait),Toast.LENGTH_SHORT).show();
        String strMiddle="";
        sequence="";
        int totalElements=0;
        titleDescription=et_make_shift_description.getText().toString();
        try {
            for (int i = 0; i < listRotationElements.size() ; i++) {
                strMiddle=listRotationElements.get(i);
                if (!strMiddle.equals("D")){
                    if (sequence==""){
                        sequence = sequence.concat(strMiddle);
                    } else {
                        sequence = sequence.concat( "-") .concat(strMiddle);
                    }
                }
            }
            //Log.e(TAG, "save: sequence"+sequence );
            putSinglePref(getApplicationContext(),"{start date}{".concat(String.valueOf(rotationProfile)).concat("}"),sa_buttonDateStart.getText().toString(),savesEnum.appSettings.ROTATION_SETTINGS);
            putSinglePref(getApplicationContext(),"{end date}{".concat(String.valueOf(rotationProfile)).concat("}"),sa_buttonDateEnd.getText().toString(),savesEnum.appSettings.ROTATION_SETTINGS);
            putSinglePref(getApplicationContext(),"{sequence}{".concat(String.valueOf(rotationProfile)).concat("}"),sequence,savesEnum.appSettings.ROTATION_SETTINGS);
            putSinglePref(getApplicationContext(),"{description}{".concat(String.valueOf(rotationProfile)).concat("}"),titleDescription,savesEnum.appSettings.ROTATION_SETTINGS);
            if(saveMess){
                Toast.makeText(this, getString(R.string.msa_toast_saving_ok),Toast.LENGTH_LONG).show();
            }
            retval=true;
        } catch (Exception e){
            Toast.makeText(this, getString(R.string.msa_toast_saving_nok),Toast.LENGTH_LONG).show();
            retval=false;
        }
        return retval;
    }
    //aggiorna tutte le celle interessate
    private void saveShifts(){
        boolean retval=save(false);
        if(!retval) { return; }
        String[] splitSequence=sequence.split("-");
        int elem=splitSequence.length;//numero elementi della sequenza
        Calendar calendarCycle=Calendar.getInstance();
        int startDay=calendarStart.get(Calendar.DAY_OF_MONTH);
        int startMonth=calendarStart.get(Calendar.MONTH);
        int startYear=calendarStart.get(Calendar.YEAR);
        calendarCycle.set(startYear,startMonth,startDay);
        int midDay=startDay;
        int midMonth=startMonth;
        int midYear=startYear;
        int endDay=calendarEnd.get(Calendar.DAY_OF_MONTH);
        int endMonth=calendarEnd.get(Calendar.MONTH);
        int endYear=calendarEnd.get(Calendar.YEAR);
        //scrive nella configurazione cella il numero dello stile prelevando dalla stringa strRotationElements il primo carattere (da 0 a 1)
        putSinglePref(getApplicationContext(),"{".concat(String.valueOf(startYear)).concat("-").concat(String.valueOf(startMonth)).concat("-").concat(String.valueOf(startDay)).concat("}{style}"),splitSequence[0],String.valueOf(startYear) .concat( " single cell"));
        int midRotationElement=0;//elemento di rotazione dei caratteri di strRotationElements
        String midstyle;//carattere indicante lo stile da applicare
        long timeMid= calendarStart.getTimeInMillis();
        long timeEnd= calendarEnd.getTimeInMillis();
        while(timeMid<timeEnd){
            calendarCycle.add( Calendar.DAY_OF_MONTH,1);//aggiungo un giorno
            timeMid = calendarCycle.getTimeInMillis();//aggiorno il timer di controllo
            //imposto il carattere di rotazione
            if (midRotationElement<elem-1){
                midRotationElement+=1;
            } else {
                midRotationElement=0;
            }
            midstyle=splitSequence[midRotationElement];
            midDay=calendarCycle.get(Calendar.DAY_OF_MONTH);
            midMonth=calendarCycle.get(Calendar.MONTH);
            midYear=calendarCycle.get(Calendar.YEAR);
            putSinglePref(getApplicationContext(),"{".concat(String.valueOf(midYear)).concat("-").concat(String.valueOf(midMonth)).concat("-").concat(String.valueOf(midDay)).concat("}{style}"),midstyle,String.valueOf(midYear) .concat( " single cell"));
            //Log.e(TAG, "saveShifts: "+"{"+midYear+"-"+midMonth+"-"+midDay+"}{style}=" +midstyle);
        }
    }

    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_left_in, R.anim.swipe_right_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "backToPreviousActivity: " + e.getMessage());
                    Log.e(TAG, "backToPreviousActivity: " + Arrays.toString(e.getStackTrace()));
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }

    public void onClickInfo(MenuItem item) {
        String title   = getString(R.string.txt_make_shift_title);
        String message = context.getString(R.string.make_shift_string);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }

    public void onClickMake(MenuItem item) {
        showDialolgOkCancel();
    }

    public void onClickSave(MenuItem item) {
        play();
        save(true);
    }

    public void onClickAddStyle(MenuItem item) {
    }
}