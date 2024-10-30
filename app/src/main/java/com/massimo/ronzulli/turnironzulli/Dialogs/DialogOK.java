package com.massimo.ronzulli.turnironzulli.Dialogs;

import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.CrLf;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.getCurrentLocale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massimo.ronzulli.turnironzulli.R;

public class DialogOK extends Dialog {
    public Activity activity;
    public Context context;
    private String title="";
    private String message="";

    public DialogOK(Context context, String title, String message) {
        super(context);
        this.title = title;
        this.message = message;
        this.context=context;
    }

    /**
     *  DialogOK dialogOK=new DialogOK(getActivity(),title,message);
     *  dialogOK.show();
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_about);
        TextView tv_da_description = findViewById(R.id.tv_da_about);
        TextView tv_da_title = findViewById(R.id.tv_da_title);
        Button bt_ok = findViewById(R.id.bt_ok);

        tv_da_title.setText(title);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        tv_da_title.setLayoutParams(params);
        tv_da_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv_da_description.setText(message);

        bt_ok.setOnClickListener(v -> dismiss());
    }
}
