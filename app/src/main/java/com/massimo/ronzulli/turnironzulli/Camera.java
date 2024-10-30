package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.loadParam;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.saveParam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.BuildConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;

/** Istruzioni
 * Preparazione
 *
 * 1) aggiungere al manifest
 *  <uses-permission android:name="android.permission.CAMERA"/>
 *  all'interno di
 *  <application>
 *         <provider
 *             android:name="androidx.core.content.FileProvider"
 *             android:authorities="com.massimo.ronzulli.camera"  <-- da modificare nel progetto attuale
 *             android:exported="false"
 *             android:grantUriPermissions="true"
 *             android:multiprocess="true">
 *             <meta-data
 *                 android:name="android.support.FILE_PROVIDER_PATHS"
 *                 android:resource="@xml/file_paths" />
 *         </provider>
 *  </application>
 *
 * 2) nella cartella res click di destra "New"->"Android Resource Directory"->"Resource Type"->"xml"->"OK"
 * 3) Copiare il file_paths.xml nella cartella appena creata
 * 4) Copiare le stringhe contenute in string.xml e it\string.xml
 * 5) Copiare la classe SecurityExtension
 * 6) inserire il riferimento alla Classe
 *
 *
 */
class Camera {
    private final static String TAG1 = "Camera";
    private final String TAG = "Camera";
    private final Context context;
    private final Activity activity;
    private final String negativeButtonString;
    private final String positiveButtonString;
    private final String privacyDescriptionForCamera;
    private final String permissionTitleCamera;
    public static String currentPhotoPath;// nomefile completo di percorso del file generato dall'acquisizione della fotocamera
    public static File photoFile;
    private static final String authorities="com.massimo.ronzulli.turnironzulli";
    private final int REQUEST_CAMERA=1110;
    public static final int REQUEST_IMAGE_CAPTURE=1125;
    public static final int REQUEST_BARCODE=1126;
    public static final int REQUEST_QRCODE=1127;

    public Camera(final Context context, final Activity activity) {
        this.context=context;
        this.activity=activity;
        permissionTitleCamera = context.getString(R.string.permissionTitleCamera);
        privacyDescriptionForCamera = getPrivacyDescription(context);
        negativeButtonString = context.getString(R.string.negativeButtonString);
        positiveButtonString = context.getString(R.string.positiveButtonString);
    }

