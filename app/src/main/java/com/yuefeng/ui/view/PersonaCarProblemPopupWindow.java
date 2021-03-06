package com.yuefeng.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuefeng.cartreeList.adapter.PersonalVechicleTreeRecyclerAdapter;
import com.yuefeng.cartreeList.common.Node;
import com.yuefeng.commondemo.R;
import com.yuefeng.features.modle.PersonalinfoListBean;
import com.yuefeng.features.modle.QuestionListBean;
import com.yuefeng.features.modle.VehicleinfoListBean;
import com.yuefeng.utils.FilterMonitoringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 筛选 人员 车辆 问题树形列表
 * <p>
 */

public class PersonaCarProblemPopupWindow extends PopupWindow {
    private Context mContext;
    private boolean isSingle;
    private OnItemClickListener mOnItemClickListener;
    private LinearLayoutCompat llPopupRoot;
    private boolean isShowAniming;//show动画是否在执行中
    private boolean isHideAniming;//hide动画是否在执行中
    private View rootView;
    private TextView tv_title;
    private TextView tv_setting;
    private ImageView iv_search;
    public TextInputEditText tv_search_txt;
    public RecyclerView recyclerview;
    private String key;
    private String mPid;
    private List<Node> mListData = new ArrayList<>();
    private PersonalVechicleTreeRecyclerAdapter mAdapter;
    private List<PersonalinfoListBean> personalinfoList = new ArrayList<>();
    private List<VehicleinfoListBean> vehicleinfoList = new ArrayList<>();
    private List<QuestionListBean> questionList = new ArrayList<>();
    private String mId;
    private String mIdTemp;

    public PersonaCarProblemPopupWindow(Context context, List<Node> carDatas, boolean isSingle) {
        super(null, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mContext = context;
        this.mListData = carDatas;
        this.isSingle = isSingle;
//        personalinfoList.clear();
//        vehicleinfoList.clear();
//        questionList.clear();
        //设置点击空白处消失
        setTouchable(true);
        setOutsideTouchable(true);
        setClippingEnabled(false);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int w = wm.getDefaultDisplay().getWidth();
        int h = wm.getDefaultDisplay().getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(context.getResources().getColor(R.color.transition));//填充颜色
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));

        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_filter, null);
        setContentView(rootView);
        llPopupRoot = (LinearLayoutCompat) rootView.findViewById(R.id.ll_popup_root);
        recyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        RelativeLayout iv_back = (RelativeLayout) rootView.findViewById(R.id.iv_back);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_search_txt = (TextInputEditText) rootView.findViewById(R.id.tv_search_txt);
        iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
        tv_setting = (TextView) rootView.findViewById(R.id.tv_title_setting);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
