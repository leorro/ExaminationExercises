package cn.edu.bnuz.exam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.bnuz.exam.dehelper.MyDBHelper;
import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class MainActivity extends AppCompatActivity {
    private static int currentIndex = 0;
    private static int totalTabsCount = 0;
    private static boolean[] answerSituation;
    private static ArrayList exerciseAnswer = new ArrayList();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    private List<Fragment> mFragments;
    private List<String> mTitles;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase database;
    Cursor cursor;

    private String[] titles = new String[]{"1", "2", "3", "4", "5", "6"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
//        mViewPager.setOffscreenPageLimit(10);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                setCurrentIndex(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setTotalTabsCount(titles.length);
        initAnswerSituation();
        mTitles = new ArrayList<>();
        mTitles.addAll(Arrays.asList(titles));
        mFragments = new ArrayList<>();

        myDBHelper = new MyDBHelper(this, "exercise.db", null, 1);
        database = myDBHelper.getWritableDatabase();
        cursor = database.rawQuery("SELECT * FROM exercise_list", null);

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
            exerciseInfo.setType(type);
            exerciseInfo.setTopic(topic);
            exerciseInfo.setOptions(options);


            mFragments.add(TabFragment.newInstance(exerciseInfo));
            cursor.moveToNext();
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);//给ViewPager设置适配器
        mViewPager.setOffscreenPageLimit(titles.length);
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来
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
        answerSituation = new boolean[getTotalTabsCount()];
        for (int i = 0; i < answerSituation.length; i++) {
            answerSituation[i] = false;
        }
    }

    public static void updateAnswerSituation(int index, boolean value) {
        answerSituation[index] = value;
    }

    public static boolean[] getAnswerSituation() {
        return answerSituation;
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        MainActivity.currentIndex = currentIndex;
    }

    public static int getTotalTabsCount() {
        return totalTabsCount;
    }

    public static void setTotalTabsCount(int totalTabsCount) {
        MainActivity.totalTabsCount = totalTabsCount;
    }
}