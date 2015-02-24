/**
 * 
 */
package com.aksharspeech.waverecorder.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aksharspeech.waverecorder.R;
import com.aksharspeech.waverecorder.config.Constants;
import com.aksharspeech.waverecorder.util.UTIL;
import com.cybern.util.ShowVisualizer;
import com.cybern.waverecorder.WaveRecorder;

/**
 * @author amitkumarsah
 * 
 */
public class SentenceRecord {
	/* Recorder Settings */
	private static int RECORDER_BPP = 16;
	private static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int RECORDER_SAMPLERATE = 16000;
	private boolean wasPaused = false;

	private boolean mSumbit = false;
	InputStreamReader mInputReader = null;
	BufferedReader mInputBuffer = null;
	ArrayList<String> mLine;
	private Activity mAct;
	private static final String BaseDir = "AksharRecorder";
	private boolean mPlayCheck = false;
	private int mIndex = 0;
	private String mCurrentLine = null;
	private String mCurrentLineNumber = null;
	/**
	 *  use gain to enable disable the use of gain
	 */
	public static boolean USE_GAIN=true;
	
	/**
	 * UI Elements
	 * 
	 */
	private ImageView mPlayButton;
	private TextView tvTextArea;
	// ImageView play;

	private Chronometer cnMeter;

	/**
	 * Boolean Flags to controls the operations
	 * 
	 */
	public static boolean EOF = false;
	public static boolean BOF = true;;

	public boolean mIsRecording = false;
	public boolean mIsPlaying = false;

	public boolean mRecordCheck = true;

	public boolean mAlwaysRecord = true;
	public boolean mAlwaysPlay = false;
	float mGain = 80;

	private String mPath = "";
	private MediaPlayer mPlayer;

	private String mTextFile = "sample.txt";

	private String mTextPath = "";

	private ImageView record;

	// private final String Tag = "SentenceRecord";

	private WaveRecorder waveRecorder;

	/**
	 * Default constructor without init function You need to call the init
	 * function after this. it is must.
	 * 
	 * @param act
	 *            Pass the current activity reference
	 */
	public SentenceRecord(Activity act) {
		BOF = true;
		EOF = false;
		USE_GAIN=true;
		waveRecorder = new WaveRecorder();
		WaveRecorder.setAudioFolder(BaseDir);
		this.mAct = act;
		this.mGain = mGain / 100;
		mLine = new ArrayList<String>();

		// Initiation of recorder
		waveRecorder.initRecording();
		record = (ImageView) mAct.findViewById(R.id.RcdBtnRecord);
		cnMeter = (Chronometer) mAct.findViewById(R.id.RcdchnoMeter);

	}

	/**
	 * Construction with init function
	 * 
	 * @param act
	 *            Pass the current activity reference
	 * @param filename
	 *            Pass the name with full of the input text file
	 */
	public SentenceRecord(Activity act, String filename) {
		BOF = true;
		EOF = false;
		USE_GAIN=true;
		waveRecorder = new WaveRecorder();
		WaveRecorder.setAudioFolder(BaseDir);
		this.mAct = act;
		this.mGain = mGain / 100;
		mLine = new ArrayList<String>();

		// Initiation of recorder
		waveRecorder.initRecording();
		record = (ImageView) mAct.findViewById(R.id.RcdBtnRecord);
		cnMeter = (Chronometer) mAct.findViewById(R.id.RcdchnoMeter);
		onInitSentenceRecording(filename);
		showVis = new ShowVisualizer(mAct);

	}

	public void cleanUp() {
		closeTextFile();
		waveRecorder.cancelRecord();
		this.releasePlayer();
	}

