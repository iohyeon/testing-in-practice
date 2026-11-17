package io.github.iohyeon.memo.application

import io.github.iohyeon.memo.application.port.MemoRepository
import io.github.iohyeon.memo.application.port.MemoViewers
import io.github.iohyeon.memo.domain.AlreadySharedException
import io.github.iohyeon.memo.domain.MemoAccessDeniedException
import io.github.iohyeon.memo.domain.MemoNotFoundException

class MemoShareService(
    private val memos: MemoRepository,
    private val viewers: MemoViewers,
) {

    /** 메모 주인만 다른 사용자에게 읽기 권한을 줄 수 있다. */
    fun share(memoId: Long, requesterId: String, viewerId: String) {
        val memo = memos.findById(memoId).orElseThrow { MemoNotFoundException(memoId) }
        if (memo.ownerId() != requesterId) {
            throw MemoAccessDeniedException(memoId, requesterId)
        }
        if (viewers.add(memoId, viewerId) == 0) {
            throw AlreadySharedException(memoId, viewerId)
        }
    }
}
