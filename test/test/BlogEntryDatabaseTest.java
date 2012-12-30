package test;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import models.Blog;
import models.BlogEntry;
import models.Mood;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import commons.DateTimeHelper;

public class BlogEntryDatabaseTest {

	@Test
	public void noEntriesAfterBlogCreation() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog = Blog.createTestBlog("test blog");
				Blog.saveNewBlog(blog);
				assertThat(BlogEntry.loadBlogHistoryLimitedEntries(blog, 1).size()).isEqualTo(0);
			}
			
		});
	}
	
	@Test
	public void oneEntryAfterBlogCreatedAndEntrySaved() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				Blog blog = Blog.createTestBlog("test blog");
				Blog.saveNewBlog(blog);
				BlogEntry.saveCurrentMoodInBlog(blog.privateLink, "happy", "some notes");
				List<BlogEntry> history = BlogEntry.loadBlogHistoryLimitedEntries(blog, 100);
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).mood).isEqualTo(Mood.HAPPY);
				assertThat(history.get(0).notes).isEqualTo("some notes");
			}
			
		});		
	}


	private Blog saveTestBlog(String name) {
		Blog blog = Blog.createTestBlog("test blog 1");
		Blog.saveNewBlog(blog);
		return blog;
	}
	
	@Test
	public void blogHistoryLimitedContainsOnlyOneBlog() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {				
				Blog blog1 = saveTestBlog("test blog 1");
				Blog blog2 = saveTestBlog("test blog 2");
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
				// create test blogs
				Blog blog1 = saveTestBlog("test blog 1");
				Blog blog2 = saveTestBlog("test blog 2");
				BlogEntry.saveCurrentMoodInBlog(blog1.privateLink, "happy", "some notes 1");
				BlogEntry.saveCurrentMoodInBlog(blog2.privateLink, "sad", "some notes 2");
				// create dates: now and -5d
				DateTime now = new DateTime().withZone(blog1.getTimeZone());
				DateTime fiveDaysAgo = new DateMidnight( now.minusDays(5)).toDateTime();		
				List<BlogEntry> history = BlogEntry.loadBlogHistoryForPeriod(blog1, fiveDaysAgo, now);
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).mood).isEqualTo(Mood.HAPPY);
				assertThat(history.get(0).notes).isEqualTo("some notes 1");
			}			
		});		
	}
	
	@Test
	public void blogHistoryPeriodContainsEntryForOneDay() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				String TZID = "Asia/Tokyo";
				Blog blog = Blog.createTestBlogWithTimeZone("test blog 1", TZID);
				Blog.saveNewBlog(blog);
				BlogEntry.saveTestEntry(blog, "happy", null, "20120831T235900Z");
				BlogEntry.saveTestEntry(blog, "happy", null, "20120901T000100Z");
				BlogEntry.saveTestEntry(blog, "happy", null, "20120902T000100Z");
				DateTime sept1 = DateTimeHelper.datetimeFromISOWithTZ("20120901T000000Z", TZID);
				DateTime sept2 = sept1.plusDays(1);
				List<BlogEntry> history = BlogEntry.loadBlogHistoryForPeriod(blog, sept1, sept2);
				assertThat(history.size()).isEqualTo(1);
				assertThat(history.get(0).tstamp.isEqual(DateTimeHelper.datetimeFromISOWithTZ("20120901T000100Z", TZID)));
			}			
		});			
	}

}
