package adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import model.Pin;
import redbearprotocol.RBLProtocol;
import activity.MainActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.SparseArray;
import async.ContinuousPlay;
import fragment.DrumFragment;
import fragment.HandBellFragment;
import fragment.HarpFragment;
import fragment.RecorderFragment;
import fragment.SaxophoneFragment;

public class InstrumentHandler {
	
	public static final int RECORDER_FLAG = 0x00;
	public static final int SAXOPHONE_FLAG = 0x01;
	public static final int DRUM_FLAG = 0x02;
	public static final int BELL_FLAG = 0x03;
	public static final int HARP_FLAG = 0x04;
	
	public static int recorder1Id;
	public static int recorder2Id;
	public static int recorder3Id;
	public static int recorder4Id;
	public static int recorder5Id;
	public static int recorder6Id;
	public static int recorder7Id;
	public static int recorder8Id;
	public static int saxophone1Id;
	public static int saxophone2Id;
	public static int saxophone3Id;
	public static int saxophone4Id;
	public static int saxophone5Id;
	public static int saxophone6Id;
	public static int saxophone7Id;
	public static int saxophone8Id;
	private static int drumId;
	private static int snareId;
	private static int bellId;
	private static int harpId;
	
	public static int curr_flag = -1; // No value -1
	public static int prev_flag = -1; // No value -1
	public static int switch1_flag = -1; // No value -1
	public static int switch2_flag = -1; // No value -1
	public static int flex;
	public static int accx;
	public static Deque<Integer> accx_stack = new ArrayDeque<Integer>();
	
	public static String button1Path;
	public static String button2Path;
	public static String button3Path;
	public static String button4Path;
	public static String button5Path;
	public static String button6Path;
	public static String button7Path;
	public static String button8Path;
	public static String drumPath;
	public static String bellPath; 
	public static String harpPath;
	
	public static int button1;
	public static int button2;
	public static int button3;
	public static int button4;
	public static int button5;
	public static int button6;
	public static int button7;
	public static int button8;
	public static int drum;
	public static int bell;
	
	public static boolean enableDrum = true; // Within boundary
	public static boolean resetDrum = true; // Played before or not
	public static boolean enableBell = true; // Within boundary
	public static boolean resetBell = true; // Played before or not
	public static boolean readAccx = false;
	public static boolean bellOrHarp = true;
	public static boolean bellInitiated = false;
	public static boolean harpInitiated = false;
	
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
	private static File audioFile = null;
	private static File folder = new File(Environment.getExternalStorageDirectory() + "/NinjaTrack");
	
	public static SoundPool sp;
	
