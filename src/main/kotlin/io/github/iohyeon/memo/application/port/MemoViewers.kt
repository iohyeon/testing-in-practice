package io.github.iohyeon.memo.application.port

import io.github.iohyeon.memo.domain.Memo

/** 메모를 읽을 수 있는 다른 사용자 목록. */
interface MemoViewers {

    /** 읽기 권한을 추가하고 새로 추가한 행 수를 돌려준다. 이미 있으면 0이다. */
    fun add(memoId: Long, viewerId: String): Int

    /** viewerId에게 공유된 메모 가운데 보관하지 않은 것만 id 순으로 돌려준다. */
    fun findActiveSharedWith(viewerId: String): List<Memo>
}
