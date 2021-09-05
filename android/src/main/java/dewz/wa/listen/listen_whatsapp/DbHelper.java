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
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS senders(id INTEGER primary key AUTOINCREMENT, name TEXT)");
        db.execSQL("create table IF NOT EXISTS messages(id INTEGER primary key AUTOINCREMENT, sender_id INTEGER, message TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertData(String sender, String date, String message) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("senders", new String[]{"id",
                        "name"}, "name" + "=?",
                new String[]{sender}, null, null, null, null);
        if (cursor != null)
            if (cursor.moveToFirst()) {
                Log.d("TAG", "insertSender: Sender exist");
            }

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", sender);
        long result = db.insert("senders", null, contentValues);
        Log.d("TAG", "insertData: INSERT RESULT =========> " + result);
        if (cursor != null)
            cursor.close();
        // return contact
    }


    public boolean insetData(String date, String data, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("data", data);
        contentValues.put("message", message);
        long result = db.insert("test", null, contentValues);
        if (result == -1) return false;
        return true;
    }


    public Cursor getdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from test", null);
        return cursor;

    }
}
