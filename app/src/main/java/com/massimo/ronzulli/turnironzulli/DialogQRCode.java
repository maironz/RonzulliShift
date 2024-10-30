package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.Camera.generateQRCode;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

class DialogQRCode extends Dialog {
    private static final String TAG = "DialogQRCode";
    public Context context;
    private ImageView image_qr;
    private Button bt_ok;
    private TextView tv;
    private LinearLayout ll;
    private SendReceive sendReceive;
    private boolean singleInstance=false;
    View view;
    SavesEnum savesEnum=new SavesEnum();
    Sync sync;


    public DialogQRCode(Context context) {
        super(context);
        this.context=context;
        //se non esistono crea appID e secret
        try {
            sync=new Sync(context);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getText(R.string.not_sync), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendReceive=new SendReceive(context);
        sendReceive.openQRCode=true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_qr_code);
        bt_ok=findViewById(R.id.bt_ok);
        image_qr=findViewById(R.id.image_qr);
        tv=findViewById(R.id.tv);
        String appID=sync.getAppID();
        String secret=sync.getMySecret();
        image_qr.setImageBitmap(generateQRCode(appID, 500));
        tv.setText(appID);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view=this.findViewById(android.R.id.content);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if(!singleInstance){
                        sendReceive.startSyncSend();
                        singleInstance=true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //fermo l'attesa di ricezione in waitReceiveStartSyncSend
        sendReceive.openQRCode=false;
        // blocca la sincronizzazone
        sendReceive.stopSync();
    }
}
