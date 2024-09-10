#!/bin/bash
version="3.0.6"
./gradlew clean &&

./gradlew build -x test  &&

docker build --platform linux/amd64 -t muhohoweb/students-image:$version . &&

docker push muhohoweb/students-image:$version &&

git add . && git commit -m "Add DOCKER_IMAGE_NAME env" && git push origin main