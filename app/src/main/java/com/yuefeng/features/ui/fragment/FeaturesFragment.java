package com.yuefeng.features.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.codereview.BaseFragment;
import com.common.network.ApiService;
import com.common.utils.AppUtils;
import com.common.utils.Constans;
import com.common.utils.LogUtils;
import com.common.utils.PreferencesUtils;
import com.common.utils.ViewUtils;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.contract.FeaturesContract;
import com.yuefeng.features.presenter.FeaturesPresenter;
import com.yuefeng.features.ui.activity.JobMonitoringActivity;
import com.yuefeng.features.ui.activity.Lllegalwork.LllegalWorkActivity;
import com.yuefeng.features.ui.activity.QualityInspectionActivity;
import com.yuefeng.features.ui.activity.monitoring.MonitoringofJobActivity;
import com.yuefeng.features.ui.activity.position.PositionAcquisitionActivity;
import com.yuefeng.features.ui.activity.sngnin.ExecutiveAttendanceActivity;
import com.yuefeng.features.ui.activity.sngnin.JobAttendanceActivity;
import com.yuefeng.features.ui.activity.track.HistoryTrackActivity;
import com.yuefeng.features.ui.activity.video.VideoCameraActivity;
import com.yuefeng.home.modle.NewMsgListDataBean;
import com.yuefeng.home.ui.activity.AnnouncementListInfosActivtiy;
import com.yuefeng.home.ui.activity.HistoryAppVersionActivtiy;
import com.yuefeng.home.ui.activity.MsgListInfosActivtiy;
import com.yuefeng.home.ui.activity.WebDetailInfosActivtiy;
import com.yuefeng.home.ui.adapter.HomeMsgInfosAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Administrator on 2018/7/11.
 * 应用
 */

public class FeaturesFragment extends BaseFragment implements FeaturesContract.View {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_bg_logo)
    TextView tvBgLogo;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    Unbinder unbinder;

    //    private FeaturesMsgAdapter adapter;
    private HomeMsgInfosAdapter adapter;
    private List<NewMsgListDataBean> listData = new ArrayList<>();
    private FeaturesPresenter mPresenter;
    private String mStartTime = "";
    private String mEndTime = "";
    private boolean isGetDataAgain = false;
    private String mUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.module_fragment_features;
    }

    @Override
    protected void initView() {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        unbinder = ButterKnife.bind(this, rootView);
//        mPresenter = new FeaturesPresenter(this, (MainActivity) getActivity());

        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        initRecycleView();
        recyclerview.setFocusable(false);
        isGetDataAgain = false;
        initViewHorW();
    }

    private void initViewHorW() {
        int hight = (int) AppUtils.mScreenHeight / 4;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        ViewUtils.setTVHightOrWidth(tvBgLogo, hight, width);
    }

    @Override
    protected void fetchData() {

    }

    private void getNetDatas() {
        if (mPresenter != null) {
            String pid = PreferencesUtils.getString(getContext(), Constans.ORGID, "");
            mPresenter.getAnnouncementByuserid(ApiService.GETANNOUNCEMENTBYUSERID, pid, mStartTime, mEndTime);
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onStart() {
//        if (isGetDataAgain) {
//            getNetDatas();
//        }
        super.onStart();
    }


    //    private void initRecycleView() {
//        adapter = new FeaturesMsgAdapter(R.layout.recyclerview_item_msginfos, listData);
//        recyclerview.setAdapter(adapter);
//        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                NewMsgListDataBean dataBean = listData.get(position);
//                assert dataBean != null;
//                Intent intent = new Intent();
//                intent.setClass(Objects.requireNonNull(getActivity()), NewMsgDetailInfosActivtiy.class);
//                intent.putExtra("dataBean", dataBean);
//                startActivity(intent);
//            }
//        });
//    }

    private void initRecycleView() {
        adapter = new HomeMsgInfosAdapter(R.layout.recyclerview_item_msginfos, listData);
        recyclerview.setAdapter(adapter);
//        addNativeDatas();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                String genre = listData.get(position).getGenre();
                // genre：1就是公告，2就是超哥的信息，3是更新的
                if (genre.equals("1")) {//公告
                    intent.setClass(Objects.requireNonNull(getActivity()), AnnouncementListInfosActivtiy.class);
                } else if (genre.equals("2")) {
                    intent.setClass(Objects.requireNonNull(getActivity()), MsgListInfosActivtiy.class);
                } else {
                    intent.setClass(Objects.requireNonNull(getActivity()), HistoryAppVersionActivtiy.class);
                }
                startActivity(intent);
                isGetDataAgain = true;
            }
        });
    }

    private void addNativeDatas() {
        List<NewMsgListDataBean> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            NewMsgListDataBean bean = new NewMsgListDataBean();
            bean.setGenre(String.valueOf(i));
            bean.setContent("");
            bean.setIsread("1");
            bean.setIssuedate("");
            bean.setOrganname("");
            bean.setSubject("");
            list.add(bean);
        }

        showAdapterDatasList(list);
    }

    /*展示数据*/
    private void showAdapterDatasList(List<NewMsgListDataBean> list) {
        listData.clear();
        if (list.size() > 0) {
            listData.addAll(list);
        }
        if (adapter != null) {
            adapter.setNewData(listData);
        }
    }


