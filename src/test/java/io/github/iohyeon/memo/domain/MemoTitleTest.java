package io.github.iohyeon.memo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoTitleTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\u3000"})
    void 비어_있거나_공백뿐인_제목은_거부한다(String raw) {
        assertThrows(IllegalArgumentException.class, () -> new MemoTitle(raw));
    }

    @ParameterizedTest(name = "{0}자 제목 허용 여부: {1}")
    @CsvSource({
            "1,  true",
            "49, true",
            "50, true",
            "51, false"
    })
    void 길이_경계에서_허용_여부가_갈린다(int length, boolean accepted) {
        String raw = "가".repeat(length);

        if (accepted) {
            assertEquals(raw, new MemoTitle(raw).value());
        } else {
            assertThrows(IllegalArgumentException.class, () -> new MemoTitle(raw));
        }
    }

    @Test
    void 앞뒤_공백은_길이에_세지_않고_저장하지도_않는다() {
        String fifty = "가".repeat(50);

        MemoTitle title = new MemoTitle("  " + fifty + "  ");

        assertEquals(fifty, title.value());
    }

    @Test
    void 이모지는_한_글자로_센다() {
        String fiftyEmoji = "😀".repeat(50);

        assertEquals(fiftyEmoji, new MemoTitle(fiftyEmoji).value());
        assertThrows(IllegalArgumentException.class, () -> new MemoTitle("😀".repeat(51)));
    }
}
