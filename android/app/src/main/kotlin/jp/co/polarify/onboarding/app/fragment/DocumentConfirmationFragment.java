package jp.co.polarify.onboarding.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.BundleKeyDefinitions;
import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.app.utils.PreferenceManager;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.common.DocumentType;
import jp.co.polarify.onboarding.sdk.types.internal.DocumentSides;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;
import jp.co.polarify.onboarding.sdk.view.SafetyButton;

/**
 * 書類の撮影確認を行うフラグメントです.
 */
abstract class DocumentConfirmationFragment extends Fragment {
    /**
     * ロガーオブジェクト.
     */
    private Logger logger = LoggerFactory.getLogger(DocumentConfirmationFragment.class);

    /**
     * 指定タスクを実行前の遅延.
     */
    private static final long DELAY_BEFORE_TASK_TIME = 350L;

    /**
     * カメラビューのサイズ.
     */
    private Size cameraSize;

    /**
     * 顔切り出しオブジェクト.
     */
    @Nullable
    private FaceCropper faceCropper;

    /**
     * 登録を始めるボタンです.
     */
    private SafetyButton nextButton = null;

    /**
     * 次へ進む時ネット通信Progressです.
     */
    private ProgressBar progressBar = null;

    /**
     * 警告メッセージのビルダー.
     */
    @NonNull
    protected final StringBuilder builder = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (container == null) {
            logger.error("container is null");
            return null;
        }

        faceCropper = createFaceCropper();  // 顔切り出しオブジェクトの生成

        final FrameLayout tutorialContainer = container.findViewById(R.id.fragment_container);
        return inflater.inflate(R.layout.document_confirmation_preview, tutorialContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showConfirmationPreview(view);

        assignFailPattern(view);    // 失敗例の設定

        final View confirmationPreview = view.findViewById(R.id.confirmation_preview);
        confirmationPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cameraSize = new Size(confirmationPreview.getWidth(), confirmationPreview.getHeight());
                confirmationPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this); // ViewTreeObserver は毎回取得しないといけない
            }
        });

        progressBar = view.findViewById((R.id.waitting_progress_bar_id));

        // 次へ進むボタン
        nextButton = view.findViewById(R.id.next_button_id);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);

                final FaceCropper cropper = getFaceCropper();
                cropper.execute();
            }
        }, false);

        // 撮り直しボタン
        final Button retakeButton = view.findViewById(R.id.retake_button_id);
        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replacePreviousFragment();   // 前のフラグメントに戻る
            }
        });
    }

    /**
     * 失敗例の設定を行います.
     *
     * @param view ルートビュー
     */
    protected void assignFailPattern(@NonNull final View view) {
        final ImageView failPattern = view.findViewById(R.id.fail_pattern_id);
        @DrawableRes final int id = getFailPatternImageId();
        failPattern.setImageResource(id);
    }

    /**
     * 確認画面を表示します.
     *
     * @param root ルートビュー
     */
    private void showConfirmationPreview(@NonNull final View root) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                final ImageView imageView = root.findViewById(R.id.captured_photo);
                final Bitmap image = getDocumentImage();
                imageView.setImageBitmap(image);
            }
        }, DELAY_BEFORE_TASK_TIME);

        // 確認画面を左側からスライドさせます
        final Context context = getContext();
        final Animation slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        final ConstraintLayout constraintLayout = root.findViewById(R.id.confirmation_preview);
        constraintLayout.startAnimation(slideLeft);
    }

    /**
     * 撮影を行う書類の向きを取得します.
     *
     * @return 取得した撮影を行う書類の向き
     */
    abstract protected DocumentSides getDocumentSides(@NonNull final DocumentKind documentKind);

    /**
     * DocumentType を取得します.
     *
     * @return 取得した DocumentType
     */
    protected DocumentType getDocumentType() {
        final DocumentKind documentKind = DocumentKind.getDocumentKind(PreferenceManager.getInt(getContext(), BundleKeyDefinitions.APP_KEY_CAPTURE_KIND));
        return DocumentSides.toDocumentType(getDocumentSides(documentKind));
    }

    /**
     * 前のフラグメントに戻ります.
     */
    protected void replacePreviousFragment() {
        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            final String message = "Cannot get FragmentManager";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final Fragment fragment = getPreviousFragment();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commit();
    }

    /**
     * 失敗例の画像イメージのリソース ID を取得します.
     *
     * @return 取得した画像イメージリソース ID
     */
    @DrawableRes
    abstract protected int getFailPatternImageId();

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

        final Fragment progressAreaFragment = getNextProgressAreaFragment();
        if (progressAreaFragment != null) {
            transaction.replace(R.id.progress_container, progressAreaFragment);
        }

        transaction.commit();
    }

    /**
     * 次に表示するフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    abstract protected Fragment getNextFragment();

    /**
     * プログレスエリアのフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @Nullable
    protected Fragment getNextProgressAreaFragment() {
        return null;
    }

    /**
     * 前のフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    abstract protected Fragment getPreviousFragment();

    @Nullable
    private Bitmap getDocumentImage() {
        return getImage(new ImageGetter() {
            @Override
            public Bitmap execute(@NonNull TutorialActivity activity) {
                return getDocumentImage(activity);
            }
        });
    }

    /**
     * アクティビティから書類画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得した書類画像
     */
    abstract protected Bitmap getDocumentImage(@NonNull final TutorialActivity activity);

    @Nullable
    protected Bitmap getCorrectedImage() {
        return getImage(new ImageGetter() {
            @Override
            public Bitmap execute(@NonNull final TutorialActivity activity) {
                return getCorrectedImage(activity);
            }
        });
    }

    /**
     * アクティビティから補正済み書類画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得した補正済み書類画像
     */
    abstract protected Bitmap getCorrectedImage(@NonNull final TutorialActivity activity);

    private interface ImageGetter {
        Bitmap execute(@NonNull final TutorialActivity activity);
    }

    @Nullable
    private Bitmap getImage(@NonNull final ImageGetter getter) {
        final TutorialActivity activity = getTutorialActivity();
        if (activity == null) {
            return null;
        }
        return getter.execute(activity);
    }

    @Nullable
    protected TutorialActivity getTutorialActivity() {
        final Activity activity = getActivity();
        if (!(activity instanceof TutorialActivity)) {
            logger.error("Cannot get Activity");
            return null;
        }
        return (TutorialActivity) activity;
    }

    @Nullable
    protected GetMatchingIDResult getMatchingIdResult() {
        final TutorialActivity activity = getTutorialActivity();
        if (activity == null) {
            return null;
        }
        return activity.getMatchingIDResult();
    }

    /**
     * 顔切り出しオブジェクトを生成します.
     *
     * @return 生成した顔切り出しオブジェクト
     */
    abstract protected FaceCropper createFaceCropper();

    /**
     * 顔切り出しオブジェクトを取得します.
     *
     * @return 取得した顔切り出しオブジェクト
     */
    @Nullable
    protected FaceCropper getFaceCropper() {
        return faceCropper;
    }


    /**
     * 書類撮影開始要求が成功.
     */
    protected void registerFaceOfDocumentOnSuccess() {
        nextButton.unlock();
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * 書類撮影開始要求が失敗.
     */
    protected void registerFaceOfDocumentOnError() {
        nextButton.unlock();
        progressBar.setVisibility(View.INVISIBLE);
    }
}
