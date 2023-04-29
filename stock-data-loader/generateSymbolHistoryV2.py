#!/usr/bin/python3
import os
from datetime import date
from datetime import timedelta

import psycopg2
import yfinance as yf


def executeList(dbcon, sqls):
    print("about to execute %d sql statements" % len(sqls))
    try:
        cursor = dbcon.cursor()
        # print("2")
        for sql in sqls:
            cursor.execute(sql)
        cursor.close();
        print("SQL Execution complete")
    except Exception as e:
        print("Execution failed")
        print(e)


def generateLiveStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ''' Fetches latest Stock Info '''

    ticker = yf.Ticker(tickerSymbol)
    tinfo = ticker.info
    print(tinfo)
    sqls = []
    sqls.append(
        "update TRADE_SYMBOL set price_time = now(), open_price = {0:.3f}, low_price = {1:.3f}, high_price = {2:.3f}, close_price = {3:.3f} , market_cap = {4:.3f} , volume = {5:.3f} , avg_volume = {6:.3f} where symbol = '{7}';".format(
            tinfo['open'], tinfo['dayLow'], tinfo['dayHigh'], tinfo['previousClose'], tinfo['marketCap'],
            tinfo['volume'], tinfo['averageVolume'], tinfo['symbol']))

    print("LiveStockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateOneYearStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ticker = yf.Ticker(tickerSymbol)
    sqls = []
    yearHistory = ticker.history(interval="3mo", period="1y")
    sqls.append(
        "delete from trade_symbol_price_historic where trade_symbol_id = {0} and interval_period='3MONTH';".format(
            tickerSymbolId))
    for index, row in yearHistory.iterrows():
        sqls.append(
            "insert into trade_symbol_price_historic(trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f}, '3MONTH');".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));

    print("One Year StockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateOneMonthStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ticker = yf.Ticker(tickerSymbol)
    sqls = []
    month_history = ticker.history(interval="1wk", period="1mo")
    sqls.append(
        "delete from trade_symbol_price_historic where trade_symbol_id = {0} and interval_period='1WEEK';".format(
            tickerSymbolId))
    for index, row in month_history.iterrows():
        sqls.append(
            "insert into trade_symbol_price_historic(trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f}, '1WEEK');".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));
    print("One Month StockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateOneWeekStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ticker = yf.Ticker(tickerSymbol)
    sqls = []
    week_history = ticker.history(interval="1d", period="5d")
    sqls.append(
        "delete from trade_symbol_price_historic where trade_symbol_id = {0}  and interval_period='1DAY';".format(
            tickerSymbolId))
    for index, row in week_history.iterrows():
        sqls.append(
            "insert into trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f},'1DAY');".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));
    print("One Week StockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateNinetyMinStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ticker = yf.Ticker(tickerSymbol)
    sqls = []
    ninety_min_history = ticker.history(interval="90m", period="1d")
    sqls.append(
        "delete from trade_symbol_price_historic where trade_symbol_id = {0}  and interval_period='90MIN' ;".format(
            tickerSymbolId))
    for index, row in ninety_min_history.iterrows():
        sqls.append(
            "insert into trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f}, '90MIN');".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));
    print("Ninety Min StockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateHourlyStockInfo(tickerSymbol='GOOGL', tickerSymbolId=1):
    ticker = yf.Ticker(tickerSymbol)
    sqls = []
    hourly_history = ticker.history(interval="60m", period="1d")
    sqls.append(
        "delete from trade_symbol_price_historic where trade_symbol_id = {0}   and interval_period='1H' ;".format(
            tickerSymbolId))
    for index, row in hourly_history.iterrows():
        sqls.append(
            "insert into trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f}, '1H');".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));
    print("Hourly StockInfo : {0} : completed".format(tickerSymbol))
    return sqls


