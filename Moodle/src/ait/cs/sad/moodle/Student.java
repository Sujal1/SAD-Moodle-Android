package ait.cs.sad.moodle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class Student extends Activity {

	public static final String MyPREFERENCES = "MyPrefs" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students);
		SharedPreferences sharedPref;
		
		
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		Toast.makeText(Student.this, sharedPref.getString("parent_token", "0"), 5000).show();
	}
}
