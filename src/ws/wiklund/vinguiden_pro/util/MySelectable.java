package ws.wiklund.vinguiden_pro.util;

import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.BaseModel;
import ws.wiklund.guides.model.Cellar;
import ws.wiklund.guides.util.Notifyable;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.vinguiden_pro.R;
import android.content.Context;
import android.widget.Toast;

public class MySelectable extends Selectable {
	public MySelectable(String header, int drawable, int action) {
		super(header, drawable, action);
	}

	@Override
	public void select(final Context context, final BeverageDatabaseHelper helper, final BaseModel model) {
		if(getAction() == Selectable.REMOVE_ACTION) {
			Cellar cellar = helper.getOldestCellarItemForBeverage(model.getId());
			
			if (cellar != null) {
				if (cellar.getNoBottles() == 1) {
					if (!helper.deleteCellar(cellar.getId())) {
						Toast.makeText(context, context.getString(R.string.deleteFailed) + " " + model.getName(), Toast.LENGTH_LONG).show();
					}
					
					((Notifyable)context).notifyDataSetChanged();
				} else {
					cellar.setNoBottles(cellar.getNoBottles() -1);
					helper.addToOrUpdateCellar(cellar);
				}
			}
			
			((Notifyable)context).notifyDataSetChanged();
		} else if(getAction() == Selectable.REMOVE_THIS_ACTION) {
			//Called from CellarActivity with a specific row in the cellar
			if (!helper.deleteCellar(model.getId())) {
				Toast.makeText(context, context.getString(R.string.deleteFailed) + " " + model.getName(), Toast.LENGTH_LONG).show();
			}
			((Notifyable)context).notifyDataSetChanged();
		} else {
			super.select(context, helper, model);
		}
	}

}
