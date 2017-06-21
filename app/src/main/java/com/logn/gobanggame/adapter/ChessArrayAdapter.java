package com.logn.gobanggame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logn.gobanggame.R;
import com.logn.gobanggame.listeners.ItemClickListener;
import com.logn.gobanggame.utils.TimeUtils;
import com.logn.gobanggame.utils.greendao.ChessArrayBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2017/6/19.
 */

public class ChessArrayAdapter extends RecyclerView.Adapter<ChessArrayAdapter.ChessArrayHolder> implements View.OnClickListener {

    private Context context;

    private List<ChessArrayBean> dataList;
    private ItemClickListener itemListener;

    public ChessArrayAdapter(Context context) {
        this.context = context;
        dataList = new ArrayList<>();
    }

    @Override
    public ChessArrayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ChessArrayHolder holder = new ChessArrayHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChessArrayHolder holder, int position) {
        long date = dataList.get(position).get_id();
        holder.chessTime.setText(TimeUtils.getExactTimeWithLong(date));//应该将时间戳转化为日期，此处使用的是时间戳为主键

        //将位置数据绑定到view
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void setDatas(List datas) {
        dataList = datas;
    }

    @Override
    public void onClick(View v) {
        if (itemListener != null) {
            int pos = (Integer) v.getTag();
            itemListener.click(v, pos, dataList.get(pos).getChessBlacks(), dataList.get(pos).getChessWhites());
        }
    }

    public void setItemClickListener(ItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public class ChessArrayHolder extends RecyclerView.ViewHolder {
        public TextView chessTime;

        public ChessArrayHolder(View itemView) {
            super(itemView);
            //绑定控件
            chessTime = (TextView) itemView.findViewById(R.id.tv_history_time);
        }
    }
}
