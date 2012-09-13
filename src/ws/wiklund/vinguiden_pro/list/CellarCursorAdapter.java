package ws.wiklund.vinguiden_pro.list;

import java.util.Date;

import ws.wiklund.guides.R;
import ws.wiklund.guides.bolaget.SystembolagetParser;
import ws.wiklund.guides.model.Category;
import ws.wiklund.guides.util.BitmapManager;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.util.BeverageAndCellarContainer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CellarCursorAdapter extends SimpleCursorAdapter {
	private LayoutInflater inflator;

	public CellarCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.item, c, new String[] {"thumb", "name", "country_id", "year", "consumption_date"}, new int[] {android.R.id.icon, android.R.id.text1});

		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		Cursor c = getCursor();
		
		if (c.moveToPosition(position)) {
			CellarViewHolder holder;
			
			if (convertView == null) {  
				convertView = inflator.inflate(R.layout.cellaritem, null);
				
				TextView titleView = (TextView) convertView.findViewById(R.id.itemTitle);  
		        TextView textView = (TextView) convertView.findViewById(R.id.itemText);  
		        TextView typeView = (TextView) convertView.findViewById(R.id.itemType);  
		        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemImage);

			    TextView added = (TextView) convertView.findViewById(R.id.itemAdded);  
			    TextView consumption = (TextView) convertView.findViewById(R.id.itemConsumption);  
			    TextView location = (TextView) convertView.findViewById(R.id.itemLocation);  
		        
		         
		        holder = new CellarViewHolder();  
		        holder.titleView = titleView;  
		        holder.textView = textView;  
		        holder.imageView = imageView;
		        holder.typeView = typeView;
		        
		        holder.added = added;
		        holder.consumption = consumption;
		        holder.location = location;
		        
		        convertView.setTag(holder);
			} else {
				holder = (CellarViewHolder) convertView.getTag(); 
			}
			
			if (c != null) {
				BeverageAndCellarContainer container = BeverageAndCellarContainer.getBeverageAndCellarContainerFromCursor(c);
				
				int noBottles = container.cellar.getNoBottles(); 
				StringBuilder name = new StringBuilder(container.beverage.getName());
				
				if(noBottles > 0) {
					name.append("(").append(noBottles).append(")");
				}
						
				holder.titleView.setText(name.toString());
				
				String type = container.beverage.getBeverageType().getName();
				
				Category category = container.beverage.getCategory();
				if(category != null && category.getName() != null) {
					type += " (" + category.getName() + ")";
				}
				
				holder.typeView.setText(type);
				
				int year = container.beverage.getYear(); 
				holder.textView.setText(container.beverage.getCountry().getName() + " " + (year != -1 ? year : ""));

				String u = container.beverage.getThumb();			
				if (u != null) {
					String url = u.startsWith("/") ? SystembolagetParser.BASE_URL + u : u;
					holder.imageView.setTag(url);
					BitmapManager.INSTANCE.loadBitmap(url, holder.imageView, 50, 100);
				} else {
					holder.imageView.setImageResource(R.drawable.icon);
				}
				
		        holder.added.setText(ViewHelper.getDateAsString(container.cellar.getAddedToCellar()));
		        
		        Date consumptionDate = container.cellar.getConsumptionDate();
		 
		        if (consumptionDate != null) {
					holder.consumption.setText(ViewHelper.getDateTimeAsString(container.cellar.getConsumptionDate()));
				}
		        
		        ViewHelper.setText(holder.location, container.cellar.getStorageLocation());

				return convertView;
			}			
		}
			
		return convertView;
	}
	
	
	private class CellarViewHolder {
		ImageView imageView;  
	    TextView titleView;  
	    TextView textView; 
	    TextView typeView;

	    TextView added;
	    TextView consumption;
	    TextView location;
	}
	
}
