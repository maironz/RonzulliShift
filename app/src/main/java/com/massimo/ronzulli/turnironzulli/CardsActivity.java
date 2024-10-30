package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewbinding.BuildConfig;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CardsActivity extends AppCompatActivity {
    private Context context;
    private final String TAG=getClass().getName();
    private ArrayList<String> cardsList;
    private SavesEnum savesEnum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        context = getApplicationContext();

        RecyclerView recycleViewCard = findViewById(R.id.recycleViewCard);
        AutoCompleteTextView ac_search_ac = findViewById(R.id.ac_search_ac);
        cardsList=new ArrayList<>();
        DragList dragList = new DragList(cardsList, this);
        dragList.setRecyclerView(recycleViewCard);
        dragList.addFilterAutoCompleteTextView(ac_search_ac);
        cardsList= dragList.retrieveList();
        savesEnum=new SavesEnum();

        //region make toolbar back button
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        Toolbar toolbar = binding.appBarMain.toolbarNew;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //endregion
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToPreviousActivity();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cards, menu);
        return true;
    }
    private void onClickInformation(){
        String title   = getString(R.string.settings_list_cards);
        String message = getString(R.string.cards_string);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }
    private void addCard() {
        try {
            Intent intent = new Intent(context, ModifyCardsActivity.class);
            putSinglePref(context,"newCard", "true", "Cards");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "addCard: " + e.getMessage());
                Log.e(TAG, "addCard: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_left_in, R.anim.swipe_right_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "backToPreviousActivity: " + e.getMessage());
                Log.e(TAG, "backToPreviousActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }

    public void onClickInformation(MenuItem item) {
        onClickInformation();
    }

    public void addCard(MenuItem item) {
        addCard();
    }
}
