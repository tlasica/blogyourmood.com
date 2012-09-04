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

	@Column(unique=true)
	public String privateLink;
	
	@Column(unique=true)
	public String publicLink;	
	
	public void generateLinks() {
		privateLink = UUID.randomUUID().toString();
		publicLink = UUID.randomUUID().toString();	
	}
	
	@Transactional	
	public static Blog saveNewBlog(Blog blog) {
		blog.save();
		return blog;
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static Finder<Long,Blog> find = new Finder(Long.class, Blog.class);
	
	public static Blog findByPrivateLink(String privateLink) {
		List<Blog> res = find.where().eq("privateLink", privateLink).findList();
		if (res!=null && !res.isEmpty() ) {
			return res.get(0);
		}
		else {
			return null;
		}
	}

}
