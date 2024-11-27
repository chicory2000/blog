package com.snmp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static final int MILLIS_100 = 100;
    public static final int MILLIS_500 = 500;

    public static final int SECOND_0 = 0;
    public static final int SECOND_1 = 1000;
    public static final int SECOND_2 = 2000;
    public static final int SECOND_3 = 3000;
    public static final int SECOND_5 = 5000;
    public static final int SECOND_10 = 10000;
    public static final int SECOND_20 = 20000;
    public static final int SECOND_30 = 30000;

    public static final int MINUTE_1 = SECOND_1 * 60;
    public static final int MINUTE_2 = MINUTE_1 * 2;
    public static final int MINUTE_3 = MINUTE_1 * 3;
    public static final int MINUTE_5 = MINUTE_1 * 5;
    public static final int MINUTE_30 = MINUTE_1 * 30;

    public static final int HOUR_1 = MINUTE_1 * 60;
    public static final int HOUR_2 = HOUR_1 * 2;
    public static final int HOUR_3 = HOUR_1 * 3;
    public static final int HOUR_8 = HOUR_1 * 8;

    public static final long DAY_1 = HOUR_1 * 24;
    public static final long DAY_2 = DAY_1 * 2;
    public static final long DAY_3 = DAY_1 * 3;
    public static final long DAY_7 = DAY_1 * 7;

    public static final long YEAR_1 = DAY_1 * 365;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd",
            Locale.US);
    private static final String CLIENT_FORMAT = "yyyy-MM-dd HH:mm";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(CLIENT_FORMAT,
            Locale.US);

    public static boolean isTheSameDay(long earlier, long later) {
        return Math.abs(later - earlier) < DAY_1;
    }

    public static boolean isExceedLimitTime(long earlier, long later, long limitMillisecond) {
        return Math.abs(later - earlier) >= limitMillisecond;
    }

    public static synchronized String formatDate(long timeStamp) {
        Date date = new Date(timeStamp);
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(long timeStamp) {
        Date date = new Date(timeStamp);
        return formatDateTime(date);
    }

    public static synchronized String formatDateTime(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

}
