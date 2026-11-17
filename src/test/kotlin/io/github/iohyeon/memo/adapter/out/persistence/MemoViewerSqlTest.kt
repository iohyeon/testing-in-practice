package io.github.iohyeon.memo.adapter.out.persistence

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/** SQL 문장만 본다. 문장에 무엇이 들어 있는지는 확인하지만, DB가 돌려주는 행은 확인하지 못한다. */
class MemoViewerSqlTest {

    @Test
    fun `공유 목록 조회는 공유 대상과 활성 상태로 거른다`() {
        assertThat(MemoViewerSql.FIND_ACTIVE_SHARED_WITH)
            .contains("v.viewer_id = :viewerId")
            .contains("m.status = 'ACTIVE'")
    }

    @Test
    fun `공유 목록 조회는 메모 테이블 전체를 읽는 하위 쿼리를 쓰지 않는다`() {
        assertThat(MemoViewerSql.FIND_ACTIVE_SHARED_WITH).doesNotContainIgnoringCase("SELECT * FROM memo")
    }
}
