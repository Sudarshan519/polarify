package com.example.iss_sdk;

import android.app.Activity;
import android.content.Intent;

import com.example.iss_sdk.activity.MainActivity;
import com.example.iss_sdk.tool.ActivityManager;

/**
 * Created by xuesong on 2022/10/11
 * Description:
 */
public class IssSdk {

    private String wss_server_url,secret,consumerId;
    private int reload;

    public IssSdk(String wss_server_url, int reload, String secret, String consumerId) {
        this.wss_server_url = wss_server_url;
        this.reload = reload;
        this.secret = secret;
        this.consumerId = consumerId;
    }

    public void Show(){
        if(ActivityManager.getInstance().getCurrentActivity() != null) {
            Activity currentActivity = ActivityManager.getInstance().getCurrentActivity();
            Intent connIntent = new Intent(currentActivity, MainActivity.class);
            connIntent.putExtra("wss_server_url", wss_server_url);
            connIntent.putExtra("secret", secret);
            connIntent.putExtra("consumerId", consumerId);
            connIntent.putExtra("reload", reload);
            currentActivity.startActivity(connIntent);
//            currentActivity.finish();
        }
    }

    public void Close(){
        if(ActivityManager.getInstance().getCurrentActivity() != null) {
            ActivityManager.getInstance().getCurrentActivity().finish();
        }
    }

}
