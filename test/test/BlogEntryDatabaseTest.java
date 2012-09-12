package test;

import static org.junit.Assert.*;

import java.util.List;

import play.test.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import models.Blog;
import models.BlogEntry;
import models.Mood;

import org.joda.time.LocalDate;
import org.junit.Test;

public class BlogEntryDatabaseTest {

	@Test
	public void noEntriesAfterBlogCreation() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog = Blog.createBlogWithGeneratedLinks("test blog");
				Blog.saveNewBlog(blog);
				assertThat(BlogEntry.loadBlogHistoryLimitedEntries(blog, 1).size()).isEqualTo(0);
			}
			
		});
	}
	
	@Test
	public void oneEntryAfterBlogCreatedAndEntrySaved() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog = Blog.createBlogWithGeneratedLinks("test blog");
				Blog.saveNewBlog(blog);
				BlogEntry.saveCurrentMoodInBlog(blog.privateLink, "happy", "some notes");
				List<BlogEntry> history = BlogEntry.loadBlogHistoryLimitedEntries(blog, 100);
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).mood).isEqualTo(Mood.HAPPY);
				assertThat(history.get(0).notes).isEqualTo("some notes");
			}
			
		});		
	}


	@Test
	public void blogHistoryLimitedContainsOnlyOneBlog() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog1 = Blog.createBlogWithGeneratedLinks("test blog 1");
				Blog.saveNewBlog(blog1);
				Blog blog2 = Blog.createBlogWithGeneratedLinks("test blog 2");
				Blog.saveNewBlog(blog2);
				BlogEntry.saveCurrentMoodInBlog(blog1.privateLink, "happy", "some notes 1");
				BlogEntry.saveCurrentMoodInBlog(blog2.privateLink, "sad", "some notes 2");
				List<BlogEntry> history = BlogEntry.loadBlogHistoryLimitedEntries(blog1, 100);
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).mood).isEqualTo(Mood.HAPPY);
				assertThat(history.get(0).notes).isEqualTo("some notes 1");
			}
			
		});		
	}

	@Test
	public void blogHistoryPeriodContainsOnlyOneBlog() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog1 = Blog.createBlogWithGeneratedLinks("test blog 1");
				Blog.saveNewBlog(blog1);
				Blog blog2 = Blog.createBlogWithGeneratedLinks("test blog 2");
				Blog.saveNewBlog(blog2);
				BlogEntry.saveCurrentMoodInBlog(blog1.privateLink, "happy", "some notes 1");
				BlogEntry.saveCurrentMoodInBlog(blog2.privateLink, "sad", "some notes 2");
				List<BlogEntry> history = BlogEntry.loadBlogHistoryForPeriod(blog1, LocalDate.now(), LocalDate.now());
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).mood).isEqualTo(Mood.HAPPY);
				assertThat(history.get(0).notes).isEqualTo("some notes 1");
			}
			
		});		
	}

}
