--clear data
TRUNCATE TABLE core.structure CASCADE;

ALTER SEQUENCE core.structure_id_seq RESTART WITH 6;

ALTER SEQUENCE core.structure_metadata_id_seq RESTART WITH 6;

INSERT INTO core.structure (id, json) VALUES (1, '{"id": "90397", "type": "Feature", "geometry": {"type": "Polygon", "coordinates": [[[32.5978597, -14.1699446], [32.5978956, -14.1699609], [32.5978794, -14.1699947], [32.5978434, -14.1699784], [32.5978597, -14.1699446]]]}, "properties": {"uid": "41587456-b7c8-4c4e-b433-23a786f742fc", "code": "21384443", "type": "Residential Structure", "status": "Active", "version": 0, "parentId": "3734", "geographicLevel": 5, "effectiveStartDate": "2017-01-10T00:00:00.000"}, "serverVersion": 1542376382851}');

INSERT INTO core.structure_metadata (id, structure_id, geojson_id, type, parent_id, uuid, status, server_version) VALUES (1, 1, '90397', 'Residential Structure', '3734', '41587456-b7c8-4c4e-b433-23a786f742fc', 'ACTIVE', 1542376382851);