//        personalinfoList.clear();
//        vehicleinfoList.clear();
//        questionList.clear();
        if (mListData.size() > 0) {
            initRecycleView(mListData);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        tv_search_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showSelectItemDatas();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onSure(personalinfoList, vehicleinfoList, questionList);
                }
            }
        });

        initSeacherWatcher();
    }

    private void initSeacherWatcher() {
        tv_search_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                key = s.toString();
                if (key.length() > 0) {
                    iv_search.setVisibility(View.GONE);
                    searchList(key);
                } else {
                    iv_search.setVisibility(View.VISIBLE);
                    if (mListData.size() > 0) {
                        initRecycleView(mListData);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initRecycleView(List<Node> listData) {
//        personalinfoList.clear();
//        vehicleinfoList.clear();
//        questionList.clear();
        mAdapter = new PersonalVechicleTreeRecyclerAdapter(recyclerview, mContext,
                listData, 1, R.drawable.list_fold, R.drawable.list_fold, isSingle, false);
        recyclerview.setAdapter(mAdapter);
//        mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
//            @Override
//            public void onClick(Node node, int position) {
//                showSelectItemDatas(position);
//
//            }
//
//        });
        mAdapter.setOnCheckBoxInterface(new PersonalVechicleTreeRecyclerAdapter.OnCheckBoxInterface() {
            @Override
            public void onCheckBoxClick(Node node, int position) {
//                showSelectItemDatas();
            }
        });
    }

    /*点击车*/
    @SuppressLint("SetTextI18n")
    private void showSelectItemDatas() {
        if (mAdapter == null) {
            return;
        }
        final List<Node> allNodes = mAdapter.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isChecked()) {
                mPid = (String) allNodes.get(i).getpId();
                mId = (String) allNodes.get(i).getId();
                if (mPid.equals(FilterMonitoringUtils.First)) {
//                    personalinfoList.clear();
//                    vehicleinfoList.clear();
//                    questionList.clear();
                    if (mPid.equals(FilterMonitoringUtils.personalId)) {
                        PersonalinfoListBean bean = (PersonalinfoListBean) allNodes.get(i).getBean();
                        if (!mId.equals(mIdTemp)) {
                            personalinfoList.add(bean);
                        }
                        mIdTemp = bean.getId();
                    } else if (mPid.equals(FilterMonitoringUtils.vehicleId)) {
                        VehicleinfoListBean bean = (VehicleinfoListBean) allNodes.get(i).getBean();
                        if (!mId.equals(mIdTemp)) {
                            vehicleinfoList.add(bean);
                        }
                        mIdTemp = bean.getId();
                    } else if (mPid.equals(FilterMonitoringUtils.questionId)) {
                        QuestionListBean bean = (QuestionListBean) allNodes.get(i).getBean();
                        if (!mId.equals(mIdTemp)) {
                            questionList.add(bean);
                        }
                        mIdTemp = bean.getId();
                    }
                } else if (mPid.equals(FilterMonitoringUtils.personalId)) {
                    PersonalinfoListBean bean = (PersonalinfoListBean) allNodes.get(i).getBean();
                    if (!mId.equals(mIdTemp)) {
                        personalinfoList.add(bean);
                    }
                    mIdTemp = bean.getId();
                } else if (mPid.equals(FilterMonitoringUtils.vehicleId)) {
                    VehicleinfoListBean bean = (VehicleinfoListBean) allNodes.get(i).getBean();
                    if (!mId.equals(mIdTemp)) {
                        vehicleinfoList.add(bean);
                    }
                    mIdTemp = bean.getId();
                } else if (mPid.equals(FilterMonitoringUtils.questionId)) {
                    QuestionListBean bean = (QuestionListBean) allNodes.get(i).getBean();
                    if (!mId.equals(mIdTemp)) {
                        questionList.add(bean);
                    }
                    mIdTemp = bean.getId();
                }
            }
        }
    }


    private void searchList(String key) {
        if (mListData.size() > 0) {
            List<Node> nodes = FilterMonitoringUtils.retureSelectData(mListData, key);
            if (nodes.size() > 0) {
                initRecycleView(nodes);
            }
        }
    }

    public void setTitleText(String titleText) {
        if (tv_title != null) {
            tv_title.setText(titleText);
        }
    }

    public void setSettingText(String setting) {
        if (tv_setting != null) {
            tv_setting.setText(setting);
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (!isShowAniming) {
            isShowAniming = true;
            popupAnim(llPopupRoot, 0.0f, 1.0f, 300, true);
        }
    }

    @Override
    public void dismiss() {
        if (!isHideAniming) {
            isHideAniming = true;
            popupAnim(llPopupRoot, 1.0f, 0.0f, 300, false);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onGoBack();

        void onSure(List<PersonalinfoListBean> personalinfoList,
                    List<VehicleinfoListBean> vehicleinfoList,
                    List<QuestionListBean> questionList);


    }

    /**
     * popupWindow属性动画
     *
     * @param view     执行属性动画的view
     * @param start    start值
     * @param end      end值
     * @param duration 动画持续时间
     * @param flag     true代表show，false代表hide
     */
    private void popupAnim(final View view, float start, final float end, int duration, final
    boolean flag) {
        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                view.setPivotX(0);
//                view.setPivotY(view.getMeasuredHeight());
//                view.setTranslationY((1 - value) * view.getHeight());

                view.setPivotY(0);
                view.setPivotX(view.getMeasuredWidth());
                view.setTranslationX((1 - value) * view.getWidth());
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!flag) {
                    isHideAniming = false;
                    PersonaCarProblemPopupWindow.super.dismiss();
                } else {
                    isShowAniming = false;
                }
            }
        });
        va.start();
    }
}
