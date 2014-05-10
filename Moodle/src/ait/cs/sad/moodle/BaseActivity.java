package ait.cs.sad.moodle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	
	public static final String MyPREFERENCES = "MyPrefs";
	protected SharedPreferences sharedPref;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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
			confirmLogout();
			//Toast.makeText(Student.this, "Token deleted", Toast.LENGTH_SHORT).show();
			return true;

//		case R.id.action_synchronise:
//			if (checkConnection()) {
//				try {
//					/*new Synchronizer(Student.this, sharedPref.getString(
//								"parent_token", "0")).execute().get();*/
//					 new Synchronizer(Student.this).execute().get();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					e.printStackTrace();
//				}
//				if (adapter != null) {
//					 name_list.clear();
//					 adapter.notifyDataSetChanged();
//				}
//				loadData();
//			} else {
//				Toast.makeText(Student.this, "No Network Connection", Toast.LENGTH_SHORT).show();
//			}
//			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	protected boolean checkConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void confirmLogout() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				BaseActivity.this);
		alert.setMessage("Log out?");
		alert.setCancelable(true);
				
		alert.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						Editor editor = sharedPref.edit();
						editor.putString("parent_token", "0");
						editor.putString("moodle_server", "");
						editor.commit();
						
						Intent i = new Intent (BaseActivity.this, MainActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						//i.putExtra("EXIT", true);
						startActivity(i);
						
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (data == null) {
	        return;  // back button, resume this activity
	    }

	    // Propagate result down the stack.
	    setResult(0, data);
	    finish();
	}
}
