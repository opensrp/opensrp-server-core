--clear data
TRUNCATE TABLE core.sms_api_processing_status CASCADE;

ALTER SEQUENCE core.sms_api_processing_status_id_seq RESTART WITH 4;

--insert data
INSERT INTO core.sms_api_processing_status (id, base_entity_id, event_type, service_type, request_status, date_created, last_updated, sms_delivery_status, attempts, sms_delivery_date) VALUES
(1,'304cbcd4-0850-404a-a8b1-486b02f7b84d','CHILD REGISTRATION','REGISTRATION','NEW','2021-04-04T10:20:00','2021-04-04T10:20:00', 'QUEUED', 0, NULL),
(2,'000b02f7b85e-404a-404a-a8b1-123cbcd4','CHILD HOME VISIT','VISIT','NEW','2021-04-04T10:30:00','2021-04-04T10:30:00','QUEUED',0,NULL),
(3,'123cbcd4-0851-404a-a8b2-000b02f7b85e','CHILD REMOVED','REMOVAL','QUEUED','2021-04-04T10:45:00','2021-04-04T10:45:00','SENT',1,'2021-04-04T10:45:00');
