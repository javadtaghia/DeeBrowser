package co.zew.browser.offline;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.zew.deebrowser.R;
import co.zew.browser.constant.Constants;
import co.zew.browser.utils.Utils;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {


	private DisplayAdapter gridAdapter;
	private Database mHelper;
	private SQLiteDatabase dataBase;

	private TextView noSavedPages;
	private TextView helpText;
	
	private DisplayAdapter.SortOrder sortOrder = DisplayAdapter.SortOrder.NEWEST_FIRST;
	String gottext ="";

	private GridView mainGrid;
	private SearchView mSearchView;
	private String searchQuery = "";
	private ProgressDialog pageLoadDialog;
	private AlertDialog dialogSortItemsBy;
	private ActionBar actionbar;
    private Timer mytimer;

	private int scrollPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences_offlinesave, false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPref.getBoolean("dark_mode", false)) {
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.main);
        mytimer = new Timer();


		////////////////////

		Intent iin = getIntent();
		try {


			Bundle b = iin.getExtras();


			if (b != null) {
				// Toast.makeText(this, arry, Toast.LENGTH_SHORT);
				 gottext = (String) b.get(Constants.LOAD_READING_URL);

				try {
//					Intent i = new Intent(getApplicationContext(), AddActivity.class);
//				//	i.putExtra(Intent.EXTRA_TEXT, gottext);
//					startActivity(i);
//					Toast.makeText(this, "Wait for " + gottext, Toast.LENGTH_SHORT)
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
//

		mainGrid = (GridView) findViewById(R.id.List);

		mainGrid.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		mainGrid.setMultiChoiceModeListener(new ModeCallback());

		int list_layout_type = Integer.parseInt(sharedPref.getString("layout" , "4")); // javad 1
		switch (list_layout_type) {
			case 1: break;
			case 2: mainGrid.setNumColumns(-1); break;
			case 4: mainGrid.setNumColumns(1); break;
			case 5: mainGrid.setNumColumns(1); break;
			case 6: mainGrid.setNumColumns(1); break;
			default:
		}


		pageLoadDialog = new ProgressDialog(MainActivity.this);
		actionbar = getActionBar();

		setUpGridClickListener();

		noSavedPages = (TextView) findViewById(R.id.textNoSavedPages);
		helpText = (TextView) findViewById(R.id.how_to_text);

		gridAdapter = new DisplayAdapter(MainActivity.this);

		mainGrid.setAdapter(gridAdapter);


	}

//	private boolean loadPage(Intent intent) {
//		if (intent == null) {
//			return false;
//		}
//		String mUrl = intent.getStringExtra(Constants.LOAD_READING_URL);
//		if (mUrl == null) {
//			return false;
//		}
//		if (getSupportActionBar() != null)
//			getSupportActionBar().setTitle(Utils.getDomainName(mUrl));
//		new PageLoader(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
//		return true;
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {super.onActivityResult(requestCode, resultCode, data);
		//if (requestCode == 1 && resultCode == RESULT_FIRST_USER) {
		try {
			recreate();
		}
		catch (Exception e) {
			Log.e("Theme:", e.toString());}


		//}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);

	}


	public boolean onQueryTextChange(String newText) {
		searchQuery = newText;
		displayData(newText);
		if (newText.length() == 0) {
			//actionbar.setSubtitle(R.string.action_bar_subtitle_showing_all);
		} else { 
			if (gridAdapter.getCount() == 1) {actionbar.setSubtitle(R.string.one_search_result);} else if (gridAdapter.getCount() == 0) {actionbar.setSubtitle(R.string.no_search_results);} else {actionbar.setSubtitle(gridAdapter.getCount() + " " + getResources().getString(R.string.num_search_results));}
		}
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
		displayData(query);
        return false;
    }

    public boolean onClose() {
        displayData("");
        return false;
    }

	@Override
	protected void onPause() {
		super.onPause();
		try {
			getApplicationContext().unregisterReceiver(mMessageReceiver);
		}
		catch (Exception ex){Log.e("erroronpause", ex.toString());}
		scrollPosition = mainGrid.getFirstVisiblePosition();
	}



	@Override
	protected void onResume() {
		super.onResume();
		pageLoadDialog.cancel();
		displayData(searchQuery);
		try {
			getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("deebrowsernoti"));
		}
		catch (Exception ex) {Log.e("error", ex.toString());}
		mainGrid.setSelection(scrollPosition);
		if (searchQuery.length() == 0) {
			//actionbar.setSubtitle(R.string.action_bar_subtitle_showing_all);
		}

	}
	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// Extract data included in the Intent
			//String message = intent.getStringExtra("donemessage");
			try{
			recreate();
				Toast.makeText(getApplicationContext(), "Done ! ", Toast.LENGTH_LONG)
						.show();
		}
			catch (Exception ex) {Toast.makeText(getApplicationContext(), "Refresh ! ", Toast.LENGTH_LONG)
					.show();}

			//do other stuff here
		}
	};

    private void TimerMethod() {

        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            Activity myact = new co.zew.browser.offline.MainActivity ();
            myact.recreate();

            mytimer.cancel();

        }
    };

    private void startSave(String s) {
		Intent intent = new Intent(this, SaveService.class);
            intent.putExtra(Intent.EXTRA_TEXT, s);
            startService(intent);

    }

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_add:
//                try {
//                   mytimer.schedule(new TimerTask() {
//                       @Override
//                       public void run() {
//                           TimerMethod();
//                       }
//
//                   }, 0, 3000);
//               }
//               catch (Exception e) {Log.e("Timer fail", e.toString());}
 if(isNetworkAvailable() == false)
 {
	 Toast.makeText(this, "No internet access ", Toast.LENGTH_LONG)
			 .show();

 }
				else {

	 if (isValidUrl(gottext)/*gottext.contains("http:") || gottext.contains("https:")*/) {
		 Toast.makeText(this, "Wait... ", Toast.LENGTH_LONG)
				 .show();
//		 Toast.makeText(this, "Refresh ! ", Toast.LENGTH_SHORT)
//				 .show();
		 startSave(gottext);
	 }
	 else {
		/* ClipboardManager clipMan = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		 //hidtext.setText(clipMan.getText().toString());
		 String str = "";
		 try {
			  str = clipMan.getText().toString();
		 }
		 catch (Exception ex){Log.e("clipproblem", ex.toString());}*/
		 if (false/*!str.isEmpty()*/) {
			// gottext = clipMan.getText().toString().trim();
			 if (isValidUrl(gottext)/*gottext.contains("http:") || gottext.contains("https:")*/) {
				 Toast.makeText(this, "Wait... ", Toast.LENGTH_LONG)
						 .show();
				 Toast.makeText(this, "Refresh ! ", Toast.LENGTH_SHORT)
						 .show();
				 startSave(gottext);
			 }
			 else {
				 Toast.makeText(this, "Invalid/Missed URL!\n Go back to DeeBrowser.", Toast.LENGTH_SHORT)
						 .show();
			 }
		 } else {
			 Toast.makeText(this, "Invalid/Missed URL!\n Go back to DeeBrowser.", Toast.LENGTH_SHORT)
					 .show();
		 }


	 }
 }


