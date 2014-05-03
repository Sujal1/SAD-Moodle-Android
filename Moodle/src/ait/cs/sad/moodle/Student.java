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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Student extends Activity {

	public static final String MyPREFERENCES = "MyPrefs";
	SharedPreferences sharedPref;
	
	private MySimpleArrayAdapter adapter;
	ListView list_children;
		
	List<String> name_list = new ArrayList<String>();
	List<String> level_list = new ArrayList<String>();
	List<String> id_list = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students);

		list_children = (ListView) findViewById(R.id.listView_children);
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		loadData();

	}

	public void loadData() {
		SQLiteDatabase db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE,
				null);
		db.execSQL("CREATE TABLE IF NOT EXISTS Student (studentID VARCHAR, Name VARCHAR, parentID VARCHAR);");
		String sql = "SELECT * FROM Student WHERE parentID = '" + sharedPref.getString("parent_token", "0") + "';";
		Cursor c = db.rawQuery(sql, null);
		if (c.getCount() == 0) {
			Toast.makeText(Student.this, "Refresh to load data.", Toast.LENGTH_SHORT).show();
			c.close();
			db.close();
		} else {
			c.moveToFirst();
			do {
				name_list.add(c.getString(c.getColumnIndex("Name")));
				id_list.add(c.getString(c.getColumnIndex("studentID")));
			} while (c.moveToNext());
			
			adapter = new MySimpleArrayAdapter(
					Student.this, name_list, 1);
			
			list_children.setAdapter(adapter);
			list_children.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int student_row_count, long arg3) {
					Intent i = new Intent(Student.this, Course.class);
					i.putExtra("student_id", id_list.get(student_row_count));
					startActivity(i);
				}
			});
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
			case R.id.action_logout:
				Editor editor = sharedPref.edit();
				editor.putString("parent_token", "0");
				editor.putString("moodle_server", "");
				editor.commit();
				Toast.makeText(Student.this, "Token deleted", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.action_synchronise:
				if (checkConnection()) {
					try {
						/*new Synchronizer(Student.this, sharedPref.getString(
									"parent_token", "0")).execute().get();*/
						 new Synchronizer(Student.this).execute().get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					if (adapter != null) {
						 name_list.clear();
						 adapter.notifyDataSetChanged();
					}
					loadData();
				} else {
					Toast.makeText(Student.this, "No Network Connection", Toast.LENGTH_SHORT).show();
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


/*@Override
public boolean onPrepareOptionsMenu(Menu menu) {
	MenuItem comment_menu = menu.findItem(R.id.action_comment);
	comment_menu.setVisible(false);
	
	return super.onPrepareOptionsMenu(menu);
}*/
