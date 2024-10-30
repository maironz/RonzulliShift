package com.massimo.ronzulli.turnironzulli.Dialogs;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.massimo.ronzulli.turnironzulli.R;

public class DialogYesNo extends Dialog {
    public Activity activity;
    public Context context;
    private String message="";
    private String title="";

    public DialogYesNo(@NonNull Context context, String title, String message) {
        super(context);
        this.context=context;
        this.title=title;
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_yes_no);
        TextView tv_description = findViewById(R.id.tv_description);
        TextView tv_title = findViewById(R.id.tv_title);
        Button bt_yes = findViewById(R.id.bt_yes);
        Button bt_no = findViewById(R.id.bt_no);
        tv_description.setText(message);
        tv_title.setText(title);
        removeSinglePref(context,"{dialog_yes}","appSettings");
        bt_yes.setOnClickListener(v -> {
            putSinglePref(context,"{dialog_yes}","yes","appSettings");
            dismiss();
        });
        bt_no.setOnClickListener(v -> dismiss());
    }
}
