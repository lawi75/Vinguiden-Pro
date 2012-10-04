package ws.wiklund.vinguiden_pro.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ws.wiklund.guides.util.BasicBootStrapHandler;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.activities.WineListActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class BootStrapHandler  {
	public static void init(Context context, String preferenceKey) {
		BasicBootStrapHandler.init(context, preferenceKey);
		
		if (runImportDatabaseOnce(context)) {
			importData(context);
		}	
	}

	private static boolean runImportDatabaseOnce(Context context) {
		return BasicBootStrapHandler.runOnce(context, "import_database");
	}

	private static void importData(final Context context) {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);	
		
		for(ResolveInfo info : pkgAppsList) {
			if(info.activityInfo.packageName.equals("ws.wiklund.vinguiden")) {
				final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.setTitle(context.getString(R.string.importTitle));
				alertDialog.setMessage(context.getString(R.string.importMessage));
				
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.importTitle), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							exportOldDatabaseToSD(context);
							replaceDatabase();
							removeOldDatabaseFromSD();
						} catch (Throwable t) {
							Log.e(WineListActivity.class.getName(), "Failed to start activity for export/import", t);
							Toast.makeText(context, context.getString(R.string.failedToExportTryUpdate), Toast.LENGTH_SHORT).show();  		
						}
					} 
				});
				
				alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.empty), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.cancel();
					} 
				});

				alertDialog.setCancelable(true);
				alertDialog.setIcon(R.drawable.export);
				alertDialog.show();
				break;
			}
		}		
	}
	
	private static void exportOldDatabaseToSD(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("ws.wiklund.vinguiden", "ws.wiklund.vinguiden.activities.ExportDatabaseActivity"); 
		context.startActivity(intent);
	}
	
	private static void replaceDatabase() throws IOException {
    	File src = new File(Environment.getExternalStorageDirectory(), "export.db");
		File dest = new File(Environment.getDataDirectory() + "/data/ws.wiklund.vinguiden_pro/databases/wineguide_pro.db");
		
		ViewHelper.copyFile(src, dest);
	}

	private static void removeOldDatabaseFromSD() {
    	File src = new File(Environment.getExternalStorageDirectory(), "export.db");
    	src.delete();
	}

}
