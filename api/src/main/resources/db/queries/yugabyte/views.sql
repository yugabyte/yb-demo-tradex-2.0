
CREATE OR REPLACE VIEW five_days_stock_trend_v AS
(
SELECT trade_symbol_id                                          AS symbol_id,
       ARRAY_AGG(TRUNC(high_price, 2) ORDER BY price_time DESC) AS trend
FROM trade_symbol_price_historic tsph
WHERE interval_period = '1DAY'
GROUP BY trade_symbol_id
  );

CREATE OR REPLACE VIEW current_stocks_v AS
(
SELECT o.user_id,
       o.preferred_region,
       o.symbol_id,
       sum(CASE WHEN trade_type = 'BUY' THEN stock_units ELSE -stock_units END) AS units
FROM trade_orders o
GROUP BY o.user_id, o.preferred_region, o.symbol_id
 );

CREATE OR REPLACE VIEW stock_period_info_v AS
(
SELECT trade_symbol_id,
       high_price,
       price_time,
       interval_period,
       preferred_region
FROM trade_symbol_price_historic tsph,
     current_stocks_v c
WHERE tsph.trade_symbol_id = c.symbol_id
  );
