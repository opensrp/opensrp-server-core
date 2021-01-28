--clear data
TRUNCATE TABLE core.product_catalogue CASCADE ;

ALTER SEQUENCE core.product_catalogue_unique_id_seq RESTART WITH 2;

insert into core.product_catalogue(unique_id,product_name, json, server_version)
								  values
								  (1, 'Midwifery Kit','{
    "productName": "Midwifery Kit",
    "isAttractiveItem": true,
    "materialNumber":"AX-123",
    "availability": "available",
    "condition": "yes",
    "appropriateUsage": "yes",
    "accountabilityPeriod": 10,
    "serverVersion": 123344
}',123344);


SELECT setval('core.product_catalogue_server_version_seq',(SELECT max(json->>'serverVersion')::bigint+1 FROM core.product_catalogue));
