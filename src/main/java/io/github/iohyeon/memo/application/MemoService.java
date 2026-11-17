package io.github.iohyeon.memo.application;

import io.github.iohyeon.memo.application.port.MemoEvents;
import io.github.iohyeon.memo.application.port.MemoRepository;
import io.github.iohyeon.memo.domain.DuplicateMemoException;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoTitle;
import java.time.Clock;
import java.time.Instant;

public class MemoService {

    private final MemoRepository repository;
    private final MemoEvents events;
    private final Clock clock;

    public MemoService(MemoRepository repository, MemoEvents events, Clock clock) {
        this.repository = repository;
        this.events = events;
        this.clock = clock;
    }

    public long register(String ownerId, String rawTitle) {
        MemoTitle title = new MemoTitle(rawTitle);
        if (repository.existsByOwnerAndTitle(ownerId, title.value())) {
            throw new DuplicateMemoException(title.value());
        }
        Memo saved = repository.save(new Memo(null, ownerId, title.value(), Instant.now(clock)));
        events.memoRegistered(saved.id(), ownerId);
        return saved.id();
    }
}
