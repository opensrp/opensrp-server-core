# opensrp-server-core

![Build Status](https://github.com/opensrp/opensrp-server-core/actions/workflows/ci.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/opensrp/opensrp-server-core/badge.svg?branch=v2)](https://coveralls.io/github/opensrp/opensrp-server-core?branch=v2)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a149805a96c547acaa3bca1d25858e0b)](https://www.codacy.com/gh/opensrp/opensrp-server-core/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-server-core&amp;utm_campaign=Badge_Grade)

Postgres, couchdb, lucene and scheduler domain objects, repositories and services

## Relevant Wiki Pages

<<<<<<< HEAD

=======
>>>>>>> 9848f012 (update to remove conflicts)
* OpenSRP Server Refactor and Cleanup
  * [Refactor and Cleanup](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/562659330/OpenSRP+Server+Refactor+and+Clean+up)
  * [How to upload and use maven jar artifacts](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/564428801/How+to+upload+and+use+maven+jar+artifacts)
  * [Managing Server Wide Properties](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/602570753/Managing+Server+Wide+Properties)
  * [Server Web Build](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/616595457/Server+Web+Build)
<<<<<<< HEAD

* [OpenSRP Server Build](https://smartregister.atlassian.net/wiki/display/Documentation/OpenSRP+Server+Build)


=======
* [OpenSRP Server Build](https://smartregister.atlassian.net/wiki/display/Documentation/OpenSRP+Server+Build)
>>>>>>> 9848f012 (update to remove conflicts)
* Deployment
  * [Docker Setup](https://smartregister.atlassian.net/wiki/display/Documentation/Docker+Setup)
  * [Docker Compose Setup](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/52690976/Docker+Compose+Setup)
  * [Ansible Playbooks](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/540901377/Ansible+Playbooks)
<<<<<<< HEAD


=======
>>>>>>> 9848f012 (update to remove conflicts)
* [Postgres Database Support](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/251068417/Postgres+Database+Support+as+Main+Datastore)
* [OpenSRP Load Testing](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/268075009/OpenSRP+Load+Testing)

## Running Integration Tests

We use [test-containers](https://github.com/testcontainers/testcontainers-java) to create ephemeral instances of the
postgres database and rabbitmq server for the integration tests. This means that one does not have to set up any
instance to run the integration tests on one's machine.

### How it works

When an integration test is run/executed:

<<<<<<< HEAD
<<<<<<< HEAD
*   Test-Containers will pull a docker image and/or start a docker container needed.
*   Test-Containers will then provide a dynamic port to connect to, from the hosts network.
*   A single instance is created for all the tests.
*   The docker container will be killed once all the tests have been executed.

## Publishing artifacts

For more on publishing this artifact see [Publishing via Tag](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/3013902337/How+to+set+up+Server+Library+artifact+CI+CD+on+Github#Publishing-via-TAG)
=======
=======
>>>>>>> 9848f012 (update to remove conflicts)
* Test-Containers will pull a docker image and/or start a docker container needed.
* Test-Containers will then provide a dynamic port to connect to, from the hosts network.
* A single instance is created for all the tests.
* The docker container will be killed once all the tests have been executed.
<<<<<<< HEAD
>>>>>>> ba07477e (update to remove conflicts)
=======
>>>>>>> 9848f012 (update to remove conflicts)
