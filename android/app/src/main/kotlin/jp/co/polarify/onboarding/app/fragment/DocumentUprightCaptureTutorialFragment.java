package jp.co.polarify.onboarding.app.fragment;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.example.bnpj_polarify_re.R;


/**
 * 書類正面撮影のチュートリアル画面のフラグメントクラスです.
 */
abstract class DocumentUprightCaptureTutorialFragment extends DocumentCaptureTutorialFragment {
    /**
     * ビューの初期設定を行います.
     *
     * @param root ルートビュー
     */
    @Override
    protected void initializeViews(@NonNull final View root) {
        super.initializeViews(root);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        final ImageView imageView = root.findViewById(R.id.tutorial_gif);
        imageView.setPadding(width / 3, 0, 0, 0);
    }

    /**
     * GIF アニメーションのリソース ID を取得します.
     *
     * @return 取得した GIF アニメーションのリソース ID
     */
    @Override
    @RawRes
    protected int getGifId() {
        return R.raw.drive_license_front_shooting;
    }
}
