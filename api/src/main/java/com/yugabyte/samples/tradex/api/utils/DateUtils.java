package com.yugabyte.samples.tradex.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static Calendar fromYYYMMDD(String input) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(input));
        return calendar;
    }
}
