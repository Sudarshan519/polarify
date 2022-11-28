package com.example.bnpj_polarify_re.nfc;

import android.nfc.Tag;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import jp.co.osstech.libjeid.CardType;
import jp.co.osstech.libjeid.JeidReader;
public class RCReaderTask extends AsyncTask<Void, String, JSONObject> {
    private static final String TAG = "RCReaderActivity";
    private WeakReference mRef;
    private Tag mNfcTag;
    private String rcNumber;

    public RCReaderTask(RCReaderActivity activity, Tag tag) {
        mRef = new WeakReference<RCReaderActivity>(activity);
        mNfcTag = tag;
    }

    @Override
    protected void onPreExecute() {
        RCReaderActivity activity = (RCReaderActivity)mRef.get();
        if (activity == null) {
            return;
        }
        rcNumber = activity.getRcNumber();

    }



    @Override
    protected JSONObject doInBackground(Void... voids) {
        long start = System.currentTimeMillis();
        JeidReader reader;
        try {
            reader = new JeidReader(mNfcTag);
        } catch (IOException e) {
            publishProgress("エラー: " + e);
            return null;
        }
        try {
            publishProgress("##");
            CardType type=reader.detectCardType();
            publishProgress("CardType"+type);
            if(type!=CardType.RC){
                publishProgress("Invalid Card Type");
                getActivity().setMessage("Invalid Card Type");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        RCReaderActivity activity = (RCReaderActivity)mRef.get();
        if (activity == null ||
                activity.isFinishing()) {
            return;
        }
        if (obj == null) {
            return;
        }
        HashMap<String, Object> hashMap = new Gson().fromJson(obj.toString(), HashMap.class);
        MethodChannel.Result result = activity.getResult();
        result.success(hashMap);
    }

    private RCReaderActivity getActivity(){
        RCReaderActivity activity = (RCReaderActivity)mRef.get();
        if (activity == null ||
                activity.isFinishing()) {
            return null;
        }
        return activity;
    }
}
