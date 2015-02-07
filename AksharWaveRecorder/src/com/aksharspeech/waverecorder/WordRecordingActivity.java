package com.aksharspeech.waverecorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aksharspeech.waverecorder.config.Constants;
import com.aksharspeech.waverecorder.ui.UserInfo;
//import com.leaveme.ssad.R;

/**
 * @author romil
 * @author amitkumarsah
 * 
 */
public class WordRecordingActivity extends Activity {
	//private final String LOG_TAG = "WordRecoding";
	UserInfo info;
	private static String mFileName = null;
	private static String pFileName = null;
	private static String[] tFileName = null;
	private int i = 0;
	private MediaPlayer mPlayer = null;
	boolean mStartPlaying = true;
	boolean mStartRecording = false;
	boolean alwaysPlay = false;
	boolean alwaysRecord = true;
	private static int RECORDER_SAMPLERATE = 16000;
	private static final int RECORDER_BPP = 16; // bits per sample
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	private Thread recordingThread = null;
	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we
	int BytesPerElement = 2; // 2 bytes in 16bit format
	int bufferSize = 0;
	String filename;
	BufferedReader reader;
	String[] line;
	TextView text;
	InputStream instream;
	InputStreamReader inputreader;
	BufferedReader buffreader;
	ImageView record;
	ImageView play;
	Button open;
	int j = 0;
	boolean playCheck = true;
	boolean recordCheck = false;
	int n = 0;
	String first;
	String filename2;
	int ab;
	ArrayAdapter<String> fileList;
	Bundle bundle;
	Button next = null;
	Button previous = null;
	String sec = null;
	final int MSG_START_TIMER = 0;
	final int MSG_STOP_TIMER = 1;
	final int MSG_UPDATE_TIMER = 2;
	Chronometer chronometer = null;
	int item1 = 1;
	int item2 = 1;
	int item3 = 0;
	int item4 = 1;
	Button submit;
	boolean isrecorded = false;
	ImageView record2;
	long timeWhenStopped = 0;
	TextView status = null;
	SeekBar seek = null;
	float gain = 80;
	TextView gaintext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lywordrecord);
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"MyPref", MODE_PRIVATE);
		i = pref.getInt("key", 0);
		info = UserInfo.getSavedUserInfo(this);
		RECORDER_SAMPLERATE = pref.getInt("sampleRate", 16000);
		RECORDER_AUDIO_ENCODING = pref.getInt("encoding",
				AudioFormat.ENCODING_PCM_16BIT);
		alwaysPlay = pref.getBoolean(Constants.ALWAYSPLAY, false);
		alwaysRecord = pref.getBoolean(Constants.ALWAYSRECORD, true);
		i = 0;
		tFileName = new String[1000];
		bundle = getIntent().getExtras();
		status = (TextView) findViewById(R.id.statusText);
		record2 = (ImageView) findViewById(R.id.button14);
		record2.setEnabled(false);
		gain = gain / 100;

		record = (ImageView) findViewById(R.id.button11);
		record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//  
				if (mStartPlaying) {
					if (!alwaysPlay || playCheck || mStartRecording) {
						mStartRecording = !mStartRecording;
						if (mStartRecording) {
							if (alwaysPlay)
								playCheck = false;
							recordCheck = true;
							record2.setEnabled(false);
							chronometer = (Chronometer) findViewById(R.id.chronometer1);
							status.setText("Recording");
							bufferSize = AudioRecord.getMinBufferSize(
									RECORDER_SAMPLERATE, RECORDER_CHANNELS,
									RECORDER_AUDIO_ENCODING);
							recorder = new AudioRecord(
									MediaRecorder.AudioSource.VOICE_COMMUNICATION,
									RECORDER_SAMPLERATE, RECORDER_CHANNELS,
									RECORDER_AUDIO_ENCODING, bufferSize
											* BytesPerElement);
							record.setImageResource(R.drawable.ic_action_pausebutton);
							int vb = recorder.getState();
							if (vb == 1) {
								recorder.startRecording();
								chronometer.setBase(SystemClock
										.elapsedRealtime() + timeWhenStopped);
								chronometer.start();
								recordingThread = new Thread(new Runnable() {
									@Override
									public void run() {
										writeAudioDataToFile();
									}
								}, "AudioRecorder Thread");
								recordingThread.start();
							}
						} else {
							if (null != recorder) {
								recorder.stop();
								recorder.release();
								timeWhenStopped = chronometer.getBase()
										- SystemClock.elapsedRealtime();
								chronometer.stop();
								recorder = null;
								recordingThread = null;
								record2.setEnabled(true);
								record.setImageResource(R.drawable.ic_action_recordbutton);
								status.setText("Recording paused");
							}
						}
					}
				}
			}
		});
		record2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//  
				mStartRecording = false;
				copyWaveFile();
				int e = 0;
				for (e = 0; e < i; e++) {
					File q = new File(tFileName[e]);
					q.delete();
				}
				i = 0;
				timeWhenStopped = 0;
				status.setText("Recording stopped");
				isrecorded = true;
				record2.setEnabled(false);
			}
		});
		submit = (Button) findViewById(R.id.button15);
		final Intent openStartingPoint = new Intent(this,SelectionMenuActivity.class);
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//  
				if (isrecorded) {
					if (!mStartPlaying)
						stopPlaying();
					
					startActivity(openStartingPoint);
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"First record the file", Toast.LENGTH_SHORT).show();
				}
			}
		});
		play = (ImageView) findViewById(R.id.button12);
		play.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//  

				pFileName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/AksharRecorder/"
						+ info.sUserName
						+ "_"
						+ info.sGender
						+ "_"
						+ info.iAge
						+ "_"
						+ info.sLang
						+ "_"
						+ info.sType
						+ "/"
						+ info.sUserName
						+ "_"
						+ info.sGender
						+ "_"
						+ info.iAge
						+ "_" + info.sLang + "_" + info.sType + ".wav";
				File filetem = new File(pFileName);
				if (filetem.exists())
					startPlayingFile();
				else {
					Toast.makeText(getApplicationContext(), "No file to play",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	// end of onCreate()

	public void startPlayingFile() {
		if (!mStartRecording) {
			if (mStartPlaying) {
				playCheck = true;
				mPlayer = new MediaPlayer();
				try {
					mStartPlaying = !mStartPlaying;
					mPlayer.setDataSource(pFileName);
					mPlayer.prepare();
					status.setText("Recorded file Playing");
					mPlayer.start();
					play.setImageResource(R.drawable.ic_action_stopbutton);
					mPlayer.setOnCompletionListener(new OnCompletionListener() {

						public void onCompletion(MediaPlayer mPlayer) {
							stopPlaying();
						}
					});
				} catch (IOException e) {
					//Log.e(LOG_TAG, "prepare() failed");
					e.printStackTrace(); return;
				}
			} else {
				stopPlaying();
			}

		}
	}

	void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		play.setImageResource(R.drawable.ic_action_playbutton);
		status.setText("Playing stopped and not recording");
		mStartPlaying = !mStartPlaying;
	}

	public void AudioRecordTest() {
		boolean success = true;
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/AksharRecorder/"
				+ info.sUserName
				+ "_"
				+ info.sGender
				+ "_"
				+ info.iAge
				+ "_"
				+ info.sLang
				+ "_"
				+ info.sType);
		if (!folder.exists()) {
			success = folder.mkdirs();
			if (success) {
				mFileName = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/AksharRecorder/"
						+ info.sUserName
						+ "_"
						+ info.sGender
						+ "_"
						+ info.iAge
						+ "_"
						+ info.sLang
						+ "_"
						+ info.sType
						+ "/"
						+ info.sUserName
						+ "_"
						+ info.sGender
						+ "_"
						+ info.iAge
						+ "_" + info.sLang + "_" + info.sType + ".wav";

				File q2 = new File(mFileName);
				if (q2.exists())
					q2.delete();
			} else {
				mFileName = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				mFileName += "/" + info.sUserName + "_" + info.sGender + "_"
						+ info.iAge + "_" + info.sLang + "_" + info.sType
						+ ".wav";
				File q2 = new File(mFileName);
				if (q2.exists())
					q2.delete();
			}
		} else {
			mFileName = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/AksharRecorder/"
					+ info.sUserName
					+ "_"
					+ info.sGender
					+ "_"
					+ info.iAge
					+ "_"
					+ info.sLang
					+ "_"
					+ info.sType
					+ "/"
					+ info.sUserName
					+ "_"
					+ info.sGender
					+ "_"
					+ info.iAge
					+ "_"
					+ info.sLang
					+ "_"
					+ info.sType + ".wav";
			File q2 = new File(mFileName);
			if (q2.exists())
				q2.delete();
		}
	}

	public void AudioRecordtemp() {
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/AksharRecorder");
		boolean success = true;

		if (!folder.exists()) {
			success = folder.mkdir();
			if (success) {
				tFileName[i] = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				tFileName[i] += "/AksharRecorder/audiorecordtest" + i + ".raw";
			} else {
				tFileName[i] = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				tFileName[i] += "/audiorecordtest" + i + ".raw";
			}
		} else {
			tFileName[i] = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			tFileName[i] += "/AksharRecorder/audiorecordtest" + i + ".raw";
		}
		i += 1;
	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte
		AudioRecordtemp();
		AudioRecordTest();
		short data[] = new short[bufferSize];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(tFileName[i - 1]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (null != os) {
			while (mStartRecording) {
				// gets the voice output from microphone to byte format
				ab = recorder.read(data, 0, bufferSize);
				if (AudioRecord.ERROR_INVALID_OPERATION != ab) {
					try {
						for (int iii = 0; iii < ab; ++iii)
							data[iii] = (short) Math.min(
									(int) (data[iii] * gain),
									(int) Short.MAX_VALUE);
						byte bData[] = short2byte(data);
						os.write(bData, 0, bufferSize * BytesPerElement);
						mTotalAudioLen += bData.length;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {

				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	long mTotalAudioLen = 0;

	private void copyWaveFile() {
		FileInputStream in = null;
		FileOutputStream out = null;

		long totalDataLen = mTotalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 1; // use 2 if doesn't work
		long byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE * channels) / 8;
		totalDataLen = mTotalAudioLen + 36;
		long totalAudioLen = 0;

		byte[] data = new byte[bufferSize];
		try {
			out = new FileOutputStream(mFileName);
			WriteWaveFileHeader(out, mTotalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			
			int e = 0;
			for (e = 0; e < i; e++) {
				in = new FileInputStream(tFileName[e]);
				totalAudioLen += in.getChannel().size();
				while (in.read(data) != -1) {
					out.write(data);
				}
				in.close();
			}
			//Log.i(LOG_TAG, "Audio=" + totalAudioLen + "\n(" + mTotalAudioLen
					//+ ")");
			if (totalAudioLen != mTotalAudioLen) {
				Toast.makeText(
						getApplicationContext(),
						"Size not matched Audio=" + totalAudioLen + ",("
								+ mTotalAudioLen + ")", Toast.LENGTH_LONG)
						.show();

			}
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	@Override
	public void onBackPressed() {
		Intent openStartingPoint = new Intent(this, SelectionMenuActivity.class);
		startActivity(openStartingPoint);
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_samplerate) {
			final CharSequence rates[] = new CharSequence[] { "8000", "16000",
					"22050", "32000" };
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick SampleRate ");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							return;
						}
					});
			builder.setSingleChoiceItems(rates, item1,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							item1 = which;
							if (rates[which] == "8000") {
								RECORDER_SAMPLERATE = 8000;
							} else if (rates[which] == "16000") {
								RECORDER_SAMPLERATE = 16000;
							} else if (rates[which] == "22050") {
								RECORDER_SAMPLERATE = 22050;
							} else if (rates[which] == "32000") {
								RECORDER_SAMPLERATE = 32000;
							}
						}
					});
			builder.show();
			return true;
		}
		if (id == R.id.action_alwaysplay) {
			final CharSequence[] aPlay = { "Yes", "No" };
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Always Play file Before recording next file");
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							return;
						}
					});
			alert.setSingleChoiceItems(aPlay, item2,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							item2 = which;
							if (aPlay[which] == "Yes") {
								alwaysPlay = true;
							} else if (aPlay[which] == "No") {
								alwaysPlay = false;
							}
						}
					});
			alert.show();
			return true;
		}
		if (id == R.id.action_alwaysrecord) {
			final CharSequence[] aRecord = { "Yes", "No" };
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Record each line before moving to the next");
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							return;
						}
					});
			alert.setSingleChoiceItems(aRecord, item3,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							item3 = which;
							if (aRecord[which] == "Yes") {
								alwaysRecord = true;
							} else if (aRecord[which] == "No") {
								alwaysRecord = false;
							}
						}
					});
			alert.show();
			return true;
		}
		if (id == R.id.action_audioencoding) {
			final CharSequence[] aRecord = { "PCM_8BIT", "PCM_16BIT" };
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Select the audio encoding system ");
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							return;
						}
					});
			alert.setSingleChoiceItems(aRecord, item4,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							item4 = which;
							if (aRecord[which] == "PCM_8BIT") {
								RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT;
							} else if (aRecord[which] == "PCM_16BIT") {
								RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
							}
						}
					});
			alert.show();
			return true;
		}
		if (id == R.id.action_gain) {
			final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
			final LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View Viewlayout = inflater.inflate(R.layout.lysetting_gain,
					(ViewGroup) findViewById(R.id.layout_dialog));

			gaintext = (TextView) Viewlayout.findViewById(R.id.stGainTVgain);
			seek = (SeekBar) Viewlayout.findViewById(R.id.stGainSBGainSeek);
			gaintext.setText("Gain: " + gain * 100);
			seek.setProgress((int) (gain * 100));
			popDialog.setTitle("Set Gain ");
			popDialog.setView(Viewlayout);
			seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// Do something here with new value
					gain = progress;
					gaintext.setText("Gain: " + progress);
					gain = gain / 100;
				}

				public void onStartTrackingTouch(SeekBar arg0) {
					//  

				}

				public void onStopTrackingTouch(SeekBar seekBar) {
					//  
					gain = seek.getProgress();
					gaintext.setText("Gain: " + gain);
					gain = gain / 100;
				}
			});
			popDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			popDialog.create();
			popDialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		/*
		 * if(!mPlaying){ mPlaying = !mPlaying; stopPlaying(); } if (mRecording)
		 * { mRecording=!mRecording; recorder.stop(); recorder.release();
		 * timeWhenStopped = chronometer.getBase() -
		 * SystemClock.elapsedRealtime(); chronometer.stop(); recorder = null;
		 * recordingThread = null; record2.setEnabled(true);
		 * record.setImageResource(R.drawable.record_button); }
		 */
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"MyPref", MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt("key", i);
		editor.putInt("sampleRate", RECORDER_SAMPLERATE);
		editor.putInt("encoding", RECORDER_AUDIO_ENCODING);
		editor.putBoolean(Constants.ALWAYSPLAY, alwaysPlay);
		editor.putBoolean(Constants.ALWAYSRECORD, alwaysRecord);
		editor.commit();
		super.onStop();
	}

	@Override
	public void onPause() {
		/*
		 * if(mPlaying){ mPlaying = false; stopPlaying(); } if (mRecording) {
		 * mRecording=false; recorder.stop(); recorder.release();
		 * timeWhenStopped = chronometer.getBase() -
		 * SystemClock.elapsedRealtime(); chronometer.stop(); recorder = null;
		 * recordingThread = null; record2.setEnabled(true);
		 * record.setImageResource(R.drawable.record_button); }
		 */
		super.onPause();
	}
}
