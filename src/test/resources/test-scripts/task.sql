--clear data
TRUNCATE TABLE core.task CASCADE;

ALTER SEQUENCE core.task_id_seq RESTART WITH 6;

ALTER SEQUENCE core.task_metadata_id_seq RESTART WITH 6;


INSERT INTO core.task (id, json) VALUES (4, '{"code": "IRS", "focus": "IRS Visit", "note": [{"text": "This should be assigned to patrick.", "time": "2018-01-01T08:00:00.000", "authorString": "demouser"}], "owner": "demouser", "status": "Ready", "priority": 3, "for": "location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "authoredOn": "2018-10-31T07:00:00.000", "identifier": "tsk11231jh22", "description": "Spray House", "lastModified": "2018-11-12T15:50:57.055", "serverVersion": 1542027762554, "businessStatus": "Not Visited", "groupIdentifier": "2018_IRS-3734", "planIdentifier": "IRS_2018_S1", "executionStartDate": "2018-11-10T22:00:00.000"}');
INSERT INTO core.task (id, json) VALUES (5, '{"code": "IRS", "focus": "IRS Visit", "note": [{"text": "This should be assigned to patrick.", "time": "2018-01-01T08:00:00.000", "authorString": "demouser"}], "owner": "demouser", "status": "Ready", "priority": 3, "for": "location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "authoredOn": "2018-11-12T17:05:43.987", "identifier": "iyr-998njoo", "description": "Spray House", "lastModified": "2018-11-12T17:05:43.987", "businessStatus": "Not Visited", "groupIdentifier": "2018_IRS-3734", "planIdentifier": "IRS_2018_S2", "executionStartDate": "2018-11-10T22:00:00.000"}');


INSERT INTO core.task_metadata (id, task_id, identifier, plan_identifier, group_identifier, for_entity, server_version, owner, date_created) VALUES (1, 4, 'tsk11231jh22', 'IRS_2018_S1', '2018_IRS-3734', 'location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc', 1542027762554,'demouser','2020-09-25T10:00:00');
INSERT INTO core.task_metadata (id, task_id, identifier, plan_identifier, group_identifier, for_entity, server_version, owner, date_created) VALUES (2, 5, 'iyr-998njoo', 'IRS_2018_S2', '2018_IRS-3734', 'location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc', 1542031602680,'demouser','2020-09-26T10:00:00');
