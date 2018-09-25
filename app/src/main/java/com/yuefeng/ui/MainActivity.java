package com.yuefeng.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.common.base.codereview.BaseActivity;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.LogUtils;
import com.common.utils.PreferencesUtils;
import com.common.utils.RxHelper;
import com.common.utils.StatusBarUtil;
import com.common.utils.TimeUtils;
import com.common.view.dialog.SigninCacheSureDialog;
import com.luck.picture.lib.permissions.RxPermissions;
import com.yuefeng.book.ui.fragment.AddressbookFragment;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.ui.fragment.FeaturesFragment;
import com.yuefeng.home.ui.fragment.HomeFragment;
import com.yuefeng.login_splash.contract.SignInContract;
import com.yuefeng.login_splash.event.SignInEvent;
import com.yuefeng.login_splash.presenter.SignInPresenter;
import com.yuefeng.tack.ui.fragment.TackFragment;
import com.yuefeng.ui.base.fragment.NoSlideViewPager;
import com.yuefeng.ui.base.fragment.TabItemInfo;
import com.yuefeng.usercenter.ui.fragment.UserInfoFragment;
import com.yuefeng.utils.BdLocationUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends BaseActivity implements SignInContract.View {
    @BindView(R.id.iv_back)
    RelativeLayout iv_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindColor(R.color.titel_color)
    int coloeWhite;
    @BindColor(R.color.titel_color)
    int coloeGray;

    @BindView(R.id.viewpager)
    NoSlideViewPager viewPager;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    private MyPagerAdapter pagerAdapter;
    private List<TabItemInfo> tabItemInfos;

    @BindString(R.string.tab_main_name)
    String msg_name;
    @BindString(R.string.tab_tack_name)
    String tack_name;
    @BindString(R.string.tab_search_name)
    String features_name;
    @BindString(R.string.tab_news_name)
    String book_name;
    @BindString(R.string.tab_mine_name)
    String my_name;

    private int mTime = 10;
    private SigninCacheSureDialog sureDialog;
    private static final int BDLOCATION_TIME = 5000;
    private boolean isFirstLocation = true;
    private SignInPresenter presenter;
    private String userId;
    private String address;
    private double latitude;
    private double longitude;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
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
        presenter = new SignInPresenter(this, this);
        initViewPager();
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        iv_back.setVisibility(View.INVISIBLE);
        tv_title.setText(features_name);
    }


    @Override
    protected void initData() {
        requestPermissions();
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
                        }
                        getLocation();
                    }
                });
    }

    private void getLocation() {
        BdLocationUtil.getInstance().requestLocation(new BdLocationUtil.MyLocationListener() {
            @Override
            public void myLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    address = location.getAddrStr();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    LatLng latLng=BdLocationUtil.ConverCommonToBaidu()
                    if (isFirstLocation) {
                        isFirstLocation = false;
                        showSignInTime(longitude, latitude, address);
                    }
                } else {
                    requestPermissions();
                }
            }
        }, BDLOCATION_TIME);
    }

    /*倒计时*/
    private void showSignInTime(final double longitude, final double latitude, final String address) {
        PreferencesUtils.putBoolean(MainActivity.this, "isSignIn", true);
        if (sureDialog == null) {
            sureDialog = new SigninCacheSureDialog(this);
        }
        String time = TimeUtils.getCurrentTime();
        sureDialog.setTextTime(time);
        sureDialog.setTextAddress(address);
        sureDialog.setDeletaCacheListener(new SigninCacheSureDialog.DeletaCacheListener() {
            @Override
            public void sure() {
                sureDialog.dismiss();
                personalSignIn(longitude, latitude, address);
                PreferencesUtils.putBoolean(MainActivity.this, "isSignIn", false);

            }

            @Override
            public void cancle() {
                sureDialog.dismiss();
                PreferencesUtils.putBoolean(MainActivity.this, "isSignIn", false);
            }
        });

        if (!isFinishing()) {
            sureDialog.show();
        }
        initCountDown();
    }

    private void initCountDown() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(mTime)//计时次数
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return mTime - aLong;// 3-0 3-2 3-1
                    }
                })
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long value) {
                        //                        Logger.e("value = " + value);
                        String s = String.valueOf(value);
                        if (sureDialog != null)
                            sureDialog.setTextTimeDown(TextUtils.isEmpty(s) ? "" : "(" + s + " s)");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {//结束，打卡
                        if (sureDialog != null) {
                            sureDialog.dismiss();
                        }
                        boolean isSignIn = PreferencesUtils.getBoolean(MainActivity.this, "isSignIn");
                        LogUtils.d("===========isSignIn==" + isSignIn);
                        if (isSignIn) {
                            personalSignIn(longitude, latitude, address);
                        }
                    }
                });
    }

    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View v) {

    }
    private void initViewPager() {
        tabItemInfos = new ArrayList<>();
        HomeFragment homeFragment = new HomeFragment();
        tabItemInfos.add(new TabItemInfo(homeFragment, R.drawable.home_button_selector, R.string.tab_main_name));

        tabItemInfos.add(new TabItemInfo(new TackFragment(), R.drawable.search_button_selector, R.string.tab_tack_name));
        /*应用*/
        tabItemInfos.add(new TabItemInfo(new FeaturesFragment(), R.drawable.application_button_selector, R.string.tab_search_name));
        tabItemInfos.add(new TabItemInfo(new AddressbookFragment(), R.drawable.fuli_button_selector, R.string.tab_news_name));
        tabItemInfos.add(new TabItemInfo(new UserInfoFragment(), R.drawable.my_button_selector, R.string.tab_mine_name));

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabItemInfos, mActivity);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(tabItemInfos.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(2).select(); //默认选中某项放在加载viewpager之后
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            String titleName = "";

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    titleName = msg_name;
                } else if (position == 1) {
                    titleName = tack_name;
                } else if (position == 2) {
                    titleName = features_name;
                } else if (position == 3) {
                    titleName = book_name;
                } else {
                    titleName = my_name;
                }
                tv_title.setText(titleName);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        initTabView();
    }

    private void initTabView() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }

    }

    private void personalSignIn(double longitude, double latitude, String address) {
        userId = PreferencesUtils.getString(this, Constans.ID, "");
        presenter.signIn(ApiService.QIANDAO, userId, "", "",
                String.valueOf(longitude), String.valueOf(latitude), address, "0");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeSignInEvent(SignInEvent event) {
        switch (event.getWhat()) {
            case Constans.LOGIN:
//                String data = (String) event.getData();
//                showSuccessToast(data);
                break;
            default:
//                String dataa = (String) event.getData();
//                showErrorToast(dataa);
                break;
        }
    }

    @Override
    protected boolean isNeedTranslateBar() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pagerAdapter != null) {
            pagerAdapter.destroy();
            pagerAdapter = null;
        }
        tabItemInfos = null;
        tabLayout = null;
        EventBus.getDefault().unregister(this);
    }

}