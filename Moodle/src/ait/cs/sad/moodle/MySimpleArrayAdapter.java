package ait.cs.sad.moodle;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	//private final String[] name;
	private final List<String> name;
	private int check;
	
	/*public MySimpleArrayAdapter(Context context, String[] name, int check) {
		super(context, R.layout.students, name);
		this.context = context;
		this.name = name;
		this.check = check;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView_label);
		TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
		
		if (check == 2) {
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.course));
		}
		textView1.setText(name[position]);
		
		return rowView;
	}*/
	
	public MySimpleArrayAdapter(Context context, List<String> name, int check) {
		super(context, R.layout.students, name);
		this.context = context;
		this.name = name;
		this.check = check;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView_label);
		TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
		
		if (check == 2) {
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.course));
		}
		textView1.setText(name.get(position));
		
		return rowView;
	}
	
	
}
