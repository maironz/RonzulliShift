package com.massimo.ronzulli.turnironzulli;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.massimo.ronzulli.turnironzulli.DB.DBGest;
import com.massimo.ronzulli.turnironzulli.STR.stringExtension;

public class RecoverPasswordActivity extends AppCompatActivity {
    EditText text_email;
    Button buttonRecovery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        text_email=findViewById(R.id.text_email);
        buttonRecovery=findViewById(R.id.buttonRecovery);
        //region buttonRecovery.setOnClickListener
        buttonRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DBGest.isConnected(getApplicationContext())){
                    // se non siamo connessi ad internet l'app non funziona
                    stringExtension.dialog(view, getString(R.string.can_not_connect), getString(R.string.there_is_no_internet_connection));
                }else {
                }
            }
        });
        //endregion
    }
}
