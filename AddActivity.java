package co.zew.browser.offline;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.zew.deebrowser.R;

public class AddActivity extends Activity {
	private Button btn_save;
	private EditText edit_origurl;

	private String origurl ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Enter URL to save");

		PreferenceManager.setDefaultValues(this, R.xml.preferences_offlinesave, false);
		setContentView(R.layout.add_activity);
/////////////////////////

		Intent iin = getIntent();
		try {


			Bundle b = iin.getExtras();


			if (b != null) {
				// Toast.makeText(this, arry, Toast.LENGTH_SHORT);
				edit_origurl = (EditText)findViewById(R.id.frst_editTxt);
				String s =   (String) b.get("Intent.EXTRA_TEXT");
				edit_origurl.setText(s);


				try {
//					Toast.makeText(this, "Wait... ", Toast.LENGTH_SHORT)
//							.show();

				} catch (Exception ex) {
					//  Toast.makeText(context, "error: " + ex.toString(), Toast.LENGTH_SHORT);
				}
				// myaddrees.replaceAll("wwww.", "");

			}

		} catch (Exception ex) {
			// Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
			//hidTextr = "Nothing-to-show!";
		}









		/////////////////////////////
		btn_save = (Button)findViewById(R.id.save_btn);
		btn_save.setEnabled(false);
		edit_origurl = (EditText)findViewById(R.id.frst_editTxt);
try {
	edit_origurl.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));

}
catch (Exception ex){}

		//save directly if activity was started via intent
		origurl = edit_origurl.getText().toString().trim();
		if (origurl.length() > 0) {
			Toast.makeText(this, "Saving in Zew! ", Toast.LENGTH_SHORT)
					.show();
			startSave("diag");
		}

		edit_origurl.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable text) {
				if (edit_origurl.length() == 0) {
					btn_save.setEnabled(false);
				} else {
					btn_save.setEnabled(true);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
	}

	public void cancelButtonClick(View view) {
		finish();
	}

	public void btn_paste(View view) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		edit_origurl.setText(clipboard.getText());
	}

	// saveButton click event
	public void okButtonClick(View view) {
		origurl = edit_origurl.getText().toString().trim();
		if (origurl.length() > 0 && (origurl.startsWith("http"))) {
			startSave("diag");
		} else if (origurl.length() > 0) {
			origurl = "http://" + origurl;
			startSave("diag");
		}
	}


	private void startSave(String s) {
		Intent intent = new Intent(this, SaveService.class);
		if (s!="diag")
		{
			intent.putExtra(Intent.EXTRA_TEXT, s);
			startService(intent);

		}
		else {
			intent.putExtra(Intent.EXTRA_TEXT, origurl);
			startService(intent);
		}
		finish();
	}
}
