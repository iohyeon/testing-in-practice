package io.github.iohyeon.memo.domain;

import java.time.Instant;

/** 저장 전에는 id가 null이고, 저장소가 id를 붙여 돌려준다. */
public record Memo(Long id, String ownerId, String title, Visibility visibility, MemoStatus status, Instant createdAt) {

    public Memo withId(long newId) {
        return new Memo(newId, ownerId, title, visibility, status, createdAt);
    }

    /** 활성 메모만 보관할 수 있다. 이미 보관된 메모를 다시 보관하면 상태를 바꾸지 않고 거부한다. */
    public Memo archive() {
        if (status == MemoStatus.ARCHIVED) {
            throw new IllegalStateException("이미 보관된 메모입니다.");
        }
        return new Memo(id, ownerId, title, visibility, MemoStatus.ARCHIVED, createdAt);
    }
}
