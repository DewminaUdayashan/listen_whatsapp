package dewz.wa.listen.listen_whatsapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB HELPER";
    String currentDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss", Locale.getDefault()).format(new Date());

    public DbHelper(@Nullable Context context) {
        super(context, "wa_msg_data_wss_saved.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS senders(id INTEGER primary key AUTOINCREMENT, name TEXT, update_at TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS messages(id INTEGER primary key AUTOINCREMENT, sender_id INTEGER, message TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS groups(id INTEGER primary key AUTOINCREMENT, name TEXT, update_at TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS group_messages(id INTEGER primary key AUTOINCREMENT, group_id INTEGER, sender Text, message TEXT)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void deleteGroup(String groupId) {
        Log.d(TAG, "deleteGroup: DELETING GROUP ====> " + groupId);
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            boolean val = DB.delete("groups", "id = ?", new String[]{
                    groupId
            }) > 0;
            Log.d(TAG, "deleteGroup: GROUP DELETEd ========> " + val);
            boolean val2 = DB.delete("group_messages", "group_id =?", new String[]{
                    groupId
            }) > 0;
            Log.d(TAG, "deleteGroup: DELETE GROUP MESSAGES ---> " + val2);
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteGroupMessage(String groupId, String messageId) {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            boolean val = DB.delete("group_messages", "group_id =? AND id = ?", new String[]{
                    groupId, messageId
            }) > 0;
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteContactMessage(String senderId, String messageId) {
        try {
            Log.d(TAG, "deleteContactMessage: JAVA DELETE THIS FOR ME ====> " + senderId + " " + messageId);
            SQLiteDatabase DB = this.getWritableDatabase();
            boolean val = DB.delete("messages", "sender_id =? AND id = ?", new String[]{
                    senderId, messageId
            }) > 0;
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteContact(String contactId) {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            boolean val = DB.delete("senders", " id = ?", new String[]{
                    contactId
            }) > 0;
            Log.d(TAG, "deleteContactMessage: DELETE SENDEr RESULT =========> " + val);
            boolean val2 = DB.delete("messages", "sender_id =?", new String[]{
                    contactId,
            }) > 0;
            Log.d(TAG, "deleteContact: SENDERM MSGS DELETED ===> " + val2);
            DB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData(String sender, String date, String message) {
        try {
            boolean IS_SENDER_EXIST = false;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("senders", new String[]{"id",
                            "name"}, "name" + "=?",
                    new String[]{sender.trim()}, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    Log.d("TAG", "insertSender: Sender exist");
                    IS_SENDER_EXIST = true;
                }
            }

            if (!IS_SENDER_EXIST) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", sender.trim());
                long result = db.insert("senders", null, contentValues);
                Log.d("TAG", "insertData: INSERT RESULT =========> " + result);
            }

            if (cursor != null)
                cursor.close();

            Cursor cursor2 = db.query("senders", new String[]{"id",
                            "name"}, "name" + "=?",
                    new String[]{sender.trim()}, null, null, null, null);
            if (cursor2 != null)
                if (cursor2.moveToFirst()) {
                    boolean res = insertMessage(cursor2.getInt(0), message);
                    Log.d("TAG", "insertData: MESSAGE INSERTED ======> " + res);
                }

            if (cursor2 != null)
                cursor2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean insertMessage(int senderId, String message) {
        try {
            //
            SQLiteDatabase rdb = this.getReadableDatabase();
            long result = -1;
            Cursor cursor2 = rdb.query("messages", new String[]{"id",
                            "message"}, "message" + "=?",
                    new String[]{message.trim()}, null, null, null, null);
            if (cursor2 != null)
                if (cursor2.moveToFirst()) {
                    Log.d("TAG", "insertData: MESSAGE DUPLICATED =================>");
                } else {
                    updateAt("senders", senderId);
                    SQLiteDatabase db = this.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("sender_id", senderId);
                    contentValues.put("message", message.trim());
                    result = db.insert("messages", null, contentValues);
                }
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Cursor getSenders() {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            return DB.rawQuery("SELECT * FROM senders", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Cursor getMessages() {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            return DB.rawQuery("SELECT * FROM messages", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void insertGroupData(String group, String sender, String date, String message) {
        try {
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

            Cursor cursor2 = db.query("groups", new String[]{"id",
                            "name"}, "name" + "=?",
                    new String[]{group}, null, null, null, null);
            if (cursor2 != null)
                if (cursor2.moveToFirst()) {
                    Log.d(TAG, "insertGroupData: Selected Group |\nId : " + cursor2.getInt(0) + "\nName : " + cursor2.getString(1) + "" +
                            "\nSender : " + sender + "\nMessage : " + message);

                    boolean res = insertGroupMessage(cursor2.getInt(0), sender, message);
                    Log.d("TAG", "insertData:GROUP MESSAGE INSERTED ======> " + res);
                }

            if (cursor2 != null)
                cursor2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean insertGroupMessage(int groupId, String sender, String message) {
        try {
            SQLiteDatabase rdb = this.getReadableDatabase();
            Cursor cursor2 = rdb.query("group_messages", new String[]{"id",
                            "message"}, "message" + "=? AND group_id =?",
                    new String[]{message.trim(), String.valueOf(groupId)}, null, null, null, null);
            if (cursor2 != null)
                if (!cursor2.moveToFirst()) {
                    updateAt("groups", groupId);
                    SQLiteDatabase db = this.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("group_id", groupId);
                    contentValues.put("sender", sender);
                    contentValues.put("message", message);
                    long result = db.insert("group_messages", null, contentValues);
                    return result != -1;
                } else {
                    Log.d(TAG, "insertGroupMessage: MESSAGE EXIST");
                }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Cursor getGroups() {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            return DB.rawQuery("SELECT * FROM groups", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Cursor getGroupMessages() {
        try {
            SQLiteDatabase DB = this.getWritableDatabase();
            return DB.rawQuery("SELECT * FROM group_messages", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void updateAt(String table, int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("update_at", currentDate);
            long res = db.update(table, values, "id = ?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
