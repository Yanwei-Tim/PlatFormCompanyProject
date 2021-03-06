package com.yuefeng.photo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;

import com.common.utils.AppUtils;
import com.common.utils.ImageUtils;
import com.common.utils.PreferencesUtils;
import com.common.utils.TimeUtils;
import com.yuefeng.photo.adapter.GvAdapter;
import com.yuefeng.photo.bean.ImageInfo;
import com.yuefeng.photo.view.MyGridView2;
import com.yuefeng.photo.view.PicShowDialog;
import com.yuefeng.ui.MyApplication;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class ImageHelper {

    private static List<ImageInfo> sImages;
    private static GvAdapter adapter = null;

    public static void showImageBitmap(MyGridView2 gridView, final Context context, String imgUrl) {

        sImages = new ArrayList<>();
        sImages.clear();
        if (imgUrl.contains(",")) {
            String[] split = imgUrl.split(",");
            for (String aSplit : split) {
                ImageInfo imageInfo = new ImageInfo(aSplit, (int) AppUtils.mScreenWidth, (int) AppUtils.mScreenHeight);
                sImages.add(imageInfo);
            }
        } else {
            ImageInfo imageInfo = new ImageInfo(imgUrl, (int) AppUtils.mScreenWidth, (int) AppUtils.mScreenHeight);
            sImages.add(imageInfo);
        }
        adapter = new GvAdapter(context, sImages);
        gridView.setAdapter(adapter);

        if (sImages.size() > 0) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    PicShowDialog dialog = new PicShowDialog(context, sImages, position);
                    dialog.show();
                }
            });
        }
    }

    public static String getFirstImageUrl(String imgUrl) {

        String imageUrl = "";
        if (imgUrl.contains(",")) {
            String[] split = imgUrl.split(",");
            imageUrl = split[0];
        } else {
            imageUrl = imgUrl;
        }

        return imageUrl;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static String compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 30, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        byte[] buffer = baos.toByteArray();
        String string = Base64.encodeToString(buffer, Base64.DEFAULT);
        return string;
    }

    /*
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）
     * @param srcPath
     * @return
     */
    public static String getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public static String getimage(Context context, String srcPath, String txt) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        if (!TextUtils.isEmpty(txt)) {
            bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap,
                    txt, 20, Color.LTGRAY, 10, 30);
            bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap, TimeUtils.getCurrentTime(), 20, Color.LTGRAY, 10, 5);
        } else {

            txt = PreferencesUtils.getString(context, "mAddress", "");
            if (!TextUtils.isEmpty(txt)) {
                bitmap = ImageUtils.drawTextToRightBottom(AppUtils.getContext(), bitmap, txt, 20, Color.LTGRAY, 10, 30);
                bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap, TimeUtils.getCurrentTime(), 20, Color.LTGRAY, 10, 5);
            } else {
                txt = TimeUtils.getCurrentTime();
                bitmap = ImageUtils.drawTextToRightBottom(AppUtils.getContext(), bitmap, txt, 20, Color.LTGRAY, 10, 20);
            }
        }
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getImageByPath(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
//         重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//        bitmap = ImageUtil.drawTextToCenter(Global.mContext, bitmap, "广东丰润环境管理服务有限公司", 40, Color.RED);
        return bitmap;
    }

    public static Bitmap getImageByPath(Context context, String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
//         重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        String txt = PreferencesUtils.getString(MyApplication.getContext(), "mAddress");
        if (!TextUtils.isEmpty(txt)) {
            bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap,
                    txt, 20, Color.LTGRAY, 10, 30);
            bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap, TimeUtils.getCurrentTime(), 20, Color.LTGRAY, 0, 5);
        } else {
            txt = TimeUtils.getCurrentTime();
            bitmap = ImageUtils.drawTextToRightBottom(MyApplication.getContext(), bitmap, txt, 20, Color.LTGRAY, 25, 10);
        }
        return bitmap;
    }
}