//				Intent i = new Intent(getApplicationContext(), AddActivity.class);
//				i.putExtra(Intent.EXTRA_TEXT, gottext);
//				startActivityForResult(i, 1);


//

				return true;
				
				case R.id.action_sort_by:
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setSingleChoiceItems(R.array.sort_by, DisplayAdapter.SortOrder.toInt(sortOrder), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							sortOrder = DisplayAdapter.SortOrder.fromInt(which);
							displayData(searchQuery);
							dialogSortItemsBy.cancel();
						}
					});
				dialogSortItemsBy = builder.create();
				dialogSortItemsBy.show();
				return true;

			case R.id.refresh_key:
				//finish();
//                this.onResume();
try {
	this.recreate();
}
catch (Exception ex){Log.e("errorinref", ex.toString());}

				return true;

			case R.id.ic_action_settings:
				Intent settings = new Intent(this, Preferences.class);
				startActivityForResult(settings, 1);

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setUpGridClickListener() {
		//click to show saved page
		mainGrid.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View clickedView, int position,
										long id) {

					clickedView.setBackgroundColor(Color.parseColor("#ff5722")); //  FFC107

					pageLoadDialog.setMessage("Please wait while loading...");
					pageLoadDialog.setIndeterminate(true);
					pageLoadDialog.setCancelable(false);

					pageLoadDialog.show();

					Intent i = new Intent(getApplicationContext(),
										  ViewActivity.class);
					i.putExtra(Database.ORIGINAL_URL, gridAdapter.getPropertiesByPosition(position, Database.ORIGINAL_URL));
					i.putExtra(Database.TITLE, gridAdapter.getPropertiesByPosition(position, Database.TITLE));
					i.putExtra(Database.ID, gridAdapter.getPropertiesByPosition(position, Database.ID));
					i.putExtra(Database.FILE_LOCATION, gridAdapter.getPropertiesByPosition(position, Database.FILE_LOCATION));
					i.putExtra(Database.THUMBNAIL, gridAdapter.getPropertiesByPosition(position, Database.THUMBNAIL));
					i.putExtra(Database.TIMESTAMP, gridAdapter.getPropertiesByPosition(position, Database.TIMESTAMP));

					startActivity(i);


				}
			});
	}
	private boolean isValidUrl(String url) {
		try {
			Pattern p = Patterns.WEB_URL;
			Matcher m = p.matcher(url);
			if (m.matches())
				return true;
			else
				return false;
		} catch (Exception ex) {
			// Toast.makeText(this, "error: " + ex.toString(), Toast.LENGTH_SHORT);
			return false;
		}
	}
	private void displayData(String searchQuery) {

		gridAdapter.refreshData(searchQuery, sortOrder, true);

		if (gridAdapter.getCount() == 0 && !searchQuery.equals("")) {
			noSavedPages.setText("No search results");
			noSavedPages.setVisibility(View.VISIBLE);
			noSavedPages.setGravity(Gravity.CENTER_HORIZONTAL);
			mainGrid.setVisibility(View.GONE);
		} else if (gridAdapter.getCount() == 0) {
			noSavedPages.setText("No saved pages");
			noSavedPages.setVisibility(View.VISIBLE);
			helpText.setVisibility(View.VISIBLE);
			mainGrid.setVisibility(View.GONE);
		} else {
			helpText.setVisibility(View.GONE); 
			noSavedPages.setVisibility(View.GONE); 
			mainGrid.setVisibility(View.VISIBLE); 
		}



	}


	class ModeCallback implements ListView.MultiChoiceModeListener	{

		private EditText e ;
		@Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			getMenuInflater().inflate(R.menu.main_activity_multi_choice, menu);
            mode.setTitle("Select Items");


            return true;
        }



		@Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			gridAdapter.selectedViewsPositions.clear();
            return true;
        }

		@Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

			switch (item.getItemId()) {
				case R.id.action_rename:
					AlertDialog.Builder rename_dialog = new AlertDialog.Builder(MainActivity.this);
					View layout = getLayoutInflater().inflate(R.layout.rename_dialog, null);
					rename_dialog.setView(layout);
					e = (EditText) layout.findViewById(R.id.rename_dialog_edit);
					TextView t = (TextView) layout.findViewById(R.id.rename_dialog_text);
					if (gridAdapter.selectedViewsPositions.size() == 1) {
						e.setText(gridAdapter.getPropertiesByPosition(gridAdapter.selectedViewsPositions.get(0), Database.TITLE));
						e.selectAll();
					
					} else {
						
						t.setText("Enter new title for these " + gridAdapter.selectedViewsPositions.size() + " saved pages :");
					}
					
					
					rename_dialog.setPositiveButton("Rename",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								mHelper = new Database(MainActivity.this);
								dataBase = mHelper.getWritableDatabase();

								for (Integer position: gridAdapter.selectedViewsPositions) {
									ContentValues values=new ContentValues();
									values.put(Database.TITLE, e.getText().toString() );
									dataBase.update(Database.TABLE_NAME, values, Database.ID + "=" + gridAdapter.getPropertiesByPosition(position, Database.ID), null);
								}
								
								if (gridAdapter.selectedViewsPositions.size() == 1) {
									Toast.makeText(MainActivity.this, "Saved page renamed", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(MainActivity.this, "Renamed " + gridAdapter.selectedViewsPositions.size() + " saved pages", Toast.LENGTH_LONG).show();
								}

								dataBase.close();
								displayData("");
								mode.finish();
							}
						});
						
					rename_dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								mode.finish();
							}
						});
					AlertDialog rename_dialog_alert = rename_dialog.create();
					rename_dialog_alert.show();
				return true;
				case R.id.action_delete:



					AlertDialog.Builder build;
					build = new AlertDialog.Builder(MainActivity.this);
					if (gridAdapter.selectedViewsPositions.size() == 1) {
						build.setMessage("Do you want to delete ?\r\n" + gridAdapter.getPropertiesByPosition(gridAdapter.selectedViewsPositions.get(0), Database.TITLE));
					} else {
						build.setMessage("Delete these " + gridAdapter.selectedViewsPositions.size() + " saved pages ?");
					}
					build.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								new deleteItemsTask().execute(gridAdapter.selectedViewsPositions.toArray());
								mode.finish();	
							}
						});

					build.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,	int which) {
								dialog.cancel();
								mode.finish();
							}
						});
					AlertDialog alert = build.create();
					alert.show();
					break;
				default:
					break;
            }
			return true;
		}
		
		private class deleteItemsTask extends AsyncTask<Object, Integer, Integer> {
			ProgressDialog pd = null;
			@Override
			protected Integer doInBackground(Object[] selectedPositions) {
				dataBase = new Database(MainActivity.this).getWritableDatabase();
				
				for (final Object position : selectedPositions) {
					String fileLocation = gridAdapter.getPropertiesByPosition((int)position, Database.FILE_LOCATION);
					DirectoryHelper.deleteDirectory(new File(fileLocation).getParentFile());
					
					dataBase.delete(Database.TABLE_NAME, Database.ID + "=" + gridAdapter.getPropertiesByPosition((int)position, Database.ID), null);
					publishProgress((Integer)position);
				}
				dataBase.close();
				return selectedPositions.length;
			}

			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(MainActivity.this);
				pd.setMessage("Deleting items...");
				pd.setIndeterminate(false);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMax(gridAdapter.selectedViewsPositions.size());
				pd.setCancelable(false);
				pd.setCanceledOnTouchOutside(false);

				pd.show();
			}

			@Override
			protected void onPostExecute(Integer result) {
				pd.hide();
				pd.cancel();	
				displayData("");
				Toast.makeText(MainActivity.this, "Deleted " + result + " saved pages", Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onProgressUpdate(Integer[] values) {
				pd.setProgress(values[0]);
			}
			
		}

		@Override
        public void onDestroyActionMode(ActionMode mode) {
			gridAdapter.selectedViewsPositions.clear();
        }

		@Override
        public void onItemCheckedStateChanged(ActionMode mode,
											  int position, long itemId, boolean checked) {
			Integer pos = position;
			View gridCellLayout = mainGrid.getChildAt(position - mainGrid.getFirstVisiblePosition()).findViewById(R.id.gridCellLayout);
			if (checked) {
				gridAdapter.selectedViewsPositions.add(pos);
				gridCellLayout.setBackgroundColor(Color.parseColor("#ff5722"));
			} else {
				gridAdapter.selectedViewsPositions.remove(pos);
				gridCellLayout.setBackgroundColor(Color.parseColor("#E2E2E2"));
			}

			final int checkedCount = gridAdapter.selectedViewsPositions.size();

            switch (checkedCount) {
                case 0:
                    mode.setSubtitle("Tap to select items");
					findViewById(R.id.action_delete).setEnabled(false);
                    break;
                case 1:
                    mode.setSubtitle("One item selected");
					findViewById(R.id.action_delete).setEnabled(true);
                    break;
                default:
                    mode.setSubtitle(checkedCount + " items selected");
					findViewById(R.id.action_delete).setEnabled(true);
                    break;
            }
        }

	}



}
