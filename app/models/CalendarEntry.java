package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarEntry {

	public Date				date;
	public String			dateStr;
	public List<BlogEntry>	moods = new ArrayList<BlogEntry>();
	
	
	public CalendarEntry(Date d) {
		date = d;
	}
	
	public void addEntry(BlogEntry e) {
		moods.add(e);
	}
	
}
