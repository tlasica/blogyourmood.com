package test;

import static org.junit.Assert.*;

import models.Blog;

import org.junit.Test;

public class BlogTest {

	@Test
	public void publicLinkIsDifferentFromPrivate() {
		Blog a = Blog.createBlogWithGeneratedLinks("test blog");
		a.generateLinks();
		assertFalse( a.privateLink == a.publicLink);
	}

}
