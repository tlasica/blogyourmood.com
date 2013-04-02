package controllers;

import models.Mood;

public class StatsEntry {

	public Mood		mood;
	public	long	count;
	public	double	percent;
	
	public StatsEntry(Mood mood, long count) {
		this.mood = mood;
		this.count = count;
	}
	
	public StatsEntry withUpdatedPercent(long total) {
		if (total > 0) {
			percent = ( 100.0 * count ) / (double) total;
		}
		else {
			percent = 0.0;
		}
		return this;
	}
	
	public String percentStr() {
		return String.format("%.2f", percent);
	}
}
