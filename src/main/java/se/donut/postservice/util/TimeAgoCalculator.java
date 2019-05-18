package se.donut.postservice.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoCalculator {

    private static final String SECONDS = " seconds ago";
    private static final String MINUTES = " minutes ago";
    private static final String HOURS = " hours ago";
    private static final String DAYS = " days ago";
    private static final String MONTHS = " months ago";
    private static final String YEARS = " years ago";

    public static String calculateTimeAgo(Date past, Date now) {
        long diff = now.getTime() - past.getTime();
        long value;
        if ((value = TimeUnit.MILLISECONDS.toDays(diff)) >= 365) {
            return value / 365 + YEARS;
        } else if (value >= 30) {
            return value / 30 + MONTHS;
        } else if (value >= 1) {
            return value + DAYS;
        } else if ((value = TimeUnit.MILLISECONDS.toHours(diff)) >= 1) {
            return value + HOURS;
        } else if ((value = TimeUnit.MILLISECONDS.toMinutes(diff)) >= 1) {
            return value + MINUTES;
        } else if ((value = TimeUnit.MILLISECONDS.toSeconds(diff)) >= 1) {
            return value + SECONDS;
        } else {
            return "now";
        }
    }
}
