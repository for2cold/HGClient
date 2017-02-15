package com.kazyle.hgclient.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Kazyle on 2016/8/28.
 */
public class FileUtils {

    private static final String TAG = "[FileUtils]";

    /**
     * 解压压缩包
     * @param source
     * @param target
     * @return
     */
    public static boolean unzip(String source, String target) {

        ZipFile zipFile = null;
        ZipInputStream zipIn = null;

        try {
            File file = new File(source);
            File out = null;
            zipFile = new ZipFile(file);
            zipIn = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = null;
            InputStream input = null;
            OutputStream output = null;

            while ((entry = zipIn.getNextEntry()) != null) {

                String name = entry.getName();
                if (name == null || name.startsWith("__MACOSX")) {
                    continue;
                }
                name = name.replaceAll(".*/", "");
                int pos = name.lastIndexOf(".");
                if (pos == -1) {
                    continue;
                }
                out = new File(target + name);
                if (!out.getParentFile().exists()) {
                    out.getParentFile().mkdirs();
                }
                if (!out.exists()) {
                    out.createNewFile();
                }
                try {
                    input = zipFile.getInputStream(entry);
                    output = new FileOutputStream(out);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = input.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    input.close();
                    output.close();
                } catch (Exception e) {
                    if (null != input) {
                        input.close();
                    }
                    if (null != output) {
                        output.close();
                    }
                }
            }
            zipIn.close();
            zipFile.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "解压文件失败");
        } finally {
            try {
                if (zipIn != null) {
                    zipIn.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }
}
