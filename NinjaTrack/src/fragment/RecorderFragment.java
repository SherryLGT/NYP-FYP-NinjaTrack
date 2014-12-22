package fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import model.Song;
import nyp.fypj.ninjatrack.R;
import adapter.SongListAdapter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecorderFragment extends Fragment {
	
	private ProgressBar progress_bar;
	private TextView start_time, end_time;
	private ListView lv_song;
	
	private ArrayList<Song> songList;
	
	public RecorderFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_recorder, container, false);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        start_time = (TextView) rootView.findViewById(R.id.start_time);
        end_time = (TextView) rootView.findViewById(R.id.end_time);
        lv_song = (ListView) rootView.findViewById(R.id.lv_song);
        
        songList = getSongList();
        Collections.sort(songList, new Comparator<Song>() {
			@Override
			public int compare(Song a, Song b) {
				return a.getTitle().compareTo(b.getTitle());
			}
        });
        
        SongListAdapter adapter = new SongListAdapter(getActivity().getApplicationContext(), songList);
        lv_song.setAdapter(adapter);
        
        progress_bar.setProgress(20);
        start_time.setText("0:00");
        end_time.setText("3:40");
         
        return rootView;
    }
	
	private ArrayList<Song> getSongList() {
		ArrayList<Song> tempSongList = new ArrayList<Song>();
		
		ContentResolver musicResolver = getActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		
		if(musicCursor != null && musicCursor.moveToFirst()) {
			int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			
			do {
				int id = musicCursor.getInt(idColumn);
				String title = musicCursor.getString(titleColumn);
				int duration = Integer.parseInt(musicCursor.getString(durationColumn));
				tempSongList.add(new Song(id, title, duration));
			} while(musicCursor.moveToNext());
		}
		
		return tempSongList;
	}
}
