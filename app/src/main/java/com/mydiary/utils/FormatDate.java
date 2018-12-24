package com.mydiary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatDate {
    public static String updateFormatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH);
        return dateFormat.format(date);
    }
}
