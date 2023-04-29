package com.yugabyte.samples.tradex.api.utils;

public class SqlQueries {
    public enum TradeSql {
        INSERT_TRADE,
        DEL_USER_TRADES,
        FETCH_USER_TRADES,
        FETCH_TRADE_BY_ID
    }

    public enum UserSql {
        FIND_BY_EMAIL_SQL,
        FIND_BY_ID,
        VERIFY_USE_PIN_SQL,
        EXISTS_BY_EMAIL_SQL,
        INSERT_APP_USER,
        UPDATE_APP_USER,
        ADD_USER_FAV,
        DEL_USER_FAV,
        UPDATE_USER_FAV,
        UPDATE_USER_NOTIF,
        UPDATE_APP_USER_PWD,
        UPDATE_USER_LANG,


    }

    public enum ChartSql {
        PORTFOLIO_CHART_SQL,
        PORTFOLIO_TOTAL
    }

    public enum StockSql {
        STOCK_BY_SYMBOL_SQL,
        STOCK_BY_ID_SQL,
        STOCK_DAY_HISTORY_BY_ID_SQL,
        STOCK_HISTORY_BY_ID_BETWEEN_SQL,
        STOCK_TREND,
        ALL_ACTIVE_STOCKS,

        APP_USER_FAV_STOCKS
    }

    public enum RefDataSql {
        REFDATA_BY_KEY_SQL,
        FETCH_DB_NODES

    }


}
