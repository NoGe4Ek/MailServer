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
    created   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_person) REFERENCES person (id)
);

CREATE TABLE possibility
(
    id      UUID PRIMARY KEY,
    name    TEXT        NOT NULL,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role
(
    id      UUID PRIMARY KEY,
    name    TEXT        NOT NULL,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_to_possibility
(
    id_role        UUID NOT NULL,
    id_possibility UUID NOT NULL,
    FOREIGN KEY (id_role) REFERENCES role (id),
    FOREIGN KEY (id_possibility) REFERENCES possibility (id)
);

CREATE TABLE role_to_staff
(
    id_role  UUID NOT NULL,
    id_staff UUID NOT NULL,
    FOREIGN KEY (id_role) REFERENCES role (id),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE student
(
    id               UUID PRIMARY KEY,
    id_person        UUID        NOT NULL,
    created          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_person) REFERENCES person (id)
);

CREATE TABLE filter
(
    id         UUID PRIMARY KEY,
    id_staff   UUID        NOT NULL,
    email      TEXT        NOT NULL,
    name       TEXT        NOT NULL,
    expression TEXT,
    created    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE group_attributes
(
    id      UUID PRIMARY KEY,
    name    TEXT        NOT NULL,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE attribute
(
    id                 UUID PRIMARY KEY,
    id_staff           UUID        NOT NULL,
    id_group_attribute UUID        NOT NULL,
    name               TEXT        NOT NULL,
    expression         TEXT,
    created            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_staff) REFERENCES staff (id),
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

CREATE TABLE mail_virtual_user
(
    id             UUID PRIMARY KEY,
    id_filter      UUID        NOT NULL,
    id_staff       UUID        NOT NULL,
    email          TEXT        NOT NULL UNIQUE,
    password       TEXT        NOT NULL,
    mail_directory TEXT        NOT NULL,
    created        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (id_filter) REFERENCES filter (id),
    FOREIGN KEY (id_staff) REFERENCES staff (id)
);

CREATE TABLE mail_auto_forward_map
(
    id_staff        UUID NOT NULL,
    id_virtual_user UUID NOT NULL,
    FOREIGN KEY (id_staff) REFERENCES staff (id),
    FOREIGN KEY (id_virtual_user) REFERENCES mail_virtual_user (id)
);
