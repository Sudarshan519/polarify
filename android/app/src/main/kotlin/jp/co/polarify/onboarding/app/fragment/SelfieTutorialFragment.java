package jp.co.polarify.onboarding.app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.bnpj_polarify_re.MainActivity;
import com.example.bnpj_polarify_re.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.polarify.onboarding.app.BundleKeyDefinitions;
import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.apilogger.ApiInterface;
import jp.co.polarify.onboarding.app.apilogger.LogModel;
import jp.co.polarify.onboarding.app.apilogger.RetrofitUtil;
import jp.co.polarify.onboarding.app.toast.Toast;
import jp.co.polarify.onboarding.app.utils.ReturnDestinationConfirmer;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.callback.AddingDocumentsCallback;
import jp.co.polarify.onboarding.sdk.types.callback.GetFaceVerificationCallback;
import jp.co.polarify.onboarding.sdk.types.callback.LivenessCallback;
import jp.co.polarify.onboarding.sdk.types.callback.SelfieCaptureCallback;
import jp.co.polarify.onboarding.sdk.types.common.DocumentType;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.paramters.AddingDocumentsParameters;
import jp.co.polarify.onboarding.sdk.types.paramters.MatchingParameters;
import jp.co.polarify.onboarding.sdk.types.result.FaceCaptureResult;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;
import jp.co.polarify.onboarding.sdk.types.result.LivenessCaptureResult;
import jp.co.polarify.onboarding.sdk.types.result.VerificationResults;
import jp.co.polarify.onboarding.sdk.view.SafetyButton;
import retrofit2.Retrofit;

/**
 * 自動撮影のチュートリアル画面を生成フラグメントです.
 */
public class SelfieTutorialFragment extends TutorialFragment implements BundleKeyDefinitions {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(SelfieTutorialFragment.class);

    /**
     * フラグメントリプレスメッセージ ID.
     */
    public static final int FRAGMENT_REPLACE_ID = 1001;

    /**
     * バックスタックのメッセージID.
     */
    public static final int WHAT_BACKSTACK_ID = 1002;

    /**
     * ライブネスプログレスに切り替えるメッセージID.
     */
    public static final int LIVENESS_PROGRESS_ID = 1003;

    /**
     * セルフィープログレスに切り替えるメッセージID.
     */
    public static final int SELFIE_PROGRESS_ID = 1004;

    /**
     * ダイアログ表示遅延時間(単位 ミリ秒).
     */
    private static final long DELAY_TIME = 1000;

    /**
     * ライブネス起動遅延時間(単位 ミリ秒).
     */
    private static final long LIVENESS_DELAY_TIME = 300;

    /**
     * バックボタンでもどってきた場合はtrueになり、処理時にfalseに戻します.
     */
    private boolean backPressedFlag;

    /**
     * 撮影開始時にtrueになりバックボタンで戻った場合はfalseになります.
     */
    private boolean captureSuccessFlag;

    /**
     * 撮影を始めるボタンです.
     */
    private SafetyButton button = null;

    /**
     * 次へ進む時ネット通信Progressです.
     */
    private ProgressBar progressBar = null;

    /**
     * プログレス切り替え用ハンドラ.
     */
    private Handler progressHandler = new Handler();

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        logger.debug("onViewCreated is called", new Throwable());
        super.onViewCreated(view, savedInstanceState);

        final TextView textView = view.findViewById(R.id.tutorial_text);
        textView.setText(R.string.selfie_tutorial_text);
        setGifByGlide(R.raw.phone_vertical);

