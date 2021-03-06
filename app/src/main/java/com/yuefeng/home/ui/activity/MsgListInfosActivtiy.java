package com.yuefeng.home.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.codereview.BaseActivity;
import com.common.event.CommonEvent;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.PreferencesUtils;
import com.common.utils.TimeUtils;
import com.common.view.timeview.TimePickerView;
import com.yuefeng.commondemo.R;
import com.yuefeng.home.contract.MsgListInfosContract;
import com.yuefeng.home.modle.MsgDataBean;
import com.yuefeng.home.modle.MsgListDataBean;
import com.yuefeng.home.presenter.MsgListInfosPresenter;
import com.yuefeng.home.ui.adapter.MsgListsInfosAdapter;
import com.yuefeng.ui.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*信息详情List*/
@SuppressLint("Registered")
public class MsgListInfosActivtiy extends BaseActivity implements MsgListInfosContract.View, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_back)
    TextView tv_back;
    @BindView(R.id.swf_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;

    @BindView(R.id.rl_time)
    RelativeLayout rlTime;
    @BindView(R.id.iv_showtime)
    ImageView ivShowtime;

    private List<MsgListDataBean> listData = new ArrayList<>();
    private MsgListsInfosAdapter adapter;
    private MsgListInfosPresenter mPresenter;

    private static int CURPAGE = 1;
    private boolean isRefresh = false;
    private String mPid;
    private TimePickerView timePickerView;
    private String mStartTime;
    private String mEndTime;
    private int mCount = 0;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_msgdetailinfos;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mPresenter = new MsgListInfosPresenter(this, this);
        initUI();
    }

    private void initUI() {
        tv_title.setText(R.string.msg);
        tv_back.setText(R.string.back);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        initRecycleView();
//        getDataByNet();
    }

    @OnClick(R.id.tv_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }


    /*获取数据源*/
    private void getDataByNet() {
        boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
        if (!networkConnected) {
            showSuccessToast("请检查网络配置");
            return;
        }
        if (mPresenter != null) {
            mPid = PreferencesUtils.getString(this, Constans.ORGID, "");
//            mPid = "dg1168";
//            ApiRetrofit.changeApiBaseUrl(NetworkUrl.ANDROID_TEST_SERVICE_DI);
            mPresenter.getAnMentDataList(ApiService.GETDATA, mPid, mStartTime, mEndTime, CURPAGE, Constans.TEN, true);
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeCommonEvent(CommonEvent event) {
        switch (event.getWhat()) {
            case Constans.MSG_LIST_SSUCESS:
                MsgDataBean bean = (MsgDataBean) event.getData();
                assert bean != null;
                listData = bean.getData();

                if (listData.size() <= 0) {
                    if (CURPAGE < 2) {
                        showSuccessToast("暂无信息");
                    }
                }
                if (!isRefresh) {
                    adapter.setNewData(listData);
                    isRefresh = true;
                    adapter.setEnableLoadMore(false);
                } else {
                    if (listData != null) {
                        adapter.addData(listData);
                    } else {
                        adapter.loadMoreEnd(true);
                    }
                }
                mCount = bean.getCount();
                break;
            case Constans.MSG_LIST_ERROR:
                showSuccessToast("暂无信息");
//                showSuccessToast("加载失败");
                break;

        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
//        listData.clear();
        super.onStop();
    }

    private void initRecycleView() {
        adapter = new MsgListsInfosAdapter(R.layout.recyclerview_item_msgdetail, listData, MsgListInfosActivtiy.this);
        adapter.setOnLoadMoreListener(this, recyclerview);
        adapter.disableLoadMoreIfNotFullPage();
        recyclerview.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (listData.size() > 0) {
                    MsgListDataBean msgListDataBean = listData.get(position);
                    toOnlyDetailActivity(msgListDataBean);
                }
            }
        });
    }

    @Override
    protected void setLisenter() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CURPAGE = 1;
                isRefresh = false;
                boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
                if (!networkConnected) {
                    showSuccessToast("请检查网络配置");
                    return;
                }
                mPresenter.getAnMentDataList(ApiService.GETDATA, mPid, mStartTime, mEndTime, CURPAGE, Constans.TEN, false);
                adapter.setEnableLoadMore(false);
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        recyclerview.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCount > 10) {
                    ++CURPAGE;
                    boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
                    if (!networkConnected) {
                        showSuccessToast("请检查网络配置");
                        return;
                    }
                    mPresenter.getAnMentDataList(ApiService.GETDATA, mPid, mStartTime, mEndTime, CURPAGE, Constans.TEN, false);
                    adapter.loadMoreComplete();
                } else {
                    adapter.loadMoreEnd(true);
                }
            }
        }, 1000);
    }

    private void toOnlyDetailActivity(MsgListDataBean msgDataBean) {
        if (msgDataBean != null) {
            Intent intent = new Intent();
            intent.setClass(MsgListInfosActivtiy.this, OnlyMsgDetailInfosActivtiy.class);
            intent.putExtra("msgData", msgDataBean);
            startActivity(intent);
        }
    }

    @OnClick({R.id.tv_start_time, R.id.tv_end_time, R.id.tv_search, R.id.iv_showtime, R.id.swf_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start_time:
                tv_start();
                break;
            case R.id.tv_end_time:
                tv_end();
                break;
            case R.id.swf_layout:
                showSuccessToast("aaaaaaa");
                break;
            case R.id.tv_search:
                rl_search();
                break;
            case R.id.iv_showtime:
                ivShowtime.setVisibility(View.INVISIBLE);
                rlTime.setVisibility(View.VISIBLE);
                break;
        }
    }

    /*查询*/
    private void rl_search() {
        if (!TextUtils.isEmpty(mPid)) {
            rlTime.setVisibility(View.INVISIBLE);
            ivShowtime.setVisibility(View.VISIBLE);
            isRefresh = false;
            CURPAGE = 1;
            boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
            if (!networkConnected) {
                showSuccessToast("请检查网络配置");
                return;
            }
            mPresenter.getAnMentDataList(ApiService.GETDATA, mPid, tvStartTime.getText().toString().trim()
                    , tvEndTime.getText().toString().trim(), CURPAGE, Constans.FOUR, true);
        }
    }


    private void tv_start() {

        if (timePickerView == null) {
            timePickerView = new TimePickerView(this, TimePickerView.Type.ALL);
        }
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        // 时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mStartTime = TimeUtils.getTimeHourMin(date);
                if (!TextUtils.isEmpty(tvEndTime.getText().toString().trim())) {
                    boolean boolenStartEndTime = TimeUtils.getBoolenStartEndTime(mStartTime, mEndTime);
                    if (boolenStartEndTime) {
                        showSuccessToast("请重新选择时间");
                        return;
                    }
                }
                tvStartTime.setText(mStartTime);
            }
        });
        timePickerView.show();
    }

    private void tv_end() {
        if (timePickerView == null) {
            timePickerView = new TimePickerView(this, TimePickerView.Type.ALL);
        }
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        // 时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mEndTime = TimeUtils.getTimeHourMin(date);
                if (!TextUtils.isEmpty(tvStartTime.getText().toString().trim())) {
                    boolean startEndTime = TimeUtils.getBoolenStartEndTime(tvStartTime.getText().toString().trim(),
                            tvEndTime.getText().toString().trim());
                    if (startEndTime) {
                        showSuccessToast("请重新选择时间");
                        return;
                    }
                }
                tvEndTime.setText(mEndTime);
            }
        });
        timePickerView.show();
    }


    @Override
    protected void initData() {
        isRefresh = false;
        CURPAGE = 1;
        getDataByNet();
    }

    @Override
    protected void widgetClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
