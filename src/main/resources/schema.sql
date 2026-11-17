CREATE TABLE IF NOT EXISTS memo (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id   VARCHAR(50)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    visibility VARCHAR(20)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 메모 주인이 다른 사용자에게 읽기 권한을 준 기록. 같은 사람에게 두 번 줄 수 없다.
CREATE TABLE IF NOT EXISTS memo_viewer (
    memo_id   BIGINT      NOT NULL REFERENCES memo (id),
    viewer_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (memo_id, viewer_id)
);
