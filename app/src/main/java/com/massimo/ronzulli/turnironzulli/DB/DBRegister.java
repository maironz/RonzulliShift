package com.massimo.ronzulli.turnironzulli.DB;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.STR.stringExtension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.decrypt;

public class DBRegister extends AsyncTask <String, Void,String> {

    Context context;
    View view;
    Boolean regEmail;
    Boolean operationComplete;

    private final static String TAG = DBRegister.class.getSimpleName();

    public DBRegister(Context context, View view){
        this.context = context;
        this.view = view;
        regEmail = false;
        operationComplete = false;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (!s.equals("")){
                stringExtension.dialog(view,context.getString(R.string.registration_status),s);
                if (s.equals("Registrazione non possibile, e-mail già registrata")){
                    regEmail = true;
                }
                operationComplete = true;
            }
        } catch ( Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... voids) {
        String result ="";
        String user = voids[0];
        String pass = voids[1];
        String rememberMe = voids[2];
        String arg10 ="arg964466";
        String arg10value ="AglioA#lor4!Con#23784!";
        String encryptSite;
        String connStr;

        try {
            if (isWifiConnected (context) || isMobileConnected(context)){

                encryptSite = decrypt("E4wrJCdVRIGq/WGw5O6EbX8MJUuFDK4mU5S5TpqKcpSVKKfAXCWfDSzP3Rkmu7ngQKedtzhaxmByiGVBMXgjdw==");
                connStr = encryptSite;
                URL url = new URL(connStr);
                HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setConnectTimeout(15000);
                http.setReadTimeout(15000);
                http.setDoInput(true);
                http.setDoOutput(true);

                String userString = "arg756755";
                String passwordString = "arg568637";
                String terminiString = "arg036678";
                String conditionString = "arg238469";
                String privacyString = "arg765450";
                String rememberMeString = "arg676574";
                String lang = Locale.getDefault().getLanguage();
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode(userString, "UTF-8")
                        .concat("=")
                        .concat(URLEncoder.encode(user, "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(passwordString, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode(pass, "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(conditionString, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode("true", "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(terminiString, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode("true", "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(privacyString, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode("true", "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(rememberMeString, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode(rememberMe, "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode("lang", "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode(lang, "UTF-8"))
                        .concat("&&")
                        .concat(URLEncoder.encode(arg10, "UTF-8"))
                        .concat("=")
                        .concat(URLEncoder.encode(arg10value, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.UTF_8));
                //noinspection UnusedAssignment
                String line ="";
                while((line = reader.readLine()) != null){
                    result = result.concat(line).concat(Objects.requireNonNull(System.getProperty("line.separator")));
                }
                reader.close();
                ips.close();
                http.disconnect();
                return result;
            }else {
                result = context.getString(R.string.there_is_no_internet_connection);
            }
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
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

}