//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void disposeCommonEvent(SignInEvent event) {
//        switch (event.getWhat()) {
//            case Constans.NEW_MSG_SUCCESS://展示最新消息
////                List<NewMsgListDataBean> list = (List<NewMsgListDataBean>) event.getData();
////                if (list.size() > 0) {
////                    showAdapterDatasList(list);
////                } else {
////                    showSuccessToast("无最新消息");
////                }
//                break;
//
//            case Constans.NEW_MSG_ERROR:
////                addNativeDatas();
//                break;
//
//        }
//    }


    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View v) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.rl_kaoqin, R.id.rl_operationbianji, R.id.rl_historytrack, R.id.rl_videojian, R.id.rl_historytrack1, R.id.rl_msgcollection1,
            R.id.rl_problemupload, R.id.rl_qualityxuncha, R.id.rl_operationweigui, R.id.rl_msgcollection, R.id.rl_money_manage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_videojian:
                toVideoSytemList();//视频监控
                break;
            case R.id.rl_kaoqin://作业考勤aa
                jobwebH5();
                break;
            case R.id.rl_msgcollection:
                msgcollection();//信息采集
                break;
            case R.id.rl_operationbianji://违规作业
                toIllegalWork();
                break;
            case R.id.rl_historytrack:
                toHistoryTrack();//历史轨迹
                break;
            case R.id.rl_problemupload:
                problemUpload();//作业监察
                break;
            case R.id.rl_qualityxuncha:
                qualityXuncha();
                break;
            case R.id.rl_operationweigui:
                jobMonitoring();
                break;
            case R.id.rl_money_manage:
                moneyManage(1);//新增申请
                break;
            case R.id.rl_historytrack1:
                moneyManage(2);//我的申请
                break;
            case R.id.rl_msgcollection1:
                toExecutiveAttendanceActivity();//主管考勤
                break;
        }
    }

    /*主管考勤*/
    private void toExecutiveAttendanceActivity() {
        int isAdmin = PreferencesUtils.getInt(getContext(), Constans.ISADMIN, 0);
        if (isAdmin == 0) {
            showSuccessToast("您无权限操作此功能");
            return;
        }

        String string = PreferencesUtils.getString(getContext(), Constans.EMAIL, "");
        if (!string.equals("true")) {
            showSuccessToast("您无权限操作此功能");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getContext(), ExecutiveAttendanceActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void moneyManage(int type) {
        String title = "";
        String userid = PreferencesUtils.getString(Objects.requireNonNull(getActivity()), Constans.ID, "");
        String pid = PreferencesUtils.getString(Objects.requireNonNull(getActivity()), Constans.ORGID, "");
        if (type == 1) {
            mUrl = ApiService.H5URL_BUSINESSEDIT + userid + "&userpid=" + pid;
            title = "新增申请";
        } else {
            mUrl = ApiService.H5URL_APPLYEDIT + pid;
            title = "我的申请";
        }

        moneyManage(mUrl, title);

    }

    /*资产管理*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void moneyManage(String url, String title) {

//        String orderName = PreferencesUtils.getString(Objects.requireNonNull(getActivity()), Constans.ORGNAME, "");
//        orderName = "q";   + "&orgName=" + orderName

        Intent intent = new Intent();
        LogUtils.d("=======" + url);
        intent.setClass(Objects.requireNonNull(getActivity()), WebDetailInfosActivtiy.class);
        intent.putExtra("webUrl", url);
        intent.putExtra("tiTle", title);
        startActivity(intent);
    }

    /*；违规作业*/
    private void toIllegalWork() {
        startActivity(new Intent(getActivity(), LllegalWorkActivity.class));
    }

    /*；历史轨迹*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toHistoryTrack() {
        Intent intent = new Intent();
        intent.setClass(Objects.requireNonNull(getActivity()), HistoryTrackActivity.class);
        intent.putExtra("terminalNO", "");
        intent.putExtra("TYPE", "2");
        intent.putExtra("carNum", "2");
        startActivity(intent);
    }

    /*；视频监控*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toVideoSytemList() {
        Intent intent = new Intent();
        intent.setClass(Objects.requireNonNull(getActivity()), VideoCameraActivity.class);
        intent.putExtra("terminalNO", "");
        intent.putExtra("TYPE", "2");
        startActivity(intent);
    }

    /*信息采集*/
    private void msgcollection() {
        startActivity(new Intent(getActivity(), PositionAcquisitionActivity.class));
    }

    /*作业监察*/
    private void problemUpload() {
        try {
//        startActivity(new Intent(getActivity(), MonitoringSngnInActivity.class));
            String string = PreferencesUtils.getString(getContext(), Constans.EMAIL, "");
            if (string.equals("true")) {
                startActivity(new Intent(getActivity(), MonitoringofJobActivity.class));
            } else {
                showSuccessToast("您无权限操作此功能");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startActivity(new Intent(getActivity(), ProblemUpdateActivity.class));
    }

    /*问题处理*/
    private void qualityXuncha() {
        try {
            String string = PreferencesUtils.getString(getContext(), Constans.EMAIL, "");
            if (string.equals("true")) {
                startActivity(new Intent(getActivity(), QualityInspectionActivity.class));
            } else {
                showSuccessToast("您无权限操作此功能");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*定位信息*/
    private void jobMonitoring() {
        startActivity(new Intent(getActivity(), JobMonitoringActivity.class));
    }


    /*作业考勤*/
    private void jobwebH5() {
        try {
//        startActivity(new Intent(getActivity(), WebH5ZuoyeKaoqinActivity.class));
            String string = PreferencesUtils.getString(getContext(), Constans.EMAIL, "");
            if (string.equals("true")) {
                startActivity(new Intent(getActivity(), JobAttendanceActivity.class));
            } else {
                showSuccessToast("您无权限操作此功能");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
