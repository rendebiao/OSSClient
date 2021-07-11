package com.rdb.oss;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSContentExistHandler extends OSSHandler {

    public OSSContentExistHandler() {
    }

    public abstract void onSuccess(boolean exist);

    public abstract void onFailure(String message);

    void handSuccess(final boolean exist) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess(exist);
            }
        });
    }

    void handFailure(final String message) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(message);
            }
        });
    }
}
