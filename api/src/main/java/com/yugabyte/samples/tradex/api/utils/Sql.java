package com.yugabyte.samples.tradex.api.utils;

public class Sql {
  public static class Charts {
    public static final String PORTFOLIO_CHART_SQL = """
      select price_time ,  trunc ( sum(cs.units * q.high_price), 2 ) as total,
      q.interval_period from current_stocks_v cs , stock_period_info_v q
      where cs.user_id =:userId and cs.preferred_region =:prefRegion and cs.symbol_id = q.trade_symbol_id
      group by price_time ,interval_period order by price_time desc , interval_period
    """;

    public static final String PORTFOLIO_TOTAL = """
     select coalesce (sum( units * ts.high_price), 0.0)
     from current_stocks_v csv2 ,trade_symbol ts
     where csv2.symbol_id = ts.trade_symbol_id and csv2.user_id = :userId and csv2.preferred_region =:prefRegion
    """;
  }

  public static class RefData {
    public static final String REFDATA_BY_KEY_SQL = """
      select *
      from public.ref_data r
      where r.key_name = :pKeyName
    """;

    public static final String FETCH_DB_NODES = """
      select region, zone, node_type from yb_servers()
    """;
    public static final String CHECK_SQL = """
      select count(*)
      from REF_DATA
    """;
  }

  public static class Stock {

    public static final String STOCK_BY_SYMBOL_SQL = """
      select *
      from trade_symbol ts
      where ts.symbol = :psymbol
    """;
    public static final String STOCK_BY_ID_SQL = """
      select *
      from trade_symbol ts
      where ts.enabled = 1 and ts.trade_symbol_id = :symbolId
    """;

    public static final String STOCK_DAY_HISTORY_BY_ID_SQL = """
      select ts.symbol, th.price_time,th.high_price, th.low_price, th.open_price
      from trade_symbol_price_historic th, trade_symbol ts
      where th.trade_symbol_id = ts.trade_symbol_id and ts.trade_symbol_id = :symbolId and th.interval_period = :intervalPeriod;
    """;

    public static final String STOCK_HISTORY_BY_ID_BETWEEN_SQL = """
      select ts.symbol, th.price_time,th.high_price, th.low_price, th.open_price
      from trade_symbol_price_historic th, trade_symbol ts
      where th.trade_symbol_id = ts.trade_symbol_id and ts.trade_symbol_id = :symbolId and price_time between :fromDate and :toDate
    """;

    public static final String STOCK_TREND = """
      select symbol_id, trend from five_days_stock_trend_v
    """;

    public static final String ALL_ACTIVE_STOCKS = """
      select * from trade_symbol ts where ts.enabled = 1 order by trade_symbol_id
    """;

    public static final String APP_USER_FAV_STOCKS = """
      select ts.*
      from trade_symbol ts, app_user au
      where ts.enabled = 1 and au.id = :userId and au.preferred_region = :prefRegion and ts.trade_symbol_id = any(au.favourites)
    """;
  }
  public static class Trade {
    public static final String INSERT_TRADE = """
      INSERT INTO trade_orders (user_id, symbol_id, trade_type, order_time, bid_price, preferred_region, stock_units, pay_method)
      VALUES ( :userId, :symbolId, :tradeType, :order_time, :bidPrice, :preferredRegion, :stockUnits, :payMethod) returning order_id
    """;
    public static final String BULK_INSERT_TRADE = """
      INSERT INTO trade_orders (user_id, symbol_id, trade_type, order_time, bid_price, preferred_region, stock_units, pay_method)
      VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)
    """;


    public static final String DEL_USER_TRADES = """
      DELETE FROM trade_orders WHERE user_id = :userId
    """;
    public static final String FETCH_USER_TRADES = """
      SELECT *
      FROM trade_orders t
      WHERE t.preferred_region = :prefRegion AND t.user_id = :pUserId AND t.order_id > :prevId
      ORDER BY t.order_time DESC
      LIMIT :plimit
    """;

    public static final String FETCH_TRADE_BY_ID = """
      select *
      from public.trade_orders
      where order_id = :orderId
    """;
  }
  public static class User {
    public static final String UPDATE_APP_USER = """
      update app_user
      set
        email = :uemail,
        USER_LANGUAGE = :ulang,
        enabled = :uenabled,
        personal_details = cast ( :upersonaldet as json ), user_notifications = cast ( :unotifications as json ),
        updated_date = now()
      where preferred_region = :prefRegion and id = :uid
    """;
    public static final String UPDATE_APP_USER_PWD = """
      update app_user set passkey = :passKey where email = :uemail
    """;

    public static final String ADD_USER_FAV = """
      update app_user set favourites = array_append ( favourites, :stockId ) where preferred_region = :prefRegion and id = :uid
    """;

    public static final String DEL_USER_FAV = """
      update app_user set favourites = array_remove ( favourites, :stockId ) where preferred_region = :prefRegion and id = :uid
    """;

    public static final String UPDATE_USER_FAV = """
      update app_user set favourites = :favourites where preferred_region = :prefRegion and id = :uid
    """;

    public static final String UPDATE_USER_NOTIF = """
      update app_user set user_notifications = cast( :pNotif as json ) where preferred_region = :prefRegion and id = :uid
    """;

    public static final String UPDATE_USER_LANG = """
      update app_user set user_language = :langCode where preferred_region = :prefRegion and id = :uid
    """;

    public static final String FIND_BY_EMAIL_SQL = """
      select id, preferred_region, email, personal_details, enabled, user_notifications, user_language, created_date , updated_date , favourites, passkey, security_pin
      from app_user au
      where au.email = :pEmail
    """;

    public static final String EXISTS_BY_EMAIL_SQL = """
      select  exists ( select 1  from app_user au where au.email = :pEmail )
    """;
    public static final String VERIFY_USE_PIN_SQL = """
      select exists ( select 1 from app_user au where au.id = :pUserId and au.preferred_region = :prefRegion and au.security_pin = :pUserPin)
    """;

    public static final String FIND_BY_ID = """
      select *
      from app_user u
      where u.id = :uid and u.preferred_region = :prefRegion
    """;
    public static final String INSERT_APP_USER = """
      insert into app_user(preferred_region,email, passkey, enabled, user_language,
        personal_details,user_notifications,security_pin, created_date,updated_date) values
            ( :prefRegion, :uemail, :upasswd, :uenabled, :ulang, cast (:upersonaldet as json) ,
      cast ( :unotifications as json), :usecuritypin, now(), now()) returning ID, PREFERRED_REGION
    """;
  }

}
