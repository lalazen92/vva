package vav.cyberspace.viettel.vva.broadcast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import vav.cyberspace.viettel.vva.contacts.ContactList;
import vav.cyberspace.viettel.vva.lanchapp.LoadInstalledApp;

public class BroadcastMessenger extends BroadcastReceiver {

	public static final String SMS_EXTRA_NAME = "pdus";
	private Context context=null;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		processSMS(intent);
//		processVolumePress(context, intent);
	}
	public  void  processVolumePress(Context context, Intent intent){
		KeyEvent ke = (KeyEvent)intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
		if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			System.out.println("I got volume up event");
		}else if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			System.out.println("I got volume key down event");
		}
	}
	private Map<String, String> RetrieveMessages(Intent intent) {
		Map<String, String> msg = null;
		SmsMessage[] msgs = null;
		Bundle bundle = intent.getExtras();
		if (bundle != null && bundle.containsKey(SMS_EXTRA_NAME)) {
			Object[] pdus = (Object[]) bundle.get(SMS_EXTRA_NAME);
			if (pdus != null) {
				int nbrOfpdus = pdus.length;
				msg = new HashMap<String, String>(nbrOfpdus);
				msgs = new SmsMessage[nbrOfpdus];
				// There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
				// However, send long SMS of same sender in one message
				for (int i = 0; i < nbrOfpdus; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
					String originatinAddress = msgs[i].getOriginatingAddress();
					// Check if index with number exists
					if (!msg.containsKey(originatinAddress)) {
						// Index with number doesn't exist
						// Save string into associative array with sender number as index
						msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());
					} else {
						// Number has been there, add content but consider that
						// msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
						// so just add the part of the current PDU
						String previousparts = msg.get(originatinAddress);
						String msgString = previousparts + msgs[i].getMessageBody();
						msg.put(originatinAddress, msgString);
					}
				}
			}
		}
		return msg;
	}
	public void processSMS(Intent intent)
	{
		Map<String, String> msg = RetrieveMessages(intent);
		if(msg != null){
			for (Map.Entry<String, String> entry : msg.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				String body = value;
				String contactname = key;

				contactname = contactname.replace("+84", "0");
				ContactList.getContactList(context);
				ContactList con = new ContactList();
				contactname = con.getContactNameFromNumber(contactname);
				if(contactname.length() == 0){
					contactname = key.replace("+84", "0");
				}
				processCommand(body, contactname);
			}
		}
				// Get the SMS map from Intent
	//	Bundle extras = intent.getExtras();

/*
		if ( extras != null )
		{
			// Get received SMS array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
			for (int i = 0; i < smsExtra.length; ++i)
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
				String body = sms.getMessageBody().toString();
				String contactname = sms.getDisplayOriginatingAddress();
				contactname = contactname.replace("+84", "0");
				ContactList.getContactList(context);
				ContactList con = new ContactList();
				contactname = con.getContactNameFromNumber(contactname);
				processCommand(body, contactname);
			}
		}*/
	}
	public void processCommand(String command, String contactname){

		LoadInstalledApp loadApp = new LoadInstalledApp();
		loadApp.processCommand(command, context, contactname);
	/*	String arr[] = command.split(" ");
		if(arr.length!=2)
			return ;
		if(!arr[0].equalsIgnoreCase("VVA") || arr[1].length() == 0)
			return;
		LoadInstalledApp loadApp = new LoadInstalledApp();
		String packageItemInfo = loadApp.getPackNameInfo(arr[1]);
		packageItemInfo = packageItemInfo.toLowerCase();
		if(packageItemInfo == null || packageItemInfo.length() == 0)
			return;
		loadApp.launchApp(packageItemInfo, context);*/
	}
/*	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_DOWN) {
					//TODO
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN) {
					//TODO
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
		}
	}*/
}
