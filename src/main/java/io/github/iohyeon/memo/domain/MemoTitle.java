package io.github.iohyeon.memo.domain;

/**
 * 메모 제목. 앞뒤 공백(유니코드 공백 포함)을 지운 뒤 1~50자만 허용한다.
 * 글자 수는 유니코드 코드 포인트로 센다. 이모지 하나는 한 글자다.
 */
public final class MemoTitle {

    private static final int MAX_LENGTH = 50;

    private final String value;

    public MemoTitle(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("메모 제목은 비어 있을 수 없습니다.");
        }
        String stripped = raw.strip();
        if (stripped.isEmpty()) {
            throw new IllegalArgumentException("메모 제목은 비어 있을 수 없습니다.");
        }
        int length = stripped.codePointCount(0, stripped.length());
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException("메모 제목은 50자 이하여야 합니다.");
        }
        this.value = stripped;
    }

    public String value() {
        return value;
    }
}
