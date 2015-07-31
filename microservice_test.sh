#!/usr/bin/env bash

java -server -XX:+UseG1GC -jar build/libs/microservice_test-1.0-SNAPSHOT-all.jar
