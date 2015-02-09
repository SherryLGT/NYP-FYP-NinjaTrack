package async;

import activity.MainActivity;
import adapter.InstrumentHandler;
import android.os.AsyncTask;

public class ContinuousPlay extends AsyncTask<Void, Void, Void>{
	
	private MainActivity activity;
	private int id;
	private String file;
	public boolean playOn;
	
	public ContinuousPlay(MainActivity activity, int id) {
		this.activity = activity;
		this.id = id;
		playOn = true;
	}
	
	public ContinuousPlay(MainActivity activity, String file) {
		this.activity = activity;
		this.file = file;
		playOn = true;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		while(playOn){
			InstrumentHandler.sp.play(id, 1, 1, 0, 0, 1);
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			AssetFileDescriptor data;
//			try {
//				data = activity.getAssets().openFd(file);
//							
//				final MediaPlayer mediaPlayer = new MediaPlayer();
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//				mediaPlayer.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
//				mediaPlayer.prepare();
////				int duration = mediaPlayer.getDuration();
//				mediaPlayer.start();
//				
//				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//					@Override
//					public void onCompletion(MediaPlayer mediaPlayer) {
//						mediaPlayer.release();
//						mediaPlayer = null;
//					}
//				});
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				Thread.sleep(30);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		return null;
	}
}
