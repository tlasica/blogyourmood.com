package commons;

import java.util.ArrayList;
import java.util.List;

import models.Blog;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeHelper {

	public String durationString(int numDays) {
		switch (numDays) {
		case 1:
			return "day";
		case 7:
			return "week";
		case 14:
			return "2 weeks";
		case 30:
			return "month";
		case 60:
			return "2 months";
		default:
			return String.valueOf(numDays) + " days";
		}
	}
	
	public static DateTime datetimeFromISOWithTZ(String iso, String timezoneID) {
		DateTimeZone tz = DateTimeZone.forID(timezoneID);
		DateTimeFormatter format = ISODateTimeFormat.basicDateTimeNoMillis().withZone(tz);
		return DateTime.parse(iso, format); 		
	}
		

	public List<LocalDate> listOfLastNDays(DateTime now, int numDays) {
		List<LocalDate> dates = new ArrayList<LocalDate>();
		for (int i = 0; i < numDays; i++) {
			LocalDate d = now.minusDays(i).toLocalDate();
			dates.add(d);
		}
		return dates;
	}
	
	public List<DateMidnight> lastNumDateMidnight(DateTime now, int numDays) {
		List<DateMidnight> days = new ArrayList<DateMidnight>();
		for (int i = 0; i < numDays; i++) {
			DateMidnight d = new DateMidnight(now.minusDays(i));
			days.add(d);
		}
		return days;
	}
	
	public DateTime getBlogNow(Blog blog) {
		return new DateTime().withZone(blog.getTimeZone());		
	}
	
	public DateTime getBlogNowMinusDays(DateTime now, int numdays) {
		return new DateMidnight( now.minusDays(numdays-1)).toDateTime();
	}
	
	
}
