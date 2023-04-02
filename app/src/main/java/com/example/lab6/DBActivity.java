package com.example.lab6;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DBActivity extends Activity {
    private ArrayList<String> lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_xml);

        ListView lv = (ListView) findViewById(R.id.lv);
        registerForContextMenu(lv);
    }
    protected void onResume() {
        super.onResume(); refresh_screen();
    }
    void refresh_screen() { new GetRowsTask().execute((Object[])null);}
    public void add_btn_clicked(View view) {
        TextView txtv=(TextView) findViewById(R.id.txtvw);

        DatabaseConnector databaseConnector=new DatabaseConnector(DBActivity.this);
        databaseConnector.insertRow(txtv.getText().toString());
        refresh_screen();

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
    }
    final int MENU_CONTEXT_DELETE_ID=123;
    final int MENU_CONTEXT_EDIT_ID=124;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv) {
            ListView lv=(ListView) v;
            menu.add(Menu.NONE, MENU_CONTEXT_DELETE_ID,Menu.NONE,"Delete");
            menu.add(Menu.NONE, MENU_CONTEXT_EDIT_ID,Menu.NONE,"Edit");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String str= lst.get(info.position);

        switch(item.getItemId()) {
            case MENU_CONTEXT_DELETE_ID: {
                Log.d(TAG, "removing item pos=" + info.position);

                long rid=Long.parseLong(str.split(",")[0].substring(3));
                DatabaseConnector databaseConnector=new DatabaseConnector(DBActivity.this);
                databaseConnector.deleteTableRow(rid);
                refresh_screen();
                return true;
            }
            case MENU_CONTEXT_EDIT_ID: {
                Log.d(TAG, "edit item pos=" + info.position);

                String txt=str.split(",")[1].substring(4);
                ((TextView)findViewById(R.id.txtvw)).setText(txt);
                return true;
            }
            default: return super.onContextItemSelected(item);
        }
    }


    private class GetRowsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnector databaseConnector=new DatabaseConnector(DBActivity.this);
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open(); return databaseConnector.getTableAllRows();
        }
        @Override
        protected void onPostExecute(Cursor cursor)
        {
            lst=new ArrayList<String>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                lst.add("id="+cursor.getString(0)+",txt= "+cursor.getString(1));
            }
            databaseConnector.close();
            ListAdapter listAdapter=new ArrayAdapter<String>(DBActivity.this,
                    android.R.layout.simple_list_item_1, lst);

            ListView lv=(ListView) findViewById(R.id.lv);
            lv.setAdapter(listAdapter);
        }
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
