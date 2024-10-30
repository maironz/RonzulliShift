package com.massimo.ronzulli.turnironzulli;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.DB.DBGest;
import com.massimo.ronzulli.turnironzulli.DB.DBVolley;
import com.massimo.ronzulli.turnironzulli.DB.ServerCallback;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.STR.stringExtension;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;


public class LoginActivity extends AppCompatActivity {

    private View view;
    private EditText text_email,text_password;
    private Button buttonLogin,buttonRegister, buttonRecovery,text_password_eye_button;
    private CheckBox remember_me_check;
    private String SSID,str_result, encryptedMail;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context context;

    private String status_checked_button_remember_me="false";

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        //region dichiarazioni findViewById
        text_email = findViewById(R.id.text_email);
        text_password = findViewById(R.id.text_password);
        text_password_eye_button = findViewById(R.id.text_password_eye_button);
        buttonLogin = findViewById(R.id.buttonLogin);
        remember_me_check = findViewById(R.id.remember_me_check);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRecovery = findViewById(R.id.buttonRecovery);
        this.context= this;
        final View view = findViewById(R.id.loginActivity);
        stringExtension.setIconBitrittoBonus(context);
        //endregion
        //attiva le opzioni di sicurezza e carica le impostazioni
        SecurityExtension.securityActivate(context);

        //recuperiamo lo stato salvato del check remember me
        status_checked_button_remember_me = SecurityExtension.getStPref(getApplicationContext(),"{status_checked_button_remember_me}");

        try {
            //region buttonLogin.setOnClickListener
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        boolean txtEmailPwdErrorCheck=false;
                        String passwordText=text_password.getText().toString();
                        if (passwordText.equals("")) {
                            text_password.requestFocus();
                            txtEmailPwdErrorCheck = true;
                        }
                        String emailText=text_email.getText().toString();
                        if (emailText.equals("")) {
                            text_email.requestFocus();
                            txtEmailPwdErrorCheck = true;
                        }
                        if (!txtEmailPwdErrorCheck) {
                            if (remember_me_check.isChecked()) {
                                status_checked_button_remember_me = "true";
                            } else {
                                status_checked_button_remember_me = "false";
                            }
                            login(emailText,passwordText,true);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onClick: " + e.getMessage());
                            Log.e(TAG, "onClick: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region buttonRegister.setOnClickListener
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!DBGest.isConnected(getApplicationContext())) {
                            // se non siamo connessi ad internet l'app non funziona
                            stringExtension.dialog(view, getString(R.string.can_not_connect), getString(R.string.there_is_no_internet_connection));
                        } else {
                            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onClick: " + e.getMessage());
                            Log.e(TAG, "onClick: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region text_password_eye_button.setOnClickListener method
            //modifica l'immagine dell'occhio della password e mostra la password
            text_password_eye_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Animation fadeOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out);
                        Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                        text_password_eye_button.startAnimation(fadeOut);
                        if (text_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                            text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                            text_password_eye_button.setBackgroundResource(R.drawable.ic_no_eye);
                        } else {
                            text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            text_password_eye_button.setBackgroundResource(R.drawable.ic_eye);
                        }
                        text_password_eye_button.startAnimation(fadeIn);
                    } catch (Resources.NotFoundException e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onClick: " + e.getMessage());
                            Log.e(TAG, "onClick: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region text_email.setOnFocusChangeListener convalida della e-mail
            text_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
                            String text = text_email.getText().toString();
                            String message;
                            message = stringExtension.checkEmail(getApplicationContext(), text);
                            if (!(message.equals(""))) {
                                text_email.setError(message);
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onFocusChange: " + e.getMessage());
                            Log.e(TAG, "onFocusChange: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region text_password.setOnFocusChangeListener convalida della password
            text_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (!hasFocus) {
                            String text = text_password.getText().toString();
                            String message;
                            message = stringExtension.checkPassword(getApplicationContext(), text);
                            if (!(message.equals(""))) {
                                text_password.setError(message);
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onFocusChange: " + e.getMessage());
                            Log.e(TAG, "onFocusChange: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region remember_me_check.setOnCheckedChangeListener
            remember_me_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try {
                        if (remember_me_check.isChecked()) {
                            status_checked_button_remember_me = "true";
                        } else {
                            status_checked_button_remember_me = "false";
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onCheckedChanged: " + e.getMessage());
                            Log.e(TAG, "onCheckedChanged: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
            //region buttonRecovery.setOnClickListener
            buttonRecovery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(view.getContext(), RecoverPasswordActivity.class);
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "onClick: " + e.getMessage());
                            Log.e(TAG, "onClick: " + Arrays.toString(e.getStackTrace()));
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
            //endregion
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onCreate: " + e.getMessage());
                Log.e(TAG, "onCreate: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            //ritorno della prima richiesta di accesso al dispositivo
            final int REQUEST_WRITE_EXTERNAL_STORAGE=112;
            boolean retBool=false;
            switch (requestCode){
                case REQUEST_WRITE_EXTERNAL_STORAGE:
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
                Uri uri = Uri.parse("package:" .concat( this.getPackageName()));
                intent.setData(uri);
                //activity.startActivity(intent);
                startActivity(intent);
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onRequestPermissionsResult: " + e.getMessage());
                Log.e(TAG, "onRequestPermissionsResult: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NotNull String name, @NotNull Context context, @NotNull AttributeSet attrs) {
        view = super.onCreateView(name, context, attrs);
        return view;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //login automatico
        SecurityExtension.removeSinglePref(getApplicationContext(),"{SSID}","");
        if (remember_me_check.isChecked()) {
            status_checked_button_remember_me = "true";
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
                    Log.e(TAG, "onResume: " + e.getMessage());
                    Log.e(TAG, "onResume: " + Arrays.toString(e.getStackTrace()));
                } else {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            SecurityExtension.putSinglePref(getApplicationContext(),"{status_checked_button_remember_me}",status_checked_button_remember_me,"");
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onPause: " + e.getMessage());
                Log.e(TAG, "onPause: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
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
    private void login(final String emailText,final String passwordText, final Boolean manual){
        try {
            DBVolley db = new DBVolley(this);
            db.DBVolleyStandardLoginExecute(emailText,passwordText,status_checked_button_remember_me,new ServerCallback() {
                @Override
                public void onSuccess(String response) {
                    int lenResponse = response.length();
                    Log.d(TAG, "onSuccess: ".concat(response));
                    if (lenResponse>11) {
                        String sub_str = response.substring(0,11);
                        final int LenStr = response.length();
                        String sub_str1 = response.substring(11,LenStr);
                        if (sub_str.equals("session_id=")){
                            //se è avvenuto l'accesso salvo una Session id
                            SecurityExtension.putSinglePref(getApplicationContext(),"{SSID}",sub_str1,"");
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.loginnok), Toast.LENGTH_LONG).show();
                        }
                        if(manual){
                            Toast.makeText(LoginActivity.this, getString(R.string.loginExecuted), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.autoLogin), Toast.LENGTH_SHORT).show();
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
                                            Log.e(TAG, "onSuccess: " + e.getMessage());
                                            Log.e(TAG, "onSuccess: " + Arrays.toString(e.getStackTrace()));
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                }else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.loginnok) , Toast.LENGTH_SHORT).show();
                                }
                            }
                            startMainActivity();
                        }
                    } else {
                        stringExtension.dialog(context, getString(R.string.login_status), getString(R.string.there_is_no_internet_connection));
                    }
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "login: " + e.getMessage());
                Log.e(TAG, "login: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
}
