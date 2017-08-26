package com.kazyle.hgclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kazyle.hgclient.callback.RequestCallback;
import com.kazyle.hgclient.callback.data.ResponseCode;
import com.kazyle.hgclient.callback.data.ResponseEntity;
import com.kazyle.hgclient.converter.ScriptTypeConverter;
import com.kazyle.hgclient.entity.Apk;
import com.kazyle.hgclient.entity.FirVersion;
import com.kazyle.hgclient.entity.Script;
import com.kazyle.hgclient.entity.ScriptType;
import com.kazyle.hgclient.exception.ClearAppDataException;
import com.kazyle.hgclient.util.AppHelper;
import com.kazyle.hgclient.util.FileUtils;
import com.kazyle.hgclient.util.XUtils;
import com.kazyle.hgclient.widget.HGProgressBar;

import org.xutils.DbManager;
import org.xutils.db.converter.ColumnConverterFactory;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

//import com.beardedhen.androidbootstrap.TypefaceProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "[HGHleper]";



    private static final int SCRIPT_MSG = 1;
    private static final int PHONEDATA_MSG = 2;
    private static final int INIT_APK = 3;

    private SharedPreferences sharedPreferences = null;
    private Button scriptBtn;
    private Button apkBtn;
    private Button phonedataBtn;
    private Button checkUpgradeBtn;
    private Button cleanCacheBtn;
    private Button browserBtn;
    private EventHandler handler = null;
    HGProgressBar progressbar = null;
    SweetAlertDialog loading = null;
    SweetAlertDialog successDialog = null;
    SweetAlertDialog errorDialog = null;
    int downloadIndex = 1;
    private List<String> appList = new ArrayList<>();

    static {
        ColumnConverterFactory.registerColumnConverter(ScriptType.class, new ScriptTypeConverter());
    }

    DbManager.DaoConfig dbConfig = new DbManager.DaoConfig()
            .setDbDir(new File(AppHelper.HGHELPER_PACKAGE + "/db"))
            .setDbName("hg_helper.db")
            .setDbVersion(1)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    db.getDatabase().enableWriteAheadLogging();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUpgradeBtn = (Button) findViewById(R.id.checkUpgradeBtn);
        scriptBtn = (Button) findViewById(R.id.scriptBtn);
        apkBtn = (Button) findViewById(R.id.apkBtn);
        phonedataBtn = (Button) findViewById(R.id.phonedataBtn);
        cleanCacheBtn = (Button) findViewById(R.id.cleanCacheBtn);
        browserBtn = (Button) findViewById(R.id.browser);
        cleanCacheBtn.setTextColor(Color.BLACK);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkUpgradeBtn.setOnClickListener(this);
        scriptBtn.setOnClickListener(this);
        apkBtn.setOnClickListener(this);
        phonedataBtn.setOnClickListener(this);
        cleanCacheBtn.setOnClickListener(this);
        browserBtn.setOnClickListener(this);
        handler = new EventHandler();
        AppHelper.getRoot();
        init();
    }

    private void init() {
        File file = new File(AppHelper.HGHELPER_PACKAGE + "/dl");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(AppHelper.HGHELPER_PACKAGE + "/db");
        if (!file2.exists()) {
            file2.mkdirs();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkUpgradeBtn:
                checkUpgrade();
                break;
            case R.id.scriptBtn: // 更新脚本
                upgradeScript();
                break;
            case R.id.apkBtn: // 下载必备资源
//                getInitApk();
                chooseApkForDownload();
                break;
            case R.id.phonedataBtn: // 下载008数据
                getPhonedata();
                break;
            case R.id.cleanCacheBtn: // 清理缓存
                cleanCache();
                break;
            case R.id.browser: // 微信浏览器
                openWchatBrowser();
                break;
        }
    }

    private void openWchatBrowser() {
        Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private void chooseApkForDownload() {
        String signature = AppHelper.signature(AppHelper.signature(this));
        String url = AppHelper.API_URL + "/api/apk/list";
        Map<String, String> params = new HashMap<>();
        params.put("userId", AppHelper.USER_ID);
        params.put("signature", signature);
        // 获取APK列表
        XUtils.Get(url, params, new RequestCallback<ResponseEntity>() {

            @Override
            public void onStarted() {
                loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loading.setTitleText("获取资源列表");
                loading.show();
            }

            @Override
            public void onSuccess(ResponseEntity result) {
                if (result.getCode() == ResponseCode.SUCCESS.getValue()) {
                    JSONArray jsonArray = (JSONArray) result.getObj();
                    List<Apk> apks = JSONObject.parseArray(jsonArray.toJSONString(), Apk.class);
                    if (apks == null || apks.isEmpty()) {
                        Toast.makeText(MainActivity.this, "请先上传APK应用", Toast.LENGTH_SHORT).show();
                    } else {
                        loading.dismiss();
                        Intent intent = new Intent(MainActivity.this, ApkActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("apks", (Serializable)apks);
                        intent.putExtras(bundle);
                        MainActivity.this.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFinished() {
                if (loading.isShowing()) {
                    loading.dismiss();
                }
            }
        });
        // 选择apk下载
    }

    // 清理缓存
    private void cleanCache() {
        boolean result = true;
        // 获取所有安装的非系统应用
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String packageName = info.packageName;
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = AppHelper.getApk(packageName);
                if (appName == null) {
                    try {
                        AppHelper.clearAppData(packageName);
                        Context otherAppContext = MainActivity.this.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
                        File path = otherAppContext.getCacheDir();
                        if (path != null && path.exists()) {
                            String killer ="rm -r " + path.toString();
                            AppHelper.execRuntimeProcess(killer);
                        }
                    } catch (ClearAppDataException e) {
                        result = false;
                        break;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (result) {
            cleanCacheBtn.setTextColor(Color.BLUE);
        } else {
            cleanCacheBtn.setTextColor(Color.RED);
        }

    }

    private void cleanCache(String packageName) {
        try {
            String killer =" pm clear " + packageName;
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os =new DataOutputStream(p.getOutputStream());
            os.writeBytes(killer.toString() +"\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUpgrade() {
        // FIR检查更新
        FIR.checkForUpdateInFIR(AppHelper.FIR_API_TOKEN, new VersionCheckCallback() {
            @Override
            public void onSuccess(String versionJson) {
                Log.i("fir","check from fir.im success! " + "\n" + versionJson);
                try {
                    final FirVersion version = JSON.parseObject(versionJson, FirVersion.class);
                    PackageManager pm = getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
                    String versionName = pi.versionName;
                    int versioncode = pi.versionCode;
                    if (version.checkVersionName(versionName)) {
                        // 更新
                        SweetAlertDialog mDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        mDialog.setTitleText("发现新版本：V" + version.getVersionShort())
                                .setContentText("更新内容：\n" + version.getChangelog())
                                .setCancelText("忽略")
                                .setConfirmText("更新")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        String path = AppHelper.HGHELPER_PACKAGE + "/dl/" + version.getName() + "_V" + version.getVersionShort() + ".apk";
                                        XUtils.DownLoadFile(version.getInstallUrl(), path, new RequestCallback<File>() {

                                            @Override
                                            public void onStarted() {
                                                progressbar = new HGProgressBar(MainActivity.this);
                                                progressbar.initDialog();
                                            }

                                            @Override
                                            public void onLoading(long total, long current, boolean isDownloading) {
                                                if (current <= total) {
                                                    progressbar.pro1.setMax((int) total);
                                                    progressbar.setProgress((int) current);
                                                }
                                            }

                                            @Override
                                            public void onFinished() {
                                                if (progressbar.isShowing()) {
                                                    progressbar.closeDialog();
                                                }
                                            }

                                            @Override
                                            public void onSuccess(File result) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setDataAndType(Uri.parse("file://" + result.getPath()),"application/vnd.android.package-archive");
                                                MainActivity.this.startActivity(intent);
                                            }
                                        });
                                    }
                                }).show();
                    } else {
                        successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("当前已是最新版本 V" + version.getVersionShort()).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception exception) {
                Log.i("fir", "check fir.im fail! " + "\n" + exception.getMessage());
            }

            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(), "正在获取", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //Toast.makeText(getApplicationContext(), "获取完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新脚本
     */
    private void upgradeScript() {
        String signature = AppHelper.signature(AppHelper.signature(this));
        final DbManager db = x.getDb(dbConfig);
        // 获取已有的脚本ID
        String scriptIds = "";
        try {
            List<Script> scripts = db.findAll(Script.class);
            List<Long> ids = new ArrayList<>();
            if (scripts != null && !scripts.isEmpty()) {
                for (Script script : scripts) {
                    ids.add(script.getId());
                }
                scriptIds = AppHelper.join(ids.iterator(), ',');
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        String url = AppHelper.API_URL + "/api/script/upgrade";
        Map<String, String> params = new HashMap<>();
        params.put("userId", AppHelper.USER_ID);
        params.put("scriptIds", scriptIds);
        params.put("signature", signature);
        XUtils.Get(url, params, new RequestCallback<ResponseEntity>() {

            @Override
            public void onStarted() {
                super.onStarted();
                loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loading.setTitleText("正在更新");
                loading.show();
            }

            @Override
            public void onSuccess(ResponseEntity result) {
                if (result.getCode() == ResponseCode.SUCCESS.getValue()) {
                    String data = JSONArray.toJSONString(result.getObj());
                    List<Script> list = JSON.parseArray(data, Script.class);
                    if (list == null || list.isEmpty()) {
                        loading.dismiss();
                        successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("没有更新的脚本了~").show();
                    } else {
                        // 发送数据处理消息
                        Message msg = handler.obtainMessage();
                        msg.what = SCRIPT_MSG;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }

                } else {
                    String msg = result.getMsg();
                    if (msg == null || msg.trim().length() == 0) {
                        msg = "服务繁忙，脚本更新失败！";
                    }
                    loading.dismiss();
                    errorDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                    errorDialog.setTitleText(msg).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                errorDialog.setTitleText("服务繁忙，脚本更新失败！").show();
            }
        });
    }

    /**
     * 下载008数据
     */
    private void getPhonedata() {
        String signature = AppHelper.signature(AppHelper.signature(this));
        String url = AppHelper.API_URL + "/api/phonedata/get";
        Map<String, String> params = new HashMap<>();
        params.put("userId", AppHelper.USER_ID);
        params.put("signature", signature);
        XUtils.Get(url, params, new RequestCallback<ResponseEntity>() {

            @Override
            public void onStarted() {
                super.onStarted();
                loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loading.setTitleText("正在下载");
                loading.show();
            }

            @Override
            public void onSuccess(ResponseEntity result) {
                if (result.getCode() == ResponseCode.SUCCESS.getValue()) {
                    // 发送数据处理消息
                    Message msg = handler.obtainMessage();
                    msg.what = PHONEDATA_MSG;
                    msg.obj = result.getObj();
                    handler.sendMessage(msg);

                } else {
                    String msg = result.getMsg();
                    if (msg == null || msg.trim().length() == 0) {
                        msg = "服务繁忙，下载手机数据失败！";
                    }
                    loading.dismiss();
                    errorDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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

    /**
     * 下载初始化APK
     */
    private void getInitApk() {
        String url = AppHelper.API_URL + "/api/apk/init";
        Map<String, String> params = new HashMap<>();
        params.put("userId", AppHelper.USER_ID);
        XUtils.Get(url, params, new RequestCallback<ResponseEntity>() {

            @Override
            public void onStarted() {
                super.onStarted();
                loading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loading.setTitleText("正在下载");
                loading.show();
            }

            @Override
            public void onSuccess(ResponseEntity result) {
                if (result.getCode() == ResponseCode.SUCCESS.getValue()) {
                    // 发送数据处理消息
                    Message msg = handler.obtainMessage();
                    msg.what = INIT_APK;
                    msg.obj = result.getObj();
                    handler.sendMessage(msg);
                } else {
                    String msg = result.getMsg();
                    if (msg == null || msg.trim().length() == 0) {
                        msg = "服务繁忙，下载初始化资源失败！";
                    }
                    loading.dismiss();
                    errorDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                    errorDialog.setTitleText(msg).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                errorDialog.setTitleText("服务繁忙，下载初始化资源失败！").show();
            }
        });
    }

    // 消息处理
    class EventHandler extends Handler {

        String url = null;
        String path = null;

        @Override
        public void handleMessage(Message msg) {
            final DbManager db = x.getDb(dbConfig);
            String url = null;
            switch (msg.what) {
                case SCRIPT_MSG: // 更新脚本
                    try {
                        List<Script> list = (List<Script>) msg.obj;
                        final int scriptSize = list.size();
                        if (list.isEmpty()) {
                            loading.dismiss();
                            successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            successDialog.setTitleText("没有更新的脚本了~").show();
                            break;
                        }
                        for (final Script pojo : list) {
                            final Script oldpojo = db.selector(Script.class).where("key", "=", pojo.getKey()).findFirst();
                            String path = null;
                            if (pojo.getType() == ScriptType.TouchSprite) {
                                path = AppHelper.TOUCHSPRITE_PATH + "/lua/";
                            } else if (pojo.getType() == ScriptType.Touchelper) {
                                path = AppHelper.TOUCHELPER_PATH + "/scripts/v2/";
                            }
                            File root = new File(path);
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            path += pojo.getName() + ".lua";
                            XUtils.DownLoadFile(pojo.getPath(), path, new RequestCallback<File>() {
                                @Override
                                public void onSuccess(File result) {
                                    try {
                                        if (oldpojo != null) {
                                            db.delete(oldpojo);
                                        }
                                        db.save(pojo);
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                    if (downloadIndex == scriptSize) {
                                        downloadIndex = 1;
                                        if (loading.isShowing()) {
                                            loading.dismiss();
                                        }
                                        successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                        successDialog.setTitleText("脚本更新成功！").show();
                                    }
                                    downloadIndex++;
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PHONEDATA_MSG: // 下载手机数据
                    url = (String) msg.obj;
                    path = AppHelper.TOUCHSPRITE_PATH + "/res/008.zip";
                    XUtils.DownLoadFile(url, path, new RequestCallback<File>() {
                        @Override
                        public void onSuccess(File result) {
                            // 解压文件
                            String target = AppHelper.TOUCHSPRITE_PATH + "/res/data/";;
                            File file = new File(target);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (FileUtils.unzip(path, target)) {
                                result.delete();
                                if (loading.isShowing()) {
                                    loading.dismiss();
                                }
                                successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                successDialog.setTitleText("手机数据下载成功！").show();
                            }
                        }
                    });
                    break;
                case INIT_APK: // 下载安装初始化APK
                    url = (String) msg.obj;
                    path = AppHelper.HGHELPER_PACKAGE + "/dl/initapk.zip";
                    XUtils.DownLoadFile(url, path, new RequestCallback<File>() {

                        @Override
                        public void onStarted() {
                            if (loading.isShowing()) {
                                loading.dismiss();
                            }
                            progressbar = new HGProgressBar(MainActivity.this);
                            progressbar.initDialog();
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isDownloading) {
                            if (current <= total) {
                                progressbar.pro1.setMax((int) total);
                                progressbar.setProgress((int) current);
                            }
                        }

                        @Override
                        public void onFinished() {
                            if (progressbar.isShowing()) {
                                progressbar.closeDialog();
                            }
                        }

                        @Override
                        public void onSuccess(File result) {
                            // 解压文件
                            final String target = AppHelper.HGHELPER_PACKAGE + "/dl/";
                            final File file = new File(target);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (FileUtils.unzip(path, target)) {
                                result.delete();
                                progressbar.closeDialog();
                                successDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                successDialog.setTitleText("下载成功！")
                                        .setContentText("存放路径：" + target).show();
                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
