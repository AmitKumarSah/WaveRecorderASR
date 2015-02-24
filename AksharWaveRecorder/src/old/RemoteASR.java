/**
 * 
 */
package old;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.aksharspeech.waverecorder.R;
import com.cybern.net.FileUploader;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * RemoteASR Its uses the remote ASR developed by Akshar Speech.
 * 
 * @author amitkumarsah
 * 
 */
public class RemoteASR {
	private long mUIResetTimer = 30;
	private Activity mAct = null;
	private FileUploader mFileUpload = null;
	private static final String RECORDER_FOLDER = "/AksharRecorder";
	private final String mBaseURL = "http://msg2voice.com/ASR/";
	private final String mUploadURL = mBaseURL + "uploader.php";
	private final String mDecodeURL = mBaseURL + "decode.php";
	private boolean mIsRecording = false;
	private boolean mIsDecoding = false;
	private WaveRecorder waveRecord = null;
	private static float mGain = 80;
	private boolean onStopClicked = false;

	/**
	 * Default Constructor.
	 * 
	 * @param activity
	 *            Must pass the current activity refernce.
	 */
	public RemoteASR(Activity activity) {
		this.mAct = activity;
		this.mFileUpload = new FileUploader();
		initRecorder();
		mGain = 80;
		mGain = mGain / 100;
	}

