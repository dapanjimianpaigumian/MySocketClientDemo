package com.yulu.zhaoxinpeng.mysocketclientdemo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 客户端:不要忘记加网络权限
 * 1. 创建一个客户端的Socket并且和服务端建立连接
 * 2. 打开Socket的输入输出流
 * 3. 对Socket进行读写的操作
 * 4. 关闭
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket mClient;
    private EditText mEtMsg;
    private TextView mTextView;
    private Handler mHandler=new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        mEtMsg = (EditText) findViewById(R.id.etMsg);
        mTextView = (TextView) findViewById(R.id.textView_show);

        btnConnect.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConnect:

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 1. 创建并且建立连接
                        try {
                            mClient = new Socket("192.168.1.123", 3600);

                            Log.i("TAG", "建立连接成功");

                        } catch (IOException e) {
                            Log.i("TAG", "建立连接失败");
                        }

                    }
                }).start();

                break;
            case R.id.btnSend:

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // 2. 打开输入和输出流
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
                            OutputStream outputStream = mClient.getOutputStream();

                            // 发送EditText输入的文字
                            String line = mEtMsg.getText().toString();

                            Log.i("TAG","client:"+line);

                            // 3.1 写的操作
                            outputStream.write((line+"\n").getBytes("utf-8"));
                            // 3.2 读取操作
                            final String echo = bufferedReader.readLine();
                            Log.i("TAG","server："+echo);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(echo);
                                }
                            });

                            // 4. 关闭Socket
                            if ("bye".equals(line)){
                                if (bufferedReader!=null&&outputStream!=null){
                                    bufferedReader.close();
                                    outputStream.close();
                                    mClient.close();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
    }
}

