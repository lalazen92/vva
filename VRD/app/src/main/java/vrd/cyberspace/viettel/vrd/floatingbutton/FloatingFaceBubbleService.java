package vrd.cyberspace.viettel.vrd.floatingbutton;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import vrd.cyberspace.viettel.vrd.R;

public class FloatingFaceBubbleService extends Service {
    private WindowManager windowManager;
    private ImageView floatingFaceBubble;
    private Messenger messageHandler;

    public void onCreate() {
        super.onCreate();

        floatingFaceBubble = new ImageView(this);
        //a face floating bubble as imageView
        floatingFaceBubble.setImageResource(R.drawable.floating_bubble);

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        //here is all the science of params
        final LayoutParams myParams = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_PHONE,
            LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x=0;
        myParams.y=100;
        // add a floatingfacebubble icon in window
        windowManager.addView(floatingFaceBubble, myParams);

        try{
        	//for moving the picture on touch and slide
        	floatingFaceBubble.setOnTouchListener(new View.OnTouchListener() {
                LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //remove face bubble on long press
                	if(System.currentTimeMillis()-touchStartTime>ViewConfiguration.getLongPressTimeout() && initialTouchX== event.getX()){
                		windowManager.removeView(floatingFaceBubble);
                		stopSelf();
                		return false;
                		
                	}
                	switch(event.getAction()){
                    
                    
                    case MotionEvent.ACTION_DOWN:
                    	touchStartTime = System.currentTimeMillis();
                        initialX = myParams.x;
                        initialY = myParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                    	
                        myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(v, myParams);
                        break;
                    }
                    return false;
                }
            });

            floatingFaceBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessage();

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");
        return START_NOT_STICKY;
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}
    public void sendMessage() {
        Message message = Message.obtain();
        message.what = 100;
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}