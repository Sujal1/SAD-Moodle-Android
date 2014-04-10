package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Student extends Activity {

	public static final String MyPREFERENCES = "MyPrefs" ;
	
	SharedPreferences sharedPref;
	ListView list_children;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students);
		
		list_children = (ListView) findViewById(R.id.listView_children);
		
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Toast.makeText(Student.this, sharedPref.getString("parent_token", "0"), 5000).show();
		
		new MyAsyncTask().execute();
	}
	
	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		JSONArray jArray;
		//String url = "http://202.28.195.68/get_children.php";
		String url = "http://203.159.6.202/moodle/webservice/rest/server.php";
		List<String> name_list = new ArrayList<String>();
		List<String> level_list = new ArrayList<String>();
		String [] name;
		
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
				jArray = JSONfunction.getJSONfromURL(url+"?wstoken="+sharedPref.getString("parent_token", "0")+"&wsfunction=local_wstemplate_get_child&moodlewsrestformat=json", null);
				//jArray = JSONfunction.getJSONfromURL(url+"?wstoken=1234567", null);
			} catch (Exception e) {
			
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			
			super.onPostExecute(result);
			
			if (jArray == null) {
				Toast.makeText(Student.this, "You have no children mapped !", 50000).show();
			} else {
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObject;
					try {
						jObject = jArray.getJSONObject(i);
						String child_name = jObject.getString("fullname").toUpperCase();
						name_list.add(child_name);
						//Toast.makeText(Student.this, child_name, 5000).show();
						//String child_level = jObject.getString("level");
						//level_list.add(child_level);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					name = new String[name_list.size()];
		    		name_list.toArray(name);
				}
					MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(Student.this, name);
		    		list_children.setAdapter(adapter);
		    		list_children.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Toast.makeText(Student.this, "Function not yet implemented !", 5000).show();
							
						}
					});
			}
		}
	}
}
