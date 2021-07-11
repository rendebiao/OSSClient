package com.rdb.oss;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by DB on 2017/8/1.
 */

public abstract class OSSFileDownloadHandler extends OSSFailureHandler<GetObjectRequest, GetObjectResult> {

    private File file;

    boolean init(File directory, String fileName) {
        if (directory != null && !TextUtils.isEmpty(fileName)) {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file = new File(directory.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.exists();
        }
        handFailure(0, "file init fail");
        return false;
    }

    public abstract void onSuccess(File file);

    @Override
    public final void onSuccess(GetObjectRequest request, GetObjectResult result) {
        FileOutputStream outStream = null;
        InputStream inputStream = result.getObjectContent();
        try {
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            resultHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(file);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            handFailure(0, e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
