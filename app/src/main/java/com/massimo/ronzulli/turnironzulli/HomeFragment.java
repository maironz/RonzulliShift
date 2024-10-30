package com.massimo.ronzulli.turnironzulli;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogHourSelector;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogLeaveSelector;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOkCancel;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogReminder;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogStyleSelector;
import com.massimo.ronzulli.turnironzulli.databinding.DialogReminderBinding;
import com.massimo.ronzulli.turnironzulli.models.Leaves;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.models.Styles;

import java.util.Arrays;
import java.util.regex.Pattern;

import static android.text.TextUtils.split;
import static android.text.TextUtils.substring;
import static com.massimo.ronzulli.turnironzulli.GR.Graphic.changeBackgroundTextView;
import static com.massimo.ronzulli.turnironzulli.GR.Graphic.getBackgroundColor;
import static com.massimo.ronzulli.turnironzulli.GR.Graphic.getBorderColor;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.CrLf;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.isNumeric;

public class HomeFragment extends Fragment {
    private SavesEnum savesEnum;
    private Context context;
    View view;
    private ScrollView calendar_scroll;
    private Leaves leaves;
    public TextView[][] ET = new TextView[7][7];
    private TextView tv_scroll,text_overwork,text_leave;
    private Scroller mSlr;
    private TableRow TableRow1;
    private Button buttonLeft,buttonCenterLeft,buttonCenter,buttonRight;
    private ImageButton button_save;
    private DatePickerDialog datePickerDialog;
    private EditText textContainer;
    private String textContainerStringTemp;
    private int yearG,monthG,dayG,weekG,dayOfWeekG;

    private int nowI;//indice i del giorno corrente
    private int nowJ;//indice j del giono corrente
    private final String TAG=getClass().getName();
    private Styles styles;
    private int borderColorNow=Color.RED;
    private int borderColorWidth=10;
    private int dotColorNote=Color.RED;
    private int dotColorOverworks=Color.RED;
    private int dotColorOverworksAndLeave=Color.parseColor("#FFA500");//ORANGE
    private int dotColorLeave=Color.GREEN;
    private String callDialogHour;//motivo della chiamata del dialogo "leaves" - "overwork"
    private String leaveString;//numero del tipo di permesso

    //selected cell properties
    private int selCellX,selCellY,selDay,selWeek,selDayOfWeek,selBackColor,selBorderColor,selTextColor,selDotColor,selSideColor,selDotColor2,selSideColor2,reminderColor;
    private String selSD,selNote;
    private int rotationProfile;
    Vibrator vibrator;

    private int mLastX,mLastY;
    private boolean mSwipingLeft,mSwipingRight,mSwipingTop,mSwipingBottom,swipeStart;

