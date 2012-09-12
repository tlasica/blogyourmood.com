package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import models.*;

//TODO: match instead of ifs in blog.scala.html

public class Application extends Controller {

	private static final int DEF_HISTORY_LIMIT = 3;
	private static final String DEMO_BLOG_LINK = "demo123";

	static Form<Blog> blogCreateForm = form(Blog.class);
	static Form<BlogEntry>moodForm = form(BlogEntry.class);

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result register() {
		return ok(register.render(blogCreateForm));
	}

	public static Result createBlog() {
		Form<Blog> form = blogCreateForm.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(register.render(form));
		} else {
			Blog blog = form.get();
			blog.generateLinks();
			blog = Blog.saveNewBlog(blog);
			return redirect(routes.Application.showBlog(blog.privateLink));
		}
	}

	public static Result showBlog(String privateLink) {
		return showBlogWithMessage(privateLink, null);
	}
	
	public static Result showBlogWithMessage(String privateLink, String message) {
		Blog blog = Blog.findByPrivateLink(privateLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		List<BlogEntry> blogHistory = null;
		blogHistory = BlogEntry.loadBlogHistoryLimitedEntries(blog, DEF_HISTORY_LIMIT);
		return ok(views.html.blog.render(blog, blogHistory, moodForm, message));		
	}
	
	public static Result addStatus(String blogPrivLink) {
		
		DynamicForm form = form().bindFromRequest();
		String notes = form.get("notes");
		String moodName = form.get("CHOOSEN_MOOD");
				
		BlogEntry.saveCurrentMoodInBlog(blogPrivLink, moodName, notes);		
		String message = "Your mood has been saved";
		return showBlogWithMessage(blogPrivLink, message);
	}

	public static Result demo() {
		createDemoBlogIfNotExists();
		return redirect(routes.Application.showBlog(DEMO_BLOG_LINK));
	}

	public static Result showPublic(String pubLink) {
		Blog blog = Blog.findByPublicLink(pubLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		List<BlogEntry> history = BlogEntry.loadBlogHistoryLimitedEntries(blog, 1);
		BlogEntry curr = history.isEmpty() ? null : history.get(0);
		return ok(views.html.publicview.render(blog, curr));		
	}
	
	public static void createDemoBlogIfNotExists() {
		Blog blog = Blog.findByPrivateLink(DEMO_BLOG_LINK);
		if (null == blog) {
			blog = new Blog();
			blog.label = "Demo Version - try it!";
			blog.privateLink = DEMO_BLOG_LINK;
			blog.publicLink = DEMO_BLOG_LINK + "public";
			Blog.saveNewBlog(blog);
		}
	}

}