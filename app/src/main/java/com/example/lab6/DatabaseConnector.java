package com.example.lab6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector {
    private static final String DATABASE_NAME="UserRows";
    private SQLiteDatabase database;
    private DBHelper databaseOpenHelper;
    public DatabaseConnector(Context context) {
        databaseOpenHelper=new DBHelper(context, DATABASE_NAME, null, 1);
    }
    public void open() throws SQLException {
        database=databaseOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (database!=null) database.close();
    }
    public void insertRow(String text) {
        ContentValues row = new ContentValues(); row.put(DBHelper.TABLE_COLUMN_text, text);
        open(); database.insert(DBHelper.TABLE, null, row); close();
    }
    public Cursor getTableAllRows() {
        return database.query(DBHelper.TABLE, new String[] {"_id", DBHelper.TABLE_COLUMN_text},
                null,null,null,null,DBHelper.TABLE_COLUMN_text);
    }
    public void deleteTableRow(long id) {
        open(); database.delete(DBHelper.TABLE, "_id=" + id, null); close();
    }


    private class DBHelper extends SQLiteOpenHelper {
        public static final String TABLE="Table1";
        public static final String TABLE_COLUMN_id="_id";
        public static final String TABLE_COLUMN_text="txt";

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " ( "
                    + TABLE_COLUMN_id + " integer primary key autoincrement, "
                    + TABLE_COLUMN_text + " TEXT" + " );");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

}
