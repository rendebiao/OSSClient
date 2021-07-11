package com.rdb.oss;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
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

    private final OSS oss;
    private Handler threadHandler;

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
            contentHandler.setResultHandler(this);
            if (contentHandler.handCache(bucketName, objectKey)) {
                return null;
            }
            contentHandler.init(charsetName);
        }
        return oss.asyncGetObject(new GetObjectRequest(bucketName, objectKey), contentHandler);
    }

    public OSSAsyncTask asyncPutFileContent(String bucketName, String objectKey, String content, String charsetName, final OSSContentPutVoidHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setResultHandler(this);
        }
        try {
            byte[] uploadData = content.getBytes(charsetName);
            return oss.asyncPutObject(new PutObjectRequest(bucketName, objectKey, uploadData), contentHandler);
        } catch (final Exception e) {
            contentHandler.handFailure(0, e.getMessage());
        }
        return null;
    }

    public OSSAsyncTask asyncDeleteFile(String bucketName, String objectKey, OSSFileDeleteVoidHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setResultHandler(this);
        }
        return oss.asyncDeleteObject(new DeleteObjectRequest(bucketName, objectKey), contentHandler);
    }

    public void asyncDoesFileExist(final String bucketName, final String objectKey, final OSSContentExistHandler contentHandler) {
        if (contentHandler != null) {
            contentHandler.setResultHandler(this);
        }
        getThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean exist = oss.doesObjectExist(bucketName, objectKey);
                    contentHandler.handSuccess(exist);
                } catch (Exception e) {
                    e.printStackTrace();
                    contentHandler.handFailure(e.getMessage());
                }
            }
        });
    }

    private synchronized Handler getThreadHandler() {
        if (threadHandler == null) {
            HandlerThread thread = new HandlerThread("OSS HandlerThread");
            thread.start();
            threadHandler = new Handler(thread.getLooper());
        }
        return threadHandler;
    }
}
