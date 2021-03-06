package com.yuefeng.features.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.common.base.codereview.BasePresenterImpl;
import com.common.network.ApiException;
import com.common.network.HttpObservable;
import com.common.network.HttpResultObserver;
import com.common.utils.Constans;
import com.common.utils.StringUtils;
import com.yuefeng.features.contract.PositionAcquisitionContract;
import com.yuefeng.features.event.PositionAcquisitionEvent;
import com.yuefeng.features.modle.GetCaijiTypeMsgBean;
import com.yuefeng.features.modle.SubmitBean;
import com.yuefeng.features.modle.video.GetCaijiTypeBean;
import com.yuefeng.features.ui.activity.position.PositionAcquisitionActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 信息采集
 */

public class PositionAcquisitionPresenter extends BasePresenterImpl<PositionAcquisitionContract.View,
        PositionAcquisitionActivity> implements PositionAcquisitionContract.Presenter {

    private String hourStr;
    private String minuteStr;
    private String secondStr;
    private String lnglat;

    public PositionAcquisitionPresenter(PositionAcquisitionContract.View view, PositionAcquisitionActivity activity) {
        super(view, activity);
    }


    /*信息采集*/
    @Override
    public void upLoadmapInfo(String function, String pid, String userid, String typeid,
                              String typename, String name, String lnglat, String area, String imageArrays,String id) {
        HttpObservable.getObservable(apiRetrofit.upLoadmapInfo(function, pid, userid, typeid,
                typename, name, lnglat, area, imageArrays,id))
                .subscribe(new HttpResultObserver<SubmitBean>() {
                    @Override
                    protected void onLoading(Disposable d) {
                        showLoadingDialog("上传中...");
                    }

                    @Override
                    protected void onSuccess(SubmitBean o) {
                        dismissLoadingDialog();
                        if (getView() != null) {
                            if (o.isSuccess()) {
                                EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.MSGCOLECTION_SSUCESS, o.getMsg()));
                            } else {
                                EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.MSGCOLECTION_ERROR, o.getMsg()));
                            }
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        dismissLoadingDialog();
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.MSGCOLECTION_ERROR, e.getMsg()));
                    }


                    @Override
                    protected void _onError(ApiException error) {
                        dismissLoadingDialog();
                        super._onError(error);
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.MSGCOLECTION_ERROR, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.MSGCOLECTION_ERROR, ""));
                    }
                });
    }

    /*上传获取采集类型*/
    @Override
    public void getCaijiType(String function) {
        HttpObservable.getObservable(apiRetrofit.getCaijiType(function))
                .subscribe(new HttpResultObserver<GetCaijiTypeBean>() {
                    @Override
                    protected void onLoading(Disposable d) {
                        showLoadingDialog("加载中...");
                    }

                    @Override
                    protected void onSuccess(GetCaijiTypeBean o) {
                        dismissLoadingDialog();
                        if (getView() != null) {
                            if (o.isSuccess()) {
                                int size = o.getMsg().size();
                                if (size == 0) {
                                    EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_ERROR, o.getMsg()));
                                } else {
                                    EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_SSUCESS, o.getMsg()));
                                }
                            } else {
                                EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_ERROR, o.getMsg()));
                            }
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        dismissLoadingDialog();
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_ERROR, e.getMsg()));
                    }


                    @Override
                    protected void _onError(ApiException error) {
                        dismissLoadingDialog();
                        super._onError(error);
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_ERROR, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                        EventBus.getDefault().postSticky(new PositionAcquisitionEvent(Constans.GETCAIJI_ERROR, ""));
                    }

                    @Override
                    protected void _onNext(GetCaijiTypeBean responseCustom) {
                        super._onNext(responseCustom);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        dismissLoadingDialog();
                    }

                    @Override
                    protected void onStart(Disposable d) {
                        super.onStart(d);
                        dismissLoadingDialog();
                    }


                });

    }

    /*实时上传经纬度*/
    @Override
    public void uploadLnglat(String function, String type, String lng, String lat, String id) {
        HttpObservable.getObservable(apiRetrofit.uploadLnglat(function, type, lng, lat, id))
                .subscribe(new HttpResultObserver<SubmitBean>() {
                    @Override
                    protected void onLoading(Disposable d) {
//                        showLoadingDialog("加载中...");
                    }

                    @Override
                    protected void onSuccess(SubmitBean o) {
                        dismissLoadingDialog();
                        if (getView() != null) {
                            if (o.isSuccess()) {
                                EventBus.getDefault().post(new PositionAcquisitionEvent(Constans.UPLOADLNGLAT_SSUCESS, o.getMsg()));
                            } else {
                                EventBus.getDefault().post(new PositionAcquisitionEvent(Constans.UPLOADLNGLAT_ERROR, o.getMsg()));
                            }
                        }
                    }

                    @Override
                    protected void onFail(ApiException e) {
                        dismissLoadingDialog();
                        EventBus.getDefault().post(new PositionAcquisitionEvent(Constans.UPLOADLNGLAT_ERROR, e.getMsg()));
                    }


                    @Override
                    protected void _onError(ApiException error) {
                        dismissLoadingDialog();
                        super._onError(error);
                        EventBus.getDefault().post(new PositionAcquisitionEvent(Constans.UPLOADLNGLAT_ERROR, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                        EventBus.getDefault().post(new PositionAcquisitionEvent(Constans.UPLOADLNGLAT_ERROR, ""));
                    }
                });
    }


    /*结束展示时间*/
    @SuppressLint("SetTextI18n")
    public String showHowLongTime(String timeLong, String distance, int type) {
        String time = "";
        String hour = "";
        String miniteSecond = "";
        String typeWhat = "";
        if (!TextUtils.isEmpty(timeLong)) {
            int length = timeLong.length();
            if (length >= 8) {//小时
                hourStr = timeLong.substring(0, 2);
                minuteStr = timeLong.substring(3, 5);
                secondStr = timeLong.substring(6, 8);
                hourStr = StringUtils.getTimeNoZero(hourStr);
                /* android:text="本次采集持续1分钟，距离0.1公里"*/
                hour = hourStr + "小时";
            } else if (length >= 7) {
                hourStr = timeLong.substring(0, 1);
                minuteStr = timeLong.substring(2, 4);
                secondStr = timeLong.substring(5, 7);
                hourStr = StringUtils.getTimeNoZero(hourStr);
                /* android:text="本次采集持续1分钟，距离0.1公里"*/
                hour = hourStr + "小时";
            } else if (length >= 5) {
                minuteStr = timeLong.substring(0, 2);
                secondStr = timeLong.substring(3, 5);
            }
            minuteStr = StringUtils.getTimeNoZero(minuteStr);
            secondStr = StringUtils.getTimeNoZero(secondStr);
            miniteSecond = minuteStr + "分" + secondStr + "秒,";
            if (type == 0) {
                if (distance.equals("NaN")) {
//                    typeWhat = "无效采集数据";
                    typeWhat = "";
                } else {
                    typeWhat = "面积约" + distance + "平方米";
                }
            } else {
                typeWhat = "距离" + distance + "公里";
            }

            time = "本次采集持续" + hour + miniteSecond + typeWhat;
        }
        return time;
    }


    public String getLnglatStr(List<LatLng> points) {
        lnglat = "";
        int size = points.size();
        for (int i = 0; i < size; i++) {
            LatLng latLng = points.get(i);
            if (i == 0) {
                lnglat = String.valueOf(latLng.longitude) + "," + latLng.latitude;
            } else {
                lnglat = lnglat + ";" + latLng.longitude + "," + latLng.latitude;
            }
        }
        return lnglat;
    }

    public List<GetCaijiTypeMsgBean> initWorkArea() {
        List<GetCaijiTypeMsgBean> list = new ArrayList<>();
        GetCaijiTypeMsgBean bean = new GetCaijiTypeMsgBean();
        bean.setId("d314c837ffffffc91739d203e8f43948");
        bean.setTitleid("mao_caiji");
        bean.setCode("1");
        bean.setData("线路");
        bean.setOrderNum("1");
        list.add(bean);

        GetCaijiTypeMsgBean bean2 = new GetCaijiTypeMsgBean();
        bean2.setId("d315ba67ffffffc91739d203491c3552");
        bean2.setTitleid("mao_caiji");
        bean2.setCode("1");
        bean2.setData("网格");
        bean2.setOrderNum("2");
        list.add(bean2);
        return list;
    }

    public List<GetCaijiTypeMsgBean> initInFrast() {
        List<GetCaijiTypeMsgBean> list = new ArrayList<>();
        GetCaijiTypeMsgBean bean = new GetCaijiTypeMsgBean();
        bean.setId("d319c2c0ffffffc91739d203213c3448");
        bean.setTitleid("mao_caiji");
        bean.setCode("2");
        bean.setData("生活垃圾收集点");
        bean.setOrderNum("3");
        list.add(bean);
        GetCaijiTypeMsgBean bean2 = new GetCaijiTypeMsgBean();
        bean2.setId("d31a0d37ffffffc91739d20355b414b9");
        bean2.setTitleid("mao_caiji");
        bean2.setCode("2");
        bean2.setData("垃圾点");
        bean2.setOrderNum("4");
        list.add(bean2);
        GetCaijiTypeMsgBean bean3 = new GetCaijiTypeMsgBean();
        bean3.setId("d31a5c8fffffffc91739d20329acb6ff");
        bean3.setTitleid("mao_caiji");
        bean3.setCode("2");
        bean3.setData("公厕");
        bean3.setOrderNum("5");
        list.add(bean3);
        return list;
    }
}
