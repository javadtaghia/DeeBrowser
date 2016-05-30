package co.zew.browser.offline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import co.zew.deebrowser.R;
import co.zew.browser.activity.*;
import co.zew.browser.constant.Constants;

public class ViewActivity extends Activity {
	private Intent incomingIntent;
	private SharedPreferences preferences;
	
	private String title;
	private String fileLocation;
	private String date;
	private WebView webview;
	private WebView.HitTestResult result;
	private boolean invertedRendering;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		incomingIntent = getIntent();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (preferences.getBoolean("dark_mode", false)) {
			setTheme(android.R.style.Theme_Holo);
		}
		
		setContentView(R.layout.view_activity);
		
		title = incomingIntent.getStringExtra(Database.TITLE);
		fileLocation = incomingIntent.getStringExtra(Database.FILE_LOCATION);
		date = incomingIntent.getStringExtra(Database.TIMESTAMP);
		
		getActionBar().setDisplayHomeAsUpEnabled(true); // javad hide bar
		getActionBar().setSubtitle(""/*incomingIntent.getStringExtra(Database.TITLE)*/);
		getActionBar().setTitle("");
		getActionBar().setIcon(R.drawable.ic_action_reading);
		setProgressBarIndeterminateVisibility(false); // javad true to false not to show in view

		webview = (WebView) findViewById(R.id.webview);
		try {
			setupWebView();


		invertedRendering = preferences.getBoolean("dark_mode", false); // false
		webview.loadUrl("file://" + fileLocation);

		}
		catch (Exception ex) {Log.e("setupprobleminview", ex.toString());}

	}





	@Override
	protected void onResume() {
		super.onResume();
		//set up inverted rendering, aka. night mode, if enabled.
		if (invertedRendering) {
			float[] mNegativeColorArray = { 
				-1.0f, 0, 0, 0, 255, // red
				0, -1.0f, 0, 0, 255, // green
				0, 0, -1.0f, 0, 255, // blue
				0, 0, 0, 1.0f, 0 // alpha
			};
			Paint mPaint = new Paint();
			ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mNegativeColorArray);
			mPaint.setColorFilter(filterInvert);
			//webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			webview.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
		}
	}
	
	private void setupWebView() {
		String ua = preferences.getString("user_agent", "Mobile");
		boolean javaScriptEnabled = preferences.getBoolean("enable_javascript", false); // javad enable javad script
		
		registerForContextMenu(webview);

		webview.getSettings().setUserAgentString(ua);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setJavaScriptEnabled(javaScriptEnabled);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setDisplayZoomControls(false);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setAllowFileAccessFromFileURLs(true);
		webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


		int currentapiVersion = android.os.Build.VERSION.SDK_INT; // javad api control crash in api 16
		if (currentapiVersion >= 17){

			webview.getSettings().setMediaPlaybackRequiresUserGesture(false);

		} else{
			// do something for phones running an SDK before lollipop
		}
		webview.getSettings().setDefaultTextEncodingName("UTF-8");
		
//		webview.setWebViewClient(new WebViewClient() {
//			@Override
//			public void onPageFinished(WebView view, String url){
//				setProgressBarIndeterminateVisibility(false);
//
//			}
//// Dispatch touch event to view
//
////			@Override
////			public boolean shouldOverrideUrlLoading(WebView view, String url) {
////				try {
////					//send the user to installed browser instead of opening in the app, as per issue 19.
////					//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); // javad webview problem
////					return true;
////				} catch (Exception e) {
////					//Activity not found or bad url
////					e.printStackTrace();
////					return false;
////				}
////			}
//@Override
//public void onScaleChanged(WebView view, float oldScale, float newScale) {
//	if (view != null) {
//		view.invalidate();
//	}
//}
//
//			@Override
//			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//				if (!url.startsWith("file") && preferences.getBoolean("offline_sandbox_mode", false)) {
//					Log.w("ViewActivity", "Request blocked: " + url);
//					return new WebResourceResponse(null, null, null);
//				} else {
//					return null;
//				}
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_activity_actions, menu);
		Toast.makeText(this, "Touch the screen!",
				Toast.LENGTH_LONG).show();


		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.ic_action_settings:
				Intent settings = new Intent(getApplicationContext(), Preferences.class);
				startActivityForResult(settings, 1);
				return true;
				
			case R.id.action_save_page_properties:
				showPropertiesDialog();
				return true;
				
			case R.id.action_open_in_zewbrowser:
				Intent incomingIntent = getIntent();

			//	Uri uri = Uri.parse(incomingIntent.getStringExtra(Database.ORIGINAL_URL));
