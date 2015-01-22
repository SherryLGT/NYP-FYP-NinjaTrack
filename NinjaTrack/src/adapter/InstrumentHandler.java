package adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import model.Pin;

import redbearprotocol.RBLProtocol;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import async.ContinuousPlay;

public abstract class InstrumentHandler {
	
	public static final int RECORDER_FLAG = 0x00;
	public static final int SAXOPHONE_FLAG = 0x01;
	public static final int DRUM_FLAG = 0x02;
	public static final int BELL_FLAG = 0x03;
	public static final int HARP_FLAG = 0x04;
	
	public static int switch1_flag = -1; // No value -1
	public static int switch2_flag = -1; // No value -1
	public static int flex;
	public static int accx;
	public static Deque<Integer> accx_stack = new ArrayDeque<Integer>();
	
	public static String button1;
	public static String button2;
	public static String button3;
	public static String button4;
	public static String button5;
	public static String button6;
	public static String button7;
	public static String button8;
	public static String drum;
	public static String bell;
	
	public static boolean enableDrum = true; // Within boundary
	public static boolean resetDrum = true; // Played before or not
	public static boolean enableBell = true; // Within boundary
	public static boolean resetBell = true; // Played before or not
	public static boolean readAccx = false;
	
	public static ContinuousPlay sound1;
	public static ContinuousPlay sound2;
	public static ContinuousPlay sound3;
	public static ContinuousPlay sound4;
	public static ContinuousPlay sound5;
	public static ContinuousPlay sound6;
	public static ContinuousPlay sound7;
	public static ContinuousPlay sound8;
	
	public static String filename;
	private static MediaRecorder mediaRecorder;
	private static File folder, audioFile = null;
	
	public static void SetMode(RBLProtocol protocol, SharedPreferences sp, SparseArray<Pin> pins) {
		for(int i = 0; i < pins.size(); i++) {
			if(sp.contains(Integer.toString(pins.get(i).getPin()))) {
				protocol.setPinMode(pins.get(i).getPin(), sp.getInt(Integer.toString(pins.get(i).getPin()), 0));
			}
		}
	}
	
	public static int CheckFlags() {
		
		if(switch1_flag == 0 && switch2_flag == 0) {
			if(flex > 26) { // Recorder
				return RECORDER_FLAG;
			}
			else { // Saxophone
				return SAXOPHONE_FLAG;
			}
		}
		else {
			if(switch1_flag == 0 && switch2_flag == 1) {
				// Drum
				if(accx >= 520) {
					enableDrum = true;
				}
				else {
					enableDrum = false;
					resetDrum = false;
				}
				
				return DRUM_FLAG;
			}
			else if(switch1_flag == 1 && switch2_flag == 0) {
				if(IsMoving()) { // Bell
					if(accx >= 460) {
						enableBell = true;
					}
					else {
						enableBell = false;
						resetBell = false;
					}
					
					return BELL_FLAG;
				}
				else { // Harp
					return HARP_FLAG;
				}
			}
		}
		return -1;
	}
	
	public static void SetSound() throws IOException {
		int flag = CheckFlags();
		
		switch(flag) {
			case RECORDER_FLAG:
				button1 = "recorder/Recorder 1.mp3";
				button2 = "recorder/Recorder 2.mp3";
				button3 = "recorder/Recorder 3.mp3";
				button4 = "recorder/Recorder 4.mp3";
				button5 = "recorder/Recorder 5.mp3";
				button6 = "recorder/Recorder 6.mp3";
				button7 = "recorder/Recorder 7.mp3";
				button8 = "recorder/Recorder 8.mp3";
				break;
				
			case SAXOPHONE_FLAG:
				button1 = "saxophone/Saxophone 1.mp3";
				button2 = "saxophone/Saxophone 2.mp3";
				button3 = "saxophone/Saxophone 3.mp3";
				button4 = "saxophone/Saxophone 4.mp3";
				button5 = "saxophone/Saxophone 5.mp3";
				button6 = "saxophone/Saxophone 6.mp3";
				button7 = "saxophone/Saxophone 7.mp3";
				button8 = "saxophone/Saxophone 8.mp3";
				break;
				
			case DRUM_FLAG:
				drum = "drum/Drum.mp3";
				break;
				
			case BELL_FLAG:
				button1 = "handbell/Bell.mp3";
				button2 = "handbell/Bell.mp3";
				button3 = "handbell/Bell.mp3";
				button4 = "handbell/Bell.mp3";
				button5 = "handbell/Bell.mp3";
				button6 = "handbell/Bell.mp3";
				button7 = "handbell/Bell.mp3";
				button8 = "handbell/Bell.mp3";
				break;
				
			case HARP_FLAG:
				button1 = "harp/Harp.mp3";
				button2 = "harp/Harp.mp3";
				button3 = "harp/Harp.mp3";
				button4 = "harp/Harp.mp3";
				button5 = "harp/Harp.mp3";
				button6 = "harp/Harp.mp3";
				button7 = "harp/Harp.mp3";
				button8 = "harp/Harp.mp3";
				break;
		}
	}
	
