/*
 * Copyright 2014 A.C.R. Development
 */
package vav.cyberspace.viettel.vva.contacts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import vav.cyberspace.viettel.vva.R;
import vav.cyberspace.viettel.vva.listview.ContactAdapter;
import vav.cyberspace.viettel.vva.preference.PreferenceManager;


public class ContactListActivity extends AppCompatActivity {

	private static final int API = android.os.Build.VERSION.SDK_INT;
	private ArrayList<ContactItem> mContactList = new ArrayList<>();
	private PreferenceManager mPreferences;
	private ListView mListView;
	private ContactAdapter listAdapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		mContactList = ContactList.mContactList;
		init();
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


        // mPreferences storage
        mPreferences = PreferenceManager.getInstance();
		mListView = (ListView) findViewById( R.id.mainListView);
		listAdapter = new ContactAdapter( this, mContactList);
		mListView.setAdapter( listAdapter );
	}


}
