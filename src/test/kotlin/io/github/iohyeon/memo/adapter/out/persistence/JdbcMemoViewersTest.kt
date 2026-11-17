package io.github.iohyeon.memo.adapter.out.persistence

import io.github.iohyeon.memo.application.port.MemoViewers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlMergeMode

/** 같은 SQL을 실제 DB(H2)에 실행해 돌아온 행을 본다. */
@SpringBootTest
@Sql(scripts = ["/sql/cleanup.sql"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class JdbcMemoViewersTest(@Autowired val viewers: MemoViewers) {

    @Test
    @Sql("/sql/active-memo.sql")
    fun `처음 공유하면 1, 같은 사람에게 다시 공유하면 0을 돌려준다`() {
        assertThat(viewers.add(100, "u2")).isEqualTo(1)
        assertThat(viewers.add(100, "u2")).isEqualTo(0)
    }

    @Test
    @Sql("/sql/shared-memos.sql")
    fun `나에게 공유된 메모 가운데 보관하지 않은 것만 돌려준다`() {
        val memos = viewers.findActiveSharedWith("u2")

        assertThat(memos).extracting<Long> { it.id() }.containsExactly(200L)
    }
}
