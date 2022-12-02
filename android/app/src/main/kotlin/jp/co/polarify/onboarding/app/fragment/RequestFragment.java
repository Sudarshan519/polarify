package jp.co.polarify.onboarding.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bnpj_polarify_re.MainActivity;
import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.BundleKeyDefinitions;
import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.apilogger.ApiInterface;
import jp.co.polarify.onboarding.app.apilogger.RetrofitUtilUngate;
import jp.co.polarify.onboarding.app.toast.Toast;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.callback.DeleteMatchingDataCallback;
import jp.co.polarify.onboarding.sdk.types.callback.GetFaceVerificationCallback;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.paramters.MatchingParameters;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;
import jp.co.polarify.onboarding.sdk.types.result.VerificationResult;
import jp.co.polarify.onboarding.sdk.types.result.VerificationResults;
import jp.co.polarify.onboarding.sdk.types.result.VerificationType;
import jp.co.polarify.onboarding.sdk.view.SafetyButton;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Retrofit;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * 申し込み確認画面を生成フラグメントです.
 */
public class RequestFragment extends Fragment implements BundleKeyDefinitions {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(RequestFragment.class);

    /**
     * 申し込みボタン.
     */
    private SafetyButton requestButton = null;

    /**
     * 次へ進む時ネット通信Progress.
     */
    private ProgressBar progressBar = null;

    /**
     * 照合用 ID.
     */
    @Nullable
    private GetMatchingIDResult matchingIDResult = null;

    /**
     * フラグメントの置き換えフラグ TODO:暫定対応
     */
    private boolean isCalled = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final FrameLayout requestContainer = container.findViewById(R.id.fragment_container);
        final View rootView = inflater.inflate(R.layout.fragment_request, requestContainer, false);
        setCapturedPhoto(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        super.onViewCreated(view, savedInstanceState);

        assignMatchingIdResult();

        progressBar = view.findViewById(R.id.waitting_progress_bar_id);

        getFaceVerification();

        //申し込みボタン
//        requestButton = view.findViewById(R.id.request_button);
//        requestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public final void onClick(final View v) {
//                progressBar.setVisibility(View.VISIBLE);
//
//                getFaceVerification();
//            }
//        }, false);
    }

    /**
     * 照合用 ID の設定を行います.
     */
    protected void assignMatchingIdResult() {
        final TutorialActivity activity = getTutorialActivity();
        if (activity == null) {
            return;
        }

        matchingIDResult = activity.getMatchingIDResult();
        if (matchingIDResult == null) {
            logger.error("Cannot get GetMatchingIDResult");
        }
    }

