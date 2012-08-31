package models;

import play.data.validation.Constraints.*;

public class Blog {

	public Long id;
	@Required
	@MaxLength(30)
	public String label;

	public static Blog create(Blog blog) {
		return blog;
	}
}