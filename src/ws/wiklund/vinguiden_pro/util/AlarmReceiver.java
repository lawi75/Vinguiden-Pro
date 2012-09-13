package ws.wiklund.vinguiden_pro.util;

import ws.wiklund.guides.model.Cellar;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.activities.AddWineToCellarActivity;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
		
		Bundle extras=intent.getExtras();
		String title=extras.getString("title");
		String message =extras.getString("message");
		Cellar cellar = (Cellar) extras.getSerializable("cellar");

		NotificationManager manger = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification(R.drawable.icon, context.getString(R.string.app_name), System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.number += 1;
		
		WineDatabaseHelper helper = new WineDatabaseHelper(context);
		Intent target = new Intent(context, AddWineToCellarActivity.class);
		target.putExtra("ws.wiklund.guides.model.Beverage", helper.getBeverage(cellar.getBeverageId()));
		target.putExtra("ws.wiklund.guides.model.Cellar", cellar);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, target, 0);
		
		notification.setLatestEventInfo(context, title, message, pendingIntent);

		// The PendingIntent to launch our activity if the user selects this notification
		manger.notify(0, notification);
		
		helper.removeSchedule(cellar);
	}

}
