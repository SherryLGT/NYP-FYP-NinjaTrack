package async;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;

public class ContinuousPlay extends AsyncTask<Void, Void, Void>{
	
	private Activity activity;
	private String file;
	public boolean playOn;
	
	public ContinuousPlay(Activity activity, String file) {
		this.activity = activity;
		this.file = file;
		playOn = true;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		while(playOn){
			AssetFileDescriptor data;
			try {
				data = activity.getAssets().openFd(file);
							
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
				mediaPlayer.prepare();
	//			int duration = mediaPlayer.getDuration();
				mediaPlayer.start();
				
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mediaPlayer) {
						mediaPlayer.release();
						mediaPlayer = null;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
