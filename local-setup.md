# Local Demo Setup

## Pre-requsites

- Docker
- JDK17
- maven

# Setup Database

```bash
   cd db
   ./local-bootstrap-db.sh
```

this will start a yugabyte database, create roles and perform migration using flyway.

### Run Application cmd line

```bash
    cd api
    mvn clean package

    source api/env-local

    java -jar api/target/tradex-api-*.jar
```
to run from IDE

Run TradeXApiApplication class by passing env variables in env-local file.

Note: env variables template file present in api/env-local-template
