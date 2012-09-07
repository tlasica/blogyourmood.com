package models;

import play.data.validation.Constraints.MaxLength;

public class BlogEntryNotes {

	@MaxLength(100)
	public String	notes;
}
