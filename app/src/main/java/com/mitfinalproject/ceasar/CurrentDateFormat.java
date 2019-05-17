package com.mitfinalproject.ceasar;

import java.text.DateFormat;
import java.util.Calendar;

public class CurrentDateFormat {

    private Calendar calendar;
    String  currentDate;

    public CurrentDateFormat(){
        calendar = Calendar.getInstance();
        currentDate = java.text.DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
    }

    public String getCurrentDate(){
        return currentDate;
    }
}
