package com.massimo.ronzulli.turnironzulli.DB;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.massimo.ronzulli.turnironzulli.BuildConfig;
import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.STR.stringExtension;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;

public class DBVolley {
    private static final String TAG = DBVolley.class.getName();
    //region declarations
    private Context context;
    private String response;
    private Bitmap bmpResponse;
    private String bmpToString;
    //array per l'invio di immagini multiple
    private ArrayList<String> arrayBmp;
    //region ProfileSave
    private String MobileNumberPar;
    private String MobileNumber;
    private String StatusMobileNumberPublicPar;
    private String StatusMobileNumberPublic;
    private String NamePar;
    private String Name;
    private String SurNamePar;
    private String SurName;
    private String AddressPar;
    private String Address;
    private String AddressStreet;
    private String AddressPostalCodePar;
    private String AddressPostalCode;
    private String AddressNumber;
    private String CityPar;
    private String City;
    private String StatePar;
    private String State;
    private String StatusPersonalOnServerPar;
    private String StatusPersonalOnServer;
    private String PushNotificationsPar;
    private String PushNotifications;
    private String EmailNotificationsPar;
    private String EmailNotifications;
    private String SmsNotificationsPar;
    private String SmsNotifications;
    private String TelegramNotificationsPar;
    private String TelegramNotifications;
    private String SellerActivePar;
    private String SellerActive;
    //endregion
    //region StoreSave
    private String Store_1;
    private String Store_2;
    private String Store_VAT;
    private String Store_Address;
    private String Store_CAP;
    private String Store_City;
    private String Store_District;
    private String Store_Phone1;
    private String Store_Phone2;
    private String Store_Keywords;
    private String Store_Opening;
    private String Store_Image;
    private String Store_Image_Rotation;
    private String Store_Image_Shrink;
    private String Store_Image_Scale;
    //endregion
    private String keywords;
    private String SSID;

    private String emailPar;
    private String email;
    private String passwordPar;
    private String password;
    private String origUrl;

    //region ProductDataLoad
    private String Product_ID;
    //endregion
    //endregion
    public String getOrigUrl(){
        return origUrl;
    }
    public void setBmpToString(String bmpToString) {
        this.bmpToString = bmpToString;
    }
    public DBVolley(Context context) {
        this.response="";
        this.context=context;
        //origUrl="https://www.bitrittobonus.com" oppure "http://192.168.1.93:81"
        // occorre modificare il files values\strings.xml
        origUrl=context.getResources().getString(R.string.basePath);
    }
    public String getResponse() {return response;}
    private void setResponse(String response) {this.response = response;}
    public void DBVolleyStandardLoginExecute(final String user, final String pass, final String rem, final ServerCallback callback) {
        final String url = origUrl.concat("/runnerfw/andro/lgN45245.php");
        final String userString = "arg756755";
        final String passwordString = "arg568637";
        final String rememberMeString = "arg676574";
        final String arg10 ="arg964466";
        final String arg10value ="AglioA#lor4!Con#23784!";
        try {
            if (isWifiConnected (context) || isMobileConnected(context)){
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this.context);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            setResponse(response);
                            callback.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(context,  context.getString(R.string.ServerLost), Toast.LENGTH_SHORT).show();
                            setResponse(context.getString(R.string.loginnok));
                            if (BuildConfig.DEBUG){
                                Log.e(TAG, "DBVolleyStandardLoginExecute: ".concat( e.getMessage()));
                                Log.e(TAG, "DBVolleyStandardLoginExecute: ".concat( Arrays.toString(e.getStackTrace())));
                            } else {
                                e.printStackTrace();
                            }
                        }
                    })
            {
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap();
                    params.put(userString,user);
                    params.put(passwordString,pass);
                    params.put(rememberMeString, rem);
                    params.put(arg10,arg10value);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            //3 tentativi 1=500ms, 2=1500=(500+(500*2)), 3=4500=(1500+(1500*2))...500,3,2
            //1 tentativo 5000ms....5000,1,1
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    1,
                    1));
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            }else {
                cannotConnect(context);
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "DBVolleyStandardLoginExecute: " +" cannotConnect ok");
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "DBVolleyStandardLoginExecute: " + e.getMessage());
                Log.e(TAG, "DBVolleyStandardLoginExecute: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }

    private static boolean isConnected(@NonNull Context context, int type) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            return isConnected(connMgr, type);
        } else {
            return false;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isConnected(@NonNull ConnectivityManager connMgr, int type) {
        Network[] networks = connMgr.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network mNetwork : networks) {
            networkInfo = connMgr.getNetworkInfo(mNetwork);
            if (networkInfo != null && networkInfo.getType() == type && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }
    private static boolean isWifiConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }
    private static boolean isMobileConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }
    private static void cannotConnect(Context context){
        stringExtension.dialog(context, context.getString(R.string.can_not_connect), context.getString(R.string.there_is_no_internet_connection));
    }

}
