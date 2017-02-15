package com.kazyle.hgclient.util;

import android.util.Log;

import com.kazyle.hgclient.exception.ClearAppDataException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kazyle on 2016/9/5.
 */
public class AppHelper {

    private static final String TAG = "[AppHelper]";

    private static Map<String, String> apkMap = null;

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
}
