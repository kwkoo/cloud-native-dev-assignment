BASE=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

PROJECT=user2-freelance
PROJ_DB_USERNAME=mongo
PROJ_DB_PASSWORD=mongo
FREELANCE_DB_USERNAME=jboss
FREELANCE_DB_PASSWORD=jboss
GATEWAY=http://$(shell oc get route/gateway-service -n $(PROJECT) --template='{{.spec.host}}')

.PHONY: all deployproject projectcm mongo deployfreelancer postgres deploygateway clean curl

all: mongo deployproject postgres deployfreelancer deploygateway
	echo "Done"

deployproject: projectcm
	-oc new-project $(PROJECT)
	oc policy add-role-to-user view -z default -n $(PROJECT)
	cd project \
	&& \
	mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$(PROJECT) -Dfabric8.openshift.deployTimeoutSeconds=120 -DskipTests

projectcm:
	-oc delete configmap/project-service -n $(PROJECT)
	sed -i '' 's/username: .*/username: "$(PROJ_DB_USERNAME)"/' project/etc/app-config.yml
	sed -i '' 's/password: .*/password: "$(PROJ_DB_PASSWORD)"/' project/etc/app-config.yml
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
	sed -i '' 's/user: .*/user: "$(FREELANCE_DB_USERNAME)"/' freelancer/src/main/fabric8/credentials-secret.yml
	sed -i '' 's/password: .*/password: "$(FREELANCE_DB_PASSWORD)"/' freelancer/src/main/fabric8/credentials-secret.yml
	cd freelancer \
	&& \
	mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$(PROJECT) -Dfabric8.openshift.deployTimeoutSeconds=120 -DskipTests

postgres:
	-oc new-project $(PROJECT)
	oc new-app \
	  -e POSTGRESQL_USER=$(FREELANCE_DB_USERNAME) \
	  -e POSTGRESQL_PASSWORD=$(FREELANCE_DB_PASSWORD) \
	  -e POSTGRESQL_DATABASE=freelancerdb \
	  centos/postgresql-10-centos7 \
	  --name=freelancer-postgresql \
	  -n $(PROJECT)

gatewaycm:
	-oc delete configmap/gateway-service -n $(PROJECT)
	oc create configmap gateway-service --from-file=gateway/etc/project-defaults.yml -n $(PROJECT)

deploygateway: gatewaycm
	-oc new-project $(PROJECT)
	cd gateway \
	&& \
	mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$(PROJECT) -Dfabric8.openshift.deployTimeoutSeconds=120 -DskipTests

clean:
	-oc delete project $(PROJECT)

curl:
	@echo "Testing /gateway/freelancers"
	curl $(GATEWAY)/gateway/freelancers
	@echo
	@echo
	@echo "Testing /gateway/freelancers/1"
	curl $(GATEWAY)/gateway/freelancers/1
	@echo
	@echo
	@echo "Testing /gateway/projects"
	curl $(GATEWAY)/gateway/projects
	@echo
	@echo
	@echo "Testing /gateway/projects/1"
	curl $(GATEWAY)/gateway/projects/1
	@echo
	@echo
	@echo "Testing /gateway/projects/status/open"
	curl $(GATEWAY)/gateway/projects/status/open
	@echo
