/**
 * 
 */
package com.aksharspeech.waverecorder.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.media.AudioFormat;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aksharspeech.waverecorder.R;
import com.cybern.net.NetUploader;
import com.cybern.waverecorder.WaveRecorder;

/**
 * @author amitkumarsah
 * 
 */
public class RemoteASR {
	private Activity mAct = null;
	private NetUploader mFileUpload = null;
	// private final String mBaseURL="http://ravi.iiit.ac.in/ASR/";
	private final String mBaseURL = "http://msg2voice.com/ASR/";
	private final String mUploadURL = mBaseURL + "uploader.php";
	private final String mDecodeURL = mBaseURL + "decode.php";
	private boolean mIsRecording = false;
	private WaveRecorder waveRecord = null;

	/**
	 * @param activity
	 */
	public RemoteASR(Activity activity) {
		this.mAct = activity;
		this.mFileUpload = new NetUploader(activity, mUploadURL);
		initRecorder();
	}

	public String getEMINumber() {
		String emiNumber = "MOBILEEMINO";

		TelephonyManager telephonyManager = (TelephonyManager) mAct
				.getSystemService(mAct.TELEPHONY_SERVICE);
		emiNumber = telephonyManager.getDeviceId();
		Log.i("EMI_NO", emiNumber);
		return emiNumber;

	}

	public void uploadFile(final String audiofilename) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				String response = mFileUpload.upLoadFile(mAct, mUploadURL,
						audiofilename, getEMINumber());

				if (response != null && response.contains("CMD_ERROR")) {

					Log.e("UPLOADER", response);
					doTost("Error=" + response);
					Looper.myLooper().quit();

				} else {

					doPostUpload(response);
					doOnUI("Uploading done, Got Response=" + response);
					Looper.myLooper().quit();
				}
				Looper.loop();
			}
		}, "FileUploaderThread");
		th.start();
		Log.i("filte", "started");

	}

	private void doPostUpload(String response) {
		Log.i("doPostUpload", response);
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				String response = mFileUpload.getPostData(mDecodeURL,
						getEMINumber());
				if (response != null && response.contains("CMD_ERROR")) {
					Log.e("doPostUpload_error", response);
					doTost("Error=" + response);
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

	private void doPostData(String response) {
		Log.i("doPostData", response);
		doOnUI("Text=" + response);
		resetText();

	}

	private void resetText() {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(120 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				doOnUI("Kindly Press the record button");

			}
		}, "reset_thread");
		th.start();
	}

	private void doOnUI(final String msg) {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
				tv.setText(msg);
				Toast.makeText(mAct, "MSG=" + msg, Toast.LENGTH_LONG).show();

			}
		});
	}

	private void doTost(final String msg) {
		mAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mAct, "MSG=" + msg, Toast.LENGTH_LONG).show();

			}
		});

	}

	public void onRecordClick() {
		if (!mIsRecording) {
			ImageButton btn = (ImageButton) mAct
					.findViewById(R.id.asrBTNRecord);
			btn.setImageResource(R.drawable.ic_action_stopbutton);
			startRecording();
			mIsRecording = true;

		} else {
			ImageButton btn = (ImageButton) mAct
					.findViewById(R.id.asrBTNRecord);

			stopRecording();
			mIsRecording = false;
			btn.setImageResource(R.drawable.ic_action_recordbutton);

		}

	}

	private static final String RECORDER_FOLDER = "/AksharRecorder";

	private void stopRecording() {

		if (mIsRecording) {
			String filename = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			filename = filename + RECORDER_FOLDER + "/ASR/";
			new File(filename).mkdirs();
			String currentDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
					Locale.getDefault()).format(new Date());
			filename = filename + currentDate+".wav";
			WaveRecorder.setAudioFileName(filename);
			waveRecord.stopRecord();
			while (waveRecord.ismIsRecording()) {

			}
			if (!waveRecord.ismIsRecording()) {

				uploadFile(WaveRecorder.getAudioFileName());

			} else {
				// TODO: handle error sitution
			}
		}

	}

	private void startRecording() {

		if (!mIsRecording)
			waveRecord.startRecord(80);

	}

	private void initRecorder() {
		waveRecord = new WaveRecorder();
		WaveRecorder.setParameters(16000, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 16);
		WaveRecorder.setAudioFolder(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + RECORDER_FOLDER);
		waveRecord.setTempFilename();

	}

}
