package jp.morishi.lifegame.wallpaper;

import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class Patterns {
	public static final String TABLENAME = "LEXICON";
	public static final String COL_KEY = "KEY";
	public static final String COL_TITLE = "TITLE";
	public static final String COL_WIDTH = "WIDTH";
	public static final String COL_HEIGHT = "HEIGHT";
	public static final String COL_DATA = "DATA";
	private Context context;
	public Patterns(Context context) {
		this.context = context;
	}
	public Pattern getRandomPattern() {
		Pattern p = null;
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		try{
			sdb = helper.getReadableDatabase();
			final String[] columns = new String[]{"MAX(" + COL_KEY + ")"};
			Cursor c = sdb.query(TABLENAME,columns,null, null,
							null, null, null , null);
			int max = 0;
			if(c.moveToNext())
			{
				max = c.getInt(0);
			}
			c.close();
			if(max > 0) {
				Random r = new Random();
				int index = r.nextInt(max);
				final String[] columns2 = new String[]{COL_TITLE, COL_WIDTH, COL_HEIGHT, COL_DATA};
				String where = COL_KEY + "=?";
				String param = String.valueOf(index + 1);
				Cursor c2 = sdb.query(TABLENAME,columns2,where, new String[]{param},
								null, null, null , null);
				if(c2.moveToNext())
				{
					String title = c2.getString(0);
					int width = c2.getInt(1);
					int height = c2.getInt(2);
					String data = c2.getString(3);
					p = new Pattern();
					p.setName(title);
					p.setByteWidth(width);
					p.setByteHeight(height);
					p.setBytedataByString(data);
				}
				c2.close();
			}
		}catch(SQLiteException e){
		}		
		finally
		{
			if(sdb != null)
			{
				sdb.close();
			}
		}
		
		return p;
	}
	public Pattern getPattern(String key) {
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		Pattern p = null;
		try{
			sdb = helper.getReadableDatabase();
			final String[] columns = new String[]{COL_TITLE, COL_WIDTH, COL_HEIGHT, COL_DATA};
			String where = COL_TITLE + "=?";
			String param = key;
			Cursor c = sdb.query(TABLENAME,columns,where, new String[]{param},
							null, null, null , null);
			if(c.moveToNext())
			{
				String title = c.getString(0);
				int width = c.getInt(1);
				int height = c.getInt(2);
				String data = c.getString(3);
				p = new Pattern();
				p.setName(title);
				p.setByteWidth(width);
				p.setByteHeight(height);
				p.setBytedataByString(data);
			}
			c.close();
		}catch(SQLiteException e){
		}		
		finally
		{
			if(sdb != null)
			{
				sdb.close();
			}
		}
		return p;
	}

}
