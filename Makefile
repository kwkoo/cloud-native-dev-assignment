BASE=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

PROJECT=user2-freelance
PROJ_DB_USERNAME=mongo
PROJ_DB_PASSWORD=mongo

.PHONY: deployproject projectcm mongo postgres clean

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

postgres:
	-oc new-project $(PROJECT)
	oc process \
	  -f freelancer/yaml/freelancer-service-postgresql-persistent.yaml \
	  -p FREELANCER_DB_USERNAME=jboss \
	  -p FREELANCER_DB_PASSWORD=jboss \
	  -p FREELANCER_DB_NAME=freelancerdb \
	| \
	oc apply -f - -n $(PROJECT)

clean:
	-oc delete project $(PROJECT)