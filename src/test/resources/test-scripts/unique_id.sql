--clear data
TRUNCATE TABLE core.unique_id CASCADE;

ALTER SEQUENCE core.unique_id_seq RESTART WITH 8;

INSERT INTO core.unique_id (id, location, openmrs_id, status, created_at)
VALUES (1, 'Akros_1', '12345-11', 'not_used', "2019-01-10T0000.000");
INSERT INTO core.unique_id (id, location, openmrs_id, status, created_at)
VALUES (2, 'Akros_1', '12345-22', 'not_used', "2019-01-12T0000.000");
INSERT INTO core.unique_id (id, location, openmrs_id, status, created_at)
VALUES (2, 'Akros_1', '12345-33', 'used', "2019-01-13T0000.000");