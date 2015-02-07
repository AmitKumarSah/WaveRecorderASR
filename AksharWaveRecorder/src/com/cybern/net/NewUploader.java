package com.cybern.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Properties;
/**
 * @author Shirin Joshi
 **/
public class NewUploader {

 private String url;
 Properties mimeTypesProperties;
 public NewUploader(String url) {
  this.url = url;
  mimeTypesProperties = new Properties();
        final String mimetypePropertiesFilename = "/conf/mimetypes.properties";
        try {
            /*
             * mimeTypesProperties.load(getClass().getResourceAsStream(
             * mimetypePropertiesFilename));
             */
            mimeTypesProperties.load(Class.forName("com.joshi.upload.NewUploader").getResourceAsStream(
                    mimetypePropertiesFilename));
            
        } catch (Exception e) {
        }
    }

 public void publish(File allfiles) {
  // URL url = new URL("http://www.domain.com/webems/upload.do");
  // create a boundary string
  try {
   String boundary = MultiPartFormOutputStream.createBoundary();
   URLConnection urlConn = MultiPartFormOutputStream
     .createConnection(url);
   urlConn.setRequestProperty("Accept", "*/*");
   urlConn.setRequestProperty("Content-Type",
     MultiPartFormOutputStream.getContentType(boundary));
   // set some other request headers...
   urlConn.setRequestProperty("Connection", "Keep-Alive");
   urlConn.setRequestProperty("Cache-Control", "no-cache");

   // no need to connect cuz getOutputStream() does it
   MultiPartFormOutputStream out = new MultiPartFormOutputStream(
     urlConn.getOutputStream(), boundary);
   
   // write a text field element
   out.writeField("myText", "text field text data..............");
   // upload a file
   //out.writeFile("myFile", "text/plain", new File("C:\\test.txt"));
   File f =allfiles;
//     Let
       String mimeType = mimeTypesProperties.getProperty(getExtension(f).toLowerCase());
       if (mimeType == null) {
           mimeType = "application/octet-stream";
       }
       System.out.println("mimeType :: " + mimeType);
       out.writeFile("file", mimeType, f);
    //out.writeFile(f.getName(), mimeType, f);
   
   // can also write bytes directly
   // out.writeFile("myFile", "text/plain", "C:\\test.txt",
   // "This is some file text.".getBytes("ASCII"));
   out.close();
   // read response from server
   BufferedReader in = new BufferedReader(new InputStreamReader(
     urlConn.getInputStream()));
   String line = "";
   while ((line = in.readLine()) != null) {
    System.out.println(line);
   }
   in.close();

  } catch (Exception e) {
   System.out.println("Exception -----------");
   e.printStackTrace();
  }
 }

 public static String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
 
 public static void main(String args[]) {
  try {
   File[] fileList = new File("/home/amit/ATest/").listFiles();
   
  for(int i=0;i<fileList.length;i++)
  {
	  System.out.println(fileList[i].toString());
  }
   
   // Your Server name and directory....
   new NewUploader("http://localhost/upload/fileupload.php").publish(fileList[0]);
  } catch (Exception e) {
   e.printStackTrace();
  }

 }
}

