package cn.edu.bnuz.exam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.bnuz.exam.modals.ExerciseInfo;
import cn.edu.bnuz.exam.utils.SaveData;
import cn.edu.bnuz.exam.utils.MyUtils;

public class TabFragment extends Fragment implements View.OnClickListener {
    private String TAG = "TabFragment";
    private boolean[] selectedButton = new boolean[]{false, false, false, false};
    private int[] buttonIdList = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4};
    private int[] optionIdList = new int[]{R.id.option1, R.id.option2, R.id.option3, R.id.option4};
    private int id;
    private String exercisename;
    private String type;
    private View exerciseView;
    private boolean isSubmit = false;
    private double total = 0;

    public static TabFragment newInstance(ExerciseInfo exerciseInfo) {
        /* 为下面的onCreateView传数据 */
        Bundle bundle = new Bundle();
        bundle.putSerializable("exerciseInfo", exerciseInfo);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExerciseInfo exerciseInfo = (ExerciseInfo) getArguments().getSerializable("exerciseInfo");
        int id = exerciseInfo.getId();
        String exercisename = exerciseInfo.getName();
        String type = exerciseInfo.getType();
        String topic = exerciseInfo.getTopic();
        String[] options = exerciseInfo.getOptions();

        /* 定义布局文件 */
        View view = inflater.inflate(R.layout.option_choice, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.label);
        TextView topicTextView = (TextView) view.findViewById(R.id.topicTextView);
        Button submitButton = (Button) view.findViewById(R.id.submit);

        int tabsCount = MainActivity.getTotalTabsCount();
        /* 判断是否为最后一页，如果是则将按钮改为“立即提交” */
        boolean isEndTab = id == tabsCount - 1;

        /* 设置习题分类：单选题/多选题 */
        labelTextView.setText(MyUtils.getLabel(type));
        /* 设置习题题目 */
        topicTextView.setText(String.format("\t\t\t\t\t\t\t\t\t\t\t%s", topic));

        /* 为四个按钮添加文本以及点击事件函数 */
        for (int index = 0; index < 4; index++) {
            int buttonId = buttonIdList[index];
            int optionId = optionIdList[index];
            String option = options[index];
            Button optionButton = (Button) view.findViewById(buttonId);
            TextView optionTextView = (TextView) view.findViewById(optionId);

            optionButton.setOnClickListener(this);
            optionTextView.setText(option);
        }

        if (isEndTab) {
            submitButton.setText("立即提交");
        }
        submitButton.setOnClickListener(this);

        this.id = id;
        this.exercisename = exercisename;
        this.type = type;
        this.exerciseView = view;
        return view;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.submit:
                /* 点击的按钮为提交或者下一题 */
                nextTab();
                break;
            default:
                /* 点击选项按钮，更新按钮状态 */
                updateButtonSelected(view, buttonId);
                break;
        }
    }

    /* 重置按钮选中状态 */
    private void resetButtonSelected() {
        View view = this.exerciseView;
        for (int index = 0; index < 4; index++) {
            int buttonId = buttonIdList[index];
            Button button = view.findViewById(buttonId);
            button.setBackgroundResource(R.drawable.option_default);
            button.setTextColor(Color.rgb(102, 102, 102));

            selectedButton[index] = false;
        }
    }

    /* 当点击下一题/立即提交所执行的函数 */
    private void nextTab() {
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        boolean isEnd = viewPager.getCurrentItem() == MainActivity.getTotalTabsCount() - 1;
        boolean isFinshed = isFinshed();
        /* 判断是否选择了至少一个选项 */
        if (!isFinshed) {
            Toast.makeText(getContext(), "请选择至少一个选项！", Toast.LENGTH_SHORT).show();
            return;
        }
        /* 标签页向后跳转一页 */
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

        /* 如果已经提交过，则提示已经提交过了，不能再更改状态 */
        if (this.isSubmit) {
            Toast.makeText(getContext(), "您已经提交过了！", Toast.LENGTH_SHORT).show();
            return;
        }

        /* 保存选择结果 */
        int isCorrect = 1;
        boolean[] exerciseAnswer = (boolean[]) MainActivity.getExerciseAnswer().get(this.id);
        View view = this.exerciseView;
        for (int i = 0; i < 4; i++) {
            int buttonId = buttonIdList[i];
            int optionId = optionIdList[i];
            Button button = view.findViewById(buttonId);
            TextView textView = view.findViewById(optionId);
            if (selectedButton[i] && exerciseAnswer[i]) {
                button.setText("对");
                continue;
            }
            if (!selectedButton[i] && exerciseAnswer[i]) {
                button.setBackgroundResource(R.drawable.option_miss);
                button.setTextColor(Color.rgb(255, 255, 255));
                button.setText("漏");
                textView.setTextColor(Color.rgb(254, 154, 139));

                isCorrect = 0;
            }
            if (selectedButton[i] && !exerciseAnswer[i]) {
                button.setBackgroundResource(R.drawable.option_error);
                button.setTextColor(Color.rgb(255, 255, 255));
                button.setText("错");
                textView.setTextColor(Color.rgb(247, 140, 160));

                isCorrect = 0;
            }
        }

        /* 更新做题情况 */
        MainActivity.updateAnswerSituation(this.id, isCorrect);

        if (isCorrect == 1) {
            Toast.makeText(getContext(), "回答正确", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "回答错误", Toast.LENGTH_SHORT).show();
        }

        /* 提交答案 */
        if (isEnd) {
            /* 判断是否全部完成，若未完成，则跳转到第一个未完成的页面 */
            boolean isAllFinish = MyUtils.findIndex(MainActivity.getAnswerSituation(), -1) == -1;
            if (!isAllFinish) {
                Toast.makeText(getContext(), "请继续完成所有题目再提交", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(MyUtils.findIndex(MainActivity.getAnswerSituation(), -1));
                return;
            }

            Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();

            /* 展示成绩分析报告弹窗 */
            this.showDialog();

            /* 添加进数据库 */
            SaveData saveDate = new SaveData(getContext());
            String username = MainActivity.getUsername();
            saveDate.insertDatabase(username, exercisename, total);
        }

        this.isSubmit = true;
    }

    /* 判断是否点击至少一个按钮 */
    private boolean isFinshed() {
        for (boolean eachButton : selectedButton) {
            if (eachButton) return true;
        }
        return false;
    }

    /* 更新按钮点击状态 */
    private void updateButtonSelected(View view, int buttonId) {
        if (this.isSubmit) return;

        Button button = view.findViewById(buttonId);
        int selectedIndex = MyUtils.findIndex(buttonIdList, buttonId);
        boolean isSelected = selectedButton[selectedIndex];

        /* 如果是单选题的话，清空按钮选中状态 */
        if (this.type.equals("single")) {
            resetButtonSelected();
        }

        /* 如果为选中，则改变样式，选中则还原样式 */
        if (!isSelected) {
            button.setBackgroundResource(R.drawable.option_active);
            button.setTextColor(Color.rgb(255, 255, 255));
            selectedButton[selectedIndex] = true;
        } else {
            button.setBackgroundResource(R.drawable.option_default);
            button.setTextColor(Color.rgb(102, 102, 102));
            selectedButton[selectedIndex] = false;
        }
    }

    /* 跳转到排行榜页面 */
    public void jumpToIndex() {
        Intent intent = new Intent();
        intent.setClassName("cn.edu.bnuz.exam", "cn.edu.bnuz.exam.IndexActivity");
        Bundle bundle = new Bundle();
        bundle.putString("exerciseName", exercisename);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /* 展示成绩分析报告对话框 */
    private void showDialog() {
        String dialogMessage = exercisename + "\n\n";
        int[] answerSituation = MainActivity.getAnswerSituation();
        int count = MainActivity.getTotalTabsCount();
        double total = 0;

        /* 判断每一题的做题情况 */
        for (int index = 0; index < answerSituation.length; index++) {
            String isCorrect = answerSituation[index] == 1 ? "正确" : "错误";
            double score = answerSituation[index] == 1 ? 100 / (count * 1.0) : 0;
            total += score;
            dialogMessage += String.format("第%d题:\t\t%s\t\t\t\t\t\t\t\t得分:\t\t%.2f\n", index + 1, isCorrect, score);
        }
        this.total = total;
        if (total < 60) {
            dialogMessage += String.format("\n成绩: %.2f，总分不及格！", total);
        } else {
            dialogMessage += String.format("\n成绩: %.2f，恭喜你及格了！", total);
        }
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(getContext());
        alterDiaglog.setIcon(R.drawable.icon);
        alterDiaglog.setTitle("成绩分析报告");
        alterDiaglog.setMessage(dialogMessage);

        alterDiaglog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumpToIndex();
            }
        });
        alterDiaglog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final String finalDialogMessage = dialogMessage;
        alterDiaglog.setNeutralButton("分享", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumpToIndex();
                shareText(getContext(), finalDialogMessage);
            }
        });

        alterDiaglog.show();
    }

    private void shareText(Context context, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "成绩报告");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(intent, "成绩报告"));
    }
}