    //region parametri salvati
    /* memorizziamo i dati per cella usando il formato {anno-mese-giorno}{impostazione}
     *         putSinglePref(Context context, String key               , String value        , @NotNull String set)
     *  esempio putSinglePref(context        , {2021-01-25}{borderColor}, (String) Color.GREEN, "cellsSettings"    )
     *  set di impostazioni "appSettings"
     *  {defaultCellColor}
     *  {defaultBorderSize}
     *  {defaultCharSize}
     *  set di impostazioni "cellsSettings"
     *  impostazioni
     *  {cornerColor}
     *  {backgroundColor}
     *  {style}//identifica la categoria di giornata
     *  {straordinario} ore di straordinario
     *  {permesso} ore di permesso
     *  {tipoPermesso} tipo di permesso
     *  {nota}//identifica la quantità di note
     *
    * */
    //endregion
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        savesEnum=new SavesEnum();
        //region edittext declaration
        ET[0][0]=view.findViewById(R.id.tV1);
        ET[1][0]=view.findViewById(R.id.tV2);
        ET[2][0]=view.findViewById(R.id.tV3);
        ET[3][0]=view.findViewById(R.id.tV4);
        ET[4][0]=view.findViewById(R.id.tV5);
        ET[5][0]=view.findViewById(R.id.tV6);
        ET[6][0]=view.findViewById(R.id.tV7);
        ET[0][1]=view.findViewById(R.id.tV11);
        ET[1][1]=view.findViewById(R.id.tV12);
        ET[2][1]=view.findViewById(R.id.tV13);
        ET[3][1]=view.findViewById(R.id.tV14);
        ET[4][1]=view.findViewById(R.id.tV15);
        ET[5][1]=view.findViewById(R.id.tV16);
        ET[6][1]=view.findViewById(R.id.tV17);
        ET[0][2]=view.findViewById(R.id.tV21);
        ET[1][2]=view.findViewById(R.id.tV22);
        ET[2][2]=view.findViewById(R.id.tV23);
        ET[3][2]=view.findViewById(R.id.tV24);
        ET[4][2]=view.findViewById(R.id.tV25);
        ET[5][2]=view.findViewById(R.id.tV26);
        ET[6][2]=view.findViewById(R.id.tV27);
        ET[0][3]=view.findViewById(R.id.tV31);
        ET[1][3]=view.findViewById(R.id.tV32);
        ET[2][3]=view.findViewById(R.id.tV33);
        ET[3][3]=view.findViewById(R.id.tV34);
        ET[4][3]=view.findViewById(R.id.tV35);
        ET[5][3]=view.findViewById(R.id.tV36);
        ET[6][3]=view.findViewById(R.id.tV37);
        ET[0][4]=view.findViewById(R.id.tV41);
        ET[1][4]=view.findViewById(R.id.tV42);
        ET[2][4]=view.findViewById(R.id.tV43);
        ET[3][4]=view.findViewById(R.id.tV44);
        ET[4][4]=view.findViewById(R.id.tV45);
        ET[5][4]=view.findViewById(R.id.tV46);
        ET[6][4]=view.findViewById(R.id.tV47);
        ET[0][5]=view.findViewById(R.id.tV51);
        ET[1][5]=view.findViewById(R.id.tV52);
        ET[2][5]=view.findViewById(R.id.tV53);
        ET[3][5]=view.findViewById(R.id.tV54);
        ET[4][5]=view.findViewById(R.id.tV55);
        ET[5][5]=view.findViewById(R.id.tV56);
        ET[6][5]=view.findViewById(R.id.tV57);
        ET[0][6]=view.findViewById(R.id.tV61);
        ET[1][6]=view.findViewById(R.id.tV62);
        ET[2][6]=view.findViewById(R.id.tV63);
        ET[3][6]=view.findViewById(R.id.tV64);
        ET[4][6]=view.findViewById(R.id.tV65);
        ET[5][6]=view.findViewById(R.id.tV66);
        ET[6][6]=view.findViewById(R.id.tV67);
        //endregion
        leaves = new Leaves(context);
        textContainer=view.findViewById(R.id.text_container);

        buttonLeft=view.findViewById(R.id.buttonLeft);
        buttonCenterLeft=view.findViewById(R.id.buttonCenterLeft);
        buttonCenter=view.findViewById(R.id.buttonCenter);
        buttonRight=view.findViewById(R.id.buttonRight);

