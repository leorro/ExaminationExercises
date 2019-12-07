package cn.edu.bnuz.exam;

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

public class TabFragment extends Fragment implements View.OnClickListener {
    private String TAG = "TabFragment";
    private boolean[] selectedButton = new boolean[]{false, false, false, false};
    private int[] buttonIdList = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4};
    private int[] optionIdList = new int[]{R.id.option1, R.id.option2, R.id.option3, R.id.option4};
    private int id;
    private String type;
    private View exerciseView;
    private boolean isSubmit = false;

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
        this.type = type;
        this.exerciseView = view;
        return view;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.submit:
//                Log.d(TAG, String.valueOf(MainActivity.getCurrentIndex()));
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
        boolean isEnd = MainActivity.getCurrentIndex() == MainActivity.getTotalTabsCount() - 1;
        boolean isFinshed = isFinshed();
        /* 判断是否选择了至少一个选项 */
        if (!isFinshed) {
            Toast.makeText(getContext(), "请选择至少一个选项！", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.setCurrentIndex(MainActivity.getCurrentIndex() + 1);
        viewPager.setCurrentItem(MainActivity.getCurrentIndex());

        if (this.isSubmit) {
            Toast.makeText(getContext(), "您已经提交过了！", Toast.LENGTH_SHORT).show();
            return;
        }

        /* 保存选择结果 */
        boolean isCorrect = true;
        boolean[] exerciseAnswer = (boolean[]) MainActivity.getExerciseAnswer().get(this.id);
//        for (boolean x : exerciseAnswer) {
//            Log.d("TabFragment", String.valueOf(x));
//        }
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

                isCorrect = false;
            }
            if (selectedButton[i] && !exerciseAnswer[i]) {
                button.setBackgroundResource(R.drawable.option_error);
                button.setTextColor(Color.rgb(255, 255, 255));
                button.setText("错");
                textView.setTextColor(Color.rgb(247, 140, 160));

                isCorrect = false;
            }
        }

        MainActivity.updateAnswerSituation(this.id, isCorrect);

        if (isCorrect) {
            Toast.makeText(getContext(), "回答正确", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "回答错误", Toast.LENGTH_SHORT).show();
        }

        if (isEnd) {
            /* 提交答案 */
            Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();
            for (boolean each : MainActivity.getAnswerSituation()) {
                Log.d(TAG, String.valueOf(each));
            }
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
}
