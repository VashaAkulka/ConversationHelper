package com.example.conversationhelper.time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStampConvertor {

    static public String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    static public String getHoursAndMinuets(String time) {
        return time.substring(11, 16);
    }
}