        button_save=view.findViewById(R.id.button_save);
        text_overwork=view.findViewById(R.id.text_overwork);
        text_leave=view.findViewById(R.id.text_leave);
        buttonCenterLeft.setOnClickListener(v -> setNow());
        styles= new Styles(context);
        vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        calendar_scroll=view.findViewById(R.id.calendar_scroll);
        rotationProfile=1;
        try {
            //preimposta il datepicker
            initDatePicker(view);
            //preleva la data corrente
            buttonCenter.setText(getTodaysDate());
            buttonCenter.setOnClickListener(v -> {
                initDatePickerDate(view);
                datePickerDialog.show();
            });
            buttonLeft.setOnClickListener(v -> previousMonth());
            buttonRight.setOnClickListener(v -> nextMonth());
            button_save.setOnClickListener(v -> saveCellNote());

        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onCreateView: " + e.getMessage());
                Log.e(TAG, "onCreateView: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        try {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    initializeLayout(view);
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onResume: " + e.getMessage());
                Log.e(TAG, "onResume: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        calendar_scroll.setOnTouchListener((v, event) -> {
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
                    //Log.e(TAG, "onInterceptTouchEvent: ACTION_UP deltaX=" + deltaX + " x="+ x + " y="+ y);
                    //Log.e(TAG, "onInterceptTouchEvent: mLastX = " + mLastX + " x = " + x +" deltaX = " + deltaX + " deltaY = " + deltaY);
                    if (deltaX>deltaY){
                        if (mLastX<x){
                            mSwipingLeft=true;
                            previousMonth();
                            //swipeScreenLeft();
                        } else {
                            nextMonth();
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //region latest update windows
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;
            String version="";
            try {
                info = manager.getPackageInfo(context.getPackageName(), 0);
                version = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.getMessage();
                e.printStackTrace();
            }
            String latestVer= getSinglePref(context,"latestVer",  savesEnum.appSettings.APP_SETTINGS);
            if (!version.equals(latestVer)) {
                putSinglePref(context,"latestVer",version,  savesEnum.appSettings.APP_SETTINGS);
                String title = getString(R.string.latest_update_title);
                String message = getString(R.string.version)
                        .concat( " " )
                        .concat( version )
                        .concat( CrLf).concat( CrLf)
                        .concat(getString(R.string.latest_update_string))
                        .concat( CrLf).concat( CrLf)
                        .concat(getString(R.string.latest_update_note));
                final DialogOK dialogOK = new DialogOK(context, title, message);
                dialogOK.show();
                dialogOK.setOnDismissListener(dialogInterface -> {
                    //todo:after ok call help me dialog
                    //showDialogOkCancel( "Aiuto", "Vuoi aiuto", "1");
                });
            }
            if (latestVer.equals("")) {

            }
            //endregion
        }   catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onResume: " + e.getMessage());
                Log.e(TAG, "onResume: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
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
    //restituisce il primo giorno del mese come giorno della settimana
    private int getFirstDayOfMonth(int month){
        return getFirstDayOfMonth(month,yearG);
    }
    private int getFirstDayOfMonth(int month,int year){
        int day=1;
        Calendar cal= new GregorianCalendar();
        cal.set(year, month, 1);
        day=cal.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    //restituisce il primo giorno del mese come giorno della settimana
    private int getLastDayOfMonth(int month){
        return getLastDayOfMonth(month,yearG);
    }
    private int getLastDayOfMonth(int month,int year){
        int days=1;
        Calendar cal= new GregorianCalendar();
        cal.set(yearG, month, 1);
        days=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }
    //preimposta il calendar picker assegnando ad oggi la data preimpostata
    private void initDatePicker(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month +=1;
                String date=makeDateString(dayOfMonth, month,year);
                buttonCenter.setText(date);
            }
        };
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        //int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_TurniRonzulli_NoActionBar, dateSetListener,  year,  month,  day);
    }
    //preimposta il calendar picker con la data preimpostata in yearG,monthG,dayG
    private void initDatePickerDate(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //month +=1;
                monthG=month;
                yearG=year;
                dayG=dayOfMonth;
                selDay=dayG;
                setCellsMonth(monthG, yearG);
                buttonCenter.setText(makeDateString(dayOfMonth, month,year));
            }
        };
        Calendar calendar=Calendar.getInstance();
        calendar.set(yearG,monthG,dayG);
        datePickerDialog=new DatePickerDialog( view.getContext(),R.style.Theme_TurniRonzulli_NoActionBar, dateSetListener,  yearG,  monthG, dayG);
    }
    //realizza la stringa da inserire come testo nel pulsante
    private String makeDateString(int dayOfMonth, int month, int year) {
        getNowIJ(dayOfMonth, month, year);
        return String.valueOf(dayOfMonth)
                .concat( " " )
                .concat( getMonthFormat(month+1) )
                .concat( " ")
                .concat(String.valueOf(year));
    }
    private void getNowIJ(int day, int month, int year){
        int i=0;
        int j=0;
        int firstDayOfMonth=getFirstDayOfMonth(month,year);//giorno della settimana del primo giorno
        int dayOfMonth=1;
        int daysOfMonth=getLastDayOfMonth(month,year);//giorni totali del mese
        int firstDay=0;
        try {
            if (firstDayOfMonth==1){firstDay=6;} else {firstDay=firstDayOfMonth-2;}
            for ( j = 0; j <= 6; j++) {
                for ( i = 0; i <= 6; i++) {
                    if (j==1 && i<firstDay){
                    } else {
                        //primo rigo
                        if (j==1 && i>=firstDay){
                            if( dayOfMonth==day){
                                nowI=i;
                                nowJ=j;
                            }
                            dayOfMonth+=1;
                        } else {
                            if(j>=2 && dayOfMonth<=daysOfMonth){
                                if( dayOfMonth==day){
                                    nowI=i;
                                    nowJ=j;
                                }
                                dayOfMonth+=1;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "getNowIJ: " + e.getMessage());
                Log.e(TAG, "getNowIJ: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void setNow(){
        getTodaysDate();
        selDay=dayG;
        setCellsMonth(monthG, yearG);
        buttonCenter.setText(makeDateString(dayG, monthG,yearG));
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

    private void initializeLayout(View v){
        int firstDayOfMonth=getFirstDayOfMonth(monthG);
        int dayOfMonth=1;
        int daysOfMonth=getLastDayOfMonth(monthG);//giorni totali del mese
        //i giorni della settimana vengono trasformati da 1(domenica) a 7(sabato) -> 0(lunedì) a 6(domenica)
        try {
            //region edittext declaration
            ET[0][0]=v.findViewById(R.id.tV1);
            ET[1][0]=v.findViewById(R.id.tV2);
            ET[2][0]=v.findViewById(R.id.tV3);
            ET[3][0]=v.findViewById(R.id.tV4);
            ET[4][0]=v.findViewById(R.id.tV5);
            ET[5][0]=v.findViewById(R.id.tV6);
            ET[6][0]=v.findViewById(R.id.tV7);
            ET[0][1]=v.findViewById(R.id.tV11);
            ET[1][1]=v.findViewById(R.id.tV12);
            ET[2][1]=v.findViewById(R.id.tV13);
            ET[3][1]=v.findViewById(R.id.tV14);
            ET[4][1]=v.findViewById(R.id.tV15);
            ET[5][1]=v.findViewById(R.id.tV16);
            ET[6][1]=v.findViewById(R.id.tV17);
            ET[0][2]=v.findViewById(R.id.tV21);
            ET[1][2]=v.findViewById(R.id.tV22);
            ET[2][2]=v.findViewById(R.id.tV23);
            ET[3][2]=v.findViewById(R.id.tV24);
            ET[4][2]=v.findViewById(R.id.tV25);
            ET[5][2]=v.findViewById(R.id.tV26);
            ET[6][2]=v.findViewById(R.id.tV27);
            ET[0][3]=v.findViewById(R.id.tV31);
            ET[1][3]=v.findViewById(R.id.tV32);
            ET[2][3]=v.findViewById(R.id.tV33);
            ET[3][3]=v.findViewById(R.id.tV34);
            ET[4][3]=v.findViewById(R.id.tV35);
            ET[5][3]=v.findViewById(R.id.tV36);
            ET[6][3]=v.findViewById(R.id.tV37);
            ET[0][4]=v.findViewById(R.id.tV41);
            ET[1][4]=v.findViewById(R.id.tV42);
            ET[2][4]=v.findViewById(R.id.tV43);
            ET[3][4]=v.findViewById(R.id.tV44);
            ET[4][4]=v.findViewById(R.id.tV45);
            ET[5][4]=v.findViewById(R.id.tV46);
            ET[6][4]=v.findViewById(R.id.tV47);
            ET[0][5]=v.findViewById(R.id.tV51);
            ET[1][5]=v.findViewById(R.id.tV52);
            ET[2][5]=v.findViewById(R.id.tV53);
            ET[3][5]=v.findViewById(R.id.tV54);
            ET[4][5]=v.findViewById(R.id.tV55);
            ET[5][5]=v.findViewById(R.id.tV56);
            ET[6][5]=v.findViewById(R.id.tV57);
            ET[0][6]=v.findViewById(R.id.tV61);
            ET[1][6]=v.findViewById(R.id.tV62);
            ET[2][6]=v.findViewById(R.id.tV63);
            ET[3][6]=v.findViewById(R.id.tV64);
            ET[4][6]=v.findViewById(R.id.tV65);
            ET[5][6]=v.findViewById(R.id.tV66);
            ET[6][6]=v.findViewById(R.id.tV67);
            //endregion
            tv_scroll=v.findViewById(R.id.scroll_text);
            tv_scroll.setSelected(true);
            TableRow1 =v.findViewById(R.id.TableRow1);
            //region imposta la dimensione delle caselle
            //preleva la dimensione dello schermo
            int widthOfView=TableRow1.getWidth();
            //preleva il padding delle righe
            float padTableRow=TableRow1.getPaddingLeft();
            //preleva il layout_marginend delle celle
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)ET[0][0].getLayoutParams();
            float marginEndTextView = params.getMarginEnd();
            //imposta la dimensione finale
            float ETWidth = (widthOfView-((6*padTableRow)+(8*marginEndTextView)))/7;
            //endregion
            setCellsMonth(monthG, yearG);//imposta tutte le celle
            //assegna gli eventi onclick alle celle
            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 6; j++) {
                    final int tempi=i,tempj=j;
                    ET[i][j].setWidth((int)ETWidth);
                    //se j=0 parliamo del rigo con i nomi dei giorni
                    if(j>0){
                        ET[i][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onclickCells(tempi, tempj);
                            }
                        });
                        ET[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                onclickCells(tempi, tempj);
                                showDialogSelection(tempi, tempj);
                                return false;
                            }
                        });
                    }
                }
            }
            //imposta la nota a piè di pagina
            textContainer.setText(R.string.string_note_def);
            //disabilita l'editazione della nota
            textContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        textContainer.setEnabled(false);
                        textContainer.setText(getString(R.string.string_note_def).concat(textContainerStringTemp));
                        button_save.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                e.printStackTrace();
            }
        }
    }
    private void onclickCells(int tempi, int tempj){
        //ripristino i bordi della precedente cella selezionata
        //prelevo le impostazioni della cella selezionata
        getColorsStyleCell();
        getCornerStyleCell();
        //riapplico il default
        changeBackgroundTextView(ET[selCellX][selCellY],selBorderColor,selBackColor,selDotColor, selSideColor,selDotColor2,selSideColor2,0,0);
        //reimposto la scrittura nelle note
        textContainer.setEnabled(false);
        button_save.setVisibility(View.GONE);

        selCellX=tempi;
        selCellY=tempj;
        String tmpStr;
        //prelevo il testo completo
        tmpStr=ET[selCellX][selCellY].getText().toString();
        //estraggo solo il numero del giorno
        tmpStr = tmpStr.substring(0,tmpStr.indexOf("\n"));
        if(tmpStr.length()>0){
            boolean isNumber = Pattern.matches("[0-9]+", tmpStr);
            if(isNumber){
                selDay= Integer.parseInt(tmpStr);
            }
        }
        getColorsStyleCell();
        getCornerStyleCell();

        //imposto i colori della cella
        changeBackgroundTextView(ET[selCellX][selCellY],borderColorNow,selBackColor,selDotColor, selSideColor,selDotColor2,selSideColor2,borderColorWidth,0);
        ET[selCellX][selCellY].setTextColor(selTextColor);
        //inserisco il testo nelle note
        setTextEditCells();
        //imposto la data sul pulsante centrale
        buttonCenter.setText(makeDateString(selDay, monthG,yearG));
    }
    //imposta tutte le celle
    private void setCellsMonth(int month, int year){
        int firstDay=0,dayOfMonth=1,totalStyles=1;
        int firstDayOfMonth=getFirstDayOfMonth(month,year);//giorno della settimana del primo giorno
        int daysOfMonth=getLastDayOfMonth(month,year);//giorni totali del mese
        int borderColor,backgroundColor,textColor=Color.BLACK,dotcolor,sidedotcolor;
        int nowDay=1;
        String shortDescription="";
        if (firstDayOfMonth==1){firstDay=6;} else {firstDay=firstDayOfMonth-2;}
        totalStyles= getTotalStyles();
        yearG=year;monthG=month;
        int sizeET=12;
        String size= getSinglePref(context, "Default size font", savesEnum.appSettings.GENERAL_SETTINGS);
        if (size.length()>0) sizeET= Integer.parseInt(size);
        for (int j = 0; j <= 6; j++) {
            for (int i = 0; i <= 6; i++) {
                ET[i][j].setVisibility(View.VISIBLE);
                ET[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeET);

                if (j==1 && i<firstDay){
                    //cancella le caselle fino al primo giorno del mese
                    ET[i][j].setVisibility(View.INVISIBLE);
                } else {
                    //da qui sono tutte le celle visibili
                    selDay=dayOfMonth;
                    if ((j==1 && i>=firstDay)||(j>=2 && dayOfMonth<=daysOfMonth)){
                        //prelevo lo stile del turno
                        String Style=getSinglePref(getContext(),
                                "{".concat(String.valueOf(year))
                                        .concat("-")
                                        .concat(String.valueOf(month))
                                        .concat("-")
                                        .concat(String.valueOf(dayOfMonth))
                                        .concat("}{style}"),
                                String.valueOf(year)
                                        .concat(" single cell"));
                        //prelevo le impostazioni generiche del giorno selezionato
                        borderColor=getBorderColor(this.getContext(),dayOfMonth,month,year);
                        backgroundColor=getBackgroundColor(this.getContext(),dayOfMonth,month,year);
                        textColor=Color.BLACK;
                        shortDescription="";
                        //se lo stile è salvato lo imposto nella cella
                        if(Style.length()!=0){
                            boolean styleExist=false;
                            if (isNumeric(Style)){
                                //ciclo tra gli stili
                                for (int m=1;m<= totalStyles;m++){
                                    //prelevo quello con id uguale
                                    if(Integer.parseInt(Style)==styles.style[m].id){
                                        borderColor=styles.style[m].bbc;
                                        backgroundColor=styles.style[m].bc;
                                        textColor=styles.style[m].tc;
                                        shortDescription=styles.style[m].sd;
                                        styleExist=true;
                                    }
                                }
                                if (!styleExist){
                                    //cerco tra gli elementi cancellati
                                    styles.styleCellsArrayList.size();
                                    for (int k=0; k< styles.styleCellsArrayList.size();k++ ){
                                        if(styles.styleCellsArrayList.get(k).id==Integer.parseInt(Style)){
                                            borderColor=styles.styleCellsArrayList.get(k).bbc;//borderColor
                                            backgroundColor=styles.styleCellsArrayList.get(k).bc;//backgroundColor
                                            textColor=styles.styleCellsArrayList.get(k).tc;//textColor
                                            shortDescription=styles.styleCellsArrayList.get(k).sd;//short description
                                        }
                                    }
                                }

                            }
                        }
                        //prelevo selDotColor e selSideColor
                        getCornerStyleCell();
                        //see è il giorno corrente imposto il bordo a rosso ed aumento lo spessore
                        if (dayOfMonth==dayG){
                            nowI=i;//memorizzo in queste variabili la locazione di oggi
                            nowJ=j;
                            selBackColor=backgroundColor;
                            //alla giornata corrente imposta il bordo rosso
                            changeBackgroundTextView(ET[i][j],borderColorNow,selBackColor,selDotColor, selSideColor,selDotColor2,selSideColor2,borderColorWidth,0);
                            //prelevo le coordinate della selezione
                            selCellX=nowI; //queste coordinate vengono usata negli onclick listeners
                            selCellY=nowJ;
                            //imposto il giorno selezionato
                            selDay=dayOfMonth;
                            nowDay=dayOfMonth;
                        } else {
                            changeBackgroundTextView(ET[i][j],borderColor,backgroundColor,selDotColor,selSideColor,selDotColor2,selSideColor2,0,0);
                        }
                        //il testo richiede un settaggio a parte
                        ET[i][j].setTextColor(textColor);
                        ET[i][j].setText(new StringBuilder().append(dayOfMonth).append("\n").append(shortDescription).toString());
                        dayOfMonth+=1;
                    } else {
                        if(j>=2){
                            //cancello le ultime dallo schermo
                            ET[i][j].setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
        selDay=nowDay;
    }
    //endregion
    //region chiamate alle finestre di dialogo
    //mostra il dialogo di scelta delle varie possibilità delle celle
    private void showDialogSelection(int i,  int j) {
        final String[] listItems = {getString(R.string.string_change_style), getString(R.string.string_edit_note), getString(R.string.string_delete_note),getString(R.string.overwork),getString(R.string.leave),getString(R.string.overwork_remove),getString(R.string.leave_remove)};//,getString(R.string.other)
        final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
        mBuilder.setTitle(getString(R.string.string_options));
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i1) -> {
            switch (i1){
                case 0:
                    //cambia turno
                    showDialogSelectionShifts();
                    break;
                case 1:
                    //Modifica nota
                    textContainer.setEnabled(true);
                    textContainerStringTemp=substring(textContainer.getText().toString(),5,textContainer.getText().length());
                    textContainer.setText(textContainerStringTemp);
                    textContainer.requestFocus();
                    button_save.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    //cancella nota
                    putSinglePref(context,"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{note}"), "",String.valueOf(yearG).concat(" single cell"));
                    setTextEditCells();
                    onclickCells(selCellX, selCellY);
                    break;
                case 3:
                    //aggiungi straordinario
                    callDialogHour="overwork";
                    showDialogHour();
                    break;
                case 4:
                    //aggiungi permesso
                    showDialogLeaves();
                    //startEditLeaveActivity();
                    break;
                case 5:
                    //rimuovi straordinrio
                    removeSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{straord}"),String.valueOf(yearG).concat(" single cell"));
                    onclickCells(selCellX, selCellY);
                    break;
                case 6:
                    //rimuovi permesso
                    removeSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{perm}"),String.valueOf(yearG).concat(" single cell"));
                    removeSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{perm tipo}"),String.valueOf(yearG).concat(" single cell"));
                    onclickCells(selCellX, selCellY);
                    break;
                case 7:
                    //aggiungi promemoria
                    showDialogReminder();
                    break;
            }
            dialogInterface.dismiss();;
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    private void showDialogReminder(){
        DialogReminder dialogReminder = new DialogReminder(this.getActivity());
        dialogReminder.setOnDismissListener(dialog -> {
            String styleString=getSinglePref(context ,"{change}", savesEnum.appSettings.APP_SETTINGS);
            if (styleString.length()!=0){
                setCellStyle(styleString);
            }
        });
        dialogReminder.show();
    }
    //mostra il dialogo di scelta del turno
    private void showDialogSelectionShifts() {
        DialogStyleSelector dialogStyleSelector=new DialogStyleSelector(this.getActivity());
        dialogStyleSelector.setOnDismissListener(dialog -> {
            String styleString=getSinglePref(context ,"{change}", savesEnum.appSettings.APP_SETTINGS);
            if (styleString.length()!=0){
                setCellStyle(styleString);
            }
        });
        dialogStyleSelector.show();
    }
    //mostra il dialogo di scelta dei permessi
    private void showDialogLeaves() {
        DialogLeaveSelector dialogLeaveSelector=new DialogLeaveSelector(this.getActivity());
        dialogLeaveSelector.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                leaveString=getSinglePref(context ,"{leave style}", savesEnum.appSettings.APP_SETTINGS);
                if(leaveString.length()!=0){
                    callDialogHour="leaves";
                    showDialogHour();
                }
            }
        });
        dialogLeaveSelector.show();
    }
    //mostra il dialogo di selezione delle ore
    private void showDialogHour() {
        DialogHourSelector dialogHourSelector=new DialogHourSelector(this.getActivity());
        dialogHourSelector.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String stringStartHour=getSinglePref(context ,"{temp hour start}", savesEnum.appSettings.APP_SETTINGS);
                String stringEndHour=getSinglePref(context ,"{temp hour end}", savesEnum.appSettings.APP_SETTINGS);
                if(stringStartHour.length()!=0 && stringEndHour.length()!=0){
                    switch (callDialogHour){
                        case "leaves":
                            putSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{perm}"),stringStartHour.concat("-").concat(stringEndHour),String.valueOf(yearG).concat(" single cell"));
                            putSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{perm tipo}"),leaveString,String.valueOf(yearG).concat(" single cell"));
                            break;
                        case "overwork":
                            putSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{straord}"),stringStartHour.concat("-").concat(stringEndHour),String.valueOf(yearG).concat(" single cell"));
                            break;
                    }
                    onclickCells(selCellX, selCellY);
                }
                callDialogHour="";
            }
        });
        dialogHourSelector.show();
    }
    //endregion

