package cn.edu.bnuz.exam;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.edu.bnuz.exam.modals.ExerciseInfo;

public class TabFragment extends Fragment implements View.OnClickListener {
    private String TAG = "TabFragment";
    private boolean[] selectedButton = new boolean[]{false, false, false, false};
    private int[] buttonIdList = new int[]{R.id.button1, R.id.button2, R.id.button3, R.id.button4};
    private int[] optionIdList = new int[]{R.id.option1, R.id.option2, R.id.option3, R.id.option4};
    private String type;
    private View exerciseView;

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
        String type = exerciseInfo.getType();
        String topic = exerciseInfo.getTopic();
        String[] options = exerciseInfo.getOptions();
        View view = inflater.inflate(R.layout.single_choice, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.label);
        TextView topicTextView = (TextView) view.findViewById(R.id.topicTextView);

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

        this.type = type;
        this.exerciseView = view;
        return view;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
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
}