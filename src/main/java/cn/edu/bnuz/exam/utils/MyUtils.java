package cn.edu.bnuz.exam.utils;

public class MyUtils {
    /* 获取不同类型题目的label标签 */
    public static String getLabel(String type) {
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

    /* 找出指定元素的下标 */
    public static int findIndex(int[] arr, int target) {
        for (int index = 0; index < arr.length; index++)
            if (arr[index] == target) return index;
        return -1;
    }
}