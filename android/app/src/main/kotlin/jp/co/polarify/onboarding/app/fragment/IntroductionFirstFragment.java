package jp.co.polarify.onboarding.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bnpj_polarify_re.R;

import java.io.Serializable;

import jp.co.polarify.onboarding.app.BundleKeyDefinitions;
import jp.co.polarify.onboarding.app.IntroductionActivity;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;

/**
 * イントロダクション画面を生成フラグメントです.
 */
public class IntroductionFirstFragment extends Fragment implements BundleKeyDefinitions {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(IntroductionFirstFragment.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final FrameLayout introductionContainer = container.findViewById(R.id.introduction_container);
        return inflater.inflate(R.layout.document_intoroduction_first, introductionContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 次の書類撮影イントロダクションへ進むボタン
        final Button nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(getContext(), IntroductionActivity.class);
                Intent intentWithData = getActivity().getIntent();

                Serializable result = intentWithData.getSerializableExtra("result");

                intent.putExtra(IntroductionActivity.KEY_INTRODUCTION_TYPE, IntroductionActivity.IntroductionType.SECOND);
                intent.putExtra("result", result);

                logger.debug("send IntroductionActivity start intent");
                startActivity(intent);
                ((IntroductionActivity)getActivity()).finish();
            }
        });
    }
}
