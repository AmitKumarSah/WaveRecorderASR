package com.aksharspeech.waverecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aksharspeech.waverecorder.ui.SentenceRecord;

//import com.leaveme.ssad.R;

/**
 * @author romil
 * 
 * @author amitkumarsah
 * @version 2.0
 * 
 */
public class SentenceRecordingActivity extends Activity {
	private SentenceRecord senRecord;
	private static String mTextFileName = null;
	private Bundle bundle;

	@Override
	public void onBackPressed() {
		// Will stay
		senRecord.cleanUp();
		Intent openStartingPoint = new Intent(this, SelectionMenuActivity.class);
		startActivity(openStartingPoint);
		super.onBackPressed();
		finish();
	}
	
	public void onCBClick(View v){
		senRecord.onCBClick(v);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.specailui);
		bundle = getIntent().getExtras();
		mTextFileName = bundle.getString("textfilename");
		senRecord = new SentenceRecord(this, mTextFileName);
		
		// UIManager userInterface = new UIManager();
		// ListenToPhoneState listener = new ListenToPhoneState(userInterface);
		// TelephonyManager tManager = (TelephonyManager)
		// getSystemService(Context.TELEPHONY_SERVICE);
		// if(tManager != null)
		// tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("REMOVEME", "onDestory_Called");
		if (senRecord != null)
			senRecord.onDestory();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("REMOVEME", "onStart_Called");

		senRecord.showFirstLine();
	}

	public void onClickNext(View v) {

		senRecord.onNextButtonClicked();
	}

	public void startRecording(View v) {
		senRecord.onRecordButtonClicked();
	}

	public void onClickPlay(View v) {
		senRecord.onPlayButtonClicked();
	}

	public void onClickPrev(View v) {

		senRecord.onPreviousButtonClicked();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Stay Here
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// StayHere
		switch (item.getItemId()) {
		case R.id.action_samplerate:
			senRecord.showSampleRateMenu(item);
			break;
		case R.id.action_alwaysplay:
			senRecord.showAlwaysPlay(item);
			break;
		case R.id.action_alwaysrecord:
			senRecord.showAlwaysRecord(item);
			break;
		case R.id.action_audioencoding:
			senRecord.showEncodingMenu(item);
			break;
		case R.id.action_gain:
			senRecord.showGainMenu(item);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("REMOVEME", "onPause_Called");

		if (senRecord != null)
			senRecord.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("REMOVEME", "onStop_Called");

		if (senRecord != null)
			senRecord.onStop();

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("REMOVEME", "onResume_Called");

		if (senRecord != null)
			senRecord.onResume();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i("REMOVEME", "onReStart_Called");

		if (senRecord != null)
			senRecord.onRestart();
	}
}
