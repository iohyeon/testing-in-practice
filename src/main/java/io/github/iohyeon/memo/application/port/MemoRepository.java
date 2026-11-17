package io.github.iohyeon.memo.application.port;

import io.github.iohyeon.memo.domain.Memo;
import java.util.List;

public interface MemoRepository {

    boolean existsByOwnerAndTitle(String ownerId, String title);

    List<Memo> findByOwner(String ownerId);

    Memo save(Memo memo);
}
