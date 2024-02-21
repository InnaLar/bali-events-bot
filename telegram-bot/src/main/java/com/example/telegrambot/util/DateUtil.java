package com.example.telegrambot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

@UtilityClass
public class DateUtil {

    public static boolean isContainsTextMonth(final String messageText) {
        return DateUtil.getMonthNumber(messageText) > 0;
    }

    public static int getMonthNumber(final String text) {
        if (text.toUpperCase(Locale.ENGLISH).contains("JAN")) {
            return 1;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("FEB")) {
            return 2;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("MAR")) {
            return 3;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("APR")) {
            return 4;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("MAY")) {
            return 5;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("JUN")) {
            return 6;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("JUL")) {
            return 7;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("AUG")) {
            return 8;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("SEP")) {
            return 9;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("OCT")) {
            return 10;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("NOV")) {
            return 11;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("DEC")) {
            return 12;
        } else {
            return 0;
        }
    }

    public static boolean isCalendarMonthChanged(final String messageText) {
        return DateUtil.getFullMonthNumber(messageText) > 0;
    }

    public static int getFullMonthNumber(final String text) {
        if (text.toUpperCase(Locale.ENGLISH).contains("JANUARY")) {
            return 1;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("FEBRUARY")) {
            return 2;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("MARCH")) {
            return 3;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("APRIL")) {
            return 4;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("MAY")) {
            return 5;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("JUNE")) {
            return 6;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("JULY")) {
            return 7;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("AUGUST")) {
            return 8;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("SEPTEMBER")) {
            return 9;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("OCTOBER")) {
            return 10;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("NOVEMBER")) {
            return 11;
        } else if (text.toUpperCase(Locale.ENGLISH).contains("DECEMBER")) {
            return 12;
        } else {
            return 0;
        }
    }

    public static boolean isDateSelected(final String text) {
        final String datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}";
        return Pattern.matches(datePattern, text)
            || DateUtil.isContainsTextMonth(text);
    }

    public static String convertToLocalDateSelected(final String text, final LocalDate currentLocalDate) {
        final int year = currentLocalDate.getYear();
        final int month = DateUtil.getMonthNumber(text);
        final int day = getDayFromText(text);

        return String.format("%02d.%02d.%d", day, month, year);
    }

    public static String convertToDateTimeCalendarMonthChanged(final String text, final LocalDate currentLocalDate) {
        final int day = currentLocalDate.getDayOfMonth();
        final int monthNumber = DateUtil.getMonthNumber(text);
        final int year = adjustYearForMonthTransition(currentLocalDate.getMonthValue(), monthNumber, currentLocalDate.getYear());

        return String.format("%02d.%02d.%d", day, monthNumber, year);
    }

    private static int adjustYearForMonthTransition(final int currentMonth, final int newMonth, final int currentYear) {
        if (currentMonth == 12 && newMonth == 1) {
            return currentYear + 1;
        } else if (currentMonth == 1 && newMonth == 12) {
            return currentYear - 1;
        }
        return currentYear;
    }

    private static int getDayFromText(final String text) {
        final int firstTwoDigits = Integer.parseInt(text.substring(0, 2));
        return (firstTwoDigits > 0 && firstTwoDigits < 32) ? firstTwoDigits : 0;
    }

}