	public boolean closeTextFile() {
		try {
			if (mInputBuffer != null)
				mInputBuffer.close();

			if (mInputReader != null) {
				mInputReader.close();
				return true;
			} else
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void enableNext() {
		if (!wasPaused)
			mAct.findViewById(R.id.RcdBtnNext).setEnabled(true);
	}

	/**
	 * @param basename
	 *            pass the line number
	 * @return return the filename based on the basename and textfile
	 */
	public String genrateAudioFileName(String basename) {
		String filename = mPath + "/" + basename + ".wav";
		return filename;
	}

	public String getPathDir() {
		String filename = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/AksharRecorder/";

		SharedPreferences pref = mAct.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		filename = filename + pref.getString("username", "defaultuser") + "_"
				+ pref.getString("gender", "nogender") + "_"
				+ pref.getInt("age", 25) + "_"
				+ pref.getString("type", "android") + "/"
				+ mTextFile.substring(0, mTextFile.indexOf('.'));

		if (filename.equals(mPath))
			return filename;
		else {
		}
		return filename;
	}

	public void initRecord() {
		waveRecorder.initRecording();

	}

	public boolean isPlayingAudio() {
		if (mPlayer != null) {
			return mPlayer.isPlaying();
		} else
			return false;

	}

	public void loadRecordContextSetting() {
		SharedPreferences pref = mAct.getApplicationContext()
				.getSharedPreferences("MyPref", Activity.MODE_PRIVATE);
		mGain = pref.getFloat("gain", 80);
		RECORDER_BPP = pref.getInt("bpp", 16);
		RECORDER_CHANNELS = pref.getInt("channel", AudioFormat.CHANNEL_IN_MONO);

		RECORDER_SAMPLERATE = pref.getInt("samplerate", 16000);
		RECORDER_AUDIO_ENCODING = pref.getInt("encoding",
				AudioFormat.ENCODING_PCM_16BIT);

		mAlwaysPlay = pref.getBoolean(Constants.ALWAYSPLAY, false);

		mAlwaysRecord = pref.getBoolean(Constants.ALWAYSRECORD, true);

	}

	private boolean loadTextFile() {
		while (!EOF)
			readLine();
		if (mLine.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * when Back Button is pressed!.
	 */
	private void onBackPressed() {
		cleanUp();
		mAct.onBackPressed();

	}

	ShowVisualizer showVis;

	public void setupVisualizer(MediaPlayer mplayer) {
		showVis = new ShowVisualizer(mAct);
		showVis.setupVisualizerFxAndUI(R.id.RcdVisualizerView, mplayer);
		showVis.enableVisualizer(true);

	}

	public void onInitSentenceRecording(String textfilename) {
		WaveRecorder.setAudioFolder("/" + BaseDir);
		loadRecordContextSetting();
		WaveRecorder.setParameters(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, RECORDER_BPP);
		this.setTextName(textfilename);
		this.setPathDir();

		mPlayButton = (ImageView) mAct.findViewById(R.id.RcdBtnPlay);
		tvTextArea = (TextView) mAct.findViewById(R.id.RcdTVfileArea);
		mGain = mGain / 100;
		this.openTextFile(mTextPath + mTextFile);
		mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
		mAct.findViewById(R.id.RcdBtnNext).setEnabled(false);

	}

	public void onNextButtonClicked() {
		if (!mAlwaysPlay || !mPlayCheck) {
			if (!mAlwaysRecord || !mRecordCheck) {
				if (mIsPlaying)
					releasePlayer();
				if (mIndex < (mLine.size() - 1)) {
					mIndex += 1;
					mAct.findViewById(R.id.RcdBtnPrev).setEnabled(true);
					mCurrentLine = this.readNextLine();
					cnMeter.setText("00:00");
					if (mCurrentLine == null) {
						if (mIndex < mLine.size())
							toastLong("Fail to read line");
						else {
							toastLong("Thanks for recording, return to Selection Menu Page. Don't forget to submit the recording");
							// this.moveBackToSelection();
							this.onBackPressed();
						}
						return;
					}
					tvTextArea.setText(mCurrentLine);
					WaveRecorder.setAudioFileName(null);
					mRecordCheck = true;
					if (mIndex == (mLine.size() - 1)) {
						Button nextB = (Button) mAct
								.findViewById(R.id.RcdBtnNext);
						nextB.setText("Submit");
						toastShort("You have reached the last line.");
						mSumbit = true;
					}
					// TODO: Bug_Previous and Next buttons 001/002
					postNagivationClicked();
				} else {// if (mIndex >= mLine.size()) {
					toastLong("Reached end of File, Kindly submit the recording");
					// this.moveBackToSelection();
					this.onBackPressed();
				}

			} else {
				toastShort("First record the file");
			}
		} else {
			toastShort("First Play the file");
		}

	}

	public void onPause() {
		wasPaused = true;
		if (showVis != null)
			showVis.realseVisualizer();
		if (mIsRecording) {
			mIsRecording = false;
			mRecordCheck = true;
			waveRecorder.cancelRecord();
			record.setImageResource(R.drawable.ic_action_recordbutton);
			this.cnMeter.stop();
			mAct.findViewById(R.id.RcdBtnNext).setEnabled(true);
			if (mIndex <= 0)
				mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
			else
				mAct.findViewById(R.id.RcdBtnPrev).setEnabled(true);
			this.releasePlayer();
		}
		SharedPreferences pref = mAct.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("mAlwaysPlay", mAlwaysPlay);
		editor.putBoolean("mAlwaysRecord", mAlwaysRecord);
		editor.putBoolean("mRecordCheck", mRecordCheck);
		editor.putBoolean("mPlayCheck", mPlayCheck);
		editor.putBoolean("misPlaying", mIsPlaying);
		editor.putBoolean("misRecording", mIsRecording);
		editor.putBoolean("mSubmit", mSumbit);
		editor.putBoolean("EOF", EOF);
		editor.putBoolean("BOF", BOF);
		editor.putInt("mIndex", mIndex);
		editor.putString("mCurrentLine", mCurrentLine);
		editor.putString("mCurrentLineNumber", mCurrentLineNumber);
		editor.putString("mPath", mPath);
		editor.putString("mTextFile", mTextFile);
		editor.putString("mTextPath", mTextPath);
		editor.putBoolean("wasPaused", wasPaused);
		editor.commit();

	}

	public void onPlayButtonClicked() {
		if (mIsRecording) {
			toastShort("Recorder is on. Kindly stop  it before playing!");
			return;
		}
		String filename = WaveRecorder.getAudioFileName();
		if (filename == null) {
			toastShort("Record audio file, no audio file exists!!");
			return;
		}
		File filetem = new File(filename);
		if (filetem.exists())
			this.startPlayingAudio(filename);
		else {
			toastShort("No audio file to play!");
		}
	}

	public void onPreviousButtonClicked() {

		if (!mAlwaysPlay || !mPlayCheck) {
			if (mIsPlaying) {
				releasePlayer();
			}
			if (mIndex > 0) {
				mIndex = mIndex - 1;
				mCurrentLine = this.readPreviousLine();
				if (mCurrentLine != null) {
					tvTextArea.setText(mCurrentLine);
					WaveRecorder.setAudioFileName(null);
					cnMeter.setText("00:00");
					// TODO: Bug_Previous and Next buttons 001/002
					postNagivationClicked();

					if (mIndex <= 0)
						mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
					if (mSumbit) {
						Button nextB = (Button) mAct
								.findViewById(R.id.RcdBtnNext);
						nextB.setText("Next");
						mSumbit = false;
					}
				} else {
					mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
					toastShort("Reached stasting point of the file");

				}
			} else {
				toastShort("Reached stasting point of the file");
			}

		} else {
			toastShort("Kindly Play the recorder file before going back!");
		}
	}

	public void onRecordButtonClicked() {

		if (!mIsRecording) {
			if (mAlwaysPlay && mPlayCheck) {
				toastLong("Kindly Play the recorded audio file before re-recording!");
				return;
			}
			if (mIsPlaying) {
				toastShort("Stop playing before recording!");
				return;
			}
			// record
			mAct.findViewById(R.id.RcdBtnNext).setEnabled(false);
			mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
			waveRecorder.startRecord(mGain);
			mIsRecording = true;
			resetTimer();
			cnMeter.start();
			record.setImageResource(R.drawable.ic_action_stopbutton);
			mRecordCheck = false;
			mPlayCheck = true;

		} else {
			// stop record
			StringTokenizer tokens = new StringTokenizer(mCurrentLine, ")");
			mCurrentLineNumber = tokens.nextToken();
			this.stopRecord(mCurrentLineNumber);
			mRecordCheck = false;
			mIsRecording = false;
			if (mIndex <= 0)
				mAct.findViewById(R.id.RcdBtnPrev).setEnabled(false);
		}

	}

	public void onRestart() {
		// TODO: implement onretsrt
		// SharedPreferences pref = mAct.getApplicationContext()
		// .getSharedPreferences("MyPref", Context.MODE_PRIVATE);

	}

	public void onResume() {
		// TODO: implement onresume
		if (wasPaused)
			wasPaused = false;

		postNagivationClicked();
	}

	public void onStop() {
		// TODO: Bug_Call_interupt

	}

	public void onDestory() {
		if (waveRecorder != null) {
			waveRecorder.cancelRecord();
			waveRecorder = null;

		}
		if (mPlayer != null) {
			releasePlayer();
			mPlayer = null;
		}
		closeTextFile();
	}

	public boolean openTextFile(String filename) {
		try {
			mInputReader = new InputStreamReader(new FileInputStream(filename));
			mInputBuffer = new BufferedReader(mInputReader);
			if (mInputReader != null && mInputBuffer != null)
				return true;
			else
				return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * It perform the post operation
	 */
	private void postNagivationClicked() {
		StringTokenizer tokens = new StringTokenizer(mCurrentLine, ")");
		mCurrentLineNumber = tokens.nextToken();
		WaveRecorder.setAudioFileName(genrateAudioFileName(mCurrentLineNumber));
		if (verifyAudioExists(WaveRecorder.getAudioFileName())) {
			// TODO: if file exits
			mRecordCheck = false;
		} else {
			// TODO: if file not exits
			mRecordCheck = true;
		}

	}

	/**
	 * read the last Line.
	 * 
	 * @return
	 */
	public String readLastLine() {
		int size = mLine.size();
		if (size > 0)
			mLine.remove(size - 1);
		else {
			BOF = true;
			return null;
		}
		size = mLine.size();
		if (size > 0)
			return mLine.get(size - 1);
		else {
			BOF = true;
			return null;
		}

	}

	private String readLine() {
		try {
			String line = mInputBuffer.readLine();
			if (line == null) {
				EOF = true;
				return line;
			}
			line = line.toString().trim();
			if (line.length() > 0)
				mLine.add(mLine.size(), line);
			BOF = false;
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			EOF = true;
			return null;
		}
	}

	private String readNextLine() {
		if (mIndex < mLine.size()) {
			return mLine.get(mIndex);
		} else
			return null;
	}

	public String readPreviousLine() {
		if (mIndex >= 0 && mLine.size() > 0) {
			return mLine.get(mIndex);

		} else {
			return null;
		}
	}

	public void releasePlayer() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			mPlayButton.setImageResource(R.drawable.ic_action_playbutton);
			mIsPlaying = false;
			try {
				// TODO: need to check if the cnMeter stop or not.
				cnMeter.stop();
			} catch (Exception e) {
				e.printStackTrace();

				resetTimer();
				cnMeter.setText("00:00");
				return;
			}

		}
		resetTimer();
		cnMeter.setText("00:00");

	}

	/*
	 * Optimization of Code is done here . Activity class function will move to
	 * here
	 */

	private void resetTimer() {
		cnMeter.setBase(SystemClock.elapsedRealtime());
	}

	/**
	 * 
	 * @return
	 */
	public void setPathDir() {
		String filename = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + BaseDir + "/";

		SharedPreferences pref = mAct.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		filename = filename + pref.getString("username", "defaultuser") + "_"
				+ pref.getString("gender", "nogender") + "_"
				+ pref.getInt("age", 25) + "_"
				+ pref.getString("language", "hindi") + "_"
				+ pref.getString("type", "android") + "/"
				+ mTextFile.substring(0, mTextFile.indexOf('.'));

		mPath = filename;
		if (new File(filename).mkdirs()) {
		}
		// return filename;

	}

	public void setTextName(String fname) {
		mTextPath = "";
		String filename = "";
		StringTokenizer toke = new StringTokenizer(fname, "/");
		while (toke.hasMoreTokens()) {
			mTextPath = mTextPath + filename;
			filename = toke.nextToken();
			mTextPath = mTextPath + "/";
		}

		mTextFile = filename;
		UTIL.savePrefernceStringValue(mAct, "textfilepath", mTextPath);
	}

	public void showAlwaysPlay(MenuItem item) {
		final CharSequence[] aPlay = { "Yes", "No" };
		int selItem = UTIL.getPrefernceBoolValue(mAct, Constants.ALWAYSPLAY,
				false) ? 0 : 1;

		AlertDialog.Builder alert = new AlertDialog.Builder(mAct);
		alert.setTitle("Always Play file Before recording next file");
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		alert.setSingleChoiceItems(aPlay, selItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// item2 = which;
						if (aPlay[which] == "Yes") {
							UTIL.savePrefernceBoolValue(
									mAct.getApplicationContext(),
									Constants.ALWAYSPLAY, true);
							mAlwaysPlay = true;

						} else if (aPlay[which] == "No") {
							UTIL.savePrefernceBoolValue(
									mAct.getApplicationContext(),
									Constants.ALWAYSPLAY, false);
							mAlwaysPlay = false;
						}
					}
				});
		alert.show();

	}

	public void showAlwaysRecord(MenuItem item) {
		final CharSequence[] aRecord = { "Yes", "No" };
		int selItem = UTIL.getPrefernceBoolValue(mAct, Constants.ALWAYSRECORD,
				true) ? 0 : 1;

		AlertDialog.Builder alert = new AlertDialog.Builder(mAct);
		alert.setTitle("Record each line before moving to the next ");
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		alert.setSingleChoiceItems(aRecord, selItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// item3 = which;
						if (aRecord[which] == "Yes") {
							UTIL.savePrefernceBoolValue(
									mAct.getApplicationContext(),
									Constants.ALWAYSRECORD, true);
							mAlwaysRecord = true;
							if (!mRecordCheck)
								mRecordCheck = false;
							else
								mRecordCheck = true;
						} else if (aRecord[which] == "No") {
							UTIL.savePrefernceBoolValue(
									mAct.getApplicationContext(),
									Constants.ALWAYSRECORD, false);
							mAlwaysRecord = false;
						}
					}
				});
		alert.show();
	}

