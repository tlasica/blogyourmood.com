package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import models.Blog;
import models.BlogEntry;
import models.CalendarEntry;
import play.mvc.Controller;
import play.mvc.Result;


public class Charts extends Controller {

	  public static Result timechart(String blogPrivLink, String days) {
		  return TODO;
	  }

	  public static Result histogram(String blogPrivLink, String days) {
		  return TODO;
	  }

	  //TODO: obsługa braku blogu
	  public static Result calendar(String blogPrivLink, String numDays) {
			int days = Integer.valueOf(numDays);
			Blog blog = Blog.findByPrivateLink(blogPrivLink);
			List<CalendarEntry> calendar = buildCalendar(blog, days);
			Period period = new Period(days);
			String durationStr = durationString(days);
			return ok(views.html.calendar.render(blog, calendar, durationStr));
	  }

	private static String durationString(int numDays) {
		switch (numDays) {
		case 1: return "day";
		case 7: return "week";
		case 14: return "2 weeks";
		case 30: return "month";
		case 60: return "2 months";
		default: return String.valueOf(numDays) + " days";
		}
	}

	private static List<CalendarEntry> buildCalendar(Blog blog, int numDays) {
		DateTimeFormatter format = DateTimeFormat.fullDate();
		Map<LocalDate,List<BlogEntry>> historyPerDay = loadBlogHistoryPerDay(blog, numDays);
		List<CalendarEntry> res = new ArrayList<CalendarEntry>();
		LocalDate now = new LocalDate();
		List<LocalDate> dates = listOfLastNDays(now, numDays);		
		for(LocalDate d: dates) {
			CalendarEntry e = new CalendarEntry(d.toDate());
			e.dateStr = d.toString( format );
			if (historyPerDay.containsKey(d)) {
				e.moods = historyPerDay.get(d);
			}
			res.add( e );
		}
		return res;
	}
	
	private static List<LocalDate> listOfLastNDays(LocalDate now, int numDays) {
		List<LocalDate> dates = new ArrayList<LocalDate>();
		for (int i=0; i < numDays; i++) {
		    LocalDate d = now.minusDays( i );
		    dates.add(d);
		}
		return dates;
	}

	// TODO: limit by date not by #entries
	private static Map<LocalDate,List<BlogEntry>> loadBlogHistoryPerDay(Blog blog, int numDays ) {
		Map<LocalDate,List<BlogEntry>> res = new HashMap<LocalDate, List<BlogEntry>>();
		List<BlogEntry> moodHistory = BlogEntry.loadBlogHistory(blog, 100);
		for(BlogEntry m : moodHistory) {
			LocalDate day = LocalDate.fromCalendarFields(m.tstamp);
			if ( !res.containsKey(day) ) {
				res.put(day, new ArrayList<BlogEntry>());
			}
			res.get(day).add( m );
		}
		return res;
	}
}
