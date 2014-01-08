package ru.ifmo.lesson6;

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
public class MainDatabase {

    private static final String TAG = "MainDatabase";
    private static final int DATABASE_VERSION = 3;
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private String DATABASE_NAME;
    private String DATABASE_TABLE;
    private String DATABASE_CREATE;
    private boolean justCreated = false;

    public MainDatabase(Context ctx) {
        this.mCtx = ctx;
        DATABASE_NAME = "MainData";
        DATABASE_TABLE = "MainDatabase";

        DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " (";
        for (int i = 0; i < FeedItem.tags.length; i++) {
            DATABASE_CREATE += FeedItem.tags[i];
            if (i == 0) DATABASE_CREATE += " INTEGER PRIMARY KEY AUTOINCREMENT";
            if (i < FeedItem.tags.length - 1) DATABASE_CREATE += ", ";
        }
        DATABASE_CREATE += ");";

    }

    public MainDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        if (justCreated) {
            FeedItem item = new FeedItem();
            item.param[FeedItem.NAME] = "lenta.ru";
            item.param[FeedItem.URL] = "http://lenta.ru/rss";
            addItem(item);
            item.param[FeedItem.NAME] = "bash.im";
            item.param[FeedItem.URL] = "http://bash.im/rss";
            addItem(item);
            item.param[FeedItem.NAME] = "stackoverflow.com";
            item.param[FeedItem.URL] = "http://stackoverflow.com/feeds/tag/android";
            addItem(item);
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long addItem(FeedItem item) {
        ContentValues initialValues = new ContentValues();
        for (int i = 1; i < FeedItem.tags.length; i++) {
            initialValues.put(FeedItem.tags[i], item.param[i]);
        }

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteItem(FeedItem item) {
        return mDb.delete(DATABASE_TABLE, FeedItem.tags[0] + "=" + item.param[FeedItem.ID], null) > 0;
    }

    public ArrayList<FeedItem> getAllItems() {
        Cursor cursor = mDb.query(DATABASE_TABLE, null, null, null, null, null, null);
        ArrayList<FeedItem> items = new ArrayList<FeedItem>();
        FeedItem curItem = new FeedItem();
        while (cursor.moveToNext()) {
            curItem.clear();
            for (int i = 0; i < FeedItem.tags.length; i++)
                curItem.param[i] = cursor.getString(cursor.getColumnIndex(FeedItem.tags[i]));
            items.add(curItem.makeCopy());
        }
        cursor.close();
        return items;
    }

    public FeedItem getItem(int id) throws SQLException {

        Cursor cursor =
                mDb.query(true, DATABASE_TABLE, null, FeedItem.tags[0] + "=" + id, null,
                        null, null, null, null);

        if (cursor == null) {
            Log.e(TAG, "Error getting item");
            return null;
        }

        cursor.moveToFirst();
        FeedItem item = new FeedItem();
        for (int i = 0; i < FeedItem.tags.length; i++)
            item.param[i] = cursor.getString(cursor.getColumnIndex(FeedItem.tags[i]));
        cursor.close();

        return item;
    }

    public boolean updateItem(FeedItem item, int id) {
        ContentValues args = new ContentValues();
        for (int i = 0; i < FeedItem.tags.length; i++) {
            args.put(FeedItem.tags[i], item.param[i]);
        }

        return mDb.update(DATABASE_TABLE, args, FeedItem.tags[0] + "=" + id, null) > 0;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            justCreated = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

}
