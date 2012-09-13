package ws.wiklund.vinguiden_pro.util;

import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.util.CoverFlowAdapter;
import android.content.Context;
import android.database.Cursor;

public class CellarCoverFlowAdapter extends CoverFlowAdapter {
	/*public View getView(int position, View convertView, ViewGroup parent) {
		return ViewHelper.getView(inflator, R.layout.coveritem, convertView, getItem(position), types);
	}*/

	public CellarCoverFlowAdapter(Context c, BeverageDatabaseHelper helper) {
		super(c, helper);
	}

	@Override
	protected Cursor getNewOrReuseCursor() {
		if (db == null || !db.isOpen()) {
			db = helper.getReadableDatabase();
			cursor = db.rawQuery(BeverageDatabaseHelper.SQL_SELECT_ALL_BEVERAGES_IN_CELLAR, null);
		}
		
		return cursor;
	}

}
