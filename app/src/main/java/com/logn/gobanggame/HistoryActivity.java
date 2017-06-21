package com.logn.gobanggame;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.logn.gobanggame.adapter.ChessArrayAdapter;
import com.logn.gobanggame.listeners.ItemClickListener;
import com.logn.gobanggame.utils.Array2StringUtils;
import com.logn.gobanggame.utils.ChessArrayUtils;
import com.logn.gobanggame.utils.greendao.ChessArrayBean;
import com.logn.gobanggame.utils.greendao.DBManager;
import com.logn.gobanggame.views.SimpleItemDecoration;
import com.logn.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2017/6/19.
 */

public class HistoryActivity extends FragmentActivity {

    private RecyclerView recyclerView;
    private TitleBar titleBar;

    private ChessArrayAdapter adapter;
    private List<ChessArrayBean> dataList;

    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();
    }

    private void initData() {
        dataList = getData();
        adapter.setDatas(dataList);

        adapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.history_list);
        titleBar = (TitleBar) findViewById(R.id.history_title_bar);
        titleBar.setOnTitleClickListener(titleClickListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new ChessArrayAdapter(this);
        adapter.setItemClickListener(listener);

        recyclerView.setAdapter(adapter);
        //获取数据库
        dbManager = DBManager.getInstance(this);
    }

    public List<ChessArrayBean> getData() {
        List<ChessArrayBean> data = new ArrayList<>(1);
        if (dbManager != null) {
            data = dbManager.queryChessArrayList();
        }
        return data;
    }

    private ItemClickListener listener = new ItemClickListener() {

        @Override
        public void click(View view, int position, String chessBlack, String chessWhite) {
            Intent intent = new Intent(HistoryActivity.this, GameViewHistoryActivity.class);
            ChessArrayUtils.chessBlack = Array2StringUtils.getPosFromString(chessBlack);
            ChessArrayUtils.chessWhite = Array2StringUtils.getPosFromString(chessWhite);
            startActivity(intent);
        }
    };

    private TitleBar.OnTitleClickListener titleClickListener = new TitleBar.OnTitleClickListener() {
        @Override
        public void onLeftClick() {
            finish();
        }

        @Override
        public void onRightClick() {
            Toast.makeText(HistoryActivity.this, "同步成功", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTitleClick() {

        }
    };
}
