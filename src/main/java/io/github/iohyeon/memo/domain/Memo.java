package io.github.iohyeon.memo.domain;

import java.time.Instant;

/** 저장 전에는 id가 null이고, 저장소가 id를 붙여 돌려준다. */
public record Memo(Long id, String ownerId, String title, Instant createdAt) {

    public Memo withId(long newId) {
        return new Memo(newId, ownerId, title, createdAt);
    }
}
