package com.kazyle.hgclient.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.kazyle.hgclient.exception.ClearAppDataException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by Kazyle on 2016/9/5.
 */
public class AppHelper {

    private static final String TAG = "[AppHelper]";

    private static Map<String, String> apkMap = null;

    public static String USER_ID = "3";
    public static final String API_URL = "http://112.74.129.247:9091";
//    public static final String API_URL = "http://192.168.0.100:8080/hgserver";
    public static final String FIR_API_TOKEN = "950223d3b9437f2ea5059923d2a8dfce";
    public static final String SIGNATURE = "B177AAB3BF38294C3E8E86315874725A";
    public static String TOUCHSPRITE_PATH = Environment.getExternalStorageDirectory().getPath() + "/TouchSprite";
    public static String TOUCHELPER_PATH = Environment.getExternalStorageDirectory().getPath() + "/Touchelper";
    public static String HGHELPER_PACKAGE = "/sdcard/TouchHGHelper";
    public static String ARTICLE_CONFIG_PATH = TOUCHSPRITE_PATH + "/config/article_config.json";

    public static String getApk(String packageName) {
        if (apkMap == null) {
            apkMap = new HashMap<>();
            apkMap.put("com.miui.barcodescanner", "二维码扫描");
            apkMap.put("com.android.browser", "浏览器");
            apkMap.put("com.android.calendar", "日历");
            apkMap.put("com.android.contacts", "联系人");
            apkMap.put("com.android.contacts", "拨号");
            apkMap.put("com.android.camera", "相机");
            apkMap.put("com.miui.gallery", "图库");
            apkMap.put("com.miui.fmradio", "电台");
            apkMap.put("com.android.mms", "短信");
            apkMap.put("com.android.settings", "设置");
            apkMap.put("com.miui.weather2", "天气");
            apkMap.put("com.android.calculator2", "计算器");
            apkMap.put("com.iflytek.inputmethod", "讯飞输入法");
            apkMap.put("com.android.deskclock", "时钟");
            apkMap.put("com.android.email", "电子邮件");
            apkMap.put("com.android.fileexplorer", "文件管理");
            apkMap.put("com.tencent.android.qqdownloader", "应用宝");
            apkMap.put("com.miui.notes", "便签");
            apkMap.put("com.android.thememanager", "主题风格");
            apkMap.put("com.miui.player", "音乐");
            apkMap.put("com.miui.securitycenter", "安全中心");
            apkMap.put("com.baidusearch.mobile", "搜索");
            apkMap.put("com.daohangforxiuzhuo", "浏览器");
            apkMap.put("com.tianqiwhite", "天气");
            apkMap.put("com.wcd.android.market.game", "游戏盒子");
            apkMap.put("com.android.providers.downloads.ui", "下载管理");
            apkMap.put("com.miui.compass", "指南针");
            apkMap.put("com.xiaomi.market", "应用商店");
            apkMap.put("com.miui.video", "视频");
            apkMap.put("com.android.soundrecorder", "录音机");
            apkMap.put("com.android.updater", "系统更新");
            apkMap.put("com.miui.voiceassist", "语音助手");
            apkMap.put("com.xiaomi.account", "我的小米");
            apkMap.put("com.kingroot.kinguser", "KingRoot");
            apkMap.put("com.tencent.mobileqq", "QQ");
            apkMap.put("com.android.packageinstaller", "Android安装器");
            apkMap.put("FF.root.android.qwerty.asdfghjkl", "小强载体");
            apkMap.put("cem.sift.apd108e", "天啦撸");
            apkMap.put("com.estrongs.android.pop", "ES文件浏览器");
            apkMap.put("com.touchsprite.android", "触动精灵");
            apkMap.put("net.aisence.Touchelper", "触摸精灵");
            apkMap.put("com.keramidas.TitaniumBackup", "钛备份");
            apkMap.put("com.kazyle.hgclient", "HG果冻");
            apkMap.put("com.eg.android.AlipayGphone", "支付宝");
        }
        return apkMap.get(packageName);
    }

