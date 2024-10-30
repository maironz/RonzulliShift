package com.massimo.ronzulli.turnironzulli.STR;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.massimo.ronzulli.turnironzulli.R;

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
    private String bmpToString;
    //region SendSync
    public String appID;
    public String syncAppID;
    public String syncType;
    public String mess;
    public static final String synkOk="Ok";//ritorno su invio ok dell'informazione
    private String param="h3yu8S3ha12fGUZ";

    //endregion
    private String SSID;

    private String origUrl;

    public void setBmpToString(String bmpToString) {
        this.bmpToString = bmpToString;
    }
    public DBVolley(Context context) {
        this.response="";
        this.context=context;
        origUrl=context.getResources().getString(R.string.basePath);
        mess="";
    }
    public String getResponse() {return response;}
    private void setResponse(String response) {this.response = response;}
    //region volley requests
    //transazioni di comunicazione con il server
    public void DBVolleySync(final ServerCallback callback){
        try {
            if (isWifiConnected (context) || isMobileConnected(context)){
                final String url = origUrl .concat("/turniRonzulli/tj2rgTRx5Tb6D53PlAaq.php");
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
                        setResponse(context.getString(R.string.data_saving_failed));
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onErrorResponse: " .concat( e.getMessage()));
                            Log.e(TAG, "onErrorResponse: " .concat( Arrays.toString(e.getStackTrace())));
                        } else {
                            e.printStackTrace();
                        }
                    }
                })
                {
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("par",param);
                        params.put("type",syncType);
                        params.put("idApp",appID);
                        params.put("mess",mess);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> params = new HashMap<>();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

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
