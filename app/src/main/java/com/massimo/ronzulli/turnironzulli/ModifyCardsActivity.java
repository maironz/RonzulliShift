package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.Camera.REQUEST_BARCODE;
import static com.massimo.ronzulli.turnironzulli.Camera.REQUEST_IMAGE_CAPTURE;
import static com.massimo.ronzulli.turnironzulli.Camera.REQUEST_QRCODE;
import static com.massimo.ronzulli.turnironzulli.Camera.generateBarcode;
import static com.massimo.ronzulli.turnironzulli.Camera.generateBarcodeEAN13;
import static com.massimo.ronzulli.turnironzulli.Camera.generateQRCode;
import static com.massimo.ronzulli.turnironzulli.Camera.returnScan;
import static com.massimo.ronzulli.turnironzulli.Camera.widthScreen;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogOK;
import com.massimo.ronzulli.turnironzulli.Dialogs.DialogYesNo;
import com.massimo.ronzulli.turnironzulli.STR.SecurityExtension;
import com.massimo.ronzulli.turnironzulli.databinding.ActivityModifyCardsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ModifyCardsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_FRONT = 3645;
    private static final int PICK_IMAGE_REAR = 4567;
    private Context context;
    private View view;
    private MenuItem action_save;
    private Menu menu;
    private final String TAG=getClass().getName();
    private ArrayList<String> cardsList;
    private int sidePhoto;
    private String photoNameFront="",photoNameRear="",photoNameReturn="",etCardDesc="";
    private float rotationFront=0,rotationRear=0;
    private boolean newCard=false;
    private int positionCard=-1;//-2=new card or numberCard (0-1000)
    private boolean newElementSaved=false;
    private Bitmap bitmapFront,bitmapRear;
    private DragList dragList;
    private String readQRCode="",readBarcode="";
    private boolean onActivityResult=false;
    private boolean editState=false;//return the state o modify after resume
    private Animation startActivityAnimationForDialogBarcode;
    private ActivityModifyCardsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_modify_cards);
        view = findViewById(R.id.ModifyCards);*/
        binding=ActivityModifyCardsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);

        context = getApplicationContext();
        SecurityExtension.securityActivate(context);

        binding.lblBarcode1.setVisibility(View.GONE);
        binding.lblFront1.setVisibility(View.GONE);
        binding.lblRear1.setVisibility(View.GONE);
        binding.btDelQRCode.setVisibility(View.GONE);
        binding.photoFrontDelete.setVisibility(View.GONE);
        binding.photoRearDelete.setVisibility(View.GONE);

        bitmapFront=null;

        binding.progressbar.setVisibility(View.VISIBLE);

        binding.photoSelectFront.setOnClickListener(v -> {
            if (binding.etCardDesc.getText().toString().length()==0){
                String title=getString(R.string.missing_description);
                String message=getString(R.string.desc_start_nok);
                DialogOK dialogOK=new DialogOK(this, title, message);
                dialogOK.show();
            } else {
                selectPhoto(1);
            }
        });
        binding.photoSelectRear.setOnClickListener(v -> {
            if (binding.etCardDesc.getText().toString().length()==0){
                String title=getString(R.string.missing_description);
                String message=getString(R.string.desc_start_nok);
                DialogOK dialogOK=new DialogOK(this, title, message);
                dialogOK.show();
            } else {
                selectPhoto(2);
            }
        });
        binding.photoRotateFrontLeft.setOnClickListener(v -> {
            if (photoNameFront.length()>0)
                rotate(true, true);
        });
        binding.photoRotateFrontRight.setOnClickListener(v -> {
            if (photoNameFront.length()>0)
                rotate(false, true);

        });
        binding.photoRotateRearLeft.setOnClickListener(v -> {
            if (photoNameRear.length()>0)
                rotate(true, false);
        });
        binding.photoRotateRearRight.setOnClickListener(v -> {
            if (photoNameRear.length()>0)
                rotate(false, false);
        });
        binding.imageFront.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(photoNameFront), "image/*");
            startActivity(intent);
        });
        binding.imageRear.setOnClickListener(v -> {
            if (binding.etCardDesc.getText().toString().length()==0){
                String title=getString(R.string.missing_description);
                String message=getString(R.string.desc_start_nok);
                DialogOK dialogOK=new DialogOK(this, title, message);
                dialogOK.show();
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(photoNameRear), "image/*");
                startActivity(intent);
            }
        });
        binding.btBarcode.setOnClickListener(v -> getBarcode());
        binding.btQRCode.setOnClickListener(v -> getQRCode());
        /*
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == PICK_IMAGE_FRONT) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri selectedImageUri = null;
                        if (data != null) {
                            selectedImageUri = data.getData();
                            photoNameFront = Camera.getRealPathFromURIForGallery(this,selectedImageUri);
                            binding.imageFront.setImageBitmap(bitmapFront);
                        }
                    }
                });*/
        binding.photoGalleryFront.setOnClickListener(v -> {
            if (binding.etCardDesc.getText().toString().length()==0){
                String title=getString(R.string.missing_description);
                String message=getString(R.string.desc_start_nok);
                DialogOK dialogOK=new DialogOK(this, title, message);
                dialogOK.show();

            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                /*
                setResult(PICK_IMAGE_FRONT, intent);
                someActivityResultLauncher.launch(intent);*/
                Activity activity = ModifyCardsActivity.this;
                activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FRONT);
            }
        });
        binding.photoGalleryRear.setOnClickListener(v -> {
            if (binding.etCardDesc.getText().toString().length()==0){
                String title=getString(R.string.missing_description);
                String message=getString(R.string.desc_start_nok);
                DialogOK dialogOK=new DialogOK(this, title, message);
                dialogOK.show();
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                Activity activity=ModifyCardsActivity.this;
                activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REAR);
            }
        });

        cardsList= new ArrayList<>();
        dragList= new DragList(cardsList,this);
        cardsList=dragList.retrieveList();

        //region make toolbar back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //endregion

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enableEdit();
        onActivityResult=true;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                if (sidePhoto==1){
                    bitmapFront = Camera.returnBitmapPhoto(this, true, 0);
                    //binding.imageFront.setImageBitmap(bitmapFront);
                    if(bitmapFront!=null) photoNameFront=photoNameReturn;

                } else if (sidePhoto==2){
                    bitmapRear= Camera.returnBitmapPhoto(this,true,0);
                    //binding.imageRear.setImageBitmap(bitmapRear);
                    if(bitmapFront!=null) photoNameRear=photoNameReturn;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                    Log.e(TAG, "onActivityResult: " + Arrays.toString(e.getStackTrace()));
                } else {
                    e.getMessage();
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == PICK_IMAGE_FRONT && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            photoNameFront = Camera.getRealPathFromURIForGallery(this,selectedImageUri);
            binding.imageFront.setImageBitmap(bitmapFront);
        }

        if (requestCode == PICK_IMAGE_REAR && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            photoNameRear = Camera.getRealPathFromURIForGallery(this,selectedImageUri);
            binding.imageRear.setImageBitmap(bitmapRear);
        }
        if (requestCode == REQUEST_QRCODE && resultCode == RESULT_OK) {
            readQRCode=returnScan(data);
            readBarcode="";
            binding.iQRCode.setImageBitmap(generateQRCode(readQRCode,widthScreen(this)));
        }
        if (requestCode == REQUEST_BARCODE && resultCode == RESULT_OK) {
            readBarcode=returnScan(data);
            readQRCode="";
            binding.tvScan.setText(readBarcode);
            binding.tvScan.selectAll();
            binding.iQRCode.setImageBitmap(generateBarcode(readBarcode,widthScreen(this),200));
        }
        //Log.e(TAG, "onActivityResult: photoNameFront="+photoNameFront);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("newElement",newElementSaved);
        outState.putBoolean("editState",editState);
        outState.putInt("positionCard",positionCard);
        etCardDesc=binding.etCardDesc.getText().toString();
        outState.putString("et_card_desc",etCardDesc);
        outState.putString("photoNameFront",photoNameFront);
        outState.putString("photoNameRear",photoNameRear);
        outState.putString("photoNameReturn",photoNameReturn);
        outState.putString("rotationFront", String.valueOf(rotationFront));
        outState.putString("rotationRear", String.valueOf(rotationRear));
        outState.putString("readQRCode", readQRCode);
        outState.putString("readBarcode", readBarcode);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        newElementSaved=savedInstanceState.getBoolean("newElement");
        editState=savedInstanceState.getBoolean("editState");
        positionCard=savedInstanceState.getInt("positionCard");
        etCardDesc=savedInstanceState.getString("et_card_desc");
        binding.etCardDesc.setText(etCardDesc);
        if (photoNameFront.length()==0) {
            photoNameFront = savedInstanceState.getString("photoNameFront");
        }
        if (photoNameRear.length()==0) {
            photoNameRear = savedInstanceState.getString("photoNameRear");
        }
        if (photoNameReturn.length()==0) {
            photoNameReturn = savedInstanceState.getString("photoNameReturn");
        }
        rotationFront = Float.parseFloat(savedInstanceState.getString("rotationFront"));
        rotationRear = Float.parseFloat(savedInstanceState.getString("rotationRear"));
        readQRCode=savedInstanceState.getString("readQRCode");
        readBarcode=savedInstanceState.getString("readBarcode");
    }
    @Override
    protected void onResume() {
        super.onResume();
        binding.progressbar.setVisibility(View.VISIBLE);
        //newCard is written by calling function
        newCard= Boolean.parseBoolean(getSinglePref(context, "newCard", "Cards"));
        if  (newCard) {
            positionCard = cardsList.size();
            enableEdit();
        } else {
            String selectedCard = getSinglePref(context,"Selected Card", "Cards");
            for (int i = 0; i < cardsList.size() ; i++) {
                if (cardsList.get(i).equals(selectedCard)){
                    positionCard=i;
                }
            }
            if(!onActivityResult) disableEdit();
        }
        try {
            //region get photo front and set visibility states
            //non è stata scattata
            if (photoNameFront.length() == 0) {
                if(positionCard != cardsList.size())
                    photoNameFront = getSinglePref(context, cardsList.get(positionCard) .concat( "_Front"), "Cards");
            }
            String tempRotationFront ="";
            //positionCard esistente
            if(positionCard != cardsList.size())
                tempRotationFront = getSinglePref(context, cardsList.get(positionCard) .concat(  "_Front_rot"), "Cards");
            //recupera la foto dallo storage
            if (photoNameFront.length() > 0) {
                Camera.currentPhotoPath = photoNameFront;
                bitmapFront = Camera.returnBitmapPhotoFromPath(photoNameFront, false, 0);
                binding.imageFront.setImageBitmap(bitmapFront);
                if (tempRotationFront.length() != 0){
                    Camera.rotate(this, binding.imageFront, Float.parseFloat(tempRotationFront));
                    rotationFront=Float.parseFloat(tempRotationFront);
                }
            } else {
                if(!onActivityResult && !newCard) binding.llCardFront.setVisibility(View.GONE);
            }
            //endregion
            //region get photo rear and set visibility states
            if (photoNameRear.length() == 0) {
                if(positionCard != cardsList.size()) {
                    photoNameRear = getSinglePref(context, cardsList.get(positionCard) .concat(  "_Rear"), "Cards");
                }
            }
            String tempRotationRear="";
            if(positionCard != cardsList.size()) {
                tempRotationRear = getSinglePref(context, cardsList.get(positionCard) .concat(  "_Rear_rot"), "Cards");
            }

            if (photoNameRear.length() > 0) {
                Camera.currentPhotoPath = photoNameRear;
                bitmapRear = Camera.returnBitmapPhotoFromPath(photoNameRear, false, 0);
                binding.imageRear.setImageBitmap(bitmapRear);
                if (tempRotationRear.length() != 0){
                    Camera.rotate(this,binding.imageRear, Float.parseFloat(tempRotationRear));
                    rotationRear=Float.parseFloat(tempRotationRear);
                }
            } else {
                if(!onActivityResult && !newCard) binding.llCardRear.setVisibility(View.GONE);
            }
            //endregion

            if(readQRCode.length()==0 && readBarcode.length()==0) {
                readQRCode = getSinglePref(context, cardsList.get(positionCard) .concat(  "_QR"), "Cards");
                readBarcode = getSinglePref(context, cardsList.get(positionCard) .concat( "_Bar"), "Cards");
            }
            if(readQRCode.length()>0) {
                binding.iQRCode.setImageBitmap(generateQRCode(readQRCode, widthScreen(this)));
            }

            if(readBarcode.length()>0) {
                if (readBarcode.length()==13){
                    binding.iQRCode.setImageBitmap(generateBarcodeEAN13(readBarcode, widthScreen(this), 200));
                } else {
                    binding.iQRCode.setImageBitmap(generateBarcode(readBarcode, widthScreen(this), 200));
                }
                binding.tvScan.setText(readBarcode);
            }
            if  (newCard) {
                if (etCardDesc.length() > 0) {
                    binding.etCardDesc.setText(etCardDesc);
                }
            } else {
                binding.etCardDesc.setText(cardsList.get(positionCard));
                if(!onActivityResult) disableBarcodeEdit();
            }

        } catch (Exception e) {
            e.getMessage();
            e.getStackTrace();
        }
        binding.progressbar.setVisibility(View.GONE);
        startActivityAnimationForDialogBarcode = AnimationUtils.loadAnimation(ModifyCardsActivity.this, R.anim.fade_fake);
        startActivityAnimationForDialogBarcode.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onActivityResult) {
                    if (readBarcode.length() == 12) {
                        String title = getString(R.string.title_upca);
                        String mess = getString(R.string.mess_upca);
                        showDialogYesNoAddZero(title, mess, readBarcode);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(startActivityAnimationForDialogBarcode);
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToPreviousActivity();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_cards, menu);
        this.menu=menu;
        action_save = this.menu.getItem(0);
        if  (newCard) {
            action_save.setVisible(true);
        }
        return true;
    }

    private void getBarcode() {
        Camera camera=new Camera(this, this);
        camera.tryToScan(true,false);
    }
    private void getQRCode() {
        Camera camera=new Camera(this, this);
        camera.tryToScan(false,false);
    }
    private void getDatamatrix() {
        Camera camera=new Camera(this, this);
        camera.tryToScan(false,true);
    }

    private void selectPhoto(int sidePhoto) {
        this.sidePhoto=sidePhoto;
        Camera camera=new Camera(this, this);
        String photoName = "";
        if (sidePhoto==1){
            photoName ="card_front_".concat(String.valueOf(positionCard)).concat( "_");
        } else {
            photoName ="card_rear_".concat(String.valueOf(positionCard)).concat( "_");
        }
        photoNameReturn = camera.tryToGetPhoto(photoName);//nomefile completo di percorso
    }
    private void rotate(boolean left,boolean photoFront){
        if (left){
            if (photoFront){
                rotationFront -=90;
                if(rotationFront<-360){ rotationFront +=360; }
                Camera.rotate(this,binding.imageFront, -90);

            } else {
                rotationRear -=90;
                if(rotationRear<-360){ rotationRear +=360; }
                Camera.rotate(this,binding.imageRear, -90);
            }
        } else {
            if (photoFront){
                rotationFront +=90;
                if(rotationFront>360){ rotationFront -=360; }
                Camera.rotate(this,binding.imageFront, +90);
            } else {
                rotationRear +=90;
                if(rotationRear>360){ rotationRear -=360; }
                Camera.rotate(this,binding.imageRear, +90);
            }
        }
    }
    private void saveCard(){
        boolean checkExist=false;//check for the same name in the list
        //casi
        for (int i = 0; i < cardsList.size() ; i++) {
            if (i!=positionCard){//excludes the current position
                if (cardsList.get(i).equals(binding.etCardDesc.getText().toString())){
                    checkExist=true;
                }
            }
        }
        if (!checkExist && !binding.etCardDesc.getText().toString().equals("")){
            if(newCard){
                //new element
                dragList.addCard(binding.etCardDesc.getText().toString());
                newCard=false;
                putSinglePref(context,"newCard", "false", "Cards");
            } else {
                //delete old element names
                removeSinglePref(context,cardsList.get(positionCard) .concat("_Front"),"Cards");
                removeSinglePref(context,cardsList.get(positionCard) .concat("_Rear"),"Cards");
                putSinglePref(context, String.valueOf(positionCard),binding.etCardDesc.getText().toString(),"Cards");
            }
            if(!photoNameFront.equals("")){
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Front"),photoNameFront,"Cards");
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Front_rot"), String.valueOf(rotationFront),"Cards");
            }
            if(!photoNameRear.equals("")){
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Rear"),photoNameRear,"Cards");
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Rear_rot"), String.valueOf(rotationRear),"Cards");
            }
            if (readQRCode.length()>0){
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_QR"),readQRCode,"Cards");
                removeSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Bar"),"Cards");
            }
            if (readBarcode.length()>0){
                putSinglePref(context,binding.etCardDesc.getText().toString() .concat("_Bar"),readBarcode,"Cards");
                removeSinglePref(context,binding.etCardDesc.getText().toString() .concat("_QR"),"Cards");
            }
            Toast.makeText(context, getString(R.string.savingok), Toast.LENGTH_SHORT).show();
            disableEdit();
            if (photoNameFront.length() > 0) {
                binding.llCardFront.setVisibility(View.VISIBLE);
            } else {
                binding.llCardFront.setVisibility(View.GONE);
            }
            if (photoNameRear.length() > 0) {
                binding.llCardRear.setVisibility(View.VISIBLE);
            } else {
                binding.llCardRear.setVisibility(View.GONE);
            }
        } else {
            showDialogOk(getString(R.string.alert), getString(R.string.name_exist));
        }
    }

    private void enableEdit(){
        binding.textDescription.setVisibility(View.VISIBLE);//Title Description
        binding.etCardDesc.setEnabled(true);//Label Description

        binding.btBarcode.setVisibility(View.VISIBLE);//text Barcode
        binding.lblBarcode.setVisibility(View.VISIBLE);//or Barcode/QRCode
        binding.btQRCode.setVisibility(View.VISIBLE);//image QRCode
        binding.llCodes.setVisibility(View.VISIBLE);//Linearlayout Barcode/QRCode
        binding.tvScan.setVisibility(View.VISIBLE);//text barcode

        binding.llCardFront.setVisibility(View.VISIBLE);//entire group
        binding.photoSelectFront.setVisibility(View.VISIBLE);//Button take picture
        binding.lblFront.setVisibility(View.VISIBLE);//or
        binding.photoGalleryFront.setVisibility(View.VISIBLE);//Button select from gallery

        binding.llCardRear.setVisibility(View.VISIBLE);//entire group
        binding.photoSelectRear.setVisibility(View.VISIBLE);//Button take picture
        binding.lblRear.setVisibility(View.VISIBLE);//or
        binding.photoGalleryRear.setVisibility(View.VISIBLE);//Button select from gallery

    }
    private void disableBarcodeEdit(){

        binding.btBarcode.setVisibility(View.GONE);//button Barcode
        binding.lblBarcode.setVisibility(View.GONE);//or Barcode/QRCode
        binding.lblBarcode1.setVisibility(View.GONE);//or Barcode/QRCode
        binding.btQRCode.setVisibility(View.GONE);//button QRCode

        if(readBarcode.equals("") && readQRCode.equals("")) {
            binding.llCodes.setVisibility(View.GONE);//Linearlayout Barcode/QRCode
        }
        if(readBarcode.equals("")) binding.tvScan.setVisibility(View.GONE);//text barcode
    }
    private void disableEdit(){
        if(onActivityResult) {
            onActivityResult=false;
        } else {
            //mca_buttonRight.setVisibility(View.GONE);

            binding.textDescription.setVisibility(View.GONE);//Title Description
            binding.etCardDesc.setEnabled(false);//Label Description

            binding.photoSelectFront.setVisibility(View.GONE);//Button take picture
            binding.lblFront.setVisibility(View.GONE);//or
            binding.lblFront1.setVisibility(View.GONE);//or
            binding.photoGalleryFront.setVisibility(View.GONE);//Button select from gallery

            binding.photoSelectRear.setVisibility(View.GONE);//Button take picture
            binding.lblRear.setVisibility(View.GONE);//or
            binding.lblRear1.setVisibility(View.GONE);//or
            binding.photoGalleryRear.setVisibility(View.GONE);//Button select from gallery
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }
    private void backToPreviousActivity() {
        try {
            Intent intent = new Intent(context, CardsActivity.class);
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
    //dialogo OK sovrascrittura
    private void showDialogOk(String title, String message){
        final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
        mBuilder.setTitle(title);
        mBuilder.setMessage(message);
        mBuilder.setNeutralButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        mBuilder.show();
    }

    private void showDialogYesNoAddZero(String title,String mess,String barcode){
        DialogYesNo dialogYesNo=new DialogYesNo(this,title,mess);
        dialogYesNo.setOnDismissListener(dialog -> {
            String retVal=getSinglePref(this,"{dialog_yes}","appSettings");
            if (retVal.length()!=0){
                readBarcode = "0" .concat( barcode);
                binding.tvScan.setText(readBarcode);
            }
        });
        dialogYesNo.show();
    }
    public void onClickSave(MenuItem item) {
        if(binding.etCardDesc.getText().toString().length()==0){
            String title=getString(R.string.missing_description);
            String message=getString(R.string.desc_save_nok);
            DialogOK dialogOK=new DialogOK(this, title, message);
            dialogOK.show();
        } else {
            saveCard();
        }
    }

    public void onClickInformation(MenuItem item) {
        String title   = getString(R.string.title_mca);
        String message = context.getString(R.string.title_mca_string);
        final DialogOK dialogOK=new DialogOK(this,title,message);
        dialogOK.show();
    }

    public void onClickEdit(MenuItem item) {
        // if (mca_buttonRight.getVisibility()==View.VISIBLE){
        if (action_save.isVisible()){

            disableEdit();
            if (photoNameFront.length() > 0) {
                binding.llCardFront.setVisibility(View.VISIBLE);
            } else {
                binding.llCardFront.setVisibility(View.GONE);
            }
            if (photoNameRear.length() > 0) {
                binding.llCardRear.setVisibility(View.VISIBLE);
            } else {
                binding.llCardRear.setVisibility(View.GONE);
            }
            disableBarcodeEdit();
            action_save.setVisible(false);
        } else {
            enableEdit();
            action_save.setVisible(true);
        }
    }

}
