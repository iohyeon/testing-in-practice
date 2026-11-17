-- u2에게 공유된 메모 두 건(활성 1, 보관 1)과 u3에게만 공유된 메모 한 건
INSERT INTO memo (id, owner_id, title, visibility, status) VALUES
    (200, 'u1', '공유한 회의록', 'PRIVATE', 'ACTIVE'),
    (201, 'u1', '보관한 회의록', 'PRIVATE', 'ARCHIVED'),
    (202, 'u1', '다른 사람에게 공유한 메모', 'PRIVATE', 'ACTIVE');

INSERT INTO memo_viewer (memo_id, viewer_id) VALUES
    (200, 'u2'),
    (201, 'u2'),
    (202, 'u3');
