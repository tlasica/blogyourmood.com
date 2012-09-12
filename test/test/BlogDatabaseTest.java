package test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import models.Blog;
import models.BlogEntry;

import org.junit.Test;

public class BlogDatabaseTest {

	@Test
	public void blogIsAccesibleByPublicLink() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog = Blog.createBlogWithGeneratedLinks("test blog");
				Blog.saveNewBlog(blog);
				Blog loaded = Blog.findByPublicLink(blog.publicLink);
				assertThat(loaded).isNotNull();
				assertThat(loaded.privateLink).isEqualTo(blog.privateLink);
			}
			
		});
	}
}
