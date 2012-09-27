package models;

public class StatsEntry {

	public Mood		mood;
	public	long	count;
	public	double	percent;
	
	public StatsEntry(Mood mood, long count) {
		this.mood = mood;
		this.count = count;
	}
	
	public StatsEntry withUpdatedPercent(long total) {
		percent = ( 100.0 * count ) / (double) total;
		return this;
	}
}
