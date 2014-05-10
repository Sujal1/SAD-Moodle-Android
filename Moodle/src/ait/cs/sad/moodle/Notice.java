package ait.cs.sad.moodle;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class Notice extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.notice);
		
		String message = getIntent().getExtras().getString("message");
		//int NOTIFICATION_ID = getIntent().getExtras().getInt("notification_id");
		
		/*NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);*/
		
		TextView txtMessage = (TextView)findViewById(R.id.textViewMessage);
		
		txtMessage.setText(message);
		
	}
	
}
