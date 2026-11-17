package io.github.iohyeon.memo.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoArchiveApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    @Sql("/sql/active-memo.sql")
    void 활성_메모를_보관하면_200이고_상태가_바뀐다() throws Exception {
        mvc.perform(patch("/memos/100/archive")).andExpect(status().isOk());

        assertEquals("ARCHIVED", statusOf(100));
    }

    @Test
    @Sql("/sql/archived-memo.sql")
    void 보관된_메모를_다시_보관하면_409이고_상태는_그대로다() throws Exception {
        mvc.perform(patch("/memos/100/archive")).andExpect(status().isConflict());

        assertEquals("ARCHIVED", statusOf(100));
    }

    private String statusOf(long id) {
        return jdbc.queryForObject("SELECT status FROM memo WHERE id = ?", String.class, id);
    }
}
