package com.rdb.oss;

import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSFileUploadHandler extends OSSFailureHandler<PutObjectRequest, PutObjectResult> implements OSSProgressCallback<PutObjectRequest> {

    public abstract void onProgress(long currentSize, long totalSize);

    public abstract void onSuccess();

    @Override
    public final void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onProgress(currentSize, totalSize);
            }
        });
    }

    @Override
    public final void onSuccess(PutObjectRequest request, PutObjectResult result) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess();
            }
        });
    }
}
