/**
 * 
 */
package com.aksharspeech.waverecorder.ui;

import java.io.File;

import android.app.Activity;
import android.media.AudioFormat;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aksharspeech.waverecorder.R;
import com.cybern.test.NewUploader;
import com.cybern.waverecorder.WaveRecorder;

/**
 * @author amitkumarsah
 * 
 */
public class RemoteASR {
	private Activity mAct = null;
	private NewUploader mFileUpload = null;
	private final String mUploadURL = "http://msg2voice.com/ASR/upload.php";
	private final String mDecodeURL = "http://msg2voice.com/ASR/decode.php";
	private boolean mIsRecording = false;
	private WaveRecorder waveRecord = null;

	/**
	 * @param activity
	 */
	public RemoteASR(Activity activity) {
		this.mAct = activity;
		this.mFileUpload = new NewUploader(activity, mUploadURL);
		initRecorder();
	}

	public String getEMINumber() {
		String emiNumber = "MOBILEEMINO";
		return emiNumber;

	}

	public void uploadFile(final String audiofilename) {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				String response = mFileUpload.upLoadFile(mAct, mUploadURL,
						audiofilename, getEMINumber());
				Log.i("THR", response);
				if (response != null && response.contains("CMD_ERROR")) {

					Log.e("UPLOADER", response);
					Looper.myLooper().quit();

				} else {

					doPostUpload(response);
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
		// TODO: do when the uploader had uploaded the file
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();

				String response = mFileUpload.getPostData(mDecodeURL,
						getEMINumber());
				if (response != null && response.contains("CMD_ERROR")) {
					Log.e("doPostUpload_error", response);
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
		// TODO: implement what to do on this post you get data

		// TextView tv = (TextView) mAct.findViewById(R.id.asrTVTextData);
		// tv.setText(response);
		// Toast.makeText(mAct, "ASR got=" + response,
		// Toast.LENGTH_LONG).show();
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
			filename = filename + "lastes.wav";
			// TODO:usefilename is current time
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
