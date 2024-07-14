CREATE SEQUENCE IF NOT EXISTS log_access_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS otp_codes_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE chats_participants
(
    chats_id        BIGINT NOT NULL,
    participants_id BIGINT NOT NULL
);

CREATE TABLE messages_user_delete_message
(
    messages_id            BIGINT NOT NULL,
    user_delete_message_id BIGINT NOT NULL
);

CREATE TABLE users_blocked_contacts
(
    user_id             BIGINT NOT NULL,
    blocked_contacts_id BIGINT NOT NULL
);

ALTER TABLE messages
    ADD is_delete BOOLEAN;

ALTER TABLE messages
    ADD sender_id BIGINT;

ALTER TABLE messages
    ALTER COLUMN is_delete SET NOT NULL;

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);

ALTER TABLE chats_participants
    ADD CONSTRAINT fk_chapar_on_chat FOREIGN KEY (chats_id) REFERENCES chats (id);

ALTER TABLE chats_participants
    ADD CONSTRAINT fk_chapar_on_user FOREIGN KEY (participants_id) REFERENCES users (id);

ALTER TABLE messages_user_delete_message
    ADD CONSTRAINT fk_mesusedelmes_on_messages FOREIGN KEY (messages_id) REFERENCES messages (id);

ALTER TABLE messages_user_delete_message
    ADD CONSTRAINT fk_mesusedelmes_on_user FOREIGN KEY (user_delete_message_id) REFERENCES users (id);

ALTER TABLE users_blocked_contacts
    ADD CONSTRAINT fk_useblocon_on_blockedcontacts FOREIGN KEY (blocked_contacts_id) REFERENCES users (id);

ALTER TABLE users_blocked_contacts
    ADD CONSTRAINT fk_useblocon_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users_chats_roles
    DROP CONSTRAINT fk_chats;

ALTER TABLE messages
    DROP CONSTRAINT fk_messages;

ALTER TABLE chats
    DROP CONSTRAINT fk_owners_chats;

ALTER TABLE users_chats_roles
    DROP CONSTRAINT fk_roles;

ALTER TABLE unread_messages
    DROP CONSTRAINT fk_unread_messages_message;

ALTER TABLE unread_messages
    DROP CONSTRAINT fk_unread_messages_user;

ALTER TABLE users_chats_roles
    DROP CONSTRAINT fk_users;

ALTER TABLE messages
    DROP CONSTRAINT fk_users_messages;

DROP TABLE roles CASCADE;

DROP TABLE unread_messages CASCADE;

DROP TABLE users_chats_roles CASCADE;

ALTER TABLE chats
    DROP COLUMN is_deleted;

ALTER TABLE chats
    DROP COLUMN owner_id;

ALTER TABLE chats
    DROP COLUMN status_id;

ALTER TABLE messages
    DROP COLUMN is_deleted;

ALTER TABLE messages
    DROP COLUMN message_id;

ALTER TABLE messages
    DROP COLUMN user_id;

ALTER TABLE chats
    ALTER COLUMN created DROP NOT NULL;

ALTER TABLE messages
    ALTER COLUMN created DROP NOT NULL;

ALTER TABLE chats
    ALTER COLUMN description TYPE VARCHAR(255) USING (description::VARCHAR(255));

ALTER TABLE messages
    ALTER COLUMN message TYPE VARCHAR(255) USING (message::VARCHAR(255));

ALTER TABLE messages
    ALTER COLUMN message DROP NOT NULL;

ALTER TABLE attachments
    ALTER COLUMN message_id DROP NOT NULL;

ALTER TABLE chats
    ALTER COLUMN name TYPE VARCHAR(255) USING (name::VARCHAR(255));

ALTER TABLE chats
    ALTER COLUMN name DROP NOT NULL;

ALTER TABLE attachments
    ALTER COLUMN type TYPE VARCHAR(255) USING (type::VARCHAR(255));

ALTER TABLE attachments
    ALTER COLUMN type DROP NOT NULL;

ALTER TABLE attachments
    ALTER COLUMN url DROP NOT NULL;