package jp.co.polarify.onboarding.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.BundleKeyDefinitions;

import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.toast.Toast;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.app.utils.PreferenceManager;
import jp.co.polarify.onboarding.app.utils.ReturnDestinationConfirmer;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.callback.DocumentCaptureCallback;
import jp.co.polarify.onboarding.sdk.types.common.DocumentType;
import jp.co.polarify.onboarding.sdk.types.common.RectPoints;
import jp.co.polarify.onboarding.sdk.types.internal.DocumentSides;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.paramters.DocumentCaptureParameters;
import jp.co.polarify.onboarding.sdk.types.paramters.GuidePositionParameters;
import jp.co.polarify.onboarding.sdk.types.result.DocumentResult;
import jp.co.polarify.onboarding.sdk.types.result.GuidePositionResult;
import jp.co.polarify.onboarding.sdk.view.SafetyButton;

/**
 * 書類撮影のチュートリアル画面フラグメントの共通基底クラスです.
 */
abstract class DocumentCaptureTutorialFragment extends SmartReplaceableFragment {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(DocumentCaptureTutorialFragment.class);

    /**
     * フラグメントリプレスメッセージ ID.
     */
    public static final int FRAGMENT_REPLACE_ID = 1001;

    /**
     * カメラビューのサイズ.
     */
    @Nullable
    private Size cameraSize;

    /**
     * カメラ要求コード
     */
    private static final int PERMISSION_REQUEST_CAMERA = 100;

    /**
     * 撮影を始めるボタンです.
     */
    private SafetyButton startCaptureButton = null;

    /**
     * 次へ進む時ネット通信Progressです.
     */
    private ProgressBar progressBar = null;

    /**
     * シャッターボタンと説明文言のおよその合計の高さ.
     */
    private static final int CONTENTS = 80;

    /**
     * 撮影を行う書類の向きを取得します.
     *
     * @return 取得した撮影を行う書類の向き
     */
    abstract protected DocumentSides getDocumentSides(@NonNull final DocumentKind documentKind);

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (container == null) {
            logger.error("container is null");
            return null;
        }

        final FrameLayout tutorialContainer = container.findViewById(R.id.fragment_container);
        return inflater.inflate(R.layout.fragment_tutorial, tutorialContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        assignOpeningMessage(view);
        assignGifAnimation();
    }

    /**
     * ビューの初期設定を行います.
     *
     * @param root ルートビュー
     */
    protected void initializeViews(@NonNull final View root) {
        final ConstraintLayout constraintLayout = root.findViewById(R.id.constraint_layout);
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cameraSize = new Size(root.getWidth(), root.getHeight());
                constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this); // ViewTreeObserver は毎回取得しないといけない

                final Bundle arguments = getArguments();
                if (arguments == null) {
                    return;
                }

