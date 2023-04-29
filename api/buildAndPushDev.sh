#! /bin/bash

if [ $# -lt 1 ]
  then
    echo "No arguments supplied."
    echo "Usage: ./buildAndPushDev.sh <version>"
    exit 1
fi


IMG_TAG=ssaranga/tradex-app-dev:$1

docker build . -t $IMG_TAG;sleep 3; docker push $IMG_TAG


