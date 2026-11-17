package io.github.iohyeon.memo.domain;

public class DuplicateMemoException extends RuntimeException {

    public DuplicateMemoException(String title) {
        super("같은 제목의 메모가 이미 있습니다: " + title);
    }
}
