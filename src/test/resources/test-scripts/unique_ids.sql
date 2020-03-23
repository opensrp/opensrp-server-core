--clear data
TRUNCATE TABLE core.unique_id CASCADE;

ALTER SEQUENCE core.unique_id_id_seq RESTART WITH 6;