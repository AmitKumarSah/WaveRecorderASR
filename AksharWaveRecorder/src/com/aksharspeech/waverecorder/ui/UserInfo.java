package com.aksharspeech.waverecorder.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aksharspeech.waverecorder.R;
import com.aksharspeech.waverecorder.config.Constants;

/**
 * 
 * @author amitkumarsah
 * 
 */

public class UserInfo {
	/**
	 * @param mAct
	 */
	public UserInfo(Activity mAct) {
		this.mAct = mAct;
	}

	private EditText editTextVar;
	private Activity mAct;
	public String sPersonName;
	public int iAge;
	public String sDate;
	public String sEducation;
	public String sEniv;
	public String sGender;
	public String sLang;
	public String sPhoneModel;

	Spinner spinner;

	public String sPlace;

	public String sType;

	public String sUserName;

	/**
	 * 
	 */
	public void addUserInfo() {

		if (this.validateForms() == false) {
			Toast.makeText(mAct.getApplicationContext(),
					"Enter The Correct Details", Toast.LENGTH_LONG).show();
			return;
		}
		UserInfo info = new UserInfo(mAct);
		info.sPersonName = getTextData(R.id.infoetFirstname);
		info.sUserName = getTextData(R.id.etUserName);
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
		new File(path).mkdirs();

		try {
			info.writeInfoToDisk(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		saveUserInfo(mAct);

	}

	/**
	 * @return the iAge
	 */
	public int getiAge() {
		return iAge;
	}

	/**
	 * @return the sDate
	 */
	public String getsDate() {
		return sDate;
	}

	/**
	 * @return the sEducation
	 */
	public String getsEducation() {
		return sEducation;
	}

	/**
	 * @return the sEniv
	 */
	public String getsEniv() {
		return sEniv;
	}

	/**
	 * @return the sGender
	 */
	public String getsGender() {
		return sGender;
	}

	/**
	 * @return the sLang
	 */
	public String getsLang() {
		return sLang;
	}

	/**
	 * @return the sPhoneModel
	 */
	public String getsPhoneModel() {
		return sPhoneModel;
	}

	/**
	 * @return the sPlace
	 */
	public String getsPlace() {
		return sPlace;
	}

	/**
	 * @return the sType
	 */
	public String getsType() {
		return sType;
	}

	/**
	 * @return the sUserName
	 */
	public String getsUserName() {
		return sUserName;
	}

	public String getsPersonName() {
		return sPersonName;
	}

	public void setsPersonName(String name) {
		this.sPersonName = name;
	}

	public String getTextData(int id) {
		editTextVar = (EditText) mAct.findViewById(id);
		return editTextVar.getText().toString().trim();
	}

	public String getTextSpinner(int id) {
		spinner = (Spinner) mAct.findViewById(id);
		return spinner.getSelectedItem().toString().trim();

	}

	/**
	 * add the deafault settings to context
	 * @param context
	 */
	public void addDeafaultSetting(Context context) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(Constants.ALWAYSRECORD, true);
		editor.putBoolean(Constants.ALWAYSPLAY, false);
		editor.putInt("bpp", 16);
		editor.putFloat("gain", 80);
		editor.putInt("channel", AudioFormat.CHANNEL_IN_MONO);
		editor.putInt("samplerate", 16000);
		editor.putInt("encoding", AudioFormat.ENCODING_PCM_16BIT);
		editor.commit();

	}

	public void saveUserInfo(Context context) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("username", this.sUserName);
		editor.putString("personname", sPersonName);
		editor.putInt("age", this.iAge);
		editor.putString("gender", this.sGender);
		editor.putString("type", this.sType);
		editor.putString("language", this.sLang);
		editor.putString("date", this.sDate);
		editor.putString("education", this.sEducation);
		editor.putString("eniv", this.sEniv);
		editor.putString("place", this.sPlace);
		editor.putString("phonemodel", this.sPhoneModel);
		editor.commit();
	}

	/**
	 * @param iAge
	 *            the iAge to set
	 */
	public void setiAge(int iAge) {
		this.iAge = iAge;
	}

	/**
	 * @param sDate
	 *            the sDate to set
	 */
	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	/**
	 * @param sEducation
	 *            the sEducation to set
	 */
	public void setsEducation(String sEducation) {
		this.sEducation = sEducation;
	}

	/**
	 * @param sEniv
	 *            the sEniv to set
	 */
	public void setsEniv(String sEniv) {
		this.sEniv = sEniv;
	}

	/**
	 * @param sGender
	 *            the sGender to set
	 */
	public void setsGender(String sGender) {
		this.sGender = sGender;
	}

	/**
	 * @param sLang
	 *            the sLang to set
	 */
	public void setsLang(String sLang) {
		this.sLang = sLang;
	}

