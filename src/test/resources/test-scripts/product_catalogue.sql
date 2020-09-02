--clear data
TRUNCATE TABLE core.product_catalogue CASCADE ;

ALTER SEQUENCE core.product_catalogue_unique_id_seq RESTART WITH 2;

insert into core.product_catalogue(unique_id,product_name, type, json, server_version)
								  values
								  (1, 'Midwifery Kit','CONSUMEABLE','{"sections": ["Health","Wash"],"condition": "yes","productName": "Midwifery Kit", "productType": "CONSUMEABLE","availability": "available","serverVersion": 123344,"appropriateUsage": "yes","accountabilityPeriod": 10}',12345);
