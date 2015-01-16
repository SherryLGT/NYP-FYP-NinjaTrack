package adapter;

import java.io.IOException;

import model.Pin;

import redbearprotocol.RBLProtocol;

import activity.MainActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.SparseArray;
import async.ContinuousPlay;

public abstract class InstrumentHandler {
	
	// No value -1
	public static int switch1_flag = -1;
	public static int switch2_flag = -1;
	public static int flex;
	public static int accx;
	
	public static String button1;
	public static String button2;
	public static String button3;
	public static String button4;
	public static String button5;
	public static String button6;
	public static String button7;
	public static String button8;
	public static String drum;
	public static boolean toPlay = true;

	public static ContinuousPlay sound1;
	public static ContinuousPlay sound2;
	public static ContinuousPlay sound3;
	public static ContinuousPlay sound4;
	public static ContinuousPlay sound5;
	public static ContinuousPlay sound6;
	public static ContinuousPlay sound7;
	public static ContinuousPlay sound8;
	
	public static void SetMode(RBLProtocol protocol, SharedPreferences sp, SparseArray<Pin> pins) {
		for(int i = 0; i < pins.size(); i++) {
			if(sp.contains(Integer.toString(pins.get(i).getPin()))) {
				protocol.setPinMode(pins.get(i).getPin(), sp.getInt(Integer.toString(pins.get(i).getPin()), 0));
			}
		}
	}
		
	public static void CheckFlags(MainActivity activity) {
		if(switch1_flag == 0 && switch2_flag == 0) {
			if(flex > 26) { // Recorder
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
				if(accx >= 530) { // Drum
					drum = "drum/Drum.mp3";
					try {
						if(toPlay) {
							PlaySound(activity, -1, 0);
							toPlay = false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					drum = "";
					toPlay = true;
				}
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
	
	public static void PlaySound(Activity activity, final int buttonNo, final int drumNo) throws IOException {
		String toPlayFile = null;
		
		if(buttonNo != -1) {
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
		}
		else if(drumNo != -1) {
			toPlayFile = drum;
		}
		
		if(toPlayFile != "" || !toPlayFile.equals("")) {
			if(buttonNo != -1){
				ContinuousPlay cp = new ContinuousPlay(activity, toPlayFile);
				switch(buttonNo) {
					case 1:
						sound1 = cp;
						sound1.execute();
						break;
					case 2:
						sound2 = cp;
						sound2.execute();
						break;
					case 3:
						sound3 = cp;
						sound3.execute();
						break;
					case 4:
						sound4 = cp;
						sound4.execute();
						break;
					case 5:
						sound5 = cp;
						sound5.execute();
						break;
					case 6:
						sound6 = cp;
						sound6.execute();
						break;
					case 7:
						sound7 = cp;
						sound7.execute();
						break;
					case 8:
						sound8 = cp;
						sound8.execute();
						break;
				}
			}
			else if(drumNo != -1){
				AssetFileDescriptor data = activity.getAssets().openFd(toPlayFile);				
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
				mediaPlayer.prepare();
//				int duration = mediaPlayer.getDuration();
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
	
	public static void StopSound(Activity activity, final int buttonNo) throws IOException {
		if(buttonNo != -1) {
			switch(buttonNo) {
				case 1:
					sound1.playOn = false;
					sound1.cancel(true);
					break;
				case 2:
					sound2.playOn = false;
					sound2.cancel(true);
					break;
				case 3:
					sound3.playOn = false;
					sound3.cancel(true);
					break;
				case 4:
					sound4.playOn = false;
					sound4.cancel(true);
					break;
				case 5:
					sound5.playOn = false;
					sound5.cancel(true);
					break;
				case 6:
					sound6.playOn = false;
					sound6.cancel(true);
					break;
				case 7:
					sound7.playOn = false;
					sound7.cancel(true);
					break;
				case 8:
					sound8.playOn = false;
					sound8.cancel(true);
					break;
			}
		}
	}
}
