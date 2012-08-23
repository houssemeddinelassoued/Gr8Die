package piotrhajduga.android.gr8die;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DiceCursorAdapter extends CursorAdapter {

	public DiceCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Integer from = cursor.getInt(cursor.getColumnIndex(DiceDbAdapter.KEY_FROM));
		Integer to = cursor.getInt(cursor.getColumnIndex(DiceDbAdapter.KEY_TO));
		Integer count = cursor.getInt(cursor.getColumnIndex(DiceDbAdapter.KEY_COUNT));
		Boolean sumup = cursor.getInt(cursor.getColumnIndex(DiceDbAdapter.KEY_SUMUP))>0;
		TextView dice = (TextView) view.findViewById(R.id.dice);
		TextView dice_range = (TextView) view.findViewById(R.id.dice_range);
		String text = count + (sumup?"d":"D") + Integer.valueOf(to-from+1);
		dice.setText(text);
		text = "<" + from + ":" + to + ">";
		dice_range.setText(text);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dice_list_item, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
