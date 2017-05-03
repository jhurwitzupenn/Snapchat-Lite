package com.example.jordan.snapchat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

final class Utils {
    static String getCurrentDateAsString(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        return df.format(c.getTime());
    }

    private Utils(){}
}
