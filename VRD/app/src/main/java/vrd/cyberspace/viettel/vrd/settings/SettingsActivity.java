/*
 * Copyright 2014 A.C.R. Development
 */
package vrd.cyberspace.viettel.vrd.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import vrd.cyberspace.viettel.vrd.R;
import vrd.cyberspace.viettel.vrd.preference.PreferenceManager;
import vrd.cyberspace.viettel.vrd.wav.ExtAudioRecorderModified;


public class SettingsActivity extends AppCompatActivity {

	private static final int API = android.os.Build.VERSION.SDK_INT;
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private PreferenceManager mPreferences;
    private EditText mText;
    private TextView mTextView;
	private TextView mTextViewID;
	private EditText mTextServer;
	private ImageButton btnTryRecord;
	ExtAudioRecorderModified audioRecorder = null;

	boolean mStartRecording = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
        mText = (EditText) findViewById(R.id.txtName);
        mTextView = (TextView) findViewById(R.id.txtUse);
		mTextViewID = (TextView) findViewById(R.id.txtRecordTxt);
		mTextServer = (EditText) findViewById(R.id.txtServer);
		btnTryRecord = (ImageButton) findViewById(R.id.btntry);
		btnTryRecord.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onRecordTest(mStartRecording);
			}
		});
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        mPreferences.setName(mText.getText().toString());
        mPreferences.setNumberUse(mTextView.getText().toString());
		mPreferences.setServerName(mTextServer.getText().toString());
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


        // mPreferences storage
        mPreferences = PreferenceManager.getInstance();

        mText.setText(mPreferences.getName());
        mTextView.setText(mPreferences.getNumberUse());
		mTextServer.setText(mPreferences.getServerName());
		mTextViewID.setText(mPreferences.getid());
		// initialize UI
	}

	private void onRecordTest(boolean start) {
		if (!start) {
			btnTryRecord.setImageResource(R.mipmap.tryplay);
			startRecording(getTestFilename());
		} else {
			btnTryRecord.setImageResource(R.mipmap.tryrecord);
			stopRecording();
			audioPlayer(getTestFilename());
		}
	}
	private String getTestFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + "vrdtest"+ AUDIO_RECORDER_FILE_EXT_WAV);
	}
	private void startRecording(String filename){

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
	public void audioPlayer(String filename){
		//set up MediaPlayer
		MediaPlayer mp = new MediaPlayer();

		try {
			mp.setDataSource(filename);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
