## Cloud Setup

This document describes steps to set up application for cloud env.


### Pre-requisites

1. Database clusters are created and access provided. ( For GEO-Partitioned, tablespaces are also created)
2. YB API Server Credentials are provided.
3. JDK 17 and maven installed

### Database Schema Creation

We use flyway docker image to apply db modifications.
sql files are in migrations directory.

```bash
cd db
source ./.env
docker build . -t $IMG_TAG
./buildDBSchema.sh envfile info|migrate
```

- envfile -- corresponds to each db cluster type.
- info -- to display current state
- migrate -- to actually run sql files.

we have to execute buildDBSchema once for each cluster type.
```
./buildDBSchema.sh singleRegionDB.env migrate
./buildDBSchema.sh multiRegionDB.env migrate
./buildDBSchema.sh readReplicaDB.env migrate
./buildDBSchema.sh geoDB.env migrate
```

Sample Env File: singleRegionDB.env

```bash
FLYWAY_URL=jdbc:postgresql://wolfsden:5433/uranus
FLYWAY_USER=uranus
FLYWAY_PASSWORD=uranus123#
FLYWAY_SCHEMA=public
FLYWAY_CLEAN_DISABLED=false
```


For GEO-DB, we drop and recreate user and trade-orders table with partitioning enabled.
We can run geoDBSchemaUpdate.sql via ysqlsh or any db client.
```bash
ysqlsh -h geodbip -f geoDBSchemaUpdate.sql
```


Connect to each db and verify, you may run select over flyway_schema_history table.

### Application

```bash
cd api
source .env
mvn clean package
docker build . -t $APP_IMAGE_NAME:latest
```
In order to run the application below environment variables are required.

| NAME | VALUE | DESCRIPTION |
| --- | --- | --- |
|APP_INSTANCE_LOCATION|BOSTON| represents one of user locations ( BOSTON, LONDON, WASHINGTON, MUMBAI, SYDNEY ) |
|APP_SINGLE_DB_HOST|srmz-dbserver.myworld||
|APP_SINGLE_DB_TP_KEYS|aws.us-east-1.us-east-1a,aws.us-east-1.us-east-1b,aws.us-east-1.us-east-1c||
|APP_MULTI_REGION_DB_HOST|mr-dbserver.myworld||
|APP_MULTI_REGION_DB_TP_KEYS|aws.us-east-1.us-east-1a,aws.us-east-2.us-east-2a,aws.us-west-1.us-west-1a||
|APP_MULTI_REGION_READ_REPLICA_DB_HOST|mrrr-dbserver.myworld|
|APP_MULTI_REGION_READ_REPLICA_DB_TP_KEYS|aws.ap-south-1.ap-south-1a,aws.us-east-1.us-east-1c,aws.us-west-1.us-west-1a|
|APP_GEO_PART_DB_HOST|geo-dbserver.myworld|
|APP_GEO_PART_DB_TP_KEYS|aws.ap-south-1.ap-south-1a,aws.ap-southeast-2.ap-southeast-2a,aws.eu-west-2.eu-west-2a,aws.us-east-1.us-east-1a,aws.us-west-2.us-west-2a|
|APP_DB_LOADBALANCE|true|
|APP_GEO_PART_DB_PWD|strongpwd|
|APP_GEO_PART_DB_URL|jdbc:yugabytedb://geo-dbserver.myworld:5433/yugabyte|
|APP_GEO_PART_DB_USER|yugabyte|
|APP_MULTI_REGION_DB_PWD|strongpwd|
|APP_MULTI_REGION_READ_REPLICA_DB_PWD|strongpwd|
|APP_MULTI_REGION_READ_REPLICA_DB_URL|jdbc:yugabytedb://mrrr-dbserver.myworld:5433/yugabyte|
|APP_MULTI_REGION_READ_REPLICA_DB_USER|yugabyte|
|APP_SINGLE_DB_PWD|strongpwd|
|APP_SINGLE_DB_URL|jdbc:yugabytedb://srmz-dbserver.myworld:5433/yugabyte|
|APP_SINGLE_DB_USER|yugabyte|
|YB_API_HOST|https://yb-apiserver.myworld|
|YB_API_CUST|XXXXX-ef7d-44dd-a05a-XXXXX|
|YB_API_TOKEN|XXXX-f46a-43fb-a582-XXXXX|
|SPRING_PROFILES_ACTIVE|SINGLE,MR,MRR,GEO|
|APP_LOAD_MOCK_DATA| true | default : true, required: false, to control user trade order creation. |

We may add these variables to a file and pass them to docker run cmd like below

```
docker run -v /home/ubuntu/tls.p12:/app/tls.p12:ro --restart=always --pull=always -d --name tradex-app \
  -e APP_INSTANCE_LOCATION=$APP_INSTANCE_LOCATION  --env-file /home/ubuntu/tradex-scripts/tradex-app-env --hostname tradex-app -p 443:443 $APP_PROD_DOCKER_IMG_TAG \
    --server.ssl.protocol=TLS --server.ssl.enabled-protocols=TLSv1.2 --server.ssl.enabled=true --server.ssl.key-store-type=PKCS12 --server.ssl.key-store=/app/tls.p12 --server.ssl.key-store-password=changeit --server.port=443
```


###   Default Users

 In the sql migrations applied, we have created 5 users. below are credentails.

 |USER|PASSWORD|
 |---|---|
 |mickey@tradex.com|mickey123|
 |donald@tradex.com|donald123|
 |scrooge@tradex.com|scrooge123|
 |leo@tradex.com|leo123|
 |sally@trade.com|sally123|

 Note: On the login page, you may reset password. it will be reset to default ( email+123 . e.g 'mickey@tradex.com123' )

 You can create more users using signup page in login screen.
 Users will be propagated to all DB clusters.


### Trade Order data generation.

In order to simulate user trade orders, we are generating random data for 2 users ( `mickey` and `donald` ).

If the property `APP_LOAD_MOCK_DATA` is set to true and instance location is boston. Trade orders are generated and inserted into all four db cluster types.

However trade order data is generated only for 2 users mickey, donald whenever application is started.






