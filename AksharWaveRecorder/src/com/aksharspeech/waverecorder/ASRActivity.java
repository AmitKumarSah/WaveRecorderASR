/**
 * 
 */
package com.aksharspeech.waverecorder;

import com.aksharspeech.waverecorder.ui.RemoteASR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author amitkumarsah
 *
 */
public class ASRActivity extends Activity  {
	
	RemoteASR mASR=null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyasr);
		mASR= new RemoteASR(this);
		

	}
	public void onAsrClick(View v){
		mASR.onRecordClick();
	}
	@Override
	public void onBackPressed() {
		Intent openStartingPoint = new Intent(this, SelectionMenuActivity.class);
		startActivity(openStartingPoint);
		super.onBackPressed();
		finish();
	}

}
