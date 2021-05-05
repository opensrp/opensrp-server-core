--clear data
TRUNCATE TABLE core.client_migration_file CASCADE;

ALTER SEQUENCE core.client_migration_file_id_seq RESTART WITH 7;

--insert data
INSERT INTO core.client_migration_file (id, identifier, filename, on_object_storage, object_storage_path, jurisdiction, version, manifest_id, file_contents,created_at) VALUES
(1, 'cec563c2-d3e7-4b2a-866e-82d5902d44de', '1.up.sql', false, null, null, 1, 1, 'CREATE TABLE vaccines(id INTEGER, vaccine_name VARCHAR);', '2018-03-19 17:27:28.717000'),
(2, '2b6387a0-f599-4af9-b96b-0c45877a7628', '1.down.sql', false, null, null, 1, 1, 'DROP TABLE vaccines;', '2018-03-19 17:27:28.717000'),
(3, 'f9b00527-fa8f-49db-8ff9-aa11b8ffc9c3', '2.up.sql', false, null, null, 2, 2, 'ALTER TABLE vaccines ADD COLUMN given BOOLEAN;', '2018-03-20 17:27:28.717000'),
(4, '70cbd363-5dd7-4e92-8364-bcc093cb4962', '2.down.sql', false, null, null, 2, 2, 'ALTER TABLE vaccines RENAME TO vaccines_old;\nCREATE TABLE vaccines(id INTEGER, vaccine_name VARCHAR);\nINSERT INTO vaccines(id, vaccine_name) SELECT id, vaccine_name FROM vaccines_old;\nDROP TABLE vaccines_old', '2018-03-20 17:27:28.717000'),
(5, '38bc7c3f-7439-4d62-bd9b-fa40867d0a44', '3.up.sql', false, null, null, 3, 3, 'CREATE TABLE alerts(id INTEGER, alert_name VARCHAR, is_offline BOOLEAN);', '2018-03-21 17:27:28.717000'),
(6, '59e0f151-7db2-41aa-b648-6494d7989c4b', '3.down.sql', false, null, null, 3, 3, 'DROP TABLE alerts;', '2018-03-21 17:27:28.717000');
