package io.github.iohyeon.memo.domain;

public final class MemoTitle {

    private final String value;

    public MemoTitle(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("메모 제목은 비어 있을 수 없습니다.");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }
}
