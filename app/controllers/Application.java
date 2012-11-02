package controllers;

import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import models.Blog;
import models.BlogEntry;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

//TODO: match instead of ifs in blog.scala.html

public class Application extends Controller {

	private static final int DEF_HISTORY_LIMIT = 3;

	static Form<BlogEntry>moodForm = form(BlogEntry.class);

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result features() {
		return ok(views.html.features.render());
	}
	
	public static Result showBlog(String privateLink) {
		return showBlogWithMessage(privateLink, null);
	}	
	
	public static Result showBlogWithMessage(String privateLink, String message) {
		Blog blog = Blog.findByPrivateLink(privateLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		List<BlogEntry> blogHistory = BlogEntry.loadBlogHistoryLimitedEntries(blog, DEF_HISTORY_LIMIT);
		return ok(views.html.blog.render(blog, blogHistory, moodForm, message));
	}
	
	public static Result addStatus(String blogPrivLink) {
		Form<BlogEntry> bindEntryForm = moodForm.bindFromRequest();
		if (bindEntryForm.hasErrors()) {
			Blog blog = Blog.findByPrivateLink(blogPrivLink);
			if(null==blog) {
				return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
			}
			List<BlogEntry> blogHistory = BlogEntry.loadBlogHistoryLimitedEntries(blog, DEF_HISTORY_LIMIT);
			Logger.error("Bad request when addingStatus");
			Logger.error(bindEntryForm.errors().toString());
			return badRequest(views.html.blog.render(blog, blogHistory, bindEntryForm, null));
		}
		else {		
			DynamicForm form = form().bindFromRequest();		
			String notes = form.get("notes");
			String moodName = form.get("CHOOSEN_MOOD");
					
			BlogEntry.saveCurrentMoodInBlog(blogPrivLink, moodName, notes);		
			String message = "Your mood has been saved";
			return showBlogWithMessage(blogPrivLink, message);
		}
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
	

	public static Result manifest() {
		ObjectNode result = Json.newObject();
		result.put("name", "Mood Blog");
		result.put("description", "Mood Blog is simple and light service which allows to track your happiness index by recording your emotions.");
		result.put("launch_path", "/");
		ObjectNode iconsInfo = Json.newObject();
		iconsInfo.put("128","/assets/images/icon_128.png");
		result.put("icons", iconsInfo);
		ObjectNode devInfo = Json.newObject();
		devInfo.put("name", "tlasica");
		devInfo.put("url", "www.3kawki.pl");
		result.put("developer", devInfo);
		result.put("default_locale", "en");
		return ok(result);
	}
}