    //region impostazioni delle note
    //imposta il testo nella textedit delle note
    private void setTextEditCells(){
        String tmpStr;
        tmpStr=getSinglePref(context,"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{note}"),String.valueOf(yearG).concat(" single cell"));

        if(tmpStr.length()!=0){
            textContainerStringTemp=tmpStr;
            textContainer.setText(String.format("%s%s", getString(R.string.string_note_def), tmpStr));
        } else {
            textContainerStringTemp="";
            textContainer.setText(getString(R.string.string_note_def));
        }
    }
    //memorizza il valore delle note
    private void saveCellNote() {
        String tempString=textContainer.getText().toString();
        textContainerStringTemp=tempString;
        putSinglePref(context,"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{note}"), tempString,String.valueOf(yearG).concat(" single cell"));
        textContainer.setEnabled(false);
        button_save.setVisibility(View.GONE);
        onclickCells(selCellX, selCellY);
    }
    //endregion


    //imposta nella cella lo stile generico e lo salva
    private void setCellStyle(String id){
        putSinglePref(getContext(),"{".concat(String.valueOf(yearG)).concat("-").concat(String.valueOf(monthG)).concat("-").concat(String.valueOf(selDay)).concat("}{style}"),id,String.valueOf(yearG).concat(" single cell"));
        getColorsStyleCell();
        getCornerStyleCell();
        //imposto i colori della cella
        changeBackgroundTextView(ET[selCellX][selCellY],borderColorNow,selBackColor,selDotColor, selSideColor,selDotColor2,selSideColor2,borderColorWidth,0);
        ET[selCellX][selCellY].setTextColor(selTextColor);
        //imposto il testo
        ET[selCellX][selCellY].setText(String.valueOf(selDay).concat("\n").concat(selSD));
        //inserisco il testo nelle note
        setTextEditCells();
    }
    //restituisce il numero totale degli stili
    private int getTotalStyles(){
        int retInt=1;
        String totalStylesString=getSinglePref(getContext(), "{total styles}",savesEnum.appSettings.APP_SETTINGS);
        if (totalStylesString.length()!=0){
            retInt= Integer.parseInt(totalStylesString);
        }
        return retInt;
    }
    //imposta nelle variabili globali selBorderColor selBackColor selTextColor selSD i colori precedentemente salvati
    private void getColorsStyleCell(){
        int totalStyles=getTotalStyles();
        boolean styleExist=false;
        String Style = getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{style}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        if(isNumeric(Style)){
            //ciclo tra gli stili
            for (int m=1;m<= totalStyles;m++){
                //prelevo quello con id uguale
                if(Integer.parseInt(Style)==styles.style[m].id){
                    selBorderColor=styles.style[m].bbc;//borderColor
                    selBackColor=styles.style[m].bc;//backgroundColor
                    selTextColor=styles.style[m].tc;//textColor
                    selSD=styles.style[m].sd;//short description
                    styleExist=true;
                }
            }
            if (!styleExist){
                //cerco tra gli elementi cancellati
                styles.styleCellsArrayList.size();
                for (int k=0; k< styles.styleCellsArrayList.size();k++ ){
                    if(styles.styleCellsArrayList.get(k).id==Integer.parseInt(Style)){
                        selBorderColor=styles.styleCellsArrayList.get(k).bbc;//borderColor
                        selBackColor=styles.styleCellsArrayList.get(k).bc;//backgroundColor
                        selTextColor=styles.styleCellsArrayList.get(k).tc;//textColor
                        selSD=styles.styleCellsArrayList.get(k).sd;//short description
                    }
                }
            }
        } else {
            selBorderColor=Color.BLACK;//borderColor
            selBackColor=Color.parseColor("#F7F7ED");//backgroundColor
            selTextColor=Color.BLACK;//textColor
            selSD="";//short description
        }

    }
    @SuppressLint("SetTextI18n")
    private void getCornerStyleCell(){
        //la sola presenza indica che dobbiamo contrassegnare le celle
        String note=getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{note}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        String straord=getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{straord}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        String perm=getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{perm}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        String permType=getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{perm tipo}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        String reminder=getSinglePref(getContext(),
                "{"
                        .concat(String.valueOf(yearG))
                        .concat("-")
                        .concat(String.valueOf(monthG))
                        .concat("-")
                        .concat(String.valueOf(selDay))
                        .concat("}{promemoria}"),
                String.valueOf(yearG)
                        .concat(" single cell"));
        //recupero i dati dei permessi o straordinari
        if (permType.length()>0){
            String leaveDescription=getSinglePref(this.context,"{leave description}{".concat(permType).concat("}"),savesEnum.appSettings.LEAVE_SETTINGS);
            text_leave.setText(perm.concat(" ").concat(leaveDescription));
            text_leave.setVisibility(View.VISIBLE);
        } else {
            text_leave.setText(getResources().getString(R.string.leave_short));
            text_leave.setVisibility(View.GONE);
        }
        if (straord.length()>0){
            text_overwork.setText(straord .concat( " " ).concat( getResources().getString(R.string.overwork_short)));
            text_overwork.setVisibility(View.VISIBLE);
        } else {
            text_overwork.setText(getResources().getString(R.string.overwork_short));
            text_overwork.setVisibility(View.GONE);
        }
        if (note.length()!=0){
            selDotColor=dotColorNote;
            selSideColor=1;
        } else {
            selDotColor=0;
            selSideColor=0;
        }
        if (straord.length()!=0 || perm.length()!=0 ){
            if(straord.length()!=0){
                selDotColor2= dotColorOverworks;
            } else {
                selDotColor2= dotColorLeave;
            }
            if (straord.length()!=0 && perm.length()!=0 ){
                selDotColor2= dotColorOverworksAndLeave;
            }
            selSideColor2=1;
        } else {
            selDotColor2=0;
            selSideColor2=0;
        }
        if (reminder.length()>0){
            String reminderSplit[] = split(reminder, ",");
            reminderColor = Integer.parseInt(reminderSplit[0]);
            int reminderID=0;
            int reminderType=0;//0 ricorrente, 1 periodico
            String reminderDescription = "";
            int reminderWakeUp = 0;//0 sveglia, //1 notifica
            int reminderHour = 0;
            int reminderMinutes =0;
            int reminderReoccurrence =0;//0 nothing, 1 30min, 2 1hour


        }
    }

    private void nextMonth(){
        if (monthG==11){
            monthG=0;
            yearG+=1;
        } else {
            monthG+=1;
        }
        setCellsMonth(monthG, yearG);
        buttonCenter.setText(makeDateString(dayG,monthG,yearG));
    }
    private void previousMonth(){
        if (monthG==0){
            monthG=11;
            yearG-=1;
        } else {
            monthG-=1;
        }
        setCellsMonth(monthG, yearG);
        buttonCenter.setText(makeDateString(dayG,monthG,yearG));
        //changeBackgroundTextView(ET[1][2],Color.BLACK,Color.GREEN,Color.RED,Color.BLUE,Color.GREEN,Color.GRAY);

    }
    private void showDialogOkCancel(String title,String mess,String step){
        //String mess=getResources().getString(R.string.string_hint_rotation);
        //String title=getResources().getString(R.string.txt_make_shift_title);
        //String step="1"; //0=nessuno
        DialogOkCancel dialogOkCancel=new DialogOkCancel(context,title,mess);
        dialogOkCancel.step=step;
        dialogOkCancel.setOnDismissListener(dialog -> {
            String retVal=getSinglePref(context,"{dialog_ok}","appSettings");
            if (retVal.length()!=0){
                //MainActivity.blink();
                //todo:enable help sequence
            }
        });
    }

    private void startEditLeaveActivity(){
        try {
            Intent intent = new Intent(context, EditLeaveActivity.class);
            this.startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startEditLeaveActivity: " + e.getMessage());
                Log.e(TAG, "startEditLeaveActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }


}
