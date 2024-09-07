#!/bin/bash
version="3.0.4"
./gradlew clean &&

./gradlew build -x test  &&

docker build --platform linux/amd64 -t muhohoweb/students-image:$version . &&

docker push muhohoweb/students-image:$version