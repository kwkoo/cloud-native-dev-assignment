# Homework Assignment Submission

* Name: Koo, Kin Wai
* Email: kkoo@redhat.com
* Instructor: Chád Darby
* Class: Advanced Cloud Native Development
* Date: 19-23 Aug 2019
* Source code: <https://github.com/kwkoo/cloud-native-dev-assignment>

## Overview

Each of the 3 services is in its own directory.

* API Gateway - `gateway`
* Project Service - `project`
* Freelancer Service - `freelancer`

To deploy this project,

* Edit the `Makefile` and set the variables at the top of the file.
* Make sure the `oc` executable is in your `PATH` and that you're logged in.
* Run the `make` command.
* Please note that this `Makefile` only works on Mac OS X due to `sed`'s peculiar syntax on the Mac.

After the services have been deployed, you can test the API gateway service by executing `make curl`.

## Project Service

* Configuration
	* The database connection details are stored in the `project-service` configmap. This is picked up by `io.vertx.config.ConfigRetriever` in `com.freelance.project.verticle.MainVerticle.start()`.
* The following commands can be used to test the project service.

````
export BASEURL=http://$(oc get route/project-service --template='{{.spec.host}}')

curl $BASEURL/projects

curl $BASEURL/project/1

curl $BASEURL/projects/status/open
````

## Freelancer Service

* Configuration
	* The database credentials are stored in the `my-database-secret` secret. These show up in the `DB_USERNAME` and `DB_PASSWORD` environment variables. `freelancer/src/main/resources/application-openshift.properties` sets up a spring datasource based on the values in those environment variables.
* The following commands can be used to test the freelancer service.

````
export BASEURL=http://$(oc get route/freelancer --template='{{.spec.host}}')

curl $BASEURL/freelancers

curl $BASEURL/freelancers/1
````


## API Gateway

* Configuration
	* The URLs to the backend services are set in the `gateway-service` configmap. The values are injected into variables in `com.freelance.rest.Gateway`.
* The following commands can be used to test the API gateway.

````
export BASEURL=http://$(oc get route/gateway-service --template='{{.spec.host}}')

curl $BASEURL/gateway/freelancers

curl $BASEURL/gateway/freelancers/1

curl $BASEURL/gateway/projects

curl $BASEURL/gateway/projects/1

curl $BASEURL/gateway/projects/status/open
````