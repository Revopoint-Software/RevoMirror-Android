package com.limelight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.limelight.ui.activity.BaseActivity;
import com.limelight.utils.LanguageUtils;
import com.limelight.utils.LogUtil;
import com.limelight.utils.ScreenUtil;
import com.limelight.utils.StatusBarUtil;
import com.pdfview.PDFView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TutorialActivity extends BaseActivity {
    public static final String TYPE_USER_LISENCE = "1";
    public static final String TYPE_PRIVACY_POLICY = "2";
    public static final String TYPE_GPLV3 = "3";

    public static void startWebView(Context ctx, String url) {
        Intent intent = new Intent(ctx, TutorialActivity.class);
        //intent.putExtra("title", title);
        intent.putExtra("url", url);
        ctx.startActivity(intent);
    }

    public static void startWebView(Context ctx, String url, String title) {
        Intent intent = new Intent(ctx, TutorialActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        ctx.startActivity(intent);
    }

    private TextView tvTitle;
    //private View ivShare;
    private WebView webView;
    private PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        initView();
        initData();
    }

    private void initView() {
        //适配刘海屏
        ScreenUtil.adapterDisplayCutout(findViewById(R.id.contentView));
        StatusBarUtil.setDefaultStatusBarStyle(this);

        tvTitle = findViewById(R.id.tvTitle);
        View ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*ivShare = findViewById(R.id.ivShare);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getIntent().getStringExtra("url");
                if (TextUtils.equals(url, TYPE_USER_LISENCE)) {
                    String path = "file:///android_asset/lisence/lisence_" + LanguageUtils.getLangName() + ".pdf";
                    String fileName = "lisence_" + LanguageUtils.getLangName() + ".pdf";
                    if (!isPathExistInAsset(path)) {
                        final String DEFAULT_LANGNAME = "en_US";
                        fileName = "lisence_" + DEFAULT_LANGNAME + ".pdf";
                    }

                    String sharePath = PathConfig.PATH_TEMP_FILE + fileName;
                    FileUtil.makeFolder(PathConfig.PATH_TEMP_FILE);
                    FileUtil.copyAssetsToDst(TutorialActivity.this, "lisence/" + fileName, sharePath);

                    ShareUtils.INSTANCE.shareFile(TutorialActivity.this, sharePath);
                } else if (TextUtils.equals(url, TYPE_PRIVACY_POLICY)) {
                    String path = "file:///android_asset/privacy/privacy_" + LanguageUtils.getLangName() + ".pdf";
                    String fileName = "privacy_" + LanguageUtils.getLangName() + ".pdf";
                    if (!isPathExistInAsset(path)) {
                        final String DEFAULT_LANGNAME = "en_US";
                        fileName = "privacy_" + DEFAULT_LANGNAME + ".pdf";
                    }

                    String sharePath = PathConfig.PATH_TEMP_FILE + fileName;
                    FileUtil.makeFolder(PathConfig.PATH_TEMP_FILE);
                    FileUtil.copyAssetsToDst(TutorialActivity.this, "privacy/" + fileName, sharePath);

                    ShareUtils.INSTANCE.shareFile(TutorialActivity.this, sharePath);
                } else if (TextUtils.equals(url, TYPE_USER_GUIDE)) {
                    String path = "file:///android_asset/userGuide/userGuide_" + LanguageUtils.getLangName() + ".pdf";
                    String fileName = "userGuide_" + LanguageUtils.getLangName() + ".pdf";
                    if (!isPathExistInAsset(path)) {
                        final String DEFAULT_LANGNAME = "en_US";
                        fileName = "userGuide_" + DEFAULT_LANGNAME + ".pdf";
                    }

                    String sharePath = PathConfig.PATH_TEMP_FILE + fileName;
                    FileUtil.makeFolder(PathConfig.PATH_TEMP_FILE);
                    FileUtil.copyAssetsToDst(TutorialActivity.this, "userGuide/" + fileName, sharePath);

                    ShareUtils.INSTANCE.shareFile(TutorialActivity.this, sharePath);
                }
            }
        });*/

        webView = findViewById(R.id.webview);
        setWebView();

        pdfView = findViewById(R.id.pdfView);
    }

    private void initData() {
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        tvTitle.setText(title);
        final String DEFAULT_LANGNAME = "en_US";
        if (TextUtils.equals(url, TYPE_USER_LISENCE)) {
            //ivShare.setVisibility(View.VISIBLE);

            //用户协议
            String path = "lisence/lisence_" + LanguageUtils.getLangName() + ".pdf";
            if (!isPathExistInAsset(path)) {
                path = "lisence/lisence_" + DEFAULT_LANGNAME + ".pdf";
            }

            webView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);

            pdfView.fromAsset(path);
            pdfView.show();
        } else if (TextUtils.equals(url, TYPE_PRIVACY_POLICY)) {
            //ivShare.setVisibility(View.VISIBLE);

            //隐私政策
            String path = "privacy/privacy_" + LanguageUtils.getLangName() + ".pdf";
            if (!isPathExistInAsset(path)) {
                path = "privacy/privacy_" + DEFAULT_LANGNAME + ".pdf";
            }

            webView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);

            pdfView.fromAsset(path);
            pdfView.show();
        } else if (TextUtils.equals(url, TYPE_GPLV3)) {
            //ivShare.setVisibility(View.VISIBLE);

            //隐私政策
            String path = "gplv3/gplv3.pdf";

            webView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);

            pdfView.fromAsset(path);
            pdfView.show();
        } else {
            if (!TextUtils.isEmpty(url)) {
                LogUtil.i("=================" + url);
                webView.loadUrl(url);
            }
        }
    }

    /**
     * 判断文件路径是否存在asset中
     *
     * @param path
     * @return
     */
    private boolean isPathExistInAsset(String path) {
        if (TextUtils.isEmpty(path)) return false;
        String ASSET_PREFIX = "file:///android_asset/";
        if (path.startsWith(ASSET_PREFIX)) {
            path = path.substring(ASSET_PREFIX.length());
        }
        String dir = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/") + 1);
        AssetManager am = getAssets();
        //LogUtil.i("==============="+path+", "+dir + ", " + name);
        try {
            String[] fileNameList = am.list(dir);
            for (String fileName : fileNameList) {
                //LogUtil.i("==============="+fileName);
                if (TextUtils.equals(name, fileName)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void openURL(String path) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(path);
        intent.setData(url);
        startActivity(intent);
    }

    private void openEmail(String path) {
        String url = path.replace("mailto:", "");
        // 邮箱正则表达式
        String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(url);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text"); // emailIntent.setType("message/rfc822");
            // //真机上使用
            String[] emailReciver = new String[]{url};
            // 设置邮件默认地址
            emailIntent.putExtra(Intent.EXTRA_EMAIL,
                    emailReciver);
            // 设置邮件默认标题
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "");
            // 设置要默认发送的内容
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            // 调用系统的邮件系统
            startActivity(Intent
                    .createChooser(emailIntent, "请选择邮件发送软件"));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(webViewClient);
        webSettings.setJavaScriptEnabled(true); //是否开启JS支持
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //是否允许JS打开新窗口
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true); //缩放至屏幕大小
        webSettings.setLoadWithOverviewMode(true); //缩放至屏幕大小
        webSettings.setSupportZoom(true); //是否支持缩放
        webSettings.setBuiltInZoomControls(true); //是否支持缩放变焦，前提是支持缩放
        webSettings.setDisplayZoomControls(false); //是否隐藏缩放控件

        webSettings.setAllowFileAccess(false); //是否允许访问文件
        webSettings.setDomStorageEnabled(true); //是否节点缓存
        webSettings.setDatabaseEnabled(true); //是否数据缓存
        //webSettings.setAppCacheEnabled(true); //是否应用缓存
//        webSettings.setAppCachePath(uri); //设置缓存路径

        webSettings.setMediaPlaybackRequiresUserGesture(false); //是否要手势触发媒体
        webSettings.setStandardFontFamily("sans-serif"); //设置字体库格式
        webSettings.setFixedFontFamily("monospace"); //设置字体库格式
        webSettings.setSansSerifFontFamily("sans-serif"); //设置字体库格式
        webSettings.setSerifFontFamily("sans-serif"); //设置字体库格式
        webSettings.setCursiveFontFamily("cursive"); //设置字体库格式
        webSettings.setFantasyFontFamily("fantasy"); //设置字体库格式
        webSettings.setTextZoom(100); //设置文本缩放的百分比
        webSettings.setMinimumFontSize(8); //设置文本字体的最小值(1~72)
        webSettings.setDefaultFontSize(16); //设置文本字体默认的大小

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //按规则重新布局
        webSettings.setLoadsImagesAutomatically(true); //是否自动加载图片
        webSettings.setDefaultTextEncodingName("UTF-8"); //设置编码格式
        webSettings.setNeedInitialFocus(true); //是否需要获取焦点
        webSettings.setGeolocationEnabled(false); //设置开启定位功能
        //webSettings.setBlockNetworkLoads(true); //是否从网络获取资源
        // binding.webview.addJavascriptInterface(this, "bridge");  //建立通讯桥梁
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onBackPressed() {
        boolean back = webView.canGoBack(); //判断网页是否可以回退
        if (back)
            webView.goBack(); //回退一页
        else
            super.onBackPressed();
    }

    @JavascriptInterface
    public void openEx(String url) {

    }

    @Override
    protected void onDestroy() {
        webView.removeJavascriptInterface("bridge"); //移除通讯桥梁
        webView.destroy();
        super.onDestroy();
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.i("=============" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http") || url.startsWith("https")) {
                // openURL(url);
                LogUtil.i("=============" + url);
                return false;
            } else if (url.startsWith("mailto")) {
                //openEmail(url);
                return true;
            } else if (url.startsWith("file:///android_asset/")) {
                //view.loadUrl(url);
                return false;
            }
            return super.shouldOverrideUrlLoading(view, url); //消费事件终止传递
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("http") || url.startsWith("https")) {
                // openURL(url);
                LogUtil.i("=============" + url);
                return false;
            } else if (url.startsWith("mailto")) {
                //openEmail(url);
                return true;
            } else if (url.startsWith("file:///android_asset/")) {
                //view.loadUrl(url);
                return false;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }
    };
}