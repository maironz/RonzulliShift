package com.massimo.ronzulli.turnironzulli;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.DB.DBVolley;
import com.massimo.ronzulli.turnironzulli.DB.ServerCallback;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;

public class SplashActivity extends AppCompatActivity {
    private Animation splash;
    private ImageView imageView;
    private View view;
    private Context context;
    private String SSID;
    private String status_checked_button_remember_me="false";
    private boolean okToStartMainActivity=false;
    private boolean internetConnection=true;
    private String TAG= getClass().getName();


    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            context = getApplicationContext();
            view = findViewById(R.id.activitySplash);
            imageView = findViewById(R.id.imageViewSplash);
            //attiva le opzioni di sicurezza e carica le impostazioni
            SecurityExtension.securityActivate(context);

            splash = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash);
            splash.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    /*
                    //recuperiamo lo stato salvato del check remember me
                    status_checked_button_remember_me = SecurityExtension.getStPref(getApplicationContext(),"{status_checked_button_remember_me}");
                    //login automatico
                    SecurityExtension.removeSinglePref(getApplicationContext(),"{SSID}","");
                    if (status_checked_button_remember_me.equals("true")) {
                        String encryptedMail = SecurityExtension.getStPref(getApplicationContext(),"{email}");
                        String encryptedPassword = SecurityExtension.getStPref(getApplicationContext(),"{password}");
                        try {
                            if (!encryptedMail.isEmpty()|| !encryptedPassword.isEmpty()){
                                String emailText = SecurityExtension.decrypt(encryptedMail);
                                String passwordText = SecurityExtension.decrypt(encryptedPassword);
                                login(emailText,passwordText,false);
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG){
                                Log.e(TAG, "onAnimationStart: "+e.getMessage() );
                            }
                        }
                    }*/

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (okToStartMainActivity && internetConnection){
                        startMainActivity();
                        finish();
                    }else{
                        if (internetConnection){
                            //todo:bypass login
                            startMainActivity();
                            //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        } else {

                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } catch (Exception e){
            startMainActivity();
            finish();
        }

    }

    public View onCreateView(@NotNull String name, @NotNull Context context, @NotNull AttributeSet attrs) {
        view = super.onCreateView(name, context, attrs);
        return view;
    }

    @Override
    protected void onPostResume() {
        try {
            imageView.startAnimation(splash);
        } catch (Exception e){
            startMainActivity();
            finish();
        }
        super.onPostResume();
    }

    private void login(final String emailText, final String passwordText, final Boolean manual){
        try {
            DBVolley db = new DBVolley(this);
            db.DBVolleyStandardLoginExecute(emailText,passwordText,status_checked_button_remember_me,new ServerCallback() {
                @Override
                public void onSuccess(String response) {
                    int lenResponse = response.length();
                    if (lenResponse>11) {
                        String sub_str = response.substring(0,11);
                        final int LenStr = response.length();
                        String sub_str1 = response.substring(11,LenStr);
                        if (sub_str.equals("session_id=")){
                            //se è avvenuto l'accesso creo una Session id
                            SecurityExtension.putSinglePref(getApplicationContext(),"{SSID}",sub_str1,"");
                        } else {
                            Toast.makeText(SplashActivity.this, getString(R.string.loginnok), Toast.LENGTH_SHORT).show();
                        }
                        if(manual){
                            Toast.makeText(SplashActivity.this, getString(R.string.loginExecuted), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, getString(R.string.autoLogin), Toast.LENGTH_SHORT).show();
                        }
                        SSID = getSinglePref(getApplicationContext(), "{SSID}", "");
                        if (!SSID.isEmpty()) {
                            if (manual){
                                if (status_checked_button_remember_me.equals("true")) {
                                    try{
                                        String encryptedMail = SecurityExtension.encrypt(emailText);
                                        SecurityExtension.putSinglePref(getApplicationContext(),"{email}",encryptedMail,"");
                                        String encryptedPassword =SecurityExtension.encrypt(passwordText);
                                        SecurityExtension.putSinglePref(getApplicationContext(),"{password}",encryptedPassword,"");

                                    } catch (Exception e) {
                                        if (BuildConfig.DEBUG){
                                            Log.e(TAG, " DBVolleyStandardLoginExecute onSuccess: "+e.getMessage() );
                                        }
                                    }
                                }else {
                                    Toast.makeText(SplashActivity.this, getString(R.string.loginnok) , Toast.LENGTH_SHORT).show();
                                }
                            }
                            okToStartMainActivity=true;
                        }
                    } else {
                        Toast.makeText(SplashActivity.this,getString(R.string.there_is_no_internet_connection), Toast.LENGTH_SHORT).show();
                        internetConnection=false;
                    }
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, " login: "+e.getMessage() );
            }
        }
    }
    private void startMainActivity(){
        try {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startMainActivity: " + e.getMessage());
                Log.e(TAG, "startMainActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
}
