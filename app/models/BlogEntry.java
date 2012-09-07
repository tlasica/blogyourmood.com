package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Entity
public class BlogEntry extends Model {

	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("E dd MMMM HH:mm");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");
	
	@Id
	public Long 	id;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Blog	blog;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Mood	 mood;
	
	@Temporal(TemporalType.TIMESTAMP)
	public DateTime	tstamp;
		
	public static Finder<Long,BlogEntry> find = new Finder(Long.class, BlogEntry.class);
	
	public BlogEntry(Mood mood) {
		this.tstamp = DateTime.now();
		this.mood = mood;
	}
	
	public String timestampFormatted() {
		return tstamp.toString(DATETIME_FORMAT);
	}
	
	public String timeStr() {
		return tstamp.toString(TIME_FORMAT);
	}
	
	@Transactional
	public static void saveCurrentMoodInBlog(String blogPrivLink, String status) {
		Blog blog = Blog.findByPrivateLink( blogPrivLink );
		BlogEntry entry = new BlogEntry( Mood.fromString(status) );
		entry.blog = blog;
		entry.save();		
	}
	
	public static List<BlogEntry> loadBlogHistoryLimitedEntries(Blog blog, int limitEntries) {
		List<BlogEntry> entries = find.where()
				.eq("blog", blog)
				.orderBy("tstamp desc")
				.setMaxRows(limitEntries).findList();
		return entries;
	}
	
	public static List<BlogEntry> loadBlogHistoryForPeriod(Blog blog, LocalDate fromDate, LocalDate toDate) {
		List<BlogEntry> entries = find.where()
				.gt("tstamp", fromDate.minusDays(1))
				.lt("tstamp", toDate.plusDays(1))
				.orderBy("tstamp desc")
				.findList();
		return entries;
	}
	
}
