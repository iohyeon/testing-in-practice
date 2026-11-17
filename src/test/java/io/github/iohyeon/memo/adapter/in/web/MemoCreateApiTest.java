package io.github.iohyeon.memo.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoCreateApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void 올바른_요청은_201과_id를_돌려준다() throws Exception {
        mvc.perform(post("/memos").contentType(MediaType.APPLICATION_JSON).content(json("valid.json")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void 정의되지_않은_공개_범위는_400이다() throws Exception {
        mvc.perform(post("/memos").contentType(MediaType.APPLICATION_JSON).content(json("unknown-visibility.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 공백뿐인_제목은_400이다() throws Exception {
        mvc.perform(post("/memos").contentType(MediaType.APPLICATION_JSON).content(json("blank-title.json")))
                .andExpect(status().isBadRequest());
    }

    private static String json(String name) throws Exception {
        return new ClassPathResource("memo-requests/" + name).getContentAsString(StandardCharsets.UTF_8);
    }
}
