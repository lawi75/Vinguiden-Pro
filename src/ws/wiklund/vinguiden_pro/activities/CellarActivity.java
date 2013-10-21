package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.CustomListActivity;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import ws.wiklund.vinguiden_pro.list.CellarCursorAdapter;
import ws.wiklund.vinguiden_pro.util.GetBeverageAndCellarFromCursorTask;
import ws.wiklund.vinguiden_pro.util.LayoutHelper;
import ws.wiklund.vinguiden_pro.util.MySelectable;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class CellarActivity extends CustomListActivity {
	private Beverage beverage;
	
	private LayoutHelper layoutHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

		ImageButton btn = (ImageButton) findViewById(R.id.sortList);
		if (btn != null) {
			//Doesn't exist in flow layout
			btn.setVisibility(View.GONE);
		}
		
		beverage = (Beverage) getIntent().getSerializableExtra("ws.wiklund.guides.model.Beverage");

		TextView view = (TextView) findViewById(R.id.title);
		view.setText(beverage.getName());

		// Now create a new list adapter bound to the cursor.
		cursorAdapter = new CellarCursorAdapter(this, getNewCursor(currentSortColumn));

		startManagingCursor(cursorAdapter.getCursor());

		// Bind to our new adapter.
		setListAdapter(cursorAdapter);

		layoutHelper = new LayoutHelper(this, getListAdapter(), getLayoutInflater());
		
		ListView list = getListView();
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				layoutHelper.setCurrentPosition(position);
				layoutHelper.handleLongClick(ViewHelper.getCellarFromCursor((Cursor) getListAdapter().getItem(position)));
				return true;
			}
		});

		layoutHelper.addSelectable(new MySelectable(getString(R.string.removeThisFromCellar), R.drawable.from_cellar, Selectable.REMOVE_THIS_ACTION));
	}
	
	@Override
	public void addBeverage(View view) {
    	Intent intent = new Intent(view.getContext(), AddWineToCellarActivity.class);
		intent.putExtra("ws.wiklund.guides.model.Beverage", beverage);

    	startActivityForResult(intent, 0);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		new GetBeverageAndCellarFromCursorTask(this, AddWineToCellarActivity.class).execute((Cursor) getListAdapter().getItem(position));
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		if (cursorAdapter != null) {
			//TODO what extra info to append?? If any
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
		
		sortColumn = validateSortColumn(sortColumn);
		
		return db.rawQuery(WineDatabaseHelper.SQL_SELECT_BEVERAGES_IN_CELLAR + " ORDER BY " + sortColumn, new String[]{String.valueOf(beverage.getId())});
	}

}

