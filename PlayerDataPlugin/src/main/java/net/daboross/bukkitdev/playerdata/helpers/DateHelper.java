/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.playerdata.helpers;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author daboross
 */
public class DateHelper {

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
     * @return A visually nice relative date.
     */
    public static String relativeFormat(long millis) {
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
}
