package com.yuefeng.home.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.LogUtils;
import com.common.utils.StringUtils;
import com.common.utils.TimeUtils;
import com.yuefeng.commondemo.R;
import com.yuefeng.home.modle.NewMsgListDataBean;

import java.util.List;

/*消息*/
public class HomeMsgInfosAdapter extends BaseItemDraggableAdapter<NewMsgListDataBean, BaseViewHolder> {


    private String genre;
    private String title;
    private String time;
    private String detail;
    private String count;
    private String name;
    private int icon;

    public HomeMsgInfosAdapter(int layoutResId, @Nullable List<NewMsgListDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewMsgListDataBean item) {
        if (item != null && helper != null) {
            genre = StringUtils.isEntryStrZero(item.getGenre());
            name = item.getOrganname();
            title = item.getSubject();
            time = item.getIssuedate();
            detail = item.getContent();
            title = StringUtils.isEntryStrNull(title);
            time = TimeUtils.formatHourMin(time);
            String isread = item.getIsread();
            if (isread.equals("0")) {
                helper.setVisible(R.id.iv_item_unread, false);
            }
            LogUtils.d("==genre== " + genre);
            // genre：1就是公告，2就是超哥的信息，3是更新的
            count = item.getNotread();
            count = StringUtils.isEntryStrZero(count);
            if (genre.equals("1")) {//公告
                icon = R.drawable.work;
                title = "[公告]" + title;
            } else if (genre.equals("2")) { //项目信息
                icon = R.drawable.xiangmutongzhi;
                title = "[项目]" + title;
            } else if (genre.equals("4")) { //项目信息
                icon = R.drawable.news_warning;
                title = "[报警]" + title;
            } else {//更新
                title = "[升级]" + title;
                icon = R.drawable.upgrade;
            }
            if (count.equals("0")) {
                helper.setVisible(R.id.tv_item_count, false);
            } else {
                helper.setVisible(R.id.tv_item_count, true);
            }
            int length = count.length();
            if (length > 2) {
                count = "99+";
            }
            TextView tv_item_title = helper.getView(R.id.tv_item_title);
            TextView tv_item_time = helper.getView(R.id.tv_item_time);
            TextView tv_item_detail = helper.getView(R.id.tv_item_detail);
            tv_item_title.setTextSize(13);
            tv_item_time.setTextSize(12);
            tv_item_detail.setTextSize(12);
            helper.setText(R.id.tv_item_title, title)
                    .setText(R.id.tv_item_count, count)
                    .setText(R.id.tv_item_time, time)
                    .setText(R.id.tv_item_detail, detail)
                    .setImageResource(R.id.iv_item_image, icon);
        }
    }
}