	/**
	 * @param sPhoneModel
	 *            the sPhoneModel to set
	 */
	public void setsPhoneModel(String sPhoneModel) {
		this.sPhoneModel = sPhoneModel;
	}

	/**
	 * @param sPlace
	 *            the sPlace to set
	 */
	public void setsPlace(String sPlace) {
		this.sPlace = sPlace;
	}

	/**
	 * @param sType
	 *            the sType to set
	 */
	public void setsType(String sType) {
		this.sType = sType;
	}

	/**
	 * @param sUserName
	 *            the sUserName to set
	 */
	public void setsUserName(String sUserName) {
		this.sUserName = sUserName;
	}

	public boolean validateForms() {

		if (getTextData(R.id.etAge) == null
				|| getTextData(R.id.etAge).length() < 1) {
			int age = Integer.parseInt(getTextData(R.id.etAge));
			if (age < 1 && age > 99) {
				Toast.makeText(mAct.getApplicationContext(), "Enter valid age",
						Toast.LENGTH_LONG).show();
				return false;
			}

			return false;
		}
		if (getTextData(R.id.infoetFirstname) == null
				|| getTextData(R.id.infoetFirstname).length() <= 0) {
			return false;
		}
		if (getTextData(R.id.etPlace) == null
				|| getTextData(R.id.etPlace).length() < 4)
			return false;
		if (getTextData(R.id.etUserName) == null
				|| getTextData(R.id.etUserName).length() < 3)
			return false;

		if (getTextSpinner(R.id.spEducation) == null
				|| getTextSpinner(R.id.spEducation).length() < 1)
			return false;
		if (getTextSpinner(R.id.spEniv) == null
				|| getTextSpinner(R.id.spEniv).length() < 1)
			return false;

		if (getTextSpinner(R.id.spGender) == null
				|| getTextSpinner(R.id.spGender).length() < 1)
			return false;
		if (getTextSpinner(R.id.spLang) == null
				|| getTextSpinner(R.id.spLang).length() < 1)
			return false;
		if (getTextSpinner(R.id.spPhoneModel) == null
				|| getTextSpinner(R.id.spPhoneModel).length() < 1)
			return false;
		if (getTextSpinner(R.id.spType) == null
				|| getTextSpinner(R.id.spType).length() < 1)
			return false;

		return true;
	}

	public static UserInfo getSavedUserInfo(Activity act) {
		UserInfo info = new UserInfo(act);
		SharedPreferences pref = act.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		info.iAge = pref.getInt("age", 25);
		info.sDate = pref.getString("date", "10/10/2015");
		info.sEducation = pref.getString("education", "post");
		info.sEniv = pref.getString("eniv", "android");
		info.sGender = pref.getString("gender", "male");
		info.sLang = pref.getString("language", "hindi");
		info.sPhoneModel = pref.getString("phonemodel", "highend");
		info.sPlace = pref.getString("place", "Hyderabad");
		info.sType = pref.getString("type", "distance");
		info.sUserName = pref.getString("username", "akshartest");
		info.sPersonName = pref.getString("personname", "Akshar Tester");

		return info;
	}

	public boolean saveUserInfoToPref() {
		SharedPreferences pref = mAct.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("username", this.sUserName);
		editor.putString("date", this.sDate);
		editor.putString("education", this.sEducation);
		editor.putString("eniv", this.sEniv);
		editor.putString("gender", this.sGender);
		editor.putString("lang", this.sLang);
		editor.putString("phonemodel", this.sPhoneModel);
		editor.putString("place", this.sPlace);
		editor.putString("type", this.sType);
		editor.putInt("age", this.iAge);
		editor.putString("personname", this.sPersonName);

		return editor.commit();
	}

	public void writeInfoToDisk(String path) throws IOException {

		if (new File(path).exists() == false) {
			new File(path).mkdirs();
		}

		File file = new File(path + "/info.txt");
		if (file.exists()) {
			file.delete();
			file.createNewFile();

		} else
			file.createNewFile();

		OutputStreamWriter osw;
		osw = new OutputStreamWriter(new FileOutputStream(file));
		String data = "Name: " + this.sUserName + "\nPerson Name: "
				+ this.sPersonName + "\nAge: " + this.iAge + "\nEnvironment: "
				+ this.sEniv + "\nPhoneModel: " + this.sPhoneModel;
		data = data + "\nDate: " + this.sDate + "\nLanguage: " + this.sLang
				+ "\nPlace: " + this.sPlace;
		data = data + "\nEducation: " + this.sEducation + "\nGender: "
				+ this.sGender + "\nType: " + this.sType;
		osw.write(data);
		osw.flush();
		osw.close();
	}

}
