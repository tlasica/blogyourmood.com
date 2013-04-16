package controllers;

import models.Blog;
import play.mvc.Controller;
import play.mvc.Result;

public class Feedback extends Controller {

	private static final String BLOG_LINK = "feedback123";
	
	public static Result showBlog() {
		createBlogIfNotExists();
		return redirect(routes.Application.showBlog(BLOG_LINK));
	}

	public static void createBlogIfNotExists() {
		Blog blog = Blog.findByPrivateLink(BLOG_LINK);
		if (null == blog) {
			blog = new Blog();
			blog.label = "Feedback: share your suggestions";
			blog.privateLink = BLOG_LINK;
			blog.publicLink = BLOG_LINK + "public";
			Blog.saveNewBlog(blog);
		}
	}
	
}
