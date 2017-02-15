package com.kazyle.hgclient.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kazyle.hgclient.R;

/**
 * Created by Kazyle on 2016/8/28.
 */
public class HGProgressBar {

    private Dialog dialog;
    private Context context;
    // 声明ProgressBar对象
    public ProgressBar pro1;
    public TextView titleView;


    /**
     * 构造
     */
    public HGProgressBar(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        dialog = new Dialog(context, R.style.dialog);
        dialog.setOnCancelListener(onCancelListener);
    }

    /**
     * 初始化进度对话框
     */
    public void initDialog() {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progressbar, null);
        dialog.setContentView(view);
        titleView = (TextView) view.findViewById(R.id.progress_title);
        pro1 = (ProgressBar) dialog.findViewById(R.id.progressBar);
        // 设置进度条是否自动旋转,即设置其不确定模式,false表示不自动旋转
        pro1.setIndeterminate(false);
        // 设置ProgressBar的最大值
        pro1.setMax(100);
        // 设置ProgressBar的当前值
        pro1.setProgress(0);
        dialog.show();
    }

    public void setProgress(int progressValue) {
        pro1.setProgress(progressValue);
    }

    public void closeDialog() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        if (dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            // TODO Auto-generated method stub
            dialog.dismiss();
        }
    };
}