        progressBar = view.findViewById(R.id.waitting_progress_bar_id);
        button = view.findViewById(R.id.tutorial_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                registerSelfie();
                captureSuccessFlag = true;
                getView().getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
                    @Override
                    public void onWindowFocusChanged(boolean hasFocus) {
                        clearMessage();
                        if (captureSuccessFlag) {
                            if (!backPressedFlag) {
                                if (hasFocus) {
                                    sendMessage(LIVENESS_PROGRESS_ID);
                                }
                            } else {
                                sendMessage(SELFIE_PROGRESS_ID);
                                backPressedFlag = false;
                            }
                        } else {
                            sendMessage(SELFIE_PROGRESS_ID);
                        }
                    }
                });
            }
        }, false);

        replaceSelfieProgressAreaFragment();
    }

    /**
     * セルフィー画像の取得を行います.
     */
    private void registerSelfie() {
        progressBar.setVisibility(View.VISIBLE);
        final Context context = getContext();
        if (context == null) {
            final String message = "Cannot get Context";
            logger.error(message);
            throw new IllegalStateException(message);
        }
        registerSelfie(context);
    }

    /**
     * セルフィー画像の取得を行います.
     *
     * @param context コンテキスト
     */
    private void registerSelfie(@NonNull final Context context) {
        logger.debug("registerSelfie is called");
        Log.d("onError: getCode", "entered");

        final GetMatchingIDResult result = getMatchingIDResult();
        if (result == null) {
            final String message = "Cannot get GetMatchingIDResult";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final MatchingParameters parameters = new MatchingParameters(result);
        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(context).registerSelfie(parameters, new SelfieCaptureCallback() {
            @Override
            public void onSuccess(@NonNull final FaceCaptureResult faceCaptureResult) {
                final Bitmap image = faceCaptureResult.getImage();
                TutorialActivity.setSelfieImage(image); // セルフィー画像の通知

                final String message = "成功";
                Toast.showLong(context, message);
                logger.debug(message);
                captureSuccessFlag = true;
                sendMessage(LIVENESS_PROGRESS_ID);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLiveness(context, getMatchingIDResult());
                    }
                }, LIVENESS_DELAY_TIME);
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                Log.d("onError: getCode", String.valueOf(errorResult.getCode()));
                Log.d("onError: getDescription", errorResult.getDescription());
                Log.d("onError: getMessage", errorResult.getMessage());
                Toast.showLong(context, "失敗");
                progressHandler.removeCallbacksAndMessages(null);
                clearMessage();
                sendMessage(SELFIE_PROGRESS_ID);
                captureSuccessFlag = false;
                final ConstraintLayout layout = getView().findViewById(R.id.constraint_layout);
                layout.setVisibility(View.VISIBLE);
                if (ErrorResult.SERVER_CONNECTION_ERROR.equals(errorResult)) {
                    showDelayedDialogForSelfie(context, errorResult);
                    return;
                }

                if (!ErrorResult.USER_CANCELLED.equals(errorResult)) {
                    ReturnDestinationConfirmer.execute(context, errorResult);
                    button.unlock();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                showDelayedDialogForSelfie(context, errorResult);
            }
        });
    }

    /**
     * セルフィーの通信エラーダイアログを表示します.
     *
     * @param context コンテキスト
     */
    protected void showConnectionErrorSelfieDialog(@NonNull final Context context) {
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(context, R.style.SDK_DialogStyle)
                .setTitle(R.string.connection_error_dialog_title)
                .setMessage(R.string.connection_error_dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_dialog_text, new DialogInterface.OnClickListener() {   // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(context, ErrorResult.SERVER_CONNECTION_ERROR);
                    }
                }).create().show();  // ダイアログの生成と表示
    }

    /**
     * セルフィーのキャンセルダイアログを遅延表示します.
     *
     * @param context コンテキスト
     */
    private void showDelayedDialogForSelfie(@NonNull final Context context, @NonNull final ErrorResult errorResult) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ErrorResult.USER_CANCELLED.equals(errorResult)) {
                    showCancelSelfieDialog(context);
                    return;
                }
                if (ErrorResult.SERVER_CONNECTION_ERROR.equals(errorResult)) {
                    showConnectionErrorSelfieDialog(context);
                }
            }
        }, DELAY_TIME);
    }

    /**
     * セルフィーのキャンセルダイアログを表示します.
     *
     * @param context コンテキスト
     */
    protected void showCancelSelfieDialog(@NonNull final Context context) {
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(context, R.style.SDK_DialogStyle)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() {   // 「続ける」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        backPressedFlag = true;
                        captureSuccessFlag = true;
                        registerSelfie(context);
                    }
                })
                .setNegativeButton(R.string.dialog_button_return, new DialogInterface.OnClickListener() {    // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(context, ErrorResult.USER_CANCELLED);
                    }
                }).create().show();  // ダイアログの生成と表示
    }

    private void moveToNextScreen(@NonNull final Context context){
        Toast.showLong(context, "成功");
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);
        sendMessage(FRAGMENT_REPLACE_ID);
    }

    /**
     * ライブネス撮影を開始します.
     *
     * @param context コンテキスト
     * @param result  マッチング ID
     */
    private void startLiveness(@NonNull final Context context, @NonNull final GetMatchingIDResult result) {
        final MatchingParameters parameter = new MatchingParameters(result);
        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();


        factory.getInstance(context).startLiveness(parameter, new LivenessCallback() {
            @Override
            public void onSuccess(@NonNull final LivenessCaptureResult livenessCaptureResult) {
//                final String kamalSirProfilePhoto = "data:image/jpeg;base64,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";
                if(MainActivity.Companion.isManualEKYC()){
                    moveToNextScreen(context);
                }
                else {
                    String profileImageStr = MainActivity.Companion.getICProfileImage();
                    Log.d("decodedByte", profileImageStr);

                    profileImageStr = profileImageStr.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");

                    byte[] decodedString = Base64.decode(profileImageStr,
                            Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Log.d("getMatchingIDResult", String.valueOf(getMatchingIDResult()));
                    Log.d("DocumentType", String.valueOf(DocumentType.OTHER));
                    Log.d("decodedByte", String.valueOf(decodedByte));
//                    DataObj obj = new DataObj(String.valueOf(getMatchingIDResult()), String.valueOf(DocumentType.OTHER), String.valueOf(decodedByte));
                    JSONObject json = new JSONObject();
                    try {
                        json.put("getMatchingIDResult", String.valueOf(getMatchingIDResult()));

                        json.put("DocumentType", String.valueOf(DocumentType.OTHER));
                        json.put("decodedByte", String.valueOf(decodedByte));
                        json.put("profileImageStr", profileImageStr);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Retrofit retrofit = RetrofitUtil.getAdapter();
                    ApiInterface aPiInterface = retrofit.create(ApiInterface.class);
                    String[] keywords = new String[]{"ANDROID", "NFC", "RESIDENCE_CARD"};
                    LogModel logModel = new LogModel(keywords, "className",
                            "Error message", "functionName: DatafromSdkSelfie",json.toString());
                    aPiInterface.sendLogData(logModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe();
                    if(profileImageStr== null || profileImageStr.isEmpty()){
                        moveToNextScreen(context);
                    }
                    else {
                        factory.getInstance(context).registerFaceOfDocument(new AddingDocumentsParameters(
                                getMatchingIDResult(), DocumentType.OTHER, decodedByte
                        ), new AddingDocumentsCallback() {
                            @Override
                            public void onSuccess() {
                                moveToNextScreen(context);
                            }

                            @Override
                            public void onError(@NonNull ErrorResult errorResult) {
                                moveToNextScreen(context);
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                Toast.showLong(context, "失敗");
                if (ErrorResult.TIMEOUT_ERROR.equals(errorResult)) {
                    button.unlock();
                    progressBar.setVisibility(View.INVISIBLE);
                    showDelayedDialogForTimeout(context);
                    return;
                }
                if (ErrorResult.SERVER_CONNECTION_ERROR.equals(errorResult)) {
                    showDelayedDialogForLiveness(context, errorResult);
                    return;
                }
                if (!ErrorResult.USER_CANCELLED.equals(errorResult)) {
                    ReturnDestinationConfirmer.execute(context, errorResult);
                    button.unlock();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                showDelayedDialogForLiveness(context, errorResult);
            }
        });
    }


    /**
     * ライブネスでタイムアウトした時のダイアログを遅延表示します.
     *
     * @param context コンテキスト
     */
    private void showDelayedDialogForLiveness(@NonNull final Context context, @NonNull final ErrorResult errorResult) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ErrorResult.USER_CANCELLED.equals(errorResult)) {
                    showCancelLivenessDialog(context);
                    return;
                }
                if (ErrorResult.SERVER_CONNECTION_ERROR.equals(errorResult)) {
                    showConnectionErrorLivenessDialog(context);
                }
            }
        }, DELAY_TIME);
    }

    /**
     * ライブネスのキャンセルダイアログを表示します.
     *
     * @param context コンテキスト
     */
    protected void showCancelLivenessDialog(@NonNull final Context context) {
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() {   // 「続ける」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        startLiveness(context, getMatchingIDResult());
                    }
                })
                .setNegativeButton(R.string.dialog_button_return, new DialogInterface.OnClickListener() {    // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(context, ErrorResult.USER_CANCELLED);
                        button.unlock();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).create().show(); // ダイアログの生成・表示
    }

    /**
     * ライブネスの通信エラーダイアログを表示します.
     *
     * @param context コンテキスト
     */
    protected void showConnectionErrorLivenessDialog(@NonNull final Context context) {
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(context, R.style.SDK_DialogStyle)
                .setTitle(R.string.connection_error_liveness_dialog_title)
                .setMessage(R.string.connection_error_liveness_dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_liveness_dialog_text, new DialogInterface.OnClickListener() {   // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(context, ErrorResult.SERVER_CONNECTION_ERROR);
                    }
                }).create().show();  // ダイアログの生成と表示
    }

    /**
     * ライブネスでタイムアウトした時のダイアログを遅延表示します.
     */
    private void showDelayedDialogForTimeout() {
        final Context context = getContext();
        if (context == null) {
            final String errorMessage = "Cannot get Context";
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        showDelayedDialogForTimeout(context);
    }

    /**
     * ライブネスでタイムアウトした時のダイアログを遅延表示します.
     *
     * @param context コンテキスト
     */
    private void showDelayedDialogForTimeout(@NonNull final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showTimeoutLivenessDialog(context);
            }
        }, DELAY_TIME);
    }

    /**
     * ライブネスでタイムアウトした時のダイアログを表示します.
     *
     * @param context コンテキスト
     */
    protected void showTimeoutLivenessDialog(@NonNull final Context context) {
        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.timeout_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() {   // 「続ける」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        startLiveness(context, getMatchingIDResult());
                    }
                })
                .setNegativeButton(R.string.dialog_button_return, new DialogInterface.OnClickListener() {    // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(context, ErrorResult.USER_CANCELLED);
                    }
                }).create().show(); // ダイアログの生成・表示
    }

    /**
     * メッセージの処理を行います.
     *
     * @param message 処理対象メッセージ
     */
    @Override
    protected void processMessage(@NonNull final Message message) {
        switch (message.what) {
            case WHAT_BACKSTACK_ID:
                showDelayedDialogForTimeout(); // ダイアログを表示
                break;
            case FRAGMENT_REPLACE_ID:
                replaceNextFragment();
                break;
            case SELFIE_PROGRESS_ID:
                replaceSelfieProgressAreaFragment();
                break;
            case LIVENESS_PROGRESS_ID:
                final View root = getView();
                if (root == null) { // この処理に至るときは resume されているので通常はあり得ないため、例外を投げることにする
                    final String errorMessage = "Cannot get root view";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                final ConstraintLayout layout = root.findViewById(R.id.constraint_layout);
                layout.setVisibility(View.GONE);
                replaceLivenessProgressAreaFragment();
                break;
        }
    }

    /**
     * 次に表示するフラグメントに切り替えます.
     */
    protected void replaceNextFragment() {
        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            final String message = "Cannot get FragmentManager";
            logger.error(message);
            throw new IllegalStateException(message);
        }
        final FragmentTransaction transaction = manager.beginTransaction(); // フラグメント切り替えトランザクションの取得

        final Fragment fragment = getNextFragment();    // 次に表示するフラグメントを取得
        transaction.replace(R.id.fragment_container, fragment);

        final Fragment progressAreaFragment = new RequestProgressAreaFragment();
        transaction.replace(R.id.progress_container, progressAreaFragment);

        transaction.commit();
    }

    /**
     * 次に表示するフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    protected Fragment getNextFragment() {
        return new RequestFragment();
    }

    /**
     * セルフィープログレスエリアのフラグメントの切り替えを行います.
     */
    protected void replaceSelfieProgressAreaFragment() {
        final Fragment fragment = getSelfieProgressAreaFragment();
        if (fragment == null) {
            return;
        }
        replaceFragment(R.id.progress_container, fragment);
    }

    /**
     * セルフィープログレスエリアのフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @Nullable
    protected Fragment getSelfieProgressAreaFragment() {
        return new SelfieProgressAreaFragment();
    }

    /**
     * ライブネスプログレスエリアのフラグメントの切り替えを行います.
     */
    protected void replaceLivenessProgressAreaFragment() {
        final Fragment fragment = getLivenessProgressAreaFragment();
        if (fragment == null) {
            return;
        }
        replaceFragment(R.id.progress_container, fragment);
    }

    /**
     * ライブネスプログレスエリアのフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @Nullable
    protected Fragment getLivenessProgressAreaFragment() {
        return new LivenessProgressAreaFragment();
    }

    /**
     * 指定された ID の View のフラグメントを切り替えます.
     *
     * @param id       フラグメントを切り替える View の ID
     * @param fragment 切り替えるフラグメント
     */
    protected void replaceFragment(@IdRes final int id, @NonNull final Fragment fragment) {
        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            final String message = "Cannot get FragmentManager";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id, fragment).commit();
    }

    @Override
    public void onPause() {
        // 仕様不明のところが多くて、念のため、ここでボタンとインジケータを初期状態にします.
        button.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        super.onPause();
    }
}

class DataObj{
    String getMatchingIDResultss;
    String DocumentTypess;
    String decodedBytess;

    public DataObj(String getMatchingIDResult, String documentType, String decodedByte) {
        this.getMatchingIDResultss = getMatchingIDResult;
        DocumentTypess = documentType;
        this.decodedBytess = decodedByte;
    }
}
