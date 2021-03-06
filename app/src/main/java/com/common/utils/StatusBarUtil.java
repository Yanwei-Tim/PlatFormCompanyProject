package com.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.yuefeng.commondemo.R;

import java.lang.reflect.Field;


/**
 * Created  on 2017-12-04.
 * author:seven
 * email:seven2016s@163.com
 * 需要什么颜色的状态栏可以自己定义
 * VERSION.SDK_INT >= VERSION_CODES.KITKAT 这句话兼容了4.4
 */
public class StatusBarUtil {
    /**
     * 设置沉浸状态栏，透明底
     *
     * @param activity
     */
    public static void setTranslate(Activity activity, boolean isWhiteIcon) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= 21) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    if (isWhiteIcon) {
                        window.getDecorView()
                                .setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    } else {
                        window.getDecorView()
                                .setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    //设置状态栏颜色
                    window.setStatusBarColor(Color.TRANSPARENT);
                    //API=19 （4.4） 设置透明
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }
            }
        }
    }

    /**
     * 设置沉浸状态栏，透明底
     *
     * @param activity
     */
    public static void setTranslateByColor(Activity activity, int color) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= 21) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView()
                            .setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    /**
     * 设置沉浸状态栏，黑色底
     *
     * @param activity
     */
    public static void clearTranslate(Activity activity) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= 21) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView()
                            .setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.BLACK);
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    /**
     * 设置沉浸状态栏，黑色底
     *
     * @param activity
     */
    public static void customTranslate(Activity activity, int color) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= 21) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView()
                            .setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    /**
     * 白底黑字！Android浅色状态栏黑色字体模式
     *
     * @param activity
     * @param color
     */
    public static void customTranslate1(Activity activity, int color) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= VERSION_CODES.M) { //6.0以上
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    window.setStatusBarColor(color);
                } else if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {//5.0以上,变回灰色底
                    customTranslate(activity, R.color.status_color);
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }


    private static int sStatusBarHeight = -1;


    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight != -1) {
            return sStatusBarHeight;
        }

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sStatusBarHeight;
    }

    public static void setFadeStatusBarHeight(Context context, View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int statusBarHeight = getStatusBarHeight(context);
            if (params != null) {
                params.height = statusBarHeight;
                view.setLayoutParams(params);
            }
        }
    }

    /**
     * 获取需要适配的状态栏高度，如果是KITKAT一下返回0
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeightFit(Context context) {
        return VERSION.SDK_INT >= VERSION_CODES.KITKAT ? getStatusBarHeight(context) : 0;
    }

    /**
     * 设置沉浸状态栏， 颜色自定 设置状态栏的alpha值
     *
     * @param activity
     */
    public static void setStatusBarColorAlpha(Activity activity, int color, int alpha) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                if (VERSION.SDK_INT >= 21) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView()
                            .setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(calculateStatusColor(color, alpha));
                } else if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}
