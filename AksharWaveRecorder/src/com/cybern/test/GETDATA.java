/**
 * 
 */
package com.cybern.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author amitkumarsah
 * 
 */
public class GETDATA {

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
		String res = "";

		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConn.getInputStream()));
		while ((line = in.readLine()) != null) {
			res = res + line;
		}
		in.close();
		return res;

	}

}
