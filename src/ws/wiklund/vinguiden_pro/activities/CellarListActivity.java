package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.CustomListActivity;
import ws.wiklund.guides.list.BeverageListCursorAdapter;
import ws.wiklund.guides.util.GetBeverageFromCursorTask;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import ws.wiklund.vinguiden_pro.util.LayoutHelper;
import ws.wiklund.vinguiden_pro.util.MySelectable;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CellarListActivity extends CustomListActivity {
	private LayoutHelper layoutHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			startActivityForResult(new Intent(getApplicationContext(), CellarFlowActivity.class), 0);
		} else {		
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.list);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
	
			TextView view = (TextView) findViewById(R.id.title);
			view.setText(getString(R.string.cellarTitle));
			
			// Now create a new list adapter bound to the cursor.
			cursorAdapter = new BeverageListCursorAdapter(this, getNewCursor(currentSortColumn));

			startManagingCursor(cursorAdapter.getCursor());

			// Bind to our new adapter.
			setListAdapter(cursorAdapter);
	
			layoutHelper = new LayoutHelper(this, getListAdapter(), getLayoutInflater());
			
			ListView list = getListView();
			list.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					layoutHelper.setCurrentPosition(position);
					layoutHelper.handleLongClick(ViewHelper.getBeverageFromCursor((Cursor) getListAdapter().getItem(position)));
					return true;
				}
			});

			layoutHelper.addSelectable(new MySelectable(getString(R.string.addToCellar), R.drawable.icon, Selectable.ADD_ACTION));
			layoutHelper.addSelectable(new MySelectable(getString(R.string.removeFromCellar), R.drawable.from_cellar, Selectable.REMOVE_ACTION));
		}
	}
	
	@Override
	public void addBeverage(View view) {
    	Intent intent = new Intent(view.getContext(), AddWineToCellarListActivity.class);
    	startActivityForResult(intent, 0);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		new GetBeverageFromCursorTask(this, CellarActivity.class).execute((Cursor) getListAdapter().getItem(position));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		if (cursorAdapter != null) {
			layoutHelper.setNoBottlesToTitle(new WineDatabaseHelper(this).getNoBottlesInCellar(), (TextView) findViewById(R.id.title));
		}
	}

	@Override
	protected void select(Selectable selectable, int position) {}

	@Override
	protected void addSelectables() {}
	
	@Override
	protected Cursor getNewCursor(String sortColumn) {
		if(db == null || !db.isOpen()) {
			db = new WineDatabaseHelper(this).getReadableDatabase();
		}
		
		return db.rawQuery(WineDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_IN_CELLAR + " ORDER BY " + sortColumn, null);
	}

}
