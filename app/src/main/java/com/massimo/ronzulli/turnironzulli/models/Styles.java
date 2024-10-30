package com.massimo.ronzulli.turnironzulli.models;

import android.content.Context;
import android.graphics.Color;

import com.massimo.ronzulli.turnironzulli.R;
import com.massimo.ronzulli.turnironzulli.SavesEnum;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

import java.util.ArrayList;

public class Styles {
    private static final String TAG = "Class.Styles";
    public static int totalStyles=0;
    public static class StyleCells {
        public int id;
        public int order;
        public String sd;//short description {sd}
        public String ld;//long description {ld}
        public int hs;//hour start {hs}
        public int he;//hour end {he}
        public int ms;//min start {hs}
        public int me;//min end {he}
        public int tc;//text color {tc}
        public int bc;//background color {bc}
        public int bbc;//border color {bbc}
        public int dc;//dot color {dc}
        public boolean ns;//not statistic {ns}
        public boolean wb;//with break {wb}
        public int bhs;//break hour start {bs}
        public int bhe;//break hour end {be}
        public int bms;//break min start {bs}
        public int bme;//break min end {be}
        public String range;//hour range
        public String rangeFood;//hour range
        public StyleCells() {
            id=0;
            order=0;
            sd= "";
            ld="";
            hs=0;
            he=0;
            ms=0;
            me=0;
            tc=Color.BLACK;
            bc=Color.WHITE;
            bbc= Color.BLACK;
            dc=Color.RED;
            ns=false;
            wb=false;
            bhs=0;
            bhe=0;
            bms=0;
            bme=0;
            range="";
            rangeFood="";
        }
    }
    public Context context;
    public StyleCells[] style;
    public ArrayList<StyleCells> styleCellsArrayList=new ArrayList<>();//deleted
    public ArrayList<StyleCells> styleCellsArrayList1=new ArrayList<>();//not deleted

