package adapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nyp.fypj.ninjatrack.R;

import model.Song;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class SongListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Song> songs;
	private LayoutInflater inflater;
	
	public SongListAdapter(Context context, ArrayList<Song> songs) {
		this.context = context;
		this.songs = songs;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.song_list_item, null);
        TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
        TextView tv_duration = (TextView) layout.findViewById(R.id.tv_duration);
        ImageView iv_button = (ImageView) layout.findViewById(R.id.iv_button);
        
        Song currentSong = songs.get(position);
        tv_title.setText(currentSong.getTitle());
        
        long millis = currentSong.getDuration();
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        if(sec < 10) {
        	tv_duration.setText(min + ":0" + sec);
        }
        else {
        	tv_duration.setText(min + ":" + sec);
        }
		
		return layout;
	}

}
