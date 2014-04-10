package ait.cs.sad.moodle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONfunction {

	public static JSONArray getJSONfromURL(String url,
			ArrayList<NameValuePair> postParameters) {
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			if (postParameters != null) {
				httppost.setEntity(new UrlEncodedFormEntity(postParameters));
			}
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {

		}

		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
				
			result = sb.toString();
			
			if (result.startsWith("{")) {
				result = "[" + result+ "]";
			}
			
		} catch (Exception e) {

		}

		try {
			jArray = new JSONArray(result); 
		} catch (JSONException e) {

		}

		return jArray;
	}
}
