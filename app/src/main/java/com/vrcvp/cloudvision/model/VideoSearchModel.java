package com.vrcvp.cloudvision.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vrcvp.cloudvision.Config;
import com.vrcvp.cloudvision.bean.req.SearchReq;
import com.vrcvp.cloudvision.bean.resp.QueryProductResp;
import com.vrcvp.cloudvision.bean.resp.QueryVideoResp;
import com.vrcvp.cloudvision.db.HttpCacheDBUtils;
import com.vrcvp.cloudvision.http.HttpAsyncTask;
import com.vrcvp.cloudvision.http.HttpStatus;
import com.vrcvp.cloudvision.utils.DataManager;
import com.vrcvp.cloudvision.utils.NetworkManager;
import com.vrcvp.cloudvision.utils.StringUtils;

/**
 * 云展视频搜索Model
 * Created by yinglovezhuzhu@gmail.com on 2016/9/29.
 */

public class VideoSearchModel implements IVideoSearchModel {

    private Context mContext;
    private HttpAsyncTask<QueryVideoResp> mSearchTask;

    public VideoSearchModel(Context context) {
        this.mContext = context;
    }

    @Override
    public void searchVideo(String keyword, int pageNo, final HttpAsyncTask.Callback<QueryVideoResp> callback) {
        final String url = Config.API_VIDEO_SEARCH;
        final SearchReq reqParam = new SearchReq();
        reqParam.setEnterpriseId(DataManager.getInstance().getCorporateId());
        reqParam.setToken(DataManager.getInstance().getToken());
        reqParam.setKeywords(keyword);
        final Gson gson = new Gson();
        final String key = gson.toJson(reqParam);
        if(NetworkManager.getInstance().isNetworkConnected()) {
            mSearchTask = new HttpAsyncTask<>();
            mSearchTask.doPost(url, reqParam,
                    QueryVideoResp.class, new HttpAsyncTask.Callback<QueryVideoResp>() {
                        @Override
                        public void onPreExecute() {
                            if(null != callback) {
                                callback.onPreExecute();
                            }
                        }

                        @Override
                        public void onCanceled() {
                            if(null != callback) {
                                callback.onCanceled();
                            }
                        }

                        @Override
                        public void onResult(QueryVideoResp result) {
                            // 保存接口请求缓存，只有在请求成功的时候才保存
                            if(HttpStatus.SC_OK == result.getHttpCode()) {
                                HttpCacheDBUtils.saveHttpCache(mContext, url, key, gson.toJson(result));
                            }

                            if(null != callback) {
                                callback.onResult(result);
                            }
                        }
                    });
        } else {
            QueryVideoResp result;
            String data = HttpCacheDBUtils.getHttpCache(mContext, url, key);
            if(StringUtils.isEmpty(data)) {
                // 错误
                if(null != callback) {
                    result = new QueryVideoResp(HttpStatus.SC_CACHE_NOT_FOUND, "Cache not found");
                    callback.onResult(result);
                }
            } else {
                try {
                    // 解析数据
                    result = new Gson().fromJson(data, QueryVideoResp.class);
                    if(null != callback) {
                        callback.onResult(result);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    if(null != callback) {
                        result = new QueryVideoResp(HttpStatus.SC_CACHE_NOT_FOUND, "Cache not found");
                        callback.onResult(result);
                        // 解析错误的，删除掉记录
                        HttpCacheDBUtils.deleteHttpCache(mContext, url, key);
                    }
                }
            }
        }
    }

    @Override
    public void cancelSearchVideo() {
        if(null != mSearchTask) {
            mSearchTask.cancel();
            mSearchTask = null;
        }
    }
}
