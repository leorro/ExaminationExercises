package cn.edu.bnuz.exam.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import cn.edu.bnuz.exam.MainActivity;
import cn.edu.bnuz.exam.R;
import cn.edu.bnuz.exam.TabFragment;


public class MyDialog {
    private String dialogMessage = "";

    public MyDialog() {
        int[] answerSituation = MainActivity.getAnswerSituation();
        int count = MainActivity.getTotalTabsCount();
        double total = 0;

        for (int index = 0; index < answerSituation.length; index++) {
            String isCorrect = answerSituation[index] == 1 ? "正确" : "错误";
            double score = answerSituation[index] == 1 ? 100 / (count * 1.0) : 0;
            total += score;
            dialogMessage += String.format("第%d题:\t\t%s\t\t\t\t\t\t\t\t得分:\t\t%.2f\n", index + 1, isCorrect, score);
        }

        if (total < 60) {
            dialogMessage += String.format("\n成绩: %.2f，总分不及格！", total);
        } else {
            dialogMessage += String.format("\n成绩: %.2f，恭喜你及格了！", total);
        }
    }

    public void showDialog(final Context context) {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(context);
        alterDiaglog.setIcon(R.drawable.icon);
        alterDiaglog.setTitle("成绩分析报告");
        alterDiaglog.setMessage(this.dialogMessage);

        alterDiaglog.setPositiveButton("确认并退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TabFragment tabFragment = new TabFragment();
                tabFragment.jumpToIndex();
            }

        });

        alterDiaglog.setNeutralButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "点击了不生不死", Toast.LENGTH_SHORT).show();
            }
        });

        //显示
        alterDiaglog.show();
    }

//    private void jumpToIndex() {
//        Intent intent = new Intent();
//        intent.setClassName("cn.edu.bnuz.exam", "cn.edu.bnuz.exam.IndexActivity");
//        Bundle bundle = new Bundle();
//        bundle.putString("exerciseName", "emm");
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
}
