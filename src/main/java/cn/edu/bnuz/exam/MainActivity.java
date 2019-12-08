package cn.edu.bnuz.exam;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bnuz.exam.adapter.FragmentAdapter;
import cn.edu.bnuz.exam.dehelper.MyDBHelper;
import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class MainActivity extends AppCompatActivity {
    private static int totalTabsCount = 0;
    private static int[] answerSituation;
    private static ArrayList exerciseAnswer = new ArrayList();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    private List<Fragment> mFragments;
    private List<String> mTitles;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase database;
    Cursor cursor;

    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mTitles = new ArrayList<>();
        mFragments = new ArrayList<>();

        myDBHelper = new MyDBHelper(this, "exercise.db", null, 1);
        database = myDBHelper.getWritableDatabase();
        cursor = database.rawQuery("SELECT * FROM exercise_list", null);

        setTotalTabsCount(cursor.getCount());
        initAnswerSituation();

        cursor.moveToFirst();
        while (cursor.getPosition() != cursor.getCount()) {
            ExerciseInfo exerciseInfo = new ExerciseInfo();
            int id = cursor.getPosition();
            String type = cursor.getString(1);
            String topic = cursor.getString(2);
            String name = cursor.getString(3);
            String[] options = cursor.getString(4).split(",");
            String[] answer = cursor.getString(5).split(",");

            setExerciseAnswer(answer);

            exerciseInfo.setId(id);
            exerciseInfo.setName(name);
            exerciseInfo.setType(type);
            exerciseInfo.setTopic(topic);
            exerciseInfo.setOptions(options);

            mTitles.add(String.valueOf(id + 1));
            mFragments.add(TabFragment.newInstance(exerciseInfo));
            cursor.moveToNext();
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mTitles.size());// 设置缓存页面数，防止点击效果被刷新
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void setExerciseAnswer(String[] answerString) {
        boolean[] eachExerciseAnswer = new boolean[]{false, false, false, false};
        for (String answerIndex : answerString) {
            eachExerciseAnswer[Integer.parseInt(answerIndex)] = true;
        }
        exerciseAnswer.add(eachExerciseAnswer);
    }

    public static ArrayList getExerciseAnswer() {
        return exerciseAnswer;
    }

    public static void initAnswerSituation() {
        answerSituation = new int[getTotalTabsCount()];
        for (int i = 0; i < answerSituation.length; i++) {
            answerSituation[i] = -1;
        }
    }

    public static void updateAnswerSituation(int index, int value) {
        answerSituation[index] = value;
    }

    public static int[] getAnswerSituation() {
        return answerSituation;
    }

    public static int getTotalTabsCount() {
        return totalTabsCount;
    }

    public static void setTotalTabsCount(int totalTabsCount) {
        MainActivity.totalTabsCount = totalTabsCount;
    }
}