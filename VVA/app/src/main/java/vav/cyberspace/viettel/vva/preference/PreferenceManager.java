package vav.cyberspace.viettel.vva.preference;
import android.content.SharedPreferences;

import vav.cyberspace.viettel.vva.VavActivity;

public class PreferenceManager {

	private static class Name {
		public static final String SERVERNAME = "svr";
		public static final String PORTNAME = "prt";
		public static final String USERECOGTYPE = "recogtype";
		public static final String READMESSAGE = "readmessa";
		public static final String HSMM = "hsmm";
	}

	private static PreferenceManager mInstance;
	private static SharedPreferences mPrefs;

	private static final String PREFERENCES = "settings";

	public static PreferenceManager getInstance() {
		if (mInstance == null) {
			mInstance = new PreferenceManager();
		}
		return mInstance;
	}

	private PreferenceManager() {
		mPrefs = VavActivity.getContext().getSharedPreferences(PREFERENCES, 0);
	}

	public void setReadMessage(String name) {
		putString(Name.READMESSAGE, name);
	}
	public String getReadMessage() {
		return mPrefs.getString(Name.READMESSAGE, "0");
	}
	public void setReadHSMM(String name) {
		putString(Name.HSMM, name);
	}
	public String getReadHSMM() {
		return mPrefs.getString(Name.HSMM, "0");
	}

	public void setRecogType(String name) {
		putString(Name.USERECOGTYPE, name);
	}
	public String getRecogType() {
		return mPrefs.getString(Name.USERECOGTYPE, "nogo");
	}

	public void setServerName(String name) {
		putString(Name.SERVERNAME, name);
	}
	public String getServerName() {
		return mPrefs.getString(Name.SERVERNAME, "10.30.153.42");
	}

	public void setPortName(String name) {
		putString(Name.PORTNAME, name);
	}
	public String getPortName() {
		return mPrefs.getString(Name.PORTNAME, "8888");
	}
	private void putBoolean(String name, boolean value) {
		mPrefs.edit().putBoolean(name, value).apply();
	}

	private void putInt(String name, int value) {
		mPrefs.edit().putInt(name, value).apply();
	}

	private void putString(String name, String value) {
		mPrefs.edit().putString(name, value).apply();
	}


}
