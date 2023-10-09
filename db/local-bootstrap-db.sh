#!/usr/bin/env bash

set -Eeuo pipefail
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_DIR=$(cd $SCRIPT_DIR/..; pwd)

yb_image=${yb_image:-yugabytedb/yugabyte:latest}
yb_container=tradex-yb

yb_client_image=${yb_client_image:-yugabytedb/yugabyte-client:latest}
yb_client_container=tradex-yb-client

db_initializer_image=db-initializer
db_initializer_container=tradexdb-initializer

db_migration_image=${db_migration_image:-tradex-db:latest}
db_migration_container=tradex-migration



function db_create(){
  echo Run YugabtyeDB
  docker rm -f $yb_container || true
  rm -rf $PROJECT_DIR/yb_data || true
  DOCKER_DEFAULT_PLATFORM=linux/amd64 docker run \
    --rm \
    -d \
    --name $yb_container \
    --hostname $yb_container \
    -p 7000:7000 -p 9000:9000 -p 5433:5433 -p 9042:9042 -p 13000:13000 \
    -v $PROJECT_DIR/yb_data:/home/yugabyte/yb_data \
    $yb_image \
    bin/yugabyted start --base_dir=/home/yugabyte/yb_data --daemon=false

  echo "Wait 20 sec for container to initialize"
  sleep 20
  echo "YB Database started"
}

function roles_init(){
  echo "Init DB Roles"
  ysql -f /db/local-init.sql
}

function migration_build(){
  echo "Build migration docker image"
  docker build -f $PROJECT_DIR/db/Dockerfile -t $db_migration_image $PROJECT_DIR/db
}

function initializer_build(){
  echo "Build initializer docker image"
  docker build -f $PROJECT_DIR/db/Dockerfile-Initializer -t $db_initializer_image $PROJECT_DIR/db
}

function initializer_run(){
docker run --rm -it -e DB_HOST=$yb_container \
    --name $db_initializer_container \
    --hostname $db_initializer_container \
    --link $yb_container:$yb_container \
    $db_initializer_image
}

function migration_run(){
  _migrate_db mercury mercury 'mercury123#'
  _migrate_db venus venus 'venus123#'
  _migrate_db uranus uranus 'uranus123#'
  _migrate_db neptune neptune 'neptune123#'
}

function yb_client(){
  DOCKER_DEFAULT_PLATFORM=linux/amd64 docker run \
    --rm -it \
    --name $yb_client_container \
    --hostname $yb_client_container \
    --link $yb_container:$yb_container \
    -v $PROJECT_DIR/db:/db \
    $yb_client_image $@
}

function ysql(){
  yb_client ysqlsh -h $yb_container -U yugabyte "$@"
}

function _migrate_db(){
  db=$1;shift
  user=$1;shift
  password=$1; shift

  echo Pre migrating $db
  flyway info -user=$user -password="$password" -url=jdbc:postgresql://$yb_container:5433/$db

  echo Migrating $db
  flyway migrate -user=$user -password="$password" -url=jdbc:postgresql://$yb_container:5433/$db

  echo Post migrating $db
  flyway info -user=$user -password="$password" -url=jdbc:postgresql://$yb_container:5433/$db
}

function flyway(){
  docker run --rm -it --name $db_migration_container --hostname $db_migration_container \
    --link $yb_container:$yb_container $db_migration_image  \
    $@
}

function bootstrap(){
  echo "Bootstraping local db"
  db_create
 # roles_init
 initializer_build
 initializer_run

  migration_build
  migration_run
}


OP=${1:-bootstrap}; shift || true
$OP "$@"
