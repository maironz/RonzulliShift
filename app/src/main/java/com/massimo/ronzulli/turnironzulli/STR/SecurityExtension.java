package com.massimo.ronzulli.turnironzulli.STR;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.DB.DBRegister;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class SecurityExtension {
    // richiede implementation 'org.jetbrains:annotations-java5:22.0.0'
    private static final String TAG = "SecurityExtension";
    private static String mSharedPreferences = "general_settings";

    private static final String ALGORITHM = "AES";
    private static final String KEY = "60fd0cd4380d4137ad155c654c92d304";//32bit

    //region inizializzazione applicazione e salvataggio dati in locale
    //attiva la registrazione sicura dei dati e importa le preferenze
    public static void securityActivate(Context context) {
        String first_access = getStPref(context,"{first_access}");
        //try non necessaria perché di default il getstring è impostato per restituire ""
        if (first_access.equals(""))
            putStPref(context,"{first_access}", "1");
    }
    //scrive una preferenza singola
    public static boolean putSinglePref(Context context, String key, String value, @NotNull String set) {
        //example
        //boolean result = putSinglePref(getApplicationContext(),"color_base","green","colors_settings");
        // if set = "" then set = mSharedPreferences
        // ad esempio è possibile impostare un set di preferenze
        // impostando set = "userPreferences"
        boolean return_boolean;
        try{
            SharedPreferences pref;
            if (set.equals("")){
                pref = context.getSharedPreferences(mSharedPreferences, MODE_PRIVATE);
            }else{
                pref = context.getSharedPreferences(set, MODE_PRIVATE);
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key,value);
            editor.apply();
            return_boolean=true;
        } catch (Exception e){
            return_boolean=false;
        }
        return return_boolean;
    }
    //cancella una preferenza singola
    public static boolean removeSinglePref(Context context, String key, @NotNull String set) {
        //example
        //Boolean result = removeSinglePref(getApplicationContext(),"color_base","green","colors_settings");
        // if set = "" then set = mSharedPreferences
        // ad esempio è possibile impostare un set di preferenze
        // impostando set = "userPreferences"
        boolean return_boolean;
        try{
            SharedPreferences pref=null;
            if (set.equals("")){
                pref = context.getSharedPreferences(mSharedPreferences, MODE_PRIVATE);
            }else{
                pref = context.getSharedPreferences(set, MODE_PRIVATE);
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.remove(key);
            editor.apply();
            return_boolean=true;
        } catch (Exception e){
            return_boolean=false;
        }
        return return_boolean;
    }
    //scrive tutte le preferenze
    public static void putStPref(Context context, String key, String value) {
        putSinglePref(context, key, value, mSharedPreferences);
    }
    //preleva la singola preferenza
    public static String getSinglePref(final Context context, String key, @NotNull String set) {
        //get the preferences on app
        //example
        // String string = getSinglePref(getApplicationContext(),"color_base","colors_settings");
        // if set = "" then set = mSharedPreferences
        String return_string;
        try{
            SharedPreferences pref;
            if (set.equals("")){
                pref = context.getSharedPreferences(mSharedPreferences, MODE_PRIVATE);
            }else{
                pref = context.getSharedPreferences(set, MODE_PRIVATE);
            }
            return_string=pref.getString(key, "");
        } catch (Exception e){
            return_string="";
        }
        return return_string;
    }
    //preleva tutte le preferenze
    public static String getStPref(Context context, String key) {
        //get the standard preferences on app
        //example
        // String string = getSinglePref(getApplicationContext(),"color_base");
        String return_string;
        try{
            return_string = getSinglePref(context,key,mSharedPreferences);
        } catch (Exception e){
            return_string="";
        }
        return return_string;
    }
    //restituisce una Map con l'esclusione degi elementi contenenti il testo indicato
    public static Map<String,?> excludeElementsByContainingString(Map<String,?> map, String textToFind){
        try {
            for(Iterator<? extends Map.Entry<String, ?>> it = map.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, ?> entry = it.next();
                if(entry.getKey().indexOf(textToFind)>0) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    //salva il file di shared preferences in download/shiftRonzulli
    public static boolean saveSharedPreferencesToFile(Context context, String prefName,String fileDest,boolean withLeaves) {
        boolean res = false;
            SharedPreferences pref =  context.getSharedPreferences(prefName, MODE_PRIVATE);
            Map<String,?> map;
            if (withLeaves){
                map = pref.getAll();
            } else {
                map = excludeElementsByContainingString(pref.getAll(),"note");
                map = excludeElementsByContainingString(pref.getAll(),"straord");
                map = excludeElementsByContainingString(map,"perm");
            }
            if(map.size()>0){
                ObjectOutputStream output = null;
                try {
                    String folder = "shiftRonzulli";
                    //File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+File.separator + folder);
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS.concat(File.separator).concat(folder));
                    //File dir = new File("//sdcard//Download//");
                    boolean success = true;
                    if (!dir.exists()) {
                        success = dir.mkdirs();
                    }
                    if (success) {
                        File file = new File(dir, fileDest);
                        output = new ObjectOutputStream(new FileOutputStream(file));
                        output.writeObject(map);
                        res = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null) {
                            output.flush();
                            output.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                res = false;
            }
        return res;
    }
    //importa le shared preferences dal file in download/shiftRonzulli
    @SuppressWarnings({ "unchecked" })
    public static boolean loadSharedPreferencesFromFile(Context context, String prefName,String fileOrigin) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS.concat(File.separator).concat("shiftRonzulli"));
            //File dir = new File("//sdcard//Download//");
            File file = new File(dir, fileOrigin);
            if(file.exists()) {
                input = new ObjectInputStream(new FileInputStream(file));
                SharedPreferences.Editor prefEdit = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
                //prefEdit.clear();
                Map<String, ?> entries = (Map<String, ?>) input.readObject();
                for (Map.Entry<String, ?> entry : entries.entrySet()) {
                    Object v = entry.getValue();
                    String key = entry.getKey();

                    if (v instanceof Boolean)
                        prefEdit.putBoolean(key, (Boolean) v);
                    else if (v instanceof Float)
                        prefEdit.putFloat(key, (Float) v);
                    else if (v instanceof Integer)
                        prefEdit.putInt(key, (Integer) v);
                    else if (v instanceof Long)
                        prefEdit.putLong(key, (Long) v);
                    else if (v instanceof String)
                        prefEdit.putString(key, ((String) v));
                }
                prefEdit.apply();
                res = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }
    //endregion
    //region funzioni di crypting

    //funzione di crypting dei files da verificare
    @NotNull
    private static Boolean encryptFile(String key, String filename) throws Exception {
        try {
            //key = "dfgfdgdfgfdlwerknwkfjewh";
            File inputFile = new File("test.txt");
            File encryptedFile = new File("test.encrypted");
            File decryptedFile = new File("test-decrypted.txt");
            execCryptDecrypt(Cipher.ENCRYPT_MODE, key, inputFile, encryptedFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //funzione di decrypting dei files da verificare
    @NotNull
    private static Boolean decryptFile(String key, String filename) throws Exception {
        try {
            //key = "dfgfdgdfgfdlwerknwkfjewh";
            File inputFile = new File("test.txt");
            File encryptedFile = new File("test.encrypted");
            File decryptedFile = new File("test-decrypted.txt");
            execCryptDecrypt(Cipher.ENCRYPT_MODE, key, inputFile, encryptedFile);
            execCryptDecrypt(Cipher.DECRYPT_MODE, key, encryptedFile, decryptedFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //funzione di encrypting/decrypting dei files
    private static void execCryptDecrypt (int cipherMode, @NotNull String key, @NotNull File inputFile, File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMode, secretKey);
        byte[] inputBytes;
        try (FileInputStream inputStream = new FileInputStream(inputFile)) {
            inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
        }
        byte[] outputBytes = cipher.doFinal(inputBytes);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(outputBytes);
        }
    }
    //funzione di crypting dei dati
    public static String encrypt(@NotNull String value) throws Exception {
        // crea la chiave di cripting
        Key key = generateKey();
        // imposta l'algoritmo di cripting
        Cipher cipher = Cipher.getInstance(SecurityExtension.ALGORITHM);
        // inizializza la modalità di cripting
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // trasferisce la stringa da criptare in un array di char criptati
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        //restituisce la stringa ricodificata in base64
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
    }
    public static String encrypt(@NotNull String value,String secret) throws Exception {
        // crea la chiave di cripting
        Key key = generateKey(secret);
        // imposta l'algoritmo di cripting
        Cipher cipher = Cipher.getInstance(SecurityExtension.ALGORITHM);
        // inizializza la modalità di cripting
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // trasferisce la stringa da criptare in un array di char criptati
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        //restituisce la stringa ricodificata in base64
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
    }
    //funzione di decrypting dei dati
    public static String decrypt(String value)  {
        String retString = "";
        try{
            // crea la chiave di cripting
            Key key = generateKey();
            // imposta l'algoritmo di cripting
            Cipher cipher = Cipher.getInstance(SecurityExtension.ALGORITHM);
            // inizializza la modalità di decripting
            cipher.init(Cipher.DECRYPT_MODE, key);
            //decodifica la stringa in base 64 e la imposta come array di byte
            byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
            // decripta l'array
            byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
            //restituisce la stringa ricodificata in base64
            retString =  new String(decryptedByteValue,StandardCharsets.UTF_8);

        } catch (Exception e){
        }
        return retString;
    }
    public static String decrypt(String value,String secret)  {
        String retString = "";
        try{
            // crea la chiave di cripting
            Key key = generateKey(secret);
            // imposta l'algoritmo di cripting
            Cipher cipher = Cipher.getInstance(SecurityExtension.ALGORITHM);
            // inizializza la modalità di decripting
            cipher.init(Cipher.DECRYPT_MODE, key);
            //decodifica la stringa in base 64 e la imposta come array di byte
            byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
            // decripta l'array
            byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
            //restituisce la stringa ricodificata in base64
            retString =  new String(decryptedByteValue,StandardCharsets.UTF_8);

        } catch (Exception e){
        }
        return retString;
    }
    //chiave di crypting
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY.getBytes(),SecurityExtension.ALGORITHM);
        return key;
    }
    private static Key generateKey(String secret) throws Exception {
        Key key = new SecretKeySpec(secret.getBytes(),SecurityExtension.ALGORITHM);
        return key;
    }
    public static boolean saveParam(Context context, String text, String param) throws Exception{
        try{
            String encName = SecurityExtension.encrypt(text);
            SecurityExtension.putStPref(context,param,encName);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    public static String loadParam(Context context, String param) throws Exception{
        try {
            String text="";
            String encCity = SecurityExtension.getStPref(context,param);
            if (!encCity.isEmpty()){
                text = SecurityExtension.decrypt(encCity);
            }
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    //endregion
    //region funzioni di accesso utente

    //registrazione nuovo utente
    @NotNull
    public static boolean register(View view, Context context, String email, String pass, String rememberMe){
        try {
            DBRegister bg = new DBRegister(context,view);
            bg.execute(email,pass,rememberMe);

        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "register: " + e.getMessage());
                Log.e(TAG, "register: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    //endregion


}