    //region calling functions
    /** Photo request, return filename or ""
     * @param photoName preferred photo name
     * @return filename or ""
     * */
    public String tryToGetPhoto(String photoName) {
        if (checkCameraPermission()){
            try {
                return takePhoto(photoName, photoName);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            showREQUEST_CAMERA();
            return "";
        }
    }
    /** Scanning request */
    public void tryToScan(boolean barcode,boolean datamatrix) {
        if(checkCameraPermission()){
            try {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                if (barcode){
                    intent.putExtra("SCAN_MODE", "CODE_39,CODE_93,CODE_128,ITF,CODABAR,EAN_13,EAN_8,UPC_A");
                    activity.startActivityForResult(intent, REQUEST_BARCODE);
                } else if (datamatrix){
                    intent.putExtra("SCAN_MODE", "DATA_MATRIX");
                    activity.startActivityForResult(intent, REQUEST_QRCODE);
                } else {
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    activity.startActivityForResult(intent, REQUEST_QRCODE);
                }
            } catch (Exception e) {
                //rinvia a scaricare l'app
                try {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    activity.startActivity(marketIntent);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } else {
            showREQUEST_CAMERA();
        }
    }
    //endregion

    //region return functions in onActivityResult
    /** This function return the photo bitmap (onActivityResult)
     * @param context Context
     * @param compressed true/false compression enabled
     * @param ratio from 0.0 to 1.0 percentage of compression 0 = default = none
     * @return Bitmap or null
     * */
    public static Bitmap returnBitmapPhoto(Context context,boolean compressed,double ratio){
        double ratiodef;
        if (ratio==0) {ratiodef=0.8;} else { ratiodef=ratio;}
        Bitmap bitmap =null;
        try {
            //prelevo la rotazione di montaggio del sensore camera
            int cameraRear = getOrientationCamera(getCameraIdBack(context),context);
            //apre il file modificato dalla Camera
            File imgFile = new  File(currentPhotoPath);
            //importo l'immagine
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (compressed){
                try{
                    //la comprimo
                    bitmap= ShrinkJpg(bitmap,ratiodef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //se è -1 non esiste
            if(cameraRear!=-1){
                String imageStr;
                if(bitmap.getWidth()>bitmap.getHeight()){ imageStr="larga";}else{ imageStr="alta";}
                switch (getOrientationDisplay(context)){
                    case 0:
                        if(imageStr.equals("larga")){bitmap= rotate(bitmap, cameraRear);}
                        break;
                    case 1: //90
                        if(imageStr.equals("alta")){bitmap= rotate(bitmap, cameraRear);}
                        break;
                    case 2: //180
                        //solo alcuni dispositivi
                        break;
                    case 3: //270
                        if(imageStr.equals("larga")){
                            bitmap= rotate(bitmap, 180);
                        }else {
                            rotate(bitmap, -cameraRear);
                        }
                        break;
                }
            }
            //la sposto in uno stream per salvarla
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //associo l'immagine allo stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 82 , bos);
            //creo la sequenza di bytes da inserire nel file
            byte[] bitmapdata = bos.toByteArray();
            //preparo lo stream alla scrittura
            FileOutputStream fos = new FileOutputStream(imgFile);
            //scrivo e chiudo il file
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /** This function return the scanned string (onActivityResult)
     * @param data Intent data into onActivityResult
     * @return String or ""
     * */
    public static String returnScan(@Nullable Intent data){
        String readedCode;
        try {
            assert data != null;
            readedCode= data.getStringExtra("SCAN_RESULT");
        } catch (Exception e){
            readedCode ="";
        }
        return readedCode;
    }
    //endregion

    //region miscellaneous functions
    @NonNull
    public static String getPrivacyDescription(@NonNull Context context){
        String privacyDescriptionForCamera =
                context.getString(R.string.privacyDescriptionForCamera);
        return privacyDescriptionForCamera;
    }
    /** verifica l'accessibilità della fotocamera
     * @return true/false
     */
    public boolean checkCameraPermission(){
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /** richiesta di accesso al sensore */
    private void showREQUEST_CAMERA(){
        final int request=REQUEST_CAMERA;
        final String sensor = Manifest.permission.CAMERA;
        try {
            if (ContextCompat.checkSelfPermission(activity, sensor) != PackageManager.PERMISSION_GRANTED) {
                //se non ha l'accesso
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,sensor)) {
                    //questo if controlla se e' stata effettuata una riciesta precedente negata
                    //spiegazione del perche'
                    new AlertDialog.Builder(context).setTitle(permissionTitleCamera)
                            .setMessage(privacyDescriptionForCamera)
                            .setCancelable(false)
                            .setNegativeButton(negativeButtonString, (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(positiveButtonString, (dialog, which) -> {
                                ActivityCompat.requestPermissions(activity, new String[]{sensor}, request);
                                dialog.dismiss();
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{sensor},request);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG){
                Toast.makeText(context, "setREQUEST_CAMERA: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "setREQUEST_CAMERA: " + Arrays.toString(e.getStackTrace()), Toast.LENGTH_SHORT).show();
            } else {
                e.printStackTrace();
            }
        }
    }

    /**Con questa funzione è possibile determinare l'angolo iniziale di montaggio della camera  0-90-180-270 = 0°-90°-180°-270° */
    public static int getOrientationCamera(final int cameraId, final Context context) {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = cameraManager.getCameraIdList();
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            return 0;
        }
    }
    /**Con questa funzione è possibile determinare l'angolo di utilizzo del dispositivo 1=ORIENTATION_PORTRAIT 2=ORIENTATION_LANDSCAPE */
    public static int getOrientationDevice(final Activity activity) {
        try {
            return activity.getResources().getConfiguration().orientation;
        } catch (Exception e) {
            return 0;
        }
    }
    /**Con questa funzione è possibile determinare l'angolo di utilizzo del display 0-1-2-3 = 0°-90°-180°-270°*/
    public static int getOrientationDisplay(final Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            return display.getRotation();
        } catch (Exception e) {
            return -1;
        }
    }

    /**Preleva l'Id della camera posteriore*/
    public static int getCameraIdBack(final Context context) {
        int backCameraIdint = -1;
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId:cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing== CameraMetadata.LENS_FACING_BACK) {
                    backCameraIdint = Integer.parseInt(cameraId);
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return backCameraIdint;
    }
    /**Preleva l'Id della camera frontale*/
    public static int getCameraIdFront(final Context context) {
        int backCameraIdint = -1;
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId:cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing== CameraMetadata.LENS_FACING_FRONT) {
                    backCameraIdint = Integer.parseInt(cameraId);
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return backCameraIdint;
    }

    /**
     * @param param = key della SharedPreferences @see SecurityExtension.loadParam
     * @param photoName = nome della foto da archiviare (se ="" genera un nome casuale)
     * @return currentPhotoPath = nome della foto da prelevare
     */
    private String takePhoto(String param,String photoName) throws Exception {
        //verifica le permissions
        if(checkCameraPermission()){
            //creo l'intento
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //imposta la camera posteriore per la foto
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", getCameraIdBack(context));
            //eseguo l'intento e verifico che sia stato eseguito
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Creo il file
                photoFile = null;
                try {
                    photoFile = createImageFile(param, photoName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Se il file è stato creato
                if (photoFile != null) {
                    try {
                        Uri photoURI = FileProvider.getUriForFile(context, authorities,  photoFile);
                        //richiede nei dati extra l'indirizzo del file
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    throw new Exception("photoFile == null");
                }
            } else {
                throw new Exception("Non riesco ad avviare l'intento");
            }
        } else {
            Toast.makeText(context, "Non ho il permesso per accedere alla fotocamera", Toast.LENGTH_SHORT).show();
        }
        return currentPhotoPath;
    }

    /** Questa funzione crea e salva un file temporaneo nella cartella Picture presente in path.xml
     * cancella il vecchio file
     * inoltre imposta il percorso in currentPhotoPath in modo da poterlo successivamente recuperare
     *
     * @param storageKey = key della SharedPreferences @see SecurityExtension.loadParam
     *              param contiene il nome del file da cancellare
     */
    private File createImageFile(String storageKey, @NonNull String photoName) throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName;
        if(photoName.equals("")){
            imageFileName = "JPEG_".concat(timeStamp).concat("_");
        } else {
            imageFileName =  photoName;
        }
        File image=null;
        try {
            File storageDir;
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //il nome è random esempio JPEG_20211003_155550_5790095222903390549.jpg
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",   /* suffix */
                    storageDir      /* directory */
            );

            //verifico se è stato salvata un'immagine precedentemente
            //nome dell'immagine salvata
            //recupero il nome del file per vedere se è stato salvato precedentemente
            String imageStr = loadParam(context,storageKey);
            if (!imageStr.equals("")){
                try {
                    File imgFile = new File(imageStr);
                    if (!imgFile.delete()){
                        if (BuildConfig.DEBUG){
                            Toast.makeText(context, "Errore di cancellazione immagine in createImageFile", Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "imageStr:" + imageStr, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "storageDir:" + storageDir, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG){
                        Toast.makeText(context, "createImageFile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "createImageFile: " + Arrays.toString(e.getStackTrace()), Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                    }
                }

            }
            //prelevo il percorso assoluto
            imageStr=image.getAbsolutePath();
            //salvo il nuovo nome tra le variabili di ambiente storageKey in "general settings"
            saveParam(context,imageStr,storageKey);
            //imposto il percorso nella variabile globale
            currentPhotoPath = imageStr;
            //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(currentPhotoPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // restituisce il file immagine creato
        return image;
    }

    /** Comprime una bitmap*/
    private static Bitmap ShrinkJpg(@NonNull Bitmap bitmap, double ratio) {
        //questa routine restituisce una immagine compressa
        byte[] byteArray;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Comprime l'immagine
        int quality = 90;
        double bitmapWidth=bitmap.getWidth();
        double bitmapHeight=bitmap.getHeight();
        double bitmapDimBase;
        double ratioDef;
        bitmapDimBase = Math.min(bitmapWidth, bitmapHeight);
        //riduciamo in base alla richiesta ratio=0.8= 80% della normale riduzione
        //1000 è il valore finale dell'immagine da ottenere nel lato più grande
        ratioDef = Math.ceil(bitmapDimBase*ratio/1200);
        int bitmapNewWidth=(int)(bitmap.getWidth()/ratioDef);
        int bitmapNewHeight=(int)(bitmap.getHeight()/ratioDef);
        Bitmap bitmapNew = Bitmap.createScaledBitmap(bitmap,bitmapNewWidth,bitmapNewHeight,true);
        bitmapNew.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        /*Bitmap.CompressFormat
                Specifies the known formats a bitmap can be compressed into.
                    Bitmap.CompressFormat  JPEG
                    Bitmap.CompressFormat  PNG
                    Bitmap.CompressFormat  WEBP
        */
        bitmapNew.recycle();
        byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
    }
    /** Rotate a Bitmap */
    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    /** Rotate a Image control */
    public static void rotate(@NonNull Activity activity, @NonNull ImageView imageView, float degrees) {
        Bitmap bitmap=Camera.rotate(bitmapFromDrawable(imageView.getDrawable()),degrees);
        Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
        imageView.setImageDrawable(d);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = widthScreen(activity);
        params.height = scaleHeight( params.width ,bitmap.getWidth(),bitmap.getHeight());
    }
    //return the screen width in pixel
    public static int widthScreen(@NonNull Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    //resize image height to width // ridimensiona l'altezza dell'immagine in base alla larghezza
    public static int scaleHeight(int widthScreen, int imageWidth, int imageHeight){
        return (int) ((double) imageHeight * ((double)widthScreen/ (double) imageWidth) );
    }

    /** This function return the photo bitmap from full path name
     * @param fullNameWithPath full name of file including path
     * @param compressed true/false compression enabled
     * @param ratio from 0.0 to 1.0 percentage of compression 0 = default = none
     * @return Bitmap or null
     *   Bitmap bitmap= Camera.returnBitmapPhotoWhitPath(this,true,0);//0=0.8=80% di 1000px, no rotation
     *   Bitmap bitmap= Camera.returnBitmapPhotoWhitPath(this,false,1);//no compress, no ratio, no rotation
     * */
    public static Bitmap returnBitmapPhotoFromPath(String fullNameWithPath,boolean compressed,double ratio){
        double ratiodef;
        if (ratio==0) {ratiodef=0.8;} else { ratiodef=ratio;}
        Bitmap bitmap =null;
        try {
            //apre il file modificato dalla Camera
            File imgFile = new  File(fullNameWithPath);
            //importo l'immagine
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (compressed){
                try{
                    //la comprimo
                    bitmap= ShrinkJpg(bitmap,ratiodef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //la sposto in uno stream per salvarla
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //associo l'immagine allo stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 82 , bos);
            //creo la sequenza di bytes da inserire nel file
            byte[] bitmapdata = bos.toByteArray();
            //preparo lo stream alla scrittura
            FileOutputStream fos = new FileOutputStream(imgFile);
            //scrivo e chiudo il file
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap bitmapFromDrawable(Drawable drawable){
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        /*
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);*/
        return bitmap;
    }
    //endregion
    public static Bitmap generateBarcode(String text, int width, int height) {
        Bitmap bitmap= Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Code128Writer codeWriter = new Code128Writer();
            BitMatrix byteMatrix = codeWriter.encode(text, BarcodeFormat.CODE_128,width, height, hintMap);
            int bitWidth = byteMatrix.getWidth();
            int bitHeight = byteMatrix.getHeight();
            bitmap = Bitmap.createBitmap(bitWidth, bitHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitWidth; i++) {
                for (int j = 0; j < bitHeight; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    public static Bitmap generateBarcodeEAN13(String text, int width, int height) {
        Bitmap bitmap= Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            EAN13Writer codeWriter = new EAN13Writer();
            BitMatrix byteMatrix = codeWriter.encode(text, BarcodeFormat.EAN_13,width, height, hintMap);
            int bitWidth = byteMatrix.getWidth();
            int bitHeight = byteMatrix.getHeight();
            bitmap = Bitmap.createBitmap(bitWidth, bitHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitWidth; i++) {
                for (int j = 0; j < bitHeight; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    @SuppressWarnings("SuspiciousNameCombination")
    public static Bitmap generateQRCode(String text, int width) {
        Bitmap bitmap= Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            BitMatrix bitMatrix=multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,width,width);
            int bitHeight = bitMatrix.getHeight();
            int bitWidth = bitMatrix.getWidth();
            bitmap = Bitmap.createBitmap(bitWidth, bitHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < bitWidth; x++){
                for (int y = 0; y < bitHeight; y++){
                    bitmap.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //retrieve path from uri
    public static String getRealPathFromURIForGallery(final Context context,Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        try{
            if (uri == null) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor.moveToFirst()) {
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            cursor.close();
            return uri.getPath();

        } catch (Exception e) {
            Log.e("Camera", "getRealPathFromURIForGallery: error = ".concat(e.getMessage()));
            Log.e("Camera", "getRealPathFromURIForGallery: Stack = ".concat(Arrays.toString(e.getStackTrace())));
            return null;
        }
    }


}
