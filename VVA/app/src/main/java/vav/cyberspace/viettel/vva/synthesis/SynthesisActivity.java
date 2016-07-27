/*
 * Copyright 2014 A.C.R. Development
 */
package vav.cyberspace.viettel.vva.synthesis;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

import vav.cyberspace.viettel.vva.R;
import vav.cyberspace.viettel.vva.VavActivity;
import vav.cyberspace.viettel.vva.downloadfile.DownloadFileFromURL;
import vav.cyberspace.viettel.vva.lanchapp.LoadInstalledApp;
import vav.cyberspace.viettel.vva.utils.NormalizerSynthesis;


public class SynthesisActivity extends AppCompatActivity {

	private static final int API = android.os.Build.VERSION.SDK_INT;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	public static boolean mPlayingSynthesiFile = false;
    private EditText mText;
	private TextView mTxtViewStatus;
	private ImageButton btnTryRecord;
	/*private MediaPlayer mp;*/
	public HashMap<Integer,String> mapTone = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synthesis);
        mText = (EditText) findViewById(R.id.txtName);
		btnTryRecord = (ImageButton) findViewById(R.id.btntry);
		mTxtViewStatus = (TextView)findViewById(R.id.txtStatus);
		btnTryRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
/*				LoadInstalledApp loadApp = new LoadInstalledApp();
				String text = mText.getText().toString();
				text = utils.mappingstringtoapp(text);
				loadApp.processVoiceCommand(text, VavActivity.mContext);*/
				onSynthesis();
			}
		});
		init();
		mapTone.put(768, "f");
		mapTone.put(769, "s");
		mapTone.put(777, "r");
		mapTone.put(771, "x");
		mapTone.put(803, "j");
		mPlayingSynthesiFile = false;
	}
	@Override
	public void onPause(){
		super.onPause();
		mPlayingSynthesiFile = false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	@SuppressLint("NewApi")
	public void init() {
		// set up ActionBar


/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

		// initialize UI
	}

	private void onSynthesis() {
		doSynthesis();
	}
	private String getSynthesisFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + "syn" + AUDIO_RECORDER_FILE_EXT_WAV);
	}
	private void doSynthesis(){
		if(mPlayingSynthesiFile == true)
			return;
		String strServer = "http://10.30.153.42:59125";
		String localeType = "vi";
	//	String [] liststring = mText.getText().toString().split(" ");
		String textInput = "";
		NormalizerSynthesis rmv = new NormalizerSynthesis();
		textInput = rmv.nomarlize(mText.getText().toString(), VavActivity.mResourcePath, VavActivity.mMappingSynthesis);
		/*for (int i = 0 ; i < liststring.length; i++){
			textInput += rmv.convertToken(liststring[i]) + " ";
		}*/
		textInput = textInput.trim();
		String strSyn = String.format("\"%s\"", textInput);
		//String textInput = String.format("\"%s\"", convertToken(mText.getText().toString()));
		String savefilePath = getSynthesisFilename();
		mTxtViewStatus.setText("Đang xử lý...");
		mPlayingSynthesiFile = true;
		new DownloadFileServiceTask().execute(strServer, localeType, strSyn, savefilePath);
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
				mTxtViewStatus.setText("Bắt đầu đọc");
				audioPlayer(getSynthesisFilename());
				mTxtViewStatus.setText("Đọc xong");

			}else{
				mTxtViewStatus.setText("Lỗi: "+result);
				mPlayingSynthesiFile = false;

			}

		}
	}


	public void audioPlayer(String filename){
		//set up MediaPlayer
		MediaPlayer mp = new MediaPlayer();
	//	mp = new MediaPlayer();
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
			e.printStackTrace();
			mPlayingSynthesiFile = false;
		}
	}
}
