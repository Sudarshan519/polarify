package jp.co.polarify.onboarding.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.fragment.IntroductionFirstFragment;
import jp.co.polarify.onboarding.app.fragment.IntroductionSecondFragment;
import jp.co.polarify.onboarding.app.utils.ReturnDestinationConfirmer;

/**
 * イントロダクション画面のレイアウト確認用のアクティビティです.
 */
public final class IntroductionActivity extends AppCompatActivity {
    /**
     * イントロダクションユニークキー.
     */
    public static final String KEY_INTRODUCTION_TYPE = "key_introduction_type";

    /**
     * イントロダクションの順序.
     */
    public enum IntroductionType {
        FIRST,
        SECOND
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        setFragment();
    }

    /**
     * 対象するイントロダクション画面を設定します.
     */
    private void setFragment() {
        final Intent intent = getIntent();
        final IntroductionType type = (IntroductionType) intent.getSerializableExtra(KEY_INTRODUCTION_TYPE);

        final Bundle arguments = new Bundle();

        final Fragment fragment = getFragment(type);
        fragment.setArguments(arguments);

        final FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragmenttransaction.replace(R.id.introduction_container, fragment).commit();

        final ViewStub progressView = findViewById(R.id.progress_layout);
        progressView.setLayoutResource(R.layout.document_progress_view);
        progressView.inflate();
    }

    /**
     * 対象するイントロダクション画面を取得します.
     *
     * @param type イントロダクション画面の種類
     */
    private Fragment getFragment(IntroductionType type) {
        if (type.equals(IntroductionType.FIRST)) {
            return new IntroductionFirstFragment();
        }
        return new IntroductionSecondFragment();
    }

    @Override
    public void onBackPressed() {
        ReturnDestinationConfirmer.execute(this); // ホーム画面へ戻ります
    }

}
