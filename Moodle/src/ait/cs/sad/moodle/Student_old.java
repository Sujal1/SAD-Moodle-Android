package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Student_old extends Activity {

	public static final String MyPREFERENCES = "MyPrefs";

	SharedPreferences sharedPref;
	ListView list_children;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students);

		list_children = (ListView) findViewById(R.id.listView_children);

		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		// Toast.makeText(Student.this, sharedPref.getString("parent_token",
		// "0"), 5000).show();

		new MyAsyncTask().execute();
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
			editor.commit();
			Toast.makeText(Student_old.this, "Token deleted", 5000).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private boolean checkConnection() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			//Toast.makeText(Student.this, "TRUE", 5000).show();
			return true;
		}
		//Toast.makeText(Student.this, "FALSE", 5000).show();
		return false;
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		JSONArray jArray;
		// String url = "http://202.28.195.68/get_children.php";
		String url = "http://203.159.6.202/moodle/webservice/rest/server.php";
		List<String> name_list = new ArrayList<String>();
		List<String> level_list = new ArrayList<String>();
		List<String> id_list = new ArrayList<String>();
		String[] name, id;
		int temp = 0;

		public MyAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(Student_old.this);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (checkConnection()) {
			/**********CONNNECTED TO THE NETWORK***********************/
				try {
					jArray = JSONfunction
							.getJSONfromURL(
									url
											+ "?wstoken="
											+ sharedPref.getString("parent_token",
													"0")
											+ "&wsfunction=local_wstemplate_get_child&moodlewsrestformat=json",
									null);
					// jArray = JSONfunction.getJSONfromURL(url+"?wstoken=1234567",
					// null);
				} catch (Exception e) {

				}

				if (jArray != null) {
					SQLiteDatabase db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);
					db.execSQL("CREATE TABLE IF NOT EXISTS Student (ID VARCHAR, Name VARCHAR);");
					String sql = "SELECT * FROM Student;";
			    	Cursor c = db.rawQuery(sql, null);
					if (c.getCount() == 0) {
						temp = 1;
					}
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject jObject;
						try {
							jObject = jArray.getJSONObject(i);
							String name = jObject.getString("fullname")
									.toUpperCase();
							name_list.add(name);
							String id = jObject.getString("id");
							id_list.add(id);

							/*** SAVE INTO SQLITE ***/
							if (temp == 1) {
								String sql1="INSERT INTO Student (ID, Name)"+" VALUES('" +id+ "','" + name +"');";
					    		db.execSQL(sql1);
							} else {
								String sql2 = "SELECT * FROM Student WHERE ID='"+id+"'";
						    	Cursor c2 = db.rawQuery(sql2, null);
						    	if (c2.getCount()==0) {
						    		String sql3="INSERT INTO Student (ID, Name)"+" VALUES('" +id+ "','" + name +"');";
						    		db.execSQL(sql3);
						    	}
							}
							/*** SAVE FINISH **/

						} catch (JSONException e) {
							e.printStackTrace();
						}
						/*name = new String[name_list.size()];
						name_list.toArray(name);
						id = new String[id_list.size()];
						id_list.toArray(id);*/
					}
				}
			} else {
			/**********NOT CONNNECTED TO THE NETWORK***********************/
				SQLiteDatabase db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);
				db.execSQL("CREATE TABLE IF NOT EXISTS Student (ID VARCHAR, Name VARCHAR);");
				String sql = "SELECT * FROM Student;";
		    	Cursor c = db.rawQuery(sql, null);
				if (c.getCount() == 0) {
					c.close();
					db.close();
				} else {	
					c.moveToFirst();
					do{
						name_list.add(c.getString(c.getColumnIndex("Name")));
						id_list.add(c.getString(c.getColumnIndex("ID")));
					}while (c.moveToNext());
				}
				jArray = new JSONArray();
				jArray.put(1);
			}
			name = new String[name_list.size()];
			name_list.toArray(name);
			id = new String[id_list.size()];
			id_list.toArray(id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			super.onPostExecute(result);

			if (jArray == null) {
				Toast.makeText(Student_old.this, "You have no children mapped !",
						5000).show();
			} else {
				Toast.makeText(Student_old.this, jArray.toString(), 5000).show();

				/*MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
						Student_old.this, name, 1);
				list_children.setAdapter(adapter);*/
				list_children.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Toast.makeText(Student_old.this, id[arg2], 5000).show();
						Intent i = new Intent(Student_old.this, Course.class);
						i.putExtra("student_id", id[arg2]);
						startActivity(i);

					}
				});
			}
		}
	}
}
