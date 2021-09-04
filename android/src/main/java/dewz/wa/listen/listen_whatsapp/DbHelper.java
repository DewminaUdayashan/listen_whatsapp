package dewz.wa.listen.listen_whatsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS test(id INTEGER primary key AUTOINCREMENT, date TEXT , data TEXT, message Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
