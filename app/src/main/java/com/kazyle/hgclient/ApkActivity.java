package com.kazyle.hgclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.kazyle.hgclient.adapter.ApkAdater;
import com.kazyle.hgclient.callback.RequestCallback;
import com.kazyle.hgclient.entity.Apk;
import com.kazyle.hgclient.entity.Script;
import com.kazyle.hgclient.entity.ScriptType;
import com.kazyle.hgclient.helper.entity.DLRowItem;
import com.kazyle.hgclient.util.FileUtils;
import com.kazyle.hgclient.util.XUtils;
import com.kazyle.hgclient.widget.HGProgressBar;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Kazyle on 2016/10/18.
 */
public class ApkActivity extends AppCompatActivity implements View.OnClickListener {

    private static String HGHELPER_PACKAGE = "/sdcard/TouchHGHelper";
    private Button selectAllBtn;
    private Button downloadBtn;
    ProgressDialog progressDialog;

    DownloadTask task = null;
    ApkAdater adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_listview);
        Bundle bundle = getIntent().getExtras();
        List<Apk> apks = (List<Apk>) bundle.getSerializable("apks");
        adapter = new ApkAdater(this, apks);
        ListView listView = (ListView) findViewById(R.id.apkList);
        listView.setAdapter(adapter);

        task = new DownloadTask(this);
        initProgress();

        selectAllBtn = (Button) findViewById(R.id.selectAllBtn);
        downloadBtn = (Button) findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Map<Integer, Boolean> state = adapter.state;
        List<DLRowItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : state.entrySet()) {
            if (entry.getValue() != null) {
                final Apk apk = (Apk) adapter.getItem(entry.getKey());
                String path = HGHELPER_PACKAGE + "/dl/" + apk.getName();
                if (!apk.getName().endsWith(".apk")) {
                    path += ".apk";
                }
                items.add(new DLRowItem(apk.getName(), apk.getPath(), path));
            }
        }
        if (!items.isEmpty()) {
            DLRowItem[] taskItems = new DLRowItem[items.size()];
            for (int i = 0; i < items.size(); ++i) {
                taskItems[i] = items.get(i);
            }
            task.execute(taskItems);
        } else {
            SweetAlertDialog errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            errorDialog.setTitleText("请勾选要下载的资源!").show();
        }
    }

    private class DownloadTask extends AsyncTask<DLRowItem, Integer, List<DLRowItem>> {

        private Activity context;
        private List<DLRowItem> items;
        private int taskSize;

        public DownloadTask(Activity context) {
            this.context = context;
        }

        @Override
        protected List<DLRowItem> doInBackground(DLRowItem... params) {
            taskSize = params.length;
            items = new ArrayList<>();
            for (DLRowItem param : params) {
                items.add(doDownload(param));
            }
            return items;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            if (items != null) {
                progressDialog.setMessage("进度：" + (items.size() + 1) + "/" + taskSize);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<DLRowItem> dlRowItems) {
            progressDialog.dismiss();
            SweetAlertDialog errorDialog = new SweetAlertDialog(ApkActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            errorDialog.setTitleText("下载完成！").show();
        }

        private DLRowItem doDownload(DLRowItem param) {
            int count = 0;
            URL url;
            InputStream in = null;
            BufferedOutputStream out = null;

            try {
                url = new URL(param.getUrl());
                URLConnection conn = url.openConnection();
                int lengthOfFile = conn.getContentLength();

                in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                OutputStream outputStream = new FileOutputStream(param.getDest());
                byte[] data = new byte[512];
                long total = 0L;
                while ((count = in.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.close();
                param.setSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return param;
        }
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载资源");// 设置Title
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
    }

}
