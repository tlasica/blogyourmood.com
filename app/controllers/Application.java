package controllers;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import models.*;

public class Application extends Controller {
  
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
		blog = Blog.create(blog);
		return redirect(routes.Application.blog(blog.privateLink));		
	}
  }
  
  public static Result blog(String privateLink) {
	Blog blog = Blog.findByPrivateLink(privateLink);
	//TODO: load all requred data - list of last 3
	return ok(views.html.blog.render(blog));
  }
  
}