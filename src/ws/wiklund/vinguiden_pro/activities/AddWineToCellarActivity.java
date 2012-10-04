package ws.wiklund.vinguiden_pro.activities;

import java.util.Calendar;

import ws.wiklund.guides.model.Beverage;
import ws.wiklund.guides.model.Cellar;
import ws.wiklund.guides.model.Country;
import ws.wiklund.guides.util.DatePickerFragment;
import ws.wiklund.guides.util.TimePickerFragment;
import ws.wiklund.guides.util.ViewHelper;
import ws.wiklund.vinguiden_pro.R;
import ws.wiklund.vinguiden_pro.db.WineDatabaseHelper;
import ws.wiklund.vinguiden_pro.util.AlarmHelper;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import de.viktorreiser.toolbox.widget.NumberPicker;

public class AddWineToCellarActivity extends FragmentActivity {
	private Beverage beverage;
	private Cellar cellar;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_wine_to_cellar);
        
        beverage = (Beverage) getIntent().getSerializableExtra("ws.wiklund.guides.model.Beverage");
		Log.d(AddWineToCellarActivity.class.getName(), "Beverage: " + beverage);

		cellar = (Cellar) getIntent().getSerializableExtra("ws.wiklund.guides.model.Cellar");
		Log.d(AddWineToCellarActivity.class.getName(), "Cellar: " + cellar);

		if(cellar == null) {
			cellar = new Cellar(beverage.getId());
		}
		
		populateUI();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.modify_beverage_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menuSaveBeverage:
				updateCellarFromUI();
				
				if (cellar.getNoBottles() > 0) {
					WineDatabaseHelper helper = new WineDatabaseHelper(this);
					cellar = helper.addToOrUpdateCellar(cellar);
					AlarmHelper.scheduleAlarm(this, helper, cellar);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.mustAddBottle), Toast.LENGTH_SHORT).show();  		
				}
			
				return true;
			case R.id.menuCancel:
				finish();
				return true;
		}
		
		return false;
	}	
	
	public void setReminder(View view) {
		final Calendar cal = Calendar.getInstance();
		DialogFragment datePicker = new DatePickerFragment() {
			public void onDateSet(DatePicker view, int year, int month, int day) {
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
				
				DialogFragment timePicker = new TimePickerFragment() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
						cal.set(Calendar.MINUTE, minute);
						
						cellar.setConsumptionDate(cal.getTime());
						
						TextView reminder = (TextView) findViewById(R.id.Text_reminder);
						reminder.setText(ViewHelper.getDateTimeAsString(cal.getTime()));
					}
					
				};
				
			    timePicker.show(getSupportFragmentManager(), "timePicker");
			}
		};
		
	    datePicker.show(getSupportFragmentManager(), "datePicker");
	}
	
    private void populateUI() {
		setTitle(beverage.getName());
		
		ViewHelper.setThumbFromUrl((ImageView) findViewById(R.id.Image_thumbUrl), beverage.getThumb());
		
		if (beverage.getNo() != -1) {
			TextView no = (TextView) findViewById(R.id.Text_no);
			no.setText(String.valueOf(beverage.getNo()));
		}
		
		TextView type = (TextView) findViewById(R.id.Text_type);
		
		ViewHelper.setText(type, beverage.getBeverageType().getName());

		Country c = beverage.getCountry();
		if (c != null) {
			ViewHelper.setCountryThumbFromUrl((ImageView) findViewById(R.id.Image_country_thumbUrl), c);
			TextView country = (TextView) findViewById(R.id.Text_country);
			ViewHelper.setText(country, c.getName());
		}

		if (beverage.getYear() != -1) {
			TextView year = (TextView) findViewById(R.id.Text_year);
			ViewHelper.setText(year, String.valueOf(beverage.getYear()));
		}
		
		if (beverage.getStrength() != -1) {
			TextView strength = (TextView) findViewById(R.id.Text_strength);
			ViewHelper.setText(strength, String.valueOf(beverage.getStrength()) + " %");
		}
		
		if (beverage.hasPrice()) {
			TextView label = (TextView) findViewById(R.id.label_price);
			label.setVisibility(View.VISIBLE);

			TextView price = (TextView) findViewById(R.id.Text_price);
			ViewHelper.setText(price, ViewHelper.formatPrice(beverage.getPrice()));
		} else {
			TextView price = (TextView) findViewById(R.id.label_price);
			price.setVisibility(View.GONE);
		}
		
		NumberPicker picker = (NumberPicker) findViewById(R.id.numberPicker);
		picker.setCurrent(cellar.getNoBottles());
		
		EditText comment = ((EditText) findViewById(R.id.Edit_comment));
		ViewHelper.setText(comment, cellar.getComment());

		EditText storage = ((EditText) findViewById(R.id.Edit_storage));
		ViewHelper.setText(storage, cellar.getStorageLocation());

		if (cellar.getConsumptionDate() != null) {
			TextView reminder = (TextView) findViewById(R.id.Text_reminder);
			reminder.setText(ViewHelper.getDateTimeAsString(cellar.getConsumptionDate()));
		}
	}
    
	private void updateCellarFromUI() {
		NumberPicker picker = (NumberPicker) findViewById(R.id.numberPicker);
		cellar.setNoBottles(picker.getCurrent());
		cellar.setComment(((EditText) findViewById(R.id.Edit_comment)).getText().toString());
		cellar.setStorageLocation(((EditText) findViewById(R.id.Edit_storage)).getText().toString());
	}
    
}
