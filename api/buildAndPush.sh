#! /bin/bash

if [ $# -lt 1 ]
  then
    echo "No arguments supplied."
    echo "Usage: ./buildAndPushFinalImage.sh <version>"
    exit 1
fi

source .env

IMG_TAG=$APP_IMAGE_NAME:$1

docker build . -t $IMG_TAG;sleep 3; docker push $IMG_TAG


