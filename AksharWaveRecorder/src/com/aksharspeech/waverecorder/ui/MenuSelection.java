/**
 * 
 */
package com.aksharspeech.waverecorder.ui;

import com.aksharspeech.waverecorder.R;

/**
 * @author amitkumarsah
 * 
 */
public class MenuSelection {

	public void showMenuSelectionButton() {

	}

	public void removeMenuSelectionButton() {

	}

	public void onMenuSelected(int id) {
		switch (id) {
		case R.id.SpbtnSentence:
			moveToSentenceRecord();
			break;
		case R.id.SpbtnWord:
			moveToWordRecord();
			break;
		default:

		}

	}

	public void moveToSentenceRecord() {

	}

	public void moveToWordRecord() {

	}

}
