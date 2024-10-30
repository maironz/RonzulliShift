package com.massimo.ronzulli.turnironzulli.STR;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.massimo.ronzulli.turnironzulli.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;


public class stringExtension {
    //TAG per debug
    private static final String TAG = stringExtension.class.getSimpleName();
    //pattern check per Email
    private static final Pattern EMAIL_ADDRESS
        = Pattern.compile(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+" +
                    "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
                    "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
                    "+[a-z0-9](?:[a-z0-9-]*[a-z0-9])"
        );
    //pattern check per password
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^" +
            "(?=.*[0-9])" +         //almeno un numero
            "(?=.*[a-z])" +         //almeno un carattere minuscolo
            "(?=.*[A-Z])" +         //almeno un carattere maiuscolo
            "(?=.*[!@#$%^&])" +       //almeno un carattere speciale
            "(?=\\S+$)" +           //niente spazi
            ".{8,64}" +               //almeno 8 caratteri
            "$");
    private static Drawable iconBitrittoBonus;
    private static int retVal1;
    public static final String CrLf = System.getProperty("line.separator");

    private static String fileExtension(@NotNull String filePath){
        //recupera tutto ciò che c'è dopo l'ultimo punto ad esempio
        //su "te.st@das.it" restituisce "it"
        String extension="";
        try {
            extension = filePath.substring(filePath.lastIndexOf(".")+1);
        } finally {
            return extension;
        }
    }
    public static String fileName(String fileName){
        //recupera tutto ciò che c'è prima dell'ultimo punto ad esempio
        //su "te.st@das.it" restituisce "te.st@das"
        String extension = fileExtension(fileName);
        String name="";
        try {
            name = fileName.substring(0,fileName.length()-extension.length()-1);
        } finally {
            return name;
        }
    }
    private static Boolean containingWord(@NotNull String text, String word){
        //restituisce true se la parola o il carattere è nel testo
        boolean containing=false;
        if (text.contains(word) && !text.isEmpty()){
            containing=true;
        }
        return containing;
    }
    private static Boolean validateEmail(@NotNull String text){
        //restituisce true per i caratteri ammessi
        boolean containing=false;
        if(!text.isEmpty()){
            if(EMAIL_ADDRESS.matcher(text).matches()){
                containing=true;
            }
        }
        return containing;
    }
    private static Boolean validatePassword(@NotNull String text){
        //restituisce true per i caratteri ammessi
        boolean containing=false;
        if(!text.isEmpty()){
            if(PASSWORD_PATTERN.matcher(text).matches()){
                containing=true;
            }
        }
        return containing;
    }
    private static Boolean validateDate(@NotNull String text){
        //restituisce true per i caratteri ammessi
        boolean containing=false;
        if(!text.isEmpty()){
            String[] separated = text.split("/");
            if (separated.length==3){
                String day = separated[0];
                String month = separated[1];
                String year = separated[2];
                if ( day.length()==2
                    && month.length()==2
                    && year.length()==4){
                    if (Integer.parseInt(day)>0 && Integer.parseInt(day)<32 &&
                            Integer.parseInt(month)>0 && Integer.parseInt(month)<13 &&
                            Integer.parseInt(year)>2019 && Integer.parseInt(year)<2200)
                    {
                        //verifico i mesi di 30 giorni
                        if (Integer.parseInt(month)==4 || Integer.parseInt(month)==6 ||
                                Integer.parseInt(month)==9 || Integer.parseInt(month)==11)
                        {
                            if (Integer.parseInt(day)<31){
                                containing=true;
                            }
                        } else {
                            //se è febbraio
                            if (Integer.parseInt(month)==2){
                                //se l'anno è bisestile
                                if((Integer.parseInt(year)-2016)/4==Math.abs((Integer.parseInt(year)-2016)/4)){
                                    containing=true;
                                }
                                //per tutti gli altri mesi
                            } else {
                                containing=true;
                            }
                        }
                    }
                }
            }
        }
        return containing;
    }
    private static Boolean validateCurrency(@NotNull String text) {
        boolean containing=false;
        try {
            if(!text.isEmpty()){
                String separated[] = text.split("\\.");
                if (separated.length==2){
                    String first=separated[0];
                    String second=separated[1];
                    if (Integer.parseInt(first)>=0){
                        if (Integer.parseInt(second)>=0){
                            if (second.length()<3){
                                containing=true;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            containing=false;
        }
        return containing;
    }
    public static Boolean isNumeric(String string){
        boolean isNumber = Pattern.matches("[0-9]+", string);
        if(isNumber){
            return true;
        } else {
            return false;
        }
    }
    public static Locale getCurrentLocale(Context context){
        return context.getResources().getConfiguration().getLocales().get(0);
    }


    public static String checkEmail(@NotNull Context context, @NotNull String email){
        //restituisce un messaggio se le regole di convalida dell e-mail non
        //sono rispettate

        String message="";
        //email=email.trim();

        //escaping special char
        if(!validateEmail(email)){
            message = context.getString(R.string.email_illegal_char);
        }
        if(fileExtension(email).length()>5
                || fileExtension(email).length()<2){
            message = context.getString(R.string.email_extension_wrong);
        }
        if(!containingWord(email,"@")){
            message = context.getString(R.string.email_without_at);

        }
        if(!containingWord(email,".") ){
            message = context.getString(R.string.email_without_dot);
        }
        //email.lastIndexOf("@")=2
        //email.lastIndexOf(".")=4
        //.->10
        int a = email.lastIndexOf(".");
        int b = email.lastIndexOf("@");
        int c=a-b;
        if(!(c>=3)){
            message = context.getString(R.string.email_wrong_format);
        }
        if(containingWord(email," ") ){
            message = context.getString(R.string.email_illegal_spaces);
        }
        return message;
    }
    public static String checkPassword(@NotNull Context context, @NotNull String password){
        //restituisce un messaggio se le regole di convalida dell Password non
        //sono rispettate
        String message="";
        if(!validatePassword(password)){
            message = context.getString(R.string.password_illegal_char);
        }
        if(fileExtension(password).length()<8){
            message = context.getString(R.string.password_extension_wrong);
        }
        if(containingWord(password," ") ){
            message = context.getString(R.string.password_illegal_spaces);
        }
        return message;
    }
    public static String checkDate(@NotNull Context context, @NotNull String date){
        String message="";
        if(!validateDate(date)){
            message = context.getString(R.string.password_illegal_date);
        }
        return message;
    }
    public static String checkCurrency(@NotNull Context context, @NotNull String currency){
        String message="";
        if(!validateCurrency(currency)){
            message = context.getString(R.string.password_illegal_currency);
        }
        return message;
    }
    public static boolean isPositiveInteger(String string){
        if (string != null){
            if (!string.equals("")){
                try {
                    int num = Integer.parseInt(string);
                    return num > 0;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
        }
        return false;
    }

    public static void notification(@NotNull View view, String title, String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), 0, new Intent(view.getContext(), view.getClass()), PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder notif = new Notification.Builder(view.getContext());
        notif.setContentTitle(title);//API 11
        notif.setContentText(text);//API 11
        notif.setSmallIcon(R.drawable.bitrittobonus1);
        notif.setAutoCancel(false);//API 11
        notif.setColor((int) 1);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notif.setSound(alarmSound);
        notif.setContentIntent(pendingIntent);//API 11
        notif.setFullScreenIntent(pendingIntent,true);//API 11
        notif.setLargeIcon(BitmapFactory.decodeResource(view.getResources(), R.drawable.bitrittobonus1));
        notif.setOnlyAlertOnce(false);
    }

    public static void setIconBitrittoBonus(@NotNull Context context){
        iconBitrittoBonus = context.getResources().getDrawable(R.drawable.bitrittobonus1, context.getTheme());
    }
    /*
    * stringExtension.dialog(view, getString(R.string.can_not_connect), getString(R.string.there_is_no_internet_connection));
    * stringExtension.dialog(context, context.getString(R.string.can_not_connect), context.getString(R.string.there_is_no_internet_connection));
    *
    */
    //esempio
    public static void dialog(View view, String title, String text){
        setIconBitrittoBonus(view.getContext());
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconBitrittoBonus);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setMessage(text);
        alertDialog.show();
    }
    public static void dialog(@NotNull Context context, String title, String text){
        setIconBitrittoBonus(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconBitrittoBonus);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setMessage(text);
        alertDialog.show();
    }
    public static int dialog(@NotNull View view, String title, String text,Boolean cancel){
        retVal1 = 0;
        setIconBitrittoBonus(view.getContext());
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconBitrittoBonus);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, view.getContext().getResources().getString(R.string.dialogNegativeButton), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Answer.YES
                retVal1=i;
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, view.getContext().getResources().getString(R.string.dialogPositiveButton), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Answer.NO
                retVal1=i;
            }
        });
        alertDialog.setMessage(text);
        alertDialog.show();
        return retVal1;
    }
    public static int dialog(@NotNull View view, String title, String text, String positiveButton,String negativeButton){
        retVal1 = 0;
        setIconBitrittoBonus(view.getContext());
        AlertDialog.Builder alerttDialog = new AlertDialog.Builder(view.getContext());
        alerttDialog.setTitle(title);
        alerttDialog.setIcon(iconBitrittoBonus);
        alerttDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                retVal1 = i;
            }
        });
        alerttDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                retVal1 = i;
            }
        });
        alerttDialog.setMessage(text);
        alerttDialog.create().show();
        return retVal1;
    }

    @NotNull
    public static String readFileTxtFromAssets(@NotNull Context context, String fileName) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.getProperty("line.separator"));
            }
            reader.close();
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    @NotNull
    public static String Left(@NotNull String st, int length){
        //"CiaoMamma" Left("CiaoMamma",5)="CiaoM"
        int stringlength=st.length();//9
        if(stringlength<=length){
            return st;
        }
        return st.substring(0,length);//9-5=4° carattere iniziale
    }
    @NotNull
    public static String Right(@NotNull String st, int length){
        //"CiaoMamma" Right("CiaoMamma",5)="Mamma"
        int stringlength=st.length();//9
        if(stringlength<=length){
            return st;
        }
        return st.substring(stringlength-length,stringlength);//9-5+1=5° carattere iniziale,9 carattere finale
    }
    @NotNull
    @Contract(pure = true)
    public static String Middle(@NotNull String st, int start, int length){
        return st.substring(start,length);
    }
    public static String makeStringFromTimepicker(int hour, int min){
        String retval="00:00";
        String strhs="00",strms="00";
        try {
            if (String.valueOf(hour).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strhs=stringbuilder.append("0").append(hour).toString();
            } else {strhs=String.valueOf(hour);}
            if (String.valueOf(min).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strms=stringbuilder.append("0").append(min).toString();
            }else {strms=String.valueOf(min);}
            retval = strhs .concat( ":" ).concat( strms );
        } catch (Exception e) {
            //Log.e(TAG, "makeStringFromTimepicker: "+ e.getMessage());
        }
        return retval;
    }

}

