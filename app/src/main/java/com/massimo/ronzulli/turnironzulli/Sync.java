package com.massimo.ronzulli.turnironzulli;

import android.content.Context;
import android.util.Log;

import com.massimo.ronzulli.turnironzulli.SavesEnum;

import java.util.UUID;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.decrypt;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.encrypt;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class Sync {
    private static final String TAG = "Sync";
    private Context context;
    private String appID="";
    private String externalAppIDSync="";
    private String mySecret="";
    SavesEnum savesEnum=new SavesEnum();

    public Sync(Context context) throws Exception {
        this.context = context;
        appID=getSinglePref(context,"appID",savesEnum.appSettings.APP_SETTINGS);
        String mySecretTemp=getSinglePref(context,"secret",savesEnum.appSettings.APP_SETTINGS);
        if(mySecretTemp.length()>0){
            mySecret=decrypt(mySecretTemp);
        }
        getExternalAppIDSync();
    if (appID.length()==0){
            //chiave applicazione
            appID= UUID.randomUUID().toString().replace("-", "");
            putSinglePref(context,"appID",appID,savesEnum.appSettings.APP_SETTINGS);
            //chiave di cripting criptata su memoria locale
            mySecret=UUID.randomUUID().toString().replace("-", "");
            putSinglePref(context,"secret",encrypt(mySecret),savesEnum.appSettings.APP_SETTINGS);
        }
    }

    public String getAppID() {
        return appID;
    }

    public String getMySecret() {
        String retval="";
        String mySecretTemp=getSinglePref(context,"secret",savesEnum.appSettings.APP_SETTINGS);
        if(mySecretTemp.length()>0){
            retval=decrypt(mySecretTemp);
        }
        return retval;
    }
    public void setMySecret(String secret) throws Exception{
        putSinglePref(context,"secret",encrypt(secret),savesEnum.appSettings.APP_SETTINGS);
    }

    public String getExternalAppIDSync(){
        String externalAppIDSyncTemp=getSinglePref(context,"externalAppIDSync",savesEnum.appSettings.APP_SETTINGS);
        if(externalAppIDSyncTemp.length()>0){
            externalAppIDSync=decrypt(externalAppIDSyncTemp);
        } else {
            externalAppIDSync="";
        }
        return externalAppIDSync;
    }
    public void setExternalAppIDSync(String id){
        try {
            putSinglePref(context,"externalAppIDSync",encrypt(mySecret),savesEnum.appSettings.APP_SETTINGS);
            externalAppIDSync=id;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
