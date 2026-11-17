#!/usr/bin/env bash
# 글에 적은 "구현을 일부러 틀리게 바꾼" 실험을 다시 돌린다.
#
# mutations/<편>/<이름>.patch 를 작업 트리 복사본에 적용하고 테스트를 실행한 뒤,
# 실패한 테스트 목록을 <이름>.expected 와 비교한다. 원본 작업 트리는 바꾸지 않는다.
#
#   scripts/mutate.sh                     모든 patch
#   scripts/mutate.sh mutations/part-2    한 편의 patch만
#
# 하나라도 기대와 다르면 종료 코드 1을 돌려준다.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
target="${1:-mutations}"
mismatch=0

failed_tests() {
    # JUnit XML에서 실패하거나 오류가 난 테스트를 "클래스 > 표시 이름" 형식으로 뽑는다.
    awk '
        /<testcase / {
            match($0, /name="[^"]*"/);      name = substr($0, RSTART + 6, RLENGTH - 7)
            match($0, /classname="[^"]*"/); cls = substr($0, RSTART + 11, RLENGTH - 12)
            n = split(cls, parts, "."); cls = parts[n]
        }
        /<failure |<error / { print cls " > " name }
    ' "$1"/build/test-results/test/*.xml | sort
}

while IFS= read -r patch; do
    work="$(mktemp -d)"
    rsync -a --exclude build --exclude .gradle --exclude .git "$root/" "$work/"
    if ! (cd "$work" && git apply "$patch"); then
        echo "적용 실패  ${patch#"$root"/}"; mismatch=1; rm -rf "$work"; continue
    fi
    (cd "$work" && ./gradlew test --offline -q >/dev/null 2>&1) || true

    actual="$(failed_tests "$work")"
    expected="$(sort "${patch%.patch}.expected" 2>/dev/null || true)"
    name="${patch#"$root"/}"

    if [[ "$actual" == "$expected" ]]; then
        echo "일치  $name ($(grep -c . <<<"$actual" || true)건 실패)"
    else
        echo "다름  $name"
        diff <(echo "$expected") <(echo "$actual") | sed 's/^/      /' || true
        mismatch=1
    fi
    rm -rf "$work"
done < <(find "$root/$target" -name '*.patch' | sort)

exit "$mismatch"
