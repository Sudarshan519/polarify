package com.example.bnpj_polarify_re.nfc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bnpj_polarify_re.BaseActivity;
import com.example.bnpj_polarify_re.R;

import io.flutter.plugin.common.MethodChannel;

public class RCReaderActivity extends BaseActivity implements MethodChannel.Result{
private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcreader);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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