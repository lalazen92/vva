package vrd.cyberspace.viettel.vrd;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import vrd.cyberspace.viettel.vrd.flacEncoder.Wav2Flac;
import vrd.cyberspace.viettel.vrd.floatingbutton.FloatingFaceBubbleService;
import vrd.cyberspace.viettel.vrd.googlerecognition.GoogleResponse;
import vrd.cyberspace.viettel.vrd.googlerecognition.Recognizer;
import vrd.cyberspace.viettel.vrd.lanchapp.LoadInstalledApp;
import vrd.cyberspace.viettel.vrd.log.AppLog;
import vrd.cyberspace.viettel.vrd.preference.PreferenceManager;
import vrd.cyberspace.viettel.vrd.settings.SettingsActivity;
import vrd.cyberspace.viettel.vrd.uploadrecord.UploadRecordFile;
import vrd.cyberspace.viettel.vrd.wav.ExtAudioRecorderModified;


public class VavActivity extends ActionBarActivity {
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;

    private PreferenceManager mPreferences;

    private AudioRecord recorder = null;
    private int bufferSize = 0;

    public static Context  mContext;

    private TextView txtSpeechInput;

    private ImageButton btnSpeak;

    private TextView txtUsername;
    private TextView txtSpeak;
    private ImageButton btnSStart;
    private EditText txtUsernameEdit;

    private int mPosRecord = 0;
    boolean mStartRecording = false;

    String []recordtextlist = {};
    ExtAudioRecorderModified audioRecorder = null;

    // Variables used to establish return code
    private static final long MIN = 10000000;
    private static final long MAX = 900000009999999L;
    private static MediaPlayer mp = new MediaPlayer();

    long PAIR;

    Handler handler = new Handler();

    String language = "en_us";

    // Key obtained through Google Developer group
    String api_key = "AIzaSyBgnC5fljMTmCFeilkgLsOKBvvnx6CBS0M";

    // Name of the sound file (.flac)

