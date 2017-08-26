package com.kazyle.hgclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.kazyle.hgclient.callback.RequestCallback;
import com.kazyle.hgclient.callback.data.ResponseCode;
import com.kazyle.hgclient.callback.data.ResponseEntity;
import com.kazyle.hgclient.domain.Article;
import com.kazyle.hgclient.domain.ArticleConfig;
import com.kazyle.hgclient.helper.entity.UrlView;
import com.kazyle.hgclient.util.AppHelper;
import com.kazyle.hgclient.util.HttpUtils;
import com.kazyle.hgclient.util.Md5Utils;
import com.kazyle.hgclient.util.XUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Kazyle on 2017/2/14.
 */
public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private EditText url;
    private Button visitUrlBtn;
    private Button collapseBtn;
    private Button getUrlBtn;
    SweetAlertDialog loading = null;
    SweetAlertDialog errorDialog = null;
    private int index = 0;
    private List<String> platforms = null;
    private String platform = null;
    private Integer type = null;
    private String wechat = null;
    private EventHandler handler = null;
    public static Map<String, String> headers = new HashMap<>();

    private static final int GET_URL_MSG = 1;
    private static final int SHOW_DOC_HTML = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_layout);
        webView = (WebView) findViewById(R.id.webview);
        visitUrlBtn = (Button) findViewById(R.id.visitUrlBtn);
        collapseBtn = (Button) findViewById(R.id.collapse);
        getUrlBtn = (Button) findViewById(R.id.getUrlBtn);
        visitUrlBtn.setOnClickListener(this);
        collapseBtn.setOnClickListener(this);
        getUrlBtn.setOnClickListener(this);
        handler = new EventHandler();
        headers.put("X-Requested-With", "");

        // Enable some feature like Javascript and pinch zoom
        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setJavaScriptCanOpenWindowsAutomatically(true);
        websettings.setSupportZoom(true);
        websettings.setUseWideViewPort(true);
        websettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        websettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websettings.setLoadWithOverviewMode(true);
        websettings.setAppCacheEnabled(true);
        websettings.setDomStorageEnabled(true);

