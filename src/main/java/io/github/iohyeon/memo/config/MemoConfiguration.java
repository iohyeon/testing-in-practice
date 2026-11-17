package io.github.iohyeon.memo.config;

import io.github.iohyeon.memo.application.MemoService;
import io.github.iohyeon.memo.application.port.MemoEvents;
import io.github.iohyeon.memo.application.port.MemoRepository;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 도메인과 유스케이스는 Spring에 의존하지 않고, 조립만 여기서 한다. */
@Configuration(proxyBeanMethods = false)
class MemoConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    MemoService memoService(MemoRepository memoRepository, MemoEvents memoEvents, Clock clock) {
        return new MemoService(memoRepository, memoEvents, clock);
    }
}
