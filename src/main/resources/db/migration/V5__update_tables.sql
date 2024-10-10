ALTER TABLE users
    ADD avatar VARCHAR;

ALTER TABLE messages
    ADD is_read BOOLEAN;

ALTER TABLE messages
    ADD type VARCHAR(32);
