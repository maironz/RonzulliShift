package com.massimo.ronzulli.turnironzulli.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

public class DialogReminder extends Dialog {
   SavesEnum savesEnum=new SavesEnum();
   public Activity activity;
   public Context context;
   private LinearLayout[] leave;

   public DialogReminder(Activity activity) {
      super(activity);
      this.activity = activity;
      context=activity.getBaseContext();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.dialog_reminder);

   }
}
