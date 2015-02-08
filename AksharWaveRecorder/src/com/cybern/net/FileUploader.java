/**
 * 
 */
package com.cybern.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Properties;
import android.util.Log;

/**
 * File uploader:
 * 
 * It will upload the file to webserver.
 * 
 * @author amitkumarsah
 * @version 1.0
 * @since 4.5
 * 
 */
public class FileUploader {
	private Properties mimeTypesProperties;

	/* Android Addition */
	/**
	 * Deafult constructor
	 * 
	 * @param act
	 * @param url
	 */

	public FileUploader() {
		mimeTypesProperties = new Properties();
		final String mimetypePropertiesFilename = "/conf/mimetypes.properties";
		try {
			/*
			 * mimeTypesProperties.load(getClass().getResourceAsStream(
			 * mimetypePropertiesFilename));
			 */
			mimeTypesProperties.load(Class
					.forName("com.cybern.net.NetUploader").getResourceAsStream(
							mimetypePropertiesFilename));

		} catch (Exception e) {
		}
	}

	private void toastit(String msg) {

		Log.i("CONTROL", msg);
	}

	public String uploadFile(String url, String filename, String imieno) {
		try {
			File fileList = new File(filename);
			if (fileList.exists()) {
				Log.i("FileUploader.uploadFile", fileList.getAbsolutePath());

			} else
				return "Error Audiofile not exists. Kindly try again ";
			return publish(url, fileList, imieno);
		} catch (Exception e) {
			return "Error occured. Kindly try again";
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
	 * Upload the file to server
	 * 
	 * @param url
	 * @param files
	 * @param imieno
	 */
	private String publish(String url, File files, String imieno) {

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
			out.writeField("mobileemi", imieno);
			File f = files;
			String mimeType = mimeTypesProperties.getProperty(getExtension(f)
					.toLowerCase(Locale.getDefault()));
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			out.writeFile("file", mimeType, f);
			out.close();
			toastit("Waiting to get upload the file");
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
			return "Error Occured While Uploading Your file to Server.";
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
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR_Failed_TO_GET_DATA";
		}
	}

	private String getData(String url) {

		try {
			String boundary = MultiPartFormOutputStream.createBoundary();
			URLConnection urlConn = MultiPartFormOutputStream
					.createConnection(url);
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));
			urlConn.setRequestProperty("Connection", "Keep-Alive");
			urlConn.setRequestProperty("Cache-Control", "no-cache");
			/*
			 * MultiPartFormOutputStream out = new MultiPartFormOutputStream(
			 * urlConn.getOutputStream(), boundary);
			 * out.writeField("ASR_DEVICE_TYPE", "ANDROID_MOBILE");
			 * out.writeField("mobileemi", imieno); String mimeType =
			 * mimeTypesProperties.getProperty(getExtension(f)
			 * .toLowerCase(Locale.getDefault())); if (mimeType == null) {
			 * mimeType = "application/octet-stream"; } out.close();
			 */
			toastit("Waiting to get data from server");
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
			return "Error Occured While Getting Your file from Server.";
		}

	}

}
