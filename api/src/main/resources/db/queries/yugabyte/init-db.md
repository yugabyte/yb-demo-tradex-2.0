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

