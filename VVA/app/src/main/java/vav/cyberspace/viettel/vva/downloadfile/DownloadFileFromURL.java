package vav.cyberspace.viettel.vva.downloadfile;

/**
 * Created by thanhtn10 on 5/16/16.
 */

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import vav.cyberspace.viettel.vva.preference.PreferenceManager;

/**
 * Background Async Task to download file
 * */
public class DownloadFileFromURL{
    private static final int  MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory){

    }
   public String downloadFile(String strUrl, String localeType, String textInput, String savefilePath){
       String result = "";
       PreferenceManager mPreferences = PreferenceManager.getInstance();
       InputStream inputStream = null;
       try {

           // 1. create HttpClient
           HttpClient httpclient = new DefaultHttpClient();

           // 2. make POST request to the given URL
           StringBuilder uri = new StringBuilder(strUrl+"/process?");
           StringBuilder sb = new StringBuilder("INPUT_TYPE=TEXT");
         //  sb.append("INPUT_TYPE=TEXT");
           sb.append("&AUDIO=WAVE_FILE");
           sb.append("&OUTPUT_TYPE=AUDIO");
           sb.append("&LOCALE=");
           sb.append(localeType);
           sb.append("&INPUT_TEXT=");
           sb.append(Uri.encode(textInput));
           sb.append("&effect_FIRFilter_selected=on&effect_FIRFilter_parameters=type%3A3%3Bfc1%3A500.0%3Bfc2%3A3000.0");

           if(mPreferences.getReadHSMM().compareToIgnoreCase("1") == 0)
               sb.append("&VOICE=my_voice-hsmm&effect_Rate_selected=on&effect_Rate_parameters=durScale%3A0.9%3B");
           else
               sb.append("&VOICE=my_voice");

           uri.append(sb.toString());
           HttpGet httpPost = new HttpGet(uri.toString());

           // 8. Execute POST request to the given URL
           HttpResponse httpResponse = httpclient.execute(httpPost);

           // 9. receive response as inputStream
           inputStream = httpResponse.getEntity().getContent();

           // 10. convert inputstream to string
           if(inputStream != null){
               FileOutputStream fos = new FileOutputStream(new File(savefilePath));

               int read = 0;
               byte[] buffer = new byte[32768];
               while( (read = inputStream.read(buffer)) > 0) {
                   fos.write(buffer, 0, read);
               }

               fos.close();
               inputStream.close();
           }

           else
               result = "Error to connect!";

       } catch (Exception e) {
           result = e.getLocalizedMessage();
           Log.d("InputStream", e.getLocalizedMessage());
       }
  /*     try {

           StringBuilder sb = new StringBuilder(strUrl+"/process?");
           sb.append("&INPUT_TYPE=TEXT");
           sb.append("&AUDIO=WAVE_FILE");
           sb.append("&OUTPUT_TYPE=AUDIO");
           sb.append("&LOCALE=");
           sb.append(localeType);
           sb.append("&INPUT_TEXT=");
           sb.append(textInput);

           URL url = new URL(sb.toString());
           HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
           //urlConnection.setRequestMethod("GET");
           //urlConnection.setDoOutput(true);
           urlConnection.connect();
           int status = urlConnection.getResponseCode();
           InputStream inputStream = urlConnection.getInputStream();
           FileOutputStream fileOutputStream = new FileOutputStream(savefilePath);
           int totalSize = urlConnection.getContentLength();

           byte[] buffer = new byte[MEGABYTE];
           int bufferLength = 0;
           while((bufferLength = inputStream.read(buffer))>0 ){
               fileOutputStream.write(buffer, 0, bufferLength);
           }
           fileOutputStream.close();
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
*/
       return  result;
   }

}