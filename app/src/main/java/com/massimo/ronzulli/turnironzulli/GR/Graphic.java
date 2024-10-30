package com.massimo.ronzulli.turnironzulli.GR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import com.massimo.ronzulli.turnironzulli.SavesEnum;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class Graphic{

    public static void underscoreTextButton(@NotNull Button button) {
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
    public static String convertImageToString(@NotNull Bitmap bmp) {
        //questa funzione si usa per convertire in testo l'immagine da inviare al server on line
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, int rotation) throws IOException {
       if (rotation!=0){
           return rotate(bitmap, rotation);
       } else {
           return bitmap;
       }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    /**
     * Modfica la grafica della textviev.
     *
     * @param      tv   TextView da modificare.
     * @param      borderColor  Colore del bordo.
     * @param      fillColor   Colore di rirmpimento.
     * @param      dotColor   colore dell'angolo.
     */
    public static void changeBackgroundTextView(TextView tv, int borderColor, int fillColor, int dotColor, int reminderColor){
        changeBackgroundTextView(tv,borderColor,fillColor,dotColor, 0,dotColor,0,0,reminderColor);
    }
    /**
     * Modfica la grafica della textviev.
     *
     * @param      tv   TextView da modificare.
     * @param      borderColor  Colore del bordo.
     * @param      fillColor   Colore di rirmpimento.
     * @param      dotColor   colore dell'angolo.
     * @param      sideColor   numero dell'angolo (0-4).
     * @param      dot2color   colore dell'angolo opposto.
     * @param      side2color   numero dell'angolo opposto (0-1).
     */
    public static void changeBackgroundTextView(TextView tv, int borderColor, int fillColor, int dotColor, int sideColor,int dot2color, int side2color,int borderSize, int reminderColor){
        //changeBackgroundTextView(textViev,borderColorNow,selBackColor,selDotColor, selSideColor,Color.BLACK,0,borderColorWidth);
        //fillcolor = colore di riempimento
        //dotcolor = primo colore da utilizzare
        //sidecolor = angolo primario da colorare

        SavesEnum savesEnum=new SavesEnum();
        int color2side;
        GradientDrawable gd = new GradientDrawable();
        if (dotColor!=0 || dot2color!=0){//almeno uno dei due angoli deve essere colorato
            switch (sideColor){
                case 2:
                    gd.setOrientation(GradientDrawable.Orientation.BL_TR);
                    if (side2color==0){
                        //top-left, top-right, bottom-right, bottom-left
                        //angolo in basso a sinistra è a spigolo vivo
                        gd.setCornerRadii(new float[] {15,15,15,15,15,15,0,0});
                    } else {
                        //angolo in basso a sinistra e in alto a destra è a spigolo vivo
                        gd.setCornerRadii(new float[] {15,15,0,0,15,15,0,0});
                    }
                    break;
                case 3:
                    gd.setOrientation(GradientDrawable.Orientation.BR_TL);
                    if (side2color==0){
                        //angolo in basso a destra è a spigolo vivo
                        gd.setCornerRadii(new float[] {15,15,15,15,0,0,15,15});
                    } else {
                        //angolo in basso a destra e in alto a sinistra è a spigolo vivo
                        gd.setCornerRadii(new float[] {0,0,15,15,0,0,15,15});
                    }
                    break;
                case 4:
                    gd.setOrientation(GradientDrawable.Orientation.TR_BL);
                    if (side2color==0){
                        //angolo in alto a destra è a spigolo vivo
                        gd.setCornerRadii(new float[] {15,15,0,0,15,15,15,15});
                    } else {
                        //angolo in alto a destra e in basso a sinistra è a spigolo vivo
                        gd.setCornerRadii(new float[] {15,15,0,0,15,15,0,0});
                    }
                    break;
                default://=1
                    gd.setOrientation(GradientDrawable.Orientation.TL_BR);
                    if(dotColor!=0){//è colorato anche il primo angolo
                        if (side2color==0){
                            //angolo in alto a sinistra è a spigolo vivo
                            gd.setCornerRadii(new float[] {0,0,15,15,15,15,15,15});
                        } else {
                            //angolo in alto a sinistra e in basso a destra è a spigolo vivo
                            gd.setCornerRadii(new float[] {0,0,15,15,0,0,15,15});
                        }
                    } else {//non è colorato anche il primo angolo
                        if (side2color==0){
                            //top-left, top-right, bottom-right, bottom-left
                            gd.setCornerRadii(new float[] {15,15,15,15,15,15,15,15});
                        } else {
                            gd.setCornerRadii(new float[] {15,15,15,15,0,0,15,15});
                        }
                    }
            }
            if (side2color==0){
                color2side=fillColor;
            } else {
                color2side=dot2color;
            }
            if(dotColor==0){
                dotColor=fillColor;
            }
            if (reminderColor==0){
                reminderColor=fillColor;
            }
            gd.setColors(new int[] {dotColor,dotColor,reminderColor,reminderColor,fillColor,fillColor,fillColor,fillColor,fillColor,fillColor,fillColor,fillColor,fillColor,fillColor,color2side,color2side,color2side,color2side});
        } else {
            gd.setCornerRadius(15);
            gd.setColor(fillColor);
        }
        if (borderSize==0){borderSize=savesEnum.appSettings.DEFAULT_BORDER_SIZE;}
        gd.setStroke(borderSize, borderColor);
        tv.setBackground(gd);
    }
    //region salvataggi e prelievi dallo storage
    public static int getCornerColor(Context context,int day, int month, int year){

        int val=0;
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        String cornerString=getSinglePref(context,"{".concat(date.toString()).concat("}{cornerColor}"),"cellsSettings");
        if (cornerString==""){
            val= Color.BLACK;
        }
        return val;
    }
    public static void setCornerColor(Context context,int day, int month, int year, int borderColor){
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        putSinglePref(context,"{".concat(date.toString()).concat("}{cornerColor}"), String.valueOf(borderColor),"cellsSettings");
    }
    public static int getBorderColor(Context context,int day, int month, int year){

        int val=0;
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        String cornerString=getSinglePref(context,"{".concat(date.toString()).concat("}{borderColor}"),"cellsSettings");
        if (cornerString==""){
            val= Color.BLACK;
        }
        return val;
    }
    public static void setBorderColor(Context context,int day, int month, int year, int borderColor){
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        putSinglePref(context,"{".concat(date.toString()).concat("}{borderColor}"), String.valueOf(borderColor),"cellsSettings");
    }
    public static int getBackgroundColor(Context context,int day, int month, int year){
        int val=0;
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        String backgroundString=getSinglePref(context,"{".concat(date.toString()).concat("}{backgroundColor}"),"cellsSettings");
        if (backgroundString==""){
            val=0xD6D6D1;
        }
        return val;
    }
    public static void setBackgroundColor(Context context,int day, int month, int year, int backgroundColor){
        StringBuilder date=new StringBuilder().append(year).append("-").append(month).append("-").append(day);
        putSinglePref(context,"{".concat(date.toString()).concat("}{backgroundColor}"),String.valueOf(backgroundColor),"cellsSettings");
    }
}
