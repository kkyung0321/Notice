INSERT INTO image_file (if_id, path, post_id)
VALUES (1, 'path', NULL);

INSERT INTO image_file (if_id, path, post_id)
VALUES (2, 'path2', NULL);

INSERT INTO member (mid, username, password, member_role, nick_name, login_date)
VALUES (1, 'username', 'password', 'ROLE_USER', 'nickname', NULL);

INSERT INTO post (pid, title, content, like_counts, hits, created_date, modified_date, member_id)
VALUES (1, 'title', 'content', 0, 0, NULL, NULL, 1);

UPDATE image_file
SET post_id = 1
WHERE if_id = 1;

UPDATE image_file
SET post_id = 1
WHERE if_id = 2;
