package com.rdb.oss;

/**
 * Created by DB on 2017/8/2.
 */

public class OSSContentResponse {

    private String bucket;
    private String key;
    private String content;
    private long modifyTime;
    private long responseTime;

    public OSSContentResponse(String bucket, String key, String content, long modifyTime, long responseTime) {
        this.bucket = bucket;
        this.key = key;
        this.content = content;
        this.modifyTime = modifyTime;
        this.responseTime = responseTime;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
