package com.vrcvp.cloudvision.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vrcvp.cloudvision.R;
import com.vrcvp.cloudvision.bean.AdvertiseBean;
import com.vrcvp.cloudvision.bean.VoiceBean;
import com.vrcvp.cloudvision.bean.VoiceSearchResultBean;
import com.vrcvp.cloudvision.utils.StringUtils;

/**
 * 语音搜索结果列表Item视图
 * Created by yinglovezhuzhu@gmail.com on 2016/9/18.
 */
public class VoiceSearchResultItemView extends LinearLayout {

    private ImageView mIvImage;
    private ImageView mIvPlay;
    private TextView mTvTitle;
    private TextView mTvContent;

    public VoiceSearchResultItemView(Context context) {
        super(context);
        initView(context);
    }

    public VoiceSearchResultItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VoiceSearchResultItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(21)
    public VoiceSearchResultItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void setData(VoiceSearchResultBean bean) {
        if(null == bean) {
            return;
        }
        switch (bean.getType()) {
            case AdvertiseBean.TYPE_VIDEO:
                mIvPlay.setVisibility(View.VISIBLE);
                loadImage(bean.getOutLink());
                break;
            case AdvertiseBean.TYPE_IMAGE:
            case AdvertiseBean.TYPE_PRODUCT:
            case AdvertiseBean.TYPE_CORPORATE:
            case AdvertiseBean.TYPE_OUTER_LINK:
                mIvPlay.setVisibility(View.GONE);
                loadImage(bean.getUrl());
                break;
            default:
                break;
        }

        final String title = bean.getName();
        mTvTitle.setVisibility(StringUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        mTvTitle.setText(bean.getName());

        final String content = bean.getContent();
        mTvContent.setVisibility(StringUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mTvContent.setText(Html.fromHtml(content));
        } else {
            try {
                mTvContent.setText(Html.fromHtml(content, 0));
            } catch (Exception e) {
                mTvContent.setText(Html.fromHtml(content));
            }
        }

    }

    private void initView(Context context) {
        inflate(context, R.layout.item_voice_search_result, this);

        mIvImage = (ImageView) findViewById(R.id.iv_item_voice_search_result_img);
        mIvPlay = (ImageView) findViewById(R.id.iv_item_voice_search_result_play);
        mTvTitle = (TextView) findViewById(R.id.tv_item_voice_search_result_title);
        mTvContent = (TextView) findViewById(R.id.tv_item_voice_search_result_content);
    }

    /**
     * 加载图片
     * @param path 图片地址
     */
    private void loadImage(String path) {
        if(StringUtils.isEmpty(path)) {
            mIvImage.setImageResource(R.drawable.img_default);
            return;
        }
        Picasso.with(getContext())
                .load(path)
                .placeholder(R.drawable.img_default)
                .error(R.drawable.img_default)
                .into(mIvImage);
    }
}
