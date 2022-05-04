CREATE TABLE person
(
    id         UUID PRIMARY KEY,
    last_name  TEXT        NOT NULL,
    first_name TEXT        NOT NULL,
    patronymic TEXT        NOT NULL,
    email      TEXT        NOT NULL,
    created    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE staff
(
    id        UUID PRIMARY KEY,
    id_person UUID        NOT NULL,
    password  TEXT        NOT NULL,
    created   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_person) REFERENCES person (id)
);

CREATE TABLE role
(
    id      UUID PRIMARY KEY,
    name    TEXT        NOT NULL,
    level   INTEGER     NOT NULL,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_to_staff
(
    id_role  UUID NOT NULL,
    id_staff UUID NOT NULL,
    FOREIGN KEY (id_role) REFERENCES role (id),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE access
(
    id          UUID PRIMARY KEY,
    id_staff    UUID,
    last_name   TEXT        NOT NULL,
    first_name  TEXT        NOT NULL,
    patronymic  TEXT        NOT NULL,
    email       TEXT        NOT NULL,
    department  TEXT        NOT NULL,
    high_school TEXT        NOT NULL,
    created     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE student
(
    id        UUID PRIMARY KEY,
    id_person UUID        NOT NULL,
    created   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_person) REFERENCES person (id)
);

CREATE TABLE email
(
    id             UUID PRIMARY KEY,
    email          TEXT        NOT NULL UNIQUE,
    password       TEXT        NOT NULL,
    mail_directory TEXT        NOT NULL,
    created        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE filter
(
    id              UUID PRIMARY KEY,
    id_staff        UUID        NOT NULL,
    id_dependency   UUID,
    id_email_send   UUID        NOT NULL,
    id_email_answer UUID        NOT NULL,
    name            TEXT        NOT NULL,
    mode            TEXT        NOT NULL,
    auto_forward    BOOLEAN     NOT NULL,
    expression      TEXT,
    link            BOOLEAN     NOT NULL DEFAULT FALSE,
    created         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id),
    FOREIGN KEY (id_dependency) REFERENCES filter (id),
    FOREIGN KEY (id_email_send) REFERENCES email (id),
    FOREIGN KEY (id_email_answer) REFERENCES email (id)
);

CREATE TABLE group_attributes
(
    id       UUID PRIMARY KEY,
    id_staff UUID        NOT NULL,
    name     TEXT        NOT NULL,
    created  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE attribute
(
    id                 UUID PRIMARY KEY,
    id_staff           UUID        NOT NULL,
    id_dependency      UUID,
    id_group_attribute UUID,
    name               TEXT        NOT NULL,
    expression         TEXT,
    link               BOOLEAN     NOT NULL DEFAULT FALSE,
    created            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id),
    FOREIGN KEY (id_dependency) REFERENCES attribute (id),
    FOREIGN KEY (id_group_attribute) REFERENCES group_attributes (id)
);

CREATE TABLE student_to_filter
(
    id_student UUID NOT NULL,
    id_filter  UUID NOT NULL,
    FOREIGN KEY (id_student) REFERENCES student (id),
    FOREIGN KEY (id_filter) REFERENCES filter (id)
);

CREATE TABLE student_to_attribute
(
    id_student   UUID NOT NULL,
    id_attribute UUID NOT NULL,
    FOREIGN KEY (id_student) REFERENCES student (id),
    FOREIGN KEY (id_attribute) REFERENCES attribute (id)
);

CREATE TABLE notification
(
    id           UUID PRIMARY KEY,
    id_consumer  UUID        NOT NULL,
    id_producer  UUID        NOT NULL,
    id_filter    UUID,
    id_attribute UUID,
    created      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_consumer) REFERENCES staff (id),
    FOREIGN KEY (id_producer) REFERENCES staff (id),
    FOREIGN KEY (id_filter) REFERENCES filter (id),
    FOREIGN KEY (id_attribute) REFERENCES attribute (id)
);

-- INIT DATA
INSERT INTO person
VALUES ('887f38df-9e1f-407f-b82d-c28ceddc92ed', 'ALL', 'ALL', 'ALL', 'basicadmin@poly-sender.ru');

INSERT INTO staff
VALUES ('ad7a8951-2f95-4619-802b-1285c3279623', '887f38df-9e1f-407f-b82d-c28ceddc92ed',
        '$2a$10$8xworZ7s4H1HIWV/ubIkKuvtiALobURe7onFm36oLPz51J1KEvAjS');

INSERT INTO email
VALUES ('b3b6a8f5-9fea-4f15-8fca-5519bd52a737',
        'noreply@poly-sender.ru',
        'fon?gfMKUC%d',
        '/noreply');

INSERT INTO role
VALUES ('6f8bcb5f-5fc4-4805-8314-f36904ef1eda', 'USER', 1);

INSERT INTO role
VALUES ('c55781a7-a000-4595-9b72-7affe99f3e26', 'ADMIN', 2);

-- INSERT INTO role_to_staff
-- VALUES ('6f8bcb5f-5fc4-4805-8314-f36904ef1eda', 'ad7a8951-2f95-4619-802b-1285c3279623');

INSERT INTO role_to_staff
VALUES ('c55781a7-a000-4595-9b72-7affe99f3e26', 'ad7a8951-2f95-4619-802b-1285c3279623');
