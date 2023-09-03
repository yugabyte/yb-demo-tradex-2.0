```bash
export YB_HOST=<YB_HOST>

ysqlsh -h $YB_HOST -f init.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f drop.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f schema.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f schema-geo-part-tablespace.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f schema-geo-part.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f views.sql
ysqlsh -h $YB_HOST -d uranus -U uranus -f data.sql

```


DEV
```bash

export YB_HOST=127.0.0.1

yb-ctl destroy

yb-ctl create --rf 3 --tserver_flags="cql_nodelist_refresh_interval_secs=2" --master_flags="tserver_unresponsive_timeout_ms=2000" --num_shards_per_tserver=1 --placement_info="aws.us-east-1.us-east-1a,aws.us-east-2.us-east-2a,aws.us-west-1.us-west-1a"

yb-ctl add_node --placement_info "aws.us-west-2.us-west-2a"

ysqlsh -h $YB_HOST -c 'create database tradex_sr'
ysqlsh -h $YB_HOST -c 'create database tradex_mr'
ysqlsh -h $YB_HOST -c 'create database tradex_mrrr'
ysqlsh -h $YB_HOST -c 'create database tradex_geo'

# SR
ysqlsh -h $YB_HOST -d tradex_sr  -f drop.sql
ysqlsh -h $YB_HOST -d tradex_sr  -f schema.sql
ysqlsh -h $YB_HOST -d tradex_sr  -f views.sql
ysqlsh -h $YB_HOST -d tradex_sr  -f data.sql

# MR
ysqlsh -h $YB_HOST -d tradex_mr  -f drop.sql
ysqlsh -h $YB_HOST -d tradex_mr  -f schema.sql
ysqlsh -h $YB_HOST -d tradex_mr  -f views.sql
ysqlsh -h $YB_HOST -d tradex_mr  -f schema-geo-part-tablespace.sql
ysqlsh -h $YB_HOST -d tradex_mr  -f schema-geo-part.sql
ysqlsh -h $YB_HOST -d tradex_mr  -f data.sql

# MRRR
ysqlsh -h $YB_HOST -d tradex_mrrr  -f drop.sql
ysqlsh -h $YB_HOST -d tradex_mrrr  -f schema.sql
ysqlsh -h $YB_HOST -d tradex_mrrr  -f views.sql
ysqlsh -h $YB_HOST -d tradex_mrrr  -f data.sql

# GEO
ysqlsh -h $YB_HOST -d tradex_geo  -f drop.sql
ysqlsh -h $YB_HOST -d tradex_geo  -f schema.sql
ysqlsh -h $YB_HOST -d tradex_geo  -f schema-geo-part-tablespace.sql
ysqlsh -h $YB_HOST -d tradex_geo  -f schema-geo-part.sql
ysqlsh -h $YB_HOST -d tradex_geo  -f views.sql
ysqlsh -h $YB_HOST -d tradex_geo  -f data.sql

```
