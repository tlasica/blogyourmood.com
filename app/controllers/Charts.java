package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Blog;
import models.BlogEntry;
import models.Mood;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.mvc.Controller;
import play.mvc.Result;

import commons.DateTimeHelper;

public class Charts extends Controller {

	private static final DateTimeHelper DTHELPER = new DateTimeHelper();
	private static final String ERROR_PAGE_STR = "Ooops. Invalid URL. Blog with this id does not exists.";
	
	public static Result timechart(String blogPrivLink, String days) {
		return TODO;
	}

	public static Result histogram(String blogPrivLink, String days) {
		return TODO;
	}

	//
	// Scatter view = 2 dimensional chart : date x time => mood
	//
	
	public static Result scatter(String blogPrivLink, String numDays) {
		int days = Integer.valueOf(numDays);
		Blog blog = Blog.findByPrivateLink(blogPrivLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError(ERROR_PAGE_STR);
		}
		// Load data
		DateTime now = DTHELPER.getBlogNow(blog);
		DateTime fromDate = DTHELPER.getBlogNowMinusDays(now, days);		
		List<BlogEntry> blogEntries = BlogEntry.loadBlogHistoryForPeriod(blog, fromDate, now);
		String durationStr = DTHELPER.durationString(days);
		return ok(views.html.scatter.render(blog, blogEntries, durationStr, days));
	}
	
	
	//
	// Calendar==History View
	//
	
	public static Result calendar(String blogPrivLink, String numDays) {
		int days = Integer.valueOf(numDays);
		Blog blog = Blog.findByPrivateLink(blogPrivLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError(ERROR_PAGE_STR);
		}
		List<CalendarEntry> calendar = buildCalendar(blog, days);
		String durationStr = DTHELPER.durationString(days);
		return ok(views.html.calendar.render(blog, calendar, durationStr));
	}

	private static List<CalendarEntry> buildCalendar(Blog blog, int numDays) {
		DateTime now = DTHELPER.getBlogNow(blog);
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
	
	//
	// Stats View (grouped by mood)
	//
	
	public static Result stats(String blogPrivLink, String numDays) {
		int days = Integer.valueOf(numDays);
		Blog blog = Blog.findByPrivateLink(blogPrivLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError(ERROR_PAGE_STR);
		}
		List<StatsEntry> stats = buildStats(blog, days); 
		String durationStr = DTHELPER.durationString(days);
		return ok(views.html.stats.render(blog, stats, durationStr));
	}
	
	
	private static List<StatsEntry> buildStats(Blog blog, int numDays) {
		// Load Data
		DateTime now = DTHELPER.getBlogNow(blog);
		DateTime from = DTHELPER.getBlogNowMinusDays(now, numDays);
		Map<Mood, Integer> data = BlogEntry.loadMoodGroupedByForPeriod(blog, from, now);
		
		// Count Total
		long total = 0;
		for( Integer c : data.values() ) {
			if (c!=null) total += c;
		}
		
		// Build return statistics
		List<StatsEntry> stats = new ArrayList<StatsEntry>();
		for(Mood mood : Mood.values() ) {
			Integer count = data.get(mood);
			StatsEntry e = new StatsEntry(mood, (count!=null) ? count.longValue() : 0) 
				.withUpdatedPercent(total);
			stats.add( e );
		}
		return stats;		
	}


	private static Map<LocalDate, List<BlogEntry>> 
	loadBlogHistoryPerDay( Blog blog, DateTime now, int numDays) {
		// Load data
		DateTime fromDate = DTHELPER.getBlogNowMinusDays(now, numDays);		
		List<BlogEntry> moodHistory = BlogEntry.loadBlogHistoryForPeriod(blog, fromDate, now);
		// Group data by day
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
			
}
