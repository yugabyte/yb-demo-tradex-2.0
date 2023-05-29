#! /bin/bash

tradexdb_container_name=tradex-db
tradexdb_initializer_name=tradex-db-initializer
tradexdb_migrator_container=tradex-db-migrator

docker stop $tradexdb_container_name

docker run --rm -d --name $tradexdb_container_name --hostname $tradexdb_container_name \
         -p7000:7000 -p9000:9000 -p5433:5433 -p9042:9042 \
         -v ~/yb_data:/home/yugabyte/yb_data \
         yugabytedb/yugabyte:latest bin/yugabyted start \
         --base_dir=/home/yugabyte/yb_data --daemon=false

echo "YB Database started"
sleep 5


echo "Build db initializer"

docker build -f ./Dockerfile-Initializer -t tradex/db-initializer .

docker run --rm -it \
    --name $tradexdb_initializer_name \
    --hostname $tradexdb_initializer_name \
    --link $tradexdb_container_name:$tradexdb_container_name \
    tradex/db-initializer 

sleep 5






