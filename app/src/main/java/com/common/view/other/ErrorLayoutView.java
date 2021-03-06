package com.common.view.other;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.common.view.progress.DoubleBounce;
import com.yuefeng.commondemo.R;


/**
 * Created  on 2018-03-31.
 * author:seven
 * email:seven2016s@163.com
 */

public class ErrorLayoutView extends RelativeLayout {

    private ProgressBar progressBar;
    private RelativeLayout loadingLayout;
    private RelativeLayout errorLayout;

    public ErrorLayoutView(Context context) {
        super(context);
    }

    public ErrorLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private onErrorLayoutListenter errorOnClickListener;

    public void setErrorOnclickListenter(onErrorLayoutListenter errorOnclickListenter) {
        this.errorOnClickListener = errorOnclickListenter;
    }

    public interface onErrorLayoutListenter {
        void replayLoading();
    }

    private void initView() {
        progressBar = findViewById(R.id.pb_progress);
        loadingLayout = findViewById(R.id.rly_loading);
        errorLayout = findViewById(R.id.rly_error);
        errorLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errorOnClickListener != null) {
                    errorOnClickListener.replayLoading();
                }
            }
        });

    }

    public void playAnimation() {
        loadingLayout.setVisibility(VISIBLE);
        errorLayout.setVisibility(GONE);
        DoubleBounce doubleBounce = new DoubleBounce();
        doubleBounce.setBounds(0, 0, 100, 100);
        doubleBounce.setColor(android.graphics.Color.parseColor("#FF0000"));
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    /*public void stopProgressbar() {
        progressBar.setVisibility(GONE);
    }*/

    public void showErrorView() {
        setVisibility(VISIBLE);
        errorLayout.setVisibility(VISIBLE);
        loadingLayout.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        Log.d("home", "showErrorView");
    }

    /*public void showEmptyView() {
        loadingLayout.setVisibility(GONE);
        errorLayout.setVisibility(GONE);
        progressBar.setVisibility(GONE);
    }*/
    public void hide() {
        progressBar.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
        errorLayout.setVisibility(GONE);
        setVisibility(GONE);
        Log.d("home", "hide");
    }
}
