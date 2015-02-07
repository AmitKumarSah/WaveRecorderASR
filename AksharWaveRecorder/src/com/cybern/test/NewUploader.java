package com.cybern.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Properties;

import android.app.Activity;
import android.util.Log;

public class NewUploader {

	private String url;
	private String mobileEMI;
	Properties mimeTypesProperties;

	/* Android Addition */
	Activity mAct;

	/**
	 * Deafult constructor
	 * 
	 * @param act
	 * @param url
	 * @param emino
	 */
	public NewUploader(Activity act, String url, String emino) {
		this.url = url;
		this.mAct = act;
		this.mobileEMI = emino;
		mimeTypesProperties = new Properties();
		final String mimetypePropertiesFilename = "/conf/mimetypes.properties";
		try {
			/*
			 * mimeTypesProperties.load(getClass().getResourceAsStream(
			 * mimetypePropertiesFilename));
			 */
			mimeTypesProperties.load(Class.forName(
					"com.cybern.test.NewUploader").getResourceAsStream(
					mimetypePropertiesFilename));

		} catch (Exception e) {
		}
		toastit("url=" + url);
	}

	public NewUploader(Activity act, String url) {
		this.url = url;
		this.mAct = act;
		mimeTypesProperties = new Properties();
		final String mimetypePropertiesFilename = "/conf/mimetypes.properties";
		try {
			/*
			 * mimeTypesProperties.load(getClass().getResourceAsStream(
			 * mimetypePropertiesFilename));
			 */
			mimeTypesProperties.load(Class.forName(
					"com.cybern.test.NewUploader").getResourceAsStream(
					mimetypePropertiesFilename));

		} catch (Exception e) {
		}
		toastit("url=" + url);
	}

	private void toastit(String msg) {

		Log.i("CONTROL", msg);
	}

	public String publish(File allfiles) {

		try {
			String boundary = MultiPartFormOutputStream.createBoundary();
			URLConnection urlConn = MultiPartFormOutputStream
					.createConnection(url);
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			urlConn.setRequestProperty("Connection", "Keep-Alive");
			urlConn.setRequestProperty("Cache-Control", "no-cache");

			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					urlConn.getOutputStream(), boundary);

			out.writeField("myText", "text field text data..............");
			out.writeField("phone", this.mobileEMI);
			toastit("Phones" + mobileEMI);
			File f = allfiles;
			String mimeType = mimeTypesProperties.getProperty(getExtension(f)
					.toLowerCase(Locale.getDefault()));
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			toastit("mimeType :: " + mimeType);
			out.writeFile("file", mimeType, f);
			out.close();
			// read response from server
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			String line = "";
			String res = "";
			while ((line = in.readLine()) != null) {
				toastit(line);
				res = res + line;
			}
			in.close();
			return res;

		} catch (Exception e) {
			toastit("Exception -----------");
			e.printStackTrace();
			return "An Error Occured While Uploading Your file to Server.";
		}
	}

	/**
	 * get The extension of the file
	 * 
	 * @param file
	 * @return
	 */
	public static String getExtension(File file) {
		String name = file.getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * upload file
	 * 
	 * @param path
	 * @return
	 */
	public String uploadFiles(String path) {
		try {
			File fileList = new File(path);
			return publish(fileList);
		} catch (Exception e) {
			e.printStackTrace();
			return "An Error occured while while reading you file";
		}

	}

	/**
	 * getPostData
	 * 
	 * @param url
	 * @param emi
	 * @return
	 */
	public String getPostData(String url, String emi) {
		try {
			return this.getData(url);
			//NewUploader upload = new NewUploader(mAct, url, emi);
			//return upload.publish(emi);
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR_Failed_TO_GET_DATA";
		}
	}

	/**
	 * upload the file to server .. custom created for ASR
	 * 
	 * @param act
	 * @param url
	 * @param path
	 * @param emino
	 * @return
	 */
	public String upLoadFile(Activity act, String url, String path, String emino) {
		try {
			toastit(url);
			toastit(path);
			File fileList = new File(path);
			mAct = act;
			if (fileList.exists())
				Log.i("NewUploader.UpLoadFile", fileList.getAbsolutePath());
			NewUploader uploader = new NewUploader(act, url, emino);
			return uploader.publish(fileList, emino);
		} catch (Exception e) {
			e.printStackTrace();
			return "An Error occured while while reading you file";
		}

	}

	public String publish(String emino) {

		try {
			String boundary = MultiPartFormOutputStream.createBoundary();
			URLConnection urlConn = MultiPartFormOutputStream
					.createConnection(url);
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			urlConn.setRequestProperty("Connection", "Keep-Alive");
			urlConn.setRequestProperty("Cache-Control", "no-cache");

			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					urlConn.getOutputStream(), boundary);

			// out.writeField("ASR_DEVICE_TYPE", "ANDROID_MOBILE");
			// out.writeField("mobileemi", emino);
			// out.writeField("ASR_CMD", "getASRText");

			toastit("Phones" + emino);

			out.close();
			toastit("closedok");
			// read response from server
			String line = "";
			String res = "CMD=";

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = in.readLine()) != null) {
				res = res + line;
			}
			in.close();
			return res;

		} catch (Exception e) {
			toastit("Exception -----------");
			e.printStackTrace();
			return "An Error Occured While Uploading Your file to Server.";
		}
	}

	/**
	 * @param allfiles
	 * @param emino
	 */

	public String publish(File allfiles, String emino) {

		try {
			String boundary = MultiPartFormOutputStream.createBoundary();
			URLConnection urlConn = MultiPartFormOutputStream
					.createConnection(url);
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			urlConn.setRequestProperty("Connection", "Keep-Alive");
			urlConn.setRequestProperty("Cache-Control", "no-cache");

			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					urlConn.getOutputStream(), boundary);

			out.writeField("ASR_DEVICE_TYPE", "ANDROID_MOBILE");
			out.writeField("mobileemi", emino);
			out.writeField("fname", allfiles.getName());
			Log.i("publist", emino);
			File f = allfiles;
			String mimeType = mimeTypesProperties.getProperty(getExtension(f)
					.toLowerCase(Locale.getDefault()));
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			toastit("mimeType :: " + mimeType);
			out.writeFile("file", mimeType, f);
			out.close();
			toastit("fileclosed");
			// read response from server
			String line = "";
			String res = "";

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = in.readLine()) != null) {
				res = res + line;
			}
			in.close();
			return res;

		} catch (Exception e) {
			toastit("Exception -----------" + e.getMessage());
			e.printStackTrace();
			return "An Error Occured While Uploading Your file to Server.";
		}
	}
	
	public String getData(String strUrl) throws IOException {
		URL url = new URL(strUrl);
		URLConnection urlConn = url.openConnection();
		if (urlConn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			httpConn.setRequestMethod("GET");
		}
		
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setDefaultUseCaches(false);
		urlConn.setRequestProperty("Accept", "*/*");
		String boundary = MultiPartFormOutputStream.createBoundary();
		urlConn.setRequestProperty("Content-Type",
				MultiPartFormOutputStream.getContentType(boundary));
		urlConn.setRequestProperty("Connection", "Keep-Alive");
		urlConn.setRequestProperty("Cache-Control", "no-cache");
		String line = "";
		String res = "CMD=";
		toastit("wow_getData");

		try{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConn.getInputStream()));
		while ((line = in.readLine()) != null) {
			res = res + line;
		}
		in.close();
		}catch(Exception e){
			e.printStackTrace();
			Log.e("ERROR", "in_error="+e.getMessage());
			return res+"error";
		}
		return res;

	}

}
