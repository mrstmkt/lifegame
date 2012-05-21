package jp.morishi.lifegame.wallpaper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "LIFEGAMEDB";
	private Context context;

//	public DatabaseOpenHelper(Context context, String name,
//			CursorFactory factory, int version) {
	public DatabaseOpenHelper(Context context) {
		super(context, DBNAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try
		{
			// テーブルの生成
			
	        StringBuilder createSql1 = new StringBuilder();
	        createSql1.append("create table " + Patterns.TABLENAME + " (");
	        createSql1.append(Patterns.COL_KEY + " integer primary key not null,");
	        createSql1.append(Patterns.COL_TITLE + " text,");
	        createSql1.append(Patterns.COL_WIDTH + " integer,");
	        createSql1.append(Patterns.COL_HEIGHT + " integer,");
	        createSql1.append(Patterns.COL_DATA + " text");
	        createSql1.append(")");

	        db.execSQL( createSql1.toString());

	        readAsset(db);
	        
	        db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	private void readAsset(SQLiteDatabase db) {
		AssetManager as = context.getResources().getAssets();   
		try {
			InputStream is = as.open("lifelexicon.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null)
            {
            	String[] spt  = line.split(",");
            	if( Integer.valueOf( spt[1]) * Integer.valueOf(spt[2]) > 0) {
        			ContentValues values = new ContentValues();
        			values.put(Patterns.COL_KEY, i);
        			values.put(Patterns.COL_TITLE, spt[0]);
        			values.put(Patterns.COL_WIDTH,  Integer.valueOf( spt[1]));
        			values.put(Patterns.COL_HEIGHT, Integer.valueOf(spt[2]));
        			values.put(Patterns.COL_DATA, spt[3]);
        			db.insert(Patterns.TABLENAME, null, values);
        	        i++;
            	}
            }
            reader.close();
            is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

}
