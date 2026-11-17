package io.github.iohyeon.memo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoTitleTest {

    @Test
    void 공백만_있는_제목은_거부한다() {
        assertThrows(IllegalArgumentException.class, () -> new MemoTitle(" "));
    }

    @Test
    void 정상_제목은_입력한_값을_보관한다() {
        assertEquals("회의", new MemoTitle("회의").value());
    }
}