def generateToday(tickerSymbol='GOOGL', tickerSymbolId=1):
    today = date.today().strftime("%Y-%m-%d")
    yesterday = (date.today() - timedelta(days=1)).strftime("%Y-%m-%d")
    print('Yesterday: ', yesterday)
    ticker = yf.Ticker(tickerSymbol)
    today_history = ticker.history(start=yesterday, end=today, interval="30m", period="1d")
    print("today's hist count", len(today_history))
    tinfo = ticker.info
    # print(type(tinfo['open']))
    # print( "Hello {0:.4f}".format(tinfo['open']))

    f = open("{}.sql".format(tickerSymbol), "w")
    f.write(
        "\nupdate TRADE_SYMBOL set price_time = now(), open_price = {0:.3f}, low_price = {1:.3f}, high_price = {2:.3f}, close_price = {3:.3f} , market_cap = {4:.3f} , volume = {5:.3f} , avg_volume = {6:.3f} where symbol = '{7}';".format(
            tinfo['open'], tinfo['dayLow'], tinfo['dayHigh'], tinfo['previousClose'], tinfo['marketCap'],
            tinfo['volume'], tinfo['averageVolume'], tinfo['symbol']))

    f.write("\ndelete from trade_symbol_price_today where trade_symbol_id = {0};".format(tickerSymbolId))

    # today's  history
    for index, row in today_history.iterrows():
        f.write(
            "\ninsert into trade_symbol_price_today(trade_symbol_id, price_time, open_price, low_price, high_price, price) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f});".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));

    f.write("\ndelete from trade_symbol_price_historic where trade_symbol_id = {0};".format(tickerSymbolId))

    # last one year history
    lastYear = (date.today() - timedelta(days=365)).strftime("%Y-%m-%d")
    yearHistory = ticker.history(start=lastYear, end=today, interval="1d", period="1y")

    for index, row in yearHistory.iterrows():
        f.write(
            "\ninsert into trade_symbol_price_historic(trade_symbol_id, price_time, open_price, low_price, high_price, price) values ({0}, '{1}', {2:.3f}, {3:.3f},{4:.3f},{5:.3f});".format(
                tickerSymbolId,
                index.strftime('%Y-%m-%d %X'),
                row['Close'],
                row['Low'],
                row['High'],
                row['Close']
            ));

    f.close()


if __name__ == '__main__':

    try:
        DB_CONNECTION_STRING = "host=%s port=%s dbname=%s user=%s password=%s" % (os.getenv("PGHOST"),
                                                                                  os.getenv("PGPORT"),
                                                                                  os.getenv("PGDATABASE"),
                                                                                  os.getenv("PGUSER"),
                                                                                  os.getenv("PGPASSWORD"))
        print(DB_CONNECTION_STRING)
        conn = psycopg2.connect(DB_CONNECTION_STRING)
        print("Database connected successfully")
        cursor = conn.cursor()
        qSelect = "select trade_symbol_id, symbol  from trade_symbol ts where enabled = true order by symbol"
        cursor.execute(qSelect)
        results = cursor.fetchall()
        print('results: {}'.format(len(results)))
        stocks = {}
        for row in results:
            print("Stock Id: {} Stock Symbol: {}".format(row[0], row[1]))
            stocks[row[0]] = row[1]
        print(stocks)
        cursor.close()
        # conn.close()
        for key, value in stocks.items():

            try:
                item_sql_list = []
                # generateToday(value, key)
                print("about to process: {0}".format(value))
                item_sql_list.extend(generateLiveStockInfo(value, key))
                item_sql_list.extend(generateOneYearStockInfo(value, key))
                item_sql_list.extend(generateOneMonthStockInfo(value, key))
                item_sql_list.extend(generateOneWeekStockInfo(value, key))
                item_sql_list.extend(generateNinetyMinStockInfo(value, key))
                item_sql_list.extend(generateHourlyStockInfo(value, key))
                item_sql_list.append("commit;")
                executeList(conn, item_sql_list)
                print("{0} stock data updated".format(value))
            except Exception as e:
                print("Encountered problem while fetching data for {0}".format(value))
                print(e)

    except Exception as e:
        print("Database not connected successfully")
        print(e)
