package com.hybris.easyjet.fixture.hybris.helpers;

import org.apache.commons.lang3.time.DateUtils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dwebb on 11/16/2016.
 */
public class DateFormat {

    private Date date;

    public static String getDate() {
        return getDate(0);
    }

    public static String getDate(int diff) {
        return DateFormat.getDate(Calendar.YEAR);
    }

    public static String getDate(int calendarUnit, int diff) {
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(calendarUnit, diff);
        return f.format(cal.getTime());
    }

    public static String getDateFromSpecificFormat(String dateValue, java.text.DateFormat fromFormat) throws ParseException {
        Date date = fromFormat.parse(dateValue);
        java.text.DateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
        return toFormat.format(date);
    }

    public static String getDate(String dateValue) throws ParseException {
        java.text.DateFormat fromFormat = new SimpleDateFormat("E dd-MMMM-yyyy HH-mm-ss");
        Date date = fromFormat.parse(dateValue);
        java.text.DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        return toFormat.format(date);
    }

    public static String getDateInSpecificFormat(String dateValue) throws ParseException {
        java.text.DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.text.DateFormat toFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm");
        Date date = fromFormat.parse(dateValue);
        return toFormat.format(date);
    }

    public String asUK() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

    public String asYearMonthDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public DateFormat today() {
        date = new Date();
        return this;
    }

    public String addDay(int days) throws ParseException {
        Format f = new SimpleDateFormat("dd-MM-yyy");
        return addDay(days, f.format(date));
    }

    public String addDay(int days, String date) throws ParseException {
        Format f = new SimpleDateFormat("dd-MM-yyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("dd-MM-yyy").parse(date));
        cal.add(Calendar.DAY_OF_YEAR, days);
        return f.format(cal.getTime());
    }

    public DateFormat withDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("E dd-MMM-yyyy HH-mm-ss");
        try {
            this.date = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static Calendar getDateCalender(String date) {

        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(getDateFormatterWithDay().parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calender;
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
    }

    public static Date getDateF(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss");
            Date actualDate = sdf.parse(date);
        return actualDate;
    }

}
