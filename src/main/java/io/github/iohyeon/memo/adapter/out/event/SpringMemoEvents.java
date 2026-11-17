package io.github.iohyeon.memo.adapter.out.event;

import io.github.iohyeon.memo.application.port.MemoEvents;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** 등록 이벤트를 Spring 애플리케이션 이벤트로 발행한다. 메시지 브로커로 바꿀 때 이 클래스만 교체한다. */
@Component
class SpringMemoEvents implements MemoEvents {

    record MemoRegistered(long memoId, String ownerId) {
    }

    private final ApplicationEventPublisher publisher;

    SpringMemoEvents(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void memoRegistered(long memoId, String ownerId) {
        publisher.publishEvent(new MemoRegistered(memoId, ownerId));
    }
}
