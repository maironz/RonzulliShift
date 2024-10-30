package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static java.lang.Math.floor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.models.Leaves;
import com.massimo.ronzulli.turnironzulli.models.Styles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


/*
*  attivare le statistiche per tutti i turni al prossimo aggiornamento dell'app
*  (alla prima installazione devono essere tutte attive, nell'ultimo aggiornamento vanno attivate in automatico)
*  eliminare getnow ed aprire le statistiche direttamente al mese selezionato
* */
public class StatsActivity extends AppCompatActivity {
    private Context context;
    private View view;
    private final String TAG=getClass().getName();
    private Leaves leaves;
    private SavesEnum savesEnum;
    private Styles styles;
    private int yearG,monthG,dayG, weekG,dayOfWeekG;

    private RelativeLayout.LayoutParams lp,lpTvRight,lpTvBottom;
    private float factor;
    private final ArrayList<String> arrayListStyles=new ArrayList<>();
    private Map<String, Integer> countStyles,countStylesDays;//map styles, stylesCount(minutes)/stylesCount(days)
    private final ArrayList<String> arrayListOverW=new ArrayList<>();
    private int countOverW;
    private final ArrayList<String> arrayListLeaves=new ArrayList<>();
    private Map<String, Integer> countLeaves;//map leaves, leavesCount
    private final ArrayList<String> arrayListLeavesTime=new ArrayList<>();
    private LinearLayout stats_ll_outer;
    private ArrayList<RelativeLayout> stats_ll;
    private ArrayList<TextView> tvLeft,tvRight,tvBottom;

    private int totalRow=0;

    private int mLastX,mLastY;
    private boolean mSwipingLeft,mSwipingRight,mSwipingTop,mSwipingBottom,swipeStart;

