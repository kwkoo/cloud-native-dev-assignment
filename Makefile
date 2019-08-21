BASE=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

PROJECT=user2-freelance
PROJ_DB_USERNAME=mongo
PROJ_DB_PASSWORD=mongo

.PHONY: deployproject projectcm mongo deployfreelancer postgres clean

deployproject: projectcm
	-oc new-project $(PROJECT)
	oc policy add-role-to-user view -z default -n $(PROJECT)
	cd project \
	&& \
	mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$(PROJECT) -Dfabric8.openshift.deployTimeoutSeconds=120 -DskipTests

projectcm:
	-oc delete configmap/project-service -n $(PROJECT)
	oc create configmap project-service --from-file=project/etc/app-config.yml -n $(PROJECT)

mongo:
	-oc new-project $(PROJECT)
	oc process \
	  -f project/yaml/project-mongodb.yaml \
	  -p PROJECT_DB_USERNAME=$(PROJ_DB_USERNAME) \
	  -p PROJECT_DB_PASSWORD=$(PROJ_DB_PASSWORD) \
	| \
	oc apply -f - -n $(PROJECT)

deployfreelancer:
	-oc new-project $(PROJECT)
	cd freelancer \
	&& \
	mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$(PROJECT) -Dfabric8.openshift.deployTimeoutSeconds=120 -DskipTests

postgres:
	-oc new-project $(PROJECT)
	oc new-app \
	  -e POSTGRESQL_USER=jboss \
	  -e POSTGRESQL_PASSWORD=jboss \
	  -e POSTGRESQL_DATABASE=freelancerdb \
	  centos/postgresql-10-centos7 \
	  --name=freelancer-postgresql \
	  -n $(PROJECT)

clean:
	-oc delete project $(PROJECT)