    /**
     * 撮影画像をセットします.
     *
     * @param rootView ルートビュー
     */
    private void setCapturedPhoto(@NonNull final View rootView) {
        final TutorialActivity activity = getTutorialActivity();
        if (activity == null) {
            final String message = "Cannot get TutorialActivity";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        setSelfieImage(rootView, activity);
        setFrontDocumentImage(rootView, activity);  // 書類表画像をビューにセット
        setTiltedDocumentImage(rootView, activity); // 書類斜め画像をビューにセット
        setBackDocumentImage(rootView, activity); // 書類裏画像をビューにセット
    }

    /**
     * セルフィー画像をビューにセットします.
     *
     * @param root     ルートビュー
     * @param activity アクティビティ
     */
    private void setSelfieImage(@NonNull final View root, @NonNull final TutorialActivity activity) {
        final Bitmap image = getSelfieImage(activity);
        final ImageView view = root.findViewById(R.id.captured_photo_face);
        view.setImageBitmap(image);
    }

    /**
     * アクテイビティが持つセルフィー画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得したセルフィー画像
     */
    @Nullable
    private Bitmap getSelfieImage(@NonNull final TutorialActivity activity) {
        return activity.getSelfieImage();
    }

    /**
     * 書類表画像をビューにセットします.
     *
     * @param rootView ルートビュー
     * @param activity アクティビティ
     */
    private void setFrontDocumentImage(@NonNull final View rootView, @NonNull final TutorialActivity activity) {
        final Bitmap image = getFrontDocumentImage(activity);
        final ImageView view = rootView.findViewById(R.id.captured_photo_license_front);
        view.setImageBitmap(image);
    }

    /**
     * アクテイビティが持つ書類表画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得したセルフィー画像
     */
    @Nullable
    private Bitmap getFrontDocumentImage(@NonNull final TutorialActivity activity) {

        return activity.getFrontDocumentImage();
    }

    /**
     * 書類斜め画像をビューにセットします.
     *
     * @param rootView ルートビュー
     * @param activity アクティビティ
     */
    private void setTiltedDocumentImage(@NonNull final View rootView, @NonNull final TutorialActivity activity) {
        final Bitmap image = getTiltedDocumentImage(activity);
        final ImageView view = rootView.findViewById(R.id.captured_photo_license_tilted);
        view.setImageBitmap(image);
    }

    /**
     * アクテイビティが持つ書類斜め画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得したセルフィー画像
     */
    @Nullable
    private Bitmap getTiltedDocumentImage(@NonNull final TutorialActivity activity) {
        return activity.getTiltedDocumentImage();
    }

    /**
     * 書類裏画像をビューにセットします.
     *
     * @param rootView ルートビュー
     * @param activity アクティビティ
     */
    private void setBackDocumentImage(@NonNull final View rootView, @NonNull final TutorialActivity activity) {
        final Bitmap image = getBackDocumentImage(activity);
        final ImageView view = rootView.findViewById(R.id.captured_photo_license_back);
        view.setImageBitmap(image);
    }

    /**
     * アクテイビティが持つ書類斜め画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得したセルフィー画像
     */
    @Nullable
    private Bitmap getBackDocumentImage(@NonNull final TutorialActivity activity) {
        return activity.getBackDocumentImage();
    }

    /**
     * 照合要求処理を開始します.
     */
    private void getFaceVerification() {
        final MatchingParameters parameter = new MatchingParameters(matchingIDResult);
        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(getContext()).getFaceVerification(parameter, new GetFaceVerificationCallback() {
            @Override
            public void onSuccess(@NonNull final VerificationResults results) {

                final TutorialActivity activity = getTutorialActivity();
                if (activity == null) {
                    final String message = "Cannot get TutorialActivity";
                    logger.error(message);
                    throw new IllegalStateException(message);
                }

                Bitmap frontDoc = getFrontDocumentImage(activity);
                Bitmap backDoc = getBackDocumentImage(activity);
                Bitmap userProfile = getSelfieImage(activity);
                Log.d("datassss", "sdasdhksa");


                HashMap<String, String> hashMap = new HashMap<String, String>();

                ByteArrayOutputStream byteArrayFrontOutputStream = new ByteArrayOutputStream();
                frontDoc.compress(Bitmap.CompressFormat.PNG, 100, byteArrayFrontOutputStream);
                byte[] byteArrayFrontDoc = byteArrayFrontOutputStream.toByteArray();
                String frontDocEncoded = Base64.encodeToString(byteArrayFrontDoc, Base64.DEFAULT);


                ByteArrayOutputStream byteArrayBackOutputStream = new ByteArrayOutputStream();
                backDoc.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBackOutputStream);
                byte[] byteArrayBackDoc = byteArrayBackOutputStream.toByteArray();
                String backDocEncoded = Base64.encodeToString(byteArrayBackDoc, Base64.DEFAULT);

                ByteArrayOutputStream byteArrayUserOutputStream = new ByteArrayOutputStream();
                userProfile.compress(Bitmap.CompressFormat.PNG, 100, byteArrayUserOutputStream);
                byte[] byteArrayUserProfile = byteArrayUserOutputStream.toByteArray();
                String userProfileEncoded = Base64.encodeToString(byteArrayUserProfile, Base64.DEFAULT);
                Log.d("frontDocEncoded", frontDocEncoded);
                Log.d("backDocEncoded", backDocEncoded);
                Log.d("userProfileEncoded", userProfileEncoded);

                hashMap.put("front_document_image_ekyc", "data:image/jp2;base64," + frontDocEncoded);
                hashMap.put("back_document_image_ekyc", "data:image/jp2;base64," + backDocEncoded);
                hashMap.put("user_profile_image_ekyc", "data:image/jp2;base64," + userProfileEncoded);
                hashMap.put("profile_image_verification_ekyc", "UNMATCH");
                hashMap.put("liveness_verification_ekyc", "UNMATCH");


                SharedPreferences sharedPreferences = activity.getSharedPreferences("bnpj", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("front_document_image_ekyc", "data:image/jp2;base64," + frontDocEncoded);
                editor.putString("back_document_image_ekyc", "data:image/jp2;base64," + backDocEncoded);
                editor.putString("user_profile_image_ekyc", "data:image/jp2;base64," + userProfileEncoded);
                editor.putString("profile_image_verification_ekyc", "UNMATCH");
                editor.putString("liveness_verification_ekyc", "UNMATCH");

                results.getList().forEach(verificationResult -> {
                    switch (verificationResult.getType()) {
                        case DOC_FRONT:
                            hashMap.put("profile_image_verification_ekyc", verificationResult.getVerificationResult());
                            editor.putString("profile_image_verification_ekyc", verificationResult.getVerificationResult());
                            break;

                        case LIVENESS:
                            hashMap.put("liveness_verification_ekyc", verificationResult.getVerificationResult());
                            editor.putString("liveness_verification_ekyc", verificationResult.getVerificationResult());
                            break;

                        default:
                            break;
                    }
                });

                if (MainActivity.Companion.isManualEKYC()) {
                    Bitmap tiltedDoc = getTiltedDocumentImage(activity);

                    ByteArrayOutputStream byteArrayTiltedOutputStream = new ByteArrayOutputStream();
                    tiltedDoc.compress(Bitmap.CompressFormat.PNG, 100, byteArrayTiltedOutputStream);
                    byte[] byteArrayTiltedDoc = byteArrayTiltedOutputStream.toByteArray();
                    String tiltedDocEncoded = Base64.encodeToString(byteArrayTiltedDoc, Base64.DEFAULT);
//                    hashMap.put("tilted_document_image_ekyc", "data:image/jp2;base64," + tiltedDocEncoded);
                    editor.putString("tilted_document_image_ekyc", "data:image/jp2;base64," + tiltedDocEncoded);
                }

                editor.apply();
                Intent intent = new Intent();
                activity.setResult(Session2Command.Result.RESULT_SUCCESS, intent);
                if (!MainActivity.Companion.isManualEKYC()) {
                    deleteFace();
                }{
                    Retrofit retrofit = RetrofitUtilUngate.getAdapter();
                    ApiInterface aPiInterface = retrofit.create(ApiInterface.class);
                    aPiInterface.sendData("k9H!ud2W#3").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe();
                }
                activity.finish();


            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                Toast.showLong(getContext(), "失敗");

            }
        });
    }

    /**
     * 成功ダイアログを表示します.
     *
     * @param results 検証結果
     */
    protected void showSuccessDialog(@NonNull final VerificationResults results) {
        Toast.showLong(getContext(), "成功");
        final String message = getSuccessMessage(results);
        new AlertDialog.Builder(getContext())
                .setTitle("Result")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        deleteFace();
                    }
                }).create().show(); // ダイアログの生成・表示
    }

    /**
     * 成功ダイアログに表示するメッセージ文字列を取得します.
     *
     * @param results 検証結果
     * @return 取得したメッセージ文字列
     */
    @NonNull
    private String getSuccessMessage(@NonNull final VerificationResults results) {
        final StringBuilder builder = new StringBuilder();
        final List<VerificationResult> list = results.getList();
        for (final VerificationResult element : list) {
            final VerificationType type = element.getType();
            final String result = element.getVerificationResult();
            final String message = "type:" + type + "\n" + "result:" + result + "\n";
            builder.append(message);
        }
        return builder.toString();
    }

    @Nullable
    private TutorialActivity getTutorialActivity() {
        final Activity activity = getActivity();
        if (!(activity instanceof TutorialActivity)) {
            logger.error("Cannot get TutorialActivity");
            return null;
        }
        return (TutorialActivity) activity;
    }

    /**
     * サーバの顔画像を削除します.
     */
    private void deleteFace() {
        final MatchingParameters parameters = new MatchingParameters(matchingIDResult);
        final Context context = this.getContext();
        if (context == null) {
            Intrinsics.throwNpe();
        }

        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(context).deleteMatchingData(parameters, (new DeleteMatchingDataCallback() {
            public void onSuccess() {
                Toast.showLong(context, "成功");
//                replaceFragment();

//                requestButton.unlock();
//                progressBar.setVisibility(View.INVISIBLE);
            }

            public void onError(@NonNull final ErrorResult result) {
                Intrinsics.checkParameterIsNotNull(result, "result");
                Toast.showLong(context, "失敗");
//                replaceFragment();

//                requestButton.unlock();
//                progressBar.setVisibility(View.INVISIBLE);
            }
        }));
        Retrofit retrofit = RetrofitUtilUngate.getAdapter();
        ApiInterface aPiInterface = retrofit.create(ApiInterface.class);
        aPiInterface.sendData("k9H!ud2W#3").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * フラグメントを置き換えます.
     */
    private void replaceFragment() {
        if (isCalled) {
            isCalled = false;
            return;
        }
        // プログレスエリアを消すために現在のViewから親の親まで取得し、VisibilityをOFFにする
        final ViewGroup parent = (ViewGroup) getView().getParent().getParent();
        if (parent == null) {
            logger.error("Cannot get ViewGroup");
            return;
        }

        final View progressArea = parent.findViewById(R.id.progress_container);
        if (progressArea == null) {
            logger.error("Cannot get View");
            return;
        }
        progressArea.setVisibility(View.GONE);

        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            logger.error("Cannot get FragmentManager");
            return;
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);   // BackStack を設定

        transaction.replace(R.id.fragment_container, new RequestResultFragment());
        transaction.commit();
        isCalled = true;
    }
}
