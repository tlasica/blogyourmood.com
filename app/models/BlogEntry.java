package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class BlogEntry extends Model {

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
		DateFormat format = SimpleDateFormat.getDateTimeInstance();
		return format.format( this.tstamp.getTime() );
	}
	
	@Transactional
	public static void saveCurrentMoodInBlog(String blogPrivLink, String status) {
		Blog blog = Blog.findByPrivateLink( blogPrivLink );
		BlogEntry entry = new BlogEntry( Mood.fromString(status) );
		entry.blog = blog;
		entry.save();		
	}
	
	public static List<BlogEntry> loadBlogHistory(Blog blog, int limitEntries) {
		List<BlogEntry> entries = find.where()
				.eq("blog", blog)
				.orderBy("tstamp desc")
				.setMaxRows(limitEntries).findList();
		return entries;
	}

}
