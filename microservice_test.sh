#!/usr/bin/env bash

java -d64 -server -XX:+UseG1GC -jar build/libs/microservice_test-1.0-SNAPSHOT-all.jar
