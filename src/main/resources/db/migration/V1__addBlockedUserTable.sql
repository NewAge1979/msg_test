-- Таблица для хранения заблокированных контактов пользователей.
CREATE TABLE blocked_contacts (
    user_id         BIGINT NOT NULL,
    blocked_user_id BIGINT NOT NULL,
    created         TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT pk_blocked_contacts PRIMARY KEY (user_id, blocked_user_id)
);
COMMENT ON TABLE blocked_contacts IS 'Список заблокированных контактов пользователей.';
COMMENT ON COLUMN blocked_contacts.user_id IS 'Идентификатор пользователя (ссылка на таблицу users).';
COMMENT ON COLUMN blocked_contacts.blocked_user_id IS 'Идентификатор заблокированного пользователя (ссылка на таблицу users).';
COMMENT ON COLUMN blocked_contacts.created IS 'Дата создания записи.';
