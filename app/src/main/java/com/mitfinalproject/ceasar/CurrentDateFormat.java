package com.mitfinalproject.ceasar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CurrentDateFormat {

    private Calendar calendar;
    String  currentDate;

    public CurrentDateFormat(){
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
        currentDate = dateFormat.format(calendar.getTime());
    }

    public String getCurrentDate(){
        return currentDate;
    }
}
