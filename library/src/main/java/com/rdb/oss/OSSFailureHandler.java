package com.rdb.oss;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSFailureHandler<T1 extends OSSRequest, T2 extends OSSResult> extends OSSHandler implements OSSCompletedCallback<T1, T2> {

    public abstract void onFailure(int type, String message);

    @Override
    public void onFailure(T1 request, ClientException clientExcepion, ServiceException serviceException) {
        if (clientExcepion != null) {
            clientExcepion.printStackTrace();
            handFailure(1, clientExcepion.getMessage());
        } else if (serviceException != null) {
            serviceException.printStackTrace();
            handFailure(2, serviceException.toString());
        }
    }

    /**
     * @param type    0 other 1 client 2 server
     * @param message
     */
    void handFailure(final int type, final String message) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(type, message);
            }
        });
    }
}
