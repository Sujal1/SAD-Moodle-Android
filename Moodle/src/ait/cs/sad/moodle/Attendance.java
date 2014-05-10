package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Attendance extends BaseActivity {

	private String student_id;
	private String course_id;
	private ListView list_attendance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.attendance);

		list_attendance = (ListView) findViewById(R.id.listView_attendance);


		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		student_id = extras.getString("student_id");
		course_id = String.valueOf(extras.getInt("course_id"));

		new LoadAttendance(1, null).execute();
	}

	public class LoadAttendance extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		private String url = "http://202.28.195.68/attendance.php";
		private JSONArray jArray;
		// private int day_count = 1;
		List<String> absent_days = new ArrayList<String>();
		int test_var = 0; // variable to test for upload and download, 1 for
							// download and 2 for upload

		String comment;  //used only for upload
		
		public LoadAttendance(int test, String comment) {
			test_var = test;
			this.comment = comment;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(Attendance.this);
			progressDialog.show();
			
			if (test_var == 1) {
				progressDialog.setMessage("Fetching Absent Days..");
			} else if(test_var == 2) {
				progressDialog.setMessage("Sending your comment..");
			}
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (test_var == 1) {
				try {
					Log.d("*****", student_id);
					Log.d("*****", course_id);
					jArray = JSONfunction.getJSONfromURL(url + "?student_id="
							+ student_id + "&course_id=" + course_id, null);
				} catch (Exception e) {

				}

				if (jArray != null) {

					try {
						JSONObject jObject = jArray.getJSONObject(0);

						absent_days.add(jObject.getString("day1"));
						absent_days.add(jObject.getString("day2"));

					} catch (Exception e) {

					}

				}
			} else if (test_var == 2) {

				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://202.28.195.68/save.php");
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("comment",
							comment));

					httppost.setEntity(new UrlEncodedFormEntity(postParameters));
					HttpResponse x = httpclient.execute(httppost);
//					Log.d("$$$$$$$$$$$", x.getEntity().toString());
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(Attendance.this, e.toString(),
							Toast.LENGTH_SHORT).show();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			super.onPostExecute(result);

			if (test_var == 1) {
				if (jArray == null) {
					Toast.makeText(Attendance.this,
							"Could not get the attendance", Toast.LENGTH_SHORT)
							.show();
				} else {
					MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
							Attendance.this, absent_days, 3);

					list_attendance.setAdapter(adapter);
				}
			}
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
//		case R.id.action_logout:
//
//			return true;

		case R.id.action_synchronise:

			return true;

		case R.id.action_comment:
			displayCommentBox();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayCommentBox() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				Attendance.this);
		alert.setMessage("Your Comment");
		alert.setCancelable(true);
		final EditText et_comment = new EditText(Attendance.this);
		/*
		 * et_comment.setSingleLine(false); et_comment.setMinLines(3);
		 */

		alert.setView(et_comment);

		alert.setPositiveButton("Submit",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						new LoadAttendance(2, et_comment.getText().toString()).execute();
					}

				});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		alert.show();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem comment_menu = menu.findItem(R.id.action_comment);
		comment_menu.setVisible(true);

		return super.onPrepareOptionsMenu(menu);
	}
}
