package com.massimo.ronzulli.turnironzulli.models;

import android.content.Context;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

import java.util.ArrayList;
import java.util.List;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

public class Leaves {
    private final String TAG = "Class.Styles";
    private int orderId=0;
    private int totalLeaves =0;
    public final int leavesNo=6;
    private Context context;
    public List<Leave> leaveList;
    SavesEnum savesEnum=new SavesEnum();
    public class Leave {
        private Context context;
        private int leaveID;
        public int orderLeaveID;
        public String leaveDescription;

        public Leave(Context context, int leaveID, int orderLeaveID, String leaveDescription) {
            this.context=context;
            this.leaveID = leaveID;
            this.orderLeaveID = orderLeaveID;
            this.leaveDescription = leaveDescription;
        }
        public int getLeaveID() {
            return leaveID;
        }
    }
    public Leaves(Context context){
        this.context=context;
        leaveList=new ArrayList<Leave>();
        int order=0;
        String leaveDescription="";
        leaveList.add(new Leave(context,0,0,context.getString(R.string.leave_desc)));
        leaveList.add(new Leave(context,1,1,context.getString(R.string.parental_leave)));
        leaveList.add(new Leave(context,2,2,context.getString(R.string.bank_hours)));
        leaveList.add(new Leave(context,3,3,context.getString(R.string.unpaid_leave)));
        leaveList.add(new Leave(context,4,4,context.getString(R.string.disability_permit)));
        leaveList.add(new Leave(context,5,5,context.getString(R.string.blood_donation)));
        leaveList.add(new Leave(context,6,6,context.getString(R.string.compensatory_leave)));
        totalLeaves=leavesNo;//totale elementi in elenco
        //la prima volta che viene eseguito
        String firstTime=getSinglePref(this.context,"{first time leave}",savesEnum.appSettings.APP_SETTINGS);
        if(firstTime.length()==0){
            for (int i = 0; i <= 6; i++) {
                putSinglePref(this.context,
                        "{id}{"
                        .concat(String.valueOf(i))
                        .concat("}"),
                        String.valueOf(leaveList.get(i).leaveID),
                        savesEnum.appSettings.LEAVE_SETTINGS);
                putSinglePref(this.context,"{order}{".concat(String.valueOf(i)).concat("}"),String.valueOf(leaveList.get(i).orderLeaveID),savesEnum.appSettings.LEAVE_SETTINGS);
                putSinglePref(this.context,"{leave description}{".concat(String.valueOf(i)).concat("}"),leaveList.get(i).leaveDescription,savesEnum.appSettings.LEAVE_SETTINGS);
                putSinglePref(this.context,"{total leaves}",String.valueOf(totalLeaves),savesEnum.appSettings.LEAVE_SETTINGS);
            }
        } else {
            putSinglePref(this.context,"{first time leave}","false",savesEnum.appSettings.APP_SETTINGS);
        }
        //prelevo il totale degli id
        String totalLeavesString=getSinglePref(this.context,"{total leaves}",savesEnum.appSettings.LEAVE_SETTINGS);
        if(totalLeavesString.length()>0){
            totalLeaves=Integer.parseInt(totalLeavesString);
        }
        for (int i = 6; i <= totalLeaves; i++) {
            String leaveIdString=getSinglePref(this.context,"{id}{".concat(String.valueOf(i)).concat("}"),savesEnum.appSettings.LEAVE_SETTINGS);
            if(leaveIdString.length()>0){
                String leaveOderString=getSinglePref(this.context,"{order}{".concat(String.valueOf(i)).concat("}"),savesEnum.appSettings.LEAVE_SETTINGS);
                if(leaveOderString.length()>0){
                    order=Integer.parseInt(leaveOderString);
                    orderId=order;//dovrei fare un controllo,ma sono in sequenza
                } else {
                    order=-1;
                }
                leaveDescription=getSinglePref(this.context,"{leave description}{".concat(String.valueOf(i)).concat("}"),savesEnum.appSettings.LEAVE_SETTINGS);
                leaveList.add(new Leave(context,i,order,leaveDescription));;
            }
        }
    }
    public void add(String leaveDescription) {
        totalLeaves ++;
        orderId ++;
        try {
            leaveList.add(new Leave(context,totalLeaves,orderId,leaveDescription));
            putSinglePref(this.context,"{total leaves}",String.valueOf(totalLeaves),savesEnum.appSettings.LEAVE_SETTINGS);
        } catch (Exception e) {
            orderId --;
        }
    }
    public void remove(int leaveID) {
        int tempoOrderID=0;
        //i primi 6 preimpostati non sono modificabili
        if(leaveID>leavesNo){
            //imposto order id=-1
            leaveList.get(leaveID).orderLeaveID=-1;
            //salvo la modifica
            putSinglePref(this.context,"{order}{".concat(String.valueOf(leaveID)).concat("}"),"-1",savesEnum.appSettings.LEAVE_SETTINGS);
            //scalo gli order id se ce ne sono
            for (int i = leaveID; i <= totalLeaves; i++) {
                if (leaveList.get(i).orderLeaveID!=-1){
                    leaveList.get(i).orderLeaveID --;
                    tempoOrderID=leaveList.get(i).orderLeaveID;
                    orderId= tempoOrderID;//dovrei fare un controllo,ma sono in sequenza
                    putSinglePref(this.context,"{order}{".concat(String.valueOf(i)).concat("}"), String.valueOf(leaveList.get(i).orderLeaveID),savesEnum.appSettings.LEAVE_SETTINGS);
                }
            }
        }
    }
    public int getTotalLeaves(){
        return totalLeaves;
    }

}
