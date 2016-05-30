package co.zew.browser.offline;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.zew.deebrowser.R;
import co.zew.browser.activity.FullscreenActivity;

public class SaveService extends Service {
	
private final String TAG = "SaveService";

private ThreadPoolExecutor executor;
private SharedPreferences sharedPreferences;
private PageSaver pageSaver;
public   NotificationTools notificationTools; // javad added static

	@Override
	public void onCreate() {
		executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SaveService.this);
		pageSaver = new PageSaver(new PageSaveEventCallback());
        pageSaver.setopt(sharedPreferences.getBoolean("imageselectoff", true),
                sharedPreferences.getBoolean("frameselectoff", true),
                sharedPreferences.getBoolean("otherselectoff", true), // javad false to true
                sharedPreferences.getBoolean("scriptselectoff", true),
                sharedPreferences.getBoolean("videoselectoff", false));

       // boolean _saveimage,boolean _saveframe,boolean _saveother, boolean _savescript,boolean _savevideo
        //    sharedPreferences.getBoolean("imageselectoff", false),
//					sharedPreferences.getBoolean("frameselectoff", true),
//					sharedPreferences.getBoolean("otherselectoff", false),
//					sharedPreferences.getBoolean("scriptselectoff", true),
//					sharedPreferences.getBoolean("videoselectoff", false));
		notificationTools = new NotificationTools(this);
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getBooleanExtra("USER_CANCELLED", false)) {
			pageSaver.cancel();
			return START_NOT_STICKY;
		}
		
		String pageUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
		executor.submit(new PageSaveTask(pageUrl));
		
		return START_NOT_STICKY;
	}
	
	private class PageSaveTask implements Runnable {
		private final String pageUrl;
		private String destinationDirectory;

		public PageSaveTask (String pageUrl) {
			this.pageUrl = pageUrl;
			this.destinationDirectory = DirectoryHelper.getDestinationDirectory(sharedPreferences);
		}

		@Override
		public void run() {

			pageSaver.resetState();
			
			notificationTools.notifySaveStarted(); //javad notification
				
			pageSaver.getOptions().setUserAgent(sharedPreferences.getString("user_agent", getResources().getStringArray(R.array.entries_list_preference)[1])); // set options

			// javad set image video options



			pageSaver.getOptions().setCache(getApplicationContext().getExternalCacheDir(), 1024 * 1024 * Integer.valueOf(sharedPreferences.getString("cache_size", "30")));
            boolean success = pageSaver.getPage(pageUrl, destinationDirectory, "index.html");
			
			if (pageSaver.isCancelled()) { //user cancelled, remove the notification, and delete files.
				try{
                DirectoryHelper.deleteDirectory(new File(destinationDirectory));
				Log.e("SaveService", "Stopping Service, (Cancelled). Deleting files in: " + destinationDirectory + ", from: " + pageUrl);
				notificationTools.cancelAll();}
				catch (Exception e) {Log.e("Exc: ", e.toString());}
                return;
            } else if (!success) { //something went wrong, leave the notification, and delete files.
                DirectoryHelper.deleteDirectory(new File(destinationDirectory));
				Log.e("SaveService", "Failed. Deleting files in: " + destinationDirectory + ", from: " + pageUrl);
                return;
            }

            File oldSavedPageDirectory = new File(destinationDirectory);
			Log.i(TAG, "Original saved page directory: "  + oldSavedPageDirectory.getPath());

			File newSavedPageDirectory = new File(getNewDirectoryPath(pageSaver.getPageTitle(), oldSavedPageDirectory.getPath()));
			Log.i(TAG, "Rename to: " + newSavedPageDirectory.getPath());

            oldSavedPageDirectory.renameTo(newSavedPageDirectory);

            new Database(SaveService.this).addToDatabase(newSavedPageDirectory.getPath() + File.separator, pageSaver.getPageTitle(), pageUrl);

//try {
//	Intent i = new Intent(SaveService.this, ScreenshotService.class);
//	i.putExtra(Database.FILE_LOCATION, "file://" + newSavedPageDirectory.getPath() + File.separator + "index.html");
//	i.putExtra(Database.THUMBNAIL, newSavedPageDirectory + File.separator + "saveForOffline_thumbnail.png");
//	//startService(i); // javad
//}

//catch (Exception ex) {}

//			Intent read2 = new Intent("co.zew.browser.offline.MainActivity");
//			startActivity(read2);

			if (executor.getQueue().isEmpty() == true) {
//                try {
//                    			Intent read2 = new Intent("co.zew.browser.offline.MainActivity");
//			startActivity(read2);
//                    Activity myact = new co.zew.browser.offline.MainActivity ();
//                    myact.recreate();
//                }
//
                try {

                    stopSelf();
                }
                catch (Exception e){
                    Log.e("CancelProblem", e.toString());}





			}


//            try{
//                    Activity myact = new co.zew.browser.offline.MainActivity ();
//                    myact.recreate();
////                Intent i = new Intent(getApplicationContext(), MainActivity.class);
////                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
//////				i.putExtra(Intent.EXTRA_TEXT, gottext);
////                startActivity(i);
//            }
//            catch ( Exception e){ Log.e("finishproblem", e.toString());}

			notificationTools.notifyFinished(pageSaver.getPageTitle());	
		}
		
		private String getNewDirectoryPath (String title, String oldDirectoryPath) {
			String returnString = title.replaceAll("[^a-zA-Z0-9-_\\.]", "_") + DirectoryHelper.createUniqueFilename();

			File f = new File(oldDirectoryPath);
			return f.getParentFile().getAbsolutePath() + File.separator  + returnString + File.separator;
		}
	}



	private class PageSaveEventCallback implements EventCallback {

		@Override
		public void onFatalError(final Throwable e, String pageUrl) {
			Log.e("PageSaverService", e.getMessage(), e);

			notificationTools.notifyFailure(e.getMessage(), pageUrl);
		}

		//public NotificationTools getnotificationtools(){return  notificationTools;}
		@Override
		public void onProgressChanged(final int progress, final int maxProgress, final boolean indeterminate) {
			notificationTools.updateProgress(progress, maxProgress, indeterminate);
		}

		@Override
		public void onProgressMessage(final String message) {
			notificationTools.updateSmallText(message);
		}

		@Override
		public void onLogMessage(final String message) {
			Log.d("PageSaverService", message);
		}

		@Override
		public void onError(final Throwable e) {
			Log.e("PageSaverService", e.getMessage(), e);
		}

		@Override
		public void onError(String errorMessage) {
			Log.e(TAG, errorMessage);
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Service destroyed");
	}

	@Override
	public IBinder onBind(Intent i) {
		return null;
	}	
}
