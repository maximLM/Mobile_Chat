package lessmeaning.easymessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by пользователь on 03.11.2016.
 */

public class LocalDataBase extends SQLiteOpenHelper implements BaseColumns {
    String myLog = "test db";

    public static final String TABLE_NAME_APPROVED = "ApprovedTable";
    public static final String TABLE_NAME_TEMP = "TempTable";
    public static final String DB_NAME = "Chat";
    public static final String CONTENT = "Content";
    public static final String TIME = "Time";
    public static final String KEY_ID = "_id";
    public LocalDataBase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME_APPROVED + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + CONTENT + " text,"
                + TIME + " integer);");

        db.execSQL("create table " + TABLE_NAME_TEMP + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + CONTENT + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTemp(String row) {

        SQLiteDatabase chatDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONTENT , row);
        chatDB.insert(TABLE_NAME_TEMP, null, cv);
        Log.d(myLog, "temp add " + row);
        chatDB.close();
    }

    public ArrayList<Row> getApproved() {
        ArrayList<Row> row = new ArrayList();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_APPROVED , null, null, null, null, null, null);
        if(c.moveToFirst()){
            do {
                String content = c.getString(c.getColumnIndex(CONTENT));
                long time = c.getInt(c.getColumnIndex(TIME));
                Row r = new Row(content, time);
                row.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return row;
    }

    public ArrayList<Pair<String, Integer>> getTemp() {
        ArrayList<Pair <String, Integer>> alTemp = new ArrayList();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_TEMP , null, null, null, null, null, null);
        if (c.moveToFirst()){
            do {
                String content = c.getString(c.getColumnIndex(CONTENT));
                int id = (int) c.getInt(c.getColumnIndex(KEY_ID));
                alTemp.add(new Pair<String, Integer>(content, id));
            } while (c.moveToNext());
        }
        c.close();
        return alTemp;
    }

    public void deleteTempRow(int id) {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        chatDB.delete(TABLE_NAME_TEMP, KEY_ID + "=" + id, null);
        chatDB.close();
    }

    public void addApproved(ArrayList<Row> rows) {
        int size = rows.size();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < size; i++) {
            cv.put(CONTENT , rows.get(i).getContent());
            cv.put(TIME, rows.get(i).getTime());
            chatDB.insert(TABLE_NAME_APPROVED, null, cv);
        }
        chatDB.close();
    }

    public long getLastTime() {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_APPROVED, null, null, null, null, null, null);
        c.moveToLast();
        long lastTime = c.getLong(c.getColumnIndex(TIME));
        c.close();
        return  lastTime;
    }
}