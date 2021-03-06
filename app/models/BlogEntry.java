package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

@SuppressWarnings("serial")
@Entity
public class BlogEntry extends Model {

	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("E dd MMMM HH:mm");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");
	
	@Id
	public Long 	id;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Blog	 blog;
	
	@Required
	@Enumerated(EnumType.STRING)
	public Mood	 mood;
	
	@Temporal(TemporalType.TIMESTAMP)
	public DateTime	tstamp;
	
	@MaxLength(200)
	public String notes;
		
	public static Finder<Long,BlogEntry> find = new Finder<Long, BlogEntry>(Long.class, BlogEntry.class);
	
	public BlogEntry(Mood mood) {
		this.tstamp = DateTime.now();
		this.mood = mood;
	}
	
	public String timestampLocalStr(DateTimeZone zone) {
		return tstamp.toString( DATETIME_FORMAT.withZone(zone) );
	}
	
	public String timeStr(DateTimeZone zone) {
		return tstamp.toString( TIME_FORMAT.withZone(zone) );
	}
	
	@Transactional
	public static void saveCurrentMoodInBlog(String blogPrivLink, String status, String notes) {
		Blog blog = Blog.findByPrivateLink( blogPrivLink );
        if (blog != null) {
		    BlogEntry entry = new BlogEntry( Mood.fromString(status) );
		    entry.blog = blog;
		    entry.notes = notes;
		    entry.save();
        }
        else throw new IllegalArgumentException("Blog with this ID does not exists.");
	}

			
	public static List<BlogEntry> loadBlogHistoryLimitedEntries(Blog blog, int limitEntries) {
		List<BlogEntry> entries = find.where()
				.eq("blog", blog)
				.orderBy("tstamp desc")
				.setMaxRows(limitEntries).findList();
		return entries;
	}
	
	public static List<BlogEntry> loadBlogHistoryForPeriod(Blog blog, DateTime fromDT, DateTime toDT) {
		List<BlogEntry> entries = find.where()
				.eq("blog", blog)
				.gt("tstamp", fromDT)
				.lt("tstamp", toDT)
				.orderBy("tstamp desc")
				.findList();
		return entries;
	}
	
	public static Map<Mood,Integer> loadMoodGroupedByForPeriod(Blog blog, DateTime fromDT, DateTime toDT) {
		// run query
		String sql = "select mood, count(1) as cnt from blog_entry where blog_id=:blogId and tstamp>=:fromDT and tstamp<=:toDT group by mood";
		SqlQuery query = Ebean.createSqlQuery(sql)
			.setParameter("blogId", blog.id)
			.setParameter("fromDT", fromDT)
			.setParameter("toDT", toDT);
		List<SqlRow> rows =	query.findList();
		// parse result and build return
		Map<Mood,Integer> map = new HashMap<Mood, Integer>();
		for(SqlRow row : rows) {
			String moodStr = row.getString("mood");
			Integer count = row.getInteger("cnt");
			map.put(Mood.fromString(moodStr), count);
		}
		return map;
	}
	
	//
	// TEST HELPERS
	//
	
	public static void saveTestEntry(Blog blog, String status, String notes, String isoDateTime) {
		BlogEntry entry = new BlogEntry( Mood.fromString(status) );
		entry.blog = blog;
		entry.notes = notes;
		entry.tstamp = DateTime.parse(isoDateTime, 
				ISODateTimeFormat.basicDateTimeNoMillis().withZone(DateTimeZone.forID(blog.timezone)));
		entry.save();				
	}
	
	
}
