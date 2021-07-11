package com.rdb.oss;

import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSVoidHandler<T1 extends OSSRequest, T2 extends OSSResult> extends OSSFailureHandler<T1, T2> {

    protected abstract void onSuccess();

    @Override
    public void onSuccess(T1 request, T2 result) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess();
            }
        });
    }
}
