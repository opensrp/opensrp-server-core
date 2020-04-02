--clear data
TRUNCATE TABLE core.manifest CASCADE;

ALTER SEQUENCE core.manifest_id_seq RESTART WITH 6;

--insert data
INSERT INTO core.manifest (id, json, app_version, app_id, created_at, updated_at) VALUES
(1, '{"identifier": "1", "json": "{}", "createdAt": "2018-11-12T11:49:00.782", "updatedAt": "2018-11-12T11:49:00.782",  "appId":"org.smartregister.giz", "appVersion":"0.0.1"}', '0.0.1', 'org.smartregister.giz', '2018-03-16 10:03:01.341000', null);
