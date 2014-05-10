package ait.cs.sad.moodle;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

public class MyService extends Service {


	/*private static final String TAG = "MyService";*/
	private static int val = 1;

	Pubnub pubnub;

	@Override
	public void onCreate() {
		
		super.onCreate();
		
		pubnub = new Pubnub("pub-c-b359888b-4119-4493-809c-48112f0f8236",
				"sub-c-ced59c3e-d741-11e3-8c07-02ee2ddab7fe");
		
		try {
			
  		  pubnub.subscribe("channel1", new Callback() {

  		    
  		      @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  		      @Override
  		      public void successCallback(String channel, Object message) {
  		         /* Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
  		                     + message.getClass() + " : " + message.toString());*/
  		          
  		        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						MyService.this)
						.setSmallIcon(R.drawable.message)
						.setContentTitle("New Message")
						.setAutoCancel(true)
						.setContentText(message.toString());
			
				Intent resultIntent = new Intent(MyService.this,
						Notice.class);
				
				resultIntent.putExtra("message", message.toString());
				//resultIntent.putExtra("notification_id", val);
				
				/*TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(MyService.this);
				
				stackBuilder.addParentStack(Notice.class);
				
				stackBuilder.addNextIntent(resultIntent);*/
				
				PendingIntent intent = PendingIntent.getActivity(MyService.this, 0, resultIntent, 0);
				
				/*PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0,
								PendingIntent.FLAG_UPDATE_CURRENT);*/
				//mBuilder.setContentIntent(resultPendingIntent);
				mBuilder.setContentIntent(intent);
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
				mNotificationManager.notify(val, mBuilder.build());
				val++;
  		      }

  		    
  		  });
  		} catch (PubnubException e) {
  		  /*Log.d("PUBNUB",e.toString());*/
  		}
	}
	
	
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void handleIntent(Intent intent) {
       
       // new PollTask().execute();
    	
    }

    
    
    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }
    
    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
       
    }
}
