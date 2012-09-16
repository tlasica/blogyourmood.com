package controllers;

import models.Blog;
import play.mvc.Controller;
import play.mvc.Result;

public class DemoBlog extends Controller {

	private static final String DEMO_BLOG_LINK = "demo123";
	
	public static Result demo() {
		createDemoBlogIfNotExists();
		return redirect(routes.Application.showBlog(DEMO_BLOG_LINK));
	}

	public static void createDemoBlogIfNotExists() {
		Blog blog = Blog.findByPrivateLink(DEMO_BLOG_LINK);
		if (null == blog) {
			blog = new Blog();
			blog.label = "Shared Demo Version";
			blog.privateLink = DEMO_BLOG_LINK;
			blog.publicLink = DEMO_BLOG_LINK + "public";
			Blog.saveNewBlog(blog);
		}
	}
	
}
