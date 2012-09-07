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

import org.joda.time.LocalDate;

@Entity
public class BlogEntry extends Model {

	private static final DateFormat TIMESTAMP_FORMAT = SimpleDateFormat.getDateTimeInstance();
	private static final DateFormat TIME_FORMAT = SimpleDateFormat.getTimeInstance();
	
	@Id
	public Long 	id;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Blog	blog;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Mood	mood;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar	tstamp;
		
	public static Finder<Long,BlogEntry> find = new Finder(Long.class, BlogEntry.class);
	
	public BlogEntry(Mood mood) {
		this.tstamp = Calendar.getInstance();
		this.mood = mood;
	}
	
	public String timestampFormatted() {
		return TIMESTAMP_FORMAT.format( tstamp.getTime() );
	}
	
	public String timeStr() {
		return TIME_FORMAT.format(tstamp.getTime());
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
