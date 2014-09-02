package com.example.dbdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private Context mContext;
	public static String TBL_LOGIN = "login";
	public static String TBL_COL_ID = "_id";
	public static String TBL_COL_UNAME = "username";
	public static String TBL_COL_PASSWORD = "password";
	public static String DB_NAME = "mydb.db";
	private static int DB_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	private void isDirectoryPresent(String db_path) {
		// create "databases" directory if not in existence in data/data/package_name/databases/
		File file = new File(db_path.substring(0, db_path.indexOf("/"+DB_NAME)));
		
		// check if databases folder exists or not.
		if(!file.isDirectory())
			file.mkdir();
	}
	
	public void createDatabaseFile(){
		
		// data/data/package_name/databases/db_name.db
		String db_path = mContext.getDatabasePath(DBHelper.DB_NAME).toString();
		isDirectoryPresent(db_path); 
		
		File file = new File(db_path);
		Log.d(getClass().getSimpleName(), file.getAbsolutePath());
		if(file.exists()){
			Log.d(getClass().getSimpleName(), "File already exists");
		}
		else{
			copyDatabase(file);
		}
	}
	
	private void copyDatabase(File file) {
		try {
			file.createNewFile();   // create new file if it is not in existence
			InputStream is = mContext.getAssets().open(DB_NAME);
			OutputStream write = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				write.write(buffer, 0, length);
			}
			is.close();
			write.close();
			Log.d(getClass().getSimpleName(), "File does not exists & Newly created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Uri getLoginTableUri() {
		Uri contentUri = Uri.withAppendedPath(ContentProviderDB.CONTENT_URI, DBHelper.TBL_LOGIN);
		Log.e("getTableName()", ContentProviderDB.getTableName(contentUri));
		return contentUri;
	}
	
	public void insertIntoLogin(String username, String password) {
		//db.execSQL("insert into "+TBL_LOGIN+" ("+TBL_COL_UNAME+","+TBL_COL_PASSWORD+")values('"+username+"','"+password+"')");
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.TBL_COL_UNAME, username.toString());
		values.put(DBHelper.TBL_COL_PASSWORD, password.toString());
		
		mContext.getContentResolver().insert(getLoginTableUri(), values);
	}
	
	public int updateUsingUserName(String username, String password) {
		
		ContentValues values = new ContentValues();
		values.put(TBL_COL_PASSWORD, password);
		
		String where = TBL_COL_UNAME+"=?";
		String[] selectionArgs = {username};
		
		return mContext.getContentResolver().update(getLoginTableUri(), values, where, selectionArgs);
		//db.execSQL("update "+TBL_LOGIN+" set "+TBL_COL_PASSWORD+"='"+password+"' where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
	
	public int deleteByUserName(String username) {

		String where = TBL_COL_UNAME+"=?";
		String[] whereArgs = {username};
		
		return mContext.getContentResolver().delete(getLoginTableUri(), where, whereArgs);
		//db.execSQL("delete from "+TBL_LOGIN+" where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
	
	public Cursor readAllLogin() {
		String[] projection = { TBL_COL_ID, TBL_COL_UNAME, TBL_COL_PASSWORD };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		
		return mContext.getContentResolver().query(getLoginTableUri(), projection, selection, selectionArgs, sortOrder);
		
		//return db.rawQuery("select * from "+TBL_LOGIN, null);
	}
	
	public Cursor readFromUserName(String username) {
		
		String[] projection = { TBL_COL_ID, TBL_COL_UNAME, TBL_COL_PASSWORD };
		String selection = TBL_COL_UNAME + " LIKE ?";
		String[] selectionArgs = {username};
		String sortOrder = null;
		
		return mContext.getContentResolver().query(getLoginTableUri(), projection, selection, selectionArgs, sortOrder);
		
		//return db.rawQuery("select * from "+TBL_LOGIN+" where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
}
