package com.massimo.ronzulli.turnironzulli.GR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class ImageShrink {

    /*
    * usage example
    *
    * Drawable drawable = getDrawable(R.drawable.bird);
    * try{
    *   Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
    *   Drawable finalImage = ShrinkJpg(bitmap);
    * } catch {
    *   Toast.makeText( context, "Impossibile restringere l'immagine", Toast.LENGTH_SHORT).show();
    * }
    *
    * */
    public static Bitmap getBitmapFromUrl(String path) throws Exception {
        URL url_value = new URL(path);
        return BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
    }
    public static Bitmap ShrinkJpg(Bitmap bitmap, double ratio) throws Exception {
        //questa routine restituisce una immagine compressa
        byte[] byteArray = Shrink(bitmap, ratio);
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
    }
    public static String ShrinkString(Bitmap bitmap, double ratio) throws Exception {
        //questa routine restituisce la stringa dell'immagine da inviare tramite php
        byte[] imageBytes = Shrink(bitmap, ratio);
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    public static Bitmap getBitmap (Drawable drawable) {
        return ((BitmapDrawable)drawable).getBitmap();
    }

    public static Bitmap getBitmap (final Context context,int drawable) {
        //int drawable =R.drawable.xxxx
        return BitmapFactory.decodeResource(context.getResources(), drawable);
    }

    private static byte[] Shrink(Bitmap bitmap, double ratio) throws Exception {
        // Initialize a new ByteArrayStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        /*Bitmap.CompressFormat
                Specifies the known formats a bitmap can be compressed into.
                    Bitmap.CompressFormat  JPEG
                    Bitmap.CompressFormat  PNG
                    Bitmap.CompressFormat  WEBP
        */
        // Comprime l'immagine
        int quality = 82;
        double bitmapWidth=bitmap.getWidth();
        double bitmapHeight=bitmap.getHeight();
        double bitmapDimBase;
        double ratioDef;
        bitmapDimBase = Math.min(bitmapWidth, bitmapHeight);
        //riduciamo in base alla richiesta ratio=0.8= 80% della normale riduzione
        //1000 è il valore finale dell'immagine da ottenere nel lato più grande
        ratioDef = Math.ceil(bitmapDimBase*ratio/1000);
        int bitmapNewWidth=(int)(bitmap.getWidth()/ratioDef);
        int bitmapNewHeight=(int)(bitmap.getHeight()/ratioDef);
        Bitmap bitmapNew = Bitmap.createScaledBitmap(bitmap,bitmapNewWidth,bitmapNewHeight,true);
        bitmapNew.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        bitmapNew.recycle();
        return stream.toByteArray();
    }
    public static byte[] bmpToByteArray(Bitmap bitmap) throws Exception{
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 82, stream);
        return stream.toByteArray();
    }
}
