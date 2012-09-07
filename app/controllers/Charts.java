package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Blog;
import models.BlogEntry;
import models.CalendarEntry;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import commons.DateTimeHelper;

import play.mvc.Controller;
import play.mvc.Result;

public class Charts extends Controller {

	private static final DateTimeHelper DTHELPER = new DateTimeHelper();
	
	public static Result timechart(String blogPrivLink, String days) {
		return TODO;
	}

	public static Result histogram(String blogPrivLink, String days) {
		return TODO;
	}

	public static Result calendar(String blogPrivLink, String numDays) {
		int days = Integer.valueOf(numDays);
		Blog blog = Blog.findByPrivateLink(blogPrivLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		List<CalendarEntry> calendar = buildCalendar(blog, days);
		String durationStr = DTHELPER.durationString(days);
		return ok(views.html.calendar.render(blog, calendar, durationStr));
	}

	private static List<CalendarEntry> buildCalendar(Blog blog, int numDays) {
		LocalDate now = new LocalDate();
		DateTimeFormatter format = DateTimeFormat.fullDate();
		Map<LocalDate, List<BlogEntry>> historyPerDay = loadBlogHistoryPerDay(blog, now, numDays);
		List<CalendarEntry> res = new ArrayList<CalendarEntry>();
		List<LocalDate> dates = DTHELPER.listOfLastNDays(now, numDays);
		for (LocalDate d : dates) {
			CalendarEntry e = new CalendarEntry(d.toDate());
			e.dateStr = d.toString(format);
			if (historyPerDay.containsKey(d)) {
				e.moods = historyPerDay.get(d);
			}
			res.add(e);
		}
		return res;
	}

	private static Map<LocalDate, List<BlogEntry>> loadBlogHistoryPerDay(
			Blog blog, LocalDate now, int numDays) {
		Map<LocalDate, List<BlogEntry>> res = new HashMap<LocalDate, List<BlogEntry>>();
		List<BlogEntry> moodHistory = BlogEntry.loadBlogHistoryForPeriod(blog, now.minusDays(numDays), now); 
		for (BlogEntry m : moodHistory) {
			LocalDate day = m.tstamp.toLocalDate();
			if (!res.containsKey(day)) {
				res.put(day, new ArrayList<BlogEntry>());
			}
			res.get(day).add(m);
		}
		return res;
	}
}
