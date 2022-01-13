INSERT INTO imagefile VALUES
(1, 'path', NULL );

INSERT INTO imagefile VALUES
(2, 'path2', NULL );

INSERT INTO post VALUES
(1, 'title', 'content', 0, 0, NULL , NULL , NULL , 1 );

INSERT INTO post VALUES
(1, 'title', 'content', 0, 0, NULL , NULL , NULL , 2 );

UPDATE TABLE image_file SET post = 1 WHERE if_id = 1;
UPDATE TABLE image_file SET post = 1 WHERE if_id = 2;
