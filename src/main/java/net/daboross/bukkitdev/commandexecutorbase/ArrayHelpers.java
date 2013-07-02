/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.commandexecutorbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author daboross
 */
public class ArrayHelpers {

    public static final String[] EMPTY_STRING = {};

    public static String[] copyArray(String[] array) {
        if (array.length == 0) {
            return EMPTY_STRING;
        }
        String[] copy = new String[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    public static String[] copyAndInclude(String[] array, String... includedItems) {
        String[] copy = new String[array.length + includedItems.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        System.arraycopy(includedItems, 0, copy, array.length, includedItems.length);
        return copy;
    }

    public static Set<String> copyToSet(String[] array) {
        return new HashSet<String>(Arrays.asList(array));
    }

    public static Set<String> copyToSetLowercase(String[] array) {
        Set<String> result = new HashSet<String>();
        for (String str : array) {
            result.add(str.toLowerCase(Locale.ENGLISH));
        }
        return result;
    }

    public static List<String> copyToList(String[] array) {
        return new ArrayList<String>(Arrays.asList(array));
    }

    public static List<String> copyToListLowercase(String[] array) {
        List<String> result = new ArrayList<String>();
        for (String str : array) {
            result.add(str.toLowerCase(Locale.ENGLISH));
        }
        return result;
    }

    public static List<String> singleStringList(String string) {
        List<String> result = new ArrayList<String>();
        result.add(string);
        return result;
    }

    public static String[] getSubArray(String[] array, int startPos, int length) {
        if (startPos + length > array.length) {
            throw new ArrayIndexOutOfBoundsException("startPos + length > array.length");
        } else if (startPos < 0) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0");
        } else if (length < 0) {
            throw new ArrayIndexOutOfBoundsException("length < 0");
        } else if (length == 0) {
            return EMPTY_STRING;
        }
        String[] copy = new String[length];
        System.arraycopy(array, startPos, copy, 0, length);
        return copy;
    }

    public static String combinedWithSeperator(Object[] array, String seperator) {
        if (array.length == 0) {
            return "";
        } else if (array.length == 1) {
            return String.valueOf(array[0]);
        } else {
            StringBuilder resultBuilder = new StringBuilder(String.valueOf(array[0]));
            for (int i = 1; i < array.length; i++) {
                resultBuilder.append(seperator).append(array[i]);
            }
            return resultBuilder.toString();
        }
    }
}
