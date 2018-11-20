--clear data
TRUNCATE TABLE core.campaign CASCADE;

ALTER SEQUENCE core.campaign_id_seq RESTART WITH 6;

ALTER SEQUENCE core.campaign_metadata_id_seq RESTART WITH 6;


INSERT INTO core.campaign (id, json) VALUES (3, '{"owner": "jdoe", "title": "2019 IRS Season 1", "status": "In Progress", "authoredOn": "2018-10-01T09:00:00.000", "identifier": "IRS_2018_S1", "description": "This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.", "lastModified": "2018-11-12T11:49:00.782", "serverVersion": 1542012540782, "executionPeriod": {"end": "2019-03-31", "start": "2019-01-01"}}');
INSERT INTO core.campaign (id, json) VALUES (4, '{"owner": "jdoe", "title": "2019 IRS Season 2", "status": "In Progress", "authoredOn": "2018-11-12T17:05:00.585", "identifier": "IRS_2018_S2", "description": "This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Apr 2019 - 30 Jun 2019.", "lastModified": "2018-11-12T17:05:00.586", "serverVersion": 1542031500585, "executionPeriod": {"end": "2019-06-30", "start": "2019-04-01"}}');

INSERT INTO core.campaign_metadata (id, campaign_id, identifier, server_version) VALUES (3, 3, 'IRS_2018_S1', 1542012540782);
INSERT INTO core.campaign_metadata (id, campaign_id, identifier, server_version) VALUES (4, 4, 'IRS_2018_S2', 1542031500585);