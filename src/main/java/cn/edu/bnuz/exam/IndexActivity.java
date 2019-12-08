package cn.edu.bnuz.exam;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.bnuz.exam.adapter.ListViewAdapter;
import cn.edu.bnuz.exam.dehelper.MyDBHelper;
import cn.edu.bnuz.exam.modals.RankInfo;

public class IndexActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exerciseName");
        Log.d("IndexAcitivity", exerciseName);

        MyDBHelper myDBHelper = new MyDBHelper(getBaseContext(), "exercise.db", null, 1);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM exercise_rank where exercise_name = '" + exerciseName + "' order by exercise_score desc", null);

        listView = (ListView) findViewById(R.id.listView);
        ArrayList<HashMap<String, Object>> listItems = new ArrayList<>();
        final ArrayList<RankInfo> rankInfos = new ArrayList<>();
        cursor.moveToFirst();

        rankInfos.add(new RankInfo("课程名称", "学生名称", "得分情况"));
        while (cursor.getPosition() != cursor.getCount()) {
            String studentName = cursor.getString(1);
            exerciseName = cursor.getString(2);
            double exerciseScore = cursor.getDouble(3);

            rankInfos.add(new RankInfo(studentName, exerciseName, String.valueOf(exerciseScore)));
            cursor.moveToNext();
        }
        listView.setAdapter(new ListViewAdapter(this, rankInfos));
    }
}
