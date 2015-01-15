package adapter;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public abstract class InstrumentHandler {
	
	// No value -1
	public static int switch1_flag = -1;
	public static int switch2_flag = -1;
	public static int flex_flag = -1;
	
	public static String button1;
	public static String button2;
	public static String button3;
	public static String button4;
	public static String button5;
	public static String button6;
	public static String button7;
	public static String button8;
		
	public static void checkFlags() {
		if(switch1_flag == 0 && switch2_flag == 0) {
			if(flex_flag > 26) { // Recorder
				button1 = "recorder/Recorder 1.mp3";
				button2 = "recorder/Recorder 2.mp3";
				button3 = "recorder/Recorder 3.mp3";
				button4 = "recorder/Recorder 4.mp3";
				button5 = "recorder/Recorder 5.mp3";
				button6 = "recorder/Recorder 6.mp3";
				button7 = "recorder/Recorder 7.mp3";
				button8 = "recorder/Recorder 8.mp3";
			}
			else { // Saxophone
				button1 = "saxophone/Saxophone 1.mp3";
				button2 = "saxophone/Saxophone 2.mp3";
				button3 = "saxophone/Saxophone 3.mp3";
				button4 = "saxophone/Saxophone 4.mp3";
				button5 = "saxophone/Saxophone 5.mp3";
				button6 = "saxophone/Saxophone 6.mp3";
				button7 = "saxophone/Saxophone 7.mp3";
				button8 = "saxophone/Saxophone 8.mp3";
			}
		}
		else {
			button1 = "";
			button2 = "";
			button3 = "";
			button4 = "";
			button5 = "";
			button6 = "";
			button7 = "";
			button8 = "";

			if(switch1_flag == 0 && switch2_flag == 1) {
				// Drum
			}
			else if(switch1_flag == 1 && switch2_flag == 0) {
				// if.. TODO: Include flex checking
				if(true) { // Bell
					
				}
				else { // Harp
					
				}
			}
		}
	}
	
	public static void PlaySound(Activity activity, final int buttonNo) throws IOException {
		String toPlayFile = null;
		
		switch(buttonNo) {
			case 1:
				toPlayFile = button1;
				break;
			case 2:
				toPlayFile = button2;
				break;
			case 3:
				toPlayFile = button3;
				break;
			case 4:
				toPlayFile = button4;
				break;
			case 5:
				toPlayFile = button5;
				break;
			case 6:
				toPlayFile = button6;
				break;
			case 7:
				toPlayFile = button7;
				break;
			case 8:
				toPlayFile = button8;
				break;
		}
		
		if(toPlayFile != "") {
			AssetFileDescriptor data = activity.getAssets().openFd(toPlayFile);				
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
		}
	}
}
