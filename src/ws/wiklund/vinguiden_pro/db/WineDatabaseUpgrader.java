package ws.wiklund.vinguiden_pro.db;

import ws.wiklund.guides.db.DatabaseUpgrader;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WineDatabaseUpgrader extends DatabaseUpgrader {
	public WineDatabaseUpgrader(SQLiteDatabase db) {
		super(db);
	}

	//Available DB versions
	static final int VERSION_1 = 1;
	static final int VERSION_2 = 2;

	public int upgrade(int oldVersion, int newVersion) {
		int version = -1;

		switch (oldVersion) {
			case VERSION_1:
				if(newVersion > VERSION_1) {
					version = moveToVersion2();
					Log.d(WineDatabaseUpgrader.class.getName(), "Upgraded DB from version [" + oldVersion + "] to version [" + version + "]");
					
					if(version < newVersion) {
						return upgrade(version, newVersion);
					} 
					
					return VERSION_2;
				}
				
				break;				
			default:
				break;
		}

		return version;
	}
	
	private int moveToVersion2() throws SQLException {
		insertImageColumnToBeverage();
		
		return VERSION_2;
	}
	

	@Override
	public void createAndPopulateBeverageTypeTable(SQLiteDatabase db) {
		//1. create beverage type table
		db.execSQL(WineDatabaseHelper.DB_CREATE_BEVERAGE_TYPE);
		
		//2. populate beverage type table
		insertBeverageType(1, "R�tt vin");
		insertBeverageType(2, "Vitt vin");
		insertBeverageType(3, "Ros�vin");
		insertBeverageType(4, "Mousserande vin, Vitt torrt");
		insertBeverageType(5, "Mousserande vin, halvtorrt");
		insertBeverageType(6, "Mousserande vin, Vitt s�tt");
		insertBeverageType(7, "Mousserande vin, Ros�");
		insertBeverageType(8, "Fruktvin");
		insertBeverageType(9, "Fruktvin, S�tt");
		insertBeverageType(10, "Fruktvin, Torrt");
	}

}
