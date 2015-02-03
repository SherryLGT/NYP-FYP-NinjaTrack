package adapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.Song;
import nyp.fypj.ninjatrack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class SongListAdapter extends ArrayAdapter<Song> {
	
	private Activity context;
	private ArrayList<Song> songs;
	
	private TextView tv_title, tv_duration;
	private ImageView btn_pp;
	
	public SongListAdapter(Activity context, ArrayList<Song> songs) {
		super(context, R.layout.song_list_item, songs);
		this.context = context;
		this.songs = songs;
	}

	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RelativeLayout layout = (RelativeLayout)context.getLayoutInflater().inflate(R.layout.song_list_item, null);
        tv_title = (TextView) layout.findViewById(R.id.tv_title);
        tv_duration = (TextView) layout.findViewById(R.id.tv_duration);
        btn_pp = (ImageView) layout.findViewById(R.id.btn_pp);
        
        Song currentSong = songs.get(position);
        tv_title.setText(currentSong.getTitle());
        if(currentSong.isPlaying()) {
        	btn_pp.setImageDrawable(layout.getResources().getDrawable(R.drawable.btn_pause));
        }
        else {
        	btn_pp.setImageDrawable(layout.getResources().getDrawable(R.drawable.btn_play));
        }
        
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
