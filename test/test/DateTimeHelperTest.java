package test;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import commons.DateTimeHelper;

public class DateTimeHelperTest {

	DateTimeHelper helper = new DateTimeHelper();
	
	@Test
	public void lastNumDateMidnightOne() {
		DateTime now = DateTime.now();
		assertEquals(1, helper.lastNumDateMidnight(now,1).size());
	}


	@Test
	public void lastNumDateMidnightTwoAreSequence() {
		DateTime now = DateTime.now();
		List<DateMidnight> days = helper.lastNumDateMidnight(now,2);
		assertEquals(2, days.size());
		assertEquals(days.get(1), days.get(0).minusDays(1));
	}

}
