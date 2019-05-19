package se.donut.postservice.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoCalculator {

    private static final String SECOND = " second";
    private static final String SECONDS = " seconds";
    private static final String MINUTE = " minute";
    private static final String MINUTES = " minutes";
    private static final String HOUR = " hour";
    private static final String HOURS = " hours";
    private static final String DAY = " day";
    private static final String DAYS = " days";
    private static final String MONTH = " month";
    private static final String MONTHS = " months";
    private static final String YEAR = " year";
    private static final String YEARS = " years";

    public static String calculateTimeAgo(Date past, Date now) {
        long diff = now.getTime() - past.getTime();
        long value;
        if ((value = TimeUnit.MILLISECONDS.toDays(diff)) >= 365) {
            return (value /= 365) + (value == 1 ? YEAR : YEARS);
        } else if (value >= 30) {
            return (value /= 30) + (value == 1 ? MONTH : MONTHS);
        } else if (value >= 1) {
            return value + (value == 1 ? DAY : DAYS);
        } else if ((value = TimeUnit.MILLISECONDS.toHours(diff)) >= 1) {
            return value + (value == 1 ? HOUR : HOURS);
        } else if ((value = TimeUnit.MILLISECONDS.toMinutes(diff)) >= 1) {
            return value + (value == 1 ? MINUTE : MINUTES);
        } else if ((value = TimeUnit.MILLISECONDS.toSeconds(diff)) >= 1) {
            return value + (value == 1 ? SECOND : SECONDS);
        } else {
            return "now";
        }
    }
}
