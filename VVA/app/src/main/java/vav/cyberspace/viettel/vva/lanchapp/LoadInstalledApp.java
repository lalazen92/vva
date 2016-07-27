package vav.cyberspace.viettel.vva.lanchapp;

import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import vav.cyberspace.viettel.vva.VavActivity;
import vav.cyberspace.viettel.vva.downloadfile.DownloadFileFromURL;
import vav.cyberspace.viettel.vva.preference.PreferenceManager;
import vav.cyberspace.viettel.vva.utils.NormalizerSynthesis;

/**
 * Created by thanhtn10 on 3/8/16.
 */
public class LoadInstalledApp {
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    public static boolean mPlayingSynthesiFile = false;

    public static Handler mHandler;
    private Messenger messageHandler = null;
    private PreferenceManager mPreferences;
    //static  public Map<String, String> packageInfoList = new HashMap<String,String>();
    private static MediaPlayer mp = new MediaPlayer();
    public LoadInstalledApp(){
        if(messageHandler == null)
            messageHandler = new Messenger(mHandler);
        mPreferences = PreferenceManager.getInstance();


    }
    static public ArrayList<PInfo> packageInfoList;
    public void launchApp(String packageName, Context context) {
        Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(
                packageName);
        if (mIntent != null) {
            try {
                context.startActivity(mIntent);
            } catch (ActivityNotFoundException err) {
                Toast t = Toast.makeText(context.getApplicationContext(),
                        "Lanch Application", Toast.LENGTH_SHORT);
                t.show();
            }
        }else if(packageName.contains("settings")){
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        }
        else if(packageName.contains("contacts")){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else if(packageName.contains("message")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage("com.google.android.apps.messaging");
            context.startActivity(intent);

        }  /*else if(packageName.contains("camera360")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage("vStudio.Android.Camera360");
            context.startActivity(intent);

        }*/
    }

    public String getPackNameInfo(String description){
        description = description.trim();
        description = description.toLowerCase();
        for (int i = 0; i < packageInfoList.size(); i++){
            String appname = packageInfoList.get(i).appName.toLowerCase();
            if(appname.compareToIgnoreCase(description) == 0){
                return packageInfoList.get(i).pName;
            }
        }
        String deslist [] = description.split(" ");
        String result = "";
        for (int k = 0; k < deslist.length; k++){
            for (int i = 0; i < packageInfoList.size(); i++){
                String appname = packageInfoList.get(i).appName.toLowerCase();
                String pName = packageInfoList.get(i).pName.toLowerCase();
                boolean check = false;
                if(deslist[k].compareToIgnoreCase(appname)==0 || appname.compareToIgnoreCase(deslist[k])==0 ){
                    result = packageInfoList.get(i).pName;
                    check = true;
                }
                if(!check){
                    String []pNameList = pName.split("\\.");
                    for (int m = 0; m < pNameList.length; m++){
                        if( deslist[k].compareToIgnoreCase(pNameList[m])==0 || pNameList[m].compareToIgnoreCase(deslist[k])==0){
                            result = packageInfoList.get(i).pName;
                            break;
                        }

                    }

                }


            }
        }

        return result;
    }

    class PInfo {
        private String appName = "";
        private String pName = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

    }

    public void getPackageInforMap(Context context) {
        packageInfoList = getInstalledApps(false, context); /* false = no system packages */

/*        for (int i = 0; i < packageInfoList.size(); i++){
            String text = packageInfoList.get(i).appName + "\t\t" + packageInfoList.get(i).pName;
            appendLog(text);
        }*/
    }
    public void appendLog(String text)
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }


