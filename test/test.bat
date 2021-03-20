@echo off
cd ..\build
java -cp ".;..\lib\json-java.jar" radityopw.couchdbclient.CouchdbClient
cd ..\test