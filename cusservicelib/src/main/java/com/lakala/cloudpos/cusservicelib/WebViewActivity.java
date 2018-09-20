package com.lakala.cloudpos.cusservicelib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebViewActivity extends Activity {

    //5.0之后回调
    private ValueCallback<Uri[]> mFilePathCallback;

    //5.0之前回调
    private ValueCallback<Uri> mUploadMessage;

    private WebView mWebView;

    //加载url key
    public static final String URL_KEY = "load_url";

    //加载框
    private Dialog mProgressDialog;

    private TextView mTvProgressTitle;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private RelativeLayout mRlBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_layout);

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mWebView = findViewById(R.id.webView);

        mTvTitle = findViewById(R.id.tvTitle);
        mIvBack = findViewById(R.id.ivBack);
        mRlBg = findViewById(R.id.rlBg);

        final WebSettings webSettings = mWebView.getSettings();

        //移除 引起远程代码执行漏洞
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        //支持javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");//设置页面默认编码为utf-8
        webSettings.setSupportZoom(false);
        //不保存密码
        webSettings.setSavePassword(false);

        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);

        // 设置允许开启多窗口
        webSettings.setSupportMultipleWindows(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mProgressDialog.dismiss();
                } else {
                    showProgressDialog(String.valueOf("进度:" + newProgress + "%"));
                }

                super.onProgressChanged(view, newProgress);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                takeImage();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                takeImage();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                takeImage();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = fileChooserParams.createIntent();
                    try {
                        startActivityForResult(intent, 0x01);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        mFilePathCallback = null;
                    }
                }

                return false;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //禁止 file 协议加载 JavaScript
                if (url.startsWith("file://")) {
                    webSettings.setJavaScriptEnabled(false);
                } else {
                    webSettings.setJavaScriptEnabled(true);
                }

                //处理弹窗问题
                if (url.contains("closeWindows")) {
                    System.exit(0);
                }
                return false;
            }
        });

        String url = getIntent().getStringExtra(URL_KEY);

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_KEY + " param is null");
        }
        mWebView.loadUrl(url);
    }

    /**
     * 选择文件
     */
    private void takeImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "请选择");
        this.startActivityForResult(chooserIntent, 0x02);
    }

    /**
     * 文件选中后回调
     *
     * @param requestCode 请求code
     * @param resultCode  返回code
     * @param data        数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 0x01) {
                if (mFilePathCallback == null)
                    return;
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mFilePathCallback = null;
            }
        } else if (requestCode == 0x02) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    /**
     * 显示加载框
     *
     * @param text 显示文字
     */
    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(this, R.style.DialogTheme);

            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress_layout, null, false);

            mTvProgressTitle = view.findViewById(R.id.tv_progress);

            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setContentView(view);

            Window window = mProgressDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }

        if (TextUtils.isEmpty(text)) {
            mTvProgressTitle.setVisibility(View.GONE);
        } else {
            mTvProgressTitle.setVisibility(View.VISIBLE);
            mTvProgressTitle.setText(text);
        }

        if (!isFinishing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 后退按钮view
     *
     * @return view
     */
    public ImageView getBackView() {
        return mIvBack;
    }

    /**
     * 标题view
     *
     * @return view
     */
    public TextView getTitleView() {
        return mTvTitle;
    }

    /**
     * 标题栏view
     *
     * @return view
     */
    public RelativeLayout getToolBarView() {
        return mRlBg;
    }

    /**
     * 后退事件
     *
     * @param v view
     */
    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webView会内存泄漏 单独开的进程 所以杀死进程
        System.exit(0);
    }
}
