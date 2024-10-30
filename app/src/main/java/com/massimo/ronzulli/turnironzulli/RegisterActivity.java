package com.massimo.ronzulli.turnironzulli;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.massimo.ronzulli.turnironzulli.GR.Graphic;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.STR.stringExtension;

public class RegisterActivity extends AppCompatActivity {
    //region declaration objects
    EditText text_email,text_password;
    CheckBox remember_me_check,terms_of_use,general_cond,policy_privacy;
    Button buttonRegister,terms_of_use_link,general_cond_link,policy_privacy_link,text_password_eye_button;
    private static boolean error_status_login_email=false;
    private static boolean error_status_login_password=false;

    String status_checked_button_remember_me="false";
    private static final String TAG = RegisterActivity.class.getSimpleName();
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //region findViewById
        text_email=findViewById(R.id.text_email);
        text_password=findViewById(R.id.text_password);
        remember_me_check=findViewById(R.id.remember_me_check);
        terms_of_use=findViewById(R.id.terms_of_use);
        general_cond=findViewById(R.id.general_cond);
        policy_privacy=findViewById(R.id.policy_privacy);
        buttonRegister=findViewById(R.id.buttonRegister);
        terms_of_use_link=findViewById(R.id.terms_of_use_link);
        general_cond_link=findViewById(R.id.general_cond_link);
        policy_privacy_link=findViewById(R.id.policy_privacy_link);
        text_password_eye_button=findViewById(R.id.text_password_eye_button);
        //endregion
        //region aggiustamenti grafici
        //carattere sottolineato stile link
        Graphic.underscoreTextButton(terms_of_use_link);
        Graphic.underscoreTextButton(general_cond_link);
        Graphic.underscoreTextButton(policy_privacy_link);

        //endregion
        //region text_email.setOnFocusChangeListener convalida dell'e-mail
        text_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text=text_email.getText().toString();
                    String message;
                    message = stringExtension.checkEmail(getApplicationContext(),text);
                    if (!(message.equals(""))){
                        text_email.setError(message);
                    } else{
                        error_status_login_email=true;
                    }
                }
            }
        });
        //endregion
        //region text_password.setOnFocusChangeListener convalida della password
        text_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text=text_password.getText().toString();
                    String message;
                    message = stringExtension.checkPassword(getApplicationContext(),text);
                    if (!(message.equals(""))){
                        text_password.setError(message);
                    }else{
                        error_status_login_password=true;
                    }
                }
            }
        });
        //endregion
        //region buttonRegister.setOnClickListener
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean txtEmailPwdErrorCheck=false;
                String textPassword =text_password.getText().toString();
                if (textPassword.equals("")) {
                    text_password.requestFocus();
                    txtEmailPwdErrorCheck = true;
                }
                String textEmail=text_email.getText().toString();
                if (textEmail.equals("")) {
                    text_email.requestFocus();
                    txtEmailPwdErrorCheck = true;
                }
                if (!txtEmailPwdErrorCheck) {
                    if(remember_me_check.isChecked()){
                        status_checked_button_remember_me= "true";
                    }else{
                        status_checked_button_remember_me= "false";
                    }
                    if(terms_of_use.isChecked() && general_cond.isChecked() && policy_privacy.isChecked()){
                        SecurityExtension.register(view, getApplicationContext(), textEmail,textPassword,status_checked_button_remember_me);
                    }else{
                        //mostra il messaggio all'utente di registrazione impossibile
                        String msg = getString(R.string.registration_impossible).concat( getString(R.string.all_terms_necessary));
                        stringExtension.dialog(view,getString(R.string.registration_status), msg);
                        if (status_checked_button_remember_me.equals("true")){
                            try {
                                String encriptedMail = SecurityExtension.encrypt(textEmail);
                                SecurityExtension.putSinglePref(getApplicationContext(),"{email}",encriptedMail,"");
                                String encriptedPassword =SecurityExtension.encrypt(textPassword);
                                SecurityExtension.putSinglePref(getApplicationContext(),"{password}",encriptedPassword,"");
                            } catch (Exception e) {
                                e.printStackTrace();
                                stringExtension.dialog(view,getString(R.string.registration_status), getString(R.string.rememb_problem));
                            }
                        }
                        SecurityExtension.putSinglePref(getApplicationContext(),"{status_checked_button_remember_me}",status_checked_button_remember_me,"");
                        finish();
                    }
                }
            }
        });
        //endregion
        //region text_password_eye_button.setOnClickListener modifica l'immagine dell'occhio della password e mostra la password
        text_password_eye_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation fadeOut = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_out);
                Animation fadeIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_in);
                text_password_eye_button.startAnimation(fadeOut);
                if (text_password.getInputType()==(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)){
                    text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                    text_password_eye_button.setBackgroundResource(R.drawable.ic_no_eye);
                }else{
                    text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    text_password_eye_button.setBackgroundResource(R.drawable.ic_eye);
                }
                text_password_eye_button.startAnimation(fadeIn);
            }
        });
        //endregion

    }
}
