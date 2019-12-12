package cn.edu.bnuz.exam;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import cn.edu.bnuz.exam.adapter.ListViewAdapter;
import cn.edu.bnuz.exam.dbhelper.MyDBHelper;
import cn.edu.bnuz.exam.modals.RankInfo;

public class IndexActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exerciseName");

        MyDBHelper myDBHelper = new MyDBHelper(getBaseContext(), "exercise.db", null, 1);
        SQLiteDatabase database = myDBHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM exercise_rank where exercise_name = '" + exerciseName + "' order by exercise_score desc", null);

        listView = (ListView) findViewById(R.id.listView);
        final ArrayList<RankInfo> rankInfos = new ArrayList<>();
        cursor.moveToFirst();

        rankInfos.add(new RankInfo("学生名称", "课程名称", "得分情况"));
        while (cursor.getPosition() != cursor.getCount()) {
            String studentName = cursor.getString(2);
            exerciseName = cursor.getString(1);
            double exerciseScore = cursor.getDouble(3);

            rankInfos.add(new RankInfo(studentName, exerciseName, String.valueOf(exerciseScore)));
            cursor.moveToNext();
        }
        listView.setAdapter(new ListViewAdapter(this, rankInfos));
    }
}
