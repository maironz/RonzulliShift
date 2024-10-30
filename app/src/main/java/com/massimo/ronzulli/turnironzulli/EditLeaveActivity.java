package com.massimo.ronzulli.turnironzulli;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewbinding.BuildConfig;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.models.Leaves;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class EditLeaveActivity extends AppCompatActivity {
    private Context context;
    private Leaves leaves;
    private LinearLayout el_ll_leave, temp_leave;
    private LinearLayout[] leave;
    private TextView[] tv_range;
    private TextView temp_tv_range;
    private EditText ela_text_container;
    private int actualSelectionId=0,totalLeaves;
    private String arrayLeavesSave="";
    private ImageButton el_button_left,el_button_delete,el_button_save,el_button_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_leave);
        context = getApplicationContext();
        leaves = new Leaves(context);
        el_button_left=findViewById(R.id.el_button_left);
        el_button_delete=findViewById(R.id.el_button_delete);
        el_button_save=findViewById(R.id.el_button_save);
        el_button_right=findViewById(R.id.el_button_right);
        el_ll_leave=findViewById(R.id.el_ll_leave);
        ela_text_container=findViewById(R.id.ela_text_container);
        el_button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPreviousActivity();
            }
        });
        //region creazione layout a codice
        totalLeaves=leaves.getTotalLeaves();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //conversione da pixel a DP
        float factor = this.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams tv = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.gravity= Gravity.CENTER;
        //definisco la dimensione iniziale degli array
        leave = new LinearLayout[totalLeaves+1];
        tv_range = new TextView[totalLeaves+1];

        for (int i = 0; i < totalLeaves; i++) {
            //se l'ordine è impostato allora non è stato cancellato
            if (leaves.leaveList.get(i).orderLeaveID!=0){
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

                //crea l'evento solo per quelli non di default
                if(i>leaves.leavesNo-1){
                    leave[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus){
                                ela_text_container.setText("");
                                ela_text_container.setEnabled(false);
                                Log.e(TAG, "onFocusChange: ".concat(temp_text ));
                            }
                        }
                    });
                    leave[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ela_text_container.setEnabled(true);
                            ela_text_container.setText(temp_text);
                            Log.e(TAG, "onClick: ".concat(temp_text) );
                        }
                    });
                }
                //aggiungo il linearlayout al linearlayout principale
                el_ll_leave.addView(leave[i]);
            }
        }

        //endregion
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        el_button_left.requestFocus();

    }

    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            this.startActivity(intent);
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
}