//				Intent startBrowserIntent = new Intent(Intent.ACTION_VIEW, uri);
//				startActivity(startBrowserIntent);
				//Intent read = new Intent(this, FullscreenActivity.class);
				Intent read = new Intent(this, co.zew.browser.activity.MainActivity.class);
				//read.addFlags(Intent.FLAG_FROM_BACKGROUND); // javad archive browser
				//read.putExtra("readurl", incomingIntent.getStringExtra(Database.ORIGINAL_URL).toString());// Database.ORIGINAL_URL);
				read.setData(Uri.parse(incomingIntent.getStringExtra(Database.ORIGINAL_URL).toString()));
				startActivity(read);
				//finish();

				return true;



			//action_open_file_in_trimview" action_open_file_in_incognito
			case R.id.action_open_file_in_trimview:
				Intent read5 = new Intent(this, ReadingActivity.class);
                read5.addFlags(read5.FLAG_ACTIVITY_CLEAR_TOP);
                read5.putExtra(Constants.LOAD_READING_URL, "file://" + fileLocation);
				//read5.putExtra(Constants.LOAD_READING_URL, incomingIntent5.getStringExtra(Database.ORIGINAL_URL).toString());
			startActivity(read5);

				return true;

			case R.id.action_open_file_in_incognito:
				Intent incomingIntent4 = getIntent();

				//	Uri uri = Uri.parse(incomingIntent.getStringExtra(Database.ORIGINAL_URL));
//				Intent startBrowserIntent = new Intent(Intent.ACTION_VIEW, uri);
//				startActivity(startBrowserIntent);
				//Intent read = new Intent(this, FullscreenActivity.class);
				Intent read4 = new Intent(this, co.zew.browser.activity.IncognitoActivity.class);
				//read.addFlags(Intent.FLAG_FROM_BACKGROUND); // javad archive browser
				//read4.putExtra("readurl", incomingIntent4.getStringExtra(Database.ORIGINAL_URL).toString());// Database.ORIGINAL_URL);
				read4.setData(Uri.parse(incomingIntent4.getStringExtra(Database.ORIGINAL_URL).toString()));

				startActivity(read4);
				//finish();

				return true;



			case R.id.action_open_in_external:
				Intent incomingIntent2 = getIntent();

					Uri uri = Uri.parse(incomingIntent2.getStringExtra(Database.ORIGINAL_URL));
				Intent startBrowserIntent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(startBrowserIntent);
