package ws.wiklund.vinguiden_pro.util;

import ws.wiklund.guides.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

public class GetBeverageAndCellarFromCursorTask extends AsyncTask<Cursor, Void, BeverageAndCellarContainer> {
	private ProgressDialog dialog;
	private Activity activity;
	
	@SuppressWarnings("rawtypes")
	private Class activityClass;

	@SuppressWarnings("rawtypes")
	public GetBeverageAndCellarFromCursorTask(Activity activity, Class activityClass) {
		this.activity = activity;
		this.activityClass = activityClass;
	}

	@Override
	protected BeverageAndCellarContainer doInBackground(Cursor... cursors) {
		return BeverageAndCellarContainer.getBeverageAndCellarContainerFromCursor(cursors[0]);
	}

	@Override
	protected void onPostExecute(BeverageAndCellarContainer bevarageAndCellar) {
		dialog.dismiss();

		Intent intent = new Intent(activity, activityClass);
		intent.putExtra("ws.wiklund.guides.model.Beverage", bevarageAndCellar.beverage);
		intent.putExtra("ws.wiklund.guides.model.Cellar", bevarageAndCellar.cellar);

		activity.startActivityForResult(intent, 0);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity);
		dialog.setMessage(activity.getString(R.string.wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

}
