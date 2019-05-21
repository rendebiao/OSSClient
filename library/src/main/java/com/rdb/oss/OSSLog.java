package com.rdb.oss;

import android.util.Log;

/**
 * Created by DB on 2017/8/3.
 */

public class OSSLog {
    public static boolean enable = true;

    public static void log(String message) {
        if (enable) {
            Log.e("OSSClient", message);
        }
    }
}