                final boolean isStartCapture = arguments.getBoolean(BundleKeyDefinitions.APP_KEY_DIRECT_START_CAPTURE);
                if (isStartCapture) {
                    checkCameraPermission();
                }
            }
        });

        progressBar = root.findViewById(R.id.waitting_progress_bar_id);

        startCaptureButton = root.findViewById(R.id.tutorial_button); // 撮影画面へ遷移するボタン
        startCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);

                checkCameraPermission();
            }
        }, false);
    }

    /**
     * 処理開始時のメッセージを表示します.
     *
     * @param root ルートビュー
     */
    private void assignOpeningMessage(@NonNull final View root) {
        final TextView textView = root.findViewById(R.id.tutorial_text);

        @StringRes final int id = getOpeningMessageId();
        textView.setText(id);
    }

    /**
     * 処理開始時に表示するメッセージのリソース ID を取得します.
     *
     * @return 取得したメッセージのリソース ID
     */
    @StringRes
    abstract protected int getOpeningMessageId();

    /**
     * GIF アニメーションの設定を行います.
     */
    private void assignGifAnimation() {
        @RawRes final int id = getGifId();  // GIF アニメーションを表示する View の ID を取得
        setGifByGlide(id);
    }

    /**
     * GIF アニメーションのリソース ID を取得します.
     *
     * @return 取得した GIF アニメーションのリソース ID
     */
    @RawRes
    abstract protected int getGifId();

    /**
     * カメラ起動します．
     */
    private void startCapture() {
        logger.debug("startCapture is called");

        final TutorialActivity activity = getTutorialActivity();
        if (activity == null) {
            return;
        }

        final DocumentKind documentKind = DocumentKind.getDocumentKind(PreferenceManager.getInt(getContext(), BundleKeyDefinitions.APP_KEY_CAPTURE_KIND));
        final DocumentType documentType = DocumentSides.toDocumentType(getDocumentSides(documentKind));
        final RectPoints guidePoints = getGuidePoint(activity, documentType);
        if (guidePoints == null) {
            logger.error("Cannot get GuidePoint");
            return;
        }

        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        final DocumentCaptureParameters parameters = new DocumentCaptureParameters(documentType, guidePoints, cameraSize);
        factory.getInstance(activity).startCapture(parameters, new DocumentCaptureCallback() {
            @Override
            public void onSuccess(@NonNull final DocumentResult documentResult) {
                final String message = "成功";
                logger.debug(message);
                Toast.showLong(activity, message);

                onImageCaptured(activity, documentResult);

                startCaptureButton.unlock();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                final String message = "失敗";
                logger.debug(message);
                Toast.showLong(activity, message);

                if (errorResult.equals(ErrorResult.TIMEOUT_ERROR)) {
                    startCaptureButton.unlock();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if (!errorResult.equals(ErrorResult.USER_CANCELLED)) {
                    ReturnDestinationConfirmer.execute(activity, errorResult);
                    startCaptureButton.unlock();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                showDelayedDialog();
            }
        });
    }

    /**
     * ガイド枠の四点座標を取得します.
     *
     * @param context コンテキスト
     * @param type    書類種別
     * @return 取得したガイド枠の四点座標
     */
    @Nullable
    private RectPoints getGuidePoint(@NonNull final Context context, @NonNull final DocumentType type) {
        final Size size = new Size(cameraSize.getWidth(), cameraSize.getHeight() - CONTENTS); // シャッターボタンと説明文言の高さの合計を引く
        final GuidePositionParameters parameter = new GuidePositionParameters(type, size);
        final PolarifyKycSdkFactory polarifyKycSdkFactory = new PolarifyKycSdkFactory();
        final GuidePositionResult result = polarifyKycSdkFactory.getInstance(context).getGuidePosition(parameter);
        return result.getRectPoints();
    }

    /**
     * キャンセルダイアログを 1 秒後に表示します.
     */
    private void showDelayedDialog() {
        final TutorialActivity activity = getTutorialActivity();    // アクティビティの取得
        if (activity == null) { // 取得失敗
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showCancelDialog(activity);
            }
        }, 1000);
    }

    /**
     * キャンセルダイアログを表示します.
     *
     * @param activity アクティビティ
     */
    protected void showCancelDialog(@NonNull final TutorialActivity activity) {
        startCaptureButton.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        if (activity.isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() { // 「続ける」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        startCapture();
                    }
                })
                .setNegativeButton(R.string.dialog_button_return, new DialogInterface.OnClickListener() {   // 「トップへ戻る」ボタン
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        logger.debug("Return home selected.");
                        ReturnDestinationConfirmer.execute(activity, ErrorResult.USER_CANCELLED);
                    }
                }).create().show(); // ダイアログを生成・表示
    }

    /**
     * TutorialActivity を取得します.
     *
     * @return 取得した TutorialActivity
     */
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
     * 画像がキャプチャされた時の処理を行います.
     *
     * @param activity アクティビティ
     * @param result   キャプチャ結果
     */
    private void onImageCaptured(@NonNull final TutorialActivity activity, @NonNull final DocumentResult result) {
        notifyDocumentResult(activity, result); // キャプチャ画像をアクティビティに通知
        sendMessage(FRAGMENT_REPLACE_ID);   // フラグメント切り替えのメッセージを発行
    }

    /**
     * メッセージの処理を行います.
     *
     * @param message 処理対象メッセージ
     */
    @Override
    protected void processMessage(@NonNull final Message message) {
        if (message.what != FRAGMENT_REPLACE_ID) {
            return;
        }

        replaceNextFragment();
    }

    /**
     * 次に表示するフラグメントの切り替えを行います.
     */
    protected void replaceNextFragment() {
        final Fragment fragment = getNextFragment();
        replaceFragment(R.id.fragment_container, fragment);
    }

    /**
     * 次に表示するフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    abstract protected Fragment getNextFragment();

    /**
     * フラグメントを置き換えます,
     *
     * @param id       置き換え場所のリソース ID
     * @param fragment 置き換えるフラグメント
     */
    protected void replaceFragment(@IdRes final int id, @NonNull final Fragment fragment) {
        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            final String message = "Cannot get FragmentManager";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(id, fragment).commit();
    }

    /**
     * キャプチャー結果をアクティビティに通知します.
     *
     * @param activity アクティビティ
     * @param result   キャップチャー結果
     */
    abstract protected void notifyDocumentResult(@NonNull final TutorialActivity activity, @NonNull final DocumentResult result);

    /**
     * Glide でGIF を表示させます.
     *
     * @param resource GIF ファイルのリソース
     */
    public final void setGifByGlide(@RawRes final int resource) {
        Glide.with(this).load(resource).listener(new RequestListener<Drawable>() {
            /**
             * 画像の読み込み失敗時に呼び出されます.
             */
            @Override
            public boolean onLoadFailed(
                    @Nullable final GlideException e,
                    @NonNull final Object model,
                    @NonNull final Target target,
                    final boolean isFirstResource) {
                return false;
            }

            /**
             * 画像の読み込み成功時に呼び出されます.
             */
            @Override
            public boolean onResourceReady(
                    @NonNull final Drawable resource,
                    @NonNull final Object model,
                    @NonNull final Target<Drawable> target,
                    @NonNull final DataSource dataSource,
                    final boolean isFirstResource) {
                if (!(resource instanceof GifDrawable)) {
                    return false;
                } else {
                    ((GifDrawable) resource).setLoopCount(1);
                    return false;
                }
            }
        }).into((ImageView) getView().findViewById(R.id.tutorial_gif));
    }

    /**
     * 撮影開始前にカメラ権限を確認.
     */
    private void checkCameraPermission() {
        final Activity activity = getActivity();
        if (activity == null) {
            logger.error("Cannot get Activity");
            return;
        }

        final String cameraPermission = Manifest.permission.CAMERA;

        if (ActivityCompat.checkSelfPermission(activity, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{cameraPermission}, PERMISSION_REQUEST_CAMERA);
            startCaptureButton.unlock();
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            startCapture();
        }
    }

    @Override
    public void onPause() {
        // 仕様不明のところが多くて、念のため、ここでボタンとインジケータを初期状態にします.
        startCaptureButton.unlock();
        progressBar.setVisibility(View.INVISIBLE);

        super.onPause();
    }
}
