# cf-service-broker-redis
Cloud Foundry Service Broker providing Redis Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added. 


# Getting started for a new Broker
Create a Repository and import the following submodules:

* `git submodule add git@github.com:evoila/osb-backup-core.git osb-backup-core`
* `git submodule add git@github.com:evoila/cf-service-broker-dashboard.git osb-dashboard`
* `git submodule add git@github.com:evoila/cf-service-broker-persistence.git osb-persistence`
* `git submodule add git@github.com:evoila/cf-service-broker-deployment.git osb-deployment`
* `git submodule add git@github.com:evoila/cf-service-broker-core.git osb-core`

Once checked out and with some existing stuff, you may want to reload all the dependencies before you
continue with:

* `git submodule update --recursive --remote`





