package com.yuefeng.features.ui.activity.video;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babelstar.gviewer.NetClient;
import com.babelstar.gviewer.VideoView;
import com.common.base.codereview.BaseActivity;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.PreferencesUtils;
import com.common.utils.ResourcesUtils;
import com.common.utils.StatusBarUtil;
import com.common.view.popuwindow.TreesListsPopupWindow;
import com.yuefeng.cartreeList.adapter.SimpleTreeRecyclerAdapter;
import com.yuefeng.cartreeList.common.Node;
import com.yuefeng.cartreeList.common.OnTreeNodeClickListener;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.contract.VideolistVContract;
import com.yuefeng.features.event.LllegalWorkEvent;
import com.yuefeng.features.modle.carlist.CarListInfosMsgBean;
import com.yuefeng.features.presenter.VideolistVPresenter;
import com.yuefeng.features.ui.view.VideoPopupWindow;
import com.yuefeng.utils.DatasUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*单独视频*/
public class VideoCameraActivity extends BaseActivity implements VideolistVContract.View {

    private static final String TAG = "tag";
    @BindView(R.id.iv_back)
    RelativeLayout iv_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_problem)
    LinearLayout ll_problem;
    @BindView(R.id.ll_nodata)
    LinearLayout ll_nodata;
    @BindView(R.id.ll_video)
    LinearLayout ll_video;

    @BindView(R.id.tv_search_txt)
    TextView tvSearchTxt;

    @BindView(R.id.imageView1)
    VideoView mImageView1;
    @BindView(R.id.imageView2)
    VideoView mImageView2;
    @BindView(R.id.imageView3)
    VideoView mImageView3;
    @BindView(R.id.imageView4)
    VideoView mImageView4;

    private UpdateViewThread mUpdateViewThread = null;
    private UpdateViewThreadOne mUpdateViewThreadOne = null;
    private VideoPopupWindow popupWindow;

    private List<CarListInfosMsgBean> carListData = new ArrayList<>();
    private List<Node> carDatas = new ArrayList<>();
    private VideolistVPresenter presenter;
    private TreesListsPopupWindow carListPopupWindow;
    private SimpleTreeRecyclerAdapter carlistAdapter;
    private String carNumber;
    private String terminal;


    @Override
    protected int getContentViewResId() {
        return R.layout.activity_videocamera;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        View view = findViewById(R.id.space);
        view.setBackground(mActivity.getResources().getDrawable(R.drawable.title_toolbar_bg_blue));
        StatusBarUtil.setFadeStatusBarHeight(mActivity, view);
        tv_title.setText(R.string.video);
        presenter = new VideolistVPresenter(this, this);
        getCarList();
        ll_nodata.setVisibility(View.VISIBLE);
        ll_video.setVisibility(View.GONE);
    }

    /*车辆列表*/
    private void getCarList() {
        if (presenter != null) {
            String pid = PreferencesUtils.getString(this, "orgId", "");
            String userid = PreferencesUtils.getString(this, "id", "");
            presenter.getCarListInfos(ApiService.LOADVEHICLELIST, pid, userid, "0");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void disposeLllegalWorkEvent(LllegalWorkEvent event) {
        switch (event.getWhat()) {
            case Constans.CARLIST_SSUCESS:
                carListData = (List<CarListInfosMsgBean>) event.getData();
                if (carListData.size() > 0) {
                    showCarlistDatas(carListData);
                } else {
                    showSuccessToast("旗下无车辆");
                }
                break;
            default:
                break;

        }
    }

    /*展示数据*/
    private void showCarlistDatas(List<CarListInfosMsgBean> organs) {
        carDatas.clear();
        carDatas = DatasUtils.ReturnTreesDatas(organs);
    }

    /*车辆列表*/
    private void initCarlistPopupView() {
        carListPopupWindow = new TreesListsPopupWindow(this);
        carListPopupWindow.setTitleText("车辆列表");
        carListPopupWindow.setSettingText(ResourcesUtils.getString(R.string.sure));

        if (carDatas.size() > 0) {
            carListPopupWindow.recyclerview.setLayoutManager(new LinearLayoutManager(this));
            if (carlistAdapter == null) {
                carlistAdapter = new SimpleTreeRecyclerAdapter(carListPopupWindow.recyclerview, this,
                        carDatas, 1, R.drawable.tree_open, R.drawable.tree_close, true);
            } else {
                carlistAdapter.notifyDataSetChanged();
            }
            carListPopupWindow.recyclerview.setAdapter(carlistAdapter);
        }
        carlistAdapter.notifyDataSetChanged();
        carlistAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
                showSelectItemDatas();
            }

        });

        carListPopupWindow.setOnItemClickListener(new TreesListsPopupWindow.OnItemClickListener() {
            @Override
            public void onGoBack() {
                carListPopupWindow.dismiss();
            }

            @Override
            public void onSure() {
                showSelectItemDatas();
                carListPopupWindow.dismiss();
            }
        });

        carListPopupWindow.showAtLocation(ll_problem, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    /*点击车*/
    @SuppressLint("SetTextI18n")
    private void showSelectItemDatas() {
        if (carlistAdapter == null) {
            return;
        }
        final List<Node> allNodes = carlistAdapter.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isChecked()) {
                carNumber = allNodes.get(i).getName();
                terminal = allNodes.get(i).getTerminalNO();
            }
        }
        if (!TextUtils.isEmpty(terminal)) {
            if (carListPopupWindow != null) {
                carListPopupWindow.dismiss();
            }
            showVideoList(terminal);
        }
    }

    /*获取*/
    private void showVideoList(String terminal) {
        if (TextUtils.isEmpty(terminal)) {
            showSuccessToast("终端号获取失败");
        } else {
            ll_nodata.setVisibility(View.GONE);
            ll_video.setVisibility(View.VISIBLE);
            NetClient.Initialize();
            NetClient.SetDirSvr(ApiService.VIDEO_IP, ApiService.VIDEO_IP, 6605, 0);//114.55.118.196
            mImageView1.setViewInfo(terminal, terminal, 0, "CH1");
            mImageView1.StartAV();
            mImageView2.setViewInfo(terminal, terminal, 1, "CH2");
            mImageView3.setViewInfo(terminal, terminal, 2, "CH3");
            mImageView4.setViewInfo(terminal, terminal, 3, "CH4");
            mImageView2.StartAV();
            mImageView3.StartAV();
            mImageView4.StartAV();
            mUpdateViewThread = new UpdateViewThread();
            mUpdateViewThread.start();
        }
    }


    @Override
    public void initData() {
    }

    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View v) {

    }

    @OnClick({R.id.tv_search_txt, R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_txt:
                initCarlistPopupView();
                break;
            case R.id.imageView1:
                jumpZooVideoCamera(0, "CH1");
                break;
            case R.id.imageView2:
                jumpZooVideoCamera(1, "CH2");
                break;
            case R.id.imageView3:
                jumpZooVideoCamera(2, "CH3");
                break;
            case R.id.imageView4:
                jumpZooVideoCamera(3, "CH4");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void jumpZooVideoCamera(int channel, String channum) {
//        if (!TextUtils.isEmpty(num)) {
//            Intent intent = new Intent(VideoCameraActivity.this, VideoCameraZooActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("tp", num);
//            bundle.putInt("channel", channel);
//            bundle.putString("channum", channum);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
        popupWindow = new VideoPopupWindow(this);
        popupWindow.videoview.setViewInfo(terminal, terminal, channel, channum);
        popupWindow.videoview.StartAV();
        mUpdateViewThreadOne = new UpdateViewThreadOne();
        mUpdateViewThreadOne.start();
        popupWindow.setOnItemClickListener(new VideoPopupWindow.OnItemClickListener() {
            @Override
            public void onVideoDismiss() {
                mUpdateViewThreadOne.setExit(true);
                mUpdateViewThreadOne = null;
                popupWindow.videoview.StopAV();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(ll_problem, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    public class UpdateViewThread extends Thread {
        private boolean isExit = false;
        private boolean isPause = false;

        public void setExit(boolean isExit) {
            this.isExit = isExit;
        }

        public void setPause(boolean isPause) {
            this.isPause = isPause;
        }

        public void run() {
            while (!isExit) {
                try {
                    if (!isPause) {
                        mImageView1.updateView();
                        mImageView2.updateView();
                        mImageView3.updateView();
                        mImageView4.updateView();
                        Thread.sleep(20);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.isExit = true;
        }
    }

    public class UpdateViewThreadOne extends Thread {
        private boolean isExit = false;
        private boolean isPause = false;

        public void setExit(boolean isExit) {
            this.isExit = isExit;
        }

        public void setPause(boolean isPause) {
            this.isPause = isPause;
        }

        public void run() {
            while (!isExit) {
                try {
                    if (!isPause) {
                        if (popupWindow != null) {
                            popupWindow.videoview.updateView();
                        }
                        Thread.sleep(20);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.isExit = true;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mUpdateViewThread != null) {
            mUpdateViewThread.setExit(true);
            mUpdateViewThread = null;
        }
        mImageView1.StopAV();
        mImageView2.StopAV();
        mImageView3.StopAV();
        mImageView4.StopAV();
        NetClient.UnInitialize();
        super.onDestroy();
    }
}
