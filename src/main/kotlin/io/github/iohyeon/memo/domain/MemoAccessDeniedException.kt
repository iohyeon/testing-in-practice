package io.github.iohyeon.memo.domain

/** 메모 주인만 할 수 있는 일을 다른 사용자가 요청했다. */
class MemoAccessDeniedException(memoId: Long, requesterId: String) :
    RuntimeException("메모 $memoId 에 대한 권한이 없습니다: $requesterId")
