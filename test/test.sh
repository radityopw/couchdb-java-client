#!/bin/bash

cd ../build
pwd
java -cp ".:../lib/json-java.jar" radityopw.couchdbclient.CouchdbClient
#cd ../test
