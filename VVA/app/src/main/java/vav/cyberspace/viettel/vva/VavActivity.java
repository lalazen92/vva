package vav.cyberspace.viettel.vva;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.firezenk.audiowaves.Visualizer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vav.cyberspace.viettel.vva.circularprogressview.CircularProgressView;
import vav.cyberspace.viettel.vva.circularprogressview.CircularProgressViewAdapter;
import vav.cyberspace.viettel.vva.contacts.ContactItem;
import vav.cyberspace.viettel.vva.contacts.ContactList;
import vav.cyberspace.viettel.vva.downloadfile.DownloadFileFromURL;
import vav.cyberspace.viettel.vva.flacEncoder.Wav2Flac;
import vav.cyberspace.viettel.vva.floatingbutton.FloatingFaceBubbleService;
import vav.cyberspace.viettel.vva.googlerecognition.GoogleRecognizer;
import vav.cyberspace.viettel.vva.googlerecognition.GoogleResponse;
import vav.cyberspace.viettel.vva.lanchapp.LoadInstalledApp;
import vav.cyberspace.viettel.vva.listview.ContactAdapter;
import vav.cyberspace.viettel.vva.preference.PreferenceManager;
import vav.cyberspace.viettel.vva.settings.SettingsActivity;
import vav.cyberspace.viettel.vva.synthesis.SynthesisActivity;
import vav.cyberspace.viettel.vva.uploadrecord.SendFileTCP;
import vav.cyberspace.viettel.vva.utils.utils;
import vav.cyberspace.viettel.vva.wav.ExtAudioRecorderModified;


import jp.naist.ahclab.speechkit.Recognizer;
import jp.naist.ahclab.speechkit.ServerInfo;
import jp.naist.ahclab.speechkit.SpeechKit;
import vav.cyberspace.viettel.vva.wavformview.WaveformView;


public class VavActivity extends AppCompatActivity implements Recognizer.Listener {
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    public static String mResourcePath;
    static public Context mContext;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private TextView mTxtTouch;
    boolean mStartRecording = false;
    boolean mStop = false;
    ExtAudioRecorderModified audioRecorder = null;
    private PreferenceManager mPreferences;
    private CircularProgressView progressView;
    private WaveformView mWaveformView;

    long startTime = System.currentTimeMillis();
    long stopTime = System.currentTimeMillis();
    // Kaldi Recognition
    protected ServerInfo serverInfo = new ServerInfo();
    Recognizer _currentRecognizer;

    Thread updateThread;

    String[] test_recog_text = {"trung tâm Không gian mạng viettel xin chào các bạn", "chúng tôi muốn mang công nghệ xử lý tiếng nói đến mọi người dân việt nam", "nhằm thay đổi cách chúng ta làm việc và tận hưởng cuộc sống", "nếu bạn là các kỹ sư yêu thích công nghệ và có kinh nghiệm trong lĩnh vực này", "đừng ngần ngại hãy liên hệ với chúng tôi qua số điện thoại 0919114252"};
    int count_step_test = 0;

    //    private ImageButton imagebutton;
//    private GifAnimationDrawable little, big;
    private static final int RECORDER_BPP = 16;


    public static ArrayList<String> mMappingSynthesis = new ArrayList<>();
    public ArrayList<String> mMappingLanchApp = new ArrayList<>();

    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    public static final int MY_NORMAL_PERMISSIONS_REQUEST = 1411;
    private static final int MY_OVERLAY_PERMISSIONS_REQUEST = 1412;

