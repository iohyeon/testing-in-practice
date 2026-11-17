package io.github.iohyeon.memo.config

import io.github.iohyeon.memo.application.MemoShareService
import io.github.iohyeon.memo.application.port.MemoRepository
import io.github.iohyeon.memo.application.port.MemoViewers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
internal class ShareConfiguration {

    @Bean
    fun memoShareService(memos: MemoRepository, viewers: MemoViewers) = MemoShareService(memos, viewers)
}
