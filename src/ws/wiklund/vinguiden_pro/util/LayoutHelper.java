package ws.wiklund.vinguiden_pro.util;

import java.io.File;

import ws.wiklund.guides.model.BaseModel;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.ExportDatabaseCSVTask;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.SelectableAdapter;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.activities.AboutActivity;
import ws.wiklund.vinguiden_pro.activities.CellarListActivity;
import ws.wiklund.vinguiden_pro.activities.StatsActivity;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LayoutHelper {
	private SelectableAdapter selectableAdapter;
	private Activity activity;
	
	private int currentPosition;

	public LayoutHelper(Activity activity, final ListAdapter adapter, LayoutInflater inflater) {
		this.activity = activity;
		
		selectableAdapter = new SelectableAdapter(activity, R.layout.spinner_row, inflater){
			public boolean isAvailableInCellar() {
				final Beverage b = ViewHelper.getBeverageFromCursor((Cursor) adapter.getItem(currentPosition));
				return b.hasBottlesInCellar();
			}
		};
	}

	public void addSelectable(Selectable selectable) {
		selectableAdapter.add(selectable);
	}
	
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public boolean onOptionsItemSelected(int id, final int numberOfRecords) {
		switch (id) {
			case ViewHelper.GO_TO_CELLAR:
				activity.startActivityForResult(new Intent(activity, CellarListActivity.class), 0);
				break;
			case R.id.menuStats:
				activity.startActivityForResult(new Intent(activity, StatsActivity.class), 0);
				break;
			case R.id.menuExport:
				final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
				alertDialog.setTitle(activity.getString(R.string.export));
				
				final File exportFile = new File(ViewHelper.getRoot(), "export_guide.csv");
				alertDialog.setMessage(String.format(activity.getString(R.string.export_message), new Object[]{exportFile.getAbsolutePath()}));
				
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
				       new ExportDatabaseCSVTask(activity, new WineDatabaseHelper(activity), exportFile, numberOfRecords).execute();
					} 
				});
				
				alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(android.R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.cancel();
					} 
				});
	
				alertDialog.setCancelable(true);
				alertDialog.setIcon(R.drawable.export);
				alertDialog.show();
				break;
			case R.id.menuAbout:
				activity.startActivityForResult(new Intent(activity, AboutActivity.class), 0);
				break;
		}
		
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem m = menu.add(Menu.NONE, ViewHelper.GO_TO_CELLAR, Menu.NONE, R.string.goToCellar);
		m.setIcon(R.drawable.stairs);

		inflater.inflate(R.menu.beverage_list_menu, menu);

		return true;
	}
	
	public void handleLongClick(final BaseModel model) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle(model != null ? model.getName() : "");
		
		alertDialog.setSingleChoiceItems( selectableAdapter, 0, new OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss();
                ((MySelectable) selectableAdapter.getItem(which)).select(activity, new WineDatabaseHelper(activity), model);
            }
		}); 

		alertDialog.show(); 				
	}

	public void setNoBottlesToTitle(int bottles, TextView view) {
		// Update title with no wines in cellar
		if (bottles > 0) {
			String text = view.getText().toString();
			if (text.contains("(")) {
				text = text.substring(0, text.indexOf("(") - 1);
			}

			view.setText(text + " (" + bottles + ")");
		}
	}	

}
