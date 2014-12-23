package fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import model.Song;
import nyp.fypj.ninjatrack.R;
import adapter.SongListAdapter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecorderFragment extends Fragment {
	
	private ProgressBar progress_bar;
	private TextView start_time, end_time;
	private ListView lv_song;
	
	private ArrayList<Song> songList;
	SongListAdapter adapter;
	private HashMap<String, MediaPlayer> mediaList;
	
	public RecorderFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_recorder, container, false);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        start_time = (TextView) rootView.findViewById(R.id.start_time);
        end_time = (TextView) rootView.findViewById(R.id.end_time);
        lv_song = (ListView) rootView.findViewById(R.id.lv_song);
        
        // Retrieve all the songs and sort accordingly
        getSongList();
        mediaList = new HashMap<String, MediaPlayer>();
        Collections.sort(songList, new Comparator<Song>() {
			@Override
			public int compare(Song a, Song b) {
				return a.getTitle().compareTo(b.getTitle());
			}
        });
        
        adapter = new SongListAdapter(getActivity().getApplicationContext(), songList);
        lv_song.setAdapter(adapter);
        lv_song.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				Song selectedSong = songList.get(position);
				
				if(mediaList.keySet().contains(selectedSong.getTitle())){
					MediaPlayer mediaPlayer = mediaList.get(selectedSong.getTitle());
					mediaPlayer.stop();
					mediaList.remove(selectedSong.getTitle());
					
					ImageView btn_pp = (ImageView) view.findViewById(R.id.btn_pp);
					btn_pp.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
				}
				else{
					MediaPlayer mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mediaPlayer.setDataSource(selectedSong.getData());
						mediaPlayer.prepare();
						mediaPlayer.start();
						mediaList.put(selectedSong.getTitle(), mediaPlayer);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					ImageView btn_pp = (ImageView) view.findViewById(R.id.btn_pp);
					btn_pp.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
				}
			}
        	
        });
        
        progress_bar.setProgress(20);
        start_time.setText("0:00");
        end_time.setText("3:40");
         
        return rootView;
    }
	
	private void getSongList() {
		songList = new ArrayList<Song>();
		
		ContentResolver musicResolver = getActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		
		if(musicCursor != null && musicCursor.moveToFirst()) {
			int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			int dataColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			
			do {
				int id = musicCursor.getInt(idColumn);
				String title = musicCursor.getString(titleColumn);
				int duration = Integer.parseInt(musicCursor.getString(durationColumn));
				String data = musicCursor.getString(dataColumn);
				songList.add(new Song(id, title, duration, data));
			} while(musicCursor.moveToNext());
		}
	}
}
