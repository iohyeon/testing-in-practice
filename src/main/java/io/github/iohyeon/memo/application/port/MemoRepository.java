package io.github.iohyeon.memo.application.port;

import io.github.iohyeon.memo.domain.Memo;
import io.github.iohyeon.memo.domain.MemoStatus;
import java.util.List;
import java.util.Optional;

public interface MemoRepository {

    boolean existsByOwnerAndTitle(String ownerId, String title);

    List<Memo> findByOwner(String ownerId);

    Optional<Memo> findById(long id);

    /** 새 메모를 저장하고 id를 붙여 돌려준다. */
    Memo save(Memo memo);

    void updateStatus(long id, MemoStatus status);
}
