package async;

import adapter.InstrumentHandler;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;

public class ContinuousPlay extends AsyncTask<Void, Void, Void>{
	
	private Activity activity;
	private int id;
	public boolean playOn;
	
	public ContinuousPlay(Activity activity, int id) {
		this.activity = activity;
		this.id = id;
		playOn = true;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		while(playOn){
			AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
			final float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			final float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume;
			InstrumentHandler.sp.play(id, streamVolume, streamVolume, 0, 0, 1);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
