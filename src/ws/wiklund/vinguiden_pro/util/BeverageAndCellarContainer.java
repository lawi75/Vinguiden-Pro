package ws.wiklund.vinguiden_pro.util;

import java.util.Date;

import android.database.Cursor;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.BeverageType;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.model.Cellar;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.model.Producer;
import ws.wiklund.guides.model.Provider;

public class BeverageAndCellarContainer {
	public Beverage beverage;
	public Cellar cellar;
	
	public BeverageAndCellarContainer(Beverage beverage, Cellar cellar) {
		this.beverage = beverage;
		this.cellar = cellar;
	}
	
	public static BeverageAndCellarContainer getBeverageAndCellarContainerFromCursor(Cursor c) {
		int i = 0;
		Beverage b = new Beverage(
				c.getInt(i++),
				c.getString(i++),
				c.getInt(i++),
				new BeverageType(c.getInt(i++), c.getString(i++)),
				c.getString(i++),
				new Country(c.getInt(i++), c.getString(i++), c.getString(i++)),
				c.getInt(i++),
				new Producer(c.getInt(i++), c.getString(i++)),
				c.getFloat(i++),
				c.getFloat(i++),
				c.getString(i++),
				c.getString(i++),
				new Provider(c.getInt(i++), c.getString(i++)),
				c.getFloat(i++),
				c.getString(i++),
				new Category(c.getInt(i++), c.getString(i++)),
				new Date(c.getLong(i++)),
				0);
		
		Cellar cellar = new Cellar(
				c.getInt(i++),
				c.getInt(i++),
				c.getInt(i++),
				c.getString(i++),
				c.getString(i++),
				new Date(c.getLong(i++)),
				c.isNull(i) ? null: new Date(c.getLong(i++)),
				c.getInt(i++));

		return new BeverageAndCellarContainer(b, cellar);
	}	
	
}
