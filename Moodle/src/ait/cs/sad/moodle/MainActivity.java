package ait.cs.sad.moodle;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText et_username, et_password;
	String token;
	SharedPreferences sharedPref;
	public static final String MyPREFERENCES = "MyPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		String saved_token = readToken();

		if (saved_token != "0") {
			Intent i = new Intent(MainActivity.this, Student.class);
			startActivity(i);
		}
		setContentView(R.layout.activity_main);
		et_username = (EditText) findViewById(R.id.editText_username);
		et_password = (EditText) findViewById(R.id.editText_password);

		final boolean networkConnected = checkConnection();

		Button btn_login = (Button) findViewById(R.id.button_login);
		btn_login.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					String username = et_username.getEditableText().toString();
					String password = et_password.getEditableText().toString();
					if (networkConnected)
						new MyAsyncTask().execute();
					else
						Toast.makeText(MainActivity.this, "Not connected to the network", 5000).show();
	
				}
	
			});
		
		
	}

	private boolean checkConnection() {
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Toast.makeText(MainActivity.this, "TRUE", 5000).show();
			return true;
		}
		Toast.makeText(MainActivity.this, "FALSE", 5000).show();
		return false;
	}

	private void saveToken() {
		Editor editor = sharedPref.edit();
		editor.putString("parent_token", token);
		editor.commit();
		Toast.makeText(
				MainActivity.this,
				"Token " + sharedPref.getString("parent_token", "0")
						+ " saved !", 5000).show();
	}

	private String readToken() {

		return sharedPref.getString("parent_token", "0");
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		JSONArray jArray;
		String url = "http://203.159.6.202/moodle/login/token.php?username=parent1&password=sad2014!Project&service=parent_access";

		// String url = "http://202.28.195.68/api_login.php";

		public MyAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			/*
			 * postParameters.add(new BasicNameValuePair("username", "sujal"));
			 * postParameters.add(new BasicNameValuePair("password", "sad"));
			 */

			postParameters.add(new BasicNameValuePair("username", "parent1"));
			postParameters.add(new BasicNameValuePair("password",
					"sad2014!Project"));
			postParameters.add(new BasicNameValuePair("service",
					"parent_access"));

			try {
				jArray = JSONfunction.getJSONfromURL(url, null);
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			super.onPostExecute(result);
			if (jArray == null) {
				Toast.makeText(MainActivity.this,
						"Could not verify your token !", 5000).show();
			} else {

				try {
					JSONObject jObject = jArray.getJSONObject(0);
					token = jObject.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				saveToken();
				Intent intent = new Intent(MainActivity.this, Student.class);
				startActivity(intent);
			}
		}

	}
}
