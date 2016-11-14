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
    public static final String TABLE_NAME_CONVERSATION = "ConversationTable";
    public static final String DB_NAME = "Chat";
    public static final String CONVERSATION_ID = "conversationID";
    public static final String USER = "user";
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
                + CONVERSATION_ID + " integer,"
                + USER + " text,"
                + CONTENT + " text,"
                + TIME + " integer);");

        db.execSQL("create table " + TABLE_NAME_TEMP + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + CONVERSATION_ID + " integer,"
                + CONTENT + " text);");

        db.execSQL("create table " + TABLE_NAME_CONVERSATION + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + CONVERSATION_ID + " integer,"
                + USER + " text,"
                + TIME + " integer,"
                + CONTENT + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTemp(TempRow row) {

        SQLiteDatabase chatDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONVERSATION_ID, row.getConversationID());
        cv.put(CONTENT , row.getContent());
        chatDB.insert(TABLE_NAME_TEMP, null, cv);
        Log.d(myLog, "temp add " + row);
        chatDB.close();
    }

    public void addApproved(ArrayList<Row> rows) {
        int size = rows.size();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < size; i++) {
            cv.put(CONVERSATION_ID, rows.get(i).getConversationID());
            cv.put(USER, rows.get(i).getUserSender());
            cv.put(CONTENT , rows.get(i).getContent());
            cv.put(TIME, rows.get(i).getTime());
            chatDB.insert(TABLE_NAME_APPROVED, null, cv);
        }
        chatDB.close();
    }

    public void addConversations(ArrayList<Conversation> convs) {
        int size = convs.size();
        SQLiteDatabase ChatDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < size; i++){
            cv.put(CONVERSATION_ID, convs.get(i).getConversationID());
            cv.put(USER, convs.get(i).getFriend());
            cv.put(TIME, convs.get(i).getTime());
            cv.put(CONTENT, convs.get(i).getLastRow());
            ChatDB.insert(TABLE_NAME_CONVERSATION, null, cv);
        }
        ChatDB.close();
    }

    public ArrayList<Row> getApproved() {
        ArrayList<Row> row = new ArrayList();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_APPROVED , null, null, null, null, null, null);
        if(c.moveToFirst()){
            do {
                String content = c.getString(c.getColumnIndex(CONTENT));
                long time = c.getInt(c.getColumnIndex(TIME));
                String userSender = c.getString(c.getColumnIndex(USER));
                long conversationID = c.getInt(c.getColumnIndex(CONVERSATION_ID));
                Row r = new Row(conversationID, userSender, content, time);
                row.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return row;
    }

    public ArrayList<TempRow> getTemp() {
        ArrayList<TempRow> alTemp = new ArrayList();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_TEMP , null, null, null, null, null, null);
        if (c.moveToFirst()){
            do {
                long id = c.getInt(c.getColumnIndex(KEY_ID));
                long conversationID = c.getInt(c.getColumnIndex(CONVERSATION_ID));
                String content = c.getString(c.getColumnIndex(CONTENT));
                alTemp.add(new TempRow(conversationID, content, id));
            } while (c.moveToNext());
        }
        c.close();
        return alTemp;
    }

    public ArrayList<Conversation> getConversation() {
        ArrayList<Conversation> alTemp = new ArrayList();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_CONVERSATION , null, null, null, null, null, null);
        if (c.moveToFirst()){
            do {
                long conversationID = c.getInt(c.getColumnIndex(CONVERSATION_ID));
                String friend = c.getString(c.getColumnIndex(USER));
                long time = c.getInt(c.getColumnIndex(TIME));
                String lastRow = c.getString(c.getColumnIndex(CONTENT));
                alTemp.add(new Conversation(conversationID, friend, time, lastRow));
            } while (c.moveToNext());
        }
        c.close();
        return alTemp;
    }

    public void setConversations(ArrayList<Conversation> covs) throws UnsupportedOperationException{
        if (0 != count(TABLE_NAME_CONVERSATION))
            throw new UnsupportedOperationException();
        addConversations(covs);
    }

    public void setApproved(ArrayList<Row> rows) throws UnsupportedOperationException{
        if (0 != count(TABLE_NAME_APPROVED))
            throw new UnsupportedOperationException();
        addApproved(rows);
    }

    public void deleteTemp(int id) {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        chatDB.delete(TABLE_NAME_TEMP, KEY_ID + "=" + id, null);
        chatDB.close();
    }

    public void deleteEverything() {
        SQLiteDatabase hatDB = this.getWritableDatabase();
        SQLiteDatabase chatDB = this.getWritableDatabase();
        chatDB.delete(TABLE_NAME_TEMP, null, null);
        chatDB.delete(TABLE_NAME_CONVERSATION, null, null);
        chatDB.delete(TABLE_NAME_APPROVED, null, null);
        Log.d(myLog, "Temp delete");
    }

    private int count(String tableName) {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(tableName , null, null, null, null, null, null);
        int count = c.getCount();
        c.close();

        return count;
    }

    public long getLastTimeMess() {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_APPROVED, null, null, null, null, null, null);
        long lastTimeMess = 0;
        if (c.moveToLast())
            lastTimeMess = c.getLong(c.getColumnIndex(TIME));
        c.close();
        return  lastTimeMess;
    }

    public long getLastTimeConv() {
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_CONVERSATION, null, null, null, null, null, null);
        long lastTimeConv = 0;
        if (c.moveToLast())
            lastTimeConv = c.getLong(c.getColumnIndex(TIME));
        c.close();
        return  lastTimeConv;
    }

    public String getUsername() {
        String username = "";
        SQLiteDatabase chatDB = this.getWritableDatabase();
        Cursor c = chatDB.query(TABLE_NAME_CONVERSATION, null, null, null, null, null, null);
        if (c.moveToFirst())
            username = c.getString(c.getColumnIndex(USER));
        c.close();

        return username;

    }


}