//				//Intent read = new Intent(this, FullscreenActivity.class);
//				Intent read = new Intent(this, co.zew.browser.activity.MainActivity.class);
//				//read.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				read.putExtra("readurl", incomingIntent.getStringExtra(Database.ORIGINAL_URL).toString());// Database.ORIGINAL_URL);
//				startActivity(read);
				//finish();

				return true;


			case R.id.action_open_file_in_zewreader:
				Intent read2 = new Intent(this, FullscreenActivity.class);
				//read2.setDataAndType(Uri.fromFile(new File(fileLocation)), "text/html");
				//newIntent.setFlags(newIntent.FLAG_ACTIVITY_NEW_TASK);
				read2.addFlags(read2.FLAG_ACTIVITY_CLEAR_TOP);
				read2.putExtra("readurl", "file://" + fileLocation);// Database.ORIGINAL_URL);
				startActivity(read2);
				finish();
				return true;




			case R.id.action_open_file_in_external:
				Intent newIntent = new Intent(Intent.ACTION_VIEW);
				newIntent.setDataAndType(Uri.fromFile(new File(fileLocation)), "text/html");
				newIntent.setFlags(newIntent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(newIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this, "No installed app can open HTML files", Toast.LENGTH_LONG).show();
				}
				return true;

			case R.id.action_delete:

				AlertDialog.Builder build;
				build = new AlertDialog.Builder(ViewActivity.this);
				build.setTitle("Delete ?");
				build.setMessage(title);
				build.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							SQLiteDatabase dataBase = new Database(ViewActivity.this).getWritableDatabase();
							Intent incomingIntent2 = getIntent();

							dataBase.delete(Database.TABLE_NAME, Database.ID + "=" + incomingIntent2.getStringExtra(Database.ID), null);

							String fileLocation = incomingIntent2.getStringExtra(Database.FILE_LOCATION);
							DirectoryHelper.deleteDirectory(new File(fileLocation).getParentFile());

							Toast.makeText(ViewActivity.this, "Saved page deleted", Toast.LENGTH_LONG).show();

							finish();
						}
					});

				build.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
				AlertDialog alert = build.create();
				alert.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showPropertiesDialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("Details of saved page");
		View layout = getLayoutInflater().inflate(R.layout.properties_dialog, null);
		build.setView(layout);
		TextView t = (TextView) layout.findViewById(R.id.properties_dialog_text_title);
		t.setText("Title: \r\n" + title);
		t = (TextView) layout.findViewById(R.id.properties_dialog_text_file_location);
		t.setText("File location: \r\n" + fileLocation);
		t = (TextView) layout.findViewById(R.id.properties_dialog_text_date);
		t.setText("Date & Time saved: \r\n" + date);
		t = (TextView) layout.findViewById(R.id.properties_dialog_text_orig_url);
		t.setText("Saved from: \r\n" + incomingIntent.getStringExtra(Database.ORIGINAL_URL));
		build.setPositiveButton("Close",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
		build.setNeutralButton("Copy file location to clipboard", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
					ClipData clip = ClipData.newPlainText(webview.getTitle(), fileLocation);
					clipboard.setPrimaryClip(clip);
					Toast.makeText(ViewActivity.this, "File location copied to clipboard", Toast.LENGTH_SHORT).show();
					
				}
		});
		AlertDialog alert = build.create();
		alert.show();
	}


	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		result = webview.getHitTestResult();

		if (result.getType() == WebView.HitTestResult.ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			// Menu options for a hyperlink.
			//set the header title to the link url
			menu.setHeaderTitle(result.getExtra());
			menu.add(3, 3, 3, "Save Link");
			menu.add(4, 4, 4, "Share Link");
			menu.add(6, 6, 6, "Copy Link to clipboard");
			menu.add(5, 5, 5, "Open Link");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == 5) {
			Uri uri = Uri.parse(result.getExtra());
			Intent startBrowserIntent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(startBrowserIntent);
		} else if (item.getItemId() == 4) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_TITLE, webview.getTitle());
			i.putExtra(Intent.EXTRA_TEXT, result.getExtra());
			startActivity(Intent.createChooser(i, "Share Link via"));

		} else if (item.getItemId() == 3) {
			Intent intent = new Intent(this, SaveService.class);
			intent.putExtra("origurl", result.getExtra());
			startService(intent);

		} else if (item.getItemId() == 6) {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			ClipData clip = ClipData.newPlainText(webview.getTitle(), result.getExtra());
			clipboard.setPrimaryClip(clip);
			Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();

		}
		return super.onContextItemSelected(item);

	}

}
