package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

import androidx.annotation.NonNull;

import com.massimo.ronzulli.turnironzulli.R;

public class DialogOkCancel extends Dialog {
    public Activity activity;
    public Context context;
    private TextView tv_doc_description,tv_doc_title;
    private Button bt_ok,bt_cancel;
    private String message="";
    private String title="";
    public String step="0";

    public DialogOkCancel(@NonNull Context context, String title,String message) {
        super(context);
        this.context=context;
        this.title=title;
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ok_cancel);
        tv_doc_description=findViewById(R.id.tv_doc_description);
        tv_doc_title=findViewById(R.id.tv_doc_title);
        bt_ok=findViewById(R.id.bt_ok);
        bt_cancel=findViewById(R.id.bt_cancel);
        String string= message;
        tv_doc_description.setText(string);
        tv_doc_title.setText(title);
        removeSinglePref(context,"{dialog_ok}","appSettings");
        bt_ok.setOnClickListener(v -> {
            putSinglePref(context,"{dialog_ok}","ok","appSettings");
            if (!step.equals("0")){
                putSinglePref(context,"{step}",step,"general_settings");
            }
            dismiss();
        });
        bt_cancel.setOnClickListener(v -> dismiss());
    }
}
