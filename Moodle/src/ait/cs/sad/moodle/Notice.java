package ait.cs.sad.moodle;

import android.os.Bundle;
import android.widget.TextView;

public class Notice extends BaseActivity {


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
