package piotrhajduga.android.gr8die;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class DiceEdit extends Activity {
	
	private EditText from;
	private EditText to;
	private EditText count;
	private CheckBox sumup;
	private CheckBox hideall;
	
	private Integer mRowId;
	
	private DiceDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dice_new);
		setTitle(R.string.new_dice);
		
		from = (EditText) findViewById(R.id.new_from);
		to = (EditText) findViewById(R.id.new_to);
		count = (EditText) findViewById(R.id.new_count);
		sumup = (CheckBox) findViewById(R.id.new_sumup);
		hideall = (CheckBox) findViewById(R.id.new_hide_all);
		
		Button done = (Button) findViewById(R.id.new_done);
		mRowId = getIntent().getIntExtra(DiceDbAdapter.KEY_DIE_ROWID, -1);
		
		mDbHelper = new DiceDbAdapter(this);
		mDbHelper.open();
		
		if(savedInstanceState != null) {
			from.setText(savedInstanceState.getSerializable(DiceDbAdapter.KEY_FROM).toString());
			to.setText(savedInstanceState.getSerializable(DiceDbAdapter.KEY_TO).toString());
			count.setText(savedInstanceState.getSerializable(DiceDbAdapter.KEY_COUNT).toString());
			sumup.setChecked(savedInstanceState.getBoolean(DiceDbAdapter.KEY_SUMUP));
			hideall.setVisibility(sumup.isChecked()?View.VISIBLE:View.INVISIBLE);
			hideall.setChecked(savedInstanceState.getBoolean(DiceDbAdapter.KEY_HIDE_ALL));
			done.setText(savedInstanceState.getString("button"));
			mRowId = savedInstanceState.getInt(DiceDbAdapter.KEY_DIE_ROWID);
		}
		else if(mRowId!=-1) {
			populateFields();
			done.setText(R.string.edit_die);
		}

		done.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				try {
					Integer ifrom = Integer.parseInt(from.getText().toString());
					Integer ito = Integer.parseInt(to.getText().toString());
					Integer icount = Integer.parseInt(count.getText().toString());
					if (ito<ifrom) return;
					Boolean bsumup = sumup.isChecked();
					Boolean bhideall = hideall.isChecked();
					if(mRowId<0) mDbHelper.addDie(ifrom, ito, icount, bsumup, bhideall);
					else mDbHelper.updateDie(mRowId, ifrom, ito, icount, bsumup, bhideall);
					setResult(RESULT_OK);
					finish();
				}
				catch (NumberFormatException exc) {
				}
			}
		});
		
		sumup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				hideall.setVisibility(arg1?View.VISIBLE:View.INVISIBLE);
				if(!arg1) hideall.setChecked(false);
			}
		});
	}
	
	private void populateFields() {
		if(mRowId==null) return;
		Cursor c = mDbHelper.fetchDie(mRowId);
		if(c==null) return;
		c.moveToFirst();
		from.setText(Integer.valueOf(c.getInt(c.getColumnIndex(DiceDbAdapter.KEY_FROM))).toString());
		to.setText(Integer.valueOf(c.getInt(c.getColumnIndex(DiceDbAdapter.KEY_TO))).toString());
		count.setText(Integer.valueOf(c.getInt(c.getColumnIndex(DiceDbAdapter.KEY_COUNT))).toString());
		sumup.setChecked(c.getInt(c.getColumnIndex(DiceDbAdapter.KEY_SUMUP))>0);
		hideall.setVisibility(sumup.isChecked()?View.VISIBLE:View.INVISIBLE);
		hideall.setChecked(c.getInt(c.getColumnIndex(DiceDbAdapter.KEY_HIDE_ALL))>0);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Button button = (Button) findViewById(R.id.new_done);
		outState.putString("button", button.getText().toString());
		outState.putInt(DiceDbAdapter.KEY_DIE_ROWID, mRowId);
		outState.putSerializable(DiceDbAdapter.KEY_FROM, from.getText().toString());
		outState.putSerializable(DiceDbAdapter.KEY_TO, to.getText().toString());
		outState.putSerializable(DiceDbAdapter.KEY_COUNT, count.getText().toString());
		outState.putBoolean(DiceDbAdapter.KEY_SUMUP, sumup.isChecked());
		outState.putBoolean(DiceDbAdapter.KEY_HIDE_ALL, hideall.isChecked());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
	}
}