	/**
	 * Its return the IEMI Number of the user phone
	 * 
	 * @return Its return the IEMI Number of the user phone.
	 */
	public String getEMINumber() {
		TelephonyManager telephonyManager = (TelephonyManager) mAct
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * It will upload the wave file to Akshar ASR Server
	 * 
	 * @param audiofilename
	 *            audio file path
	 */
	public void uploadFile(final String audiofilename) {
		if (waveRecord.STATUS_RECORDING) {
			Log.i("AMIT", "STATUS_RECORDING===");

		}
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				doOnUIText("Recognizing.... ");
				mIsDecoding = true;
				File f = new File(audiofilename);
				if (f.exists()) {
					if (f.length() <= 50) {
						doPostError();
						Looper.myLooper().quit();
						return;
					}

				} else {
					doPostError();
					Looper.myLooper().quit();
					return;
				}
				String response = mFileUpload.uploadFile(mUploadURL,
						audiofilename, getEMINumber());
				if (response != null && response.contains("Error")) {
					Log.e("UPLOADER", response);
					doTost(response);
					doPostError();
					Looper.myLooper().quit();

				} else {

					doPostUpload(response);
					doOnUIText("Recognizing....");
					Looper.myLooper().quit();
				}
				Looper.loop();
			}
		}, "FileUploaderThread");
		th.start();

	}

	/**
	 * Its handler for Audio Uploaded to decode the audio file
	 * 
	 * @param response
	 *            Message received from server
	 */
	private void doPostUpload(String response) {
		Log.i("doPostUpload", response);
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				String response = mFileUpload.getPostData(mDecodeURL,
						getEMINumber());
				if (response != null && response.contains("Error")) {
					Log.e("doPostUpload_error", response);
					doTost(response);
					doPostError();
					Looper.myLooper().quit();

				} else {

					doPostData(response);
					Looper.myLooper().quit();
				}
				Looper.loop();
			}
		}, "POST_GET_DATA_THREAD");

		th.start();

	}

	/**
	 * After the Error Message is over.
	 */
	private void doPostError() {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mIsDecoding = false;
				TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
				tv.setText("Speak");

			}
		});

	}

	/**
	 * Its handler for Audio Decoder. It will show on the UI
	 * 
	 * @param response
	 */
	private void doPostData(String response) {
		Log.i("doPostData", response);
		doOnUI("You asked for " + response + ".");
		mIsDecoding = false;
		resetText();

	}

	/**
	 * It will reset the text The TextView UI
	 */
	private void resetText() {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(mUIResetTimer * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				if (mAct != null && !mIsDecoding && !mIsRecording)
					mAct.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TextView tv = (TextView) mAct
									.findViewById(R.id.asrTVTextData);
							tv.setText("Press and Say");

						}
					});

			}
		}, "reset_thread");
		th.start();

	}

	/**
	 * It will change or show the message and update on UI Thread.
	 * 
	 * @param msg
	 */
	private void doOnUI(final String msg) {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
				tv.setText(msg);
				Toast.makeText(mAct, msg, Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void doOnUIText(final String msg) {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
				tv.setText(msg);

			}
		});
	}

	/**
	 * Display message through toast message. It will do in UI thread
	 * 
	 * @param msg
	 */
	private void doTost(final String msg) {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (msg.contains("Error")) {
					Toast.makeText(mAct, "Error(" + msg + ")",
							Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(mAct, msg, Toast.LENGTH_SHORT).show();

			}
		});

	}

	/**
	 * It will record the audio. It onClick function for Record button.
	 */
	public void onRecordClick() {
		if (!mIsRecording) {
			ImageButton btn = (ImageButton) mAct
					.findViewById(R.id.asrBTNRecord);
			btn.setImageResource(R.drawable.ic_action_stopbutton);
			onStopClicked = false;
			startRecording();
			mIsRecording = true;

			checkInBackground();

		} else {
			ImageButton btn = (ImageButton) mAct
					.findViewById(R.id.asrBTNRecord);
			onStopClicked = true;

			stopRecording();
			mIsRecording = false;
			btn.setImageResource(R.drawable.ic_action_recordbutton);

		}

	}

	private void checkInBackground() {
		Thread recordcheck = new Thread(new Runnable() {

			@Override
			public void run() {
				if (waveRecord != null) {
					while (waveRecord.STATUS_RECORDING == false
							&& onStopClicked == false) {
						// DO nothing;
					}
					if (onStopClicked)
						return;
					if (waveRecord.STATUS_RECORDING) {
						waveRecord.STATUS_RECORDING = false;
						stopRecording();
						mIsRecording = false;
						mAct.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ImageButton btn = (ImageButton) mAct
										.findViewById(R.id.asrBTNRecord);
								btn.setImageResource(R.drawable.ic_action_recordbutton);

							}
						});
					} else {
						Log.i("Amit", "status is true");
					}

				} else {
					Log.i("Amit", "waverecorde is null");
				}

			}
		}, "RECORD_CHECK");
		recordcheck.start();
	}

	/**
	 * It will stop recording and will call the uploader to decode the audio
	 */
	private void stopRecording() {

		if (mIsRecording) {
			onStopClicked = true;
			String filename = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			filename = filename + RECORDER_FOLDER + "/ASR/";
			new File(filename).mkdirs();
			String currentDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
					Locale.getDefault()).format(new Date());
			filename = filename + currentDate + ".wav";
			WaveRecorder.setAudioFileName(filename);
			waveRecord.stopRecord();
			while (waveRecord.ismIsRecording()) {

			}
			if (!waveRecord.ismIsRecording()) {

				uploadFile(WaveRecorder.getAudioFileName());

			} else {
				Toast.makeText(mAct, "Kindly say is again!", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	void handleCancle() {
		mAct.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
				tv.setText("Press and Say");
				ImageButton btn = (ImageButton) mAct
						.findViewById(R.id.asrBTNRecord);
				btn.setImageResource(R.drawable.ic_action_recordbutton);

			}
		});
	}

	/**
	 * Start the recording
	 */
	private void startRecording() {
		if (!mIsRecording) {
			doOnUIText("Listening....");
			mIsDecoding = true;
			waveRecord.startRecord(mGain);
		}
	}

	/**
	 * It init the recorder with basic settings
	 */
	private void initRecorder() {
		waveRecord = new WaveRecorder();//
		WaveRecorder.setParameters(16000, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 16);//
		WaveRecorder.setAudioFolder(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + RECORDER_FOLDER);//
		waveRecord.setAudioTempFile();//
		waveRecord.initWaveRecorder();//
	}

	/**
	 * Call on destroy of the activity. It clean up all variables
	 */
	public void onDestory() {
		if (waveRecord != null) {
			waveRecord.cancelWaveRecord();
			waveRecord = null;
		}
		if (mFileUpload != null) {
			mFileUpload = null;
		}
	}

	/**
	 * Not implemented
	 */
	public void onResume() {

	}

	/**
	 * Not implemented
	 */
	public void onRestart() {

	}

}
