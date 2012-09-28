package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Blog;
import models.BlogEntry;
import models.CalendarEntry;
import models.Mood;
import models.StatsEntry;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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

	
	public static Result stats(String blogPrivLink, String numDays) {
		int days = Integer.valueOf(numDays);
		Blog blog = Blog.findByPrivateLink(blogPrivLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		List<StatsEntry> stats = buildStats(blog, days); 
		String durationStr = DTHELPER.durationString(days);
		return ok(views.html.stats.render(blog, stats, durationStr));
	}
	
	
	private static List<StatsEntry> buildStats(Blog blog, int numDays) {
		Map<Mood, StatsEntry> moodCounters = new HashMap<Mood, StatsEntry>();
		for(Mood m : Mood.values()) {
			moodCounters.put(m, new StatsEntry(m,0L));			
		}
		
		long total = 0;
		List<BlogEntry> history = loadBlogHistoryFromNow(blog, numDays);
		for(BlogEntry e: history) {
			if (e.mood != null) {
				moodCounters.get(e.mood).count++;
				total++;
			}
		}			
		
		List<StatsEntry> stats = new ArrayList<StatsEntry>();
		stats.add( moodCounters.get(Mood.HAPPY).withUpdatedPercent(total));
		stats.add( moodCounters.get(Mood.NORMAL).withUpdatedPercent(total));
		stats.add( moodCounters.get(Mood.SAD).withUpdatedPercent(total));
		stats.add( moodCounters.get(Mood.ANGRY).withUpdatedPercent(total));
		return stats;		
	}

	private static List<CalendarEntry> buildCalendar(Blog blog, int numDays) {
		DateTime now = new DateTime().withZone(blog.getTimeZone());
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

	private static Map<LocalDate, List<BlogEntry>> 
	loadBlogHistoryPerDay( Blog blog, DateTime now, int numDays) {
		List<BlogEntry> moodHistory = loadBlogHistoryFromNow(blog, numDays);		
		Map<LocalDate, List<BlogEntry>> res = new HashMap<LocalDate, List<BlogEntry>>();
		for (BlogEntry m : moodHistory) {	// tstamps are in UTC
			LocalDate day = m.tstamp.withZone(blog.getTimeZone()).toLocalDate();
			if (!res.containsKey(day)) {
				res.put(day, new ArrayList<BlogEntry>());
			}
			res.get(day).add(m);
		}
		return res;
	}
	
	private static List<BlogEntry> loadBlogHistoryFromNow(Blog blog, int numDays) {
		DateTime now = new DateTime().withZone(blog.getTimeZone());
		DateTime fromDate = new DateMidnight( now.minusDays(numDays-1)).toDateTime();		
		return BlogEntry.loadBlogHistoryForPeriod(blog, fromDate, now);				
	}
}
