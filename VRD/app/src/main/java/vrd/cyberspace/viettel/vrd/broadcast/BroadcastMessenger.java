package vrd.cyberspace.viettel.vrd.broadcast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.KeyEvent;

import vrd.cyberspace.viettel.vrd.lanchapp.LoadInstalledApp;

public class BroadcastMessenger extends BroadcastReceiver {

	public static final String SMS_EXTRA_NAME = "pdus";
	private Context context=null;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		processSMS(intent);
	/*	processVolumePress(context, intent);*/
	}
	public  void  processVolumePress(Context context, Intent intent){
		KeyEvent ke = (KeyEvent)intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
		if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			System.out.println("I got volume up event");
		}else if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			System.out.println("I got volume key down event");
		}
	}
	public void processSMS(Intent intent)
	{
		// Get the SMS map from Intent
		Bundle extras = intent.getExtras();

		//String messages = "";

		if ( extras != null )
		{
			// Get received SMS array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
			for (int i = 0; i < smsExtra.length; ++i)
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
				String body = sms.getMessageBody().toString();
				processCommand(body);
			}
		}
	}
	public void processCommand(String command){

		LoadInstalledApp loadApp = new LoadInstalledApp();
		loadApp.processCommand(command, context);
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
