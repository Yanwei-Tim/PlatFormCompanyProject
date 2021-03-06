package com.yuefeng.features.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.common.base.codereview.BaseActivity;
import com.common.event.CommonEvent;
import com.common.network.ApiService;
import com.common.utils.Constans;
import com.common.utils.LogUtils;
import com.common.utils.StringUtils;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.contract.CarDetailInfosContract;
import com.yuefeng.features.modle.CarDetailInfosBean;
import com.yuefeng.features.modle.VehicleinfoListBean;
import com.yuefeng.features.presenter.CarDetailInfosPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*车辆详情*/
public class CarDetailInfosActivity extends BaseActivity implements CarDetailInfosContract.View {


    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_banzu)
    TextView tvBanzu;
    @BindView(R.id.tv_zhuguan)
    TextView tvZhuguan;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_islocation)
    TextView tvIslocation;
    @BindView(R.id.tv_ang)
    TextView tvAng;
    @BindView(R.id.tv_ternumber)
    TextView tvTernumber;
    @BindView(R.id.tv_tertype)
    TextView tvTertype;
    @BindView(R.id.tv_hangtype)
    TextView tvHangtype;
    @BindView(R.id.tv_cartype)
    TextView tvCartype;
    @BindView(R.id.tv_cheping)
    TextView tvCheping;
    @BindView(R.id.tv_chexing_hao)
    TextView tvChexingHao;
    @BindView(R.id.tv_oilsmax)
    TextView tvOilsmax;
    @BindView(R.id.tv_oilschuan)
    TextView tvOilschuan;
    @BindView(R.id.tv_zaimax)
    TextView tvZaimax;
    @BindView(R.id.tv_seatcount)
    TextView tvSeatcount;
    @BindView(R.id.tv_oilsguan)
    TextView tvOilsguan;
    private VehicleinfoListBean mBean;
    private CarDetailInfosPresenter mPresenter;
    private String mVid;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_cardetailinfos;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mPresenter = new CarDetailInfosPresenter(this, this);
        setTitle("车辆详情");
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mBean = (VehicleinfoListBean) bundle.getSerializable(Constans.GROUPID);
        if (mBean != null) {
            showUIData(mBean);
        }
    }

    private void showUIData(VehicleinfoListBean bean) {
        mVid = bean.getId();
        LogUtils.d("===vid===" + mVid);
        String registrationNO = StringUtils.isEntryStrWu(bean.getRegistrationNO());
        String time = StringUtils.isEntryStrXieg(bean.getTime());
        String phone = StringUtils.isEntryStrXieg("");
        String banzu = StringUtils.isEntryStrXieg(bean.getPid());
        String zuguan = StringUtils.isEntryStrXieg("");
        String speed = StringUtils.isEntryStrXieg(bean.getSpeed());
        String address = StringUtils.isEntryStrXieg(bean.getAddress());
        String location = StringUtils.isEntryStrXieg("定位");
        String ang = StringUtils.isEntryStrXieg("");
        if (!speed.equals("-")) {
            speed = speed + "km/h";
        }

        setTitle(registrationNO);
        tvTime.setText(time);
        tvPhone.setText(phone);
        tvBanzu.setText(banzu);
        tvZhuguan.setText(zuguan);
        tvSpeed.setText(speed);
        tvAddress.setText(address);
        tvIslocation.setText(location);
        tvAng.setText(ang);

        getDataByNet(mVid);
    }

    private void getDataByNet(String mVid) {
        if (mPresenter != null && !TextUtils.isEmpty(mVid)) {
            mPresenter.getVehicleDetail(ApiService.GETVEHICLEDETAIL, mVid);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeCommonEvent(CommonEvent event) {
        switch (event.getWhat()) {
            case Constans.CARDETAIL_SUCCESS://树
                CarDetailInfosBean.MsgBean bean = (CarDetailInfosBean.MsgBean) event.getData();
                if (null != bean) {
                    showUICarData(bean);
                }
                break;

            case Constans.CARDETAIL_ERROR:
                showSureGetAgainDataDialog("加载数据失败,是否重新加载?");
                break;
        }
    }

    private void showUICarData(CarDetailInfosBean.MsgBean bean) {
        String terminalNO = StringUtils.isEntryStrXieg(bean.getTerminalNO());
        String terminalTypeID = StringUtils.isEntryStrXieg(bean.getTerminalTypeID());
        String vehicletype = StringUtils.isEntryStrXieg(bean.getVehicletype());
        String carType = StringUtils.isEntryStrXieg(bean.getCarType());
        String brandType = StringUtils.isEntryStrXieg(bean.getBrandType());
        String modeltype = StringUtils.isEntryStrXieg(bean.getModeltype());
        String oilMax = StringUtils.isEntryStrXieg(bean.getOilMax());
        String oilSum = StringUtils.isEntryStrXieg(bean.getOilSum());
        String weight = StringUtils.isEntryStrXieg(bean.getWeight());
        String seatcount = StringUtils.isEntryStrXieg(bean.getSeatcount());

        tvTernumber.setText(terminalNO);
        tvTertype.setText(terminalTypeID);
        tvHangtype.setText(vehicletype);
        tvCheping.setText(carType);
        tvCartype.setText(brandType);
        tvChexingHao.setText(modeltype);

        tvOilsmax.setText(oilMax);
        tvOilschuan.setText(oilSum);
        tvZaimax.setText(weight);
        tvSeatcount.setText(seatcount);
    }

    @Override
    public void getDatasAgain() {
        super.getDatasAgain();
        getDataByNet(mVid);
    }

    @Override
    protected void setLisenter() {

    }

    @Override
    protected void widgetClick(View v) {

    }

    @OnClick(R.id.tv_phone)
    public void onClick() {
        String phone = tvPhone.getText().toString().trim();

        StringUtils.callPhone(CarDetailInfosActivity.this, phone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
