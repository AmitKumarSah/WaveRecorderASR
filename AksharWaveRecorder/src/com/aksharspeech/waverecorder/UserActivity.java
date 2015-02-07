package com.aksharspeech.waverecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aksharspeech.waverecorder.ui.UserInfo;

/**
 * @author romil
 * @author amitkumarsah
 * @version 2.0
 * 
 */
public class UserActivity extends Activity {
	EditText editTextVar;
	Spinner spinner;

	private boolean _doubleBackToExitPressedOnce = false;

	public void okClick(View v) {

		if (this.validateForms() == false) {
			Toast.makeText(getApplicationContext(),
					"Enter The Correct Details", Toast.LENGTH_LONG).show();
			return;
		}
		
		UserInfo info = new UserInfo(this);
		//TODO: Bug Setting 01 solved. Asked by Harish
		info.addDeafaultSetting(getApplicationContext());
		info.sUserName = getTextData(R.id.etUserName);
		info.sPersonName = getTextData(R.id.infoetFirstname);
		info.iAge = Integer.parseInt(getTextData(R.id.etAge));
		info.sDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
				.format(Calendar.getInstance().getTime());
		info.sPlace = getTextData(R.id.etPlace);
		info.sEducation = getTextSpinner(R.id.spEducation);
		info.sEniv = getTextSpinner(R.id.spEniv);
		info.sGender = getTextSpinner(R.id.spGender);
		info.sLang = getTextSpinner(R.id.spLang);
		info.sPhoneModel = getTextSpinner(R.id.spPhoneModel);
		info.sType = getTextSpinner(R.id.spType);
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		path = path + "/AksharRecorder/" + info.sUserName + "_" + info.sGender
				+ "_" + info.iAge + "_" + info.sLang + "_" + info.sType;

		try {
			new File(path).mkdirs();
			info.writeInfoToDisk(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		info.saveUserInfo(this);
		Intent openStartingPoint = new Intent(this, SelectionMenuActivity.class);
		startActivity(openStartingPoint);
		finish();

	}

	private boolean validateTextElement(int id) {
		String text = getTextData(id);
		if (text == null)
			return false;
		if (text.length() <= 0)
			return false;
		return true;
	}

	private boolean validateSpiner(int id) {
		String text = getTextSpinner(id);
		if (text == null)
			return false;
		if (text.length() <= 0)
			return false;
		return true;
	}

	public boolean validateForms() {
		if (validateTextElement(R.id.etUserName) == false)
			return false;
		if (validateTextElement(R.id.infoetFirstname) == false)
			return false;

		if (validateTextElement(R.id.etAge) == true) {

			String tage = getTextData(R.id.etAge);
			if (tage.equals(""))
				return false;
			try {
				int age = Integer.parseInt(tage);
				if (age < 1 && age > 99) {
					Toast.makeText(getApplicationContext(), "Enter valid age",
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (NumberFormatException e) {
				return false;

			}

		} else
			return false;

		if (validateTextElement(R.id.etPlace) == false)
			return false;

		if (validateSpiner(R.id.spEducation) == false)
			return false;
		if (validateSpiner(R.id.spEniv) == false)
			return false;

		if (validateSpiner(R.id.spGender) == false)
			return false;
		if (validateSpiner(R.id.spLang) == false)
			return false;

		if (validateSpiner(R.id.spPhoneModel) == false)
			return false;
		if (validateSpiner(R.id.spType) == false)
			return false;

		return true;
	}

	public String getTextSpinner(int id) {
		spinner = (Spinner) findViewById(id);
		return spinner.getSelectedItem().toString().trim();

	}

	@SuppressWarnings("unchecked")
	public void setTextSpinner(int id, String value) {
		spinner = (Spinner) findViewById(id);
		spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter())
				.getPosition(value));

	}

	public String getTextData(int id) {
		editTextVar = (EditText) findViewById(id);
		return editTextVar.getText().toString().trim();
	}

	public void setTextData(int id, String value) {
		editTextVar = (EditText) findViewById(id);
		editTextVar.setText(value);// .toString().trim();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyuserinfo);

	}

	public void onLastUser(View v) {
		showLastUser();
	}

	private void showLastUser() {

		UserInfo info = UserInfo.getSavedUserInfo(this);
		setTextData(R.id.etUserName, info.sUserName);
		setTextData(R.id.infoetFirstname,info.sPersonName);
		setTextData(R.id.etAge, "" + info.iAge);
		setTextData(R.id.etPlace, info.sPlace);

		setTextSpinner(R.id.spEducation, info.sEducation);
		setTextSpinner(R.id.spEniv, info.sEniv);

		setTextSpinner(R.id.spGender, info.sGender);
		setTextSpinner(R.id.spLang, info.sLang);
		setTextSpinner(R.id.spPhoneModel, info.sPhoneModel);
		setTextSpinner(R.id.spType, info.sType);

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

	@Override
	protected void onStop() {
		super.onStop();
	}

}
