package io.github.iohyeon.memo.adapter.out.persistence

import io.github.iohyeon.memo.application.port.MemoViewers
import io.github.iohyeon.memo.domain.Memo
import io.github.iohyeon.memo.domain.MemoStatus
import io.github.iohyeon.memo.domain.Visibility
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

/** 테스트가 SQL 문장을 직접 볼 수 있게 상수로 둔다. */
internal object MemoViewerSql {

    /** 이미 있는 (memo_id, viewer_id)면 아무 행도 넣지 않아 0을 돌려준다. */
    const val ADD = """
        INSERT INTO memo_viewer (memo_id, viewer_id)
        SELECT :memoId, :viewerId
        WHERE NOT EXISTS (
            SELECT 1 FROM memo_viewer WHERE memo_id = :memoId AND viewer_id = :viewerId
        )
    """

    const val FIND_ACTIVE_SHARED_WITH = """
        SELECT m.id, m.owner_id, m.title, m.visibility, m.status, m.created_at
        FROM memo m
        JOIN memo_viewer v ON v.memo_id = m.id
        WHERE v.viewer_id = :viewerId
          AND m.status = 'ACTIVE'
        ORDER BY m.id
    """
}

@Repository
internal class JdbcMemoViewers(private val jdbc: JdbcClient) : MemoViewers {

    override fun add(memoId: Long, viewerId: String): Int =
        jdbc.sql(MemoViewerSql.ADD)
            .param("memoId", memoId)
            .param("viewerId", viewerId)
            .update()

    override fun findActiveSharedWith(viewerId: String): List<Memo> =
        jdbc.sql(MemoViewerSql.FIND_ACTIVE_SHARED_WITH)
            .param("viewerId", viewerId)
            .query { rs, _ ->
                Memo(
                    rs.getLong("id"),
                    rs.getString("owner_id"),
                    rs.getString("title"),
                    Visibility.valueOf(rs.getString("visibility")),
                    MemoStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toInstant(),
                )
            }
            .list()
}
