package com.example.bnpj_polarify_re.ekyc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bnpj_polarify_re.R;

import java.io.Serializable;
import  jp.co
        .polarify.onboarding.sdk.PolarifyKycSdkFactory;
import io.flutter.plugin.common.MethodChannel; 
public class EkycMainActivity extends AppCompatActivity implements Serializable , MethodChannel.Result {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekyc_main);

         
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