    public Styles(Context context) {
        this.context=context;
        int lastIdStyle=19;
        SavesEnum savesEnum=new SavesEnum();
        String temp_str=getSinglePref(context, savesEnum.appSettings.LAST_ID_STYLE, savesEnum.appSettings.APP_SETTINGS);
        if (temp_str.length()==0){
            makeStandardStyle(context, savesEnum);
            temp_str=getSinglePref(context, savesEnum.appSettings.LAST_ID_STYLE, savesEnum.appSettings.APP_SETTINGS);
        }
        if(temp_str.length()!=0){
            lastIdStyle = Integer.parseInt(temp_str);
        }
        style= new StyleCells[lastIdStyle+1];
        totalStyles=lastIdStyle+1;
        for (int i = 0; i <= lastIdStyle; i++) {
            style[i]= new StyleCells();
        }
        for (int i = 0; i <= lastIdStyle; i++) {
            StyleCells tempStyleCells=getStyle(i);
            //l'ordine di caricamento è in base ad order
            if (tempStyleCells.order!=0){
                style[tempStyleCells.order]= tempStyleCells;
                styleCellsArrayList1.add(tempStyleCells);
            } else {
                styleCellsArrayList.add(getStyle(i));
            }
            //se la long description è vuota la riscrivo come resource string
            if (tempStyleCells.ld.equals("")){
                style[tempStyleCells.order].ld=context.getString(R.string.default_value_styles_long_description);
            }
        }
    }
    public StyleCells getStyle(int id){
        StyleCells tempStyleCells= new StyleCells();
        String param,retval;
        try {
            param="id";
            retval=getParam( id, param);
            if (retval.length()>0){
                tempStyleCells.id=Integer.parseInt(retval);//id {id}
            }
            param="order";
            retval=getParam( id, param);
            if (retval.length()>0){
                tempStyleCells.order=Integer.parseInt(retval);//ordinamento {order}
            }
            param="sd";
            tempStyleCells.sd=getParam( id, param);//short description {sd}
            param="ld";
            tempStyleCells.ld=getParam( id, param);//long description {ld}
            param="hs";
            retval=getParam( id, param);//hour start {hs}
            if (retval.length()>0){
                tempStyleCells.hs= Integer.parseInt(retval);
            }
            param="he";
            retval=getParam( id, param);//hour end {he}
            if (retval.length()>0){
                tempStyleCells.he= Integer.parseInt(retval);
            }
            param="ms";
            retval=getParam( id, param);//min start {ms}
            if (retval.length()>0){
                tempStyleCells.ms= Integer.parseInt(retval);
            }
            param="me";
            retval=getParam( id, param);//min end {me}
            if (retval.length()>0){
                tempStyleCells.me= Integer.parseInt(retval);
            }
            param="tc";
            retval=getParam( id, param);//text color {tc}
            if (retval.length()>0){
                tempStyleCells.tc= Integer.parseInt(retval);
            }
            param="bc";
            retval=getParam( id, param);//background color {bc}
            if (retval.length()>0){
                tempStyleCells.bc= Integer.parseInt(retval);
            }
            param="bbc";
            retval=getParam( id, param);//border color {bbc}
            if (retval.length()>0){
                tempStyleCells.bbc= Integer.parseInt(retval);
            }
            param="dc";
            retval=getParam( id, param);//dot color {dc}
            if (retval.length()>0){
                tempStyleCells.dc= Integer.parseInt(retval);
            }
            param="ns";
            retval=getParam( id, param);//not statistic {ns}
            if (retval.length()>0){
                tempStyleCells.ns= Boolean.parseBoolean(retval);
            }
            param="wb";
            retval=getParam( id, param);//with break {wb}
            if (retval.length()>0){
                tempStyleCells.wb= Boolean.parseBoolean(retval);
                if(tempStyleCells.wb){
                    String str=getParam( id, "bhs");
                    if(getParam( id, "bhs").length()!=0){
                        tempStyleCells.bhs= Integer.parseInt(getParam( id, "bhs"));//break hour start {bhs}
                    } else {
                        tempStyleCells.bhs= 0;//break hour start {bhs}
                    }
                    if(getParam( id, "bhe").length()!=0){
                        tempStyleCells.bhe= Integer.parseInt(getParam( id, "bhe"));//break hour bhe {bhe}
                    } else {
                        tempStyleCells.bhe= 0;//break hour bhe {bhe}
                    }
                    if(getParam( id, "bms").length()!=0){
                        tempStyleCells.bms= Integer.parseInt(getParam( id, "bms"));//break min start {bms}
                    } else {
                        tempStyleCells.bms= 0;//break min start {bms}
                    }
                    if(getParam( id, "bme").length()!=0){
                        tempStyleCells.bme= Integer.parseInt(getParam( id, "bme"));//break min end {bme}
                    } else {
                        tempStyleCells.bme= 0;//break min end {bme}
                    }
                    tempStyleCells.rangeFood = getRange(tempStyleCells.bhs,tempStyleCells.bms,tempStyleCells.bhe,tempStyleCells.bme);
                }
            }
            tempStyleCells.range = getRange(tempStyleCells.hs,tempStyleCells.ms,tempStyleCells.he,tempStyleCells.me);
        } catch (Exception e){
            //Log.e(TAG, "getStyle: id=" + id + " param=" + param + " retval=" + retval + " message:" +e.getMessage());
           // Log.e(TAG, "getStyle: " + Arrays.toString(e.getStackTrace()));
        }
        return tempStyleCells;
    }
    private String getParam(int id, String param){
        return getSinglePref(this.context,"{".concat(param).concat("}{").concat(String.valueOf(id)).concat("}"),"styles");
    }
    private String getRange(int hs,int ms,int he, int me){
        String retval="";
        String strhs="00",strhe="00",strms="00",strme="00";
        if (!(hs==-1 || he==-1 || ms==-1 || me==-1)){
            if (String.valueOf(hs).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strhs=stringbuilder.append("0").append(hs).toString();
            } else {strhs=String.valueOf(hs);}
            if (String.valueOf(he).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strhe=stringbuilder.append("0").append(he).toString();
            } else {strhe=String.valueOf(he);}
            if (String.valueOf(ms).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strms=stringbuilder.append("0").append(ms).toString();
            } else {strms=String.valueOf(ms);}
            if (String.valueOf(me).length()==1){
                StringBuilder stringbuilder = new StringBuilder();
                strme=stringbuilder.append("0").append(me).toString();
            } else {strme=String.valueOf(me);}
            retval = strhs .concat( ":" ).concat( strms ).concat( "-" ).concat( strhe ).concat( ":" ).concat( strme);
            if (strhs.equals("00")&&strms.equals("00")&&strhe.equals("00")&&strme.equals("00")) {retval ="";}
        }
        return retval;
    }
    public static int makeStandardStyle(Context context, SavesEnum savesEnum){
        final int BROWNRED=Color.rgb(160, 40, 40);
        final int BROWNREDLIGHT=Color.rgb(200, 50, 50);

        putSinglePref(context,"{id}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{0}","",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{0}","",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{0}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{0}", String.valueOf(Color.parseColor("#F7F7ED")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{0}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{0}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{0}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{0}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{0}","0",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{1}","1",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{1}","06-14",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{1}",context.getResources().getString(R.string.styles_lt_6_14),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{1}","6",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{1}","14",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{1}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{1}", String.valueOf(Color.parseColor("#F4FF81")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{1}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{1}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{1}", "true",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{1}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{1}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{1}","1",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{2}","2",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{2}","14-22",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{2}",context.getResources().getString(R.string.styles_lt_14_22),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{2}","14",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{2}","22",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{2}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{2}", String.valueOf(Color.parseColor("#84FFFF")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{2}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{2}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{2}", "true",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{2}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{2}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{2}","2",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{3}","3",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{3}","22-06",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{3}",context.getResources().getString(R.string.styles_lt_22_6),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{3}","22",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{3}","6",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{3}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{3}", String.valueOf(Color.parseColor("#82B1FF")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{3}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{3}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{3}", "true",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{3}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{3}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{3}","3",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{4}","4",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{4}",context.getResources().getString(R.string.Sat),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{4}",context.getResources().getString(R.string.Saturday),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{4}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{4}", String.valueOf(Color.parseColor("#EA80FC")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{4}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{4}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{4}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{4}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{4}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{4}","4",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{5}","5",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{5}",context.getResources().getString(R.string.Sun),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{5}",context.getResources().getString(R.string.Sunday),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{5}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{5}", String.valueOf(Color.parseColor("#EA80FC")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{5}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{5}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{5}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{5}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{5}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{5}","5",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{6}","6",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{6}",context.getResources().getString(R.string.styles_sd_layoffs),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{6}",context.getResources().getString(R.string.styles_ld_layoffs),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{6}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{6}", String.valueOf(Color.parseColor("#a4aeb4")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{6}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{6}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{6}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{6}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{6}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{6}","6",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{7}","7",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{7}",context.getResources().getString(R.string.styles_sd_vacation),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{7}",context.getResources().getString(R.string.styles_ld_vacation),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{7}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{7}", String.valueOf(Color.parseColor("#00bf30")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{7}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{7}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{7}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{7}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{7}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{7}","7",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{8}","8",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{8}",context.getResources().getString(R.string.styles_sd_holiday),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{8}",context.getResources().getString(R.string.styles_ld_holiday),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{8}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{8}", String.valueOf(Color.RED),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{8}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{8}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{8}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{8}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{8}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{8}","8",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{9}","9",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{9}",context.getResources().getString(R.string.styles_sd_corporate_training),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{9}",context.getResources().getString(R.string.styles_ld_corporate_training),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{9}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{9}", String.valueOf(Color.parseColor("#ff66ff")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{9}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{9}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{9}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{9}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{9}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{9}","9",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{10}","10",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{10}",context.getResources().getString(R.string.styles_sd_cdp),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{10}",context.getResources().getString(R.string.styles_ld_cdp),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{10}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{10}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{10}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{10}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{10}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{10}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{10}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{10}","10",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{11}","11",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{11}",context.getResources().getString(R.string.styles_sd_corporate_ptf),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{11}",context.getResources().getString(R.string.styles_ld_corporate_ptf),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{11}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{11}", String.valueOf(Color.parseColor("#ff66ff")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{11}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{11}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{11}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{11}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{11}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{11}","11",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{12}","12",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{12}",context.getResources().getString(R.string.styles_sd_corporate_vacation),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{12}",context.getResources().getString(R.string.styles_ld_corporate_vacation),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{12}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{12}", String.valueOf(Color.parseColor("#ff66ff")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{12}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{12}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{12}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{12}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{12}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{12}","12",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{13}","13",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{13}",context.getResources().getString(R.string.styles_sd_disease),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{13}",context.getResources().getString(R.string.styles_ld_disease),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{13}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{13}", String.valueOf(Color.parseColor("#00bf30")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{13}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{13}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{13}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{13}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{13}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{13}","13",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{14}","14",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{14}",context.getResources().getString(R.string.styles_sd_ptf),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{14}",context.getResources().getString(R.string.styles_ld_ptf),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{14}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{14}", String.valueOf(Color.parseColor("#00bf30")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{14}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{14}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{14}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{14}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{14}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{14}","14",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{15}","15",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{15}",context.getResources().getString(R.string.styles_sd_08_16),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{15}",context.getResources().getString(R.string.styles_ld_08_16),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{15}","8",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{15}","16",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{15}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{15}", String.valueOf(Color.parseColor("#ff6666")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{15}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{15}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{15}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{15}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{15}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{15}","15",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{16}","16",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{16}",context.getResources().getString(R.string.styles_sd_00_06),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{16}",context.getResources().getString(R.string.styles_ld_00_06),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{16}","6",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{16}","15",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{16}", String.valueOf(Color.WHITE),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{16}", String.valueOf(Color.BLUE),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{16}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{16}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{16}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{16}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{16}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{16}","16",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{17}","17",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{17}",context.getResources().getString(R.string.styles_sd_06_12),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{17}",context.getResources().getString(R.string.styles_ld_06_12),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{17}","6",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{17}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{17}","12",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{17}","15",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{17}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{17}", String.valueOf(Color.parseColor("#F4FF81")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{17}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{17}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{17}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{17}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{17}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{17}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{17}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{17}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{17}","17",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{18}","18",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{18}",context.getResources().getString(R.string.styles_sd_12_18),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{18}",context.getResources().getString(R.string.styles_ld_12_18),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{18}","12",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{18}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{18}","18",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{18}","15",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{18}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{18}", String.valueOf(Color.parseColor("#84FFFF")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{18}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{18}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{18}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{18}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{18}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{18}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{18}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{18}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{18}","18",savesEnum.appSettings.STYLE_SETTINGS);

        putSinglePref(context,"{id}{19}","19",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{sd}{19}",context.getResources().getString(R.string.styles_sd_18_24),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ld}{19}",context.getResources().getString(R.string.styles_ld_18_24),savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{hs}{19}","18",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{ms}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{he}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{me}{19}","15",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{tc}{19}", String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//text color
        putSinglePref(context,"{bc}{19}", String.valueOf(Color.parseColor("#82B1FF")),savesEnum.appSettings.STYLE_SETTINGS);//background color
        putSinglePref(context,"{bbc}{19}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//border color
        putSinglePref(context,"{dc}{19}",String.valueOf(Color.BLACK),savesEnum.appSettings.STYLE_SETTINGS);//dot color
        putSinglePref(context,"{ns}{19}", "false",savesEnum.appSettings.STYLE_SETTINGS);//not statistic
        putSinglePref(context,"{wb}{19}", "false",savesEnum.appSettings.STYLE_SETTINGS);//with break
        putSinglePref(context,"{bhs}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bms}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bhe}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{bme}{19}","0",savesEnum.appSettings.STYLE_SETTINGS);
        putSinglePref(context,"{order}{19}","19",savesEnum.appSettings.STYLE_SETTINGS);

        int styles=19;
        putSinglePref(context,"{last id style}", String.valueOf(styles),savesEnum.appSettings.APP_SETTINGS);
        //set the total rows to 5
        putSinglePref(context, "{total styles}", String.valueOf(styles),savesEnum.appSettings.APP_SETTINGS);
        putSinglePref(context,"{sequence}{1}","1-1-1-1-1-4-5-3-3-3-3-3-4-5-2-2-2-2-2-4-5",savesEnum.appSettings.ROTATION_SETTINGS);
        return styles;
    }
}
