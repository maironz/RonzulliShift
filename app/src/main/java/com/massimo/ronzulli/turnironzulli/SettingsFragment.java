package com.massimo.ronzulli.turnironzulli;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogYearSpinner;
import com.massimo.ronzulli.turnironzulli.STR.Permissions;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;

import java.io.File;
import java.util.Arrays;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.loadSharedPreferencesFromFile;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.saveSharedPreferencesToFile;
import static com.massimo.ronzulli.turnironzulli.STR.stringExtension.CrLf;


public class SettingsFragment extends Fragment {
    private LinearLayout fragment_setting_ll_user_profile;
    private LinearLayout fragment_setting_ll_logout;

    private LinearLayout fragment_setting_ll_cards,
            fragment_setting_ll_cell_style, set_ll_make_shift,
            set_ll_instructions,set_ll_backup,set_ll_restore,
            set_ll_qr_code,set_ll_qr_sync,set_ll_credits,
            fragment_setting_ll_auth,set_ll_about,
            fragment_setting_ll_privacy,fragment_setting_ll_exit,fragment_setting_ll_stats;
    //region camera
    private String privacyCamera;
    public ActivityResultLauncher<Intent> someActivityResultLauncher;
    //endregion

    private Context context;
    private final String TAG=getClass().getName();
    private View view;
    private int year=0;
    private final String backupFilenameSingleCellSettings="_rotation_backup.xml";
    private final String backupFilenameLeave="leave_backup.xml";
    private final String backupFilenameRotations="rotations_backup.xml";
    private final String backupFilenameStyles="styles_backup.xml";

