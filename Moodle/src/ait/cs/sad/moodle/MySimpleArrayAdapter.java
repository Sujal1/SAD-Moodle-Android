package ait.cs.sad.moodle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] name;
	
	public MySimpleArrayAdapter(Context context, String[] name) {
		super(context, R.layout.students, name);
		this.context = context;
		this.name = name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		
		TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
		
		textView1.setText(name[position]);
		
		return rowView;
	}
	
	
}
