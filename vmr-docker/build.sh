#! /bin/bash

# build backend
cd ../vmr-newbackend
mvn clean package
cd ..
cp vmr-newbackend/target/vmr-newbackend-1.0-SNAPSHOT.jar vmr-docker/vmr-backend/
mv vmr-docker/vmr-backend/vmr-newbackend-1.0-SNAPSHOT.jar vmr-docker/vmr-backend/bundle.jar
cp vmr-newbackend/src/main/resources/* vmr-docker/vmr-backend/configs

# build frontend
cd vmr-frontend
yarn build
cd ..
mkdir -p vmr-docker/vmr-frontend/build
cp -R vmr-frontend/build/ vmr-docker/vmr-frontend/build/
cp vmr-frontend/server.js vmr-docker/vmr-frontend
