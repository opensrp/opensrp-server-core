--clear data
TRUNCATE TABLE team.organization CASCADE;

INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (1, 'fcc19470-d599-11e9-bb65-2a2ae2dbcce4', true, 'The Luang', '{"coding": [{"code": "team", "system": "http://terminology.hl7.org/CodeSystem/organization-type", "display": "Team"}]}', null, null);
INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (3, '4c506c98-d3a9-11e9-bb65-2a2ae2dbcce4', true, 'Demo Team', null, null, null);
INSERT INTO team.organization (id, identifier, active, name, type, date_deleted, parent_id) VALUES (2, 'd23f7350-d406-11e9-bb65-2a2ae2dbcce4', true, 'Takang 1', '{"coding": [{"code": "team", "system": "http://terminology.hl7.org/CodeSystem/organization-type", "display": "Team"}]}', null, 1);