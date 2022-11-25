package jp.co.polarify.onboarding.app.fragment;

import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * アクティビティの状態に依存せず切り替えられるフラグメントの抽象基底クラスです.
 * アクティビティがポーズ中にフラグメント切り替えを実行しないようにメッセージをキューイングします。
 */
abstract class SmartReplaceableFragment extends Fragment {
    /**
     * ポーズ中にメッセージをキューイングするハンドラ.
     */
    private final PauseHandler handler = new PauseHandler(getClass(), new PauseHandler.Callback() {
        @Override
        public void processMessage(@NonNull final Message message) {
            SmartReplaceableFragment.this.processMessage(message);
        }
    });

    @Override
    public void onPause() {
        super.onPause();
        handler.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.resume();
    }

    /**
     * メッセージをキューイングします.
     *
     * @param what メッセージ ID
     */
    protected void sendMessage(final int what) {
        final Message message = handler.obtainMessage(what);
        handler.sendMessage(message);
    }

    /**
     * ポーズ状態から解除されたとき、キューイングされていた個々のメッセージの処理を行います.
     *
     * @param message 処理対象メッセージ
     */
    abstract protected void processMessage(@NonNull final Message message);

    /**
     * ハンドラに送られたメッセージを削除します.
     */
    void clearMessage() {
        handler.clearMessage();
    }
}
