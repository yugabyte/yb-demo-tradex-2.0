/*
yb-ctl create --rf 1 --placement_info="aws.eu-west-2.eu-west-2a"
yb-ctl add_node --placement_info "aws.ap-south-1.ap-south-1a"
yb-ctl add_node --placement_info="aws.ap-southeast-2.ap-southeast-2a"
yb-ctl add_node --placement_info="aws.us-east-1.us-east-1a"
yb-ctl add_node --placement_info="aws.ap-us-west-2.us-west-2a"
ysqlsh -c 'create database tradex_geo;'
ysqlsh -d 'tradex_geo' -f db/local-geopart.sql
*/

CREATE TABLESPACE london_tablespace WITH (
  replica_placement='{
    "num_replicas": 1,
    "placement_blocks": [
      {
        "cloud":"aws",
        "region":"eu-west-2",
        "zone":"eu-west-2a",
        "min_num_replicas":1
      }
    ]
  }'
  );

CREATE TABLESPACE mumbai_tablespace WITH (
  replica_placement='{
    "num_replicas": 1,
    "placement_blocks": [
      {
        "cloud":"aws",
        "region":"ap-south-1",
        "zone":"ap-south-1a",
        "min_num_replicas":1
      }
    ]
  }'
  );

CREATE TABLESPACE sydney_tablespace WITH (
  replica_placement='{
    "num_replicas": 1,
    "placement_blocks": [
      {
        "cloud":"aws",
        "region":"ap-southeast-2",
        "zone":"ap-southeast-2a",
        "min_num_replicas":1
      }
    ]
  }'
  );

CREATE TABLESPACE washington_tablespace WITH (
  replica_placement='{
    "num_replicas": 1,
    "placement_blocks": [
      {
        "cloud":"aws",
        "region":"us-west-2",
        "zone":"us-west-2a",
        "min_num_replicas":1
      }
  ]}'
  );

CREATE TABLESPACE boston_tablespace WITH (
  replica_placement='{
    "num_replicas": 1,
    "placement_blocks": [
      {
        "cloud":"aws",
        "region":"us-east-1",
        "zone":"us-east-1a",
        "min_num_replicas":1
      }
    ]
  }'
  );
