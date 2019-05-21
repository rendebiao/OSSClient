package com.rdb.oss;

import android.os.Handler;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSFailureHandler<T1 extends OSSRequest, T2 extends OSSResult> implements OSSCompletedCallback<T1, T2> {

    Handler handler;

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    public abstract void onFailure(String message);

    @Override
    public void onFailure(T1 request, ClientException clientExcepion, ServiceException serviceException) {
        final StringBuffer sb = new StringBuffer();
        if (clientExcepion != null) {
            clientExcepion.printStackTrace();
            sb.append(clientExcepion.getMessage());
        }
        if (serviceException != null) {
            serviceException.printStackTrace();
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(serviceException.toString());
        }
        handFailure(sb.toString());
    }

    void handFailure(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(message);
            }
        });
    }
}
