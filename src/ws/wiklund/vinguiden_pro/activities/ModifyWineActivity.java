package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.ModifyBeverageActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;

public class ModifyWineActivity extends ModifyBeverageActivity {

	@Override
	protected BeverageDatabaseHelper getDatabaseHelper() {
		return new WineDatabaseHelper(this);
	}
	
	@Override
	protected Class<?> getIntentClass() {
		return WineListActivity.class;
	}
	
}