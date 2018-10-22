package com.common.view.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuefeng.commondemo.R;


/**
 * 展示人员
 */

public class ShowPersonalpop extends PopupWindow {

    private View view, item_view;
    private TextView tv_item_name, tv_item_chiwei, tv_item_track;
    private TextView tv_item_phone, tv_item_phonename;
    private TextView tv_item_class, tv_item_classname, tv_item_video;
    private TextView tv_item_address, tv_item_addressname, tv_item_native;
    private Context context;
    private int colorInt;

    public ShowPersonalpop(Context context) {
        super(context);
        initView(context);
        this.context = context;
        setPopView();
    }

    private void setPopView() {
        setContentView(view);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.pop_show_personal, null);
        tv_item_name = view.findViewById(R.id.tv_item_name);
        tv_item_chiwei = view.findViewById(R.id.tv_item_chiwei);

        tv_item_phone = view.findViewById(R.id.tv_item_phone);
        tv_item_phonename = view.findViewById(R.id.tv_item_phonename);

        tv_item_class = view.findViewById(R.id.tv_item_class);
        tv_item_classname = view.findViewById(R.id.tv_item_classname);

        tv_item_address = view.findViewById(R.id.tv_item_address);
        tv_item_addressname = view.findViewById(R.id.tv_item_addressname);

        item_view = view.findViewById(R.id.item_view);
        tv_item_video = view.findViewById(R.id.tv_item_video);
        tv_item_native = view.findViewById(R.id.tv_item_native);
        tv_item_track = view.findViewById(R.id.tv_item_track);

        tv_item_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
                if (takePhotoTouch != null) {
                    takePhotoTouch.takeTrack();

                }
            }
        });
        tv_item_native.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
                if (takePhotoTouch != null) {
                    takePhotoTouch.takeNativ();
                }
            }
        });
        tv_item_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
                if (takePhotoTouch != null) {
                    takePhotoTouch.onVideo();
                }
            }
        });

    }

    public void setTextContent(String name, String position, String phone, String classname, String address, boolean isVisible) {
        if (isVisible) {
            item_view.setVisibility(View.VISIBLE);
            tv_item_video.setVisibility(View.VISIBLE);
        }
        tv_item_name.setText(name);
        tv_item_chiwei.setText(position);
        switch (position) {
            case "在线":
            case "行驶中":
                colorInt = context.getResources().getColor(R.color.green);
                break;
            case "离线":
                colorInt = context.getResources().getColor(R.color.red);
                break;
            default:
                colorInt = context.getResources().getColor(R.color.gray);
                break;
        }
        tv_item_chiwei.setTextColor(colorInt);
        tv_item_phonename.setText(phone);
        tv_item_classname.setText(classname);
        tv_item_addressname.setText(address);
    }

    public void showTakePop(View parent) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    private TakePhotoTouch takePhotoTouch;

    public void setTakePhotoTouch(TakePhotoTouch touch) {
        this.takePhotoTouch = touch;
    }

    public interface TakePhotoTouch {
        void onVideo();//视频

        void takeTrack();//轨迹

        void takeNativ();//导航

    }
}
