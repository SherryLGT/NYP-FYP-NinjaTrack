package model;

public class Song {

	private int id, duration;
	private String title;
	
	public Song() {};
	
	public Song(String title, int duration) {
		this.title = title;
		this.duration = duration;
	}
	
	public Song(int id, String title, int duration) {
		this.id = id;
		this.title = title;
		this.duration = duration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
