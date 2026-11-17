package io.github.iohyeon.memo.application

import io.github.iohyeon.memo.application.port.MemoRepository
import io.github.iohyeon.memo.application.port.MemoViewers
import io.github.iohyeon.memo.domain.AlreadySharedException
import io.github.iohyeon.memo.domain.Memo
import io.github.iohyeon.memo.domain.MemoAccessDeniedException
import io.github.iohyeon.memo.domain.MemoNotFoundException
import io.github.iohyeon.memo.domain.MemoStatus
import io.github.iohyeon.memo.domain.Visibility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class MemoShareServiceTest {

    @Mock
    lateinit var memos: MemoRepository

    @Mock
    lateinit var viewers: MemoViewers

    lateinit var service: MemoShareService

    @BeforeEach
    fun setUp() {
        service = MemoShareService(memos, viewers)
    }

    @Test
    fun `주인이 아니면 공유할 수 없다`() {
        `when`(memos.findById(MEMO_ID)).thenReturn(Optional.of(memoOwnedBy("u1")))
        // 가드가 없을 때 추가가 성공하도록 반대 결과를 깔아 둔다. 그래야 거부의 이유가 가드 하나로 좁혀진다.
        // 가드가 동작하면 이 stub은 쓰이지 않으므로 엄격 모드의 미사용 stub 검사에서 뺀다.
        lenient().`when`(viewers.add(MEMO_ID, "u3")).thenReturn(1)

        assertThrows<MemoAccessDeniedException> { service.share(MEMO_ID, "u2", "u3") }
    }

    @Test
    fun `주인은 다른 사용자에게 공유할 수 있다`() {
        `when`(memos.findById(MEMO_ID)).thenReturn(Optional.of(memoOwnedBy("u1")))
        `when`(viewers.add(MEMO_ID, "u3")).thenReturn(1)

        assertDoesNotThrow { service.share(MEMO_ID, "u1", "u3") }
        verify(viewers).add(MEMO_ID, "u3")
    }

    @Test
    fun `이미 공유한 사용자에게 다시 공유하면 거부한다`() {
        `when`(memos.findById(MEMO_ID)).thenReturn(Optional.of(memoOwnedBy("u1")))
        `when`(viewers.add(MEMO_ID, "u3")).thenReturn(0)

        assertThrows<AlreadySharedException> { service.share(MEMO_ID, "u1", "u3") }
    }

    @Test
    fun `없는 메모는 공유할 수 없다`() {
        `when`(memos.findById(MEMO_ID)).thenReturn(Optional.empty())

        assertThrows<MemoNotFoundException> { service.share(MEMO_ID, "u1", "u3") }
    }

    private fun memoOwnedBy(ownerId: String) =
        Memo(MEMO_ID, ownerId, "회의록", Visibility.PRIVATE, MemoStatus.ACTIVE, Instant.parse("2026-11-16T09:00:00Z"))

    companion object {
        const val MEMO_ID = 100L
    }
}
