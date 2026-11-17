# testing-in-practice

테스트 코드 연재의 예제 저장소입니다. 메모 애플리케이션 하나를 편마다 키우면서, 테스트가 무엇을 확인하고 무엇을 놓치는지를 구현을 일부러 틀리게 바꿔 보며 확인합니다.

각 편의 코드는 `part-N` 태그에 그 시점 그대로 남아 있고, `main`은 마지막 편까지 반영한 상태입니다.

## 연재와 태그

| 편 | 글 | 태그 | 이 편에서 추가한 것 |
|---|---|---|---|
| 1 | [테스트가 통과하면 어디까지 믿어도 될까?](https://develop-tracking.tistory.com/entry/테스트가-통과하면-어디까지-믿어도-될까) | `part-1` | 비어 있는 제목 거부, 변경 실험 스크립트 |
| 2 | [정상, 예외, 경계를 테스트 케이스로 바꾸기 전에 정해야 하는 것](https://develop-tracking.tistory.com/entry/정상-예외-경계를-테스트-케이스로-바꾸기-전에-정해야-하는-것) | `part-2` | 제목 길이 50자 제한(코드 포인트 기준), 경계 테스트 |
| 3 | [Mock을 언제 쓰고 무엇을 검증할까](https://develop-tracking.tistory.com/entry/Mock을-언제-쓰고-무엇을-검증할까-같은-기능을-Fake와-Mock으로-테스트해-보기) | `part-3` | 메모 등록 유스케이스, Fake 저장소와 Mock 저장소 비교 |
| 4 | [테스트 준비 코드가 커질 때: 객체, JSON, SQL 중 무엇으로 준비할까](https://develop-tracking.tistory.com/entry/테스트-준비-코드가-커질-때-객체-JSON-SQL-중-무엇으로-준비할까) | `part-4` | 메모 보관, HTTP API, JDBC 저장소, JSON과 SQL로 준비하는 테스트 |

## 실행

JDK 21이 필요합니다. Gradle은 wrapper가 내려받습니다.

```bash
./gradlew test          # 테스트 27건
scripts/mutate.sh       # 글에 적은 구현 변경 실험 9개를 다시 실행
```

특정 편의 상태로 돌아가려면 태그를 체크아웃합니다.

```bash
git checkout part-3
./gradlew test
scripts/mutate.sh mutations/part-3
```

## 구현 변경 실험

글마다 "구현을 일부러 틀리게 바꿨을 때 어느 테스트가 실패하는가"를 근거로 삼았습니다. 그 변경을 `mutations/<편>/<이름>.patch`로, 기대하는 실패 목록을 같은 이름의 `.expected`로 남겼습니다. `scripts/mutate.sh`는 작업 트리 복사본에 patch를 적용해 테스트를 실행하고, 실패 목록이 기대와 다르면 종료 코드 1을 돌려줍니다. CI도 같은 스크립트를 실행합니다.

| patch | 바꾸는 것 | `main`에서 실패하는 테스트 |
|---|---|---|
| `part-2/upper-bound-uses-gte` | 길이 상한 비교 `>`를 `>=`로 | 50자를 넣는 테스트 3건 |
| `part-2/length-counts-utf16-units` | `codePointCount`를 `length()`로 | 이모지 테스트 1건 |
| `part-2/strip-replaced-by-trim` | `strip()`을 `trim()`으로 | 전각 공백 입력 2건(도메인 1, API 1) |
| `part-3/refactor-duplicate-check-via-findByOwner` | 중복 확인 방법만 바꾼 리팩터링 | Mock 저장소 테스트 3건, Fake 저장소 테스트 0건 |
| `part-3/event-not-published` | 등록 이벤트 발송 누락 | 양쪽 각 1건 |
| `part-3/save-called-twice` | `save` 두 번 호출 | Fake 저장소 테스트 3건, Mock 저장소 테스트 0건 |
| `part-3/event-published-before-save` | 저장 전에 이벤트 발송 | Fake 1건, Mock 2건 |
| `part-4/shared-fixture-default-archived` | 공용 생성 함수의 기본 상태를 `ARCHIVED`로 | 기본값에 기대는 테스트 1건 |
| `part-4/sql-merge-mode-removed` | `@SqlMergeMode(MERGE)` 제거 | 정리 스크립트가 실행되지 않아 기본 키 위반 1건 |

1편 patch는 2편에서 대상 코드가 바뀌어 `main`에서 지웠습니다. `part-1` 태그에서 실행할 수 있습니다. 2편 `strip-replaced-by-trim`은 2편 시점에 1건이 실패했고, 4편에서 같은 입력을 보내는 API 테스트가 생겨 `main`에서는 2건이 실패합니다.

## 구조

```
src/main/java/io/github/iohyeon/memo
├─ domain                     MemoTitle, Memo, 상태와 공개 범위, 도메인 예외. 프레임워크 의존 없음
├─ application                MemoService(등록, 보관)
│  └─ port                    MemoRepository, MemoEvents. 유스케이스가 바깥에 요구하는 인터페이스
├─ adapter
│  ├─ in/web                  MemoController, 예외를 ProblemDetail로 옮기는 ApiExceptionHandler
│  └─ out
│     ├─ persistence          JdbcClient 기반 JdbcMemoRepository
│     └─ event                Spring 애플리케이션 이벤트로 발행하는 SpringMemoEvents
└─ config                     도메인과 유스케이스를 Spring 빈으로 조립
```

- `domain`과 `application`은 Spring에 의존하지 않습니다. 1~3편 테스트가 Spring 없이 몇 밀리초 안에 끝나는 이유입니다.
- 테스트는 관찰하는 경계에 맞춰 준비 방식을 고릅니다. 도메인 규칙은 객체(`MemoTest`), HTTP 요청 본문은 JSON 파일(`MemoCreateApiTest`), DB 상태는 `@Sql` 스크립트(`MemoArchiveApiTest`)로 준비합니다.
- 테스트 메서드 이름은 한국어로 쓰고 `_`를 공백으로 표시합니다. 실패 보고서에서 그대로 문장으로 읽히게 하려는 것입니다.

## 환경

Java 21, Gradle 8.10.2, Spring Boot 3.3.5, JUnit 5.10.5, Mockito 5.14.2, H2 2.2.224(인메모리).

Mockito는 JDK 21의 동적 attach 경고를 피하려고 테스트 JVM에 javaagent로 올립니다. 이 방식은 mockito-core 5.14부터 지원해, Spring Boot 3.3.5가 관리하는 5.11 대신 5.14.2를 씁니다.
