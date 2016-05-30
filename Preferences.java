package co.zew.browser.offline;

import android.R;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.Toast;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private String list_appearance;

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (!preferences.getString("layout" , "1").equals(list_appearance)) {
			Intent intent = new Intent();
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			setResult(RESULT_FIRST_USER, intent);
		} else if (key.equals("dark_mode")) {
			Intent intent2 = new Intent();
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			setResult(RESULT_FIRST_USER, intent2);
		} else {
			Intent intent3 = new Intent();
			intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			setResult(RESULT_OK, intent3);}
		disableEnablePreferences(); //javad offline location
	}
//
//	@Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            try {
//			//	finish();
//
//            }
//            catch (Exception ex) {
//                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
//               // finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setSubtitle("Preferences");

       addPreferencesFromResource(co.zew.deebrowser.R.xml.preferences_offlinesave);
		
		list_appearance = getPreferenceScreen().getSharedPreferences().getString("layout" , "1");
		disableEnablePreferences(); //javad change for file location
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
	
	private void disableEnablePreferences () {
		//this can probably be done through Preference dependencies, too lazy to figure out how..
//		boolean useCustomStorageDirEnabled = getPreferenceScreen().getSharedPreferences().getBoolean("is_custom_storage_dir", true);
//		if (useCustomStorageDirEnabled) {
//			getPreferenceScreen().findPreference("custom_storage_dir").setEnabled(true);
//		} else {
//			getPreferenceScreen().findPreference("custom_storage_dir").setEnabled(false);
//		}
	}

}