	public void showEncodingMenu(MenuItem item) {
		final CharSequence[] aRecord = { "PCM_8BIT", "PCM_16BIT" };
		int setItem = UTIL.getPrefernceIntValue(mAct, "encoding",
				AudioFormat.ENCODING_PCM_16BIT);
		if (setItem == AudioFormat.ENCODING_PCM_16BIT)
			setItem = 1;
		else
			setItem = 0;
		AlertDialog.Builder alert = new AlertDialog.Builder(mAct);
		alert.setTitle("Select the audio encoding system");
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		alert.setSingleChoiceItems(aRecord, setItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// item4 = which;
						if (aRecord[which] == "PCM_8BIT") {
							UTIL.savePrefernceIntValue(
									mAct.getApplicationContext(), "encoding",
									AudioFormat.ENCODING_PCM_8BIT);
							RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT;

						} else if (aRecord[which] == "PCM_16BIT") {
							UTIL.savePrefernceIntValue(
									mAct.getApplicationContext(), "encoding",
									AudioFormat.ENCODING_PCM_16BIT);
							RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
						}
					}
				});
		alert.show();

	}

	public void showFirstLine() {
		if (wasPaused) {
			wasPaused = false;
			return;
		} // TODO: Bug_Pause_Resume Activity
		mCurrentLine = this.readLine();
		if (mCurrentLine != null)
			tvTextArea.setText(mCurrentLine);
		else {
			toastLong("Error while reading file, Kindly go back and select file again");

		}

		Thread loadingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (loadTextFile())
					enableNext();

			}
		}, "TextFile Loading Thread");
		loadingThread.start();

	}

	public void showGainMenu(MenuItem item) {
		mGain = UTIL.getPrefernceFloatValue(mAct, "gain", 80);
		mGain = mGain / 100;
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(mAct);
		final LayoutInflater inflater = (LayoutInflater) mAct
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		final View Viewlayout = inflater.inflate(R.layout.lysetting_gain,
				(ViewGroup) mAct.findViewById(R.id.layout_dialog));
		final TextView gaintext = (TextView) Viewlayout
				.findViewById(R.id.stGainTVgain);
		final SeekBar seek = (SeekBar) Viewlayout
				.findViewById(R.id.stGainSBGainSeek);
		gaintext.setText("Gain: " + mGain * 100);
		seek.setProgress((int) (mGain * 100));
		popDialog.setTitle("Set Gain ");
		popDialog.setView(Viewlayout);
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mGain = progress;
				gaintext.setText("Gain: " + progress);
				mGain = mGain / 100;
			}

			public void onStartTrackingTouch(SeekBar arg0) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				mGain = seek.getProgress();
				gaintext.setText("Gain: " + mGain);
				mGain = mGain / 100;
			}
		});
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						float x = (mGain * 100);
						UTIL.savePrefernceFloatValue(mAct, "gain", x);
						dialog.dismiss();
					}
				});
		popDialog.create();
		popDialog.show();

	}

	public void showSampleRateMenu(MenuItem item) {
		// int id = item.getItemId();
		final CharSequence rates[] = new CharSequence[] { "8000", "16000",
				"22050", "32000" };
		int setItem = UTIL.getPrefernceIntValue(mAct, "samplerate", 16000);
		switch (setItem) {
		case 8000:
			setItem = 0;
			break;
		case 16000:
			setItem = 1;
			break;
		case 22050:
			setItem = 2;
			break;
		case 32000:
			setItem = 3;
			break;
		default:
			setItem = 1;
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
		builder.setTitle("Pick SampleRate");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		builder.setSingleChoiceItems(rates, setItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						UTIL.savePrefernceIntValue(
								mAct.getApplicationContext(),
								"samplerate",
								Integer.parseInt(rates[which].toString().trim()));
						RECORDER_SAMPLERATE = Integer.parseInt(rates[which]
								.toString().trim());
					}
				});
		builder.show();

	}

	/* Activity Life Cyle Handler */

	private void startPlayingAudio(String audioplayfile) {
		if (!mIsRecording) {
			if (!mIsPlaying) {
				mIsPlaying = true;
				mPlayer = new MediaPlayer();
				try {
					mPlayer.setDataSource(audioplayfile);
					mPlayer.prepare();
					showVis.setupVisualizerFxAndUI(R.id.RcdVisualizerView, mPlayer);
					showVis.enableVisualizer(true);
					mPlayer.start();
					
					resetTimer();
					cnMeter.start();
					mPlayButton
							.setImageResource(R.drawable.ic_action_stopbutton);
					mPlayCheck = false;
					mPlayer.setOnCompletionListener(new OnCompletionListener() {

						public void onCompletion(MediaPlayer mPlayer) {
							stopPlayingAudio();
						}
					});

				} catch (IOException e) {
					toastShort("prepare() failed");
					// TODO: remove after check
				}
			} else
				stopPlayingAudio();

		} else
			toastShort("Recording is still going on");
		// TODO: remove after check
	}

	private void stopPlayingAudio() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			
		}
		if(showVis!=null){
			showVis.enableVisualizer(false);
			showVis.realseVisualizer();
		}
		cnMeter.stop();
		mPlayButton.setImageResource(R.drawable.ic_action_playbutton);
		mIsPlaying = false;
	}

	/**
	 * StopRecording AKSHAR
	 * 
	 * @param ofname
	 */
	public void stopRecord(String ofname) {
		WaveRecorder.setAudioFileName(genrateAudioFileName(ofname));
		waveRecorder.stopRecord();
		record.setImageResource(R.drawable.ic_action_recordbutton);
		this.cnMeter.stop();
		mAct.findViewById(R.id.RcdBtnNext).setEnabled(true);
		mAct.findViewById(R.id.RcdBtnPrev).setEnabled(true);
		mIsRecording = false;

	}

	public void toastLong(String msg) {
		Toast.makeText(mAct.getApplicationContext(), msg, Toast.LENGTH_LONG)
				.show();
	}

	public void toastShort(String msg) {
		Toast.makeText(mAct.getApplicationContext(), msg, Toast.LENGTH_SHORT)
				.show();
	}

	public void updateRecorderSetting(Context context) {

		UTIL.savePrefernceIntValue(context, "encoding",
				AudioFormat.ENCODING_PCM_16BIT);
		UTIL.savePrefernceIntValue(context, "bpp", 16);
		UTIL.savePrefernceIntValue(context, "channel",
				AudioFormat.CHANNEL_IN_MONO);
		UTIL.savePrefernceIntValue(context, "samplerate", 16000);

	}

	/**
	 * Verify the Audio file exsits or not!
	 * 
	 * @param filename
	 *            name of the file to be verifed
	 * @return return true if the exits else return false
	 */
	private boolean verifyAudioExists(String filename) {
		File fname = new File(filename);
		if (fname.exists()) {
			if (fname.isFile()) {
				if (fname.length() > 144)
					return true;
				else
					return false;
			} else
				return false;

		} else
			return false;

	}
	public void onCBClick(View v){
		CheckBox cb=(CheckBox)v;
		USE_GAIN=cb.isChecked();
		
	}
}
