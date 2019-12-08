package cn.edu.bnuz.exam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.bnuz.exam.modals.ExerciseInfo;
import cn.edu.bnuz.exam.utils.SaveData;

public class TabFragment extends Fragment implements View.OnClickListener {
    private String TAG = "TabFragment";
    private boolean[] selectedButton = new boolean[]{false, false, false, false};
    private int[] buttonIdList = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4};
    private int[] optionIdList = new int[]{R.id.option1, R.id.option2, R.id.option3, R.id.option4};
    private int id;
    private String name;
    private String type;
    private View exerciseView;
    private boolean isSubmit = false;
    private double total = 0;

    public static TabFragment newInstance(ExerciseInfo exerciseInfo) {
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
        String name = exerciseInfo.getName();
        String type = exerciseInfo.getType();
        String topic = exerciseInfo.getTopic();
        String[] options = exerciseInfo.getOptions();

        View view = inflater.inflate(R.layout.single_choice, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.label);
        TextView topicTextView = (TextView) view.findViewById(R.id.topicTextView);
        Button submitButton = (Button) view.findViewById(R.id.submit);

        int tabsCount = MainActivity.getTotalTabsCount();
        boolean isEndTab = id == tabsCount - 1;

        labelTextView.setText(getLabel(type));
        topicTextView.setText(String.format("\t\t\t\t\t\t\t\t\t\t\t%s", topic));

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
        this.name = name;
        this.type = type;
        this.exerciseView = view;
        return view;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.submit:
                nextTab();
                break;
            default:
                updateButtonSelected(view, buttonId);
                break;
        }
    }

    /* 找出指定元素的下标 */
    private int findIndex(int[] arr, int target) {
        for (int index = 0; index < arr.length; index++)
            if (arr[index] == target) return index;
        return -1;
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

    /* 获取不同类型题目的label标签 */
    private String getLabel(String type) {
        String label = "未知题";
        switch (type) {
            case "single":
                label = "单选题";
                break;
            case "multi":
                label = "多选题";
                break;
            case "judge":
                label = "判断题";
                break;
        }
        return label;
    }

    private void nextTab() {
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        boolean isEnd = viewPager.getCurrentItem() == MainActivity.getTotalTabsCount() - 1;
        boolean isFinshed = isFinshed();
        /* 判断是否选择了至少一个选项 */
        if (!isFinshed) {
            Toast.makeText(getContext(), "请选择至少一个选项！", Toast.LENGTH_SHORT).show();
            return;
        }
//        MainActivity.setCurrentIndex(MainActivity.getCurrentIndex() + 1);
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

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

        MainActivity.updateAnswerSituation(this.id, isCorrect);

        if (isCorrect == 1) {
            Toast.makeText(getContext(), "回答正确", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "回答错误", Toast.LENGTH_SHORT).show();
        }

        /* 提交答案 */
        if (isEnd) {
            boolean isAllFinish = findIndex(MainActivity.getAnswerSituation(), -1) == -1;
            if (!isAllFinish) {
                Toast.makeText(getContext(), "请继续完成所有题目再提交", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(findIndex(MainActivity.getAnswerSituation(), -1));
                return;
            }
            for (int each : MainActivity.getAnswerSituation()) {
                Log.d(TAG, String.valueOf(each));
            }

            Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();

            this.showDialog();

            SaveData saveDate = new SaveData(getContext());
            saveDate.insertDatabase(name, total);
        }

        this.isSubmit = true;
    }

    private boolean isFinshed() {
        for (boolean eachButton : selectedButton) {
            if (eachButton) return true;
        }
        return false;
    }

    private void updateButtonSelected(View view, int buttonId) {
        if (this.isSubmit) return;

        Button button = view.findViewById(buttonId);
        int selectedIndex = findIndex(buttonIdList, buttonId);
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

    public void jumpToIndex() {
        Intent intent = new Intent();
        intent.setClassName("cn.edu.bnuz.exam", "cn.edu.bnuz.exam.IndexActivity");
        Bundle bundle = new Bundle();
        bundle.putString("exerciseName", name);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showDialog() {
        String dialogMessage = "";
        int[] answerSituation = MainActivity.getAnswerSituation();
        int count = MainActivity.getTotalTabsCount();
        double total = 0;

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

        alterDiaglog.setPositiveButton("确认并退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumpToIndex();
            }
        });

        alterDiaglog.setNeutralButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alterDiaglog.show();
    }
}
