package cn.edu.bnuz.exam;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class MainActivity extends AppCompatActivity {
    private static int currentIndex = 0;
    private static int totalTabsCount = 0;
    private static boolean[] answerSituation;
    private static ArrayList exerciseAnswer;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    private List<Fragment> mFragments;
    private List<String> mTitles;

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
        initExerciseAnswer();
        initAnswerSituation();
        for (boolean item : getAnswerSituation()) {
            Log.d("MainActivity", String.valueOf(item));
        }
        mTitles = new ArrayList<>();
        mTitles.addAll(Arrays.asList(titles));
        mFragments = new ArrayList<>();

        for (int i = 0; i < mTitles.size(); i++) {
            ExerciseInfo exerciseInfo = new ExerciseInfo();
            if (i % 2 == 0) {
                exerciseInfo.setId(i);
                exerciseInfo.setType("single");
                exerciseInfo.setTopic("这是题目这是题目这是题目这是题目这是题目这是题目这是题目这是题目这是题目");
                String[] options = new String[]{"程序", "质量", "人员", "过程"};
                exerciseInfo.setOptions(options);
            } else {
                exerciseInfo.setId(i);
                exerciseInfo.setType("multi");
                exerciseInfo.setTopic("人们常常把软件工程的方法（开发方法）、工具（支持方法的工具）、（）称为软件工程三要素。");
                String[] options = new String[]{"软件工程", "软件测试", "软件设计", "软件维护"};
                exerciseInfo.setOptions(options);
            }
            mFragments.add(TabFragment.newInstance(exerciseInfo));
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);//给ViewPager设置适配器
        mViewPager.setOffscreenPageLimit(titles.length);
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public static void initExerciseAnswer() {
        exerciseAnswer = new ArrayList();
        for (int i = 0; i < 6; i++) {
            boolean[] eachExerciseAnswer = new boolean[4];
            for (int j = 0; j < 4; j++) {
                if (j % 2 == 0)
                    eachExerciseAnswer[j] = true;
                else
                    eachExerciseAnswer[j] = false;
            }
            exerciseAnswer.add(eachExerciseAnswer);
        }
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