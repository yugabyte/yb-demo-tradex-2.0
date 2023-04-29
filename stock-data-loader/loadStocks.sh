#! /bin/bash

docker run --rm --env-file ./.localdb-env ssaranga/local-tradex-db-loader-2:latest  
