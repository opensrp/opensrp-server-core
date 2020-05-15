--clear data
TRUNCATE TABLE core.identifier_source;

ALTER SEQUENCE core.identifier_source_id_seq RESTART WITH 2;


insert into core.identifier_source(id,identifier,description,identifier_validator_algorithm,base_character_set,
								  first_identifier_base,prefix,suffix,min_length,max_length,blacklisted)
								  values
								  (1, 'Test Identifier', 'Test', 'Luhn Check Digit Algorithm', 'baseCharacterSet',
								  'firstIdentifierBase','1','A','5','10','blacklistedRegex');
