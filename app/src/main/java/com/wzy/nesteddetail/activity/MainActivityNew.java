package com.wzy.nesteddetail.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.wzy.nesteddetail.R;
import com.wzy.nesteddetail.adapter.RvAdapter;
import com.wzy.nesteddetail.model.InfoBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivityNew extends AppCompatActivity {
    RvAdapter rvAdapter;
    NestedScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        scrollView = findViewById(R.id.nested_container);
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        rvList = findViewById(R.id.recyclerview);
        initView();
    }


    private void initView() {
        initSwipeRefreshLayout();
        initRecyclerView();
        initListener();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefreshLayout.setEnabled(scrollView.getScrollY() == (0));
            }
        });
    }

    private void initListener() {

        initPullRefresh();

        initLoadMoreListener();

    }


    private void initPullRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvAdapter.AddHeaderItem(getCommentData());
                        //刷新完成
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }, 300);
            }
        });
    }

    private void initLoadMoreListener() {

        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == rvAdapter.getItemCount()) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvAdapter.AddFooterItem(getCommentDataMore());
                        }
                    }, 300);


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);
        List<InfoBean> data = getCommentData();
        rvAdapter = new RvAdapter(this, data);
        rvList.setAdapter(rvAdapter);
        Log.e("TAG","data="+rvAdapter.getItemCount());
        getWindow().getDecorView().requestLayout();
    }

    private List<InfoBean> getCommentData() {
        List<InfoBean> commentList = new ArrayList<>();
        InfoBean titleBean = new InfoBean();
        titleBean.type = InfoBean.TYPE_TITLE;
        titleBean.title = "评论列表";
        commentList.add(titleBean);
        for (int i = 0; i < 4; i++) {
            InfoBean contentBean = new InfoBean();
            contentBean.type = InfoBean.TYPE_ITEM;
            contentBean.title = "评论标题" + i;
            contentBean.content = "评论内容" + i;
            commentList.add(contentBean);
        }
        return commentList;
    }

    private List<InfoBean> getCommentDataMore() {
        List<InfoBean> commentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            InfoBean contentBean = new InfoBean();
            contentBean.type = InfoBean.TYPE_ITEM;
            contentBean.title = "评论标题" + i;
            contentBean.content = "评论内容" + i;
            commentList.add(contentBean);
        }
        return commentList;
    }
}