        File logFile = new File(file.getAbsolutePath() + "/" +"mediaplayer.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            String filename = file.getAbsolutePath() + "/" +"package.txt";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(text + "\n");
            fw.close();
        } catch (IOException ioe) {
        }

    }
    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages, Context context) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            newInfo.pName = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(newInfo);
        }
        return res;
    }
    private String getSynthesisFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "syn" + AUDIO_RECORDER_FILE_EXT_WAV);
    }
    private void doSynthesis(String text){
        if(mPlayingSynthesiFile == true)
            return;
        mPlayingSynthesiFile = true;
        String strServer = "http://10.30.153.42:59125";
        String localeType = "vi";
        String textInput = String.format("\"%s\"", text);
        String savefilePath = getSynthesisFilename();
        new DownloadFileServiceTask().execute(strServer, localeType, textInput, savefilePath);
    }
    private class DownloadFileServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            DownloadFileFromURL upload = new DownloadFileFromURL();
            return upload.downloadFile(urls[0], urls[1], urls[2], urls[3]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(result.length() == 0){

                audioPlayer(getSynthesisFilename());

            }else{
            }


        }
    }

    public void audioPlayer(String filename){
        //set up MediaPlayer

     /*   MediaPlayer mp = new MediaPlayer();
        //	mp = new MediaPlayer();*/

        mp.reset();
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2) {
                mPlayingSynthesiFile = false;
                return true;
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayingSynthesiFile = false;
            }
        });


        try {
            mp.setDataSource(filename);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            mPlayingSynthesiFile = false;
            appendLog(e.getMessage());
            e.printStackTrace();
        }

    }

    public void processCommand(String command, Context context, String contactname){
        String arr[] = command.split(" ");
        if(arr.length < 1)
            return;
        if(arr[0].equalsIgnoreCase("VVA_CMD")){
            String packageItemInfo = getPackNameInfo(arr[1]);
            packageItemInfo = packageItemInfo.toLowerCase();
            if(packageItemInfo == null || packageItemInfo.length() == 0)
                return;
            launchApp(packageItemInfo, context);
        }else{
            boolean bcheck = false;
            if(mPreferences.getReadMessage().compareToIgnoreCase("1") == 0)
                bcheck = true;
            else{
                if(arr[0].equalsIgnoreCase("v")){
                    bcheck = true;
                }
            }
            if(bcheck){
                String packageItemInfo = command;
              /*  for (int i = 1; i < arr.length; i++)
                    packageItemInfo += arr[i] + " ";*/

                String strSyn = "Bạn có tin nhắn, nội dung như sau. ";
                if(contactname.length() > 0){
                    strSyn = "Bạn có tin nhắn từ " + contactname + ". Nội dung như sau. ";
                }
                packageItemInfo = packageItemInfo.trim().toLowerCase();
                strSyn += packageItemInfo;

                packageItemInfo = strSyn;

                NormalizerSynthesis rmv = new NormalizerSynthesis();
                String textInput = "";
                textInput = rmv.nomarlize(packageItemInfo, VavActivity.mResourcePath, VavActivity.mMappingSynthesis);
         /*   String [] liststring = packageItemInfo.split(" ");
            String textInput = "";
            for (int i = 0 ; i < liststring.length; i++){
                textInput += rmv.convertToken(liststring[i]) + " ";
            }*/
                textInput = textInput.trim();

                packageItemInfo = textInput;
                if(packageItemInfo.length() > 0){
                    //       sendMessage(5);
                    doSynthesis(packageItemInfo);
                }
            }

        }

    }
    public void toggleWiFi(boolean status, Context context){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (status && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }
    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }
    public void mobiledataenable(boolean enabled, Context context) {

        try {
            final ConnectivityManager conman = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void processVoiceCommand(String command, Context context){

        if(command.length() == 0)
            return;
        if(command.contains("bluetooth")){
            setBluetooth(true);
        }else if(command.contains("wifi")){
            toggleWiFi(true, context);
        }else if(command.contains("3g")){
            mobiledataenable(true, context);
        }else{
            String packageItemInfo = getPackNameInfo(command);
            packageItemInfo = packageItemInfo.toLowerCase();
            if(packageItemInfo == null || packageItemInfo.length() == 0)
                return;

            launchApp(packageItemInfo, context);
        }

    }
    public void sendMessage(int iwhat) {
        Message message = Message.obtain();
        message.what = iwhat;
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            mPlayingSynthesiFile = false;
        }
    }
}
