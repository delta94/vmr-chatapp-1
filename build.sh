#! /bin/bash

# build backend
cd vmr-newbackend
mvn clean package
cd ..
cp vmr-newbackend/target/vmr-newbackend-1.0-SNAPSHOT.jar vmr-docker/vmr-backend/
mv vmr-docker/vmr-backend/vmr-newbackend-1.0-SNAPSHOT.jar vmr-docker/vmr-backend/bundle.jar
cp vmr-newbackend/src/main/resources/* vmr-docker/vmr-backend/configs
