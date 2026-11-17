package io.github.iohyeon.memo.domain

class AlreadySharedException(memoId: Long, viewerId: String) :
    RuntimeException("메모 $memoId 는 이미 $viewerId 에게 공유되어 있습니다.")
