create or replace view five_days_stock_trend_v as (
    select trade_symbol_id as symbol_id, array_agg( trunc(high_price, 2) order by price_time desc )  as trend
     from trade_symbol_price_historic tsph
     where interval_period = '1DAY' group by trade_symbol_id
);

create or replace view current_stocks_v as (
	   select o.user_id, o.preferred_region, o.symbol_id,
	    sum ( case when trade_type = 'BUY' then stock_units else -stock_units end) as units
	    from trade_orders o group by o.user_id, o.preferred_region, o.symbol_id
);

create or replace view stock_period_info_v as (
   select trade_symbol_id, high_price, price_time, interval_period
    from trade_symbol_price_historic tsph, current_stocks_v c where tsph.trade_symbol_id = c.symbol_id
);

