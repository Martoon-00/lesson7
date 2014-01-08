package ru.ifmo.lesson6;

/**
 * Created by asus on 07.01.14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 06.11.13
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created by asus on 27.12.13.
 */
public class ArticlesDatabase {

    private static final String TAG = "ArticlesDatabase";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    private String DATABASE_NAME;
    private String DATABASE_TABLE;

    private String DATABASE_CREATE;

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public ArticlesDatabase(Context ctx) {
        this.mCtx = ctx;
        DATABASE_NAME = "ArticlesData";
        DATABASE_TABLE = "ArticlesDatabase";

        DATABASE_CREATE = "CREATE TABLE "+ DATABASE_TABLE + " (";
        for (int i = 0; i < Article.SqlTags.length; i++){
            DATABASE_CREATE += Article.SqlTags[i];
            if (i == 0) DATABASE_CREATE += " INTEGER PRIMARY KEY AUTOINCREMENT";
            if (i < Article.SqlTags.length - 1) DATABASE_CREATE += ", ";
        }
        DATABASE_CREATE += ");";

    }

    public ArticlesDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long addItem(Article item) {
        ContentValues initialValues = new ContentValues();
        for (int i = 1; i < Article.SqlTags.length; i++){
            initialValues.put(Article.SqlTags[i], item.param[i]);
        }

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteItem(Article item) {
        return mDb.delete(DATABASE_TABLE, Article.SqlTags[0] + "=" + item.param[Article.ID], null) > 0;
    }


    public ArrayList<Article> getAllItems(String feedId) {
        Cursor cursor = mDb.query(DATABASE_TABLE, null, Article.SqlTags[Article.FEED_ID] + " = '" + feedId + "'", null, null, null, null);
        ArrayList<Article> items = new ArrayList<Article>();
        Article curItem = new Article();
        while (cursor.moveToNext()){
            curItem.clear();
            for (int i = 0; i < Article.SqlTags.length; i++) curItem.param[i] = cursor.getString(cursor.getColumnIndex(Article.SqlTags[i]));
            items.add(curItem.makeCopy());
        }
        cursor.close();
        return items;
    }

    public Article getItem(int id) throws SQLException {

        Cursor cursor =
                mDb.query(true, DATABASE_TABLE, null, Article.SqlTags[0] + "=" + id, null,
                        null, null, null, null);

        if (cursor == null) {
            Log.e(TAG, "Error getting item");
            return null;
        }

        cursor.moveToFirst();
        Article item = new Article();
        for (int i = 0; i < Article.SqlTags.length; i++) item.param[i] = cursor.getString(cursor.getColumnIndex(Article.SqlTags[i]));
        cursor.close();

        return item;
    }

    public boolean updateItem(Article item, int id) {
        ContentValues args = new ContentValues();
        for (int i = 0; i < Article.SqlTags.length; i++){
            args.put(Article.SqlTags[i], item.param[i]);
        }

        return mDb.update(DATABASE_TABLE, args, Article.SqlTags[0] + "=" + id, null) > 0;
    }

    public void replaceAllWith(String feedid, ArrayList<Article> items){
        mDb.delete(DATABASE_TABLE, Article.SqlTags[Article.FEED_ID] + " = '" + feedid + "'", null);
        for (int i = 0; i < items.size(); i++){
            addItem(items.get(i));
        }
    }

}

