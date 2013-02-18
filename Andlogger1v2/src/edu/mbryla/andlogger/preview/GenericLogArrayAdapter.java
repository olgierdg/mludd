package edu.mbryla.andlogger.preview;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.mbryla.andlogger.R;
import edu.mbryla.andlogger.database.models.GenericLog;

/** Array adapter for <code>GenericLog</code> preview
 * 
 * @author mbryla
 * @version 1.0
 */
public class GenericLogArrayAdapter extends ArrayAdapter<GenericLog> {
	private Activity context;
	private List<GenericLog> logs;
	
	public GenericLogArrayAdapter(Activity context, List<GenericLog> logs) {		
		super(context, R.layout.preview_item, logs);
		this.context = context;
		this.logs = logs;
	}
	
	/** Viewholder class for <code>GenericLog</code> item preview
	 * 
	 * @author mbryla
	 * @version 1.0
	 */
	static class ViewHolder {
		public TextView tvGenericLog;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View rowView = convertView;
		
		if(rowView == null) {
			LayoutInflater layoutInflater = context.getLayoutInflater();
			rowView = layoutInflater.inflate(R.layout.preview_item,  null, true);
			
			viewHolder = new ViewHolder();
			viewHolder.tvGenericLog = (TextView) rowView.findViewById(R.id.tvGenericLog);
			
			rowView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) rowView.getTag();
		
		GenericLog gl = logs.get(position);
		viewHolder.tvGenericLog.setText(gl.toString());
		
		return rowView;
	}
	
}
