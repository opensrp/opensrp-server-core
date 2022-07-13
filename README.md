# opensrp-server-core

[![Build Status](https://travis-ci.org/OpenSRP/opensrp-server-core.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-server-core)
[![Coverage Status](https://coveralls.io/repos/github/opensrp/opensrp-server-core/badge.svg?branch=v2)](https://coveralls.io/github/opensrp/opensrp-server-core?branch=v2)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/97b0f387f0fa484caffea641f4762fbe)](https://www.codacy.com/app/OpenSRP/opensrp-server-core?utm\_source=github.com\&utm\_medium=referral\&utm\_content=OpenSRP/opensrp-server-core\&utm\_campaign=Badge\_Grade)

Postgres, couchdb, lucene and scheduler domain objects, repositories and services

## Relevant Wiki Pages

* OpenSRP Server Refactor and Cleanup
  * [Refactor and Cleanup](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/562659330/OpenSRP+Server+Refactor+and+Clean+up)
  * [How to upload and use maven jar artifacts](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/564428801/How+to+upload+and+use+maven+jar+artifacts)
  * [Managing Server Wide Properties](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/602570753/Managing+Server+Wide+Properties)
  * [Server Web Build](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/616595457/Server+Web+Build)
* [OpenSRP Server Build](https://smartregister.atlassian.net/wiki/display/Documentation/OpenSRP+Server+Build)
* Deployment
  * [Docker Setup](https://smartregister.atlassian.net/wiki/display/Documentation/Docker+Setup)
  * [Docker Compose Setup](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/52690976/Docker+Compose+Setup)
  * [Ansible Playbooks](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/540901377/Ansible+Playbooks)
* [Postgres Database Support](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/251068417/Postgres+Database+Support+as+Main+Datastore)
* [OpenSRP Load Testing](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/268075009/OpenSRP+Load+Testing)

## Running Integration Tests

We use [test-containers](https://github.com/testcontainers/testcontainers-java) to create ephemeral instances of the
postgres database and rabbitmq server for the integration tests. This means that one does not have to set up any
instance to run the integration tests on one's machine.

### How it works

When an integration test is run/executed:

* Test-Containers will pull a docker image and/or start a docker container needed.
* Test-Containers will then provide a dynamic port to connect to, from the hosts network.
* A single instance is created for all the tests.
* The docker container will be killed once all the tests have been executed.
