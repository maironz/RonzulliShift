package com.massimo.ronzulli.turnironzulli;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.decrypt;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.encrypt;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

import com.massimo.ronzulli.turnironzulli.STR.DBVolley;
import com.massimo.ronzulli.turnironzulli.STR.ServerCallback;

/*
* create dialog class to instantiate the communication
    class DialogQRCode extends Dialog
* declaration
    private SendReceive sendReceive;
    private boolean singleInstance=false; // get a unique instance
* in onCreate
    sendReceive=new SendReceive(context);
    sendReceive.openQRCode=true;
    view=this.findViewById(android.R.id.content);
    ViewTreeObserver vto = view.getViewTreeObserver();
    vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            try {
                if(!singleInstance){//unique instance
                    sendReceive.startSyncSend();
                    singleInstance=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        }
    });
* */
class SendReceive {
    private Context context;
    private SavesEnum savesEnum=new SavesEnum();
    private Sync sync;
    private String secret;//archiviazione interna protetta

    private String appID;//application id
    private String receivedAppID;//AppID ricevuto tramite scansione
    //secretExchange = chiave da inviare al receiver prelevata dall'archiviazione in fase di inizializzazione di questa classe
    // creata dalla classe Sync in fase di inizializzazione se non esistente
    private String secretExchange;
    private String sentMessage;//messaggio inviato
    private String encryptedSentMessage;//messaggio criptato inviato
    private long tsLong;//timestamp sentMessage

    private String AppIDSync;//application id della sincronizzata
    private String secretExchangeReturn;//chiave ricevuta dal receiver
    private String receivedMessage;//messaggio ricevuto
    private String encryptedReceivedMessage;//messaggio criptato ricevuto

    private final ScheduledExecutorService scheduledExecutorService =Executors.newScheduledThreadPool(1);
    private int endSync;

    public boolean openQRCode=false;//da impostare a true all'apertura della finestra QRCode
    public boolean openSync=false;//da impostare a true all'apertura della finestra di sincronizzazione

