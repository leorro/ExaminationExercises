package cn.edu.bnuz.exam;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.bnuz.exam.adapter.FragmentAdapter;
import cn.edu.bnuz.exam.dbhelper.MyDBHelper;
import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class MainActivity extends AppCompatActivity {
    private static int totalTabsCount = 0;
    private static int[] answerSituation;
    private static ArrayList exerciseAnswer = new ArrayList();
    private static String username = "";
    private static String theme = "light";

    private ScrollView scrollView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    private List<Fragment> mFragments;
    private List<String> mTitles;

    private MyDBHelper myDBHelper;
    private SQLiteDatabase database;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String exercisename = intent.getStringExtra("exercisename");

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

        /* 生成标签栏的题号 */
        mTitles = new ArrayList<>();
        mFragments = new ArrayList<>();

        myDBHelper = new MyDBHelper(this, "exercise.db", null, 1);
        database = myDBHelper.getWritableDatabase();
        cursor = database.rawQuery("SELECT * FROM exercise_list where exercise_name = '" + exercisename + "'", null);

        /* 设置总共展示的题目数量 */
        setTotalTabsCount(10);
        /* 定义做题情况列表 */
        initAnswerSituation();
        /* 设置用户名 */
        setUsername(username);

        Set<Integer> randomIndexSet = getRandomExercise();
        int id = 0;
        cursor.moveToFirst();
        while (cursor.getPosition() != cursor.getCount()) {
            /* 如果不在生成的随机下标集合内，则跳过 */
            if (!randomIndexSet.contains(cursor.getPosition())) {
                cursor.moveToNext();
                continue;
            }

            ExerciseInfo exerciseInfo = new ExerciseInfo();
            String type = cursor.getString(1);
            String topic = cursor.getString(2);
            String name = cursor.getString(3);
            String[] options = cursor.getString(4).split(",");
            String[] answer = cursor.getString(5).split(",");

            /* 设置该题目的答案 */
            setExerciseAnswer(answer);

            exerciseInfo.setId(id);
            exerciseInfo.setName(name);
            exerciseInfo.setType(type);
            exerciseInfo.setTopic(topic);
            exerciseInfo.setOptions(options);

            /* 添加题号 */
            mTitles.add(String.valueOf(id + 1));
            /* 添加标签页 */
            mFragments.add(TabFragment.newInstance(exerciseInfo));

            /* 移除该随机下标 */
            randomIndexSet.remove(cursor.getPosition());
            id++;

            cursor.moveToNext();
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);
        /* 设置缓存页数，防止做题状态被更新 */
        mViewPager.setOffscreenPageLimit(mTitles.size());
        mTabLayout.setupWithViewPager(mViewPager);
        /* 设置标签页导航栏为可滚动 */
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    /* 创建菜单 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /* 监听主题选择 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.light_theme:
                setTheme("light");
                break;
            case R.id.dark_theme:
                setTheme("dark");
                break;
            default:
                break;
        }
        setTabTheme();
        return true;
    }

    private void setTabTheme() {
        int[] buttonIdList = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4};
        int[] optionIdList = new int[]{R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        for (int index = 0; index < 10; index++) {
            int darkBackgroundColor = Color.rgb(34, 34, 34);
            int darkTextColorDefault = Color.rgb(167, 167, 167);
            int darkTextColorActive = Color.rgb(255, 255, 255);
            int lightBackgroundColor = Color.rgb(255, 255, 255);
            int lightTextColorDefault = Color.rgb(102, 102, 102);
            int lightTextColorActive = Color.rgb(51, 51, 51);

            View currentView = adapter.getItem(index).getView();
            TextView topicTextView = (TextView) currentView.findViewById(R.id.topicTextView);

            if (getCurrentTheme().equals("light")) {
                mViewPager.setBackgroundColor(lightBackgroundColor);
                mTabLayout.setBackgroundColor(lightBackgroundColor);
                mTabLayout.setTabTextColors(lightTextColorDefault, lightTextColorActive);
            } else {
                mViewPager.setBackgroundColor(darkBackgroundColor);
                mTabLayout.setBackgroundColor(darkBackgroundColor);
                mTabLayout.setTabTextColors(darkTextColorDefault, darkTextColorActive);
            }

            if (getCurrentTheme().equals("light")) {
                topicTextView.setTextColor(lightTextColorDefault);
            } else {
                topicTextView.setTextColor(darkTextColorDefault);
            }

            for (int buttonId : buttonIdList) {
                Button button = (Button) currentView.findViewById(buttonId);

                Drawable.ConstantState drawable = (Drawable.ConstantState) button.getBackground().getConstantState();

                /* 判断是否已经选中，选中了则不进行改变主题 */
                if (drawable.equals(getDrawable(R.drawable.option_active).getConstantState())
                        || drawable.equals(getDrawable(R.drawable.option_error).getConstantState())
                        || drawable.equals(getDrawable(R.drawable.option_miss).getConstantState())) {
                    continue;
                }
                if (getCurrentTheme().equals("light")) {
                    button.setTextColor(lightTextColorDefault);
                    button.setBackgroundResource(R.drawable.option_default);
                } else {
                    button.setTextColor(darkTextColorDefault);
                    button.setBackgroundResource(R.drawable.option_dark);
                }
            }

            for (int optionId : optionIdList) {
                TextView optionTextView = (TextView) currentView.findViewById(optionId);
                if (getCurrentTheme().equals("light")) {
                    optionTextView.setTextColor(lightTextColorDefault);
                } else {
                    optionTextView.setTextColor(darkTextColorDefault);
                }
            }
        }
    }

    /* 生成随机题目下标集合 */
    private Set<Integer> getRandomExercise() {
        Set<Integer> hashset = new HashSet<>();
        while (true) {
            hashset.add((int) (Math.random() * cursor.getCount()));
            if (hashset.size() == 10) {
                return hashset;
            }
        }
    }

    /* 设置每道题目的答案 */
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

    /* 定义用户做题的列表，-1表示未完成，0表示错误，1表示正确 */
    public static void initAnswerSituation() {
        answerSituation = new int[getTotalTabsCount()];
        for (int i = 0; i < answerSituation.length; i++) {
            answerSituation[i] = -1;
        }
    }

    /* 更新做题情况 */
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

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MainActivity.username = username;
    }

    public static String getCurrentTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        MainActivity.theme = theme;
    }
}