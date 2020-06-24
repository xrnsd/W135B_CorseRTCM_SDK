package com.kuyou.rtcm.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DataCleanManager {

	private static String TAG = "RTC_LOG";
	private static void deleteFilesByDirectory(File directory) {
		Log.d(TAG, "directory = " + directory);
		if (directory != null && directory.exists()) {
			File[] files = directory.listFiles();
			Log.d(TAG, "enter directory  files.length = " + files.length);
			for (File item : files) {
				if(item.isDirectory()){
					deleteFilesByDirectory(item);
				}else{
					boolean result = item.delete();
					Log.d(TAG, "directory = " + item + "; result = " + result);
				}
			}
		}
	}
	/**
	 * 
	* * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
	*
	* @param context
	*/
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/**
	* * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
	*
	* @param context
	*/
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
	}

	/**
	* * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
	*
	* @param context
	*/
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
	}

	/**
	* * 清除/data/data/com.xxx.xxx/files下的内容 * *
	*
	* @param context
	*/
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	* * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
	*
	* @param context
	*/
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	* * 清除本应用所有的数据 * *
	*
	* @param context
	*/
	public static void cleanApplicationData(Context context) {
//		cleanDatabases(context);
//		cleanInternalCache(context);
//		cleanExternalCache(context);
//		cleanSharedPreference(context);
//		cleanFiles(context);
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName()));
	}
	
	public static boolean cleanStorageFileByPath(String path){
		try{
			File file1 = new File(Environment.getExternalStorageDirectory(),path);
			boolean result = file1.delete();
			Log.d(TAG, "directory = " + file1 + "; result = " + result);
			return result;
		}catch (Exception e) {
			Log.e(TAG, "cleanStorageFileByPath file = " + path);
			return false;
		}
	}
}
