package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Course extends Activity {

	String student_id;
	ListView list_course;
	public static final String MyPREFERENCES = "MyPrefs";
	
	private MySimpleArrayAdapter adapter;
	
	SharedPreferences sharedPref;
	
	List<String> course_ids = new ArrayList<String>();
	List<String> course_names = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		
		list_course = (ListView) findViewById(R.id.listView_course);
		student_id = getIntent().getExtras().getString("student_id");

		//new MyAsyncTask().execute();
		
		loadCourses();
	}

	
	
public void loadCourses() {
		SQLiteDatabase db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE,
				null);
		db.execSQL("CREATE TABLE IF NOT EXISTS Course (courseID VARCHAR, studentID VARCHAR, Name VARCHAR);");
		String sql = "SELECT * FROM Course WHERE studentID = '" + student_id + "';";
		Cursor c = db.rawQuery(sql, null);
		if (c.getCount() == 0) {
			Toast.makeText(Course.this, "NULLLLLLLLLL", Toast.LENGTH_SHORT).show();
			c.close();
			db.close();
		} else {
			c.moveToFirst();
			do {
				course_ids.add(c.getString(c.getColumnIndex("courseID")));
				course_names.add(c.getString(c.getColumnIndex("Name")));
			} while (c.moveToNext());
			
			adapter = new MySimpleArrayAdapter(
					Course.this, course_names, 2);
			
			list_course.setAdapter(adapter);
			list_course.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int course_id, long arg3) {
					/*Intent i = new Intent(Course.this, Attendance.class);
					i.putExtra("student_id", student_id);
					i.putExtra("course_id", course_id);
					startActivity(i);*/
					
					Intent i = new Intent(Course.this, Attendance.class);
					Bundle extras = new Bundle();
					extras.putString("student_id", student_id);
					extras.putInt("course_id", course_id);
					i.putExtras(extras);
					startActivity(i);
				}
			});
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_actions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_logout:
			
			Editor editor = sharedPref.edit();
			editor.putString("parent_token", "0");
			editor.commit();
			Toast.makeText(Course.this, "Token deleted", Toast.LENGTH_SHORT).show();
			return true;
		
		case R.id.action_synchronise:
			if (checkConnection()) {
				
				 try {
					/*new Synchronizer(Course.this, sharedPref.getString(
								"parent_token", "0")).execute().get();*/
					 new Synchronizer(Course.this).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 course_names.clear();
				 adapter.notifyDataSetChanged();
				 loadCourses();
			} else {
				Toast.makeText(Course.this, "No Network Connection", Toast.LENGTH_SHORT).show();
			}
			return true;
			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	private boolean checkConnection() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
}
