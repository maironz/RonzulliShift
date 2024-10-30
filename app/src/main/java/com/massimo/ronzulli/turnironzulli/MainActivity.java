package com.massimo.ronzulli.turnironzulli;


import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.loadSharedPreferencesFromFile;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.saveSharedPreferencesToFile;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.CrLf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.BuildConfig;
import com.google.android.material.navigation.NavigationView;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogFontSizeSpinner;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogYearSpinner;
import com.massimo.ronzulli.turnironzulli.STR.Permissions;
import com.massimo.ronzulli.turnironzulli.databinding.ActivityMainBinding;
import com.massimo.ronzulli.turnironzulli.models.Styles;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int[] TAB_TILES = new int[]{R.drawable.home,
            R.drawable.wheel, R.mipmap.acc2,
            R.drawable.accsell, R.drawable.not, R.drawable.shop};
    private static final int[] TAB_TILES1 = new int[]{R.drawable.home1,
            R.drawable.wheel1, R.drawable.acc1,
            R.drawable.accsell1, R.drawable.not1, R.drawable.shop1};
    private Context context;
    private SavesEnum savesEnum;
    private int year=0;
    private String privacyCamera;
    private final String backupFilenameSingleCellSettings="_rotation_backup.xml";
    private final String backupFilenameLeave="leave_backup.xml";
    private final String backupFilenameRotations="rotations_backup.xml";
    private final String backupFilenameStyles="styles_backup.xml";
    private SendReceive sendReceive;
    private String receivedMessage="";
    private String readQRCode="";

    //swipe variables
    private int mLastX;
    private int mLastY;
    private boolean mSwipingLeft,mSwipingRight,mSwipingTop,mSwipingBottom,swipeStart;
    private NavController navController;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public MainActivity() {
    }

    /** bugs report
     * you don't delete photo cards
     * */
    /** reminder list to update
    latest_update_string
     <string name="instruction_string">

     QR code
     This is the universal identifier of your application, it is used to synchronize (by sending data) the same on multiple devices.

     Synchronize
     This setting allows you to receive data from another synchronized application.

     Codice QR
     Questo è l\'identificativo universale della tua applicazione, serve per poter sincronizzare (inviando i dati) la stessa su più dispositivi.

     Sincronizza
     Questa impostazione permette di ricevere i dati da un\'altra applicazione sincronizzata.

     <string name="about_string">
     <string name="latest_update_string">
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privacyCamera=getString(R.string.privacyDescriptionForCamera);
        context=this;
        savesEnum=new SavesEnum();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbarNew);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_cards, R.id.nav_stats, R.id.nav_cell_style, R.id.nav_make_shift, R.id.nav_instructions,
                R.id.nav_backup, R.id.nav_restore, R.id.nav_reminder, R.id.nav_information, R.id.nav_credits, R.id.nav_auth, R.id.nav_privacy, R.id.nav_exit)
                .setOpenableLayout(drawer)
                .build();
        final int test = R.id.nav_cards;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId()!=R.id.nav_home) {
                int id=destination.getId();
                if (id==R.id.nav_cards){
                    startCards();
                } else if (id==R.id.nav_stats){
                    startStats();
                } else if (id==R.id.nav_cell_style){
                    startStylesActivity();
                } else if (id==R.id.nav_make_shift){
                    startMakeShiftActivity();
                } else if (id==R.id.nav_instructions){
                    String title   = getString(R.string.instructions);
                    String message = getString(R.string.instruction_string);
                    final DialogOK dialogOK=new DialogOK(context,title,message);
                    dialogOK.show();
                } else if (id==R.id.nav_backup){
                    backup();
                } else if (id==R.id.nav_restore){
                    restore();
                } else if (id==R.id.nav_reminder){
                    restore();
                } else if (id==123){
                    showDialogQRCode();
                } else if (id==456){
                    showDialogYesNoSync(getString(R.string.privacyInfo), privacyCamera);
                } else if (id== R.id.nav_information){
                    PackageManager manager = context.getPackageManager();
                    PackageInfo info = null;
                    try {
                        info = manager.getPackageInfo(context.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String version = null;
                    if (info != null) {
                        version = info.versionName;
                    }
                    String title1   = getString(R.string.about);
                    String message1 = getString(R.string.version)
                            .concat( " " )
                            .concat( version )
                            .concat( CrLf)
                            .concat( CrLf)
                            .concat(getString(R.string.latest_update_string))
                            .concat( CrLf)
                            .concat(getString(R.string.about_string));
                    DialogOK dialogOK1=new DialogOK(context,title1,message1);
                    dialogOK1.show();
                } else if (id==R.id.nav_credits){
                    String title2   = getString(R.string.credits);
                    String message2 = context.getString(R.string.credits_string);
                    DialogOK dialogOK2=new DialogOK(context,title2,message2);
                    dialogOK2.show();
                } else if (id==R.id.nav_auth){
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (id==R.id.nav_privacy){
                    String title3   = getString(R.string.privacyInfo);
                    //camera
                    String message3=
                            getString(R.string.permissionTitleCamera)
                                    .concat( "\n")
                                    .concat("\n")
                                    .concat(getString(R.string.privacyDescriptionForCamera))
                                    .concat("\n")
                                    .concat(getString(R.string.privacyInfoAndOpp));
                    final DialogOK dialogOK3=new DialogOK(context,title3,message3);
                    dialogOK3.show();
                } else if (id==R.id.nav_exit){
                    finish();
                }
                navController.navigate(R.id.nav_home);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String lang=Locale.getDefault().getLanguage();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (lang.equals("it")) menu.getItem(1).setVisible(true);
        return true;
    }
    public void onClickInformation(MenuItem item) {
        String title   = getString(R.string.instructions);
        String message = context.getString(R.string.main_text);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }
    public void onClickFestivity(MenuItem item) {
        Styles styles=new Styles(this);
        int id=-1;
        for (int i = 0; i < Styles.totalStyles; i++) {
            if (styles.style[i].ld.equals(getString(R.string.styles_ld_holiday))) id=i;
        }
        if (id>0){
            DialogYearSpinner dialogYearSpinner=new DialogYearSpinner(this);
            int finalId = id;
            dialogYearSpinner.setOnDismissListener(dialog -> {
                int year=dialogYearSpinner.year;
                //01/01/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(0)).concat("-").concat(String.valueOf(1)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //06/01/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(0)).concat("-").concat(String.valueOf(6)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //25/04/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(3)).concat("-").concat(String.valueOf(25)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //01/05/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(4)).concat("-").concat(String.valueOf(1)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //02/06/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(5)).concat("-").concat(String.valueOf(2)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //15/08/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(7)).concat("-").concat(String.valueOf(15)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //01/11/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(10)).concat("-").concat(String.valueOf(1)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //08/12/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(11)).concat("-").concat(String.valueOf(8)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //25/12/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(11)).concat("-").concat(String.valueOf(25)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                //26/12/20xx
                putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(11)).concat("-").concat(String.valueOf(26)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                if (year>2018 && year<2033) {
                    int day = 21, month = 3, day2, month2;
                    if (year == 2019) {day = 21;month = 3;}
                    if (year == 2020) {day = 12;month = 3;}
                    if (year == 2021) { day = 4; month = 3;}
                    if (year == 2022) { day = 17;month = 3;}
                    if (year == 2023) { day = 9;month = 3;}
                    if (year == 2025) { day = 20;month = 3;}
                    if (year == 2026) {day = 5;month = 3;}
                    if (year == 2027) {day = 28; month = 2;}
                    if (year == 2028) {day = 16; month = 3; }
                    if (year == 2029) {day = 1;month = 3;}
                    if (year == 2030) {day = 21;month = 3;}
                    if (year == 2031) { day = 13; month = 3;}
                    if (year == 2032) {day = 28;month = 2;}
                    day2=day+1;month2=month;
                    if (year == 2024) {day = 31; month = 2;day2=1;month2=3;}
                    putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(month)).concat("-").concat(String.valueOf(day)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                    putSinglePref(this,"{".concat(String.valueOf(year)).concat("-").concat(String.valueOf(month2)).concat("-").concat(String.valueOf(day2)).concat("}{style}"), String.valueOf(finalId),String.valueOf(year).concat(" single cell"));
                    navController.navigate(R.id.nav_home);
                }
            });
            dialogYearSpinner.show();
        } else {
            String title   = getString(R.string.alert);
            String message = getString(R.string.holiday_missing);
            final DialogOK dialogOK=new DialogOK(context,title,message);
            dialogOK.show();
        }
    }
    public void onClickCharDim(MenuItem item) {
        DialogFontSizeSpinner dialogFontSizeSpinner=new DialogFontSizeSpinner(this);
        dialogFontSizeSpinner.setOnDismissListener(dialog -> {
            int sizeFont=dialogFontSizeSpinner.size;
            if (sizeFont>0){
                putSinglePref(context, "Default size font", String.valueOf(sizeFont), savesEnum.appSettings.GENERAL_SETTINGS);
                navController.navigate(R.id.nav_home);
            }
        });
        dialogFontSizeSpinner.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milliseconds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();
    }
    protected void swipeScreenLeft(){
        if(!swipeStart){
            swipeStart=true;

            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            swipeStart=false;
        }
    }
    protected void swipeScreenRight(){
        //viewPager.setUserInputEnabled(false);
    }
    private void startCards(){
        try {
            Intent intent = new Intent(this, CardsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startCards: " + e.getMessage());
                Log.e(TAG, "startCards: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void startStats() {
        try {
            Intent intent = new Intent(context, StatsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startCards: " + e.getMessage());
                Log.e(TAG, "startCards: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void startStylesActivity(){
        try {
            Intent intent = new Intent(context, StylesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startStylesActivity: " + e.getMessage());
                Log.e(TAG, "startStylesActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void startMakeShiftActivity(){
        try {
            Intent intent = new Intent(context, MakeShiftActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_right_in, R.anim.swipe_left_out);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startMakeShiftActivity: " + e.getMessage());
                Log.e(TAG, "startMakeShiftActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void backup(){
        try {
            if(Permissions.setREQUEST_WRITE_EXTERNAL_STORAGE(context,this)){
                showDialogBackup() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void restore(){
        try {
            if(Permissions.setREQUEST_READ_EXTERNAL_STORAGE(context,this)){
                showDialogRestore() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //todo: da integrare
    //dialogo QRCode
    private void showDialogQRCode() {
        try {
            DialogQRCode dialogQRCode=new DialogQRCode(context);
            dialogQRCode.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //dialogo yes no sync
    private void showDialogYesNoSync(String title, String message){
        final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
        mBuilder.setTitle(title);
        mBuilder.setMessage(message);
        mBuilder.setPositiveButton(R.string.dialogYES, (dialog, which) -> startReceive());
        mBuilder.setNegativeButton(R.string.dialogNO, (dialog, which) -> dialog.dismiss());
        mBuilder.show();
    }
    //funzione di verifica della sincronizzazione dopo scansione del QRCode
    private void startReceive() {
        sendReceive=new SendReceive(context);
        sendReceive.setReceivedAppID(receivedMessage);
        sendReceive.startSyncReceive(readQRCode);
    }


    //dialogo con le opzioni di backup
    private void showDialogBackup() {
        try {
            final String[] listItems = {
                    getString(R.string.all_settings),
                    getString(R.string.shift_rotations),
                    getString(R.string.shift_rotations_with_leaves),
                    getString(R.string.all_appsettings),
                    getString(R.string.rotation_settings),
                    getString(R.string.leaves_settings),
                    getString(R.string.styles_settings)
            };//,getString(R.string.other)
            final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
            mBuilder.setTitle(getString(R.string.string_options));
            mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
                String prefString="",dialogString="";
                switch (i){
                    case 1:
                        //Solo rotazione dei turni
                        showDialogYears(false,true);
                        break;
                    case 2:
                        //Rotazione dei turni con ferie note e permessi
                        showDialogYears(true,true);
                        break;
                    case 0:
                        dialogString=saveAll();
                    case 3:
                        saveAllSettings(dialogString);
                        break;
                    case 4:
                        //Solo l\'elenco della rotazione \\rotations
                        prefString="rotations";
                        saveSharedPreferencesToFile(context,prefString,backupFilenameRotations,true);
                        dialogGeneric(getString(R.string.backup_file)
                                .concat( "=")
                                .concat( backupFilenameRotations )
                                .concat( "\n")
                                .concat(getString(R.string.folder) )
                                .concat("=" )
                                .concat( Environment.DIRECTORY_DOWNLOADS)
                        );
                        break;
                    case 5:
                        //Solo l\'elenco dei permessi \\leave
                        prefString="leave";
                        saveSharedPreferencesToFile(context,prefString,backupFilenameLeave,true);
                        dialogGeneric(getString(R.string.backup_file)
                                .concat("=")
                                .concat( backupFilenameLeave )
                                .concat( "\n")
                                .concat(getString(R.string.folder) )
                                .concat("=" )
                                .concat(Environment.DIRECTORY_DOWNLOADS)
                        );
                        break;
                    case 6:
                        //Solo l\'elenco degli stili dei turni \\styles
                        prefString="styles";
                        saveSharedPreferencesToFile(context,prefString,backupFilenameStyles,true);
                        dialogGeneric(getString(R.string.backup_file)
                                .concat( "=")
                                .concat( backupFilenameStyles )
                                .concat( "\n")
                                .concat(getString(R.string.folder) )
                                .concat("=" )
                                .concat(Environment.DIRECTORY_DOWNLOADS)
                        );
                        break;
                }
                dialogInterface.dismiss();
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //mostra la finestra di dialogo selezione anno
    private void showDialogYears(boolean withLeaves,boolean backup) {
        DialogYearSpinner dialogYearSpinner=new DialogYearSpinner(this);
        dialogYearSpinner.setOnDismissListener(dialog -> {
            year=dialogYearSpinner.year;
            saveRotation(withLeaves,backup);
        });
        dialogYearSpinner.show();
    }
    //salva le impostazioni dell'anno selezionato
    private void saveRotation(boolean withLeaves,boolean backup){
        try {
            if (year>0){
                if(backup){
                    if(saveSharedPreferencesToFile(context,String.valueOf(year)
                            .concat(" single cell"),String.valueOf(year)
                            .concat(backupFilenameSingleCellSettings),withLeaves)){
                        dialogGeneric(getString(R.string.backup_file)
                                .concat( "=" )
                                .concat( String.valueOf(year))
                                .concat(backupFilenameSingleCellSettings )
                                .concat( "\n")
                                .concat(getString(R.string.folder) )
                                .concat("=" )
                                .concat(Environment.DIRECTORY_DOWNLOADS));
                    } else {
                        dialogGeneric(getString(R.string.this_setting_is_empty));
                    }
                } else {
                    if(loadSharedPreferencesFromFile(context,String.valueOf(year)
                            .concat(" single cell"),String.valueOf(year)
                            .concat(backupFilenameSingleCellSettings))){
                        dialogGenericWhitRestart(getString(R.string.the_file)
                                .concat( " ")
                                .concat( String.valueOf(year))
                                .concat(backupFilenameSingleCellSettings)
                                .concat(" ")
                                .concat( getString(R.string.restore) )
                                .concat( "\n")
                                .concat( getString(R.string.folder) )
                                .concat("=" )
                                .concat( Environment.DIRECTORY_DOWNLOADS));
                    } else {
                        dialogGeneric(getString(R.string.this_setting_is_empty));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //finestra di dialogo generica con ok incorporato
    private void dialogGeneric(String message){
        final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
        mBuilder.setTitle(getString(R.string.privacyInfo));
        mBuilder.setMessage(message);
        mBuilder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    //finestra di dialogo generica con ok incorporato e riavvio applicazione
    private void dialogGenericWhitRestart(String message){
        final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
        mBuilder.setTitle(getString(R.string.folder));
        mBuilder.setMessage(message);
        mBuilder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                restart();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    //riavvia l'app
    private void restart(){
        try {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "restart: " + e.getMessage());
                Log.e(TAG, "restart: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    //salva impostazioni
    private void saveAllSettings(String dialogString){
        //Tutte le impostazioni
        String prefString="";
        prefString="rotations";
        saveSharedPreferencesToFile(context,prefString,backupFilenameRotations,true);
        dialogString =
            dialogString.concat(
                getString(R.string.backup_file)
                .concat( "=")
                .concat( backupFilenameRotations )
                .concat( "\n")
            );
        prefString="leave";
        saveSharedPreferencesToFile(context,prefString,backupFilenameLeave,true);
        dialogString =
            dialogString.concat(
                getString(R.string.backup_file)
                .concat( "=")
                .concat( backupFilenameLeave )
                .concat( "\n")
            );
        prefString="styles";
        saveSharedPreferencesToFile(context,prefString,backupFilenameStyles,true);
        dialogString =
            dialogString.concat(
                getString(R.string.backup_file)
                .concat( "=")
                .concat( backupFilenameStyles )
                .concat( "\n")
            );
        dialogString =
            dialogString.concat(getString(R.string.folder)
                .concat("=" )
                .concat(Environment.DIRECTORY_DOWNLOADS)
                .concat( File.separator)
                .concat( "shiftRonzulli")
            );
        dialogGeneric(dialogString);
    }
    //salva impostazioni e dati
    private String saveAll(){
        String dialogString="";
        try {
            for (int i = 1995; i < 2040; i++) {
                //faccio il backup di tutti gli anni
                if(saveSharedPreferencesToFile(context,String.valueOf(i)
                        .concat(" single cell")
                        ,String.valueOf(i)
                        .concat(backupFilenameSingleCellSettings)
                        ,true)){
                    dialogString =
                        dialogString.concat(
                            getString(R.string.backup_file)
                            .concat( "=" )
                            .concat( String.valueOf(i))
                            .concat(backupFilenameSingleCellSettings)
                            .concat("\n")
                        );
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return dialogString;
    }
    //dialogo con le opzioni di restore
    private void showDialogRestore(){
        try {
            final String[] listItems = {
                    getString(R.string.all_settings),
                    getString(R.string.shift_rotations),
                    getString(R.string.shift_rotations_with_leaves),
                    getString(R.string.all_appsettings),
                    getString(R.string.rotation_settings),
                    getString(R.string.leaves_settings),
                    getString(R.string.styles_settings)
            };//,getString(R.string.other)
            final AlertDialog.Builder mBuilder =new AlertDialog.Builder(this);
            mBuilder.setTitle(getString(R.string.string_options));
            mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
                String prefString="",dialogString="";
                switch (i){
                    case 1:
                        //Solo rotazione dei turni
                        showDialogYears(false,false);
                        break;
                    case 2:
                        //Rotazione dei turni con ferie note e permessi
                        showDialogYears(true,false);
                        break;
                    case 0:
                        dialogString=restoreAll();
                    case 3:
                        restoreAllSettings(dialogString);
                        break;
                    case 4:
                        //Solo l\'elenco della rotazione \\rotations
                        prefString="rotations";
                        loadSharedPreferencesFromFile(context,prefString,backupFilenameRotations);
                        dialogGenericWhitRestart(getString(R.string.restored_file) .concat("=").concat( backupFilenameRotations));
                        break;
                    case 5:
                        //Solo l\'elenco dei permessi \\leave
                        prefString="leave";
                        loadSharedPreferencesFromFile(context,prefString,backupFilenameLeave);
                        dialogGenericWhitRestart(getString(R.string.restored_file) .concat( "=").concat( backupFilenameLeave));

                        break;
                    case 6:
                        //Solo l\'elenco degli stili dei turni \\styles
                        prefString="styles";
                        loadSharedPreferencesFromFile(context,prefString,backupFilenameStyles);
                        dialogGenericWhitRestart(getString(R.string.restored_file) .concat("=").concat( backupFilenameStyles));

                        break;
                }
                dialogInterface.dismiss();
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "restart: " + e.getMessage());
                Log.e(TAG, "restart: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    //ripristina impostazioni e dati
    private String restoreAll(){
        String dialogString="";
        try {
            for (int i = 1995; i < 2040; i++) {
                //faccio il backup di tutti gli anni
                if(
                    loadSharedPreferencesFromFile(
                        context,String.valueOf(i)
                        .concat(" single cell")
                        ,String.valueOf(i)
                        .concat(backupFilenameSingleCellSettings)
                    )
                ){
                    dialogString =
                        dialogString.concat(getString(R.string.restored_file)
                                .concat("=")
                                .concat(String.valueOf(i))
                                .concat(backupFilenameSingleCellSettings  )
                                .concat( "\n")
                        );
                }
            }
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "restart: " + e.getMessage());
                Log.e(TAG, "restart: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return dialogString;
    }
    //ripristina impostazioni
    private void restoreAllSettings(String dialogString){
        String prefString="";
        try {
            //Tutte le impostazioni
            prefString="rotations";
            if(loadSharedPreferencesFromFile(context,prefString,backupFilenameRotations)){
                dialogString = dialogString.concat( getString(R.string.restored_file) .concat( "=").concat( backupFilenameRotations).concat( "\n"));
            } else {
                dialogString = dialogString.concat( getString(R.string.the_file) .concat( backupFilenameRotations ).concat(getString(R.string.is_missing)).concat( "\n"));
            }
            prefString="leave";
            if(loadSharedPreferencesFromFile(context,prefString,backupFilenameLeave)){
                dialogString  = dialogString.concat( getString(R.string.restored_file).concat(  "=").concat( backupFilenameLeave).concat( "\n"));
            } else {
                dialogString  = dialogString.concat( getString(R.string.the_file).concat( backupFilenameLeave ).concat(getString(R.string.is_missing) ).concat( "\n"));
            }
            prefString="styles";
            if(loadSharedPreferencesFromFile(context,prefString,backupFilenameStyles)){
                dialogString  = dialogString.concat( getString(R.string.restored_file) .concat( "=").concat( backupFilenameStyles).concat( "\n"));
            } else {
                dialogString  = dialogString.concat( getString(R.string.the_file) .concat(  backupFilenameStyles ).concat(getString(R.string.is_missing) ).concat( "\n"));
            }
            dialogString  = dialogString.concat(  getString(R.string.folder) .concat( "=" ).concat(Environment.DIRECTORY_DOWNLOADS).concat(File.separator ).concat( "shiftRonzulli"));
            dialogGenericWhitRestart(dialogString);
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "restart: " + e.getMessage());
                Log.e(TAG, "restart: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }

}