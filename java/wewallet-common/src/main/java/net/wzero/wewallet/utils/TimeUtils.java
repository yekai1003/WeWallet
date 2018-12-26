package net.wzero.wewallet.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
	public static final int SECONDS_IN_DAY= 86400;//60*60*24;
	public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
	public static final DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat formatSN = new SimpleDateFormat("yyyyMMdd");
	public static final DateFormat formatWeiXin = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String getNowDateTime() {
		return formatDateTime.format(new Date());
	}
	public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }
	public static Date after(int second) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}
	private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }
	public static int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthday)) {  
            throw new IllegalArgumentException(  
                    "The birthDay is before Now.It's unbelievable!");  
        }
		int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        cal.setTime(birthday);  
  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;
	}
	
	public static Date millisToDate(Long millionSeconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millionSeconds);
		return c.getTime();
	}
	
	public static Date parseStringDate(String date) throws ParseException {
		return formatDate.parse(date);
	}
	
	public static Date afterMonth(Date when,int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(when);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}
	
	public static String dateTimeToString(Date dateTime) {
		return formatDateTime.format(dateTime);
	}
}
