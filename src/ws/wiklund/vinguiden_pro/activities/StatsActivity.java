package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.BeverageStatsActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;

public class StatsActivity extends BeverageStatsActivity {

	@Override
	protected BeverageDatabaseHelper getDatabaseHelper() {
		return new WineDatabaseHelper(this);
	}

}
