package com.rdb.oss.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rdb.oss.OSSClient;
import com.rdb.oss.OSSContentGetHandler;
import com.rdb.oss.OSSContentPutHandler;

public class MainActivity extends AppCompatActivity {

    private EditText bucketName;
    private EditText objectKey;
    private EditText contentView;
    private Button ossFile;
    private Button saveFile;
    private OSSClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        client = new OSSClient(this, "oss-cn-hangzhou.aliyuncs.com", "", "");
        bucketName = findViewById(R.id.bucketName);
        objectKey = findViewById(R.id.objectKey);
        contentView = findViewById(R.id.content);
        ossFile = findViewById(R.id.ossFile);
        saveFile = findViewById(R.id.saveFile);
        ossFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.asyncGetFileContent(bucketName.getText().toString(), objectKey.getText().toString(), "utf-8", new OSSContentGetHandler(null, 0) {
                    @Override
                    public void onSuccess(long modifyTime, boolean valid, String content) {
                        Log.e("oss", "getObjectContent onSuccess " + content);
                        contentView.setText(content);
                    }

                    @Override
                    public void onFailure(int type, String message) {
                        Log.e("oss", "getObjectContent onFailure " + message);
                        contentView.setText(null);
                    }
                });
            }
        });
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.asyncPutFileContent(bucketName.getText().toString(), objectKey.getText().toString(), contentView.getText().toString().trim(), "utf-8", new OSSContentPutHandler() {
                    @Override
                    public void onSuccess() {
                        Log.e("oss", "putObjectContent onSuccess ");
                        Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int type, String message) {
                        Log.e("oss", "putObjectContent onFailure " + message);
                        Toast.makeText(MainActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
