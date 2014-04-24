package ait.cs.sad.moodle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class Synchronizer extends AsyncTask<Void, Void, Void> {

	private ProgressDialog progressDialog;
	private String url = "http://203.159.6.202/moodle/webservice/rest/server.php";
	private String token;
	private int temp = 0;
	private JSONArray jArray;
	private Context context;

	public Synchronizer(Context context, String token) {
		this.context = context;
		this.token = token;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.show();
		progressDialog.setMessage("Fetching Data..");
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		List<String> student_id = synchronizeStudents();
		synchronizeCourses(student_id);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progressDialog.dismiss();
		super.onPostExecute(result);

		/*
		 * if (network == false) { Toast.makeText(context,
		 * "Not connected to the network", 5000) .show(); } else if (jArray ==
		 * null) { Toast.makeText(context, "You have no children mapped.", 5000)
		 * .show(); } else { Intent i = new Intent(context, Student.class);
		 * startActivity(i); // loadData();
		 * 
		 * }
		 */
	}

	private void synchronizeCourses(List<String> student_ids) {

		for (String student_id : student_ids) {
			String url = "http://203.159.6.202/moodle/webservice/rest/server.php?wstoken="
					+ token
					+ "&wsfunction=local_wstemplate_get_course&moodlewsrestformat=json&userid="
					+ student_id;

			try {
				jArray = JSONfunction.getJSONfromURL(url, null);

			} catch (Exception e) {

			}

			if (jArray != null) {
				SQLiteDatabase db = context.openOrCreateDatabase("MyDatabase",
						Context.MODE_PRIVATE, null);
				db.execSQL("CREATE TABLE IF NOT EXISTS Course (courseID VARCHAR, studentID VARCHAR, Name VARCHAR);");

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObject;
					try {
						jObject = jArray.getJSONObject(i);

						String course_id = jObject.getString("id");
						String course_name = jObject.getString("fullname");
						String selectStatement = "SELECT * FROM Course WHERE studentID = '"
								+ student_id + "' AND courseID = '" + course_id + "';";
						Cursor c = db.rawQuery(selectStatement, null);
						if (c.getCount() == 0) {
							// INSERT
							String insertStatement = "INSERT INTO Course (studentID, courseID, Name)"
									+ " VALUES('" + student_id + "','" + course_id + "', ' " + course_name
									+ "');";
							db.execSQL(insertStatement);
						} else {

						}
						c.close();
					} catch (Exception e) {

					}
				}
				db.close();
			} else {
				// NULL ARRAY
				Log.d("#############", "NULL JARAY");
			}
		}
	}

	private List<String> synchronizeStudents() {
		List<String> student_id = new ArrayList<String>();
		try {

			jArray = JSONfunction
					.getJSONfromURL(
							url
									+ "?wstoken="
									+ token
									+ "&wsfunction=local_wstemplate_get_child&moodlewsrestformat=json",
							null);

		} catch (Exception e) {
			// Log.d("!!!!!!!!!!!!!!", "NO NETWORK");
		}

		if (jArray != null) {
			SQLiteDatabase db = context.openOrCreateDatabase("MyDatabase",
					Context.MODE_PRIVATE, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS Student (studentID VARCHAR, Name VARCHAR);");
			String sql = "SELECT * FROM Student;";
			Cursor c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				temp = 1;
			}
			c.close();
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObject;
				try {
					jObject = jArray.getJSONObject(i);
					String name = jObject.getString("fullname").toUpperCase(
							Locale.ENGLISH);
					String id = jObject.getString("id");
					student_id.add(id);
					Log.d("^^^^^^^^", name);
					Log.d("********", id);
					/*** SAVE INTO SQLITE ***/
					if (temp == 1) {
						String sql1 = "INSERT INTO Student (studentID, Name)"
								+ " VALUES('" + id + "','" + name + "');";
						db.execSQL(sql1);
					} else {
						String sql2 = "SELECT * FROM Student WHERE studentID='"
								+ id + "'";
						Cursor c2 = db.rawQuery(sql2, null);
						if (c2.getCount() == 0) {
							String sql3 = "INSERT INTO Student (studentID, Name)"
									+ " VALUES('" + id + "','" + name + "');";
							db.execSQL(sql3);
						}
						c2.close();
					}
					/*** SAVE FINISH **/

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			db.close();
		} else {
			// NULL ARRAY
			Log.d("#############", "NULL JARAY");
		}
		/*
		 * } else { network = false; }
		 */
		return student_id;
	}

}
