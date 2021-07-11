package com.rdb.oss;

import android.os.Handler;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSHandler {

    Handler resultHandler;

    void setResultHandler(Handler resultHandler) {
        this.resultHandler = resultHandler;
    }
}