    public static String join(Iterator iterator, char separator) {
        if(iterator == null) {
            return null;
        } else if(!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if(!iterator.hasNext()) {
                return ObjectUtils.toString(first);
            } else {
                StringBuilder buf = new StringBuilder(256);
                if(first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {
                    buf.append(separator);
                    Object obj = iterator.next();
                    if(obj != null) {
                        buf.append(obj);
                    }
                }
                return buf.toString();
            }
        }
    }

    public static Process clearAppData(String packageName) throws ClearAppDataException {
        Process p = execRuntimeProcess("pm clear " + packageName);
        if (p == null) {
            throw new ClearAppDataException("清理应用数据失败");
        }
        return p;
    }

    public static Process uninstallApp(String packageName) throws ClearAppDataException {
        Process p = execRuntimeProcess("pm uninstall " + packageName);
        if (p == null) {
            throw new ClearAppDataException("卸载应用失败");
        }
        return p;
    }

    public static Process execRuntimeProcess(String cmd) {
        Runtime localRuntime = Runtime.getRuntime();
        try {
            Process pro = localRuntime.exec("su");
            DataOutputStream out = new DataOutputStream(pro.getOutputStream());
            out.writeBytes(cmd + " \n");
            return pro;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean getRoot() {
        Process pro = execRuntimeProcess("chmod 777 /dev/input/event*");
        return pro != null;
    }

    public static String signature(String paramString) {
        return SIGNATURE;
        /*int i = 0;
        char[] array = new char[16];
        array[0] = 48;
        array[1] = 49;
        array[2] = 50;
        array[3] = 51;
        array[4] = 52;
        array[5] = 53;
        array[6] = 54;
        array[7] = 55;
        array[8] = 56;
        array[9] = 57;
        array[10] = 65;
        array[11] = 66;
        array[12] = 67;
        array[13] = 68;
        array[14] = 69;
        array[15] = 70;
        int j;
        int m;
        int n;
        String ret = null;
        try {
            Object localObject = paramString.getBytes("utf-8");
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update((byte[])localObject);
            byte[] params = digest.digest();
            int k = params.length;
            char[] object = new char[k * 2];
            j = 0;
            for (i = 0; i < k; i++) {
                m = params[i];
                n = j + 1;
                object[j] = ((char)array[(m >>> 4 & 0xF)]);
                j = n + 1;
                object[n] = ((char)array[(m & 0xF)]);
            }
            ret = new String(object);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ret;*/
    }

    public static String signature(Context context) {
        Method method;
        try {
            Signature[] arrayOfSignature = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            Class localClass = Class.forName("android.content.pm.Signature");
            int i3 = arrayOfSignature.length;
            StringBuilder localStringBuilder = new StringBuilder();
            for (int i1 = 0; i1 < i3; ++i1) {
                Signature localSignature;
                localSignature = arrayOfSignature[i1];
                method = localClass.getMethod("toCharsString", new Class[0]);
                String ret = method.invoke(localSignature, new Object[0]) + "hugo";
                localStringBuilder.append(ret);
            }
            return localStringBuilder.toString();
        } catch (Exception e) {
        }
        return null;
    }

    private static final String[] netTypes = {"4G", "3G"};

    public static String getUserAgent() {
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String release = Build.VERSION.RELEASE;
//        System.out.println("brand-->" + brand + "\t model-->" + model + "\t release-->" + release + "\t product-->" + product);
        Random random = new Random();
        int index = random.nextInt(netTypes.length);
        String netType = netTypes[index];
        StringBuilder userAgent = new StringBuilder("Mozilla/5.0 (Linux; Android ");
        userAgent.append(release).append("; ").append(product)
                .append(" Build/").append(model).append("; wv)")
                .append(" AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0")
                .append(" Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043221 Safari/537.36 MicroMessenger/6.5.4.1000")
                .append(" NetType/").append(netType).append(" Language/zh_CN");
        return userAgent.toString();
    }
}
