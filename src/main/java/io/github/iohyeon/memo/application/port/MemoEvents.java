package io.github.iohyeon.memo.application.port;

/** 메모 등록 사실을 바깥에 알린다. 호출 자체가 계약이라 테스트에서는 호출을 검증한다. */
public interface MemoEvents {

    void memoRegistered(long memoId, String ownerId);
}