	public static void PlaySound(Activity activity, final int buttonNo) throws IOException {
		
		int flag = CheckFlags();
		String toPlayFile = null;
		
		if(flag == DRUM_FLAG) {
			if(enableDrum && !resetDrum) {
				toPlayFile = drum;				
				resetDrum = true;
			}
			else {
				return;
			}
		}
		else if(flag == BELL_FLAG) {			
			switch(buttonNo) {
				case 1:
					bell = button1;
					break;
				case 2:
					bell = button2;
					break;
				case 3:
					bell = button3;
					break;
				case 4:
					bell = button4;
					break;
				case 5:
					bell = button5;
					break;
				case 6:
					bell = button6;
					break;
				case 7:
					bell = button7;
					break;
				case 8:
					bell = button8;
					break;
			}
			if(enableBell && !resetBell) {
				toPlayFile = bell;				
				resetBell = true;
			}
			else {
				return;
			}
		}
		else if(flag == RECORDER_FLAG || flag == SAXOPHONE_FLAG || flag == HARP_FLAG) {
			enableBell = false;
			
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
				default:
					return;
			}
			
			if(flag == RECORDER_FLAG || flag == SAXOPHONE_FLAG) {
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
				
				return;
			}
		}
		else {
			return;
		}
		
		if(toPlayFile != "" || !toPlayFile.equals("") || toPlayFile != null) {
			AssetFileDescriptor data = activity.getAssets().openFd(toPlayFile);
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
			mediaPlayer.prepare();
	//		int duration = mediaPlayer.getDuration();
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
		else {
			bell = "";
		}
	}
	
	public static boolean IsMoving() {
		if(accx_stack.size() >= 5) {
			int movement = 0;
			int prev = 0;
			int current = 0;
			Deque<Integer> temp = new ArrayDeque<Integer>();
			int count = 0;
			while(accx_stack.size() > 0) {
				int single = accx_stack.pollLast();
				temp.addFirst(single);
				if(count == 0) {
					prev = single;
				}
				else{
					current = single;
					movement += Math.abs(current - prev);
					prev = current;
				}
				count++;
			}
			accx_stack = temp;
			if(movement  >= 10) {
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	
	public static void MonitorAccx(int change) {
		if(accx_stack.size() < 5) {
			accx_stack.push(change);
		}
		else{
			accx_stack.pollLast();
			accx_stack.push(change);
		}
	}
	
	public static void StartRecording() throws IOException {
		folder = new File(Environment.getExternalStorageDirectory() + "/NinjaTrack");
		
		if(folder.exists()) {
			audioFile = File.createTempFile("temp_", ".3gp", folder);
			
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start();
		}
		else {
			if(folder.mkdir()) {
				audioFile = File.createTempFile("temp_", ".3gp", folder);
				
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
				mediaRecorder.prepare();
				mediaRecorder.start();
			}
		}
	}
	
	public static Uri StopRecording(ContentResolver contentResolver) {
		mediaRecorder.stop();
		mediaRecorder.release();
		
		audioFile.renameTo(new File(folder + "/" + filename + ".3gp"));
		
		long current = System.currentTimeMillis();
		
		ContentValues values = new ContentValues(4);
		values.put(MediaStore.Audio.Media.TITLE, audioFile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
		
		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);
		
		return newUri; 
	}
}
