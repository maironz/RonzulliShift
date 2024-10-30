package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.models.Styles;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

import static com.massimo.ronzulli.turnironzulli.GR.Graphic.changeBackgroundTextView;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

public class DialogStyleSelector extends Dialog {
    SavesEnum savesEnum=new SavesEnum();
    public Activity activity;
    public Context context;
    public LinearLayout[] ll_cell_style_new,ll_internal,ll_internal2;
    public LinearLayout styles_ll;
    public Styles styles;
    public int totalStyles;
    public TextView[] tv_short_text;
    public TextView[] tv_long_text;
    public TextView[] tv_range;


    public DialogStyleSelector(Activity activit) {
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_selector);

        String retval=getSinglePref(context,"{firstTimeOpenStylesActivity}","appSettings");
        if (retval.length()==0){
            totalStyles=Styles.makeStandardStyle(context,savesEnum);
            putSinglePref(context,"{firstTimeOpenStylesActivity}","false","appSettings");
        } else {
            totalStyles= Integer.parseInt(getSinglePref(context, "{total styles}","appSettings"));
        }
        styles= new Styles(context);
        styles_ll=findViewById(R.id.styles_ll_dialog);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //conversione da pixel a DP
        float factor = activity.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams tv = new LinearLayout.LayoutParams( (int)(50*factor),(int)(50*factor));
        tv.setMargins((int)(3*factor),(int)(3*factor),(int)(3*factor),(int)(3*factor));
        tv.gravity=Gravity.CENTER;
        removeSinglePref(context ,"{change}", savesEnum.appSettings.APP_SETTINGS);

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
            tv_short_text[i].setBackground(activity.getDrawable(R.drawable.calendar_border_square_rounded));
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
            int dotColor= Color.BLACK;
            int var=styles.style[i].id;
            tv_short_text[i].setTextColor(styles.style[i].tc);
            changeBackgroundTextView(tv_short_text[i], borderColor,fillColor,dotColor,0);
            //trasferisce l'id del rigo all'intent
            final int finalI=var;
            ll_internal[i].setOnClickListener(v -> {
                putSinglePref(context ,"{change}", String.valueOf(finalI),savesEnum.appSettings.APP_SETTINGS);
                dismiss();
            });
        }
    }
}