--clear data
TRUNCATE TABLE core.location_tag_map CASCADE;

-- insert data
INSERT INTO core.location_tag_map (location_id,location_tag_id) VALUES
(1,1),
(2,2);
