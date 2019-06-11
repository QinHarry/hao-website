package com.hao.website.blog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateKit {

    static Logger logger = LoggerFactory.getLogger(DateKit.class);

    public static final int INTERVAL_DAY = 1;
    public static final int INTERVAL_WEEK = 2;
    public static final int INTERVAL_MONTH = 3;
    public static final int INTERVAL_YEAR = 4;
    public static final int INTERVAL_HOUR = 5;
    public static final int INTERVAL_MINUTE = 6;
    public static final int INTERVAL_SECOND = 7;

    public static final Date tempDate = new Date((new Long("-2177481952000")).longValue());

    private static List<SimpleDateFormat> dateFormats = new ArrayList(12) {
        private static final long serialVersionUID = 2249396579858199535L;

        {
            this.add(new SimpleDateFormat("yyyy-MM-dd"));
            this.add(new SimpleDateFormat("yyyy/MM/dd"));
            this.add(new SimpleDateFormat("yyyy.MM.dd"));
            this.add(new SimpleDateFormat("yyyy-MM-dd HH:24:mm:ss"));
            this.add(new SimpleDateFormat("yyyy/MM/dd HH:24:mm:ss"));
            this.add(new SimpleDateFormat("yyyy.MM.dd HH:24:mm:ss"));
            this.add(new SimpleDateFormat("M/dd/yyyy"));
            this.add(new SimpleDateFormat("dd.M.yyyy"));
            this.add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
            this.add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
            this.add(new SimpleDateFormat("dd.MMM.yyyy"));
            this.add(new SimpleDateFormat("dd-MMM-yyyy"));
        }
    };

    public DateKit() {
    }

    public static int getCurrentUnixTime() {
        return getUnixTimeByDate(new Date());
    }

    public static int getUnixTimeByDate(Date date) {
        return (int) (date.getTime() / 1000L);
    }

    public static Date dateFormat(String date, String dateFormat) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            try {
                return format.parse(date);
            } catch (Exception e) {
                logger.info("Fail to format date");
            }
        }
        return null;
    }

    public static Date dateFormat(String date) {
        return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateFormat(Date date, String dateFormat) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(date);
        }
        return "";
    }

    public static String formatDateByUnixTime(Timestamp unixTime, String dateFormat) {
        return dateFormat(new Date(unixTime.getTime()), dateFormat);
    }
}
