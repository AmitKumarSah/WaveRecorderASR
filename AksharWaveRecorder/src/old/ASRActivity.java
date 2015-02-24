package old;

import com.aksharspeech.waverecorder.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;


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
		mASR = new RemoteASR(this);

	}

	public void onAsrClick(View v) {
		mASR.onRecordClick();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		//cnSpeechRecoz = new RemoteASR(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mASR != null)
			mASR.onDestory();
		mASR = null;
	}

	@Override
	protected void onStop() {
		super.onStop();

//		if (cnSpeechRecoz != null)
//			cnSpeechRecoz.onDestory();
//		cnSpeechRecoz = null;

	}

	@Override
	public void onResume() {
		super.onResume();

		// if (cnSpeechRecoz != null)
		// cnSpeechRecoz.onResume();
	}

	@Override
	public void onRestart() {
		super.onRestart();

//		if (cnSpeechRecoz != null)
//			cnSpeechRecoz.onRestart();
	}

}
