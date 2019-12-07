package cn.edu.bnuz.exam.dehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("MyDBHelper", "createcreatecreatecreatecreate");
        sqLiteDatabase.execSQL("create table IF NOT EXISTS exercise_list(_id integer primary key autoincrement, exercise_type text,exercise_topic text,exercise_name text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
