--clear data
TRUNCATE TABLE core.location_tag CASCADE;

-- insert data
INSERT INTO core.location_tag (name,active,description) VALUES
('Ward',TRUE,'ward label tag'),
('Block',TRUE,'block label tag');
