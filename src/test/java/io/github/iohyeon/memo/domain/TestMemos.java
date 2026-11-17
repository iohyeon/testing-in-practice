package io.github.iohyeon.memo.domain;

import java.time.Instant;

/** 테스트 결과에 영향을 주지 않는 값만 기본값으로 채우는 공용 생성 함수. */
public final class TestMemos {

    private static final Instant CREATED_AT = Instant.parse("2026-11-17T09:00:00Z");

    private TestMemos() {
    }

    public static Memo memo() {
        return memo(MemoStatus.ACTIVE);
    }

    public static Memo memo(MemoStatus status) {
        return new Memo(1L, "u1", "회의록", Visibility.PRIVATE, status, CREATED_AT);
    }
}
