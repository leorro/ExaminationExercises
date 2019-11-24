package cn.edu.bnuz.exam;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    //ViewPage选项卡页面列表
    private List<Fragment> mFragments;
    private List<String> mTitles;

    private String[] titles = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);

        mTitles = new ArrayList<>();
        mTitles.addAll(Arrays.asList(titles));
        mFragments = new ArrayList<>();

        for (int i = 0; i < mTitles.size(); i++) {
            ExerciseInfo exerciseInfo = new ExerciseInfo();
            if (i % 2 == 0) {
                exerciseInfo.setType("single");
                exerciseInfo.setTopic("这是题目这是题目这是题目这是题目这是题目这是题目这是题目这是题目这是题目");
                String[] options = new String[]{"程序", "质量", "人员", "过程"};
                exerciseInfo.setOptions(options);
            } else {
                exerciseInfo.setType("multi");
                exerciseInfo.setTopic("人们常常把软件工程的方法（开发方法）、工具（支持方法的工具）、（）称为软件工程三要素。");
                String[] options = new String[]{"软件工程", "软件测试", "软件设计", "软件维护"};
                exerciseInfo.setOptions(options);
            }
            mFragments.add(TabFragment.newInstance(exerciseInfo));
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_tab, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.tab_add:
//                if (mTitles.size() == titles.length) {
//                    return true;
//                }
//                mTitles.add(titles[mTitles.size()]);
//                mFragments.add(TabFragment.newInstance(mTitles.size() - 1));
//                adapter.notifyDataSetChanged();
//                mTabLayout.setupWithViewPager(mViewPager);
//                return true;
//            case R.id.tab_change:
//                //设置TabLayout的模式，系统默认模式:MODE_FIXED
//                if (mTabLayout.getTabMode() == TabLayout.MODE_FIXED) {
//                    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//                } else {
//                    mTabLayout.setTabMode(TabLayout.MODE_FIXED);
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}