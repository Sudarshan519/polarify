package jp.co.polarify.onboarding.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//import com.nitv.bnpjcredit.R;

import com.example.bnpj_polarify_re.R;

import io.flutter.plugin.common.MethodChannel;
import jp.co.polarify.onboarding.app.fragment.DocumentCaptureProgressAreaFragment;
import jp.co.polarify.onboarding.app.fragment.DocumentFrontCaptureTutorialFragment;
import jp.co.polarify.onboarding.app.utils.PreferenceManager;
import jp.co.polarify.onboarding.app.utils.ReturnDestinationConfirmer;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.result.DocumentResult;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;

/**
 * チュートリアル画面のレイアウト確認用のアクティビティです.
 */
public class TutorialActivity extends AppCompatActivity implements BundleKeyDefinitions {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(TutorialActivity.class);

    /**
     * 照合用 ID.
     * 起動インテントに含まれています。
     */
    @Nullable
    private GetMatchingIDResult matchingIDResult = null;

    /**
     * 書類表全体画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap frontFullImage = null;

    /**
     * 書類表切り出し画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap frontDocumentImage = null;

    /**
     * 書類表補正後画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap frontCorrectedImage = null;

    /**
     * 書類斜め全体画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap tiltedFullImage = null;

    /**
     * 書類斜め切り出し画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap tiltedDocumentImage = null;

    /**
     * 書類斜め補正後画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap tiltedCorrectedImage = null;

    /**
     * 書類裏全体画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap backFullImage = null;

    /**
     * 書類裏切り出し画像.
     * DocumentFrontCaptureTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap backDocumentImage = null;

    /**
     * セルフィー画像.
     * SelfieTutorialFragment が取得して当アクティビティに設定します。
     */
    @Nullable
    private static Bitmap selfieImage = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        if (savedInstanceState == null) {
            clearImages();
            setFragment();
        } else {
            logger.warn("TutorialActivity is recreated");

            // POLA_ANDROID-940 撮影中カメラ権限変更、また不明原因で画面再起動した場合、トップ画面へ遷移します。
            final ErrorResult errorResult;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                errorResult = ErrorResult.UNAUTHORIZED_CAMERA_PERMISSION_ERROR;
            } else {
                errorResult = ErrorResult.UNEXPECTED_ERROR;
            }
            ReturnDestinationConfirmer.execute(this, errorResult);
        }
    }

    /**
     * 画像イメージをクリアします.
     */
    private void clearImages() {
        recycleBitmap(frontFullImage);
        frontFullImage = null;
        recycleBitmap(frontDocumentImage);
        frontDocumentImage = null;
        recycleBitmap(frontCorrectedImage);
        frontCorrectedImage = null;

        recycleBitmap(tiltedFullImage);
        tiltedFullImage = null;
        recycleBitmap(tiltedDocumentImage);
        tiltedDocumentImage = null;
        recycleBitmap(tiltedCorrectedImage);
        tiltedCorrectedImage = null;

        recycleBitmap(backFullImage);
        backFullImage = null;
        recycleBitmap(backDocumentImage);
        backDocumentImage = null;

        recycleBitmap(selfieImage);
        selfieImage = null;
    }

    /**
     * ビットマップのリサイクルを行います.
     *
     * @param bitmap リサイクル対象のビットマップ
     */
    private void recycleBitmap(@Nullable final Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * フラグメントの設定を行います.
     */
    private void setFragment() {
        final FragmentManager manager = getSupportFragmentManager();
        if (manager == null) {
            final String message = "Cannot get FragmentManager";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final FragmentTransaction transaction = manager.beginTransaction(); // フラグメント切り替えトランザクションの取得

         final Fragment fragment = new DocumentFrontCaptureTutorialFragment();    // 次に表示するフラグメントを取得
//         final Fragment fragment = new SelfieTutorialFragment();
        transaction.replace(R.id.fragment_container, fragment);

        final Fragment progressAreaFragment = new DocumentCaptureProgressAreaFragment();
        transaction.replace(R.id.progress_container, progressAreaFragment);

        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ReturnDestinationConfirmer.execute(this); // ホーム画面へ戻ります
            return false;
        }
        return false;
    }

    /**
     * 書類前撮影フラグメントから撮影結果の通知を行います.
     *
     * @param result 撮影結果
     */
    public void notifyFrontImages(@NonNull final DocumentResult result) {
        if (frontFullImage != null && !frontFullImage.isRecycled()) {
            logger.debug("recycle old frontFullImage");
            frontFullImage.recycle();
        }
        frontFullImage = result.getFullImage(); // 書類前の全体画像

        if (frontDocumentImage != null && !frontDocumentImage.isRecycled()) {
            logger.debug("recycle old frontDocumentImage");
            frontDocumentImage.recycle();
        }
        frontDocumentImage = result.getDocumentImage(); // 書類前の切り出し画像

        if (frontCorrectedImage != null && !frontCorrectedImage.isRecycled()) {
            logger.debug("recycle old frontCorrectedImage");
            frontCorrectedImage.recycle();
        }
        frontCorrectedImage = result.getCorrectedImage();   // 書類前の補正後画像
    }

    /**
     * 書類斜め撮影フラグメントから撮影結果の通知を行います.
     *
     * @param result 撮影結果
     */
    public void notifyTiltedImages(@NonNull final DocumentResult result) {
        if (tiltedFullImage != null && !tiltedFullImage.isRecycled()) {
            logger.debug("recycle old tiltedFullImage");
            tiltedFullImage.recycle();
        }
        tiltedFullImage = result.getFullImage(); // 書類斜めの全体画像

        if (tiltedDocumentImage != null && !tiltedDocumentImage.isRecycled()) {
            logger.debug("recycle old tiltedDocumentImage");
            tiltedDocumentImage.recycle();
        }
        tiltedDocumentImage = result.getDocumentImage(); // 書類斜めの切り出し画像

        if (tiltedCorrectedImage != null && !tiltedCorrectedImage.isRecycled()) {
            logger.debug("recycle old tiltedCorrectedImage");
            tiltedCorrectedImage.recycle();
        }
        tiltedCorrectedImage = result.getCorrectedImage();   // 書類斜めの補正後画像
    }

    /**
     * 書類裏撮影フラグメントから撮影結果の通知を行います.
     *
     * @param result 撮影結果
     */
    public void notifyBackImages(@NonNull final DocumentResult result) {
        if (backFullImage != null && !backFullImage.isRecycled()) {
            logger.debug("recycle old backFullImage");
            backFullImage.recycle();
        }
        backFullImage = result.getFullImage(); // 書類裏の全体画像

        if (backDocumentImage != null && !backDocumentImage.isRecycled()) {
            logger.debug("recycle old backDocumentImage");
            backDocumentImage.recycle();
        }
        backDocumentImage = result.getDocumentImage(); // 書類裏の切り出し画像
    }

    public static void setSelfieImage(@Nullable final Bitmap image) {
        if (selfieImage != null && !selfieImage.isRecycled()) {
            logger.debug("recycle old selfieImage");
            selfieImage.recycle();
        }
        selfieImage = image;
    }

    @Nullable
    public GetMatchingIDResult getMatchingIDResult() {
        if (matchingIDResult == null) {
            matchingIDResult = PreferenceManager.getGetMatchingIDResult(this, APP_KEY_MATCHING_ID_RESULT);
        }
        return matchingIDResult;
    }

    /**
     * 書類表の書類部分を切り出した画像を取得します.
     *
     * @return 取得した書類画像
     */
    @Nullable
    public Bitmap getFrontDocumentImage() {
        return frontDocumentImage;
    }

    /**
     * 書類表の書類部分の補正処理を行った画像を取得します.
     *
     * @return 取得した補正後書類画像
     */
    @Nullable
    public Bitmap getFrontCorrectedImage() {
        return frontCorrectedImage;
    }

    /**
     * 書類表の書類部分を切り出した画像を取得します.
     *
     * @return 取得した書類画像
     */
    @Nullable
    public Bitmap getTiltedDocumentImage() {
        return tiltedDocumentImage;
    }

    /**
     * 書類斜めの書類部分の補正処理を行った画像を取得します.
     *
     * @return 取得した補正後書類画像
     */
    @Nullable
    public Bitmap getTiltedCorrectedImage() {
        return tiltedCorrectedImage;
    }

    /**
     * 書類裏の書類部分を切り出した画像を取得します.
     *
     * @return 取得した書類画像
     */
    @Nullable
    public Bitmap getBackDocumentImage() {
        return backDocumentImage;
    }

    /**
     * セルフィー画像を取得します.
     *
     * @return 取得したセルフィー画像
     */
    @Nullable
    public Bitmap getSelfieImage() {
        return selfieImage;
    }


    /**
    * get Intent result
    * */
    @Nullable
    public MethodChannel.Result  getResult(){
        Intent intent = getIntent();
        MethodChannel.Result result = (MethodChannel.Result) intent.getSerializableExtra("result");
        return result;
    }

}