    void init_speechkit(ServerInfo serverInfo) {
        SpeechKit _speechKit = SpeechKit.initialize(getApplication().getApplicationContext(), "", "", serverInfo);
        _currentRecognizer = _speechKit.createRecognizer(VavActivity.this);
        _currentRecognizer.connect();
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onPartialResult(String result) {
        txtSpeechInput.setText(result);
    }

    @Override
    public void onFinalResult(String result) {
        txtSpeechInput.setText(result);
        //   mTxtTouch.setText("Chạm để nói");
    }

    @Override
    public void onFinish(String reason) {
        //    mTxtTouch.setText("Chạm để nói");
/*        if (lst_dialog.isShowing())
            lst_dialog.dismiss();*/
    }

    @Override
    public void onPause() {
        super.onPause();
        LoadInstalledApp.mPlayingSynthesiFile = false;
        SynthesisActivity.mPlayingSynthesiFile = false;
    }

    @Override
    public void onReady(String reason) {
        btnSpeak.setEnabled(true);
    }

    @Override
    public void onNotReady(String reason) {
        btnSpeak.setEnabled(false);

        Toast.makeText(getApplicationContext(), "Server connected, but not ready, reason: " + reason,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateStatus(SpeechKit.Status status) {

    }

    @Override
    public void onRecordingBegin() {
        //     mTxtTouch.setText("Đang ghi âm...");
    }

    @Override
    public void onRecordingDone() {
        //   mTxtTouch.setText("Đợi chút...");
    }

    @Override
    public void onError(Exception error) {
        //    mTxtTouch.setText("Chạm để nói");
      /*  Toast.makeText(getApplicationContext(), error.getMessage().toString(),
                Toast.LENGTH_SHORT).show();*/
    }

    private void onRecord(boolean start) {
        if (mStop == true)
            return;

        if (!start) {
         /*   new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(mStartRecording == false && mStop)
                                stopRecording();
                        }
                    });

                }
            }, 5000);*/
            startRecording();

        } else {
            stopRecording();
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + "vva" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getSynthesisFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "syn" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getFilenameFlac() {
        String flacFile = getFilename() + ".flac";
        return flacFile;
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private String getAcronymConfig() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + "acronym.txt");
    }

    private String getUnitofMeasureConfig() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + "unit-of-measure.txt");
    }

    private String getResourcePathConfig() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/");
    }

    private void startRecording() {
        mTxtTouch.setText("Đang ghi âm...");
        mWaveformView.setVisibility(View.VISIBLE);
        btnSpeak.setVisibility(View.GONE);
        txtSpeechInput.setText("");
        if (mPreferences.getRecogType().compareToIgnoreCase("nogo") == 0) {
            ExtAudioRecorderModified.CAPTURE_SAMPLE_RATE_HZ = 16000;

            //    mStop = true;
            //    resetAquisition();
        } else if (mPreferences.getRecogType().compareToIgnoreCase("go") == 0) {
            ExtAudioRecorderModified.CAPTURE_SAMPLE_RATE_HZ = 8000;
         /*   audioRecorder = ExtAudioRecorderModified.getInstance();
            audioRecorder.setOutputFile(getFilename());
            audioRecorder.prepare();
            audioRecorder.start();*/
        }

        audioRecorder = ExtAudioRecorderModified.getInstance();
        audioRecorder.setOutputFile(getFilename());
        audioRecorder.prepare();
        audioRecorder.start();
        mStartRecording = true;

    }

    public void decodeVoice() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtTouch.setText("Đang xử lý...");
                progressView.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.GONE);
                mWaveformView.setVisibility(View.GONE);
                //  btnSpeak.setImageResource(R.mipmap.loading);
            }
        });
        String strServer = "10.30.153.42";
        String strPort = "8989";
        new RecognizerServiceTask().execute(strServer, strPort, getFilename());
    }

    private void stopRecording() {

        audioRecorder.release();
        mStartRecording = false;
        mStop = true;

        startTime = System.currentTimeMillis();
        if (mPreferences.getRecogType().compareToIgnoreCase("nogo") == 0) {
            decodeVoice();
        } else if (mPreferences.getRecogType().compareToIgnoreCase("go") == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtTouch.setText("Đang xử lý...");
                    progressView.setVisibility(View.VISIBLE);
                    btnSpeak.setVisibility(View.GONE);
                    mWaveformView.setVisibility(View.GONE);
                    //  btnSpeak.setImageResource(R.mipmap.loading);
                }
            });
            // convertWavToFlac(getFilename(), getFilenameFlac());
            //  new VoiceRecognitionServiceTask().execute(getFilenameFlac());

            new WebService(mContext, getFilename()).execute();
        }

    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void deletewavFile() {
        File file = new File(getFilename());

        file.delete();
    }


    public void convertWavToFlac(String wavFilename, String flacFilename) {
        // TODO Auto-generated method stub

        Wav2Flac flacEncoder = new Wav2Flac();   // <---- Error
        flacEncoder.convertWavToFlac(wavFilename, flacFilename);
    }


    private class RecognizerServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            SendFileTCP doWork = new SendFileTCP();
            return doWork.sendfile(urls[0], urls[1], urls[2]);

         /*   UploadRecordFile upload = new UploadRecordFile();

            return upload.doFileUpload(urls[0], urls[1], urls[2], urls[3]);*/
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //   deletewavFile();
            processDecodedData(result);
        }
    }

    public void processDecodedData(String result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtTouch.setText("Chạm để nói");
                progressView.setVisibility(View.GONE);
                btnSpeak.setVisibility(View.VISIBLE);
                mWaveformView.setVisibility(View.GONE);
                btnSpeak.setImageResource(R.mipmap.ico_mic);
            }
        });

        mStop = false;
        mStartRecording = false;


        if (result.length() == 0) {

            return;
        }


        stopTime = System.currentTimeMillis();
        float elapsedTime = (float) (stopTime - startTime) / 1000;
        int pos = result.indexOf("$");
        if (pos != -1) {
            String strDecodeTime = result.substring(pos + 1);
            result = result.substring(0, pos);
            pos = strDecodeTime.indexOf("$");
            if (pos != -1) {
                strDecodeTime = strDecodeTime.substring(0, pos);
            }

            result = result.trim();

            String strLog = "Decode Time: " + strDecodeTime + "(s) Time Total: " + Float.toString(elapsedTime) + "(s)" + " Text: " + result;
            appendLog(strLog);


        }
        String txtDisplay = result;

        String noAccentResult = utils.removeAccent(result).toLowerCase();
        String[] liststring = noAccentResult.split(" ");
        boolean b = false;
        if (liststring.length > 0) {
            if (liststring[0].compareToIgnoreCase("goi") == 0) {
                txtDisplay = result + " ";
                txtDisplay = utils.mappingstringtonumber(txtDisplay);
                txtDisplay = txtDisplay.trim();
                String number = "";
                for (int i = 1; i < liststring.length; i++)
                    number += liststring[i] + " ";

                if (number.length() > 0) {
                    ContactList obj = new ContactList();
                    number = utils.mappingstringtonumber(number);
                    String title = "Bạn gọi cho ai?";
                    ArrayList<ContactItem> contact_list = obj.getSimilarContact(number);
                    if (contact_list.size() == 1) {
                        //  callNumber(contact_list.get(0).getmPhoneNumber());
                        title = "Bạn muốn gọi cho?";
                    } else if (contact_list.size() > 1) {
                    } else {
                        title = "Bạn muốn gọi tới số?";
                        ContactItem item = new ContactItem();
                        item.setmPhoneNumber(number);
                        item.setmContactName(number);
                        contact_list.add(item);
                        //   callNumber(number);
                    }
                    startContactListActivity(contact_list, title);

                    b = true;
                }
            }
        }
        if (txtDisplay.indexOf("Error: ") != -1) {
            txtDisplay = "Mạng chập chờn. Mời thử lại!";
        }
        final String txtFinal = txtDisplay;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtSpeechInput.setText(txtFinal);
            }
        });

        if (!b) {

            String[] listcommand = result.trim().split(" ");
            if (listcommand.length > 1) {
                if (listcommand[0].compareToIgnoreCase("mở") == 0) {
                    LoadInstalledApp loadApp = new LoadInstalledApp();
                    result = utils.mappingstringtoapp(result, mMappingLanchApp);
                    loadApp.processVoiceCommand(result, mContext);
                }
            }

        }

    }

    private void callNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }


    public class WebService extends AsyncTask<String, Void, String> {

        private Context mContext;
        private String path = "";


        public WebService(Context context, String url) {
            this.mContext = context;
            this.path = url;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final File uploadFile = new File(path);
                String duy = Long.toString(uploadFile.length());
                String boundary = Long.toHexString(System.currentTimeMillis());
                String name = uploadFile.getName();
                int file_size = Integer.parseInt(String.valueOf(uploadFile.length()));
                String POST_URL = "http://10.30.154.11:8234/voices/api/v1/decode";

                String charset = "UTF-8";
                URLConnection connection = new URL(POST_URL).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Content-Length", duy);
                connection.setRequestProperty("Content-Type", "audio/x-wav");
                connection.setRequestProperty("SampleRate", "16000");
                connection.setRequestProperty("type", "wav");
                connection.setRequestProperty("QueueType", "decode_jobs_opendomain");
                connection.setRequestProperty("name", "test");
//

                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                byte[] array = getBytesFromFile(uploadFile);

                output.write(array);
                // Files.copy(uploadFile.toPath(), output);
                output.flush();
                // System.out.println(connection.getHeaderFields().toString());
                //get response body as input stream
                BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                StringBuffer sb = new StringBuffer("");
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String result = sb.toString();
                System.out.println(result);
                return result;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //  super.onPostExecute(s);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtTouch.setText("Chạm để nói");
                    progressView.setVisibility(View.GONE);
                    btnSpeak.setVisibility(View.VISIBLE);
                    mWaveformView.setVisibility(View.GONE);
                    btnSpeak.setImageResource(R.mipmap.ico_mic);
                }
            });


            if (result != null) {
                if (result.length() > 0) {

                    //result = result.replace("", "");
                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        txtSpeechInput.setText(jsonObj.getString("text"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // LoadInstalledApp loadApp = new LoadInstalledApp();
                    //   loadApp.processVoiceCommand(result, mContext);
                }
            }
            mStop = false;
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    private class VoiceRecognitionServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String text = "";
            final int mid = 1;
            final String POST_URL = "http://10.30.154.11:8234/voices/api/v1/decode";
            final File uploadFile = new File("/home/maituan/5.wav"); //path to file

            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";
            String charset = "UTF-8";
            URLConnection connection = null;
            try {
                connection = new URL(POST_URL).openConnection();

                connection.setDoOutput(true);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Content-Length", Long.toString(uploadFile.length()));
                connection.setRequestProperty("Content-Type", "audio/x-wav");
                connection.setRequestProperty("SampleRate", "16000");
                connection.setRequestProperty("type", "wav");
                connection.setRequestProperty("QueueType", "decode_jobs_opendomain");
                connection.setRequestProperty("name", "test");
            } catch (IOException e) {
                e.printStackTrace();
            }
//
            try {
                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.copy(uploadFile.toPath(), output);
                    }
                    output.flush();
                    // System.out.println(connection.getHeaderFields().toString());
                    //get response body as input stream
                    BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String inputLine = "";
                    StringBuffer sb = new StringBuffer("");
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    String result = sb.toString();


                    return result;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtTouch.setText("Chạm để nói");
                    progressView.setVisibility(View.GONE);
                    btnSpeak.setVisibility(View.VISIBLE);
                    mWaveformView.setVisibility(View.GONE);
                    btnSpeak.setImageResource(R.mipmap.ico_mic);
                }
            });


            if (result != null) {
                if (result.length() > 0) {

                    txtSpeechInput.setText(result);

                    LoadInstalledApp loadApp = new LoadInstalledApp();
                    //   loadApp.processVoiceCommand(result, mContext);
                }
            }
            mStop = false;
        }
    }

    public void onStartService() {
/*        Intent i = new Intent(VavActivity.this, AndroidServiceStartOnBoot.class);

        startService(i);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //   getActionBar().hide();
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> missingPermissions = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.CALL_PHONE);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.RECORD_AUDIO);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.WRITE_CALL_LOG);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.READ_CONTACTS);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.READ_PHONE_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.READ_SMS);
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.SEND_SMS);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!missingPermissions.isEmpty()) {
                String[] requestPermissions = new String[missingPermissions.size()];
                missingPermissions.toArray(requestPermissions);
                requestPermissions(requestPermissions, MY_NORMAL_PERMISSIONS_REQUEST);
                return;
            }
        }*/


        setContentView(R.layout.activity_vav);

        //     getSupportActionBar().hide();
 /*       mAudioVisulizer = ((Visualizer) findViewById(R.id.visualizer));
        mAudioVisulizer.startListening();
*/
        mResourcePath = getResourcePathConfig();
        mContext = getApplicationContext();
        mPreferences = PreferenceManager.getInstance();
        //   LoadInstalledApp.mHandler = messageHandler2;

        // LoadInstalledApp loadApp = new LoadInstalledApp();
        //   loadApp.getPackageInforMap(mContext);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        mTxtTouch = (TextView) findViewById(R.id.txtTouch);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        WaveformView.mHandler = messageHandler2;
        mWaveformView = (WaveformView) findViewById(R.id.waveform_view);
        mWaveformView.initMessenger();

        SurfaceHolder sfhTrackHolder = mWaveformView.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
    /*    imagebutton = (ImageButton)findViewById(R.id.imagebutton);
        try{
            little = new GifAnimationDrawable(getResources().openRawResource(R.raw.audiospectrum));
            little.setOneShot(true);
            imagebutton.setImageDrawable(little);
            big = new GifAnimationDrawable(getResources().openRawResource(R.raw.audiospectrum));
            big.setOneShot(true);
        }catch(IOException ioe){

        }
*/
     /*   imagebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((GifAnimationDrawable) imagebutton.getDrawable()).setVisible(true, true);

            }
        });*/
/*        ((GifAnimationDrawable)imagebutton.getDrawable()).setVisible(true, true);
        if(imageview.getDrawable() == null) imageview.setImageDrawable(big);
        big.setVisible(true, true);*/
   /*     serverInfo.setAddr(mPreferences.getServerName());
        serverInfo.setPort(Integer.parseInt(mPreferences.getPortName()));
        serverInfo.setAppSpeech(this.getResources().getString(R.string.default_server_app_speech));
        serverInfo.setAppStatus(this.getResources().getString(R.string.default_server_app_status));

        init_speechkit(serverInfo);

*/
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // onRecognizerKaldi(mStartRecording); //online streamming
                onRecord(mStartRecording);
            }
        });
        progressView = (CircularProgressView) findViewById(R.id.progress_view);

        // Test the listener with logcat messages
        progressView.addListener(new CircularProgressViewAdapter() {
            @Override
            public void onProgressUpdate(float currentProgress) {
                Log.d("CPV", "onProgressUpdate: " + currentProgress);
            }

            @Override
            public void onProgressUpdateEnd(float currentProgress) {
                Log.d("CPV", "onProgressUpdateEnd: " + currentProgress);
            }

            @Override
            public void onAnimationReset() {
                Log.d("CPV", "onAnimationReset");
            }

            @Override
            public void onModeChanged(boolean isIndeterminate) {
                Log.d("CPV", "onModeChanged: " + (isIndeterminate ? "indeterminate" : "determinate"));
            }
        });
        startAnimationThreadStuff(1000);
        progressView.setVisibility(View.GONE);
        mWaveformView.setVisibility(View.GONE);

    /*    InputStream stream = null;
        try {
            stream = getAssets().open("audiospectrum.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        ExtAudioRecorderModified.mHandler = messageHandler2;

        Intent intent = new Intent(VavActivity.this, FloatingFaceBubbleService.class);
        final Messenger msg = new Messenger(messageHandler2);
        intent.putExtra("MESSENGER", msg);
        startService(intent);
        ContactList.getContactList(mContext);

        //   copyResource();

        //     getMappingText();
        new Thread(new Task()).start();
    }

    class Task implements Runnable {

        @Override

        public void run() {
            ContactList.getContactList(mContext);

            copyResource();

            getMappingText();

        }


    }

    private void getMappingText() {
        mMappingLanchApp.clear();
        mMappingSynthesis.clear();
        try {
            InputStream is_1 = getAssets().open("map_word.txt");
            BufferedReader r_1 = new BufferedReader(new InputStreamReader(is_1));
            String line;
            while ((line = r_1.readLine()) != null) {
                mMappingSynthesis.add(line);
            }

            for (int i = 0; i < mMappingSynthesis.size(); i++)
                for (int j = i + 1; j < mMappingSynthesis.size(); j++) {
                    String[] strMappingList_1 = mMappingSynthesis.get(i).split("->");
                    String[] strMappingList_2 = mMappingSynthesis.get(j).split("->");

                    if (strMappingList_1.length > 1 && strMappingList_2.length > 1) {
                        if (strMappingList_1[0].trim().length() > strMappingList_2[0].trim().length()) {
                            String tmp = mMappingSynthesis.get(i);
                            mMappingSynthesis.set(i, mMappingSynthesis.get(j));
                            mMappingSynthesis.set(j, tmp);
                        }
                    }
                }
            InputStream is_2 = getAssets().open("mapapp.txt");
            BufferedReader r_2 = new BufferedReader(new InputStreamReader(is_2));
            while ((line = r_2.readLine()) != null) {
                mMappingLanchApp.add(line);
            }
        } catch (IOException ex) {

        }
    }

    private void copyResource() {

        try {
            InputStream is_1 = getAssets().open("acronym.txt");
            BufferedReader r_1 = new BufferedReader(new InputStreamReader(is_1));
            StringBuilder total_1 = new StringBuilder();
            String line;
            while ((line = r_1.readLine()) != null) {
                total_1.append(line).append('\n');
            }
            writeStringAsFile(total_1.toString(), getAcronymConfig());

            InputStream is_2 = getAssets().open("acronym.txt");
            BufferedReader r_2 = new BufferedReader(new InputStreamReader(is_2));
            StringBuilder total_2 = new StringBuilder();
            while ((line = r_2.readLine()) != null) {
                total_2.append(line).append('\n');
            }
            writeStringAsFile(total_2.toString(), getUnitofMeasureConfig());
        } catch (IOException ex) {

        }

    }

    public static void writeStringAsFile(final String fileContents, String fileName) {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);

            stream.write(fileContents.getBytes());
            stream.close();

        } catch (IOException e) {

        } finally {

        }
    }

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        // Start animation after a delay so there's no missed frames while the app loads up
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!progressView.isIndeterminate()) {
                    progressView.setProgress(0f);
                    // Run thread to update progress every quarter second until full
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
                                // Must set progress in UI thread
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setProgress(progressView.getProgress() + 10);
                                    }
                                });
                                SystemClock.sleep(250);
                            }
                        }
                    });
                    updateThread.start();
                }
                // Alias for resetAnimation, it's all the same
                progressView.startAnimation();
            }
        }, delay);
    }

    private void onRecognizerKaldi(boolean start) {
        if (!start) {
            btnSpeak.setImageResource(R.mipmap.ico_mic_stop);
            _currentRecognizer.start();
            mStartRecording = true;
            //  lst_dialog.show();

        } else {
            btnSpeak.setImageResource(R.mipmap.ico_mic);
            _currentRecognizer.stopRecording();
            mStartRecording = false;
            mTxtTouch.setText("Đang xử lý...");
        }
    }

    /**
     * Showing google speech input dialog
     */
    public void startContactListActivity(final ArrayList<ContactItem> conlist, String title) {

        final AlertDialog.Builder mCallContactDialog;
        ListView mContactListView;
        final ContactAdapter mContactListViewAdapter;

        mCallContactDialog = new AlertDialog.Builder(VavActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.contact_list, null);
        mCallContactDialog.setView(convertView);
        mCallContactDialog.setTitle(title);
        mContactListView = (ListView) convertView.findViewById(R.id.mainListView);
        mContactListViewAdapter = new ContactAdapter(this, conlist);
        mContactListView.setAdapter(mContactListViewAdapter);
        final AlertDialog ad = mCallContactDialog.show();

        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, position, Toast.LENGTH_LONG).show();
                mContactListViewAdapter.setmSelectedItem(position);
                mContactListViewAdapter.notifyDataSetChanged();

                ad.dismiss();
                if (position >= 0 && position < conlist.size()) {
                    String strNumber = conlist.get(position).getmPhoneNumber();
                    callNumber(strNumber);
                }
            }
        });



 /*       Intent intent = new Intent(VavActivity.this, ContactListActivity.class);
        startActivity(intent);
*/
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
            startActivity(new Intent(this, SettingsActivity.class));
            //   startContactListActivity(ContactList.mContactList);
            return true;
        }
        if (id == R.id.action_syn) {

            startActivity(new Intent(this, SynthesisActivity.class));
            return true;
        }
        if (id == R.id.action_google) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtTouch.setText("Đang xử lý...");
                    progressView.setVisibility(View.VISIBLE);
                    btnSpeak.setVisibility(View.GONE);
                    mWaveformView.setVisibility(View.GONE);

                    //  btnSpeak.setImageResource(R.mipmap.loading);
                }
            });
            txtSpeechInput.setText("");
            convertWavToFlac(getFilename(), getFilenameFlac());
            new VoiceRecognitionServiceTask().execute(getFilenameFlac());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        checkOverlayPermission();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    // UPSTREAM channel. its servicing a thread and should have its own handler
    Handler messageHandler2 = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: // GET DOWNSTREAM json
                    stopRecording();

                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWaveformView.updateAudioData(ExtAudioRecorderModified.wavData);
                        }
                    });

                    break;
                case 3:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTxtTouch.setText("Đang xử lý...");
                            progressView.setVisibility(View.VISIBLE);
                            btnSpeak.setVisibility(View.GONE);
                            mWaveformView.setVisibility(View.GONE);
                            //  btnSpeak.setImageResource(R.mipmap.loading);
                        }
                    });


                    break;
                case 100:
                    bringApplicationToFront();
                    break;
                case 7:
                    audioSynthesisPlayer(getSynthesisFilename());
                    break;
                case 5:
                    bringApplicationToFront();
                    break;
            }

        }
    }; // UPstream handler end

    public void audioSynthesisPlayer(String filename) {
        //set up MediaPlayer

        MediaPlayer mp = new MediaPlayer();
        //	mp = new MediaPlayer();
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2) {
                LoadInstalledApp.mPlayingSynthesiFile = false;
                return true;
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LoadInstalledApp.mPlayingSynthesiFile = false;
            }
        });


        try {
            mp.setDataSource(filename);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            LoadInstalledApp.mPlayingSynthesiFile = false;
            appendLog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void bringApplicationToFront() {

        Intent notificationIntent = new Intent(this, VavActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
         /*   case KeyEvent.KEYCODE_VOLUME_UP:
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

    public boolean audioPlayer(String filename) {
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(filename);
            mp.prepare();
            mp.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void doSynthesis(String content) {
        String strServer = "http://10.30.153.42:59125";
        String localeType = "en_US";
        String textInput = String.format("\"%s\"", content);
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

            if (result.length() == 0) {
                audioPlayer(getSynthesisFilename());
            } else {
                txtSpeechInput.setText("Lỗi xẩy ra, Chưa tổng hợp được!: " + result);
            }

        }
    }

    public void appendLog(String text) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }


        File logFile = new File(file.getAbsolutePath() + "/" + "logtime.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            String filename = file.getAbsolutePath() + "/" + "logtime.txt";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(text + "\n");
            fw.close();
        } catch (IOException ioe) {
        }

    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, MY_OVERLAY_PERMISSIONS_REQUEST);
        }
    }

    public static boolean canDrawOverlays(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }



/*    public void startAquisition(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                //elapsedTime=0;
                mStartRecording = true;
                if (recordTask == null)
                    recordTask = new RRAudioRecord();
                recordTask.execute();
                //startButton.setText("RESET");
            }
        }, 500);
    }*/

}
