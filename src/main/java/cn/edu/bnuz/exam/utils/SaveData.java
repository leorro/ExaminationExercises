package cn.edu.bnuz.exam.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.edu.bnuz.exam.dehelper.MyDBHelper;

public class SaveData {
    private Context context;

    public SaveData(Context context) {
        this.context = context;
    }

    public void insertDatabase(String exerciseName, double total) {
        MyDBHelper myDBHelper = new MyDBHelper(context, "exercise.db", null, 1);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();

        ContentValues exerciseInfo = new ContentValues();
        exerciseInfo.put("student_name", "学生一");
        exerciseInfo.put("exercise_name", exerciseName);
        exerciseInfo.put("exercise_score", total);
        Log.d("SaveData", String.valueOf(total));
        Cursor cursor = database.rawQuery("SELECT * FROM exercise_rank where student_name = '" + "学生一" + "'", null);
        /* 判断是否有过记录，如果有则不修改，无则添加 */
        if (cursor.getCount() == 0) {
            database.insert("exercise_rank", null, exerciseInfo);
        } else {
            /* 已经记录了成绩，无法更新 */
        }
    }
}
