FROM flyway/flyway:latest
ADD  --chown=2000:2000 https://repo1.maven.org/maven2/com/yugabyte/jdbc-yugabytedb/42.3.5-yb-2/jdbc-yugabytedb-42.3.5-yb-2.jar drivers/
COPY migrations/*.sql /flyway/sql