//        websettings.setUserAgent(websettings.getUserAgentString() + " MicroMessenger/6.5.4.1000 NetType/WIFI Language/zh_CN");
        websettings.setUserAgent(AppHelper.getUserAgent());

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        CookieSyncManager.getInstance().sync();

        webView.setWebViewClient(new WebViewClient() {
            // Load opened URL in the application instead of standard browser
            // application
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, headers);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            public void onProgressChanged(WebView view, int progress) {
                BrowserActivity.this.setProgress(progress * 100);
            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        url = (EditText) findViewById(R.id.url);
        url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            openUrl();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        init();
    }

    private void init() {
        File file = new File(AppHelper.ARTICLE_CONFIG_PATH);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                StringBuilder json = new StringBuilder("");
                String str = null;
                while ((str = reader.readLine()) != null) {
                    json.append(str);
                }
                ArticleConfig config = JSON.parseObject(json.toString(), ArticleConfig.class);
                type = config.getType();
                platforms = config.getPlatforms();
                wechat = config.getWechat();
            } catch (IOException e) {
            } finally {
                if (reader == null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


    private void openUrl() {
        String inputUrl = url.getText().toString();
//        inputUrl = "http://100126.com.cn/1.html?SHNldmcsMjQ2MTkxLExOSSw2NTg2LDAyODU0LCZ6eGM9MTQ5NzU0MzE5Mw==";
//        platform = "瞎转";
        /*platform = null;
        if ("瞎转".equals(platform)) {
            final String u = "NaN";
            String id = inputUrl.substring(inputUrl.indexOf('?') + 1);
            try {
                id = URLEncoder.encode(id, "utf-8");
                inputUrl = URLEncoder.encode(inputUrl, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            final String finalId = id;
            final String finalInputUrl = inputUrl;
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        String result = HttpUtils.getUserAgent("http://ajax.xiazhuan.cc/?m=api&a=abc", "&u=" + u + "&id=" + finalId + "&h=" + finalInputUrl);
                        result = result.substring(1, result.length() - 1);
                        UrlView view = JSON.parseObject(result, UrlView.class);
                        Document doc = Jsoup.parse(new URL(view.getMsg()), 10000);
                        try {
                            Elements els = doc.getAllElements();
                            for (Element e : els) {
                                if ("list".equals(e.className())) {
                                    e.remove();
                                    break;
                                }
                            }
                            doc.getElementById("hengfu_top").remove();
                            doc.getElementById("wenzi_top").remove();
                            doc.getElementById("wenzi_bottom").remove();
                            doc.getElementById("hengfu_bottom").remove();
                            doc.getElementById("ad_top").remove();
                        } catch (Exception e) {
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = SHOW_DOC_HTML;
                        msg.obj = doc.outerHtml();
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                    }
                }
            });
            thread.start();
        } else {
            webView.loadUrl(inputUrl, headers);
        }*/
        webView.loadUrl(inputUrl, headers);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                url.getWindowToken(), 0);
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i ++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getUrlBtn:
                getUrl();
                break;
            case R.id.visitUrlBtn:
                openUrl();
                break;
            case R.id.collapse:
                collapse();
                break;
        }
    }

    private void getUrl() {
        String _url = AppHelper.API_URL + "/api/article/index";
        Map<String, String> params = new HashMap<>();
        String platform = null;
        if (platforms != null && platforms.size() > 0) {
            if ((index + 1) > platforms.size()) {
                index = 0;
            }
            platform = platforms.get(index);
        }
        String _type = null;
        if (type != null) {
            _type = type + "";
        }
        params.put("userId", AppHelper.USER_ID);
        params.put("platform", platform);
        params.put("type", _type);
        params.put("wechat", wechat);
        XUtils.Get(_url, params, new RequestCallback<ResponseEntity>() {

            @Override
            public void onStarted() {
                super.onStarted();
                loading = new SweetAlertDialog(BrowserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loading.setTitleText("正在获取");
                loading.show();
            }

            @Override
            public void onSuccess(ResponseEntity result) {
                loading.dismiss();
                if (result.getCode() == ResponseCode.SUCCESS.getValue()) {
                    // 发送数据处理消息
                    if (result.getObj() == null) {
                        String msg = result.getMsg();
                        if (msg == null || msg.trim().length() == 0) {
                            msg = "服务繁忙，下载手机数据失败！";
                        }
                        errorDialog = new SweetAlertDialog(BrowserActivity.this, SweetAlertDialog.ERROR_TYPE);
                        errorDialog.setTitleText(msg).show();
                    } else {
                        String json = JSON.toJSONString(result.getObj());
                        Article pojo = JSON.parseObject(json, Article.class);
                        // 发送数据处理消息
                        Message msg = handler.obtainMessage();
                        msg.what = GET_URL_MSG;
                        msg.obj = pojo;
                        handler.sendMessage(msg);
                        index++;
                    }
                } else {
                    String msg = result.getMsg();
                    if (msg == null || msg.trim().length() == 0) {
                        msg = "服务繁忙，下载手机数据失败！";
                    }
                    loading.dismiss();
                    errorDialog = new SweetAlertDialog(BrowserActivity.this, SweetAlertDialog.ERROR_TYPE);
                    errorDialog.setTitleText(msg).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                errorDialog.setTitleText("服务繁忙，下载手机数据失败！").show();
            }
        });
    }

    private void collapse() {
        String _fg = Md5Utils.hash("" + System.currentTimeMillis());
        Random random = new Random();
        int px = random.nextInt(700);
        if (px <= 700) {
            px+= 100;
        }
        int py = random.nextInt(1100);
        if (py < 300) {
            py += 100;
        }
        String zz1 = getBetaGamma(random);
        String zz2 = getBetaGamma(random);
        String zz3 = getBetaGamma(random);
        String ajax = "";
        if ("瞎转".equals(platform)) {
            String requestUrl = url.getText().toString();
            ajax = "$.ajax({async:false,type:'GET',url:'http://ajax.xiazhuan.cc/?m=zhuanfa&a=get_id',jsonp:'callback',data:{'dddd':dddd,'sq':sq,'zid':zid,'id':zf_id,'u':u,'_tt':new Date().getTime(),'j':hrefa,'href':'"+requestUrl+"'},dataType:'jsonp',success:function(a){lai=parseInt(a.lai);_c=a._c;if(parseInt(ios)==1){$('.in-ios').show()}else $('.in-android').show();var b=navigator.userAgent;if(b.indexOf('MicroMessenger')>-1){$('.in-weixin-show').show()}else $('.no-weixin-show').show();$('.about-content').nextAll('div.aa').css('padding-top',(Math.random()*12+3))}});$.getJSON('http://ajax.xiazhuan.cc/?m=zhuanfa&a=qw&u='+u+'&zid='+zid+'&href='+location.href+'&j='+j+'&callback=?&_tt='+new Date().getTime(),function(a){initCollapse(parseInt(njs.sex),a._qw,a._qwc);if(parseInt(j)==1){init_wx(a._wx,B.decode(njs.zf_name),njs.zf_img,B.decode(njs.zf_jianjie))}});";
        }
//        String js = "dddd=1;$('.onread').trigger('click');_fg='"+_fg+"';px="+px+";py="+py+";_dd=0.59485241;$(window).scroll(function(){b=$(this).scrollTop();var c=$(document).height();_dd=(b/c);is_scroll=1});setTimeout(function(){$('html,body').animate({scrollTop:$(document).height()/4},2000);_px=Math.round(px);_py=Math.round(py);try{ox=_px;oy=_py;if(_dd==0){_dd=0.6894571174}cm++;zz='"+zz1+"';stay()}catch(e){};try{_pm++;_pcm_flag=0;pcm()}catch(e){};try{window.setTimeout('interval();',1000);$('.dwss').parent().remove();$('#box').css('height','initial');$('.dwss').trigger('click');$('.dwss').parent().trigger('click')}catch(e){};setTimeout(function(){try{ox=_px+10;oy=_py+100;_img=1;cm++;zz='"+zz2+"';stay()}catch(e){};$('.collapse a').trigger('click');try{_pm++;_pcm_flag=0;pcm()}catch(e){};try{haveTouched=true;$('#'+btnid).trigger('click')}catch(e){};$('html,body').animate({scrollTop:$(document).height()/2},2000);setTimeout(function(){try{ox=_px+50;oy=_py+150;cm++;zz='"+zz3+"';stay()}catch(e){};$('html,body').animate({scrollTop:$(document).height()},2000);setTimeout(function(){scrollTop:($(document).height()/3)*2},10000)},5000)},5000)},3000);";
        String js = "dddd=1;_video=1;is_touch=1;juli=1;down=1;jifei=0;is_touch=2;minus=40;iframe=1;$('.onread').trigger('click');_fg='"+_fg+"';res='"+_fg+"';px="+px+";py="+py+";_dd=0.59485241;$(window).scroll(function(){b=$(this).scrollTop();var c=$(document).height();_dd=(b/c);is_scroll=1});setTimeout(function(){"+ajax+"$('html,body').animate({scrollTop:$(document).height()/4},2000);_px=Math.round(px);_py=Math.round(py);try{ox=_px;oy=_py;if(_dd==0){_dd=0.6894571174}cm++;zz='"+zz1+"';stay()}catch(e){};try{_pm++;_pcm_flag=0;pcm()}catch(e){};try{window.setTimeout('interval();',1000);$('.dwss').parent().remove();$('#box').css('height','initial');$('.dwss').trigger('click');$('.dwss').parent().trigger('click')}catch(e){};setTimeout(function(){try{$('.unfold-field').trigger('click')}catch(e){}try{ox=_px+10;oy=_py+100;_img=1;cm++;zz='"+zz2+"';stay()}catch(e){};$('.collapse a').trigger('click');try{_pm++;_pcm_flag=0;pcm()}catch(e){};try{haveTouched=true;$('#'+btnid).trigger('click')}catch(e){};$('html,body').animate({scrollTop:$(document).height()/2},2000);setTimeout(function(){try{ox=_px+50;oy=_py+150;cm++;zz='"+zz3+"';stay()}catch(e){};$('html,body').animate({scrollTop:$(document).height()},2000);setTimeout(function(){scrollTop:($(document).height()/3)*2},10000)},5000)},5000)},3000);";
        webView.loadUrl("javascript:" + js);
    }

    private String getBetaGamma(Random random) {
        int beta = random.nextInt(90) + 10;
        int gamma = random.nextInt(10) - 1;
        return beta + "|" + gamma;
    }

    // 消息处理
    class EventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_URL_MSG: // 更新脚本
                    try {
                        Article pojo = (Article) msg.obj;
                        String urlString = pojo.getUrl();
                        platform = pojo.getPlatform();
                        url.setText(urlString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_DOC_HTML:
                    String html = (String) msg.obj;
                    webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    break;
            }
        }
    }
}
