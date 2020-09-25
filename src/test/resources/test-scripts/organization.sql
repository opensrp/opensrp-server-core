--clear data
TRUNCATE TABLE team.organization CASCADE;
TRUNCATE TABLE team.organization_location CASCADE;
TRUNCATE TABLE core.location CASCADE;
TRUNCATE TABLE core.plan CASCADE;

ALTER SEQUENCE team.organization_id_seq RESTART WITH 4;
ALTER SEQUENCE team.organization_location_id_seq RESTART WITH 4;

INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (1, 'fcc19470-d599-11e9-bb65-2a2ae2dbcce4', true, 'The Luang', '{"coding": [{"code": "team", "system": "http://terminology.hl7.org/CodeSystem/organization-type", "display": "Team"}]}', null, null);
INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (3, '4c506c98-d3a9-11e9-bb65-2a2ae2dbcce4', true, 'Demo Team', null, null, null);
INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (2, 'd23f7350-d406-11e9-bb65-2a2ae2dbcce4', true, 'Takang 1', '{"coding": [{"code": "team", "system": "http://terminology.hl7.org/CodeSystem/organization-type", "display": "Team"}]}', null, 1);



INSERT INTO core.location (id, json) VALUES (2243, '{"id": "304cbcd4-0850-404a-a8b1-486b02f7b84d", "type": "Feature", "properties": {"name": "TLv1_02", "status": "Active", "version": 0, "parentId": "dad42fa6-b9b8-4658-bf25-bfa7ab5b16ae", "OpenMRS_Id": "de28c78d-3111-4266-957b-c731a3330c1d", "geographicLevel": 4}, "serverVersion": 1568331709467}');
INSERT INTO core.location (id, json) VALUES (224, '{"id": "3270", "type": "Feature", "geometry": {"type": "MultiPolygon"}, "properties": {"name": "NYJ_6", "status": "Active", "version": 0, "parentId": "2977", "geographicLevel": 2}, "serverVersion": 1545220000265}');
INSERT INTO core.location_metadata (id, location_id, geojson_id, type, parent_id, uuid, status, server_version, name) VALUES (2243, 2243, '304cbcd4-0850-404a-a8b1-486b02f7b84d', null, 'dad42fa6-b9b8-4658-bf25-bfa7ab5b16ae', null, 'ACTIVE', 1568331709467, 'TLv1_02');
INSERT INTO core.location_metadata (id, location_id, geojson_id, type, parent_id, uuid, status, server_version, name) VALUES (224, 224, '3270', null, '2977', null, 'ACTIVE', 1545220000265, 'NYJ_6');

INSERT INTO core.plan (identifier, json, date_deleted, server_version, id) VALUES ('36adfd55-3a61-4fcf-be39-3c9ccb2ffd78', '{}', null, 1567765433007, 162);
INSERT INTO core.plan (identifier, json, date_deleted, server_version, id) VALUES ('9d1403a5-756d-517b-91d6-5b19059a69f0', '{}', null, 1567765433007, 11);
INSERT INTO core.plan (identifier, json, date_deleted, server_version, id) VALUES ('7f2ae03f-9569-5535-918c-9d976b3ae5f8', '{}', null, 1567765433007, 294);


INSERT INTO team.organization_location (id, organization_id, location_id, plan_id, from_date, to_date,duration) VALUES (1, 1, 2243, 162, '2019-09-10 17:29:55.059000', '2021-09-10 17:29:55.059000',daterange('2019-09-10','2021-09-10'));
INSERT INTO team.organization_location (id, organization_id, location_id, plan_id, from_date, to_date,duration) VALUES (2, 1, 2243, 11, '2019-09-10 17:29:55.059000', '2021-09-10 17:29:55.059000',daterange('2019-09-10','2021-09-10'));
INSERT INTO team.organization_location (id, organization_id, location_id, plan_id, from_date, to_date,duration) VALUES (3, 2, 2243, 294, '2019-09-10 17:29:55.059000', '2021-09-10 17:29:55.059000',daterange('2019-09-10','2021-09-10'));