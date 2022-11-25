/*
 * Copyright (C) 2019 Polarify. All Rights Reserved.
 */
package jp.co.polarify.onboarding.app.utils;

/**
 * 本人確認書類選択種類 Enum クラスです.
 */
public enum DocumentKind {

    /**
     * 運転免許証です.
     */
    DRIVER_LICENSE_CARD,

    /**
     * マイナンバーカードです.
     */
    MY_NUMBER_CARD,

    /**
     * 在留カード・特別永住者証明書です.
     */
    RESIDENCE_CARD;

    /**
     * 本人確認書類種類を取得します.
     *
     * @param index
     * @return 本人確認書類種類
     */
    public static DocumentKind getDocumentKind(int index) {
        for (DocumentKind kind : DocumentKind.values()) {
            if (kind.ordinal() == index) {
                return kind;
            }
        }
        return DocumentKind.DRIVER_LICENSE_CARD;
    }

}
