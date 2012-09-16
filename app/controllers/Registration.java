package controllers;

import models.Blog;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.register;

public class Registration extends Controller {

	static Form<Blog> blogCreateForm = form(Blog.class);

	public static Result showRegisterPage() {
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
			return redirect(routes.Registration.showWelcomeBlogPage(blog.privateLink));
			// return redirect(routes.Application.showBlog(blog.privateLink));
		}
	}


	public static Result showWelcomeBlogPage(String privLink) {
		Blog blog = Blog.findByPrivateLink(privLink);
		if(null==blog) {
			return play.mvc.Results.internalServerError("Ooops. Invalid URL. Blog with this id does not exists.");
		}
		return ok(views.html.welcome.render(blog));		
	}
	
}
