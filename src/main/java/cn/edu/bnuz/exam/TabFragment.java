package cn.edu.bnuz.exam;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
    private String type;
    private View exerciseView;
    private static int activedTabIndex = 0;

    public static TabFragment newInstance(ExerciseInfo exerciseInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("exerciseInfo", exerciseInfo);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /* 创建视图View，会提前创建下一个标签页的view，例如点击第二页，会生成第三页的标签页 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExerciseInfo exerciseInfo = (ExerciseInfo) getArguments().getSerializable("exerciseInfo");
        String type = exerciseInfo.getType();
        String topic = exerciseInfo.getTopic();
        String[] options = exerciseInfo.getOptions();
        View view = inflater.inflate(R.layout.single_choice, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.label);
        TextView topicTextView = (TextView) view.findViewById(R.id.topicTextView);
        Button submitButton = (Button) view.findViewById(R.id.submit);


        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tablayout);
        int tabsCount = tabLayout.getTabCount();
        boolean isEndTab = activedTabIndex == tabsCount - 2;

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

        this.type = type;
        this.exerciseView = view;
        return view;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.submit:
                nextTab(view);

//                Log.d(TAG, "ememmeme");
                break;
            default:
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

    private void nextTab(View view) {
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tablayout);
        int tabsCount = tabLayout.getTabCount();
        boolean isEnd = activedTabIndex == tabsCount - 1;
        boolean isFinshed = isFinshed();

        /* 判断是否选择了至少一个选项 */
        if (!isFinshed) {
            Toast.makeText(getContext(), "请选择至少一个选项！", Toast.LENGTH_SHORT).show();
        } else {
            /* 判断是否为最后一页 */
            if (!isEnd) {
                activedTabIndex = activedTabIndex + 1;
                tabLayout.getTabAt(activedTabIndex).select();
            } else {
                /* 提交答案 */
                Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isFinshed() {
        boolean isFinshed = false;
        for (boolean eachButton : selectedButton) {
            if (eachButton) return true;
        }
        return isFinshed;
    }
}
