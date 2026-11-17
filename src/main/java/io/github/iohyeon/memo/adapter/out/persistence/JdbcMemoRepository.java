package io.github.iohyeon.memo.adapter.out.persistence;

import io.github.iohyeon.memo.application.port.MemoRepository;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoStatus;
import io.github.iohyeon.memo.domain.Visibility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
class JdbcMemoRepository implements MemoRepository {

    private static final String COLUMNS = "id, owner_id, title, visibility, status, created_at";

    private final JdbcClient jdbc;

    JdbcMemoRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean existsByOwnerAndTitle(String ownerId, String title) {
        return jdbc.sql("SELECT COUNT(*) FROM memo WHERE owner_id = :ownerId AND title = :title")
                .param("ownerId", ownerId)
                .param("title", title)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public List<Memo> findByOwner(String ownerId) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM memo WHERE owner_id = :ownerId ORDER BY id")
                .param("ownerId", ownerId)
                .query(JdbcMemoRepository::toMemo)
                .list();
    }

    @Override
    public Optional<Memo> findById(long id) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM memo WHERE id = :id")
                .param("id", id)
                .query(JdbcMemoRepository::toMemo)
                .optional();
    }

    @Override
    public Memo save(Memo memo) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.sql("""
                        INSERT INTO memo (owner_id, title, visibility, status, created_at)
                        VALUES (:ownerId, :title, :visibility, :status, :createdAt)
                        """)
                .param("ownerId", memo.ownerId())
                .param("title", memo.title())
                .param("visibility", memo.visibility().name())
                .param("status", memo.status().name())
                .param("createdAt", Timestamp.from(memo.createdAt()))
                .update(key, "id");
        return memo.withId(key.getKey().longValue());
    }

    @Override
    public void updateStatus(long id, MemoStatus status) {
        jdbc.sql("UPDATE memo SET status = :status WHERE id = :id")
                .param("status", status.name())
                .param("id", id)
                .update();
    }

    private static Memo toMemo(ResultSet rs, int rowNum) throws SQLException {
        return new Memo(
                rs.getLong("id"),
                rs.getString("owner_id"),
                rs.getString("title"),
                Visibility.valueOf(rs.getString("visibility")),
                MemoStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toInstant());
    }
}
