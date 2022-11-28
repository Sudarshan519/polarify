package com.example.iss_sdk.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bnpj_polarify_re.R;
import com.example.iss_sdk.IssSdk;

import java.io.Serializable;

import io.flutter.plugin.common.MethodChannel;

public class ConnectActivity extends AppCompatActivity implements Serializable, MethodChannel.Result{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Button connBtn = findViewById(R.id.btn_connect);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动
                //ws连接地址
                String ws_server_url="ws://54.64.232.56/ws/";
                //自动刷新时间,单位毫秒,3分钟
                int reload= 180000;
                //secret
                String secret="B2B";
                //this is bnpj_agreement_no
                String consumerId="8823936075282";
                IssSdk iss_sdk = new IssSdk(ws_server_url,reload,secret,consumerId);
                iss_sdk.Show();

//                //关闭
//                iss_sdk.Close();
            }
        });
    }

    @Override
    public void success(@Nullable Object result) {

    }

    @Override
    public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

    }

    @Override
    public void notImplemented() {

    }
}