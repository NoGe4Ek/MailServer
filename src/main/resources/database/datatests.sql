INSERT INTO person
VALUES ('887f38df-9e1f-407f-b82d-c28ceddc92ed', 'ALL', 'ALL', 'ALL', 'private@poly-sender.ru');

INSERT INTO person
VALUES ('fde5395e-766f-492e-b6c6-cca981026769', 'Staff1', 'Staff1', 'Staff1', 'getrhymes@yandex.ru');

INSERT INTO person
VALUES ('f53a37c1-4db0-4c50-a490-6ef18f74c20f', 'Staff2', 'Staff2', 'Staff2', 'getrhymes@yandex.ru');

INSERT INTO person
VALUES ('9a60d81c-916d-4d03-b5ab-1809d13ad268', 'Student1', 'Student1', 'Student1', 'getrhymes@gmail.com');

INSERT INTO person
VALUES ('ce00ac40-791c-48ad-abf1-b0be560043a2', 'Student2', 'Student2', 'Student2', 'tarasenko.ns@edu.spbstu.ru');

INSERT INTO staff
VALUES ('ad7a8951-2f95-4619-802b-1285c3279623', '887f38df-9e1f-407f-b82d-c28ceddc92ed');

INSERT INTO staff
VALUES ('9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b', 'fde5395e-766f-492e-b6c6-cca981026769');

INSERT INTO staff
VALUES ('725cee0f-7a95-4094-b19a-11b27f779490', 'f53a37c1-4db0-4c50-a490-6ef18f74c20f');

INSERT INTO group_attributes
VALUES ('a6d2f576-1c52-48f1-b092-5fe5dbfe860f', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Страна');
INSERT INTO group_attributes
VALUES ('d68ca5d9-1124-4d3f-b192-cfd52c30449c', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Подразделение');
INSERT INTO group_attributes
VALUES ('b7a1e33d-505e-4350-9602-c3fdce0330ca', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Финансирование');
INSERT INTO group_attributes
VALUES ('7a73efd2-2b36-42c3-9304-4036a5a85c64', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Форма обучения');
INSERT INTO group_attributes
VALUES ('655be334-435f-486f-913a-09155cac31a6', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Тип программы');
INSERT INTO group_attributes
VALUES ('98a7291b-9577-4d37-bf37-efc0dfbca7f0', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Направление');
INSERT INTO group_attributes
VALUES ('357a94ac-5017-42c2-b5cf-7f571b20174b', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Направленность');
INSERT INTO group_attributes
VALUES ('a2bde73b-1000-4404-9bf7-461b33441c17', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Целевое назначение');
INSERT INTO group_attributes
VALUES ('a429a404-5edc-450c-8445-96fc86ae77e7', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Номер группы');
INSERT INTO group_attributes
VALUES ('cd3e9ad6-5535-46d2-98b4-e2214583126f', 'ad7a8951-2f95-4619-802b-1285c3279623', 'Курс');

INSERT INTO attribute
VALUES ('318511c1-e9b1-4597-8e93-b68149fd3657', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'a6d2f576-1c52-48f1-b092-5fe5dbfe860f', 'Российская Федерация', null);
INSERT INTO attribute
VALUES ('eddbd202-c79a-45de-9d84-4de8f93fcb04', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'a6d2f576-1c52-48f1-b092-5fe5dbfe860f', 'Республика Ангола', null);
INSERT INTO attribute
VALUES ('1cdf23fc-6a1d-4648-9c4f-a0f13ac8c60b', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'd68ca5d9-1124-4d3f-b192-cfd52c30449c', 'ИКНиТ / ВШИСиСТ', null);
INSERT INTO attribute
VALUES ('614b6832-4f19-40c4-b02a-dcc586892b47', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'b7a1e33d-505e-4350-9602-c3fdce0330ca', 'Бюджет', null);
INSERT INTO attribute
VALUES ('d159c111-18cd-4413-a69c-2dc67b2bb7be', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'b7a1e33d-505e-4350-9602-c3fdce0330ca', 'Контракт', null);
INSERT INTO attribute
VALUES ('b41b2f91-cca9-41c1-9597-99a2416f1928', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'a429a404-5edc-450c-8445-96fc86ae77e7', '3530901/80203', null);
INSERT INTO attribute
VALUES ('da45efeb-ac57-447b-b062-4c500d2675fa', 'ad7a8951-2f95-4619-802b-1285c3279623',
        'a429a404-5edc-450c-8445-96fc86ae77e7', '3530901/80202', null);

INSERT INTO student
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de',
        '9a60d81c-916d-4d03-b5ab-1809d13ad268');

INSERT INTO student
VALUES ('33067576-4164-492a-a2f7-908831aeb240',
        'ce00ac40-791c-48ad-abf1-b0be560043a2');

INSERT INTO student_to_attribute
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de', '1cdf23fc-6a1d-4648-9c4f-a0f13ac8c60b');
INSERT INTO student_to_attribute
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de', '318511c1-e9b1-4597-8e93-b68149fd3657');
INSERT INTO student_to_attribute
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de', '614b6832-4f19-40c4-b02a-dcc586892b47');
INSERT INTO student_to_attribute
VALUES ('8ff93425-b708-4f63-9792-91814ea2a2de', 'b41b2f91-cca9-41c1-9597-99a2416f1928');

INSERT INTO student_to_attribute
VALUES ('33067576-4164-492a-a2f7-908831aeb240', '1cdf23fc-6a1d-4648-9c4f-a0f13ac8c60b');
INSERT INTO student_to_attribute
VALUES ('33067576-4164-492a-a2f7-908831aeb240', 'eddbd202-c79a-45de-9d84-4de8f93fcb04');
INSERT INTO student_to_attribute
VALUES ('33067576-4164-492a-a2f7-908831aeb240', 'd159c111-18cd-4413-a69c-2dc67b2bb7be');
INSERT INTO student_to_attribute
VALUES ('33067576-4164-492a-a2f7-908831aeb240', 'da45efeb-ac57-447b-b062-4c500d2675fa');

INSERT INTO email
VALUES ('5a146b72-00aa-4b88-95de-8bd20ff9179f',
        'filter1_send@poly-sender.ru',
        'thisisfilter1',
        '/filter1-send');

INSERT INTO email
VALUES ('43976846-fa85-4632-9f1e-405ae134ba75',
        'filter1_answer@poly-sender.ru',
        'thisisfilter1',
        '/filter1-answer');

INSERT INTO email
VALUES ('59fc5c05-ab5d-4c15-a790-53185b4c3370',
        'filter2_send@poly-sender.ru',
        'thisisfilter2',
        '/filter2-send');

INSERT INTO email
VALUES ('0a6fb05e-087d-47f6-890a-6abd4b1f5f89',
        'filter2_answer@poly-sender.ru',
        'thisisfilter2',
        '/filter2-answer');

INSERT INTO filter
VALUES ('59cbc294-c52d-43b1-831d-66b1a4d04047',
        '725cee0f-7a95-4094-b19a-11b27f779490',
        '5a146b72-00aa-4b88-95de-8bd20ff9179f',
        '43976846-fa85-4632-9f1e-405ae134ba75',
        'filter1',
        'manual',
        false);

INSERT INTO filter
VALUES ('4312bd25-aa7a-4924-ad39-e3ed997d9054',
        '725cee0f-7a95-4094-b19a-11b27f779490',
        '59fc5c05-ab5d-4c15-a790-53185b4c3370',
        '0a6fb05e-087d-47f6-890a-6abd4b1f5f89',
        'filter2',
        'auto',
        true);

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