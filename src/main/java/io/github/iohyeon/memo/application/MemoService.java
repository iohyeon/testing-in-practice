package io.github.iohyeon.memo.application;

import io.github.iohyeon.memo.application.port.MemoEvents;
import io.github.iohyeon.memo.application.port.MemoRepository;
import io.github.iohyeon.memo.domain.DuplicateMemoException;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoNotFoundException;
import io.github.iohyeon.memo.domain.MemoStatus;
import io.github.iohyeon.memo.domain.MemoTitle;
import io.github.iohyeon.memo.domain.Visibility;
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

    /** 공개 범위를 정하지 않으면 본인만 보는 메모로 등록한다. */
    public long register(String ownerId, String rawTitle) {
        return register(ownerId, rawTitle, Visibility.PRIVATE);
    }

    public long register(String ownerId, String rawTitle, Visibility visibility) {
        MemoTitle title = new MemoTitle(rawTitle);
        if (repository.existsByOwnerAndTitle(ownerId, title.value())) {
            throw new DuplicateMemoException(title.value());
        }
        Memo saved = repository.save(
                new Memo(null, ownerId, title.value(), visibility, MemoStatus.ACTIVE, Instant.now(clock)));
        events.memoRegistered(saved.id(), ownerId);
        return saved.id();
    }

    public void archive(long id) {
        Memo memo = repository.findById(id).orElseThrow(() -> new MemoNotFoundException(id));
        repository.updateStatus(id, memo.archive().status());
    }
}