    // URL for Google API
    String root = "https://www.google.com/speech-api/full-duplex/v1/";
    String dwn = "down?maxresults=1&pair=";
    String API_DOWN_URL = root + dwn;
    String up_p1 = "up?lang=" + language
            + "&lm=dictation&client=chromium&pair=";
    String up_p2 = "&key=";
    public void showSpeaking(boolean b){
        if(b){
            txtSpeechInput.setVisibility(View.VISIBLE);
            btnSpeak.setVisibility(View.VISIBLE);
            txtSpeak.setVisibility(View.VISIBLE);
        }else{
            txtSpeechInput.setVisibility(View.GONE);
            btnSpeak.setVisibility(View.GONE);
            txtSpeak.setVisibility(View.GONE);
        }

    }
    public void showdousername(boolean b){
        if(b){
            txtUsername.setVisibility(View.VISIBLE);

            btnSStart.setVisibility(View.VISIBLE);
            txtUsernameEdit.setVisibility(View.VISIBLE);
        }else{
            txtUsername.setVisibility(View.GONE);

            btnSStart.setVisibility(View.GONE);
            txtUsernameEdit.setVisibility(View.GONE);
        }
    }
    private void onRecord(boolean start) {
        if (!start) {

            startRecording(getFilename());
        } else {
            btnSpeak.setImageResource(R.mipmap.ico_mic);
            stopRecording();
            File f = new File(getFilename());
            if(f.exists() && !f.isDirectory()) {
                audioPlayer(getFilename());
/*                if(audioPlayer(getFilename()) == true){
                    String filename = Integer.toString(mPosRecord);
                    int size = 0;
                    if (filename.length() < 5)
                        size = 5 - filename.length();
                    String temp = "";
                    for (int i = 0; i < size; i++)
                        temp += "0";
                    temp += filename;
                    filename = temp;
                    int pos = mPosRecord - 1;
                    if(pos < 0)
                        pos = 0;
                    new UploadREcordServiceTask().execute(mPreferences.getServerName(), mPreferences.getName(), filename, getFilename() ,Integer.toString(pos));
                }else{
                    if(mPosRecord > 0)
                        mPosRecord -= 1;
                    txtSpeechInput.setText("Lỗi xẩy ra, có thể chưa ghi âm được. Chọn để ghi âm lại");
                    return;
                }*/
            }else{
                if(mPosRecord > 0)
                    mPosRecord -= 1;
                txtSpeechInput.setText("Lỗi xẩy ra, có thể chưa ghi âm được. Chọn để ghi âm lại");
                return;
            }
        }
    }
    public boolean audioPlayer(String strfilename){
        //set up MediaPlayer

        mp.reset();
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2) {
                if(mPosRecord > 0)
                    mPosRecord -= 1;
                txtSpeechInput.setText("Lỗi xẩy ra, có thể chưa ghi âm được. Chọn để ghi âm lại");
                return true;
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                String filename = Integer.toString(mPosRecord);
                int size = 0;
                if (filename.length() < 5)
                    size = 5 - filename.length();
                String temp = "";
                for (int i = 0; i < size; i++)
                    temp += "0";
                temp += filename;
                filename = temp;
                int pos = mPosRecord - 1;
                mPreferences.setPos(mPosRecord);
                if(pos < 0)
                    pos = 0;
                new UploadREcordServiceTask().execute(mPreferences.getServerName(), mPreferences.getName(), filename, getFilename() ,Integer.toString(pos));
            }
        });
        try {
            mp.setDataSource(strfilename);
            mp.prepare();
            mp.start();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "vrd" + AUDIO_RECORDER_FILE_EXT_WAV);
    }
    private String getTestFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "vrdtest"+ AUDIO_RECORDER_FILE_EXT_WAV);
    }
    private String getFilenameFlac(){
        String flacFile = getFilename() + ".flac";
        return flacFile;
    }
    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording(String filename){
        if(recordtextlist.length == 0)
            return;
        if(mPosRecord >= 0 && mPosRecord < recordtextlist.length){
            txtSpeechInput.setText(recordtextlist[mPosRecord]);
            mPosRecord += 1;
            mPreferences.setPos(mPosRecord);
        }
        else if (mPosRecord >= recordtextlist.length){
            txtSpeechInput.setText("Đã thu âm xong, cần dữ liệu ghi âm mới từ server");
            showdousername(true);
            showSpeaking(false);
            mPosRecord = 0;
            mPreferences.setPos(mPosRecord);
            return;
        }
        else if(recordtextlist.length == 0){
            txtSpeechInput.setText("Không có dữ liệu ghi âm, cần dữ liệu ghi âm mới từ server");
            return;
        }
        btnSpeak.setImageResource(R.mipmap.ico_mic_stop);
        audioRecorder = ExtAudioRecorderModified.getInstance();
        audioRecorder.setOutputFile(filename);
        audioRecorder.prepare();
        audioRecorder.start();
        mStartRecording = true;
    }
    private void stopRecording(){

        //audioRecorder.stop();
        audioRecorder.release();
        mStartRecording = false;

    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }
    private void deletewavFile() {
        File file = new File(getFilename());

        file.delete();
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
    public void convertWavToFlac(String wavFilename, String flacFilename) {
        // TODO Auto-generated method stub

        Wav2Flac flacEncoder = new Wav2Flac();   // <---- Error
       flacEncoder.convertWavToFlac(wavFilename, flacFilename);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class VoiceRecognitionServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                Recognizer mRecognizer = new Recognizer("vi", "AIzaSyC0uaoJq9Kdp1aAxJv_ZucOp8SUirZO7BA");
                GoogleResponse respone = mRecognizer.getRecognizedDataForFlac(urls[0], 10);
                return respone.getResponse();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            txtSpeechInput.setText(result);
            LoadInstalledApp loadApp = new LoadInstalledApp();
            loadApp.processVoiceCommand(result, mContext);
        }
    }

    private class RecordTextServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            UploadRecordFile upload = new UploadRecordFile();

            return upload.getrecordtextlist(urls[0], urls[1]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(result.length() == 0){
                txtSpeechInput.setText("Lỗi kết nối");
                return;
            }
            JSONParser parser = new JSONParser();

            try {
                Object obj = parser.parse(result);

                JSONObject jsonObject =  (JSONObject) obj;

                String id = (String) jsonObject.get("id");
                if(id.compareToIgnoreCase(mPreferences.getid()) != 0)
                {
                    mPosRecord = 0;
                    mPreferences.setPos(mPosRecord);
                    mPreferences.setid(id);
                }
                recordtextlist = ((String) jsonObject.get("res")).split("\n");
                if(mPosRecord >= 0 && mPosRecord < recordtextlist.length){
                    txtSpeechInput.setText(recordtextlist[mPosRecord]);
                }

            } catch (ParseException e) {
                txtSpeechInput.setText(result);
                e.printStackTrace();
            }
        }
    }
    private class UploadREcordServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            UploadRecordFile upload = new UploadRecordFile();

            return upload.doFileUpload(urls[0], urls[1], urls[2], urls[3], urls[4]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            deletewavFile();
            if(result.length() == 0){

                if(mPosRecord >= 0 && mPosRecord < recordtextlist.length && recordtextlist.length > 0){
                    txtSpeechInput.setText(recordtextlist[mPosRecord]);
                }
                //txtSpeechInput.setText("Success. Ghi âm câu tiếp theo!");
            }else{
                if(mPosRecord > 0)
                    mPosRecord -= 1;
                txtSpeechInput.setText("Lỗi xẩy ra, có thể mạng lỗi. Chọn để ghi âm lại");
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vav);
        mContext = getApplicationContext();
        mPreferences = PreferenceManager.getInstance();

        LoadInstalledApp loadApp = new LoadInstalledApp();
        loadApp.getPackageInforMap(mContext);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        txtUsername = (TextView)findViewById(R.id.txtusernamelabel);
        txtSpeak = (TextView)findViewById(R.id.txtSpeak);
        btnSStart = (ImageButton) findViewById(R.id.btnStart);
        txtUsernameEdit = (EditText) findViewById(R.id.txtusername);


        bufferSize = AudioRecord.getMinBufferSize(16000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

/*        mPosRecord = mPreferences.getPos()-1;
        if(mPosRecord < 0)*/
            mPosRecord = 0;

        if (mPreferences.getid().length() == 0){
            mPosRecord = 0;
            mPreferences.setPos(mPosRecord);
        }
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onRecord(mStartRecording);
            }
        });
        btnSStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(txtUsernameEdit.getText().toString().trim().length() == 0){
                    Toast.makeText(getContext(), "Tên không đúng", Toast.LENGTH_SHORT);
                    return;
                }
                View view = txtUsernameEdit;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                showdousername(false);
                showSpeaking(true);
                mPreferences.setName(txtUsernameEdit.getText().toString());
                new RecordTextServiceTask().execute(mPreferences.getServerName(), mPreferences.getName());
                txtSpeechInput.setText("");
            }
        });
        showdousername(true);
        showSpeaking(false);

        Intent intent = new Intent(VavActivity.this, FloatingFaceBubbleService.class);
        final Messenger msg = new Messenger(messageHandler2);
        intent.putExtra("MESSENGER", msg);
        startService(intent);
    }
    public static Context getContext(){
        return mContext;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String number = Integer.toString(mPosRecord) + "/" + Integer.toString(recordtextlist.length) + "  (Đã đọc/Tổng)";
            mPreferences.setNumberUse(number);
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_back) {
            if(mPosRecord < 1)
                return true;
            mPosRecord -= 1;
            if(mPosRecord >= 0 && mPosRecord < recordtextlist.length){
                txtSpeechInput.setText(recordtextlist[mPosRecord]);
                mPreferences.setPos(mPosRecord);
            }
            return true;
        }
        if (id == R.id.action_lastread) {
            mPosRecord = mPreferences.getPos();
/*            if(mPosRecord < 1)
                return true;*/
            mPosRecord -= 1;
            if(mPosRecord >= 0 && mPosRecord < recordtextlist.length){
                txtSpeechInput.setText(recordtextlist[mPosRecord]);
                mPreferences.setPos(mPosRecord);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    // DOWN handler
    Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: // GET DOWNSTREAM json id="@+id/comment"
                    String mtxt = msg.getData().getString("text");
                    if (mtxt.length() > 20) {
                        final String f_msg = mtxt;
                        handler.post(new Runnable() { // This thread runs in the UI
                            // TREATMENT FOR GOOGLE RESPONSE
                            @Override
                            public void run() {
                                System.out.println(f_msg);
                                txtSpeechInput.setText(f_msg);
                            }
                        });
                    }
                    break;
                case 2:
                    break;
            }
        }
    }; // doDOWNSTRM Handler end

    // UPSTREAM channel. its servicing a thread and should have its own handler
    Handler messageHandler2 = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: // GET DOWNSTREAM json
                    Log.d("ParseStarter", msg.getData().getString("post"));
                    break;
                case 2:
                    Log.d("ParseStarter", msg.getData().getString("post"));
                    break;
                case 100:
                    bringApplicationToFront();
                    break;
            }

        }
    }; // UPstream handler end
    private void bringApplicationToFront()
    {

        Intent notificationIntent = new Intent(this, VavActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        try
        {
            pendingIntent.send();
        }
        catch (PendingIntent.CanceledException e)
        {
            e.printStackTrace();
        }
    }
    public void getTranscription() {

        File myfil = new File(getFilenameFlac());
        if (!myfil.canRead())
            Log.d("ParseStarter", "FATAL no read access");

        // first is a GET for the speech-api DOWNSTREAM
        // then a future exec for the UPSTREAM / chunked encoding used so as not
        // to limit
        // the POST body sz

        PAIR = MIN + (long) (Math.random() * ((MAX - MIN) + 1L));
        // DOWN URL just like in curl full-duplex example plus the handler
        downChannel(API_DOWN_URL + PAIR, messageHandler);

        // UP chan, process the audio byteStream for interface to UrlConnection
        // using 'chunked-encoding'
        FileInputStream fis;
        try {
            fis = new FileInputStream(myfil);
            FileChannel fc = fis.getChannel(); // Get the file's size and then
            // map it into memory
            int sz = (int) fc.size();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
            byte[] data2 = new byte[bb.remaining()];
            Log.d("ParseStarter", "mapfil " + sz + " " + bb.remaining());
            bb.get(data2);
            // conform to the interface from the curl examples on full-duplex
            // calls
            // see curl examples full-duplex for more on 'PAIR'. Just a globally
            // uniq value typ=long->String.
            // API KEY value is part of value in UP_URL_p2
            upChannel(root + up_p1 + PAIR + up_p2 + api_key, messageHandler2,
                    data2);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void downChannel(String urlStr, final Handler messageHandler) {

        final String url = urlStr;

        new Thread() {
            Bundle b;

            public void run() {
                String response = "NAO FOI";
                Message msg = Message.obtain();
                msg.what = 1;
                // handler for DOWN channel http response stream - httpsUrlConn
                // response handler should manage the connection.... ??
                // assign a TIMEOUT Value that exceeds by a safe factor
                // the amount of time that it will take to write the bytes
                // to the UPChannel in a fashion that mimics a liveStream
                // of the audio at the applicable Bitrate. BR=sampleRate * bits
                // per sample
                // Note that the TLS session uses
                // "* SSLv3, TLS alert, Client hello (1): "
                // to wake up the listener when there are additional bytes.
                // The mechanics of the TLS session should be transparent. Just
                // use
                // httpsUrlConn and allow it enough time to do its work.
                Scanner inStream = openHttpsConnection(url);
                // process the stream and store it in StringBuilder
                while (inStream.hasNextLine()) {
                    b = new Bundle();
                    b.putString("text", inStream.nextLine());
                    msg.setData(b);
                    messageHandler.dispatchMessage(msg);
                }

            }
        }.start();
    }

    private void upChannel(String urlStr, final Handler messageHandler,
                           byte[] arg3) {

        final String murl = urlStr;
        final byte[] mdata = arg3;
        Log.d("ParseStarter", "upChan " + mdata.length);
        new Thread() {
            public void run() {
                String response = "NAO FOI";
                Message msg = Message.obtain();
                msg.what = 2;
                Scanner inStream = openHttpsPostConnection(murl, mdata);
                inStream.hasNext();
                // process the stream and store it in StringBuilder
                while (inStream.hasNextLine()) {
                    response += (inStream.nextLine());
                    Log.d("ParseStarter", "POST resp " + response.length());
                }
                Bundle b = new Bundle();
                b.putString("post", response);
                msg.setData(b);
                // in.close(); // mind the resources
                messageHandler.sendMessage(msg);

            }
        }.start();

    }

    // GET for DOWNSTREAM
    private Scanner openHttpsConnection(String urlStr) {
        InputStream in = null;
        int resCode = -1;
        Log.d("ParseStarter", "dwnURL " + urlStr);

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpsURLConnection)) {
                throw new IOException("URL is not an Https URL");
            }

            HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            // TIMEOUT is required
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");

            httpConn.connect();

            resCode = httpConn.getResponseCode();
            if (resCode == HttpsURLConnection.HTTP_OK) {
                return new Scanner(httpConn.getInputStream());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // GET for UPSTREAM
    private Scanner openHttpsPostConnection(String urlStr, byte[] data) {
        InputStream in = null;
        byte[] mextrad = data;
        int resCode = -1;
        OutputStream out = null;
        // int http_status;
        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpsURLConnection)) {
                throw new IOException("URL is not an Https URL");
            }

            HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setChunkedStreamingMode(0);
            httpConn.setRequestProperty("Content-Type", "audio/x-flac; rate="
                    + RECORDER_SAMPLERATE);
            httpConn.connect();

            try {
                // this opens a connection, then sends POST & headers.
                out = httpConn.getOutputStream();
                // Note : if the audio is more than 15 seconds
                // dont write it to UrlConnInputStream all in one block as this
                // sample does.
                // Rather, segment the byteArray and on intermittently, sleeping
                // thread
                // supply bytes to the urlConn Stream at a rate that approaches
                // the bitrate ( =30K per sec. in this instance ).
                Log.d("ParseStarter", "IO beg on data");
                out.write(mextrad); // one big block supplied instantly to the
                // underlying chunker wont work for duration
                // > 15 s.
                Log.d("ParseStarter", "IO fin on data");
                // do you need the trailer?
                // NOW you can look at the status.
                resCode = httpConn.getResponseCode();

                Log.d("ParseStarter", "POST OK resp "
                        + httpConn.getResponseMessage().getBytes().toString());

                if (resCode / 100 != 2) {
                    Log.d("ParseStarter", "POST bad io ");
                }

            } catch (IOException e) {
                Log.d("ParseStarter", "FATAL " + e);

            }

            if (resCode == HttpsURLConnection.HTTP_OK) {
                Log.d("ParseStarter", "OK RESP to POST return scanner ");
                return new Scanner(httpConn.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
        /*    case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    Toast.makeText(this, "Volume", Toast.LENGTH_LONG);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    onRecord(mStartRecording);
                }
                return true;*/
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
