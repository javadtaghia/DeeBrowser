package co.zew.browser.offline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.zew.deebrowser.R;

public class NotificationTools {

		private Notification.Builder builder;
		private   NotificationManager notificationManager; // javad added static
		
		private Service context;

		private final int NOTIFICATION_ID = 1;
		
		public NotificationTools (Service context) {
			try {
				this.context = context;
				notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				builder = new Notification.Builder(context);
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		public void notifySaveStarted () {
			try {
				builder = new Notification.Builder(context);
				builder.setTicker("Saving page...")
						.setContentTitle("Dee: Saving page...")
						.setContentText("Save in progress")
						.setSmallIcon(android.R.drawable.stat_sys_download)
						.setProgress(0, 1, true)
						.setOnlyAlertOnce(true)
						.setOngoing(true);
				addCancelAction();
				context.startForeground(NOTIFICATION_ID, builder.build());
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		public void updateProgress (int progress, int maxProgress, boolean indeterminate) {
			try {
				builder.setProgress(maxProgress, progress, indeterminate);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		public void updateSmallText (String newText) {
			try {
				builder.setContentText(newText);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		public void notifyFinished (String pageTitle) {
			try {
				builder = new Notification.Builder(context);

				builder.setTicker("Save completed: " + pageTitle)
						.setContentTitle("Dee: Save completed")
						.setContentText(pageTitle)
						.setSmallIcon(R.drawable.ic_notify_save)
						.setProgress(0, 0, false)
						.setOnlyAlertOnce(false)
						.setOngoing(false);
updateMyActivity(context, "done");
				context.stopForeground(true);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}

		}
		
		public void notifyFailure (String message, String pageUrl) {
			try {
				builder = new Notification.Builder(context);

				builder.setTicker("Error, page not saved: " + message)
						.setContentTitle("Dee: Error, page not saved")
						.setContentText(message)
						.setProgress(0, 0, false)
						.setOngoing(false)
						.setOnlyAlertOnce(false)
						.setSmallIcon(android.R.drawable.stat_sys_warning);

				addRetryAction(pageUrl);

				context.stopForeground(true);
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		public  void cancelAll() {
			try {
				context.stopForeground(true);
				notificationManager.cancelAll();
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
		
		private void addCancelAction () {
			try {
			Intent cancelIntent = new Intent(context, SaveService.class);
			cancelIntent.putExtra("USER_CANCELLED", true);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(R.drawable.ic_notify_discard, "Cancel", pendingIntent);
            }
            catch (Exception e){
                Log.e("CancelProblem", e.toString());}
//            try {
//                cancelAll();
//            }
//            catch (Exception c){}
			//builder.setAutoCancel(true);

		}



	static void updateMyActivity(Context context, String message) {

		Intent intent = new Intent("deebrowsernoti");

		//put whatever data you want to send, if any
		intent.putExtra("donemessage", message);

		//send broadcast
		context.sendBroadcast(intent);
	}

		private void addRetryAction (String url) {
			try {
				Intent intent = new Intent(context, SaveService.class);
				intent.putExtra(Intent.EXTRA_TEXT, url);
				PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.ic_notify_retry, "Retry", pendingIntent);
			}
			catch (Exception e){
				Log.e("CancelProblem", e.toString());}
		}
}
