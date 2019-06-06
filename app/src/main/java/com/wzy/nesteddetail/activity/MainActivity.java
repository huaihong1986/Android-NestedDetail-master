package com.wzy.nesteddetail.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.wzy.nesteddetail.R;
import com.wzy.nesteddetail.adapter.RvAdapter;
import com.wzy.nesteddetail.databinding.ActivityMainBinding;
import com.wzy.nesteddetail.model.InfoBean;
import com.wzy.nesteddetail.view.NestedScrollingDetailContainer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    RvAdapter rvAdapter;
    NestedScrollingDetailContainer scrollView;
    TextView tvNoScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scrollView =  findViewById(R.id.nested_container);
        tvNoScroll = findViewById(R.id.tv_no_scroll);
        initImmersed();
        initView();


    }

    private void initImmersed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
    }

    private int webHeight = 0;
    private int tvHeight = 0;
    private void initView() {
        initWebView();
        initSwipeRefreshLayout();
        initRecyclerView();
        initToolBarView();
        initListener();
        //演示获取高度
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webHeight = mainBinding.webContainer.getMeasuredHeight();
                tvHeight = tvNoScroll.getMeasuredHeight();
            }
        }, 300);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new  ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mainBinding.swipeRefreshLayout.setEnabled(scrollView.getScrollY()==(0));
            }
        });
    }

    private void initListener() {

        initPullRefresh();

        initLoadMoreListener();

    }



    private void initPullRefresh() {
         mainBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvAdapter.AddHeaderItem(getCommentData());

                        //刷新完成
                         mainBinding.swipeRefreshLayout.setRefreshing(false);
                    }

                }, 3000);

            }
        });
    }

    private void initLoadMoreListener() {

        mainBinding.rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==rvAdapter.getItemCount()){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rvAdapter.AddFooterItem(getCommentDataMore());
                        }
                    }, 3000);


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void initSwipeRefreshLayout() {
        mainBinding.swipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);
    }

    private void initToolBarView() {
        mainBinding.vToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBinding.nestedContainer.scrollToTarget(mainBinding.rvList);
            }
        });
    }

    private void initWebView() {
        mainBinding.webContainer.getSettings().setJavaScriptEnabled(true);
        mainBinding.webContainer.setWebViewClient(new WebViewClient());
        mainBinding.webContainer.setWebChromeClient(new WebChromeClient());
        mainBinding.webContainer.loadUrl("https://github.com/wangzhengyi/Android-NestedDetail");
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainBinding.rvList.setLayoutManager(layoutManager);
        List<InfoBean> data = getCommentData();
        rvAdapter = new RvAdapter(this, data);
        mainBinding.rvList.setAdapter(rvAdapter);
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
