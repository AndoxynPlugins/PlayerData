/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author daboross
 */
public class StaticHelper {

    /**
     * Get a visually nice date from a timestamp. Acts like: 4 years, 2 months,
     * 1 day, 10 hours, 30 minutes, and 9 seconds (That is just a random string
     * of numbers I came up with, but that is what the formating is like) Will
     * emit any terms that are 0, eg, if 0 days, then it would be 4 years, 2
     * months, 10 hours, 30 minutes, and 9 seconds Will put a , between all
     * terms and also a , and between the last term and the second to last term.
     * would do 4 years, 2 months and 10 hours returns now if
     *
     * @param millis the millisecond value to turn into a date string
     * @return A visually nice date. "Not That Long" if millis == 0;
     */
    public static String getFormattedDate(long millis) {
        if (millis == 0) {
            return "Not That Long";
        }
        long years, days, hours, minutes, seconds;

        years = TimeUnit.MILLISECONDS.toDays(millis) / 365;
        days = TimeUnit.MILLISECONDS.toDays(millis);
        hours = TimeUnit.MILLISECONDS.toHours(millis);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        minutes -= TimeUnit.HOURS.toMinutes(hours);
        hours -= TimeUnit.DAYS.toHours(days);
        days %= 365;
        StringBuilder resultBuilder = new StringBuilder();
        if (years > 0) {
            resultBuilder.append(years).append(years == 1 ? " year" : " years");
            if (days > 0) {
                resultBuilder.append(" and ");
            }
        }
        if (days > 0) {
            resultBuilder.append(days).append(days == 1 ? " day" : " days");
            if (hours > 0 && years <= 0) {
                resultBuilder.append(" and ");
            }
        }
        if (years <= 0) {
            if (hours > 0) {
                resultBuilder.append(hours).append(hours == 1 ? " hour" : " hours");
                if (minutes > 0 && days <= 0) {
                    resultBuilder.append(" and ");
                }
            }
            if (days <= 0) {
                if (minutes > 0) {
                    resultBuilder.append(minutes).append(minutes == 1 ? " minute" : " minutes");
                } else if (seconds > 0 && hours <= 0) {
                    resultBuilder.append(seconds).append(seconds == 1 ? " second" : " seconds");
                }
            }
        }
        return resultBuilder.toString();
    }

    public static String getCombinedString(String[] array, int start) {
        if (array == null || start >= array.length || start < 0) {
            throw new IllegalArgumentException();
        } else if (start + 1 == array.length) {
            return array[start];
        } else {
            StringBuilder sb = new StringBuilder(array[start]);
            for (int i = start + 1; i < array.length; i++) {
                sb.append(" ").append(array[i]);
            }
            return sb.toString();
        }
    }
}
