package com.yuefeng.login_splash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.common.base.codereview.BaseActivity;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.PreferencesUtils;
import com.yuefeng.commondemo.R;
import com.yuefeng.login_splash.contract.LoginContract;
import com.yuefeng.login_splash.event.LoginEvent;
import com.yuefeng.login_splash.model.LoginDataBean;
import com.yuefeng.login_splash.presenter.LoginPresenter;
import com.yuefeng.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class LoginActivity extends BaseActivity implements LoginContract.View {

    private EditText accountEt, passwordEt;
    private TextView loginBt;
    private LoginPresenter loginPresenter;
    private LoginDataBean loginInfo;
//    private AlertDialog alertDilaog;
    private String userNames;
    private String passwords;
    private Button btn_eye_type;
    private CheckBox cb_pwd;
    private boolean cheche_pwd = false;
    private boolean isRemberPwd;

    @Subscribe
    @Override
    protected void initView(Bundle savedInstanceState) {
        loginPresenter = new LoginPresenter(this, this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        accountEt = findViewById(R.id.et_account);
        passwordEt = findViewById(R.id.et_password);
        loginBt = findViewById(R.id.btn_login);
        btn_eye_type = findViewById(R.id.btn_eye_type);
        cb_pwd = findViewById(R.id.cb_pwd);

        userNames = PreferencesUtils.getString(LoginActivity.this, Constans.USERNAME, "");

        passwords = PreferencesUtils.getString(LoginActivity.this, Constans.USERPASSWORD, "");
        accountEt.setText(userNames);
        passwordEt.setText(passwords);

        isRemberPwd = PreferencesUtils.getBoolean(this, "cheche_pwd", false);
        if (isRemberPwd) {
            cheche_pwd = true;
        } else {
            cheche_pwd = false;
        }
        cb_pwd.setChecked(cheche_pwd);

        btnEyeCloseAndOpen();
    }

    private boolean canSee = false;

    private void btnEyeCloseAndOpen() {
        btn_eye_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通过全局的一个变量的设置，这个就是判断控件里面的内容是不是能被看到
                if (!canSee) {
                    //如果是不能看到密码的情况下，
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btn_eye_type.setBackgroundResource(R.drawable.visual);
                    canSee = true;
                } else {
                    //如果是能看到密码的状态下
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btn_eye_type.setBackgroundResource(R.drawable.invisible);
                    canSee = false;
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void remenberPwd() {
        if (!cheche_pwd) {
            cheche_pwd = true;
            PreferencesUtils.putBoolean(this, "cheche_pwd", true);
        } else {
            PreferencesUtils.putString(this, Constans.USERPASSWORD, "");
            PreferencesUtils.putBoolean(this, "cheche_pwd", false);
            cheche_pwd = false;
        }
        cb_pwd.setChecked(cheche_pwd);
    }


    @Override
    protected void setLisenter() {
        loginBt.setOnClickListener(this);
        cb_pwd.setOnClickListener(this);
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                userNames = accountEt.getText().toString().trim();
                passwords = passwordEt.getText().toString().trim();
                if (!userNames.isEmpty() && !passwords.isEmpty()) {
                    showLoadingDialog(getString(R.string.login));
                    loginPresenter.login(ApiService.LOGIN, userNames, passwords, Constans.ANDROID);
                } else {
                    showErrorToast(getString(R.string.usernamePwd_noNull));
                }
                break;
            case R.id.cb_pwd:
                remenberPwd();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeLoginEvent(LoginEvent loginEvent) {
        dismissLoadingDialog();
        switch (loginEvent.getWhat()) {
            case Constans.LOGIN:
                if (loginEvent.getData() != null) {
                    loginInfo = (LoginDataBean) loginEvent.getData();
                }
                PreferencesUtils.putString(LoginActivity.this, Constans.USERNAME, loginInfo.getLoginid());
                if (cheche_pwd) {
                    PreferencesUtils.putString(LoginActivity.this, Constans.USERPASSWORD, loginInfo.getPassword());
                }

                PreferencesUtils.putString(LoginActivity.this, Constans.ORGID, loginInfo.getOrgId());
                PreferencesUtils.putString(LoginActivity.this, Constans.ID, loginInfo.getId());
                PreferencesUtils.putBoolean(LoginActivity.this, Constans.ISREG, loginInfo.isIsreg());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
            case Constans.USERERROR:
//                showErrorToast((String) loginEvent.getData());
                break;
        }
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*DialogInterface.OnClickListener listenter = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    if (!isFinishing()) {
                        alertDilaog.dismiss();
                    }
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
//                    AppManager.getAppManager().removedAlllActivity(mActivity);
                    System.exit(0);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertDilaog = new AlertDialog.Builder(mActivity).create();
            alertDilaog.setTitle(getString(R.string.sure_exit_app));
            alertDilaog.setMessage("");
            alertDilaog.setButton(1, getString(R.string.cancel), listenter);
            alertDilaog.setButton(2, getString(R.string.sure), listenter);
            alertDilaog.show();
        }
        return false;
    }*/
}
