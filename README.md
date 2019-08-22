# Homework Assignment Submission

* Name: Koo, Kin Wai
* Email: kkoo@redhat.com
* Instructor: Chád Darby
* Class: Advanced Cloud Native Development
* Date: 19-23 Aug 2019

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
