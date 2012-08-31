package models;

import java.util.UUID;
import java.util.List;

import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

// TODO: external logic class

@Entity
public class Blog extends Model {

	@Id
	public Long id;
	
	@Required
	@MaxLength(30)
	public String label;

	//TODO: unique
	public String privateLink;
	public String publicLink;	
	
	private void generateLinks() {
		privateLink = UUID.randomUUID().toString();
		publicLink = UUID.randomUUID().toString();	
	}
	
	public static Blog create(Blog blog) {
		blog.generateLinks();
		blog.save();
		return blog;
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static Finder<Long,Blog> find = new Finder(Long.class, Blog.class);
	
	public static Blog findByPrivateLink(String privateLink) {
		List<Blog> res = find.where().eq("privateLink", privateLink).findList();
		Blog blog = res.get(0);
		return blog;
	}
}
