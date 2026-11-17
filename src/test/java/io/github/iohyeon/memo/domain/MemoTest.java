package io.github.iohyeon.memo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoTest {

    @Test
    void 활성_메모를_보관하면_보관_상태가_된다() {
        Memo archived = TestMemos.memo(MemoStatus.ACTIVE).archive();

        assertEquals(MemoStatus.ARCHIVED, archived.status());
    }

    @Test
    void 보관된_메모는_다시_보관할_수_없다() {
        Memo memo = TestMemos.memo(MemoStatus.ARCHIVED);

        assertThrows(IllegalStateException.class, memo::archive);
    }

    @Test
    void 기본값에_기대는_테스트_보관하면_보관_상태가_된다() {
        Memo archived = TestMemos.memo().archive();   // 기본 상태가 ACTIVE라는 사실에 기댄다

        assertEquals(MemoStatus.ARCHIVED, archived.status());
    }
}
