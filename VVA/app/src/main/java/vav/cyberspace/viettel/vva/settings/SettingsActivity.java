/*
 * Copyright 2014 A.C.R. Development
 */
package vav.cyberspace.viettel.vva.settings;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.TextView;
import vav.cyberspace.viettel.vva.R;
import vav.cyberspace.viettel.vva.preference.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {

	private static final int API = android.os.Build.VERSION.SDK_INT;

	private PreferenceManager mPreferences;
    private EditText mTextPort;
	private EditText mTextServer;
	private CheckBox mchkDecodeType;
	private CheckBox mchkReadMessage;
	private CheckBox mchkHSMM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mTextPort = (EditText) findViewById(R.id.txtPort);
		mTextServer = (EditText) findViewById(R.id.txtServer);
		mchkDecodeType = (CheckBox) findViewById(R.id.chkDecode);
		mchkReadMessage = (CheckBox) findViewById(R.id.chkReadMessage);
		mchkHSMM = (CheckBox) findViewById(R.id.chkHsmmm);
		mchkDecodeType.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					mPreferences.setRecogType("go");
				}else{
					mPreferences.setRecogType("nogo");
				}

			}
		});
		mchkReadMessage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					mPreferences.setReadMessage("1");
				}else{
					mPreferences.setReadMessage("0");
				}

			}
		});
		mchkHSMM.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					mPreferences.setReadHSMM("1");
				}else{
					mPreferences.setReadHSMM("0");
				}

			}
		});
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        mPreferences.setPortName(mTextPort.getText().toString());
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

        mTextPort.setText(mPreferences.getPortName());
		mTextServer.setText(mPreferences.getServerName());
		if(mPreferences.getRecogType().compareToIgnoreCase("go") == 0)
			mchkDecodeType.setChecked(true);
		else
			mchkDecodeType.setChecked(false);

		if(mPreferences.getReadMessage().compareToIgnoreCase("1") == 0)
			mchkReadMessage.setChecked(true);
		else
			mchkReadMessage.setChecked(false);

		if(mPreferences.getReadHSMM().compareToIgnoreCase("1") == 0)
			mchkHSMM.setChecked(true);
		else
			mchkHSMM.setChecked(false);
		// initialize UI
	}


}
