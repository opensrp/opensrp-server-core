--clear data
TRUNCATE TABLE core.structure CASCADE;

ALTER SEQUENCE core.structure_id_seq RESTART WITH 6;

ALTER SEQUENCE core.structure_metadata_id_seq RESTART WITH 6;

INSERT INTO core.structure (id, json) VALUES (1, '{"id": "90397", "type": "Feature", "geometry": {"type": "Polygon", "coordinates": [[[32.5978597, -14.1699446], [32.5978956, -14.1699609], [32.5978794, -14.1699947], [32.5978434, -14.1699784], [32.5978597, -14.1699446]]]}, "properties": {"uid": "41587456-b7c8-4c4e-b433-23a786f742fc", "code": "21384443", "type": "Residential Structure", "status": "Active", "version": 0, "parentId": "3734", "geographicLevel": 5, "effectiveStartDate": "2017-01-10T0000.000"}, "serverVersion": 1542376382851}');

INSERT INTO core.structure_metadata (id, structure_id, geojson_id, type, parent_id, uuid, status, server_version, date_created) VALUES (1, 1, '90397', 'Residential Structure', '3734', '41587456-b7c8-4c4e-b433-23a786f742fc', 'ACTIVE', 1542376382851, '2020-09-25T10:00:00+0300');

INSERT INTO core.structure (id, json) VALUES (2, '{"id": "90398", "type": "Feature", "geometry": {"type": "Polygon", "coordinates": [[[43.5978597, -14.1699446], [43.5978956, -13.1699609], [43.5978794, -13.1699947], [42.5978434, -14.1699784], [42.5978597, -14.1699446]]]}, "properties": {"uid": "3970790a-5a00-11ea-82b4-0242ac130003", "code": "21384421", "type": "Larval Dipping", "status": "Active", "version": 0, "parentId": "3724", "geographicLevel": 4, "effectiveStartDate": "2017-01-10T0000.000"}, "serverVersion": 1542376382842}');

INSERT INTO core.structure_metadata (id, structure_id, geojson_id, type, parent_id, uuid, status, server_version, date_created) VALUES (2, 2, '90398', 'Larval Dipping', '3724', '3970790a-5a00-11ea-82b4-0242ac130003', 'ACTIVE', 1542376382862, '2020-09-26T10:00:00+0300');
