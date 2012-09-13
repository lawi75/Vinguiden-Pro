package ws.wiklund.vinguiden_pro.util;

import java.util.List;

import ws.wiklund.guides.model.Cellar;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Re-schedule all alarms after a reboot
 * @author wiklar
 *
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WineDatabaseHelper helper = new WineDatabaseHelper(context);
		
		List<Cellar> alarms = helper.getAllActiveAlarms();
		Log.d(BootReceiver.class.getName(), "BootReceiver woke up after reboot, got [" + alarms.size() + "] alarms that might need to be rescheduled");
		
		for(Cellar c : alarms) {
			AlarmHelper.scheduleAlarm(context, helper, c);
		}
	}

}
