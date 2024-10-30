package com.massimo.ronzulli.turnironzulli.STR;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.R;

import java.util.Arrays;

/*
*  aggiungere queste righe nel fragment per verificare l'accesso
*  il controllo avviene nella MainActivity in onRequestPermissionsResult
*
//  Permissions permissions = new Permissions(getContext(), getActivity());
//  permissions.setREQUEST_CAMERA();
*
* */


/*
*   mentre occorre inserire nell'activity principale
*   questo codice come callback della richiesta
*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //ritorno della prima richiesta di accesso al dispositivo
        final int REQUEST_CAMERA=110;
        boolean retBool=false;
        switch (requestCode){
            case REQUEST_CAMERA:
                retBool=true;
                break;
        }
        if (retBool && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //se ha l'accesso
        }  else if (!shouldShowRequestPermissionRationale(permissions[0])) {
            // se l'utente ha disabilitato la richiesta di accesso
            // gli attivo la finestra di sistema
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.parse("package:" + this.getPackageName());
            intent.setData(uri);
            //activity.startActivity(intent);
            startActivity(intent);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
*
* */
public  class Permissions {
    private static final String TAG = Permissions.class.getName();
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE=112;
    private static final int REQUEST_READ_EXTERNAL_STORAGE=114;

    public Permissions(final Context context, final Activity activity) {
    }

    public static boolean setREQUEST_WRITE_EXTERNAL_STORAGE(Context context,Activity activity){
        final int request=REQUEST_WRITE_EXTERNAL_STORAGE;
        final String sensor = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permissionTitle = context.getString(R.string.PermissionDenied);
        String permissionString = context.getString(R.string.setREQUEST_WRITE_EXTERNAL_STORAGE);
        String negativeButtonString = context.getString(R.string.negativeButtonString);
        String positiveButtonString = context.getString(R.string.positiveButtonString);
        boolean retBool=false;
        try {
            if (ContextCompat.checkSelfPermission(activity, sensor) != PackageManager.PERMISSION_GRANTED) {
                //se non ha l'accesso
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,sensor)) {
                    //questo if controlla se e' stata effettuata una richiesta precedente negata
                    //spiegazione del perche'

                    new AlertDialog.Builder(context).setTitle(permissionTitle)
                            .setMessage(permissionString)
                            .setCancelable(false)
                            .setNegativeButton(negativeButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(positiveButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, new String[]{sensor}, request);
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{sensor},request);
                }
            } else {
                // Permission has already been granted
                retBool = true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "setREQUEST_WRITE_EXTERNAL_STORAGE: " + e.getMessage());
                Log.e(TAG, "setREQUEST_WRITE_EXTERNAL_STORAGE: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        //return retBool;
        return setREQUEST(context,activity,request,sensor,permissionString,negativeButtonString,positiveButtonString);
    }

    public static boolean setREQUEST_READ_EXTERNAL_STORAGE(Context context,Activity activity){
        //necessaria per la galleria
        final int request=REQUEST_READ_EXTERNAL_STORAGE;
        final String sensor = Manifest.permission.READ_EXTERNAL_STORAGE;
        String permissionTitle = context.getString(R.string.PermissionDenied);
        String permissionString = context.getString(R.string.setREQUEST_WRITE_EXTERNAL_STORAGE);
        String negativeButtonString = context.getString(R.string.negativeButtonString);
        String positiveButtonString = context.getString(R.string.positiveButtonString);
        boolean retBool=false;
        try {
            if (ContextCompat.checkSelfPermission(activity, sensor) != PackageManager.PERMISSION_GRANTED) {
                //se non ha l'accesso
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,sensor)) {
                    //questo if controlla se e' stata effettuata una richiesta precedente negata
                    //spiegazione del perche'

                    new AlertDialog.Builder(context).setTitle(permissionTitle)
                            .setMessage(permissionString)
                            .setCancelable(false)
                            .setNegativeButton(negativeButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(positiveButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, new String[]{sensor}, request);
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{sensor},request);
                }
            } else {
                // Permission has already been granted
                retBool = true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "setREQUEST_READ_EXTERNAL_STORAGE: " + e.getMessage());
                Log.e(TAG, "setREQUEST_READ_EXTERNAL_STORAGE: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return setREQUEST(context,activity,request,sensor,permissionString,negativeButtonString,positiveButtonString);
    }

    public static boolean setREQUEST(Context context,Activity activity,final int request,final String sensor,String permissionString, String negativeButtonString,String positiveButtonString){
        Boolean retBool=false;
        try {
            if (ContextCompat.checkSelfPermission(activity, sensor) != PackageManager.PERMISSION_GRANTED) {
                //se non ha l'accesso
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,sensor)) {
                    //questo if controlla se e' stata effettuata una riciesta precedente negata
                    //spiegazione del perche'
                    new AlertDialog.Builder(context).setTitle(context.getString(R.string.PermissionDenied))
                            .setMessage(permissionString)
                            .setCancelable(false)
                            .setNegativeButton(negativeButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(positiveButtonString, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, new String[]{sensor}, request);
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{sensor},request);
                }
            } else {
                // Permission has already been granted
                retBool = true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "setREQUEST: " + e.getMessage());
                Log.e(TAG, "setREQUEST: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return retBool;
    }


}
