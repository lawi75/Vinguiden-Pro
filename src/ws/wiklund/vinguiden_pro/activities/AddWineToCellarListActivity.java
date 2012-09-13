package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.CustomListActivity;
import ws.wiklund.guides.list.BeverageListCursorAdapter;
import ws.wiklund.guides.util.GetBeverageFromCursorTask;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class AddWineToCellarListActivity extends CustomListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list);

		// Now create a new list adapter bound to the cursor.
		cursorAdapter = new BeverageListCursorAdapter(this, getNewCursor(currentSortColumn));

		startManagingCursor(cursorAdapter.getCursor());

		// Bind to our new adapter.
		setListAdapter(cursorAdapter);	
	}
	
	@Override
	public void addBeverage(View view) {
    	Intent intent = new Intent(view.getContext(), AddWineToCellarListActivity.class);
    	startActivityForResult(intent, 0);
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		new GetBeverageFromCursorTask(this, AddWineToCellarActivity.class).execute((Cursor) getListAdapter().getItem(position));
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
