@echo off
cd ..\build
java -cp ".;..\lib\json-java.jar" test.Test
cd ..\test