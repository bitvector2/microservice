#!/usr/bin/env bash

if [ $(uname -m) == "armv7l" ];
then
    java -d32 -server -XX:+UseG1GC -jar build/libs/microservice_test-1.0-SNAPSHOT-all.jar
else
    java -d64 -server -XX:+UseG1GC -jar build/libs/microservice_test-1.0-SNAPSHOT-all.jar
fi
