package io.github.iohyeon.memo.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.iohyeon.memo.application.port.MemoEvents;
import io.github.iohyeon.memo.domain.DuplicateMemoException;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoStatus;
import io.github.iohyeon.memo.domain.Visibility;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoServiceFakeTest {

    private static final Instant NOW = Instant.parse("2026-11-16T09:00:00Z");

    private InMemoryMemoRepository repository;
    private MemoEvents events;
    private MemoService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMemoRepository();
        events = mock(MemoEvents.class);
        service = new MemoService(repository, events, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void 등록하면_정리한_제목과_현재_시각으로_한_건_저장된다() {
        long id = service.register("u1", "  회의록  ");

        assertEquals(List.of(new Memo(id, "u1", "회의록", Visibility.PRIVATE, MemoStatus.ACTIVE, NOW)), repository.findByOwner("u1"));
    }

    @Test
    void 같은_사용자의_같은_제목은_거부하고_더_저장하지_않는다() {
        service.register("u1", "회의록");

        assertThrows(DuplicateMemoException.class, () -> service.register("u1", "회의록"));
        assertEquals(1, repository.findByOwner("u1").size());
    }

    @Test
    void 다른_사용자는_같은_제목을_쓸_수_있다() {
        service.register("u1", "회의록");

        service.register("u2", "회의록");

        assertEquals(1, repository.findByOwner("u2").size());
    }

    @Test
    void 등록하면_등록_이벤트를_보낸다() {
        long id = service.register("u1", "회의록");

        verify(events).memoRegistered(id, "u1");
    }

    @Test
    void 제목이_잘못되면_저장도_이벤트도_없다() {
        assertThrows(IllegalArgumentException.class, () -> service.register("u1", " "));

        assertEquals(List.of(), repository.findByOwner("u1"));
        verifyNoInteractions(events);
    }
}
