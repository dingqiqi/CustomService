package com.lakala.cloudpos.cusservicelib;

import android.app.Activity;
import android.content.Intent;

public class ServiceManager {
    /**
     * 启动在新客服
     *
     * @param activity 活动
     * @param loadUrl  客服url
     */
    public static void startCustomService(Activity activity, String loadUrl) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(WebViewActivity.URL_KEY, loadUrl);
        activity.startActivity(intent);
    }

}
