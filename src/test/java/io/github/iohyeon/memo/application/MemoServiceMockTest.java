package io.github.iohyeon.memo.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.iohyeon.memo.application.port.MemoEvents;
import io.github.iohyeon.memo.application.port.MemoRepository;
import io.github.iohyeon.memo.domain.DuplicateMemoException;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoStatus;
import io.github.iohyeon.memo.domain.Visibility;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoServiceMockTest {

    private static final Instant NOW = Instant.parse("2026-11-16T09:00:00Z");

    @Mock
    private MemoRepository repository;
    @Mock
    private MemoEvents events;
    private MemoService service;

    @BeforeEach
    void setUp() {
        service = new MemoService(repository, events, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void 등록하면_정리한_제목과_현재_시각으로_save를_한_번_호출한다() {
        when(repository.existsByOwnerAndTitle("u1", "회의록")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.<Memo>getArgument(0).withId(1L));

        long id = service.register("u1", "  회의록  ");

        assertEquals(1L, id);
        verify(repository, times(1)).save(new Memo(null, "u1", "회의록", Visibility.PRIVATE, MemoStatus.ACTIVE, NOW));
        verify(events).memoRegistered(1L, "u1");
    }

    @Test
    void 같은_제목이_있다고_답하면_save와_이벤트를_호출하지_않는다() {
        when(repository.existsByOwnerAndTitle("u1", "회의록")).thenReturn(true);

        assertThrows(DuplicateMemoException.class, () -> service.register("u1", "회의록"));

        verify(repository, never()).save(any());
        verifyNoInteractions(events);
    }

    @Test
    void 저장소가_실패하면_이벤트를_보내지_않는다() {
        when(repository.existsByOwnerAndTitle("u1", "회의록")).thenReturn(false);
        when(repository.save(any())).thenThrow(new IllegalStateException("저장 실패"));

        assertThrows(IllegalStateException.class, () -> service.register("u1", "회의록"));

        verifyNoInteractions(events);
    }
}
