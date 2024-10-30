package com.massimo.ronzulli.turnironzulli;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

class BackupManager extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String appSettings = "appSettings";
    static final String general_settings = "general_settings";
    static final String leave = "leave";
    static final String rotations = "rotations";
    static final String styles = "styles";
    SharedPreferencesBackupHelper helper1;
    SharedPreferencesBackupHelper helper2;
    SharedPreferencesBackupHelper helper3;
    SharedPreferencesBackupHelper helper4;
    SharedPreferencesBackupHelper helper5;


    // A key to uniquely identify the set of backup data
    static final String first_backup = "first_backup";
    private Object BackupDataInputStream;

    @Override
    public void onCreate() {
        helper1 = new SharedPreferencesBackupHelper(this, appSettings);
        helper2 = new SharedPreferencesBackupHelper(this, appSettings);
        helper3 = new SharedPreferencesBackupHelper(this, appSettings);
        helper4 = new SharedPreferencesBackupHelper(this, appSettings);
        helper5 = new SharedPreferencesBackupHelper(this, appSettings);
        addHelper(first_backup, helper1);
        addHelper(first_backup, helper2);
        addHelper(first_backup, helper3);
        addHelper(first_backup, helper4);
        addHelper(first_backup, helper5);
    }

}
