package fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import model.Song;
import nyp.fypj.ninjatrack.R;
import adapter.SongListAdapter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
	private SongListAdapter adapter;
	private HashMap<String, MediaPlayer> mediaList;
	
	public RecorderFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_instrument, container, false);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        start_time = (TextView) rootView.findViewById(R.id.start_time);
        end_time = (TextView) rootView.findViewById(R.id.end_time);
        lv_song = (ListView) rootView.findViewById(R.id.lv_song);
        
        // Retrieve all the songs and sort accordingly
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
			@Override
			public int compare(Song a, Song b) {
				return a.getTitle().compareTo(b.getTitle());
			}
        });

        mediaList = new HashMap<String, MediaPlayer>();
        adapter = new SongListAdapter(getActivity().getApplicationContext(), songList);
        lv_song.setAdapter(adapter);
        
        lv_song.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final ImageView btn_pp = (ImageView) view.findViewById(R.id.btn_pp);
				final Song selectedSong = songList.get(position);
				
				// Check selected
				if(mediaList.keySet().contains(selectedSong.getTitle())){
					MediaPlayer mediaPlayer = mediaList.get(selectedSong.getTitle());
					
					// Pause playing
					if(mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
//						mediaPlayer.stop();
//						mediaList.remove(selectedSong.getTitle());
						
						btn_pp.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
					}
					// Resume playing
					else {
						mediaPlayer.start();
						
						btn_pp.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause));
					}
				}
				// Play new song
				else{
					MediaPlayer mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mediaPlayer.setDataSource(selectedSong.getData().getFileDescriptor(), selectedSong.getData().getStartOffset(), selectedSong.getData().getLength());
//						mediaPlayer.setDataSource(selectedSong.getData());
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mediaPlayer) {
								mediaPlayer.reset();
								try {
									mediaPlayer.setDataSource(selectedSong.getData().getFileDescriptor(), selectedSong.getData().getStartOffset(), selectedSong.getData().getLength());
									mediaPlayer.prepare();
									mediaPlayer.start();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalStateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
//								mediaPlayer.stop();
//								mediaList.remove(selectedSong.getTitle());
//								
//								btn_pp.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
							}
						});
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
		String[] songNames;
		
		AssetManager assetManager = getActivity().getAssets();
		try {
			songNames = assetManager.list("recorder");
			for(int i = 0; i < songNames.length; i++) {
				String title = songNames[i];
				AssetFileDescriptor data = getActivity().getAssets().openFd("recorder/" + songNames[i]);				
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
				mediaPlayer.prepare();
				int duration = mediaPlayer.getDuration();
				songList.add(new Song(title, duration, data));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Retrieve all song files
//		ContentResolver musicResolver = getActivity().getContentResolver();
//		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//		
//		if(musicCursor != null && musicCursor.moveToFirst()) {
//			int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
//			int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
//			int durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
//			int dataColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
//			
//			do {
//				int id = musicCursor.getInt(idColumn);
//				String title = musicCursor.getString(titleColumn);
//				int duration = Integer.parseInt(musicCursor.getString(durationColumn));
//				String data = musicCursor.getString(dataColumn);
//				songList.add(new Song(id, title, duration, data));
//			} while(musicCursor.moveToNext());
//		}
	}
}
