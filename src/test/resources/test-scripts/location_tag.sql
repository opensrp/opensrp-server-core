--clear data
TRUNCATE TABLE core.location_tag CASCADE;

-- insert data
INSERT INTO core.location_tag (id,  active, name, description) VALUES
(1,'Ward',TRUE,'ward label tag'),
(2,'Block',TRUE,'block label tag');
