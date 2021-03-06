package ait.cs.sad.moodle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {

	EditText et_username;
	EditText et_password;
	EditText et_server;
	private String token;
	private String username;
	private String password;
	private String moodle_server;
//	SharedPreferences sharedPref;
//	public static final String MyPREFERENCES = "MyPrefs";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		
//		if (getIntent().getBooleanExtra("EXIT", false)) {
//			Log.d("!!!!!!!!!!!!!!", "1");
//			this.finish();
//		}
		
		super.onCreate(savedInstanceState);
		
		
		
		//sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		String saved_token = readToken();
	
		if (!saved_token.equals("0")) {
			Intent i = new Intent(MainActivity.this, Student.class);
			startActivity(i);
		} else {
			setContentView(R.layout.activity_main);
			ActionBar actionBar = getActionBar();
			actionBar.hide();
			
			et_username = (EditText) findViewById(R.id.editText_username);
			et_password = (EditText) findViewById(R.id.editText_password);
			et_server = (EditText) findViewById(R.id.editText_server);
			
			et_password.setText("");
			Button btn_login = (Button) findViewById(R.id.button_login);
			btn_login.setOnClickListener(MainActivity.this); /* Modified the way button click is implemented to give simpler view to code */
			
		}

	}

	/* Action for button login */
	
	@Override
	public void onClick(View view) {
		
		/*Action if the login button is pressed*/
		
		if (view.getId() == R.id.button_login) {
			username = et_username.getEditableText().toString();
			password = et_password.getEditableText().toString();
			moodle_server = et_server.getEditableText().toString();
			if (username.equals("") || password.equals("") || moodle_server.equals("")) {
				/*Toast.makeText(MainActivity.this,
						"Missing fields.", Toast.LENGTH_SHORT).show();*/
			}
			else if (checkConnection()) {
				moodle_server = "203.159.6.202";
				username = "parent1";
				password = "sad2014!Project";
				new MyAsyncTask().execute();
			}
			else {
				Toast.makeText(MainActivity.this,
						"No connnection", Toast.LENGTH_LONG).show();
			}
		}
	}
	
//	protected boolean checkConnection() {
//
//		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo netInfo = cm.getActiveNetworkInfo();
//		/*if (netInfo != null && netInfo.isConnectedOrConnecting()) {*/
//		if (netInfo != null) {
//			return true;	//connected to the network
//		}
//		Toast.makeText(MainActivity.this, "No Network Connection", Toast.LENGTH_LONG).show();
//		return false; 	//no network connection
//	}

	/* Save user token and the server in local storage */
	private void saveToken() {
		Editor editor = sharedPref.edit();
		editor.putString("parent_token", token);
		editor.putString("moodle_server", moodle_server);
		editor.commit();
		Toast.makeText(
				MainActivity.this,
				"Token " + sharedPref.getString("parent_token", "0")
						+ " saved !", Toast.LENGTH_SHORT).show();
	}

	private String readToken() {
		return sharedPref.getString("parent_token", "0");
	}

	
	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		JSONArray jArray;
		/*String url = "http://203.159.6.202/moodle/login/token.php?username=parent1&password=sad2014!Project&service=parent_access";*/
		String url = "http://" + moodle_server + "/moodle/login/token.php?username=" + username + "&password=" + password + "&service=parent_access";


		public MyAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Logging in..");
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			//ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			
			/*
			 * postParameters.add(new BasicNameValuePair("username", "sujal"));
			 * postParameters.add(new BasicNameValuePair("password", "sad"));
			 */

			/*postParameters.add(new BasicNameValuePair("username", "parent1"));
			postParameters.add(new BasicNameValuePair("password",
					"sad2014!Project"));
			postParameters.add(new BasicNameValuePair("service",
					"parent_access"));
			 */
			
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
						"Could not verify your token !", Toast.LENGTH_LONG).show();
			} else {

				try {
					JSONObject jObject = jArray.getJSONObject(0);
					token = jObject.getString("token");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				saveToken();
				
				startService(new Intent(MainActivity.this, MyService.class));
				
				Intent intent = new Intent(MainActivity.this, Student.class);
				startActivity(intent);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		et_password.setText("");
	}
}
