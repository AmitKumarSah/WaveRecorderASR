
package com.aksharspeech.waverecorder;

import java.io.File;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;

/**
 * @author romil
 *
 */
public class Loadingpage extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.splash);
		
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(2000);
					
					
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				finally{
					moveTo();
				}
			}
		};
		timer.start();
	}
	
	public void moveTo(){
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AksharRecorder");
		folder.mkdir();
		Intent openStartingPoint = new Intent(this, UserActivity.class);
		startActivity(openStartingPoint);
		finish();
	}
	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
		return;
	}
}
