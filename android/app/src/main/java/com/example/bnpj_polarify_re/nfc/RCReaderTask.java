package com.example.bnpj_polarify_re.nfc;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.osstech.libjeid.CardType;
import jp.co.osstech.libjeid.InvalidACKeyException;
import jp.co.osstech.libjeid.JeidReader;
import jp.co.osstech.libjeid.RCKey;
import jp.co.osstech.libjeid.ResidenceCardAP;
import jp.co.osstech.libjeid.ValidationResult;
import jp.co.osstech.libjeid.rc.RCAddress;
import jp.co.osstech.libjeid.rc.RCCardEntries;
import jp.co.osstech.libjeid.rc.RCCardType;
import jp.co.osstech.libjeid.rc.RCCommonData;
import jp.co.osstech.libjeid.rc.RCComprehensivePermission;
import jp.co.osstech.libjeid.rc.RCFiles;
import jp.co.osstech.libjeid.rc.RCIndividualPermission;
import jp.co.osstech.libjeid.rc.RCPhoto;
import jp.co.osstech.libjeid.rc.RCSignature;
import jp.co.osstech.libjeid.rc.RCUpdateStatus;
import jp.co.osstech.libjeid.util.BitmapARGB;
import jp.co.polarify.onboarding.app.apilogger.ApiInterface;
import jp.co.polarify.onboarding.app.apilogger.LogModel;
import jp.co.polarify.onboarding.app.apilogger.RetrofitUtil;
import retrofit2.Retrofit;

public class RCReaderTask extends AsyncTask<Void, String, JSONObject> {
String TAG="RCReaderActivity";
private WeakReference mRef;
private  Tag mNfcTag;
private String rcNumber;
RCReaderTask(RCReaderActivity activity, Tag nfcTag){
    mRef=new WeakReference<RCReaderActivity>(activity);
    mNfcTag=nfcTag;
};

