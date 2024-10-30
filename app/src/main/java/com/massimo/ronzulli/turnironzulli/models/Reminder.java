package com.massimo.ronzulli.turnironzulli.models;

import static java.lang.Math.abs;
import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Reminder {
   private static int _reminderID = 0;
   private boolean _reminderDeleted = false;
   private String _reminderDescription = "";
   private int _reminderWakeUp = 0;//0 sveglia, //1 notifica
   private int _reminderHour = 0;//0-23
   private int _reminderMinutes = 0;//0-59
   private int _reminderDay = 1;//1-31
   private int _reminderMonth = 1;//1-12
   private int _reminderYear = 1;//1900-2200
   private int _reminderReoccurrence = 1;// 1 min, 2 hour, 3 days, 4 weeks, 5 month, 6 years
   private int _reminderReoccurrenceValues = 10;// values of _reminderReoccurrence example 30 = 30 minutes (max 525600)
   private int _reminderReoccurrenceCounter = 2;// numbers of _reminderReoccurrence
   private String _definedDate = new Date().toString();

   public Reminder(boolean _reminderDeleted, String _reminderDescription, int _reminderWakeUp, int _reminderHour, int _reminderMinutes, int _reminderDay, int _reminderMonth, int _reminderYear, int _reminderReoccurrence, int _reminderReoccurrenceValues, int _reminderReoccurrenceCounter, String _definedDate) {
      this._reminderDeleted = _reminderDeleted;
      this._reminderDescription = _reminderDescription;
      this._reminderWakeUp = _reminderWakeUp;
      this._reminderHour = checkHour(_reminderHour);
      this._reminderMinutes = checkMinutes(_reminderMinutes);
      this._reminderYear = checkYears(_reminderYear);
      this._reminderMonth = checkMonth(_reminderMonth);
      this._reminderDay = checkDay(_reminderDay,this._reminderMonth,this._reminderYear);
      this._reminderReoccurrence = checkValueReminderReoccurrence(_reminderReoccurrence);
      this._reminderReoccurrenceValues = checkValueReminderReoccurrenceValues(this._reminderReoccurrence,_reminderReoccurrenceValues);
      this._reminderReoccurrenceCounter = checkValueReminderReminderReoccurrenceCounter(this._reminderReoccurrence,this._reminderReoccurrenceValues,_reminderReoccurrenceCounter);
      this._definedDate = _definedDate;
   }
   //region checks values
   private int checkValueReminderReoccurrence(int _reminderReoccurrence){
      int tmpval = _reminderReoccurrence;
      if (tmpval< 0){
         tmpval = 0;
      } else if (tmpval >5){
         tmpval = 5;
      }
      return tmpval;
   }
   private int checkValueReminderReoccurrenceValues(int _reminderReoccurrence,int _reminderReoccurrenceValues){
      int tmpval = _reminderReoccurrenceValues;
      if( _reminderReoccurrence == 1 ){
         if( _reminderReoccurrenceValues > 525600 ){
            tmpval = 525600;
         }
      } else if( _reminderReoccurrence == 2 ){
         if( _reminderReoccurrenceValues > 8760 ){
            tmpval = 8760;
         }
      } else if( _reminderReoccurrence == 3 ){
         if( _reminderReoccurrenceValues > 365 ){
            tmpval = 365;
         }
      } else if( _reminderReoccurrence == 4 ){
         if( _reminderReoccurrenceValues > 12 ){
            tmpval = 12;
         }
      } else {
         //year e all
         tmpval = 1;
      }
      return tmpval;
   }
   private int checkValueReminderReminderReoccurrenceCounter(int _reminderReoccurrence,int _reminderReoccurrenceValues, int _reminderReoccurrenceCounter){
      int tmpval = _reminderReoccurrenceCounter;
      if( _reminderReoccurrence == 1 ){
         if( _reminderReoccurrenceCounter > Integer.divideUnsigned(525600,_reminderReoccurrenceValues) ){
            tmpval = Integer.divideUnsigned(525600,_reminderReoccurrenceValues);
         }
      } else if( _reminderReoccurrence == 2 ){
         if( _reminderReoccurrenceCounter > Integer.divideUnsigned(8760,_reminderReoccurrenceValues)){
            tmpval = Integer.divideUnsigned(8760,_reminderReoccurrenceValues);
         }
      } else if( _reminderReoccurrence == 3 ){
         if( _reminderReoccurrenceCounter > Integer.divideUnsigned(365,_reminderReoccurrenceValues) ){
            tmpval = Integer.divideUnsigned(365,_reminderReoccurrenceValues);
         }
      } else if( _reminderReoccurrence == 4 ){
         if( _reminderReoccurrenceCounter > Integer.divideUnsigned(12,_reminderReoccurrenceValues) ){
            tmpval = Integer.divideUnsigned(12,_reminderReoccurrenceValues);
         }
      } else {
         //year e all
         tmpval = 1;
      }
      return tmpval;
   }
   private int checkHour(int hour){
      int tmpval = hour;
      if (hour <0){
         tmpval = 0;
      } else if(hour>23){
         tmpval = 23;
      }
      return tmpval;
   }
   private int checkMinutes(int minutes){
      int tmpval = minutes;
      if (minutes <0){
         tmpval = 0;
      } else if(minutes>59){
         tmpval = 59;
      }
      return tmpval;
   }
   private int checkDay(int day,int month,int year){
      List<Integer> month31 = new ArrayList<>();
      month31.add(1);
      month31.add(3);
      month31.add(5);
      month31.add(7);
      month31.add(8);
      month31.add(10);
      month31.add(12);
      int tmpval = day;
      if (day <0){
         tmpval = 1;
      } else if(month31.contains(month)){//month with 31 days
         if (day>31){
            tmpval = 31;
         }
      } else if(month != 2){//not february
         if (day>30){
            tmpval = 30;
         }
      } else { //february
         float tmpYear = (float) year /4;
         if (tmpYear == round((float)year/4) || year == 1900){ //leap year
            if (day>29){
               tmpval = 29;
            }
         } else { //not leap year
            if (day>28){
               tmpval = 28;
            }
         }
      }
      return tmpval;
   }
   private int checkMonth(int month){
      int tmpval = month;
      if (month <1){
         tmpval = 1;
      } else if(month>12){
         tmpval = 12;
      }
      return tmpval;
   }
   private int checkYears(int year){
      int tmpval = year;
      if (year <1900){
         tmpval = 1900;
      } else if(year>2200){
         tmpval = 2200;
      }
      return tmpval;
   }
   //endregion

}
