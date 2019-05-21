package com.rdb.oss;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.io.File;

/**
 * Created by DB on 2017/2/7.
 */

public class OSSClient extends Handler {

    private OSS oss;

    public OSSClient(Context context, String endpoint, String accessKeyId, String accessKeySecret) {
        super(Looper.getMainLooper());
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(8 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(8 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(0); // 失败后最大重试次数，默认2次
        oss = new com.alibaba.sdk.android.oss.OSSClient(context.getApplicationContext(), endpoint, credentialProvider, conf);
    }

    public OSSAsyncTask asyncUploadFile(String bucketName, String objectKey, String uploadFilePath, OSSFileUploadHandler uploadHandler) {
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
        put.setProgressCallback(uploadHandler);
        return oss.asyncPutObject(put, uploadHandler);
    }

    public OSSAsyncTask asyncDownloadFile(String bucketName, String objectKey, File directory, String fileName, final OSSFileDownloadHandler downloadHandler) {
        if (downloadHandler.init(directory, fileName)) {
            return oss.asyncGetObject(new GetObjectRequest(bucketName, objectKey), downloadHandler);
        } else {
            return null;
        }
    }

    public OSSAsyncTask asyncGetFileContent(String bucketName, String objectKey, String charsetName, final OSSContentGetHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setHandler(this);
            if (contentHandler.handCache(bucketName, objectKey)) {
                return null;
            }
            contentHandler.init(charsetName);
        }
        return oss.asyncGetObject(new GetObjectRequest(bucketName, objectKey), contentHandler);
    }

    public OSSAsyncTask asyncPutFileContent(String bucketName, String objectKey, String content, String charsetName, final OSSContentPutHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setHandler(this);
        }
        try {
            return oss.asyncPutObject(new PutObjectRequest(bucketName, objectKey, content.getBytes(charsetName)), contentHandler);
        } catch (final Exception e) {
            contentHandler.handFailure(e.getMessage());
        }
        return null;
    }

    public OSSAsyncTask asyncDeleteFile(String bucketName, String objectKey, OSSFileDeleteHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setHandler(this);
        }
        return oss.asyncDeleteObject(new DeleteObjectRequest(bucketName, objectKey), contentHandler);
    }

    public boolean doesObjectExist(String bucketName, String objectKey) throws ClientException, ServiceException {
        return oss.doesObjectExist(bucketName, objectKey);
    }
}