    private SendReceive sendReceive;
    private String receivedMessage="";
    private String readQRCode="";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            someActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String contents = data.getStringExtra("SCAN_RESULT");
                            //riceve i risutati di scansione
                            readQRCode = contents;
                            //mostra il messaggio di sincronizzazione
                            showDialogYesNoSync(getString(R.string.titleSync), getString(R.string.messageSync));
                            Toast.makeText(getContext(), contents, Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onCreate: " + e.getMessage());
                Log.e(TAG, "onCreate: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void backup(){
        try {
            if(Permissions.setREQUEST_WRITE_EXTERNAL_STORAGE(context,getActivity())){
                showDialogBackup() ;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    private void restore(){
        try {
            if(Permissions.setREQUEST_READ_EXTERNAL_STORAGE(context,getActivity())){
                showDialogRestore() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        try {
            privacyCamera=getString(R.string.privacyDescriptionForCamera);
            view =  inflater.inflate(R.layout.fragment_settings, container, false);
            this.context = view.getContext();
            //region menu userprofile
            fragment_setting_ll_user_profile = view.findViewById(R.id.fragment_setting_ll_user_profile);
            //endregion
            //region menu cards
            fragment_setting_ll_cards= view.findViewById(R.id.fragment_setting_ll_cards);
            fragment_setting_ll_cards.setOnClickListener(v -> startCards());
            //endregion
            //region menu cards
            fragment_setting_ll_stats= view.findViewById(R.id.fragment_setting_ll_stats);
            fragment_setting_ll_stats.setOnClickListener(view1 -> startStats());
            //endregion
            //region menu day style
            fragment_setting_ll_cell_style= view.findViewById(R.id.fragment_setting_ll_cell_style);
            fragment_setting_ll_cell_style.setOnClickListener(v -> startStylesActivity());
            //endregion
            //region menu make shifts
            set_ll_make_shift = view.findViewById(R.id.set_ll_make_shift);
            set_ll_make_shift.setOnClickListener(v -> startMakeShiftActivity());
            //endregion
            //region menu Instructions ?
            set_ll_instructions= view.findViewById(R.id.set_ll_instructions);
            set_ll_instructions.setOnClickListener(v -> {
                String title   = getString(R.string.instructions);
                String message = getString(R.string.instruction_string);
                final DialogOK dialogOK=new DialogOK(context,title,message);
                dialogOK.show();
            });
            //endregion
            //region menu backup
            set_ll_backup = view.findViewById(R.id.set_ll_backup);
            set_ll_backup.setOnClickListener(v -> backup());
            //endregion
            //region menu restore
            set_ll_restore = view.findViewById(R.id.set_ll_restore);
            set_ll_restore.setOnClickListener(v -> restore());
            //endregion
            //region menu qr_code
            set_ll_qr_code= view.findViewById(R.id.set_ll_qr_code);
            set_ll_qr_code.setOnClickListener(v -> showDialogQRCode());
            //endregion
            //region menu sync
            set_ll_qr_sync= view.findViewById(R.id.set_ll_qr_sync);
            set_ll_qr_sync.setOnClickListener(v -> {
                showDialogYesNoSync(getString(R.string.privacyInfo), privacyCamera);
            });
            //endregion
            //region menu About ?
            set_ll_about= view.findViewById(R.id.set_ll_about);
            set_ll_about.setOnClickListener(v -> {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(context.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = info.versionName;
                String title   = getString(R.string.about);
                String message = getString(R.string.version)
                        .concat( " ")
                        .concat( version)
                        .concat( CrLf)
                        .concat(CrLf)
                        .concat(getString(R.string.latest_update_string))
                        .concat(CrLf);
                //message += getString(R.string.about_string);
                DialogOK dialogOK=new DialogOK(context,title,message);
                dialogOK.show();
            });
            //endregion
            //region menu Credits
            set_ll_credits= view.findViewById(R.id.set_ll_credits);
            set_ll_credits.setOnClickListener(v -> {
                String title   = getString(R.string.credits);
                String message = context.getString(R.string.credits_string);
                DialogOK dialogOK=new DialogOK(context,title,message);
                dialogOK.show();
            });
            //endregion
            //region menu auth
            fragment_setting_ll_auth= view.findViewById(R.id.fragment_setting_ll_auth);
            fragment_setting_ll_auth.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            //endregion
            //region menu Privacy
            fragment_setting_ll_privacy= view.findViewById(R.id.fragment_setting_ll_privacy);
            fragment_setting_ll_privacy.setOnClickListener(v -> {
                String title   = getString(R.string.privacyInfo);
                //camera
                String message=getString(R.string.permissionTitleCamera).concat("\n" ).concat("\n" ).concat(getString(R.string.privacyDescriptionForCamera) ).concat("\n");
                message = message.concat(getString(R.string.privacyInfoAndOpp));
                final DialogOK dialogOK=new DialogOK(context,title,message);
                dialogOK.show();
            });
            //endregion
            //region exit
            fragment_setting_ll_exit = view.findViewById(R.id.fragment_setting_ll_exit);
            fragment_setting_ll_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requireActivity().finish();
                }
            });
            //endregion
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onCreateView: " + e.getMessage());
                Log.e(TAG, "onCreateView: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return view;
    }

    private void showDialogYesNoCamera(String string, String privacyCamera) {
    }

    private void startStats() {
        try {
            Intent intent = new Intent(context, StatsActivity.class);
            this.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startCards: " + e.getMessage());
                Log.e(TAG, "startCards: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //onResumeListener.onResumeFragment(true);
    }

    //funzione di verifica della sincronizzazione dopo scansione del QRCode
    private void startReceive() {
        sendReceive=new SendReceive(context);
        sendReceive.setReceivedAppID(receivedMessage);
        sendReceive.startSyncReceive(readQRCode);
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
    //dialogo QRCode
    private void showDialogQRCode() {
        try {
            DialogQRCode dialogQRCode=new DialogQRCode(context);
            dialogQRCode.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        dialogGeneric(getString(R.string.backup_file) .concat( "=" ).concat( backupFilenameRotations  ).concat("\n"
                        ).concat(getString(R.string.folder)  ).concat("="  ).concat(Environment.DIRECTORY_DOWNLOADS));
                        break;
                    case 5:
                        //Solo l\'elenco dei permessi \\leave
                        prefString="leave";
                        saveSharedPreferencesToFile(context,prefString,backupFilenameLeave,true);
                        dialogGeneric(getString(R.string.backup_file).concat( "=" ).concat(backupFilenameLeave ).concat( "\n"
                        ).concat(getString(R.string.folder)  ).concat("="  ).concat(Environment.DIRECTORY_DOWNLOADS));
                       break;
                    case 6:
                        //Solo l\'elenco degli stili dei turni \\styles
                        prefString="styles";
                        saveSharedPreferencesToFile(context,prefString,backupFilenameStyles,true);
                        dialogGeneric(getString(R.string.backup_file).concat( "=" ).concat( backupFilenameStyles  ).concat( "\n"
                        ).concat(getString(R.string.folder)  ).concat("="  ).concat(Environment.DIRECTORY_DOWNLOADS));
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
            final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
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
                        dialogGenericWhitRestart(getString(R.string.restored_file).concat( "=" ).concat( backupFilenameRotations));
                        break;
                    case 5:
                        //Solo l\'elenco dei permessi \\leave
                        prefString="leave";
                        loadSharedPreferencesFromFile(context,prefString,backupFilenameLeave);
                        dialogGenericWhitRestart(getString(R.string.restored_file).concat( "=" ).concat( backupFilenameLeave));

                        break;
                    case 6:
                        //Solo l\'elenco degli stili dei turni \\styles
                        prefString="styles";
                        loadSharedPreferencesFromFile(context,prefString,backupFilenameStyles);
                        dialogGenericWhitRestart(getString(R.string.restored_file).concat( "=" ).concat( backupFilenameStyles));

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
        DialogYearSpinner dialogYearSpinner=new DialogYearSpinner(this.getActivity());
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
                    if(saveSharedPreferencesToFile(
                            context,String.valueOf(year)
                                    .concat(" single cell")
                            ,String.valueOf(year)
                                    .concat(backupFilenameSingleCellSettings)
                            ,withLeaves)){
                        dialogGeneric(getString(R.string.backup_file)
                                .concat( "=" )
                                .concat( String.valueOf(year))
                                .concat(backupFilenameSingleCellSettings)
                                .concat( "\n")
                                .concat(getString(R.string.folder))
                                .concat("=")
                                .concat(Environment.DIRECTORY_DOWNLOADS));
                    } else {
                        dialogGeneric(getString(R.string.this_setting_is_empty));
                    }
                } else {
                    if(loadSharedPreferencesFromFile(
                            context,String.valueOf(year)
                                    .concat(" single cell")
                            ,String.valueOf(year)
                                .concat(backupFilenameSingleCellSettings)))
                    {
                        dialogGenericWhitRestart(getString(R.string.the_file)
                            .concat( " ")
                            .concat(String.valueOf(year))
                            .concat(backupFilenameSingleCellSettings)
                            .concat(" ")
                            .concat( getString(R.string.restore))
                            .concat("\n")
                            .concat(getString(R.string.folder))
                            .concat("=")
                            .concat(Environment.DIRECTORY_DOWNLOADS)
                        );
                    } else {
                        dialogGeneric(getString(R.string.this_setting_is_empty));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //salva impostazioni e dati
    private String saveAll(){
        String dialogString="";
        try {
            for (int i = 1995; i < 2040; i++) {
                //faccio il backup di tutti gli anni
                if(saveSharedPreferencesToFile(context,String.valueOf(i)
                        .concat(" single cell"),String.valueOf(i)
                        .concat(backupFilenameSingleCellSettings),true)){
                    dialogString = dialogString
                            .concat(getString(R.string.backup_file))
                            .concat( "=" )
                            .concat( String.valueOf(i))
                            .concat(backupFilenameSingleCellSettings  )
                            .concat( "\n");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return dialogString;
    }
    //salva impostazioni
    private void saveAllSettings(String dialogString){
        //Tutte le impostazioni
        String prefString="";
        prefString="rotations";
        saveSharedPreferencesToFile(context,prefString,backupFilenameRotations,true);
        dialogString = dialogString.concat(getString(R.string.backup_file) ).concat( "=").concat(backupFilenameRotations .concat( "\n"));
        prefString="leave";
        saveSharedPreferencesToFile(context,prefString,backupFilenameLeave,true);
        dialogString = dialogString.concat(getString(R.string.backup_file) ).concat(  "=").concat(  backupFilenameLeave ).concat(  "\n");
        prefString="styles";
        saveSharedPreferencesToFile(context,prefString,backupFilenameStyles,true);
        dialogString = dialogString.concat(getString(R.string.backup_file) ).concat(  "=").concat(  backupFilenameStyles ).concat(  "\n");
        dialogString = dialogString.concat(getString(R.string.folder) ).concat( "=" ).concat( Environment.DIRECTORY_DOWNLOADS).concat( File.separator ).concat( "shiftRonzulli");
        dialogGeneric(dialogString);
    }
    //ripristina impostazioni e dati
    private String restoreAll(){
        String dialogString="";
        try {
            for (int i = 1995; i < 2040; i++) {
                //faccio il backup di tutti gli anni
                if(loadSharedPreferencesFromFile(context,String.valueOf(i).concat( " single cell"),String.valueOf(i).concat( backupFilenameSingleCellSettings))){
                    dialogString = dialogString.concat( getString(R.string.restored_file) ).concat(  "=" ).concat(  String.valueOf(i)).concat( backupFilenameSingleCellSettings  ).concat(  "\n");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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
                dialogString = dialogString.concat(getString(R.string.restored_file)  ).concat( "=" ).concat( backupFilenameRotations ).concat( "\n");
            } else {
                dialogString = dialogString.concat(getString(R.string.the_file)  ).concat(backupFilenameRotations  ).concat(getString(R.string.is_missing)  ).concat( "\n");
            }
            prefString="leave";
            if(loadSharedPreferencesFromFile(context,prefString,backupFilenameLeave)){
                dialogString = dialogString.concat(getString(R.string.restored_file)  ).concat( "=" ).concat(backupFilenameLeave ).concat( "\n");
            } else {
                dialogString = dialogString.concat(getString(R.string.the_file)  ).concat(backupFilenameLeave  ).concat(getString(R.string.is_missing)  ).concat( "\n");
            }
            prefString="styles";
            if(loadSharedPreferencesFromFile(context,prefString,backupFilenameStyles)){
                dialogString = dialogString.concat(getString(R.string.restored_file)  ).concat("=" ).concat( backupFilenameStyles ).concat( "\n");
            } else {
                dialogString = dialogString.concat(getString(R.string.the_file)  ).concat( backupFilenameStyles  ).concat(getString(R.string.is_missing)  ).concat( "\n");
            }
            dialogString = dialogString.concat( getString(R.string.folder)  ).concat("="  ).concat(Environment.DIRECTORY_DOWNLOADS ).concat(File.separator  ).concat( "shiftRonzulli");
            dialogGenericWhitRestart(dialogString);
        } catch (Exception e){
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
    private void startStylesActivity(){
        try {
            Intent intent = new Intent(context, StylesActivity.class);
            this.startActivity(intent);
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
            this.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startMakeShiftActivity: " + e.getMessage());
                Log.e(TAG, "startMakeShiftActivity: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    private void startCards(){
        try {
            Intent intent = new Intent(context, CardsActivity.class);
            this.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "startCards: " + e.getMessage());
                Log.e(TAG, "startCards: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    //riavvia l'app
    private void restart(){
        try {
            Intent intent = new Intent(context, MainActivity.class);
            this.startActivity(intent);
            this.getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "restart: " + e.getMessage());
                Log.e(TAG, "restart: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
}