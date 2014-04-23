package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;

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

	private MySimpleArrayAdapter adapter;
	
	SharedPreferences sharedPref;
	ListView list_children;

	JSONArray jArray, jArray_temp;
	String url = "http://203.159.6.202/moodle/webservice/rest/server.php";
	
	List<String> name_list = new ArrayList<String>();
	List<String> level_list = new ArrayList<String>();
	List<String> id_list = new ArrayList<String>();
	
	String[] name, id;
	

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
		
		/********** LOAD LOCAL SQLITE DATABASE ***********************/
		SQLiteDatabase db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE,
				null);
		db.execSQL("CREATE TABLE IF NOT EXISTS Student (studentID VARCHAR, Name VARCHAR);");
		String sql = "SELECT * FROM Student;";
		Cursor c = db.rawQuery(sql, null);
		if (c.getCount() == 0) {
			Toast.makeText(Student.this, "NULLLLLLLLLL", Toast.LENGTH_SHORT).show();
			c.close();
			db.close();
		} else {
			
			
			c.moveToFirst();
			do {
				
				name_list.add(c.getString(c.getColumnIndex("Name")));
				id_list.add(c.getString(c.getColumnIndex("studentID")));
			} while (c.moveToNext());
			
			/*name = new String[name_list.size()];
			name_list.toArray(name);
			id = new String[id_list.size()];
			id_list.toArray(id);*/
			
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
			editor.commit();
			Toast.makeText(Student.this, "Token deleted", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_synchronise:
			
			if (checkConnection()) {
				
				 try {
					new Synchronizer(Student.this, sharedPref.getString(
								"parent_token", "0")).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
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

/*	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		String url = "http://203.159.6.202/moodle/webservice/rest/server.php";
		boolean network;
		int temp = 0;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(Student.this);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (checkConnection()) {
				*//********** CONNNECTED TO THE NETWORK ***********************//*
				network = true;
				try {
					jArray_temp = JSONfunction
							.getJSONfromURL(
									url
											+ "?wstoken="
											+ sharedPref.getString(
													"parent_token", "0")
											+ "&wsfunction=local_wstemplate_get_child&moodlewsrestformat=json",
									null);
					
				} catch (Exception e) {
						//Log.d("!!!!!!!!!!!!!!", "NO NETWORK");
				}

				if (jArray_temp != null) {
					SQLiteDatabase db = openOrCreateDatabase("MyDatabase",
							MODE_PRIVATE, null);
					db.execSQL("CREATE TABLE IF NOT EXISTS Student (ID VARCHAR, Name VARCHAR);");
					String sql = "SELECT * FROM Student;";
					Cursor c = db.rawQuery(sql, null);
					if (c.getCount() == 0) {
						temp = 1;
					}
					c.close();
					for (int i = 0; i < jArray_temp.length(); i++) {
						JSONObject jObject;
						try {
							jObject = jArray_temp.getJSONObject(i);
							String name = jObject.getString("fullname")
									.toUpperCase();
							String id = jObject.getString("id");
							Log.d("^^^^^^^^", name);
							Log.d("********", id);
							*//*** SAVE INTO SQLITE ***//*
							if (temp == 1) {
								String sql1 = "INSERT INTO Student (ID, Name)"
										+ " VALUES('" + id + "','" + name
										+ "');";
								db.execSQL(sql1);
							} else {
								String sql2 = "SELECT * FROM Student WHERE ID='"
										+ id + "'";
								Cursor c2 = db.rawQuery(sql2, null);
								if (c2.getCount() == 0) {
									String sql3 = "INSERT INTO Student (ID, Name)"
											+ " VALUES('"
											+ id
											+ "','"
											+ name
											+ "');";
									db.execSQL(sql3);
								}
								c2.close();
							}
							*//*** SAVE FINISH **//*

						} catch (JSONException e) {
							e.printStackTrace();
						}
					
					}
					db.close();
				} else {
					//NULL ARRAY
					Log.d("#############", "NULL JARAY");
				}
			} else {
				network = false;
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
			
			
			if (network == false) {
				Toast.makeText(Student.this, "Not connected to the network", 5000).show();
			}
			else if (jArray_temp == null) {
				Toast.makeText(
						Student.this,
						"You have no children mapped.",
						5000).show();
			} else {
				Intent i = new Intent(Student.this, Student.class);
				startActivity(i);
				//loadData();
				
			}
		}

	}*/

}
