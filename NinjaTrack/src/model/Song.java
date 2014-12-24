package model;

import android.content.res.AssetFileDescriptor;

public class Song {

	private int id, duration;
	private String title;
	private AssetFileDescriptor data;
	
	public Song() {};
	
	public Song(String title, int duration, AssetFileDescriptor data) {
		this.title = title;
		this.duration = duration;
		this.data = data;
	}
	
	public Song(int id, String title, int duration, AssetFileDescriptor data) {
		this.id = id;
		this.title = title;
		this.duration = duration;
		this.data = data;
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

	public AssetFileDescriptor getData() {
		return data;
	}

	public void setData(AssetFileDescriptor data) {
		this.data = data;
	}
}
