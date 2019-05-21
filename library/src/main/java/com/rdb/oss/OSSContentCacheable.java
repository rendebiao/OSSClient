package com.rdb.oss;

/**
 * Created by DB on 2017/8/2.
 */

public interface OSSContentCacheable {

    void cache(OSSContentResponse response);

    OSSContentResponse getCacheResponse(String bucketName, String objectKey);

    long getCurTime();
}