    public SendReceive(Context context){
        this.context=context;
        try {
            sync=new Sync(context);
            appID=sync.getAppID();
            AppIDSync=getReceivedAppID();
            secret=sync.getMySecret();//secret di archiviazione protetta dati interna casuale
            getSecretExchange();
            //se la chiave di scambio non esiste, la creo e la salvo criptata
            if (secretExchange.length()!=32){
                updateSecretExchange();
            }
            //prelevo sentMessage
            getSentMessage();
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getText(R.string.not_sync), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    //region gestione chiave segreta secretExchange
    // aggiorna in secretExchange la chiave segreta di comunicazione tra le app e la archivia criptata con secret
    private void updateSecretExchange(){
        secretExchange = UUID.randomUUID().toString().replace("-", "");
        try {
            putSinglePref(context,"secretExchange",encrypt(secretExchange,secret),savesEnum.appSettings.APP_SETTINGS);
            getSecretExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // imposta in secretExchange la chiave segreta di comunicazione tra le app e la archivia criptata con secret
    private void setSecretExchange(String secret){
        secretExchange = secret;
        try {
            putSinglePref(context,"secretExchange",encrypt(secretExchange,secret),savesEnum.appSettings.APP_SETTINGS);
            getSecretExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // imposta in secretExchange la chiave segreta di comunicazione tra le app prelevandola dallo storage
    private void getSecretExchange(){
        secretExchange=decrypt(getSinglePref(context,"secretExchange",savesEnum.appSettings.APP_SETTINGS),secret);
    }
    //endregion

    //region gestione chiave segreta secretExchangeReturn
    // aggiorna in secretExchangeReturn la chiave segreta di comunicazione tra le app e la archivia criptata con secret
    private void updateSecretExchangeReturn(){
        secretExchangeReturn = UUID.randomUUID().toString().replace("-", "");
        try {
            putSinglePref(context,"secretExchangeReturn",encrypt(secretExchangeReturn,secret),savesEnum.appSettings.APP_SETTINGS);
            getSecretExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // imposta in secretExchange la chiave segreta di comunicazione tra le app e la archivia criptata con secret
    private void setSecretExchangeReturn(String secret){
        secretExchangeReturn = secret;
        try {
            putSinglePref(context,"secretExchangeReturn",encrypt(secretExchangeReturn,secret),savesEnum.appSettings.APP_SETTINGS);
            getSecretExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // imposta in secretExchange la chiave segreta di comunicazione tra le app prelevandola dallo storage
    private void getSecretExchangeReturn(){
        secretExchangeReturn=decrypt(getSinglePref(context,"secretExchangeReturn",savesEnum.appSettings.APP_SETTINGS),secret);
    }
    //endregion

    public void setReceivedAppID(String received_appID){
        receivedAppID=received_appID;
        AppIDSync=received_appID;
        sync.setExternalAppIDSync(received_appID);
        try {
            putSinglePref(context,"receivedAppID",encrypt(receivedAppID,secret),savesEnum.appSettings.APP_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getReceivedAppID(){
        receivedAppID=decrypt(getSinglePref(context,"receivedAppID",savesEnum.appSettings.APP_SETTINGS),secret);
        return receivedAppID;
    }

    //region gestione sentMessage
    //salva nello storage sentMessage criptato e imposta sentMessage e encryptedSentMessage
    private void setSentMessage(String sentMessage){
        this.sentMessage=sentMessage;
        try {
            putSinglePref(context,"sentMessage",encrypt(sentMessage,secret),savesEnum.appSettings.APP_SETTINGS);
            getSentMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // imposta sentMessage prelevandola dallo storage e decrittandola
    private String getSentMessage(){
        sentMessage=decrypt(getSinglePref(context,"sentMessage",savesEnum.appSettings.APP_SETTINGS),secret);
        return sentMessage;
    }
    //endregion

    // funzione che avvia la sincronizzazione
    public boolean startSyncSend(){
        //primo messaggio (apertura QRcode)
        boolean retval=false;
        if(openQRCode){
            //creo la disponibilità alla sincronizzazione
            //invio un messaggio al server che contiene AppID (solo la prima volta) e secretExchange valida per qualche minuto
            //secretExchange servirà per decodificare il messaggio ricevuto
            //nel messaggio ricevuto ci sono :
            // AppID send
            // Time Start
            // secretExchange chiave di cripting del messaggio successivo
            endSync=0;
            try {
                //usa come chiave segreta appID come prima volta
                setSentMessage(appID.concat("__").concat(secretExchange));
                encryptedSentMessage=encrypt(sentMessage,appID);
                //invia il messaggio al server (va memorizzato in messaggio di prima connessione)
                DBVolley dbVolley=new DBVolley(context);
                dbVolley.appID=appID;
                dbVolley.syncType="startSyncSend52";
                dbVolley.mess=encryptedSentMessage;
                dbVolley.DBVolleySync(new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if(result.equals(DBVolley.synkOk)){
                            waitReceiveStartSyncSend();
                        }
                    }
                });
                retval=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retval;
    }
    //dopo il primo invio dei dati con successo attende la risposta
    //questa funzione chiama in maniera asincrona ogni secondo receiveStartSyncSend
    private void waitReceiveStartSyncSend(){
        try {
            //imposto gli eventi di funzionamento
            final Runnable receiveStartSyncSend = new Runnable() {
                public void run() {
                    endSync+=1;
                    Log.e("receiveStartSyncSend", " endSync=".concat(String.valueOf(endSync)));
                    if(endSync>20){ scheduledExecutorService.shutdown(); }
                    receiveStartSyncSend();
                }
            };
            final ScheduledFuture<?> receiverHandle = scheduledExecutorService.scheduleAtFixedRate(receiveStartSyncSend, 500, 1000, TimeUnit.MILLISECONDS);

            scheduledExecutorService.schedule(new Runnable() {
                public void run() { receiverHandle.cancel(true); }
            }, 40, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //funzione di richiamo dei dati che viene chiamata ogni secondo da waitReceiveStartSyncSend
    private boolean receiveStartSyncSend(){
        final boolean[] retval = {false};
        try {
            //legge il messaggio dal server
            DBVolley dbVolley=new DBVolley(context);
            dbVolley.appID=appID;
            dbVolley.syncType="startSyncSendReceive52";
            dbVolley.DBVolleySync(new ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    encryptedReceivedMessage=result;
                    //se il messaggio letto è diverso il partner ha scritto lui qualcosa
                    if(!encryptedReceivedMessage.equals(encryptedSentMessage)){
                        //se ricevo nok ho un problema con il server
                        if(!encryptedReceivedMessage.equals("nok")){
                            //il partner risponde sovrascrivendo il messaggio
                            receivedMessage=decrypt(encryptedReceivedMessage,secretExchange);
                            if (receivedMessage.length()>0){
                                //effettuo lo split del messaggio ricevuto
                                String[] splitMessage=receivedMessage.split("__");
                                // mi restituisce il suo partner appID
                                AppIDSync=splitMessage[0];//partner id
                                if (AppIDSync.length()==32){
                                    sync.setExternalAppIDSync(AppIDSync);
                                }
                                //chiave di scambio successiva
                                setSecretExchange(splitMessage[1]);
                                scheduledExecutorService.shutdown();
                                openQRCode=false;
                                retval[0] =true;
                            }
                        }
                    } else {
                        Log.e("receiveStartSyncSend", "onSuccess: messaggi uguali");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval[0];
    }
    //termina la sincronizzazione (richiamata in onDismiss())
    public void stopSync(){
        scheduledExecutorService.shutdown();
    }
    //avvia la sincronizzazione in ricezione
    public boolean startSyncReceive(String readQRCode){
        boolean retval=false;
        sync.setExternalAppIDSync(readQRCode);
        //legge il messaggio dal server
        DBVolley dbVolley=new DBVolley(context);
        dbVolley.appID=readQRCode;
        dbVolley.syncType="startSyncSendReceive52";
        dbVolley.DBVolleySync(new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                encryptedReceivedMessage=result;
                //se ricevo nok ho un problema con il server
                if(!encryptedReceivedMessage.equals("nok")){
                    receivedMessage=decrypt(encryptedReceivedMessage,dbVolley.appID);
                    if (receivedMessage.length()>0){
                        String[] splitMessage=receivedMessage.split("__");
                        setReceivedAppID(splitMessage[0]);
                        if (AppIDSync.length()==32){
                            sync.setExternalAppIDSync(AppIDSync);
                        }
                        //chiave di scambio da usare per codificare per il dispositivo A
                        setSecretExchangeReturn(splitMessage[1]);
                        //comunico l'avvenuta ricezione al dispositivo A
                        sendFirstResponseToSender();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getText(R.string.server_offline), Toast.LENGTH_LONG).show();
                }
            }
        });
        return retval;
    }
    //
    private void sendFirstResponseToSender(){
        updateSecretExchange();
        String message=appID.concat("__").concat(secretExchange);//id+next key
        try {
            encryptedSentMessage=encrypt(message,secretExchangeReturn);
            //invia il messaggio al server (va memorizzato in messaggio di prima connessione)
            DBVolley dbVolley=new DBVolley(context);
            dbVolley.appID=appID;
            dbVolley.syncType="startSyncSend52";
            dbVolley.mess=encryptedSentMessage;
            dbVolley.DBVolleySync(new ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    if(!result.equals(DBVolley.synkOk)){
                        Toast.makeText(context, context.getResources().getText(R.string.server_offline), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "posso ascoltare", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //invia un nuovo messaggio al partner
    public boolean syncSend(String message){
        boolean retval=false;
        getSecretExchange();
        secretExchangeReturn= UUID.randomUUID().toString().replace("-", "");
        long actualTsLong = System.currentTimeMillis()/1000;
        try {
            setSentMessage(appID.concat("__").concat(String.valueOf(actualTsLong)).concat("__").concat(secretExchangeReturn).concat("__").concat(message));
            encryptedSentMessage=encrypt(sentMessage,secretExchange);
            setSecretExchange(secretExchangeReturn);
            //todo:send message with volley
            retval=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }
    //recupera un nuovo messaggio dal partner
    public boolean syncReceive(){
        boolean retval=false;
        getSecretExchange();
        secretExchangeReturn= UUID.randomUUID().toString().replace("-", "");
        long actualTsLong = System.currentTimeMillis()/1000;
        try {
            sentMessage=encrypt(appID.concat("__").concat(String.valueOf(actualTsLong)).concat("__").concat(secretExchangeReturn),secretExchange);
            //todo:receive message with volley
            retval=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }
}
