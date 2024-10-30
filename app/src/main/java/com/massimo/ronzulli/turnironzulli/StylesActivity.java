package com.massimo.ronzulli.turnironzulli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding;
import com.massimo.ronzulli.turnironzulli.models.Styles;

import java.util.Arrays;

import static com.massimo.ronzulli.turnironzulli.GR.Graphic.changeBackgroundTextView;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class StylesActivity extends AppCompatActivity {
    SavesEnum savesEnum=new SavesEnum();
    private View view;
    private Context context;
    private TextView[] tv_short_text;
    private TextView[] tv_long_text;
    private TextView[] tv_range;
    private LinearLayout[] ll_cell_style;
    private LinearLayout[] ll_cell_style_new,ll_internal,ll_internal2;
    private LinearLayout styles_ll;
    private Styles styles;
    private int totalStyles;
    private final String TAG=getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styles);
        context = getApplicationContext();
        view = findViewById(R.id.StylesActivity);
        SecurityExtension.securityActivate(context);

        //region declarations

        //get flag first Time
        String retval=getSinglePref(context,savesEnum.appSettings.FIRST_TIME_OPEN_STYLES_ACTIVITY,savesEnum.appSettings.APP_SETTINGS);
        //todo:uncomment the next line to initialise first 3 elements
        //retval="";
        if (retval.length()==0){
            totalStyles=Styles.makeStandardStyle(getApplicationContext(),savesEnum);
            putSinglePref(getApplicationContext(),"{firstTimeOpenStylesActivity}","false","appSettings");
        } else {
            totalStyles= Integer.parseInt(getSinglePref(getApplicationContext(), "{total styles}","appSettings"));
        }
        styles= new Styles(context);


        styles_ll=findViewById(R.id.styles_ll);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //conversione da pixel a DP
        float factor = this.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams tv = new LinearLayout.LayoutParams( (int)(50*factor),(int)(50*factor));
        tv.setMargins((int)(3*factor),(int)(3*factor),(int)(3*factor),(int)(3*factor));
        tv.gravity=Gravity.CENTER;

        ll_cell_style_new=new LinearLayout[totalStyles+1];
        tv_short_text=new TextView[totalStyles+1];
        ll_internal=new LinearLayout[totalStyles+1];
        ll_internal2=new LinearLayout[totalStyles+1];
        tv_long_text=new TextView[totalStyles+1];
        tv_range=new TextView[totalStyles+1];
        int i=0;
        for(i=1; i<=totalStyles; i++)
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
            tv_short_text[i].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.calendar_border_square_rounded, null));
            tv_short_text[i].setTextAppearance(android.R.style.TextAppearance_Small);
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
            ll_internal2[i].setAddStatesFromChildren(true);//new
            ll_internal[i].addView(tv_short_text[i]);
            ll_internal[i].addView(ll_internal2[i]);
            ll_internal[i].setAddStatesFromChildren(true);
            ll_cell_style_new[i].setAddStatesFromChildren(true);//new
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
            tv_short_text[i].setTextColor(styles.style[i].tc);
            changeBackgroundTextView(tv_short_text[i], borderColor,fillColor,dotColor,0);
            //trasferisce l'id del rigo all'intent
            ll_internal[i].setOnClickListener(v -> {

                putSinglePref(context ,"{selected style}", String.valueOf(var),"appSettings");
                Intent intent = new Intent(context, ModifyStylesActivity.class);
                intent.putExtra("index of element", var);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
                finish();
            });
        }


        //endregion
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
        getMenuInflater().inflate(R.menu.menu_styles, menu);
        return true;
    }

    private void startModifyStyleActivity() {
        try {
            Intent intent = new Intent(this, ModifyStylesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "startModifyStyleActivity: " + e.getMessage());
                    Log.e(TAG, "startModifyStyleActivity: " + Arrays.toString(e.getStackTrace()));
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            String fromStats = getSinglePref(context, "fromStats", savesEnum.appSettings.GENERAL_SETTINGS);
            if (fromStats.equals("true")){
                putSinglePref(context, "fromStats", "false", savesEnum.appSettings.GENERAL_SETTINGS);
                intent = new Intent(context, StatsActivity.class);
            } else {
                intent = new Intent(context, MainActivity.class);
            }
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

    public void onClickInformation(MenuItem item) {
        String title   = getString(R.string.settings_list_cells_style);
        String message = context.getString(R.string.style_text);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }

    public void onClickAddStyle(MenuItem item) {
        startModifyStyleActivity();
    }
}