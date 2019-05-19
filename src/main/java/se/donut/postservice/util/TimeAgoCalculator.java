package se.donut.postservice.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoCalculator {

    private static final String SECOND = " second ago";
    private static final String SECONDS = " seconds ago";
    private static final String MINUTE = " minute ago";
    private static final String MINUTES = " minutes ago";
    private static final String HOUR = " hour ago";
    private static final String HOURS = " hours ago";
    private static final String DAY = " day ago";
    private static final String DAYS = " days ago";
    private static final String MONTH = " month ago";
    private static final String MONTHS = " months ago";
    private static final String YEAR = " year ago";
    private static final String YEARS = " years ago";

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
