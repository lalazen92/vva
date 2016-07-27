package vrd.cyberspace.viettel.vrd.preference;
import android.content.SharedPreferences;

import vrd.cyberspace.viettel.vrd.VavActivity;

public class PreferenceManager {

	private static class Name {
		public static final String DEFAULT_NAME = "name";
		public static final String NUMBER_USE = "use";
		public static final String SEVER_NAME = "server";
		public static final String POS_RECORD = "pos";
		public static final String ID_TEXTRECORD = "id";
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

	public String getid() {
		return mPrefs.getString(Name.ID_TEXTRECORD, "");
	}
	public void setid(String name) {
		putString(Name.ID_TEXTRECORD, name);
	}

	public int getPos() {
		return mPrefs.getInt(Name.POS_RECORD, 0);
	}
	public void setPos(int pos) {
		putInt(Name.POS_RECORD, pos);
	}
	public String getName() {
		return mPrefs.getString(Name.DEFAULT_NAME, "record");
	}
	public void setServerName(String name) {
		putString(Name.SEVER_NAME, name);
	}
	public String getServerName() {
		return mPrefs.getString(Name.SEVER_NAME, "http://10.30.153.42:8000/");
	}
	public void setName(String name) {
		putString(Name.DEFAULT_NAME, name);
	}
	public String getNumberUse() {
		return mPrefs.getString(Name.NUMBER_USE, "0/0");
	}
	public void setNumberUse(String name) {
		putString(Name.NUMBER_USE, name);
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
