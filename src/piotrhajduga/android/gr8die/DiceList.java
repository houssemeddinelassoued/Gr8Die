package piotrhajduga.android.gr8die;

import java.util.Random;
import java.util.Vector;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

public class DiceList extends ListActivity {
	
	private static final int ADD_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	//private static final int GROUP_ID = Menu.FIRST + 2;
	//private static final int GROUPS_ID = Menu.FIRST + 3;
	
	private DiceDbAdapter mDbHelper;
	private Cursor mCursor;
	
	private TextView rollName;
	private TextView rollResult;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dice_list);
        rollName = (TextView) findViewById(R.id.roll);
        rollResult = (TextView) findViewById(R.id.roll_result);
        if(savedInstanceState != null) {
        	rollName.setText(savedInstanceState.getSerializable("rollName").toString());
        	rollResult.setText(savedInstanceState.getSerializable("rollResult").toString());
        }
        registerForContextMenu(getListView());
		mDbHelper = new DiceDbAdapter(this);
		mDbHelper.open();
        fillData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
    	MenuItem menuItem = menu.add(0, ADD_ID, 0, R.string.menu_add_die);
    	menuItem.setIcon(R.drawable.new_die);
    	//menuItem = menu.add(0, GROUP_ID, 0, R.string.menu_group);
    	//menuItem.setIcon(R.drawable.group);
    	//menuItem = menu.add(0, GROUPS_ID, 0, R.string.menu_groups);
    	//menuItem.setIcon(R.drawable.manage_groups);
    	return result;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	menu.setHeaderTitle(getDiceName(info.position));
    	MenuItem menuItem = menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    	menuItem.setIcon(R.drawable.edit);
    	menuItem = menu.add(0, DELETE_ID, 0, R.string.menu_delete_die);
    	menuItem.setIcon(R.drawable.delete);
    }
    
    private String getDiceName(int position) {
    	mCursor.moveToPosition(position);
    	Integer ifrom = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_FROM));
    	Integer ito = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_TO));
    	Integer icount = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_COUNT));
    	Boolean bsumup = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_SUMUP))>0;
    	String result = icount + (bsumup?"d":"D") + (ito-ifrom+1) +
    			" <" + ifrom + ":" + ito + ">";
    	return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case ADD_ID:
    		addDie();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	mCursor.moveToPosition(position);
    	
    	Integer from = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_FROM));
    	Integer to = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_TO));
    	Integer count = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_COUNT));
    	Boolean sumup = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_SUMUP)) > 0;
    	Boolean hideall = mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_HIDE_ALL)) > 0;
    	
    	rollName.setText(count+(sumup?"d":"D")+Integer.valueOf(to-from+1) +
    			" <" + from + ":" + to + ">");
    	
    	Vector<Integer> results = new Vector<Integer>();
    	Random random = new Random();
    	while (count-->0) {
    		results.add(Integer.valueOf(random.nextInt(to-from+1)+from));
    	}
    	String result = "";
    	Integer sum = 0;
		for(Integer i: results) {
	    	if(!hideall) {
				result += (i<0?"("+i+")":i) + (sumup?" + ":", ");
	    	}
	    	sum += i;
		}
		if(!hideall) result = result.substring(0, result.length()-(sumup?3:2));
		if(sumup) result += hideall?sum:" = "+sum;
    	rollResult.setText(result);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info =
			(AdapterContextMenuInfo) item.getMenuInfo();
    	switch(item.getItemId()) {
    	case DELETE_ID:
    		deleteDie(info.id);
    		fillData();
    		return true;
    	case EDIT_ID:
    		mCursor.moveToPosition(info.position);
    		editDie(mCursor.getInt(mCursor.getColumnIndex(DiceDbAdapter.KEY_DIE_ROWID)));
    		return true;
    	}
    	return super.onContextItemSelected(item);
    }
    
    private void addDie() {
    	Intent i = new Intent(this, DiceEdit.class);
    	startActivity(i);
	}
    
    private void editDie(int id) {
    	Intent i = new Intent(this, DiceEdit.class);
    	i.putExtra(DiceDbAdapter.KEY_DIE_ROWID, id);
    	startActivity(i);
    }
    
    private void deleteDie(long id) {
		mDbHelper.deleteDie(id);
    }
    
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	//super.onActivityResult(requestCode, resultCode, intent);
    	//mDbHelper.open();
    	//fillData();
    //}

	private void fillData() {
		mCursor = mDbHelper.fetchAllDice();
		startManagingCursor(mCursor);
		
		DiceCursorAdapter dice = new DiceCursorAdapter(this, mCursor);
		setListAdapter(dice);
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("rollName", rollName.getText().toString());
		outState.putSerializable("rollResult", rollResult.getText().toString());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}