package com.yuefeng.features.ui.activity.position;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.codereview.BaseActivity;
import com.common.network.ApiService;
import com.common.utils.AppUtils;
import com.common.utils.Constans;
import com.common.utils.ImageUtils;
import com.common.utils.LogUtils;
import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtils;
import com.common.utils.ViewUtils;
import com.common.view.popuwindow.CameraPhotoPopupWindow;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.adapter.StringSingleAdapter;
import com.yuefeng.features.contract.PositionAcquisitionContract;
import com.yuefeng.features.event.PositionAcquisitionEvent;
import com.yuefeng.features.modle.GetCaijiTypeMsgBean;
import com.yuefeng.features.presenter.PositionAcquisitionPresenter;
import com.yuefeng.features.ui.view.MsgCollectionPopupWindow;
import com.yuefeng.photo.adapter.GridImageAdapter;
import com.yuefeng.photo.other.FullyGridLayoutManager;
import com.yuefeng.photo.utils.PictureSelectorUtils;
import com.yuefeng.ui.MyApplication;
import com.yuefeng.utils.BdLocationUtil;
import com.yuefeng.utils.LocationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/*定位采集*/
public class PositionAcquisitionActivity extends BaseActivity implements PositionAcquisitionContract.View {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_root_position)
    LinearLayout ll_root_position;
    @BindView(R.id.mapview)
    TextureMapView mapview;
    @BindView(R.id.tv_title_setting)
    TextView tvTitleSetting;
    @BindView(R.id.rly_home_title)
    RelativeLayout rlyHomeTitle;
    @BindView(R.id.recyclerview_left)
    TextView recyclerviewLeft;
    @BindView(R.id.recyclerview_right)
    TextView recyclerviewRight;

    @BindView(R.id.rl_select_type)
    RelativeLayout rlSelectType;

    @BindView(R.id.btn_beginorstop)
    Button btnBeginorstop;

    @BindView(R.id.viewstub_ct)
    ViewStub viewstubCt;

    @BindView(R.id.viewstub_timer)
    ViewStub viewstubTimer;
    @BindView(R.id.viewstub_finish)
    ViewStub viewstubFinish;

    private Chronometer ctTimerCircle;

    private RelativeLayout rl_time;
    TextView tvTimer;
    TextView tvCarryon;
    TextView tvFinish;

    private LinearLayout ll_timer;
    private RelativeLayout rl_select_start;
    private LinearLayout ll_finish;

    private TextView tvTimeDistance;
    private TextView tvQuduanType;
    private TextView tvRelease;
    private EditText edtMsgName;
    private RecyclerView recyclerPhoto;


    private boolean isFirstLoc = true;
    private BaiduMap baiduMap;
    private MarkerOptions ooA;
    private Marker mMarker;
    BitmapDescriptor personalImage = BitmapDescriptorFactory.fromResource(R.drawable.worker);
    BitmapDescriptor beginImage = BitmapDescriptorFactory.fromResource(R.drawable.start);
    BitmapDescriptor endImage = BitmapDescriptorFactory.fromResource(R.drawable.destination);
    private double latitude;
    private double longitude;
    private String address;

    private CameraPhotoPopupWindow popupWindow;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;

    private String mImagesArrays;
    private int current = 0;
    private long mRecordTime;
    private LatLng latLng;
    private LatLng latLngTemp = null;
    /*距离*/
    private double distance = 0;
    /*条件选择框*/
    List<LatLng> points = new ArrayList<>();
    private Polyline mPolyline;
    private String timeLong;
    private String infrastructureStr;//基础设施
    private String workAreaStr;//作业区段
    private String type = "";
    private MsgCollectionPopupWindow msgPopupWindow;
    private StringSingleAdapter singleAdapter;
    private List<GetCaijiTypeMsgBean> listData = new ArrayList<>();
    private List<GetCaijiTypeMsgBean> listLine = new ArrayList<>();
    private List<GetCaijiTypeMsgBean> listPoint = new ArrayList<>();
    private String typeWhat;
    private PositionAcquisitionPresenter presenter;

    /*是否采集*/
    private boolean isPositionAcquisition = false;
    /*选择网格还是路段*/
    private int typePosition = 0;
    private List<GetCaijiTypeMsgBean> listType = new ArrayList<>();
    private String typeId;
    private String area;
    private String lnglat = "";
    private boolean isFirstComeIn = true;
    private boolean isFirstInit = true;
    private boolean isFirstInitCt = true;
    private boolean isFirstInitFinish = true;
    private String thirtyTwoId = "";

    public MoLocationListenner myListener = new MoLocationListenner();
    private LocationClient mLocClient;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_positionacquistion;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        tv_title.setText(R.string.position_acquisition);
        presenter = new PositionAcquisitionPresenter(this, this);
        infrastructureStr = "";
        workAreaStr = "";
        initRlType();
        isFirstInit = true;
        isFirstInitCt = true;
        isFirstInitFinish = true;
        thirtyTwoId = "";
    }


    /*获取采集类型*/
    private void getCaijiType() {
        if (presenter != null) {
            presenter.getCaijiType(ApiService.GETCAIJITYPE);
        }
    }

    private void initChronometer() {
        ctTimerCircle.setBase(SystemClock.elapsedRealtime());
        ctTimerCircle.setFormat("%s");
    }

    private void initRlType() {
        tvTitleSetting.setText(R.string.history);
        ViewUtils.setRLHightOrWidth(rlSelectType, (int) AppUtils.mScreenHeight / 4, ActionBar.LayoutParams.MATCH_PARENT);
    }


    private void ininViewStubTimer() {
        if (isFirstInit) {
            View view = viewstubTimer.inflate();
            rl_time = view.findViewById(R.id.rl_time);
            ll_timer = view.findViewById(R.id.ll_timer);
            tvTimer = view.findViewById(R.id.tv_timer);
            tvCarryon = view.findViewById(R.id.tv_carryon);
            tvCarryon.setOnClickListener(this);
            tvFinish = view.findViewById(R.id.tv_finish);
            tvFinish.setOnClickListener(this);
            isFirstInit = false;
        }
    }

    private void ininViewStubCt() {
        if (isFirstInitCt) {
            View view = viewstubCt.inflate();
            rl_select_start = view.findViewById(R.id.rl_select_start);
            rl_select_start.setOnClickListener(this);
            ctTimerCircle = view.findViewById(R.id.ct_timer_circle);
            initChronometer();
            isFirstInitCt = false;
        }
    }

    private void ininViewStubFinish() {
        if (isFirstInitFinish) {
            isFirstInitFinish = false;
            View view = viewstubFinish.inflate();
            tvTimeDistance = view.findViewById(R.id.tv_time_distance);
            tvQuduanType = view.findViewById(R.id.tv_quduan_type);
            edtMsgName = view.findViewById(R.id.edt_msg_name);
            recyclerPhoto = view.findViewById(R.id.recycler_photo);
            ll_finish = view.findViewById(R.id.ll_finish);
            tvRelease = view.findViewById(R.id.tv_release);
            tvRelease.setOnClickListener(this);
        }
    }


    @Override
    protected void initData() {
        requestPermissions();
        getCaijiType();
    }

    /**
     * 百度地图定位的请求方法 拿到国、省、市、区、地址
     */
    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (!granted) {
                            showSuccessToast("App未能获取相关权限，部分功能可能不能正常使用.");
                        } else {
                            getLocation();
                        }
                    }
                });
    }

    /**
     * 定位SDK监听函数
     */
    public class MoLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapview == null) {
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr();
            if ((longitude > 140.0) || (longitude < 65.0) || (latitude > 56.0) || (latitude < 12.0)) {
                LogUtils.d("坐标异常");
            } else {
                firstLocation(latitude, longitude, address);
            }
        }
    }

    /*定位*/
    private void getLocation() {
        baiduMap = mapview.getMap();
        // 地图初始化
        mapview.showZoomControls(false);// 缩放控件是否显示
        // 开启定位图层
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(20000);
        option.setIsNeedAddress(true);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        mLocClient.start();

        baiduMap.showMapPoi(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void firstLocation(double latitude, double longitude, String address) {
        try {
            if (!TextUtils.isEmpty(address) && address.contains(getString(R.string.CHINA))) {
                int length = address.length();
                address = address.substring(2, length);
            }
            latLng = new LatLng(latitude, longitude);
            if (isFirstLoc) {
                isFirstLoc = false;
                latLngTemp = new LatLng(latitude, longitude);
                MapStatus ms = new MapStatus.Builder().target(latLng)
                        .overlook(-20).zoom(Constans.BAIDU_ZOOM_EIGHTEEN).build();
                ooA = new MarkerOptions().icon(personalImage).zIndex(10);
                ooA.position(latLng);
//                ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                mMarker = null;
                mMarker = (Marker) (baiduMap.addOverlay(ooA));
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
//            BdLocationUtil.MoveMapToCenter(baiduMap, latLngTemp, 14);
                PreferencesUtils.putString(MyApplication.getContext(), "Fengrun", address);
                PreferencesUtils.putString(MyApplication.getContext(), "mAddress", address);
                PreferencesUtils.putString(PositionAcquisitionActivity.this, Constans.STARTADDRESS, address);
            }
            PreferencesUtils.putString(PositionAcquisitionActivity.this, Constans.ENDADDRESS, address);
            starDrawTrackLine(latLng);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void starDrawTrackLine(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (points.size() > 0) {
            LatLng latLngDis = points.get(points.size() - 1);
            double distance = DistanceUtil.getDistance(latLngDis, latLng);
            LogUtils.d("距离====" + distance);
            drawTrackLine(latLng);
//            if (Math.abs(distance) < 1000) {
//            }
        } else {
            drawTrackLine(latLng);
        }
    }

    /*划线*/
    private void drawTrackLine(LatLng latLng) {
        try {
            if (isPositionAcquisition) {
                uploadLngLat(latLng);
                points.add(latLng);//如果要运动完成后画整个轨迹，位置点都在这个集合中
                if (points.size() > 1 && baiduMap != null) {
                    //清除上一次轨迹，避免重叠绘画
                    drawLineIntoBaiduMap(baiduMap, points, true);
                }
            } else {
                points.clear();
                points.add(latLng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadLngLat(LatLng latLng) {
        String type = "";
        double lng = latLng.longitude;
        double lat = latLng.latitude;
        if (TextUtils.isEmpty(thirtyTwoId)) {
            thirtyTwoId = StringUtils.get32String(Constans.THIRTYTWO);
            PreferencesUtils.putString(PositionAcquisitionActivity.this, Constans.THIRTYTWOID, thirtyTwoId);
        }
        if (typePosition == 1) {
            type = Constans.TYPE_LATLNG_LINE;
        } else if (typePosition == 0) {
            type = Constans.TYPE_LATLNG_MR;
        }
        if (presenter != null) {
            presenter.uploadLnglat(ApiService.UPLOADLNGLAT, type, String.valueOf(lng), String.valueOf(lat), thirtyTwoId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposePositionAcquisitionEvent(PositionAcquisitionEvent event) {
        switch (event.getWhat()) {
            case Constans.MSGCOLECTION_SSUCESS:
                thirtyTwoId = "";
                showSuccessDialog("上传成功，是否退出当前界面?");
                break;
            case Constans.MSGCOLECTION_ERROR://上传失败

                break;
            case Constans.GETCAIJI_SSUCESS://获采集类型成功
                listType = (List<GetCaijiTypeMsgBean>) event.getData();
                if (listType.size() > 0) {
                    initCaijiTypeData(listType);
                }
                break;
            case Constans.GETCAIJI_ERROR://获采集类型失败
//                showSuccessToast("发布失败");
                break;
            case Constans.UPLOADLNGLAT_SSUCESS:
//                showSuccessToast("成功");
                break;
            case Constans.UPLOADLNGLAT_ERROR:
//                showSuccessToast("发布失败");
                break;

        }
    }

    /*采集类型数据*/
    private void initCaijiTypeData(List<GetCaijiTypeMsgBean> listType) {
        listPoint.clear();
        listLine.clear();
        for (GetCaijiTypeMsgBean typeMsgBean : listType) {
            String code = typeMsgBean.getCode();
            if (code.equals("1")) {
                listLine.add(typeMsgBean);
            } else {
                listPoint.add(typeMsgBean);
            }
        }
    }

    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View view) {
        switch (view.getId()) {
            case R.id.tv_carryon://继续
                tv_carryon();
                break;
            case R.id.tv_finish://结束
                ininViewStubFinish();
                tv_finish("1");
                break;
            case R.id.rl_select_start://点击暂停
                ininViewStubTimer();
                cvTimerStop();
                break;
            case R.id.tv_release:
                tv_release();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        assert mapview != null;
        mapview.getMap().clear();
        mapview.onDestroy();
        mapview = null;
        beginImage.recycle();
        endImage.recycle();
    }

    @OnClick({R.id.btn_beginorstop, R.id.recyclerview_left, R.id.recyclerview_right, R.id.tv_title_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_beginorstop://开始采集
                beginOrStop();
                break;
            case R.id.recyclerview_left://基础设施
                initInFrastDatas();
                initMsgPopuwindow(recyclerviewLeft, true);
                workAreaStr = "";
                recyclerviewRight.setText(workAreaStr);
                break;
            case R.id.recyclerview_right://作业区段:
                infrastructureStr = "";
                recyclerviewLeft.setText(infrastructureStr);
                initWorkAreaDatas();
                initMsgPopuwindow(recyclerviewRight, false);
                break;
            case R.id.tv_title_setting://历史
                history();
                break;
        }
    }

    /*历史*/
    private void history() {
        startActivity(new Intent(PositionAcquisitionActivity.this, HistoryPositionAcqusitionActivity.class));
    }

    /*作业区段数据*/
    private void initWorkAreaDatas() {
        listData.clear();
        if (listLine.size() > 0) {
            listData.addAll(listLine);
        } else {
            if (presenter != null) {
                List<GetCaijiTypeMsgBean> list = presenter.initWorkArea();
                listData.addAll(list);
            }

        }
        if (singleAdapter != null) {
            singleAdapter.setNewData(listData);
        }
    }

    /*基础设施数据*/
    private void initInFrastDatas() {
        listData.clear();
        if (listPoint.size() > 0) {
            listData.addAll(listPoint);

        } else {
            if (presenter != null) {
                List<GetCaijiTypeMsgBean> list = presenter.initInFrast();
                listData.addAll(list);
            }
        }
        if (singleAdapter != null) {
            singleAdapter.setNewData(listData);
        }
    }

    /*选择基础设施*/
    private void initMsgPopuwindow(View parent, final boolean isWhatType) {
        msgPopupWindow = new MsgCollectionPopupWindow(this);
        msgPopupWindow.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        singleAdapter = new StringSingleAdapter(R.layout.list_item_string, listData);
        msgPopupWindow.recyclerview.setAdapter(singleAdapter);
        singleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                typeWhat = listData.get(position).getData();
                typeId = listData.get(position).getOrderNum();

                if (msgPopupWindow != null) {
                    msgPopupWindow.dismiss();
                }
                if (TextUtils.isEmpty(typeWhat)) {
                    showSuccessToast("类型未知");
                    return;
                }
                if (isWhatType) {
                    typePosition = 5;
                    recyclerviewLeft.setText(typeWhat);
                } else {
                    recyclerviewRight.setText(typeWhat);
                }
                if (typeWhat.equals("线路")) {
                    typePosition = 1;
                } else {
                    typePosition = 0;
                }
            }
        });
        msgPopupWindow.showPopuWindow(parent);
    }

    /*暂停*/
    private void cvTimerStop() {
        timeLong = ctTimerCircle.getText().toString().trim();
        if (rl_select_start != null && ll_timer != null) {
            rl_select_start.setVisibility(View.INVISIBLE);
            ll_timer.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(timeLong)) {
            tvTimer.setText(timeLong);
        }
        isPositionAcquisition = false;
        initCtStop();
    }


    /*发布*/
    private void tv_release() {
        if (presenter != null && points.size() > 0) {

            String pid = PreferencesUtils.getString(this, Constans.ORGID, "");
            String userId = PreferencesUtils.getString(this, Constans.ID, "");
            String tyepName = tvQuduanType.getText().toString().trim();
            String name = edtMsgName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                showSuccessToast("请填写名称");
                return;
            }
            if (tyepName.equals("线路") || tyepName.equals("网格")) {
                lnglat = "";
            } else {
                lnglat = presenter.getLnglatStr(points);
            }
            LogUtils.d("=========" + lnglat + " ++ " + thirtyTwoId);
            thirtyTwoId = PreferencesUtils.getString(PositionAcquisitionActivity.this, Constans.THIRTYTWOID, "");
            presenter.upLoadmapInfo(ApiService.UPLOADMAPINFO, pid, userId, typeId, tyepName, name, lnglat,
                    area, mImagesArrays, thirtyTwoId);
        }
    }

    /*结束*/
    @SuppressLint("SetTextI18n")
    private void tv_finish(String typeDistance) {
        if (ll_timer != null) {
            ll_timer.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(infrastructureStr)) {
            type = infrastructureStr;
        }
        if (!TextUtils.isEmpty(workAreaStr)) {
            type = workAreaStr;
        }
        if (ll_finish != null) {
            ll_finish.setVisibility(View.VISIBLE);
        }
        if (tvQuduanType != null) {
            tvQuduanType.setText(type);
        }
        if (recyclerPhoto != null) {
            selectPhoto();
        }
        if (typeDistance.equals("2")) {
            if (TextUtils.isEmpty(address)) {
                address = PreferencesUtils.getString(PositionAcquisitionActivity.this, Constans.ADDRESS, "");
            }
            tvTimeDistance.setText("本次采集地址:" + address);
        } else {
            initAreaOrDistance();
        }
    }

    /*距离或者面积*/
    private void initAreaOrDistance() {
        area = "";
        if (points.size() > 2) {
            if (typePosition == 0) {//网格
                points.add(new LatLng(points.get(0).latitude, points.get(0).longitude));
                area = LocationUtils.getArea(points);//面积
            } else {//路段
                for (int i = 0; i < points.size(); i++) {
                    if (latLngTemp != null) {
                        distance = distance + DistanceUtil.getDistance(latLngTemp, points.get(i));
                    }
                    latLngTemp = points.get(i);
                }
                distance = distance / 1000.0;
                area = StringUtils.getStringDistance(distance);
            }

        }
        if (presenter != null) {
            String time = presenter.showHowLongTime(timeLong, area, typePosition);
            if (tvTimeDistance != null) {
                tvTimeDistance.setText(time);
            }
        }
        if (area.equals("NaN")) {
            area = "0";
        }
        //起始点图层也会被清除，重新绘画
        if (points.size() > 1 && baiduMap != null) {
            drawLineIntoBaiduMap(baiduMap, points, false);

            MarkerOptions oEnd = new MarkerOptions();
            oEnd.position(points.get(points.size() - 2));
            oEnd.icon(endImage);
            baiduMap.addOverlay(oEnd);
        }
    }

    /*划线*/
    private void drawLineIntoBaiduMap(BaiduMap baiduMap, List<LatLng> points, boolean isFirstDraw) {
        //清除上一次轨迹，避免重叠绘画
        baiduMap.clear();
        //起始点图层也会被清除，重新绘画
        MarkerOptions oStart = new MarkerOptions();
        oStart.position(points.get(0));
        oStart.icon(beginImage);
        baiduMap.addOverlay(oStart);

        OverlayOptions ooPolyline = new PolylineOptions().width(12).color(Color.RED).points(points);
        mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
        if (isFirstDraw) {
            if (points.size() >= 2) {
                OverlayOptions markerOptions;
                markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)).position(points.get(points.size() - 1))
                        .rotate((float) BdLocationUtil.getAngle(mPolyline, 0));
                mMarker = (Marker) baiduMap.addOverlay(markerOptions);
            }
        }

        BdLocationUtil.MoveMapToCenter(baiduMap, points.get(points.size() - 1), Constans.BAIDU_ZOOM_EIGHTEEN);
    }

    /*继续采集*/
    private void tv_carryon() {
        if (ll_timer != null && rl_select_start != null) {
            ll_timer.setVisibility(View.INVISIBLE);
            rl_select_start.setVisibility(View.VISIBLE);
        }
        isPositionAcquisition = true;
        initCtStart();
    }

    /*开始采集*/
    private void beginOrStop() {
        infrastructureStr = recyclerviewLeft.getText().toString().trim();
        workAreaStr = recyclerviewRight.getText().toString().trim();
        if (TextUtils.isEmpty(infrastructureStr) && TextUtils.isEmpty(workAreaStr)) {
            showSuccessToast("请选择一种标注类型");
            return;
        }
        points.add(new LatLng(latitude, longitude));
        if (!TextUtils.isEmpty(workAreaStr)) {//选择作业区段
            ininViewStubCt();
            isPositionAcquisition = true;
            initCtStart();
            rlSelectType.setVisibility(View.INVISIBLE);
            btnBeginorstop.setVisibility(View.INVISIBLE);
            if (rl_select_start != null) {
                rl_select_start.setVisibility(View.VISIBLE);
            }
        } else {//选择基础设施
            ininViewStubFinish();
            tv_finish("2");
            rlSelectType.setVisibility(View.INVISIBLE);
            btnBeginorstop.setVisibility(View.INVISIBLE);
            isPositionAcquisition = false;
        }
    }

    /*计时器start*/
    private void initCtStart() {

        if (ctTimerCircle != null)
            if (mRecordTime != 0) {
                ctTimerCircle.setBase(ctTimerCircle.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
            } else {
                ctTimerCircle.setBase(SystemClock.elapsedRealtime());
            }
        assert ctTimerCircle != null;
        ctTimerCircle.start();
    }

    /*计时器stop*/
    private void initCtStop() {
        if (ctTimerCircle != null) {
            ctTimerCircle.stop();
        }
        mRecordTime = SystemClock.elapsedRealtime();
    }

    /*图片选择*/
    private void selectPhoto() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(PositionAcquisitionActivity.this, Constans.THREE, GridLayoutManager.VERTICAL, false);
        recyclerPhoto.setLayoutManager(manager);
        adapter = new GridImageAdapter(PositionAcquisitionActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(Constans.THREE);
        recyclerPhoto.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PositionAcquisitionActivity.this).externalPicturePreview(position, selectList);
                            break;
                    }
                }
            }
        });
        delectSelectPhotos();
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            initPopupView();
        }
    };

    /*拍照，图片*/
    private void initPopupView() {
        popupWindow = new CameraPhotoPopupWindow(this);
        popupWindow.setOnItemClickListener(new CameraPhotoPopupWindow.OnItemClickListener() {
            @Override
            public void onCaremaClicked() {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
//                onCarema();
                PictureSelectorUtils.getInstance().onAcCamera(PositionAcquisitionActivity.this,
                        PictureSelectorUtils.getInstance().type, Constans.THREE, selectList);
            }

            @Override
            public void onPhotoClicked() {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
//                onPhoto();
                PictureSelectorUtils.getInstance().onAcAlbum(PositionAcquisitionActivity.this,
                        PictureSelectorUtils.getInstance().type, Constans.THREE, Constans.FOUR,
                        true, ImageUtils.getPath(), selectList);
            }

            @Override
            public void onCancelClicked() {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.showAtLocation(ll_root_position, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
    private void delectSelectPhotos() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PositionAcquisitionActivity.this);
                } else {
                    showSuccessToast(getString(R.string.picture_jurisdiction));
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    showPhotos(data);
                    break;
            }
        }
    }

    /*展示图片*/
    private void showPhotos(Intent data) {
        // 图片选择结果回调
        selectList = PictureSelector.obtainMultipleResult(data);
        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
        if (selectList.size() <= 0) {
            return;
        }
        assert adapter != null;
        adapter.setList(selectList);
        adapter.notifyDataSetChanged();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingDialog(getString(R.string.photo_processing));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String string = PreferencesUtils.getString(AppUtils.getContext(), Constans.ENDADDRESS, "");
                mImagesArrays = PictureSelectorUtils.compressionPhotos(PositionAcquisitionActivity.this, selectList, string);
            }
        }).start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
            }
        });
        if (!TextUtils.isEmpty(mImagesArrays)) {
        }
    }

}
