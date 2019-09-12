--clear data
TRUNCATE TABLE team.organization CASCADE;

-- insert data
INSERT INTO team.organization (id, identifier, active, name) VALUES
(1,'org1',TRUE,'Org 1');


INSERT INTO team.practitioner (id, identifier, active, name, user_id, username) VALUES
(1,'p1-identifier',TRUE,'first practitioner','1','p1'),
(2,'p2-identifier',TRUE,'second practitioner','2','p2');
