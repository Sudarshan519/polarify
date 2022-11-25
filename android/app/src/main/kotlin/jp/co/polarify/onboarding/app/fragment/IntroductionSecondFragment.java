package jp.co.polarify.onboarding.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bnpj_polarify_re.R;

import java.io.Serializable;

import jp.co.polarify.onboarding.app.BundleKeyDefinitions;
import jp.co.polarify.onboarding.app.IntroductionActivity;
import jp.co.polarify.onboarding.app.TutorialActivity;

/**
 * イントロダクション画面を生成フラグメントです.
 */
public class IntroductionSecondFragment extends Fragment implements BundleKeyDefinitions {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final FrameLayout introductionContainer = container.findViewById(R.id.introduction_container);
        return inflater.inflate(R.layout.document_intoroduction_second, introductionContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int gifMovie = R.raw.drive_license_random;
        final ImageView imageView = view.findViewById(R.id.introduction_gif);
        Glide.with(this).load(gifMovie).into(imageView);

        // 表面の撮影をするボタン
        final Button nextButton = view.findViewById(R.id.start_capture_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onStartCaptureButtonClick();
            }
        });
    }

    /**
     * 「まずは表面の撮影をする」ボタンタップ処理.
     */
    private void onStartCaptureButtonClick() {
        final Intent intent = new Intent(getContext(), TutorialActivity.class);
        Intent intentWithData = getActivity().getIntent();
        Serializable result = intentWithData.getSerializableExtra("result");
        intent.putExtra("result", result);
        startActivity(intent);
        ((IntroductionActivity)getActivity()).finish();
    }
}
