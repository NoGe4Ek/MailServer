INSERT INTO person
VALUES ('fde5395e-766f-492e-b6c6-cca981026769', 'Staff1', 'Staff1', 'Staff1', 'getrhymes@yandex.ru');

INSERT INTO person
VALUES ('f53a37c1-4db0-4c50-a490-6ef18f74c20f', 'Staff2', 'Staff2', 'Staff2', 'getrhymes@yandex.ru');

INSERT INTO person
VALUES ('9a60d81c-916d-4d03-b5ab-1809d13ad268', 'Student1', 'Student1', 'Student1', 'getrhymes@gmail.com');

INSERT INTO person
VALUES ('ce00ac40-791c-48ad-abf1-b0be560043a2', 'Student2', 'Student2', 'Student2', 'tarasenko.ns@edu.spbstu.ru');

INSERT INTO staff
VALUES ('9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b', 'fde5395e-766f-492e-b6c6-cca981026769');

INSERT INTO staff
VALUES ('725cee0f-7a95-4094-b19a-11b27f779490', 'f53a37c1-4db0-4c50-a490-6ef18f74c20f');

INSERT INTO student
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de',
        '9a60d81c-916d-4d03-b5ab-1809d13ad268',
        'Российская Федерация',
        'ИКНиТ / ВШИСиСТ',
        'Бюджет',
        'Очное',
        'Бакалавриат',
        '09.03.03 - бакалавриат - Прикладная информатика',
        '09.03.03_03 - бакалавриат - Интеллектуальные инфокоммуникационные технологии',
        true,
        '3530903/80302',
        4);

INSERT INTO student
VALUES ('33067576-4164-492a-a2f7-908831aeb240',
        'ce00ac40-791c-48ad-abf1-b0be560043a2',
        'Российская Федерация',
        'ИКНиТ / ВШИСиСТ',
        'Бюджет',
        'Очное',
        'Бакалавриат',
        '09.03.03 - бакалавриат - Прикладная информатика',
        '09.03.03_03 - бакалавриат - Интеллектуальные инфокоммуникационные технологии',
        false,
        '3530903/80302',
        4);

INSERT INTO filter
VALUES ('59cbc294-c52d-43b1-831d-66b1a4d04047',
        '9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b',
        'filter1@poly-sender.ru',
        'filter1');

INSERT INTO filter
VALUES ('4312bd25-aa7a-4924-ad39-e3ed997d9054',
        '725cee0f-7a95-4094-b19a-11b27f779490',
        'filter2@poly-sender.ru',
        'filter2');

INSERT INTO student_to_filter
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de',
        '59cbc294-c52d-43b1-831d-66b1a4d04047');

INSERT INTO student_to_filter
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de',
        '4312bd25-aa7a-4924-ad39-e3ed997d9054');

INSERT INTO student_to_filter
VALUES ('33067576-4164-492a-a2f7-908831aeb240',
        '59cbc294-c52d-43b1-831d-66b1a4d04047');

INSERT INTO student_to_filter
VALUES ('33067576-4164-492a-a2f7-908831aeb240',
        '4312bd25-aa7a-4924-ad39-e3ed997d9054');

INSERT INTO mail_virtual_user
VALUES ('2a3bc6f6-7ba0-41cf-a278-3f39b4c9ad91',
        '59cbc294-c52d-43b1-831d-66b1a4d04047',
        '9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b',
        'filter1@poly-sender.ru',
        '{plain}thisisfilter1',
        '/filter1');

INSERT INTO mail_virtual_user
VALUES ('6b36ab78-f3ee-4408-8d2a-bd511a4365c9',
        '4312bd25-aa7a-4924-ad39-e3ed997d9054',
        '725cee0f-7a95-4094-b19a-11b27f779490',
        'filter2@poly-sender.ru',
        '{plain}thisisfilter2',
        '/filter2');

INSERT INTO mail_auto_forward_map
VALUES ('725cee0f-7a95-4094-b19a-11b27f779490', '6b36ab78-f3ee-4408-8d2a-bd511a4365c9');
