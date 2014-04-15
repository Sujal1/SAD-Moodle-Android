package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ait.cs.sad.moodle.Student.MyAsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class Course extends Activity {

	String student_id;
	ListView list_course;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		
		list_course = (ListView) findViewById(R.id.listView_course);
		student_id = getIntent().getExtras().getString("student_id");

		new MyAsyncTask().execute();
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		JSONArray jArray;
		String url = "http://203.159.6.202/moodle/course_temp.php?student_id="
				+ student_id;
		List<String> coursename_list = new ArrayList<String>();
		List<String> courseid_list = new ArrayList<String>();
		String[] name, id;
		int x =  1;
		
		public MyAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(Course.this);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				jArray = JSONfunction.getJSONfromURL(url, null);
				
			} catch (Exception e) {

			}
			
			if (jArray != null) {
				JSONObject jObject;
				try {
					jObject = jArray.getJSONObject(0);
					for (int i = 0; i < jObject.length(); i++) {
						coursename_list.add(jObject.getString("course"+x));
						x++;
					}
					//courseid_list.add(jObject.getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				name = new String[coursename_list.size()];
				coursename_list.toArray(name);
				/*id = new String[courseid_list.size()];
				courseid_list.toArray(id);*/
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			super.onPostExecute(result);

			if (jArray == null) {
				Toast.makeText(Course.this, "No courses enrolled !", 50000)
						.show();
				Course.this.finish();
			} else {
				MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
						Course.this, name, 2);
				list_course.setAdapter(adapter);		
			}
		}
	}
}
