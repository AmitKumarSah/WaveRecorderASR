/**
 * 
 */
package com.aksharspeech.waverecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aksharspeech.waverecorder.ui.RemoteASR;

/**
 * @author amitkumarsah
 * 
 */
public class ASRActivity extends Activity {

	RemoteASR mASR = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyasr);

	}

	public void onAsrClick(View v) {
		mASR.onRecordClick();
	}

	@Override
	public void onBackPressed() {
		Intent openStartingPoint = new Intent(this, SelectionMenuActivity.class);
		startActivity(openStartingPoint);
		super.onBackPressed();
		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		mASR = new RemoteASR(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mASR != null)
			mASR.onDestory();
		mASR = null;
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mASR != null)
			mASR.onDestory();
		mASR = null;

	}

	@Override
	public void onResume() {
		super.onResume();

		if (mASR != null)
			mASR.onResume();
	}

	@Override
	public void onRestart() {
		super.onRestart();

		if (mASR != null)
			mASR.onRestart();
	}

}
