package ws.wiklund.vinguiden_pro.activities;

import ws.wiklund.guides.activities.BaseActivity;
import ws.wiklund.guides.util.DownloadBeverageTask;
import ws.wiklund.guides.util.DownloadBeveragesTask;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class AddWineActivity extends BaseActivity {
	private WineDatabaseHelper helper;
	private EditText searchStr;
	private EditText searchStrByName;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        
        helper = new WineDatabaseHelper(this);
        searchStr = (EditText)findViewById(R.id.EditNo);
        searchStrByName = (EditText)findViewById(R.id.EditFindByName);
        
        searchStr.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == 0) {
					searchWine(v);
					return true;
				}
				
				return false;
			}
		});
        
        searchStrByName.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == 0) {
					searchWineByName(v);
					return true;
				}
				
				return false;
			}
		});
    }
    
	public void searchWine(View view) {    	
		search(searchStr.getText().toString());
    }
    
	public void searchWineByName(View view) {    	
		searchByName(searchStrByName.getText().toString());
    }

	private void search(String no) {
    	if(ViewHelper.isValidNo(this, helper, no)) {
    		new DownloadBeverageTask(helper, false, this, ModifyWineActivity.class).execute(no);
    	}
    }
	
	private void searchByName(String searchWord) {
		new DownloadBeveragesTask(helper, this, SearchWineActivity.class).execute(searchWord);
	}

	public void addBeverageManually(View view) {    	
		Intent intent = new Intent(this, ModifyWineActivity.class);
    	startActivity(intent);
    }

}
