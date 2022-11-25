package jp.co.polarify.onboarding.app.fragment;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;

/**
 * アクティビティが Pause 中にフラグメント切り替えを防ぐためのハンドラ
 */
final class PauseHandler extends Handler {
    /*
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(PauseHandler.class);

    /**
     * メッセージキューのマップ.
     */
    private static final Map<Class<? extends SmartReplaceableFragment>, Deque<Message>> queueMap = new HashMap<>();

    /**
     * 処理対象クラス名.
     */
    private final Class<? extends SmartReplaceableFragment> clazz;

    /**
     * ポーズ中を示すフラグ.
     */
    private boolean paused;

    /**
     * コールバックオブジェクト.
     */
    @NonNull
    private final Callback callback;

    /**
     * コールバックインタフェース.
     */
    interface Callback {
        void processMessage(@NonNull final Message message);
    }

    /**
     * コンストラクタ.
     *
     * @param clazz    クラス名
     * @param callback コールバックオブジェクト
     */
    PauseHandler(@NonNull final Class<? extends SmartReplaceableFragment> clazz, @NonNull final Callback callback) {
        logger.debug("PauseHandler is created.(class = \"{}\")", clazz.toString());
        this.clazz = clazz;
        this.callback = callback;
    }

    /**
     * レジューム状態を通知します.
     */
    void resume() {
        paused = false; // ポーズ中フラグを解除

        final Deque<Message> queue = getQueue();    // キューの取得
        logger.debug("resume is called(Queue entries = {})", queue.size());

        Message message;
        while ((message = queue.pollFirst()) != null) {
            logger.debug("send message from queue");
            sendMessage(message);   // キューに積まれているメッセージの処理
        }
    }

    /**
     * ポーズ状態に設定します.
     */
    void pause() {
        paused = true;
    }

    /**
     * キューに積まれたメッセージをクリアします.
     */
    void clearMessage() {
        getQueue().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void handleMessage(final Message message) {
        if (paused) {
            logger.debug("push message to queue");
            final Message copied = new Message();
            copied.copyFrom(message);
            final Deque<Message> queue = getQueue();
            queue.push(copied); // ポーズ状態が解除されるまでキューに保存
            logger.debug("queue entries = {}", queue.size());
        } else {
            logger.debug("process message");
            callback.processMessage(message);    // ポーズ状態じゃないのでメッセージの処理を行う
        }
    }

    /**
     * キューを取得します.
     *
     * @return 取得したキュー
     */
    @NonNull
    private Deque<Message> getQueue() {
        final Deque<Message> queue = queueMap.get(clazz);   // マップからクラスに対応するキューを取得
        if (queue != null) {    // 取得成功
            return queue;   // 取得したキューを返す
        }
        return createQueue();   // 新たにキューを作成して返す
    }

    /**
     * 新たにキューを生成します.
     *
     * @return 生成したキュー
     */
    @NonNull
    private Deque<Message> createQueue() {
        final Deque<Message> queue = new ArrayDeque<>(); //  新しいキューを作成
        queueMap.put(clazz, queue);  // クラスとキューを紐づけてマップに格納
        return queue;    // 作成したキューを返す
    }
}
