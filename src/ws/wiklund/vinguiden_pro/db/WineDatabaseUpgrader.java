package ws.wiklund.vinguiden_pro.db;

import ws.wiklund.guides.db.DatabaseUpgrader;
import ws.wiklund.guides.model.BeverageType;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WineDatabaseUpgrader extends DatabaseUpgrader {
	public WineDatabaseUpgrader(SQLiteDatabase db) {
		super(db);
	}

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
			case VERSION_2:
				if(newVersion > VERSION_2) {
					version = moveToVersion3();
					Log.d(WineDatabaseUpgrader.class.getName(), "Upgraded DB from version [" + oldVersion + "] to version [" + version + "]");
					
					if(version < newVersion) {
						return upgrade(version, newVersion);
					} 
					
					return VERSION_3;
				}
				
				break;	
			case VERSION_3:
			case VERSION_4:
			case VERSION_5:
			case VERSION_6:
				return VERSION_7;				
			default:
				break;
		}

		return version;
	}
	
	private int moveToVersion2() throws SQLException {
		insertImageColumnToBeverage();
		
		return VERSION_2;
	}
	
	private int moveToVersion3() {
		db.execSQL("DROP TABLE IF EXISTS " + WineDatabaseHelper.BEVERAGE_TYPE_TABLE);
		
		createAndPopulateBeverageTypeTable(db);
		
		addOtherBeverageType();

		return VERSION_3;
	}

	@Override
	public void createAndPopulateBeverageTypeTable(SQLiteDatabase db) {
		//1. create beverage type table
		db.execSQL(WineDatabaseHelper.DB_CREATE_BEVERAGE_TYPE);
		
		//2. populate beverage type table
		insertBeverageType(1, "Rött vin");
		insertBeverageType(2, "Vitt vin");
		insertBeverageType(3, "Rosévin");
		insertBeverageType(4, "Mousserande vin, Vitt torrt");
		insertBeverageType(5, "Mousserande vin, halvtorrt");
		insertBeverageType(6, "Mousserande vin, Vitt sött");
		insertBeverageType(7, "Mousserande vin, Rosé");
		insertBeverageType(8, "Fruktvin");
		insertBeverageType(9, "Fruktvin, Sött");
		insertBeverageType(10, "Fruktvin, Torrt");
		insertBeverageType(BeverageType.OTHER.getId(), BeverageType.OTHER.getName());
	}

}
