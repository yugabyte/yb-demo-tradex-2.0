# Local Demo Setup

## Pre-requsites

- Docker
- JDK17
- maven
- ysqlsh

### Create YB Cluster

Run below commands to start a single yugabytedb/yugabyte:latest container

```bash
db/startYbDB.sh
```

**OR on M1 Mac / ARM**

```bash
DOCKER_DEFAULT_PLATFORM=linux/amd64 db/startYbDB.sh
```

You may check db is up using

```bash
docker ps
```

Note: Default username: yugabyte and password: yugabyte

### Create databases & users

Run below sql files to create database and users.

```bash
ysqlsh -h localhost -U yugabyte -f db/init-users.sql

```

Use default password: yugabyte

### Schema generation

Build database migration image using flyway

```bash
docker build db/ -t mytradex-db:latest

```

## DB Migration
Run Migration for each database. You can use `Migrate` command to perform the migration, and `Info` command to check the migration status

| DB      | Action  | Command                                                                                                                                                  |
| ------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mercury | Migrate | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest migrate -user=mercury -password='mercury123#' -url=jdbc:postgresql://yugabyte:5433/mercury` |
|         | Info    | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest info -user=mercury -password='mercury123#' -url=jdbc:postgresql://yugabyte:5433/mercury`    |
| Venus   | Migrate | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest migrate -user=venus -password='venus123#' -url=jdbc:postgresql://yugabyte:5433/venus` |
|         | Info    | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest info -user=venus -password='venus123#' -url=jdbc:postgresql://yugabyte:5433/venus`    |
| Uranus  | Migrate | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest migrate -user=uranus -password='uranus123#' -url=jdbc:postgresql://yugabyte:5433/uranus` |
|         | Info    | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest info -user=uranus -password='uranus123#' -url=jdbc:postgresql://yugabyte:5433/uranus`    |
| Neptune | Migrate | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest migrate -user=neptune -password='neptune123#' -url=jdbc:postgresql://yugabyte:5433/neptune` |
|         | Info    | `docker run --link yugabyte:yugabyte --rm mytradex-db:latest info -user=neptune -password='neptune123#' -url=jdbc:postgresql://yugabyte:5433/neptune`    |


### Run Application cmd line

```bash
    mvn clean package

    source api/env-local

    java -jar api/target/tradex-api-*.jar
```

to run from IDE

Run TradeXApiApplication class by passing env variables in env-local file.