    @Override
    protected void onPreExecute() {
        RCReaderActivity activity=(RCReaderActivity)mRef.get();
        if(activity==null){
            return
                    ;
        }
        rcNumber=activity.getRcNumber();
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        if(rcNumber.isEmpty()){
            publishProgress("在留カード番号または特別永住者証明書番号を設定してください");
            getActivity().setMessage("在留カード番号または特別永住者証明書番号を設定してください");
            return null;
        }
        JeidReader reader;
        try {
            reader = new JeidReader(mNfcTag);
            publishProgress("## カード種別");
            CardType type = reader.detectCardType();
            publishProgress("CardType: " + type);
            if (type != CardType.RC) {
                publishProgress("在留カード/特別永住者証明書ではありません");
                getActivity().setMessage("在留カード/特別永住者証明書ではありません");
                return null;
            }
            RCKey rcKey=new RCKey(rcNumber);
            publishProgress("## セキュアメッセージング用の鍵交換&認証");
            getActivity().setMessage("## セキュアメッセージング用の鍵交換&認証");
            ResidenceCardAP ap = reader.selectResidenceCardAP();
            RCCommonData commonData = ap.readCommonData();
            publishProgress("commonData: " + commonData);
            RCCardType cardType = ap.readCardType();
            publishProgress("cardType: " + cardType);
            RCKey rckey = new RCKey(rcNumber);
            publishProgress("## セキュアメッセージング用の鍵交換&認証");
            getActivity().setMessage("## セキュアメッセージング用の鍵交換&認証");
            try {
                ap.startAC(rcKey);
            }catch (InvalidACKeyException e){
                publishProgress("失敗\n"
                        + "在留カード番号または特別永住者証明書番号が間違っています");
                getActivity().setMessage("失敗\n"
                        + "在留カード番号または特別永住者証明書番号が間違っています");
                return null;
            }
            publishProgress("完了");

            publishProgress("## カードから情報を取得します");
            getActivity().setMessage("## カードから情報を取得します");
            RCFiles files = ap.readFiles();
            publishProgress("完了");
            JSONObject obj = new JSONObject();
            JSONObject data = new JSONObject();
            obj.put("rc-card-type", cardType.getType());
            data.put("document_type", cardType.getType());
            RCCardEntries cardEntries = files.getCardEntries();
            byte[] png = cardEntries.toPng();
            String src = "data:image/png;base64," + Base64.encodeToString(png, Base64.DEFAULT);
            obj.put("rc-front-image", src);
            data.put("front_image", src);
            publishProgress("## 写真のデコード");
            RCPhoto photo = files.getPhoto();
            BitmapARGB argb = photo.getPhotoBitmapARGB();

            if (argb != null) {
                Bitmap bitmap = Bitmap.createBitmap(argb.getData(),
                        argb.getWidth(),
                        argb.getHeight(),
                        Bitmap.Config.ARGB_8888);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] jpeg = os.toByteArray();
                src = "data:image/jpeg;base64," + Base64.encodeToString(jpeg, Base64.DEFAULT);
                obj.put("rc-photo", src);
                data.put("profile_image", src);
            }

            publishProgress("完了");
            publishProgress("## 住居地（裏面追記）");
            RCAddress address = files.getAddress();
            publishProgress(address.toString());
            if ("1".equals(cardType.getType())) {
                publishProgress("## 裏面資格外活動包括許可欄");
                RCComprehensivePermission comprehensivePermission = files.getComprehensivePermission();
                publishProgress(comprehensivePermission.toString());

                publishProgress("## 裏面資格外活動個別許可欄");
                RCIndividualPermission individualPermission = files.getIndividualPermission();
                publishProgress(individualPermission.toString());

                publishProgress("## 裏面在留期間等更新申請欄");

                RCUpdateStatus updateStatus = files.getUpdateStatus();
                publishProgress(updateStatus.toString());

                getActivity().setMessage("## 裏面在留期間等更新申請欄");
            }

            publishProgress("## 電子署名");
            RCSignature signature = files.getSignature();
            publishProgress(signature.toString());

            // 真正性検証
            publishProgress("## 真正性検証");
            try {
                ValidationResult result = files.validate();
                obj.put("rc-valid", result.isValid());
                data.put("is_valid", result.isValid());
                publishProgress("真正性検証結果: " + result);
                return data;
            } catch(UnsupportedOperationException e) {
                // free版の場合、真正性検証処理で
                // UnsupportedOperationException が返ります。
            }
//            return obj;

        }  catch (TagLostException e){
            publishProgress("エラー: " + e);
            getActivity().setMessage("端末とICカードの接続が切断された。\nデバイスをカードにかざしてください。在留カードの場合、\n" +
                    "スキャンには10～12秒程度かかります。正常に進行するまでカードをかざしてください。");
            return null;
        } catch (Exception e) {
            publishProgress("エラー: " + e);
            getActivity().setMessage("エラー: " + e);
            return null;
        }
        return  null;

    }


    @Override
    protected void onProgressUpdate(String... values) {
        RCReaderActivity activity = (RCReaderActivity)mRef.get();
        if (activity == null) {
            return;
        }
//        activity.addMessage(values[0]);
    }


    @Override
    protected void onPostExecute(JSONObject obj) {
//        mProgress.dismissAllowingStateLoss();

        RCReaderActivity activity = (RCReaderActivity)mRef.get();
        if (activity == null ||
                activity.isFinishing()) {
            return;
        }
        if (obj == null) {
            return;
        }

        /// region - API Logger section
        Retrofit retrofit = RetrofitUtil.getAdapter();
        ApiInterface aPiInterface = retrofit.create(ApiInterface.class);
        String[] keywords = new String[]{"ANDROID", "NFC", "RESIDENCE_CARD"};
        LogModel logModel = new LogModel(keywords, "className",
                "Error message", "functionName",obj.toString());
        aPiInterface.sendLogData(logModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe();


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
