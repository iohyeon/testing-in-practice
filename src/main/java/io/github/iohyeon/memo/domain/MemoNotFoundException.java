package io.github.iohyeon.memo.domain;

public class MemoNotFoundException extends RuntimeException {

    public MemoNotFoundException(long id) {
        super("메모가 없습니다: " + id);
    }
}
