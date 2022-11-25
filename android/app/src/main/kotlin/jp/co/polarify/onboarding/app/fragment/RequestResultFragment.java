package jp.co.polarify.onboarding.app.fragment;

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


//import jp.co.polarify.onboarding.app.MainActivity;


/**
 * 撮影した画像の申し込み結果画面を生成フラグメントです.
 */
public class RequestResultFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final FrameLayout requestContainer = container.findViewById(R.id.fragment_container);
        return inflater.inflate(R.layout.fragment_request_result, requestContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 閉じるボタン
        final Button closeButton = view.findViewById(R.id.close_button);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public final void onClick(View v) {
//                onCloseClick();
//            }
//        });
    }

    /**
     * 「閉じる」ボタンタップ処理.
     */
    private void onCloseClick() {
//        final Intent intent = new Intent(getContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }
}