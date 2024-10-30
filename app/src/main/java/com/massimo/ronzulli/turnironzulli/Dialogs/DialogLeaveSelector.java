package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.models.Leaves;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

public class DialogLeaveSelector extends Dialog {
    SavesEnum savesEnum=new SavesEnum();
    public Activity activity;
    public Context context;
    private LinearLayout[] leave;
    private LinearLayout el_ll_leave;
    private TextView[] tv_range;
    private Leaves leaves;
    public int totalLeaves;

    public DialogLeaveSelector(Activity activit) {
        super(activit);
        this.activity = activit;
        context=activit.getBaseContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_leave_selector);
        leaves = new Leaves(context);
        el_ll_leave=findViewById(R.id.el_ll_leave);
        //region creazione layout a codice
        totalLeaves=leaves.getTotalLeaves();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //conversione da pixel a DP
        float factor = activity.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams tv = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.gravity= Gravity.CENTER;
        //definisco la dimensione iniziale degli array
        leave = new LinearLayout[totalLeaves+1];
        tv_range = new TextView[totalLeaves+1];
        removeSinglePref(context ,"{leave style}", savesEnum.appSettings.APP_SETTINGS);
        for (int i = 0; i <= totalLeaves; i++) {
            //se l'ordine è impostato allora non è stato cancellato
            if (leaves.leaveList.get(i).orderLeaveID!=-1){
                //posso aggiungere l'elemento
                leave[i] = new LinearLayout(context);
                leave[i].setLayoutParams(lp);
                leave[i].setFocusable(true);
                leave[i].setClickable(true);
                leave[i].setOrientation(LinearLayout.VERTICAL);
                leave[i].setPadding((int)(4*factor),(int)(4*factor),(int)(4*factor),(int)(4*factor));
                tv_range[i] = new TextView(context);
                tv_range[i].setLayoutParams(tv);
                tv_range[i].setPadding((int)(5*factor),(int)(5*factor),(int)(5*factor),(int)(5*factor));
                String temp_text=leaves.leaveList.get(i).leaveDescription;
                tv_range[i].setText(temp_text);
                //aggiungo la textview al linearlayout
                leave[i].addView(tv_range[i]);
                //final String var=String.valueOf(i);
                final String var= String.valueOf(leaves.leaveList.get(i).getLeaveID());
                leave[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        putSinglePref(context ,"{leave style}", var,savesEnum.appSettings.APP_SETTINGS);
                        dismiss();
                    }
                });
                leave[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        putSinglePref(context ,"{leave style}", var,savesEnum.appSettings.APP_SETTINGS);
                        dismiss();
                    }
                });
                //aggiungo il linearlayout al linearlayout principale
                el_ll_leave.addView(leave[i]);
            }
        }

        //endregion
    }
}
