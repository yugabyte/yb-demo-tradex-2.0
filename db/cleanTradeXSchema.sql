drop view if exists portfolio_dailyperf_7days_v;
drop view if exists portfolio_dailyperf_month_v;
drop view if exists seven_days_stock_trend_v;
drop view if exists five_days_stock_trend_v;
drop view if exists stock_period_info_v;
drop view if exists current_stocks_v;
drop table if exists trade_symbol_price_today;
drop table if exists trade_symbol_price_historic;
DROP TABLE IF EXISTS TRADE_ORDERS;
DROP TABLE IF EXISTS REF_DATA;
DROP TABLE IF EXISTS TRADE_SYMBOL;
DROP SEQUENCE IF EXISTS ORDER_ID_SEQ;
DROP SEQUENCE IF EXISTS TRADE_SYMBOL_SEQ;
DROP TABLE IF EXISTS APP_USER;
drop table if exists flyway_schema_history ;

/**
insert into REF_DATA(KEY_NAME, KEY_VALUE) values( 'DB_CLUSTER_TYPES',
'{
	"DB_CLUSTER_TYPES": [
	    {
	        "id":0,
	        
	        
			"title": "Single-region, multi-zone",
			"subtitle": "3 node deployed in US West"
		},
		{
		    "id":1,
			"title": " Multi-region",
			"subtitle": "3 nodes deployed in US West, US Central and US East"
		},
		{
		  "id":2,
			"title": " Multi-region, multi-zone with Read Replicas",
			"subtitle": "3 nodes deployed in US East, with read replicas in Europe and Asia"
		},
		{
			"id":3,
			"title": " Geo-partitioned",
			"subtitle": "3 nodes deployed in US East, with 2 nodes in Europe and Asia"
		}
    ]
}'
);
**/


--select * from trade_symbol ts ;


--alter table trade_orders drop constraint TRADE_SYMBOL_FK;
--alter table trade_orders drop constraint FK_APP_USER;
--
--
--select * from trade_orders to2 order by order_id desc;

--select * from pg_timezone_names;



show timezone;