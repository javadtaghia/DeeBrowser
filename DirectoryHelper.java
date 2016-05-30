package co.zew.browser.offline;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DirectoryHelper {
	
	public static String createUniqueFilename () {
		//creates filenames based on the date and time, hopefully unique
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
		String filename = sdf.format(new Date());
		return filename;
	}
	
	private static String getStorageDir() {
		//FIXME!!!! replace this with getExternalFilesDir()
		String baseDir = Environment.getExternalStorageDirectory().toString();
		String directory = baseDir + "/Android/data/co.zew.deebrowser/files/";
		return directory;
	}

	
	public static String getDestinationDirectory (SharedPreferences sharedPref) {

        String defaultFileLocation = getStorageDir() + createUniqueFilename() + File.separator;

        if (false /*sharedPref.getBoolean("is_custom_storage_dir", false)*/) { // javad location offline change
			String directoryLocation = sharedPref.getString("custom_storage_dir", defaultFileLocation) + createUniqueFilename() + File.separator;
			createNomediaFile(directoryLocation);
			return directoryLocation;
        } else {
            return defaultFileLocation; //  defaultFileLocation javad change of default directory
        }

	}
	
	private static void createNomediaFile (String directoryLocation) {
		File directory = new File(directoryLocation);
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				Log.e("SaveService / DirectoryHelper", "Could not create directory " + directoryLocation + ", mkdirs() returned false !");
			}
			try {
				new File(directory.getParent(), ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.e("SaveService / DirectoryHelper", "IOException while creating .nomedia file in " + directory.getParent() +" Is the path writable ?");
			}
		}
	}
	
	public static void deleteDirectory(File directory) {
		
		if (!directory.exists()) {
            return;
		}
		
       if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
              	 deleteDirectory(f);	 
            }
        }
       directory.delete();
	}
}
