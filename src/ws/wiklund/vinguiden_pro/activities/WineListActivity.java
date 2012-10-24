package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.CustomListActivity;
import ws.wiklund.guides.list.BeverageListCursorAdapter;
import ws.wiklund.guides.util.GetBeverageFromCursorTask;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.Sortable;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import ws.wiklund.vinguiden_pro.util.BootStrapHandler;
import ws.wiklund.vinguiden_pro.util.LayoutHelper;
import ws.wiklund.vinguiden_pro.util.MySelectable;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class WineListActivity extends CustomListActivity {
	private static final String PRIVATE_PREF = "vinguiden_pro";

	private LayoutHelper layoutHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BootStrapHandler.init(this, PRIVATE_PREF);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			startActivity(new Intent(getApplicationContext(), WineFlowActivity.class));
		} else {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.list);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

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
			
	        sortableItems.add(new Sortable(
	        		getString(R.string.sortOnCategory), 
	        		getString(R.string.sortOnCategorySub), 
	        		R.drawable.category, 
	        		"category.name"));
			
			layoutHelper.addSelectable(new MySelectable(getString(R.string.addToCellar), R.drawable.icon, Selectable.ADD_ACTION));
			layoutHelper.addSelectable(new MySelectable(getString(R.string.removeFromCellar), R.drawable.from_cellar, Selectable.REMOVE_ACTION));
			layoutHelper.addSelectable(new MySelectable(getString(R.string.deleteTitle), R.drawable.trash, Selectable.DELETE_ACTION));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		new GetBeverageFromCursorTask(this, WineActivity.class).execute((Cursor) getListAdapter().getItem(position));
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		if (cursorAdapter != null) {
			layoutHelper.setNoBottlesToTitle(new WineDatabaseHelper(this).getNoBottlesInCellar(), (TextView) findViewById(R.id.title));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return layoutHelper.onCreateOptionsMenu(menu, getMenuInflater());
	}
		
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menuStats).setEnabled(hasSomeStats());
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return layoutHelper.onOptionsItemSelected(item.getItemId(), getListAdapter().getCount());
	}

	@Override
	public void addBeverage(View view) {
    	Intent intent = new Intent(view.getContext(), AddWineActivity.class);
    	startActivityForResult(intent, 0);
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

		return db.rawQuery(WineDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_INCLUDING_NO_IN_CELLAR + " ORDER BY " + sortColumn, null);
	}

}
