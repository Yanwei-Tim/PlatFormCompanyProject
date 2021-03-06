package com.yuefeng.contacts.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.codereview.BaseActivity;
import com.common.event.CommonEvent;
import com.common.utils.Constans;
import com.yuefeng.commondemo.R;
import com.yuefeng.contacts.adapter.ContactsAdapter;
import com.yuefeng.contacts.contract.FindOrganUserContract;
import com.yuefeng.contacts.modle.contacts.OrganPersonalDataBean;
import com.yuefeng.contacts.modle.contacts.OrganlistBean;
import com.yuefeng.contacts.presenter.FartherFindOrganPresenter;
import com.yuefeng.ui.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/*通讯录*/
public class FartherGroupNameActivity extends BaseActivity implements FindOrganUserContract.View {
    //List<OrganlistBean>
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private String mTitltName;
    private List<OrganlistBean> mListData = new ArrayList<>();
    private ContactsAdapter mAdapter;
    private FartherFindOrganPresenter mPresenter;
    private String mName;
    private int typeOnclick = 0;
    private String mUserid;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_fragment_whatgroupname;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);

            ButterKnife.bind(this);
            mPresenter = new FartherFindOrganPresenter(this, this);
            initRecyclerView();

        }
    }

    private void initRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactsAdapter(R.layout.recyclerview_item_contacts, mListData);
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                typeOnclick = 1;
                OrganlistBean contactsBean = mListData.get(position);
                if (contactsBean != null) {
                    mName = contactsBean.getName();
                    boolean isorgan = contactsBean.isIsorgan();
                    mUserid = contactsBean.getId();
                    if (isorgan) {
                        getWhatGroupDatasByNet(mUserid);
                    } else {
                        toUserDetailActivity(contactsBean.getId());
                    }
                }
            }
        });
    }

    private void toUserDetailActivity(String id) {
        Intent intent = new Intent();
        intent.setClass(FartherGroupNameActivity.this, UserDetailInfosActivity.class);
        intent.putExtra(Constans.GROUPID, id);
        startActivity(intent);
    }

    private void getWhatGroupDatasByNet(String userId) {
        boolean networkConnected = MyApplication.getInstance().isNetworkConnected();
        if (!networkConnected) {
            return;
        }
        if (mPresenter != null) {
            mPresenter.findOrganWithID(userId, "", 0);
        }
    }


    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        mTitltName = (String) bundle.get(Constans.GROUPNAME);
        mUserid = (String) bundle.get(Constans.GROUPID);
        setTitle(mTitltName);
        getWhatGroupDatasByNet(mUserid);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeSCommonEvent(CommonEvent event) {
        switch (event.getWhat()) {
            case Constans.CONTACTS_SUCCESS://获取通讯录成功
                OrganPersonalDataBean bean = (OrganPersonalDataBean) event.getData();
                if (typeOnclick == 0) {
                    addData(bean);
                } else {
                    toActivity(bean);
                }
                break;
            case Constans.CONTACTS_ERROR:
                showSureGetAgainDataDialog("数据加载失败，是否重新加载?");
                break;
        }
    }

    @Override
    public void getDatasAgain() {
        super.getDatasAgain();
        getWhatGroupDatasByNet(mUserid);
    }

    /*添加数据*/
    private void addData(OrganPersonalDataBean bean) {
        mListData.clear();
        if (bean != null) {
            List<OrganlistBean> organlist = bean.getOrganlist();
            int sizeOrgan = organlist.size();
            if (sizeOrgan > 0) {
                mListData.addAll(organlist);
            }
            List<OrganlistBean> userlist = bean.getUserlist();
            int sizeUser = userlist.size();
            if (sizeUser > 0) {
                mListData.addAll(userlist);
            }
        }
        if (mAdapter != null) {
            mAdapter.setNewData(mListData);
        }
    }

    /*跳转*/
    private void toActivity(OrganPersonalDataBean bean) {
        typeOnclick = 0;
        Intent intent = new Intent();
        if (bean != null) {
            List<OrganlistBean> listData = new ArrayList<>();
            boolean isorgan = bean.isIsorgan();//是否机构
            boolean haschild = bean.isHaschild();//是否有人
            List<OrganlistBean> organlist = bean.getOrganlist();
            List<OrganlistBean> userlist = bean.getUserlist();
            int sizeOrgan = organlist.size();
            int sizeUser = userlist.size();
            if (sizeOrgan == 0 && sizeUser == 0) {
                showSuccessToast("旗下无机构和人");
                return;
            }

            if (sizeOrgan > 0) {
                listData.addAll(organlist);
                intent.setClass(FartherGroupNameActivity.this, SixthGroupNameActivity.class);
            } else {
                if (sizeUser > 0) {
                    intent.setClass(FartherGroupNameActivity.this, SelectPersonalWhoActivity.class);
                }
            }
            if (sizeUser > 0) {
                listData.addAll(userlist);
            }

            intent.putExtra(Constans.GROUPNAME, mName);
            intent.putExtra(Constans.GROUPID, (Serializable) listData);
            startActivity(intent);
        }
    }



    @Override
    protected void setLisenter() {

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
