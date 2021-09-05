package dewz.wa.listen.listen_whatsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context) {
        super(context, "wa_data_dewz_wss.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS senders(id INTEGER primary key AUTOINCREMENT, name TEXT)");
        db.execSQL("create table IF NOT EXISTS messages(id INTEGER primary key AUTOINCREMENT, sender_id INTEGER, message TEXT)");
        db.execSQL("create table IF NOT EXISTS groups(id INTEGER primary key AUTOINCREMENT, name TEXT)");
        db.execSQL("create table IF NOT EXISTS group_messages(id INTEGER primary key AUTOINCREMENT, group_id INTEGER, sender Text, message TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void insertData(String sender, String date, String message) {
        boolean IS_SENDER_EXIST = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("senders", new String[]{"id",
                        "name"}, "name" + "=?",
                new String[]{sender}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d("TAG", "insertSender: Sender exist");
                IS_SENDER_EXIST = true;
            } else {
                IS_SENDER_EXIST = false;
            }
        } else {
            IS_SENDER_EXIST = false;
        }

        if (!IS_SENDER_EXIST) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", sender);
            long result = db.insert("senders", null, contentValues);
            Log.d("TAG", "insertData: INSERT RESULT =========> " + result);
        }

        if (cursor != null)
            cursor.close();

        Cursor cursor2 = db.query("senders", new String[]{"id",
                        "name"}, "name" + "=?",
                new String[]{sender}, null, null, null, null);
        if (cursor2 != null)
            if (cursor2.moveToFirst()) {
                boolean res = insertMessage(cursor2.getInt(0), message);
                Log.d("TAG", "insertData: MESSAGE INSERTED ======> " + res);
            }

        if (cursor2 != null)
            cursor2.close();


    }


    public boolean insertMessage(int senderId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sender_id", senderId);
        contentValues.put("message", message);
        long result = db.insert("messages", null, contentValues);
        return result != -1;
    }


    public Cursor getSenders() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT * FROM senders", null);
    }


    public Cursor getMessages() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT * FROM messages", null);
    }


    public void insertGroupData(String group, String sender, String date, String message) {
        boolean IS_GROUP_EXIST = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("groups", new String[]{"id",
                        "name"}, "name" + "=?",
                new String[]{group}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d("TAG", "Group Data: Group exist");
                IS_GROUP_EXIST = true;
            } else {
                IS_GROUP_EXIST = false;
            }
        } else {
            IS_GROUP_EXIST = false;
        }

        if (!IS_GROUP_EXIST) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", group);
            long result = db.insert("groups", null, contentValues);
            Log.d("TAG", "insertData: INSERT RESULT =========> " + result);
        }

        if (cursor != null)
            cursor.close();

        Cursor cursor2 = db.query("senders", new String[]{"id",
                        "name"}, "name" + "=?",
                new String[]{sender}, null, null, null, null);
        if (cursor2 != null)
            if (cursor2.moveToFirst()) {
                boolean res = insertGroupMessage(cursor2.getInt(0), sender, message);
                Log.d("TAG", "insertData:GROUP MESSAGE INSERTED ======> " + res);
            }

        if (cursor2 != null)
            cursor2.close();


    }


    public boolean insertGroupMessage(int groupId, String sender, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("group_id", groupId);
        contentValues.put("sender", sender);
        contentValues.put("message", message);
        long result = db.insert("group_messages", null, contentValues);
        return result != -1;
    }


}
