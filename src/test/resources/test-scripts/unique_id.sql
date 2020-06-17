--clear data
--clear data
TRUNCATE TABLE core.identifier_source CASCADE ;

ALTER SEQUENCE core.identifier_source_id_seq RESTART WITH 2;

TRUNCATE TABLE core.unique_id CASCADE;

ALTER SEQUENCE core.unique_id_id_seq RESTART WITH 6;

insert into core.identifier_source(id,identifier,description,identifier_validator_algorithm,base_character_set,
								  first_identifier_base,prefix,suffix,min_length,max_length,regex_format)
								  values
								  (1, 'Identifier-1 ', 'Test', 'LUHN_CHECK_DIGIT_ALGORITHM', 'AB12',
								  'B12A','','','4','10','');

insert into core.unique_id(id,location,openmrs_id,status,used_by,updated_at,created_at,identifier,id_source,is_reserved)
values(1,null,'','not_used',null,null,CURRENT_DATE, 'AAAB-0',1,false);

insert into core.unique_id(id,location,openmrs_id,status,used_by,updated_at,created_at,identifier,id_source,is_reserved)
values(2,null,'','not_used',null,null,CURRENT_DATE, 'AAAA-1',1,false);

insert into core.unique_id(id,location,openmrs_id,status,used_by,updated_at,created_at,identifier,id_source,is_reserved)
values(3,null,'','not_used',null,null,CURRENT_DATE, 'AAAAA',null,true);
