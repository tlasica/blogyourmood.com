package models;

import java.util.UUID;
import java.util.List;

import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@SuppressWarnings("serial")
@Entity
public class Blog extends Model {

	@Id
	public Long id;
	
	@Required
	@MaxLength(30)
	public String label;

	@Column(unique=true)
	public String privateLink;
	
	@Column(unique=true)
	public String publicLink;

	public String timezone ;
	
	public DateTime createdOn = DateTime.now();
	
	public DateTimeZone getTimeZone() {
		if (timezone != null ) {
			return DateTimeZone.forID(timezone);
		}
		else {
			return DateTimeZone.forID("Europe/Warsaw");
		}
	}
	
	public void generateLinks() {
		privateLink = UUID.randomUUID().toString();
		publicLink = UUID.randomUUID().toString();	
	}
	
	public static Blog createTestBlog(String label) {
		Blog b = new Blog();
		b.label = label;
		b.generateLinks();
		return b;
	}

	public static Blog createTestBlogWithTimeZone(String label, String timezoneID) {
		Blog b = createTestBlog(label);
		b.timezone = timezoneID;
		return b;
	}
	
	@Transactional	
	public static Blog saveNewBlog(Blog blog) {
		blog.save();
		return blog;
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	private static Finder<Long,Blog> find = new Finder<Long,Blog>(Long.class, Blog.class);
	
	public static Blog findByPrivateLink(String privateLink) {
		List<Blog> res = find.where().eq("privateLink", privateLink).findList();
		return (res!=null && !res.isEmpty() ) ? res.get(0) : null;
	}

	public static Blog findByPublicLink(String publicLink) {
		List<Blog> res = find.where().eq("publicLink", publicLink).findList();
		return (res!=null && !res.isEmpty() ) ? res.get(0) : null;
	}

}