    private Button buttonLeft,buttonCenterLeft,buttonCenter,buttonRight;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_stats);
        view=findViewById(R.id.StatsActivity);
        leaves = new Leaves(context); //leaves.leaveList (0 to5)
        savesEnum = new SavesEnum();
        styles= new Styles(context); //styles.styleCellsArrayList (0 to totalStyles)

        buttonLeft=findViewById(R.id.buttonLeft);
        buttonCenterLeft=findViewById(R.id.buttonCenterLeft);
        buttonCenter=findViewById(R.id.buttonCenter);
        buttonRight=findViewById(R.id.buttonRight);

        stats_ll_outer=findViewById(R.id.stats_ll_outer);//linear layout to add elements
        buttonLeft.setOnClickListener(v -> previousMonth());
        buttonRight.setOnClickListener(v -> nextMonth());
        buttonCenter.setOnClickListener(v -> {
            initDatePickerDate(view);
        });
        buttonCenter.setText(getTodaysDate());
        buttonCenterLeft.setOnClickListener(v -> {
            int yearOld=yearG, monthOld=monthG;
            boolean direction=true,changed=true;
            getTodaysDate();
            buttonCenter.setText(makeDateString(dayG, monthG,yearG));
            if (yearOld>yearG) direction=false;
            if (yearOld==yearG){
                if (monthOld>monthG) direction=false;
                if (monthOld==monthG){
                    changed=false;
                }
            }
            if (changed) updateStatistics(direction);
        });

        factor = this.getResources().getDisplayMetrics().density;
        lp = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpTvRight = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpTvRight.alignWithParent=true;
        lpTvBottom = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, (int)(factor));


        getNow();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getArrayListsData(monthG,yearG);
                //show the data on the screen
                try {
                    makeGraphicsStyles(true);
                } catch (Exception ignored) {}
                try {
                    makeGraphicsLeaves(true);
                } catch (Exception ignored) {}
                try {
                    makeGraphicsOverWork(true);
                } catch (Exception ignored) {}
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToPreviousActivity();
        return super.onSupportNavigateUp();
    }
    //region menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }
    public void onClickInfoStats(MenuItem item) {
        String title   = getString(R.string.stats);
        String message = context.getString(R.string.stats_text);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }
    public void onClickEditShift(MenuItem item) {
        putSinglePref(context, "fromStats", "true", savesEnum.appSettings.GENERAL_SETTINGS);
        Intent intent = new Intent(context, StylesActivity.class);
        //intent.putExtra("index of element", var);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
        finish();
    }
    //endregion
    private void updateStatistics(boolean directionLeft){
        Animation anim;
        if (directionLeft) {
            anim = AnimationUtils.loadAnimation(this, R.anim.swipe_left_out);
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.swipe_right_out);
        }
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                stats_ll_outer.removeAllViews();
                for (int i = 0; i < totalRow; i++) {
                    try {
                        stats_ll.get(i).removeAllViews();
                    } catch (Exception ignored){}
                }
                stats_ll.clear();
                tvLeft.clear();
                tvRight.clear();
                tvBottom.clear();

                getArrayListsData( monthG, yearG);
                try {
                    makeGraphicsStyles(false);
                } catch (Exception ignored) {}
                try {
                    makeGraphicsLeaves(false);
                } catch (Exception ignored) {}
                try {
                    makeGraphicsOverWork(false);
                } catch (Exception ignored) {}

                Animation anim;
                if (directionLeft) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.swipe_right_in);
                } else {
                    anim = AnimationUtils.loadAnimation(context, R.anim.swipe_left_in);
                }
                stats_ll_outer.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        stats_ll_outer.startAnimation(anim);
    }
    //fill ArrayLists arrayListStyles,arrayListOverS,arrayListLeaves,arrayListLeavesTime, countStyles(days),countLeaves(minutes),countOverS(minutes)
    private void getArrayListsData(int month, int year){
        String  param="",day,style="{style}",overS="{straord}",leaveType="{perm tipo}",leaveTime="{perm}";
        String settingFileStyle= String.valueOf(year).concat(" single cell");
        stats_ll=new ArrayList<>();
        tvLeft=new ArrayList<>();
        tvRight=new ArrayList<>();
        tvBottom=new ArrayList<>();
        countStyles = new HashMap<>();//map styles, stylesCount
        countStylesDays= new HashMap<>();//map styles, stylesCount
        countLeaves = new HashMap<>();//map leaves, leavesCount
        countOverW=0;

        for (int i=1; i<=31 ; i++){
            day="{".concat(String.valueOf(year))
                    .concat("-")
                    .concat(String.valueOf(month))
                    .concat("-")
                    .concat( String.valueOf(i))
                    .concat("}"
                    );
            param=getSinglePref(context,day.concat(style),settingFileStyle);
            if(param.length()>0){
                arrayListStyles.add(param);
                //region map styles->stylesCount
                int oldCount=0;
                int oldCounDays=0;
                if (countStyles.get(param) != null) {
                    oldCount=countStyles.get(param);
                    oldCounDays=countStylesDays.get(param);
                    countStyles.put(param,oldCount+minutesCountStyle(param));
                    countStylesDays.put(param,oldCounDays+1);
                } else {
                    countStyles.put(param,minutesCountStyle(param));
                    countStylesDays.put(param,1);
                }
                //endregion
            }
            param=getSinglePref(context,day.concat(overS),settingFileStyle);
            if(param.length()>0){
                arrayListOverW.add(param);
                countOverW+=minutesCount(param);
            }
            param=getSinglePref(context,day.concat(leaveType),settingFileStyle);
            if(param.length()>0){
                arrayListLeaves.add(param);
                String timeLeave=getSinglePref(context,day.concat(leaveTime),settingFileStyle);
                arrayListLeavesTime.add(timeLeave);
                //region map leaves->leavesCount
                int oldCount=0;
                if (countLeaves.get(param) != null) {
                    oldCount=countLeaves.get(param);
                    countLeaves.put(param,oldCount+minutesCount(timeLeave));
                } else {
                    countLeaves.put(param,minutesCount(timeLeave));
                }
                //endregion
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void makeGraphics(int i, boolean animStart){
            stats_ll.add(new RelativeLayout(context));
            stats_ll.get(i).setLayoutParams(lp);
            stats_ll.get(i).setClickable(true);
            stats_ll.get(i).setFocusable(true);
            stats_ll.get(i).setPadding((int)(10*factor),(int)(10*factor),(int)(10*factor),(int)(10*factor));

            tvLeft.add(new TextView(context));
            tvLeft.get(i).setLayoutParams(lp);
            tvLeft.get(i).setTextColor(getResources().getColor(R.color.black, null));
            tvLeft.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvLeft.get(i).setWidth((int)(Camera.widthScreen(StatsActivity.this)/3));

            tvRight.add(new TextView(context));
            tvRight.get(i).setLayoutParams(lpTvRight);
            tvRight.get(i).setTextColor(getResources().getColor(R.color.black, null));
            tvRight.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvRight.get(i).setGravity(Gravity.END);

            tvBottom.add(new TextView(context));
            tvBottom.get(i).setLayoutParams(lpTvBottom);
            tvBottom.get(i).setBackgroundColor(getResources().getColor(R.color.black, null));
            if(animStart) {
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down_top_in);
                anim.setStartOffset(i * 70L);
                stats_ll.get(i).setAnimation(anim);
            }
            stats_ll.get(i).addView(tvLeft.get(i));
            stats_ll.get(i).addView(tvRight.get(i));

        stats_ll.get(i).setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX=  (int) Math.abs(event.getX());
                        mLastY= (int) Math.abs(event.getY());
                        mSwipingLeft = false;
                        mSwipingRight = false;
                        mSwipingTop = false;
                        mSwipingBottom = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        int x = (int) Math.abs(event.getX());
                        int y = (int) Math.abs(event.getY());
                        int deltaX = Math.abs(mLastX - x);
                        int deltaY = Math.abs(mLastY - y);
                        if (deltaX>deltaY){
                            if (mLastX<x){
                                mSwipingLeft=true;
                                //swipeScreenLeft();
                            } else {
                                animDelete(i);
                                mSwipingRight=true;
                                //swipeScreenRight();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }

                return false;
            });

            stats_ll_outer.addView(stats_ll.get(i));
            stats_ll_outer.addView(tvBottom.get(i));
    }
    private void makeGraphicsStyles(boolean animStart){
        int i=-1;
        //totalRow è globale, il rigo sotto impedisce errori se countStyle è vuoto
        totalRow = i;
        for (Map.Entry<String, ?> entry : countStyles.entrySet()) {
            i++;
            totalRow = i;
            makeGraphics(i, animStart);
            //long description style
            tvLeft.get(i).setText(styles.getStyle(Integer.parseInt(entry.getKey())).ld);
            //values
            String daysText = "";
            if (entry.getKey() != null) {
                if (countStylesDays.get(entry.getKey()) != 1) {
                    daysText = "(".concat(String.valueOf(countStylesDays.get(entry.getKey()))).concat(" ").concat(getResources().getString(R.string.days)).concat(") ");
                } else {
                    daysText = "(".concat(String.valueOf(countStylesDays.get(entry.getKey()))).concat(" ").concat(getResources().getString(R.string.day)).concat(") ");
                }

                String minText = String.valueOf(minutesHoursCountToString((int) entry.getValue()));
                tvRight.get(i).setText(String.format("%s%s", daysText, minText));
            }
        }
    }
    private void makeGraphicsLeaves(boolean animStart){
        int i=totalRow;
        for (Map.Entry<String, ?> entry : countLeaves.entrySet()) {
            i++;
            totalRow = i;
            makeGraphics(i, animStart);
            //description leave
            tvLeft.get(i).setText(leaves.leaveList.get(Integer.parseInt(entry.getKey())).leaveDescription);
            String minText = String.valueOf(minutesHoursCountToString((int) entry.getValue()));
            tvRight.get(i).setText(minText);
        }
    }
    private void makeGraphicsOverWork(boolean animStart){
        if (countOverW>0) {
            int i = totalRow;
            if (i>=0){
                i++;
                totalRow = i;
                makeGraphics(i, animStart);
                //description overwork
                tvLeft.get(i).setText(getResources().getString(R.string.overwork));
                String minText = String.valueOf(minutesHoursCountToString((int) countOverW));
                tvRight.get(i).setText(minText);
            }
        }
    }
    private void animDelete(int i) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.swipe_left_out);
        anim.setStartOffset(i * 70L);
        stats_ll.get(i).startAnimation(anim);
    }

    //preimposta il calendar picker con la data preimpostata in yearG,monthG,dayG
    private void initDatePickerDate(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //month +=1;
                boolean direction=true,changed=true;

                if (year<yearG) direction=false;
                if (year==yearG){
                    if (month<monthG) direction=false;
                    if (month==monthG){
                        changed=false;
                    }
                }
                monthG=month;
                yearG=year;
                dayG=dayOfMonth;
                buttonCenter.setText(makeDateString(dayOfMonth, month,year));
                if (changed) updateStatistics(direction);
            }
        };
        Calendar calendar=Calendar.getInstance();
        calendar.set(yearG,monthG,dayG);
        DatePickerDialog datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_TurniRonzulli_NoActionBar, dateSetListener,  yearG,  monthG, dayG);
        //todo: verificare funzionamento dopo sostituzione Theme_AppCompat_NoActionBar
        //DatePickerDialog datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_AppCompat_NoActionBar, dateSetListener,  yearG,  monthG, dayG);
        datePickerDialog.show();
    }
    //converte il numero del mese in testo
    private String getMonthFormat(int month) {
        String temp="";
        switch (month){
            case 1:
                temp = "Gen";
                break;
            case 2:
                temp ="Feb";
                break;
            case 3:
                temp ="Mar";
                break;
            case 4:
                temp ="Apr";
                break;
            case 5:
                temp ="Mag";
                break;
            case 6:
                temp ="Giu";
                break;
            case 7:
                temp ="Lug";
                break;
            case 8:
                temp ="Ago";
                break;
            case 9:
                temp ="Set";
                break;
            case 10:
                temp ="Ott";
                break;
            case 11:
                temp ="Nov";
                break;
            case 12:
                temp ="Dic";
                break;
        }
        return temp;
    }
    private void nextMonth(){
        if (monthG==11){
            monthG=0;
            yearG+=1;
        } else {
            monthG+=1;
        }
        buttonCenter.setText(makeDateString(dayG,monthG,yearG));
        updateStatistics(true);
    }
    private void previousMonth(){
        if (monthG==0){
            monthG=11;
            yearG-=1;
        } else {
            monthG-=1;
        }
        buttonCenter.setText(makeDateString(dayG,monthG,yearG));
        updateStatistics(false);
    }
    private String makeDateString(int dayOfMonth, int month, int year) {
        return  getMonthFormat(month+1) .concat( " " ).concat(String.valueOf(year));
    }
    //preleva la data corrente
    private String getTodaysDate() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        yearG=year;
        int month=calendar.get(Calendar.MONTH);
        monthG=month;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        dayG=day;
        weekG=calendar.get(Calendar.WEEK_OF_YEAR);
        dayOfWeekG=calendar.get(Calendar.DAY_OF_WEEK);
        return makeDateString(day, month, year);
    }
    //return integer minutes from style
    private int minutesCountStyle(String style){
        int total=0;
        String time= styles.getStyle(Integer.parseInt(style)).range;
        total=minutesCount(time);
        return total;
    }
    //return integer minutes from delta time
    private int minutesCount(String time){
        int total=0;
        try {
            int hourStart= Integer.parseInt(time.substring(0,2));
            int minStart= Integer.parseInt(time.substring(3,5));
            int hourEnd= Integer.parseInt(time.substring(6,8));
            int minEnd= Integer.parseInt(time.substring(9,11));
            int minsStart=60*hourStart+minStart;
            int minsEnd=60*hourEnd+minEnd;
            String hours,minutes;
            if(hourStart>hourEnd){
                //another day
                total=1440-minsStart+minsEnd;
            } else {
                total=minsEnd-minsStart;
            }
        } catch (Exception e ){
            e.printStackTrace();
        }
        return total;
    }
    //return formatted time from minutes
    private String minutesHoursCountToString(int totalMinutes) {
        String timeRet="00:00";
        String hours,minutes;
        try {
            if (totalMinutes>0){
                hours= String.valueOf((int) floor(totalMinutes/60));
                minutes=String.valueOf(totalMinutes%60);
                if (hours.length()<2){
                    hours= "0".concat(hours);
                }
                if (minutes.length()<2){
                    minutes= "0".concat(minutes);
                }
                timeRet=hours.concat(":").concat(minutes);
            }
        } catch (Exception ignored){}
        return timeRet;
    }
    private void getNow(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        yearG=year;
        int month=calendar.get(Calendar.MONTH);
        monthG=month;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        dayG=day;
        weekG=calendar.get(Calendar.WEEK_OF_YEAR);
        dayOfWeekG=calendar.get(Calendar.DAY_OF_WEEK);
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
                Log.e(TAG, "backToPreviousActivity: " .concat( e.getMessage()));
                Log.e(TAG, "backToPreviousActivity: " .concat(Arrays.toString(e.getStackTrace())));
            } else {
                e.printStackTrace();
            }
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }

}
