package com.yuefeng.login_splash.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.common.base.codereview.BaseActivity;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.LogUtils;
import com.common.utils.PreferencesUtils;
import com.luck.picture.lib.permissions.RxPermissions;
import com.yuefeng.commondemo.R;
import com.yuefeng.contacts.modle.TokenBean;
import com.yuefeng.login_splash.contract.LoginContract;
import com.yuefeng.login_splash.event.LoginEvent;
import com.yuefeng.login_splash.model.LoginDataBean;
import com.yuefeng.login_splash.presenter.SplashPresenter;
import com.yuefeng.rongIm.RongIMUtils;
import com.yuefeng.ui.MainActivity;
import com.yuefeng.ui.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 引导界面
 */

public class SplashActivity extends BaseActivity implements LoginContract.View, RongIM.UserInfoProvider {

    @BindView(R.id.text)
    ImageView imageView;
    private LoginDataBean mLoginInfo;
    private SplashPresenter loginPresenter;

    @Override
    protected void initData() {

    }

    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
        loginPresenter = new SplashPresenter(this, this);

//        boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
//        if (!networkConnected) {
//            toLoginActivity();
//        }

        if (!isTaskRoot()) {
            finish();
            return;
        }
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        try {
            imageView.setBackgroundResource(R.drawable.bg_login);
//            sentBro();
            requestPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RongIMUtils.initUserInfoListener(this);
    }

    private void initUI() {
        try {
            String userNames = PreferencesUtils.getString(SplashActivity.this, Constans.USERNAME, "");
            String passwords = PreferencesUtils.getString(SplashActivity.this, Constans.USERPASSWORD, "");
            if (!TextUtils.isEmpty(userNames) && !TextUtils.isEmpty(passwords)) {
                loginPresenter.login(ApiService.LOGIN, userNames, passwords, Constans.ANDROID);
            } else {
                toLoginActivity();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toLoginActivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void sentBro() {
        Intent intent = new Intent();
        intent.setAction("");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);

    }

    private void initCountDown() {
        try {
            boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
            if (!networkConnected) {
                toLoginActivity();
            } else {
                initUI();

//                boolean isHaveDatas = PreferencesUtils.getBoolean(this, Constans.HAVE_USER_DATAS);
//                if (isHaveDatas) {
//                    String string = PreferencesUtils.getString(this, Constans.COOKIE_PREF);
//                    if (!TextUtils.isEmpty(string)) {//主界面
//                        toMainActivity();
//                    } else {//登录界面
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    }
//                } else {
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                }
//                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        String email = PreferencesUtils.getString(SplashActivity.this, Constans.EMAIL, "");
                        if (email.equals("true")) {//个人aa
                            LogUtils.d("======mai===");
                            if (loginPresenter != null) {
                                String name = PreferencesUtils.getString(SplashActivity.this, Constans.USERNAME_N, "");
                                String userid = PreferencesUtils.getString(SplashActivity.this, Constans.ID, "");
                                loginPresenter.getToken(userid, name,
                                        "http://testresource.hangyunejia.com/resource/uploads/file/20181212/YM1mlVZxMnpBAhM2dBiK.jpeg");
                            }
                        } else {*/


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeLoginEvent(LoginEvent loginEvent) {
        dismissLoadingDialog();
        switch (loginEvent.getWhat()) {
            case Constans.REGISTER_SUCCESS:
                if (loginEvent.getData() != null) {
                    mLoginInfo = (LoginDataBean) loginEvent.getData();
                }
                PreferencesUtils.putString(SplashActivity.this, Constans.USERNAME, mLoginInfo.getLoginid());
                PreferencesUtils.putString(SplashActivity.this, Constans.USERNAME_N, mLoginInfo.getUsername());

                PreferencesUtils.putBoolean(SplashActivity.this, Constans.HAVE_USER_DATAS, true);
                PreferencesUtils.putString(SplashActivity.this, Constans.ORGID, mLoginInfo.getOrgId());
                PreferencesUtils.putString(SplashActivity.this, Constans.TELNUM, mLoginInfo.getTelNum());
                PreferencesUtils.putString(SplashActivity.this, Constans.ID, mLoginInfo.getId());
//                LogUtils.d("=============" + loginInfo.getId());
                PreferencesUtils.putString(SplashActivity.this, Constans.EMAIL, mLoginInfo.getEmail());
                PreferencesUtils.putBoolean(SplashActivity.this, Constans.ISREG, mLoginInfo.isIsreg());
                String string = PreferencesUtils.getString(this, Constans.COOKIE_PREF);
//                String alias = MD5Utils.toString(string);
//                JPushManager.getInstance().setAliasAndTags(alias, "");//极光推送
                if (mLoginInfo.getEmail().equals("true")) {//个人
                    if (loginPresenter != null) {
                        loginPresenter.getToken(mLoginInfo.getId(), mLoginInfo.getUsername(),
                                "http://testresource.hangyunejia.com/resource/uploads/file/20181212/YM1mlVZxMnpBAhM2dBiK.jpeg");
                    }
                } else {
                    toMainActivity();
                }
                break;
            case Constans.REGISTER_ERROR:
                toLoginActivity();
                break;

            case Constans.RONGIM_SUCCESS:
                TokenBean tokenBean = (TokenBean) loginEvent.getData();
                String token = tokenBean.getData();
                PreferencesUtils.putString(SplashActivity.this, Constans.TOKEN, token);
                if (!TextUtils.isEmpty(token)) {
                    initRongIMToken(token);
                } else {
                    toMainActivity();
                }
                break;
            case Constans.RONGIM_ERROR:
                toLoginActivity();
                break;
            case Constans.RONGIM_SUCCESS_NET:
                toMainActivity();
                LogUtils.d("======开始==");
                break;
        }
    }

    /*融云连接token*/
    private void initRongIMToken(String token) {
        String userId = PreferencesUtils.getString(SplashActivity.this, Constans.ID, "");
        String name = PreferencesUtils.getString(SplashActivity.this, Constans.USERNAME_N, "");
        String portraitUrl = "";
        RongIMUtils.connectToken(SplashActivity.this,token, userId, name, portraitUrl);
//        RongIMUtils.init(userId, name, portraitUrl);

        LogUtils.d("======开始1111==");
    }

    private void toMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        try {
            RxPermissions rxPermission = new RxPermissions(SplashActivity.this);
            //请求权限全部结果 Manifest.permission.CAMERA,
            rxPermission.request(
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_PHONE_STATE,
                    //位置
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    //相机、麦克风
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.CAMERA,
                    //存储空间
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
//                            if (!granted) {
//                                showSuccessToast("App未能获取全部需要的相关权限，部分功能可能不能正常使用.");
//                            }
                            //不管是否获取全部权限，进入主页面
                            initCountDown();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        String userid = PreferencesUtils.getString(SplashActivity.this, Constans.ID, "");
        String name = PreferencesUtils.getString(SplashActivity.this, Constans.USERNAME_N, "");
        Uri parse = Uri.parse("http://testresource.hangyunejia.com/resource/uploads/file/20181212/YM1mlVZxMnpBAhM2dBiK.jpeg");
        UserInfo info = new UserInfo(userid, name, parse);
        RongIMUtils.refreshUserInfoCache(info);
        return info;
    }


    @Override
    protected int getContentViewResId() {
        return R.layout.activity_splash;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
