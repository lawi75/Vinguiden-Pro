package ws.wiklund.vinguiden_pro.util;

import java.util.Date;

import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.Cellar;
import ws.wiklund.vinguiden_pro.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmHelper {
	public static final int REQUEST_CODE = 535646;
	
	public static void scheduleAlarm(Context context, BeverageDatabaseHelper helper, Cellar cellar) {
		Date consumptionDate = cellar.getConsumptionDate();
		
		Log.i(AlarmHelper.class.getName(), "Got alarm with date [" + consumptionDate + "] from cellar with id [" + cellar.getId() + "]");
		
		
		if(consumptionDate != null && consumptionDate.after(new Date())) {
			Beverage beverage = helper.getBeverage(cellar.getBeverageId());
			
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("title", beverage.getName());
			intent.putExtra("message", String.format(context.getString(R.string.alarm_message), new Object[]{cellar.getNoBottles(), beverage.getName()}));
			intent.putExtra("cellar", cellar);
			
			PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			 
			// Get the AlarmManager service
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Log.i(AlarmHelper.class.getName(), "Schedule new alarm for [" + consumptionDate + "]");
			
			
			am.set(AlarmManager.RTC_WAKEUP, consumptionDate.getTime(), sender);	
		} else if(consumptionDate != null) {
			Log.i(AlarmHelper.class.getName(), "Got old alarm [" + consumptionDate + "] fom cellar with id [" + cellar.getId() + "], removing it");
			helper.removeSchedule(cellar);
		}
	}
	
}
