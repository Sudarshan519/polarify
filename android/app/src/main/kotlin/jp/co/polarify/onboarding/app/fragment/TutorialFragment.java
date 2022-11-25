package jp.co.polarify.onboarding.app.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;

/**
 * チュートリアル画面を生成フラグメントです.
 */
abstract class TutorialFragment extends SmartReplaceableFragment {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(TutorialFragment.class);

    /**
     * 照合用 ID.
     */
    private GetMatchingIDResult matchingIDResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = getActivity();
        if (!(activity instanceof TutorialActivity)) {
            logger.error("Cannot get TutorialActivity");
            return;
        }
        final TutorialActivity tutorialActivity = (TutorialActivity) activity;

        matchingIDResult = tutorialActivity.getMatchingIDResult();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    /**
     * Glide でGIF を表示させます.
     *
     * @param resource GIF ファイルのリソース
     */
    public final void setGifByGlide(final int resource) {
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
     * 照合用 ID を取得します。
     *
     * @return 取得した照合用 ID
     */
    @Nullable
    protected GetMatchingIDResult getMatchingIDResult() {
        return matchingIDResult;
    }
}
