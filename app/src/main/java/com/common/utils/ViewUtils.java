package com.common.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewUtils {


    /*设置RelativeLayout height width*/
    public static void setRLHightOrWidth(RelativeLayout relativeLayout, int height, int width) {
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.height = height;
        params.width = width;
        relativeLayout.setLayoutParams(params);
    }

    /*LinearLayout height width*/
    public static void setLLHightOrWidth(LinearLayout linearLayout, int height, int width) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = height;
        params.width = width;
        linearLayout.setLayoutParams(params);
    }

    /*TextView height width*/
    public static void setTVHightOrWidth(TextView textView, int height, int width) {
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.height = height;
        params.width = width;
        textView.setLayoutParams(params);
    }


    /*ImageView height width*/
    public static void setIVHightOrWidth(ImageView imageView, int height, int width) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = height;
        params.width = width;
        imageView.setLayoutParams(params);
    }

    /*EditView height width*/
    public static void setEditHightOrWidth(EditText editText, int height, int width) {
        ViewGroup.LayoutParams params = editText.getLayoutParams();
        params.height = height;
        params.width = width;
        editText.setLayoutParams(params);
    }

    /*PopupWindow是否显示*/
    public static boolean isPopuwindowShowing(PopupWindow popupwindow) {
        if (popupwindow != null) {
            return popupwindow.isShowing();
        }
        return false;
    }

    /*LinearLayout 显示隐藏*/
    public static void setLlVisible(LinearLayout linearLayout, boolean visible) {
        int type = 0;
        if (visible) {
            type = View.INVISIBLE;
        } else {
            type = View.VISIBLE;
        }
        linearLayout.setVisibility(type);
    }
}