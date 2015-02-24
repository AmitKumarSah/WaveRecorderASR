package com.aksharspeech.waverecorder;

//import com.leaveme.ssad.R;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aksharspeech.waverecorder.util.UTIL;
import com.cybern.files.CyberNFileDialog;

/**
 * @author romil edited by
 * @author amitkumarsah
 * 
 */
public class SelectionMenuActivity extends Activity {
	Button word;
	Button sentence;
	Bundle bundle;

	private boolean _doubleBackToExitPressedOnce = false;
	String mSelectedFileName = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyselectionpage);
		word = (Button) findViewById(R.id.button21);

		sentence = (Button) findViewById(R.id.button22);

	}

	public void onClickSentence(View v) {
		this.onButtonChooseFile(v);

	}

	private void moveToSentenceRecord() {
		if (this.mSelectedFileName != null) {

			File file = new File(this.mSelectedFileName);
			if (file.exists() && file.isFile()
					&& UTIL.verifyTextFile(mSelectedFileName)) {
				// Intent openStartingPoint = new Intent(this, openFile.class);
				Intent openStartingPoint = new Intent(this,
						SentenceRecordingActivity.class);
				openStartingPoint.putExtra("textfilename", mSelectedFileName);
				startActivity(openStartingPoint);
				finish();

			} else {
				Toast.makeText(getApplicationContext(),
						"Kindly Select the vaild input text file...",
						Toast.LENGTH_LONG).show();
				return;
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Kindly Select the input text file...", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void onClickWord(View v) {
		Intent openStartingPoint = new Intent(this, WordRecordingActivity.class);
		startActivity(openStartingPoint);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (_doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this._doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				_doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	public void onButtonChooseFile(View v) {
		// Create FileOpenDialog and register a callback

		String path = UTIL.getPrefernceStringValue(getApplicationContext(),
				"textfilepath", Environment.getExternalStorageDirectory()
						.getAbsolutePath());
		CyberNFileDialog fileOpenDialog = new CyberNFileDialog(this,
				"FileOpen..", new CyberNFileDialog.SimpleFileDialogListener() {
					@Override
					public void onChosenDir(String chosenDir) {
						// The code in this function will be executed when the
						// dialog OK buttonis pushed
						mSelectedFileName = chosenDir;
						// move to next activty
						moveToSentenceRecord();

					}
				});
		// You can change the default filename using the public
		// variable"Default_File_Name"
		if (path.contains(Environment.getExternalStorageDirectory()
				.getAbsolutePath()))
			fileOpenDialog.default_file_name = path;
		else
			fileOpenDialog.default_file_name = Environment
					.getExternalStorageDirectory().getAbsolutePath();
		fileOpenDialog.chooseFile_or_Dir(fileOpenDialog.default_file_name);
	}
}
