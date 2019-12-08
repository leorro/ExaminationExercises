package cn.edu.bnuz.exam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.bnuz.exam.holder.ViewHolder;
import cn.edu.bnuz.exam.modals.RankInfo;
import cn.edu.bnuz.exam.R;

public class ListViewAdapter extends BaseAdapter {
    private List<RankInfo> rankInfoList;
    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context, List<RankInfo> list) {
        this.rankInfoList = list;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return rankInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return rankInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        /* 如果view没有生成过，缓存里面没有，就新建一个 */
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.listview_item, null);

            viewHolder.studentName = (TextView) view.findViewById(R.id.studentName);
            viewHolder.exerciseName = (TextView) view.findViewById(R.id.exerciseName);
            viewHolder.exerciseScore = (TextView) view.findViewById(R.id.exerciseScore);

            /* 使view跟viewHolader根据tag联系起来 */
            view.setTag(viewHolder);
        } else {
            /* 如果缓存里面有，就拿出来 */
            viewHolder = (ViewHolder) view.getTag();
        }

        RankInfo rankInfo = rankInfoList.get(index);
        viewHolder.studentName.setText(rankInfo.getStudentName());
        viewHolder.exerciseName.setText(rankInfo.getExerciseName());
        viewHolder.exerciseScore.setText(String.valueOf(rankInfo.getExerciseScore()));

        return view;
    }
}
