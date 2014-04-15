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
import android.os.AsyncTask;
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
			Toast.makeText(Student.this, "Token deleted", 5000).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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

		public MyAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(Student.this);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

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
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObject;
					try {
						jObject = jArray.getJSONObject(i);
						name_list.add(jObject.getString("fullname")
								.toUpperCase());
						id_list.add(jObject.getString("id"));
						// Toast.makeText(Student.this, child_name,
						// 5000).show();
						// String child_level = jObject.getString("level");
						// level_list.add(child_level);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					name = new String[name_list.size()];
					name_list.toArray(name);
					id = new String[id_list.size()];
					id_list.toArray(id);
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			super.onPostExecute(result);

			if (jArray == null) {
				Toast.makeText(Student.this, "You have no children mapped !",
						5000).show();
			} else {
				Toast.makeText(Student.this, jArray.toString(), 5000).show();
				
				MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
						Student.this, name, 1);
				list_children.setAdapter(adapter);
				list_children.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Toast.makeText(Student.this, id[arg2], 5000).show();
						Intent i = new Intent(Student.this, Course.class);
						i.putExtra("student_id", id[arg2]);
						startActivity(i);

					}
				});
			}
		}
	}
}
