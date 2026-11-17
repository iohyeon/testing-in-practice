package io.github.iohyeon.memo.application;

import io.github.iohyeon.memo.application.port.MemoRepository;
import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 메모리 리스트에 저장하는 Fake 저장소. 저장 결과를 테스트가 직접 조회한다. */
class InMemoryMemoRepository implements MemoRepository {

    private final List<Memo> memos = new ArrayList<>();
    private long nextId = 1;

    @Override
    public boolean existsByOwnerAndTitle(String ownerId, String title) {
        return memos.stream()
                .anyMatch(m -> m.ownerId().equals(ownerId) && m.title().equals(title));
    }

    @Override
    public List<Memo> findByOwner(String ownerId) {
        return memos.stream()
                .filter(m -> m.ownerId().equals(ownerId))
                .toList();
    }

    @Override
    public Optional<Memo> findById(long id) {
        return memos.stream()
                .filter(m -> m.id() == id)
                .findFirst();
    }

    @Override
    public Memo save(Memo memo) {
        Memo saved = memo.withId(nextId++);
        memos.add(saved);
        return saved;
    }

    @Override
    public void updateStatus(long id, MemoStatus status) {
        memos.replaceAll(m -> m.id() == id
                ? new Memo(m.id(), m.ownerId(), m.title(), m.visibility(), status, m.createdAt())
                : m);
    }
}
