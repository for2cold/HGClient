package com.kazyle.hgclient.callback;

import com.kazyle.hgclient.callback.data.ResponseEntity;

import org.xutils.common.Callback;

/**
 * Created by Kazyle on 2016/8/26.
 */
public class RequestCallback<T> implements Callback.ProgressCallback<T> {

    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onWaiting() {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {

    }
}
