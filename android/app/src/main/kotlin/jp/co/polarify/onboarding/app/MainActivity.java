package jp.co.polarify.onboarding.app;

import static jp.co.polarify.onboarding.app.BundleKeyDefinitions.APP_KEY_CAPTURE_KIND;
import static jp.co.polarify.onboarding.app.BundleKeyDefinitions.APP_KEY_MATCHING_ID_RESULT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bnpj_polarify_re.R;

import java.io.Serializable;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import jp.co.polarify.onboarding.app.utils.CheckFacetID;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.app.utils.EkycDataClass;
import jp.co.polarify.onboarding.app.utils.PreferenceManager;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.types.callback.GetMatchingIDCallback;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;

/**
 * 起動時の画面アクティビティです.
 */
public class MainActivity extends AppCompatActivity implements Serializable, MethodChannel.Result {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EkycDataClass.data = null;

        // インスタンス生成 (このとき、facetID が生成される.)
        final CheckFacetID checkFacetID = new CheckFacetID(this);

        // facetID をログで確認する場合
        checkFacetID.checkByLog();

        // PolarifyKycSdk の初期設定
        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(this);   // インスタンスを取得すると初期設定が実行される

        String documentType = getIntent().getStringExtra("documentType");

        switch (documentType) {
            case "DRIVING_LICENSE":
                PreferenceManager.putInt(getApplicationContext(), APP_KEY_CAPTURE_KIND, DocumentKind.DRIVER_LICENSE_CARD.ordinal());
                break;

            case "MY_NUMBER_CARD":
                PreferenceManager.putInt(getApplicationContext(), APP_KEY_CAPTURE_KIND, DocumentKind.MY_NUMBER_CARD.ordinal());
                break;

            case "RESIDENCE_CARD":
                PreferenceManager.putInt(getApplicationContext(), APP_KEY_CAPTURE_KIND, DocumentKind.RESIDENCE_CARD.ordinal());
                break;

            default:
                PreferenceManager.putInt(getApplicationContext(), APP_KEY_CAPTURE_KIND, DocumentKind.DRIVER_LICENSE_CARD.ordinal());
                break;
        }

        getMatchingID();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (EkycDataClass.data != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("data", "");
            ((MainActivity) this).setResult(com.example.bnpj_polarify_re.MainActivity.MainActivityObject.KYC_RESULT, returnIntent);
            finish();
        }
    }

    private void getMatchingID() {
        final Context context = getApplicationContext();
        PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(context).getMatchingID(new GetMatchingIDCallback() {
            @Override
            public void onSuccess(@NonNull final GetMatchingIDResult result) {
                PreferenceManager.putGetMatchingIDResult(context, APP_KEY_MATCHING_ID_RESULT, result);
                startIntroductionActivity(IntroductionActivity.IntroductionType.FIRST);
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                android.widget.Toast.makeText(getApplicationContext(), errorResult.getMessage(), android.widget.Toast.LENGTH_LONG).show(); // Release版でも通信エラーが分かるようにAndroid標準のトースト表示を行う
            }
        });
    }

    private void startIntroductionActivity(@NonNull final IntroductionActivity.IntroductionType type) {
        final Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
        intent.putExtra(IntroductionActivity.KEY_INTRODUCTION_TYPE, type);
        intent.putExtra("result", this);
        startActivityForResult(intent, 222);
    }

    @Override
    public void success(@Nullable Object result) {
        //yeha samma data aaucha.. yeslai chai toString() garera api ma send garne... Do that here--->

        HashMap<String, String> hashMap = (HashMap<String, String>) result;
        EkycDataClass.data = hashMap;

        Intent returnIntent = new Intent();
        if (result != null) {
            returnIntent.putExtra("data", hashMap);
        }
        ((MainActivity) this).setResult(com.example.bnpj_polarify_re.MainActivity.MainActivityObject.KYC_RESULT, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        SharedPreferences sharedPreferences = getSharedPreferences("bnpj", Context.MODE_PRIVATE);
        String frontImage = sharedPreferences.getString("front_document_image_ekyc", "");
        String backImage = sharedPreferences.getString("back_document_image_ekyc", "");
        String profileImage = sharedPreferences.getString("user_profile_image_ekyc", "");
        String tiltedImage = sharedPreferences.getString("tilted_document_image_ekyc", "");
        String profileImageVerification = sharedPreferences.getString("profile_image_verification_ekyc", "UNMATCH");
        String livenessVerification = sharedPreferences.getString("liveness_verification_ekyc", "UNMATCH");

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("front_document_image_ekyc", frontImage);
        hashMap.put("back_document_image_ekyc", backImage);
        hashMap.put("user_profile_image_ekyc", profileImage);

        if (com.example.bnpj_polarify_re.MainActivity.Companion.isManualEKYC())
            hashMap.put("tilted_document_image_ekyc", tiltedImage);


        hashMap.put("profile_image_verification_ekyc", profileImageVerification);
        hashMap.put("liveness_verification_ekyc", livenessVerification);
        hashMap.put("isManualEKYC", com.example.bnpj_polarify_re.MainActivity.Companion.isManualEKYC() ? "1" : "0");


        EkycDataClass.data = hashMap;

        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove("front_document_image_ekyc");
        sharedPreferencesEditor.remove("back_document_image_ekyc");
        sharedPreferencesEditor.remove("user_profile_image_ekyc");
        sharedPreferencesEditor.remove("tilted_document_image_ekyc");
        sharedPreferencesEditor.remove("profile_image_verification_ekyc");
        sharedPreferencesEditor.remove("liveness_verification_ekyc");
        sharedPreferencesEditor.commit();

        setResult(com.example.bnpj_polarify_re.MainActivity.MainActivityObject.KYC_RESULT);
        finish();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

    }

    @Override
    public void notImplemented() {

    }
}
