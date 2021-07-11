package com.rdb.oss;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSContentGetHandler extends OSSFailureHandler<GetObjectRequest, GetObjectResult> {

    String charsetName;
    long cacheValidTime;
    OSSContentResponse response;
    OSSContentCacheable cacheable;

    public OSSContentGetHandler(OSSContentCacheable cacheable, long cacheValidTime) {
        this.cacheable = cacheable;
        this.cacheValidTime = cacheValidTime;
    }

    boolean handCache(String bucketName, String objectKey) {
        if (cacheable != null) {
            response = cacheable.getCacheResponse(bucketName, objectKey);
            OSSLog.log("OSSContentGetHandler getCacheResponse " + objectKey + "-" + (response != null));
            if (response != null) {
                boolean isValid = response.getResponseTime() + cacheValidTime > cacheable.getCurTime();
                OSSLog.log("OSSContentGetHandler handCache isValid " + isValid + "  " + response.getResponseTime() + "-" + cacheValidTime + "-" + cacheable.getCurTime());
                if (isValid) {
                    handCache(response);
                    OSSLog.log("OSSContentGetHandler handCache before request");
                    return true;
                }
            }
        }
        return false;
    }

    void init(String charsetName) {
        this.charsetName = charsetName;
    }

    public abstract void onSuccess(long modifyTime, boolean isCache, String content);

    @Override
    public final void onSuccess(GetObjectRequest request, GetObjectResult result) {
        long modifyTime = result.getMetadata().getLastModified().getTime();
        if (response == null || response.getModifyTime() < modifyTime) {
            InputStream inputStream = result.getObjectContent();
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            try {
                StringBuffer content = new StringBuffer();
                inputStreamReader = new InputStreamReader(inputStream, charsetName);
                bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.length() > 0) {
                        if (content.length() > 0) {
                            content.append("\n");
                        }
                        content.append(line);
                    }
                }
                if (cacheable != null) {
                    OSSLog.log("OSSContentGetHandler cache " + request.getObjectKey() + " " + modifyTime + " " + result.getMetadata().getLastModified().toString());
                    cacheable.cache(new OSSContentResponse(request.getBucketName(), request.getObjectKey(), content.toString(), modifyTime, cacheable.getCurTime()));
                }
                handSuccess(modifyTime, false, content.toString());
                OSSLog.log("OSSContentGetHandler handResult");
            } catch (final IOException e) {
                e.printStackTrace();
                if (request == null) {
                    handFailure(0, e.getMessage());
                } else {
                    handCache(response);
                    OSSLog.log("OSSContentGetHandler handCache after error");
                }
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            response.setResponseTime(cacheable.getCurTime());
            cacheable.cache(response);
            handCache(response);
            OSSLog.log("OSSContentGetHandler handCache after request");
        }
    }

    @Override
    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
        if (response == null) {
            super.onFailure(request, clientExcepion, serviceException);
        } else {
            handCache(response);
            OSSLog.log("OSSContentGetHandler handCache after fail");
        }
    }

    private void handCache(OSSContentResponse response) {
        handSuccess(response.getModifyTime(), true, response.getContent());
    }

    private void handSuccess(final long lastModify, final boolean isCache, final String content) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess(lastModify, isCache, content);
            }
        });
    }
}
