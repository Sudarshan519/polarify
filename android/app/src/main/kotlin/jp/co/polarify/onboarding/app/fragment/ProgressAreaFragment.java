package jp.co.polarify.onboarding.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;

/**
 * プログレスエリアの抽象フラグメントです.
 */
abstract class ProgressAreaFragment extends Fragment {
    /**
     * ロガーオブジェクト.
     */
    private Logger logger = LoggerFactory.getLogger(ProgressAreaFragment.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            logger.error("container is null");
            return null;
        }

        final FrameLayout progressContainer = container.findViewById(R.id.progress_container);
        @LayoutRes final int id = getLayoutId();
        return inflater.inflate(id, progressContainer, false);
    }

    /**
     * フラグメントレイアウトのリソース ID を取得します.
     *
     * @return 取得したレイアウト ID
     */
    @LayoutRes
    abstract protected int getLayoutId();
}
