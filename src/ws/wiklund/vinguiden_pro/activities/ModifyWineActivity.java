package ws.wiklund.vinguiden_pro.activities;

import java.util.ArrayList;
import java.util.List;

import ws.wiklund.guides.activities.ModifyBeverageActivity;
import ws.wiklund.guides.db.BeverageDatabaseHelper;
import ws.wiklund.guides.model.Category;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ModifyWineActivity extends ModifyBeverageActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		populateCategorySpinner();
	}

	@Override
	protected BeverageDatabaseHelper getDatabaseHelper() {
		return new WineDatabaseHelper(this);
	}
	
	@Override
	protected Class<?> getIntentClass() {
		return WineListActivity.class;
	}
	
	private void populateCategorySpinner() {
		List<Category> categories = new ArrayList<Category>();
		categories.add(new Category(Category.EMPTY_ID, null));
		categories.add(new Category(Category.NEW_ID, getString(R.string.newCategory)));
		categories.addAll(getDatabaseHelper().getCategories());
		
		final Spinner category = (Spinner) findViewById(R.id.Spinner_category);
		final ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		category.setAdapter(categoryAdapter);
		
		if (beverage != null) {
			Category c = beverage.getCategory();
			
			if (c != null) {
				category.setSelection(categoryAdapter.getPosition(c));
			}
		}
		
		category.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Category selectedCategory = (Category) category.getItemAtPosition(position);
		    	if(selectedCategory.getId() == Category.NEW_ID) {
		    		AlertDialog.Builder alert = new AlertDialog.Builder(ModifyWineActivity.this);                 
		    	    alert.setIcon(R.drawable.category);
		    		alert.setTitle(getString(R.string.createCategoryTitle));  
		    	    alert.setMessage(getString(R.string.createCategoryMessage));                

		    	    // Set an EditText view to get user input   
		    	    final EditText input = new EditText(ModifyWineActivity.this); 
		    	    alert.setView(input);

		    	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
		    	    	public void onClick(DialogInterface dialog, int whichButton) {  
		    	    		String name = input.getText().toString();
		    	    		Category newCat = new Category(name);
		    	    		categoryAdapter.add(newCat);
		    	    		category.setSelection(categoryAdapter.getPosition(newCat));
		    	    	}  
		    	    });  

		    	    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    	    	public void onClick(DialogInterface dialog, int which) {
		    	        }
		    	    });
		    	    
	                alert.show();		    		
		    	} 
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }
		});		
		
	}

}