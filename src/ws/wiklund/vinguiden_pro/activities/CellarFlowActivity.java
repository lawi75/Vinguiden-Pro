package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.BaseActivity;
import ws.wiklund.guides.util.CoverFlow;
import ws.wiklund.guides.util.CoverFlowAdapter;
import ws.wiklund.guides.util.GetBeverageFromCursorTask;
import ws.wiklund.guides.util.Notifyable;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import ws.wiklund.vinguiden_pro.util.LayoutHelper;
import ws.wiklund.vinguiden_pro.util.MySelectable;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class CellarFlowActivity extends BaseActivity implements Notifyable {
	private CoverFlowAdapter adapter;
	private WineDatabaseHelper helper;
	private LayoutHelper layoutHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			startActivity(new Intent(getApplicationContext(), CellarListActivity.class));
		} else {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.flow);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
	
			TextView view = (TextView) findViewById(R.id.title);
			view.setText(getString(R.string.cellarTitle));

			helper = new WineDatabaseHelper(this);			
			
			final CoverFlow flow = (CoverFlow) findViewById(R.id.coverFlow);
			adapter = new CoverFlowAdapter(this, helper);
	
			flow.setAdapter(adapter);
	
			flow.setSpacing(-25);
			flow.setSelection(adapter.getOptimalSelection(), true);
			flow.setAnimationDuration(1000);
			
			flow.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					layoutHelper.setCurrentPosition(position);
					layoutHelper.handleLongClick(ViewHelper.getBeverageFromCursor(adapter.getItem(position)));
					return true;
				}
			});
			
			flow.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new GetBeverageFromCursorTask(CellarFlowActivity.this, CellarActivity.class).execute(adapter.getItem(position));
				}
				
			});
			
			layoutHelper = new LayoutHelper(this, adapter, getLayoutInflater());
			
			layoutHelper.addSelectable(new MySelectable(getString(R.string.addToCellar), R.drawable.icon, Selectable.ADD_ACTION));
			layoutHelper.addSelectable(new MySelectable(getString(R.string.removeFromCellar), R.drawable.from_cellar, Selectable.REMOVE_ACTION));
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
		return layoutHelper.onOptionsItemSelected(item.getItemId(), adapter.getCount());
	}
	
	public void addBeverage(View view) {
    	Intent intent = new Intent(view.getContext(), AddWineToCellarListActivity.class);
    	startActivityForResult(intent, 0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		notifyDataSetChanged();
	}

	private boolean hasSomeStats() {
		return adapter.getCount() > 0;
	}
	
	public void notifyDataSetChanged() {
		layoutHelper.setNoBottlesToTitle(helper.getNoBottlesInCellar(), (TextView) findViewById(R.id.title));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		if (adapter != null) {
			adapter.destroy();
		}
		
		super.onDestroy();
	}

}




