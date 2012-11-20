package ws.wiklund.vinguiden_pro.activities;

import java.util.List;

import ws.wiklund.guides.activities.CustomListActivity;
import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.util.DownloadBeverageTask;
import ws.wiklund.guides.util.DownloadBeveragesTask;
import ws.wiklund.guides.util.SearchResult;
import ws.wiklund.guides.util.Selectable;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.guides.util.ViewHolder;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchWineActivity extends CustomListActivity {
	private WineDatabaseHelper helper;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_by_name);
        
        helper = new WineDatabaseHelper(this);
        
        SearchResult result = (SearchResult) getIntent().getSerializableExtra("ws.wiklund.guides.util.SearchResult");
                
        final EditText findByName = (EditText) findViewById(R.id.EditFindByName);
        
        findByName.setText((String)getIntent().getSerializableExtra("ws.wiklund.guides.util.SearchResult.SearchWord"));
        
        setListAdapter(new BeverageAdapter(this, R.layout.search_item, result.getResult()));
        
        findByName.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == 0) {
					searchByName(findByName.getText().toString());
					return true;
				}
				
				return false;
			}
		});
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//This is not a complete beverage object need to get info from Systembolaget
		Beverage b = (Beverage) getListAdapter().getItem(position);
		new DownloadBeverageTask(helper, false, this, ModifyWineActivity.class).execute(String.valueOf(b.getNo()));
	}

	@Override
	protected void select(Selectable selectable, int position) {
	}

	@Override
	protected void addSelectables() {
	}

	@Override
	protected Cursor getNewCursor(String sortColumn) {
		return null;
	}

	@Override
	public void addBeverage(View view) {
	}
	
	private void searchByName(String searchWord) {
		new DownloadBeveragesTask(helper, this, SearchWineActivity.class).execute(searchWord);
	}

	
	private class BeverageAdapter extends ArrayAdapter<Beverage> {
		private LayoutInflater inflator;
		private int textViewResourceId;

		public BeverageAdapter(Context context, int textViewResourceId, List<Beverage> beverages) {
			super(context, textViewResourceId, beverages);
			
			this.textViewResourceId = textViewResourceId;
			inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {	
			Beverage b = getItem(position);
			
			if(b != null) {
				ViewHolder holder;
				
				if (convertView == null) {  
					convertView = inflator.inflate(textViewResourceId, null);
					
					TextView titleView = (TextView) convertView.findViewById(R.id.itemTitle);  
			        TextView typeView = (TextView) convertView.findViewById(R.id.itemType);  
			        TextView textView = (TextView) convertView.findViewById(R.id.itemText);  
			        TextView priceView = (TextView) convertView.findViewById(R.id.itemPrice);  

			         
			        holder = new ViewHolder();  
			        holder.titleView = titleView;  
			        holder.typeView = typeView;
			        holder.textView = textView;  
			        holder.priceView = priceView;
			        
			        convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag(); 
				}
				
				holder.titleView.setText(b.getName());
				
				String type = b.getBeverageType().getName();
				holder.typeView.setText(type);
				
				holder.textView.setText(b.getCountry().getName());
				
				holder.priceView.setText( ViewHelper.formatPrice(b.getPrice()));
			}
			
			return convertView;
		}
		
	}
    
}