	public static void LoadSoundPool(Activity activity) {
		sp = new SoundPool(100, AudioManager.STREAM_MUSIC, 0); // maxStream, streamType, srcQuality
		sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool sp, int id, int status) {
				System.out.println("SoundPool loaded: " + id);
			}
		});
		try {
			recorder1Id = sp.load(activity.getAssets().openFd("recorder/Recorder 1.mp3"), 0);
			recorder2Id = sp.load(activity.getAssets().openFd("recorder/Recorder 2.mp3"), 0);
			recorder3Id = sp.load(activity.getAssets().openFd("recorder/Recorder 3.mp3"), 0);
			recorder4Id = sp.load(activity.getAssets().openFd("recorder/Recorder 4.mp3"), 0);
			recorder5Id = sp.load(activity.getAssets().openFd("recorder/Recorder 5.mp3"), 0);
			recorder6Id = sp.load(activity.getAssets().openFd("recorder/Recorder 6.mp3"), 0);
			recorder7Id = sp.load(activity.getAssets().openFd("recorder/Recorder 7.mp3"), 0);
			recorder8Id = sp.load(activity.getAssets().openFd("recorder/Recorder 8.mp3"), 0);
			saxophone1Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 1.mp3"), 0);
			saxophone2Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 2.mp3"), 0);
			saxophone3Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 3.mp3"), 0);
			saxophone4Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 4.mp3"), 0);
			saxophone5Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 5.mp3"), 0);
			saxophone6Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 6.mp3"), 0);
			saxophone7Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 7.mp3"), 0);
			saxophone8Id = sp.load(activity.getAssets().openFd("saxophone/Saxophone 8.mp3"), 0);
			drumId = sp.load(activity.getAssets().openFd("drum/Drum.mp3"), 0);
			snareId = sp.load(activity.getAssets().openFd("drum/Snare.mp3"), 0);
			bellId = sp.load(activity.getAssets().openFd("handbell/Bell.mp3"), 0);
			harpId = sp.load(activity.getAssets().openFd("harp/Harp.mp3"), 0);
		}
		catch(IOException e) {}
	}
	
	public static void SetMode(RBLProtocol protocol, SharedPreferences sp, SparseArray<Pin> pins) {
		for(int i = 0; i < pins.size(); i++) {
			if(sp.contains(Integer.toString(pins.get(i).getPin()))) {
				protocol.setPinMode(pins.get(i).getPin(), sp.getInt(Integer.toString(pins.get(i).getPin()), 0));
			}
		}
	}
	
	public static int CheckFlags(MainActivity activity) {
		int flag = -1;
		
		if(switch1_flag == 0 && switch2_flag == 0) {
			if(flex > 26) { // Recorder
				flag = RECORDER_FLAG;
			}
			else { // Saxophone
				flag = SAXOPHONE_FLAG;
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
				
				flag = DRUM_FLAG;
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
					
					flag = BELL_FLAG;
				}
				else { // Harp
					flag = HARP_FLAG;
				}
			}
		}
		
		if(curr_flag == -1) {
			curr_flag = flag;
		}
		else {
			if(prev_flag != curr_flag) {
				prev_flag = curr_flag;
			}
			curr_flag = flag;
		}
		
		if(flag == BELL_FLAG && bellPath != null && bellOrHarp) {
			bellOrHarp = false;
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("FLAG", BELL_FLAG);
			msg.setData(bundle);
			activity.handleTabs.sendMessage(msg);
			if(!bellInitiated){
				bellInitiated = true;
				Runnable runnable = new Runnable(){
					@Override
					public void run() {
						ManipulateUI(BELL_FLAG, bellPath, true);
					}
				};
				activity.h.postDelayed(runnable, 1000);
			}
			else{
				ManipulateUI(BELL_FLAG, bellPath, true);
			}
		}
		if(flag != BELL_FLAG && bellPath != null) {
			bellOrHarp = true;
			ManipulateUI(BELL_FLAG, bellPath, false);
			bellPath = null;
		}
		if(flag != BELL_FLAG) {
			bell = -1;
		}
		if(flag == HARP_FLAG && harpPath != null && bellOrHarp) {
			bellOrHarp = false;
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("FLAG", HARP_FLAG);
			msg.setData(bundle);
			activity.handleTabs.sendMessage(msg);
			if(!harpInitiated){
				harpInitiated  = true;
				Runnable runnable = new Runnable(){
					@Override
					public void run() {
						ManipulateUI(HARP_FLAG, harpPath, true);
					}
				};
				activity.h.postDelayed(runnable, 1000);
			}
			else{
				ManipulateUI(HARP_FLAG, harpPath, true);
			}
		}
		if(flag != HARP_FLAG && harpPath != null){
			bellOrHarp = true;
			ManipulateUI(HARP_FLAG, harpPath, false);
			harpPath = null;
		}
		
		return flag;
	}
	
	public static void SetSound(MainActivity activity) throws IOException {
		int flag = CheckFlags(activity);
		
		switch(flag) {
			case RECORDER_FLAG:
				button1Path = "recorder/Recorder 1.mp3";
				button2Path = "recorder/Recorder 2.mp3";
				button3Path = "recorder/Recorder 3.mp3";
				button4Path = "recorder/Recorder 4.mp3";
				button5Path = "recorder/Recorder 5.mp3";
				button6Path = "recorder/Recorder 6.mp3";
				button7Path = "recorder/Recorder 7.mp3";
				button8Path = "recorder/Recorder 8.mp3";
				
				button1 = recorder1Id;
				button2 = recorder2Id;
				button3 = recorder3Id;
				button4 = recorder4Id;
				button5 = recorder5Id;
				button6 = recorder6Id;
				button7 = recorder7Id;
				button8 = recorder8Id;
				break;
				
			case SAXOPHONE_FLAG:
				button1Path = "saxophone/Saxophone 1.mp3";
				button2Path = "saxophone/Saxophone 2.mp3";
				button3Path = "saxophone/Saxophone 3.mp3";
				button4Path = "saxophone/Saxophone 4.mp3";
				button5Path = "saxophone/Saxophone 5.mp3";
				button6Path = "saxophone/Saxophone 6.mp3";
				button7Path = "saxophone/Saxophone 7.mp3";
				button8Path = "saxophone/Saxophone 8.mp3";
				
				button1 = saxophone1Id;
				button2 = saxophone2Id;
				button3 = saxophone3Id;
				button4 = saxophone4Id;
				button5 = saxophone5Id;
				button6 = saxophone6Id;
				button7 = saxophone7Id;
				button8 = saxophone8Id;
				break;
				
			case DRUM_FLAG:
				drumPath = "drum/Drum.mp3";
				
				drum = drumId;
				break;
				
			case BELL_FLAG:
				button1Path = "handbell/Bell.mp3";
				button2Path = "handbell/Bell.mp3";
				button3Path = "handbell/Bell.mp3";
				button4Path = "handbell/Bell.mp3";
				button5Path = "handbell/Bell.mp3";
				button6Path = "handbell/Bell.mp3";
				button7Path = "handbell/Bell.mp3";
				button8Path = "handbell/Bell.mp3";
				
				button1 = bellId;
				button2 = bellId;
				button3 = bellId;
				button4 = bellId;
				button5 = bellId;
				button6 = bellId;
				button7 = bellId;
				button8 = bellId;
				break;
				
			case HARP_FLAG:
				button1Path = "harp/Harp.mp3";
				button2Path = "harp/Harp.mp3";
				button3Path = "harp/Harp.mp3";
				button4Path = "harp/Harp.mp3";
				button5Path = "harp/Harp.mp3";
				button6Path = "harp/Harp.mp3";
				button7Path = "harp/Harp.mp3";
				button8Path = "harp/Harp.mp3";
				
				button1 = harpId;
				button2 = harpId;
				button3 = harpId;
				button4 = harpId;
				button5 = harpId;
				button6 = harpId;
				button7 = harpId;
				button8 = harpId;
				break;
		}
	}
	
	public static void PlaySound(MainActivity activity, final int buttonNo) throws IOException {
		int flag = CheckFlags(activity);
		String toPlayFile = null;
		int toPlayId = -1;
		
		if(flag == DRUM_FLAG) {
			if(enableDrum && !resetDrum) {
				toPlayId = drum;
				resetDrum = true;
				
				ManipulateUI(flag, drumPath, true);
			}
			else {
				ManipulateUI(flag, drumPath, false);
				return;
			}
		}
		
		else if(flag == BELL_FLAG) {			
			switch(buttonNo) {
				case 1:
					toPlayFile = button1Path;
					bell = button1;
					break;
				case 2:
					toPlayFile = button2Path;
					bell = button2;
					break;
				case 3:
					toPlayFile = button3Path;
					bell = button3;
					break;
				case 4:
					toPlayFile = button4Path;
					bell = button4;
					break;
				case 5:
					toPlayFile = button5Path;
					bell = button5;
					break;
				case 6:
					toPlayFile = button6Path;
					bell = button6;
					break;
				case 7:
					toPlayFile = button7Path;
					bell = button7;
					break;
				case 8:
					toPlayFile = button8Path;
					bell = button8;
					break;
			}
			if(toPlayFile != null){
				bellPath = toPlayFile;
			}
			if(enableBell && !resetBell && bell != -1) {
				toPlayId = bell;
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
					toPlayFile = button1Path;
					toPlayId = button1;
					break;
				case 2:
					toPlayFile = button2Path;
					toPlayId = button2;
					break;
				case 3:
					toPlayFile = button3Path;
					toPlayId = button3;
					break;
				case 4:
					toPlayFile = button4Path;
					toPlayId = button4;
					break;
				case 5:
					toPlayFile = button5Path;
					toPlayId = button5;
					break;
				case 6:
					toPlayFile = button6Path;
					toPlayId = button6;
					break;
				case 7:
					toPlayFile = button7Path;
					toPlayId = button7;
					break;
				case 8:
					toPlayFile = button8Path;
					toPlayId = button8;
					break;
				default:
					return;
			}
			
			if(flag == HARP_FLAG) {
				harpPath = toPlayFile;
			}
			
			if(flag == RECORDER_FLAG || flag == SAXOPHONE_FLAG) {
				ContinuousPlay cp = new ContinuousPlay(activity, toPlayId);
//				ContinuousPlay cp = new ContinuousPlay(activity, toPlayFile);
				switch(buttonNo) {
					case 1:
						sound1 = cp;
						sound1.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 2:
						sound2 = cp;
						sound2.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 3:
						sound3 = cp;
						sound3.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 4:
						sound4 = cp;
						sound4.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 5:
						sound5 = cp;
						sound5.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 6:
						sound6 = cp;
						sound6.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 7:
						sound7 = cp;
						sound7.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
					case 8:
						sound8 = cp;
						sound8.executeOnExecutor(ContinuousPlay.THREAD_POOL_EXECUTOR);
						break;
				}
				
				ManipulateUI(flag, toPlayFile, true);
				return;
			}
		}
		else {
			return;
		}
		
		if(toPlayId != -1){
			AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
			final float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			final float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume;
			sp.play(toPlayId, streamVolume, streamVolume, 0, 0, 1);
		}
	}
	
	public static void StopSound(MainActivity activity, final int buttonNo) throws IOException {
		if(buttonNo != -1) {
			switch(buttonNo) {
				case 1:
					sound1.playOn = false;
					sound1.cancel(true);
					ManipulateUI(CheckFlags(activity), button1Path, false);
					break;
				case 2:
					sound2.playOn = false;
					sound2.cancel(true);
					ManipulateUI(CheckFlags(activity), button2Path, false);
					break;
				case 3:
					sound3.playOn = false;
					sound3.cancel(true);
					ManipulateUI(CheckFlags(activity), button3Path, false);
					break;
				case 4:
					sound4.playOn = false;
					sound4.cancel(true);
					ManipulateUI(CheckFlags(activity), button4Path, false);
					break;
				case 5:
					sound5.playOn = false;
					sound5.cancel(true);
					ManipulateUI(CheckFlags(activity), button5Path, false);
					break;
				case 6:
					sound6.playOn = false;
					sound6.cancel(true);
					ManipulateUI(CheckFlags(activity), button6Path, false);
					break;
				case 7:
					sound7.playOn = false;
					sound7.cancel(true);
					ManipulateUI(CheckFlags(activity), button7Path, false);
					break;
				case 8:
					sound8.playOn = false;
					sound8.cancel(true);
					ManipulateUI(CheckFlags(activity), button8Path, false);
					break;
			}
		}
		else {
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
	
	public static void ManipulateUI(int flag, String toPlayFile, boolean isPlaying) {
		String[] temp1 = toPlayFile.split("/");
		String title = temp1[1];

		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("title", title);
		data.putBoolean("isPlaying", isPlaying);
		msg.setData(data);
		
		switch(flag) {
			case RECORDER_FLAG :
				RecorderFragment.handler.sendMessage(msg);
				break;
				
			case SAXOPHONE_FLAG :
				SaxophoneFragment.handler.sendMessage(msg);
				break;
				
			case DRUM_FLAG :
				DrumFragment.handler.sendMessage(msg);
				break;
				
			case BELL_FLAG :
				HandBellFragment.handler.sendMessage(msg);
				break;
				
			case HARP_FLAG :
				HarpFragment.handler.sendMessage(msg);
				break;
		}
	}
}
