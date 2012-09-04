idpackage controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import models.*;

//TODO: match instead of ifs in blog.scala.html

public class Application extends Controller {
  
	private static final int DEF_HISTORY_LIMIT = 3;
  
	static Form<Blog> blogCreateForm = form(Blog.class);
  
  public static Result index() {
    return ok(index.render("Your new application is ready."));
  }
  
  public static Result register() {
	return ok(register.render(blogCreateForm));
  }

  public static Result createBlog() {
	Form<Blog> form = blogCreateForm.bindFromRequest();
	if ( form.hasErrors() ) {
		return badRequest( register.render(form) );
	} 
	else {
		Blog blog = form.get();
		blog = Blog.createAndSaveNewBlog(blog);
		return redirect(routes.Application.blog(blog.privateLink));		
	}
  }
  
  public static Result blog(String privateLink) {
	Blog blog = Blog.findByPrivateLink(privateLink);
	List<BlogEntry> blogHistory = null;
	blogHistory = BlogEntry.loadBlogHistory(blog, DEF_HISTORY_LIMIT);
	String currentMood = "?";
	if (!blogHistory.isEmpty()) {
		currentMood = blogHistory.get(0).mood.toString();
	}
	return ok(views.html.blog.render(blog, blogHistory, currentMood));
  }
  
  public static Result addStatus(String blogPrivLink, String mood) {
	  BlogEntry.saveCurrentMoodInBlog(blogPrivLink, mood);
	  return redirect(routes.Application.blog(blogPrivLink));		
  }

  public static Result blogPublicView(String publicLink) {
	  return TODO;
  }

  public static Result demo() {
		return this.blog("demo123");
	}
  
}