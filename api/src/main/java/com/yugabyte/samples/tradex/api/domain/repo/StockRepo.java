package com.yugabyte.samples.tradex.api.domain.repo;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.TradeXHistoricalQuote;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.utils.Sql.Stock;
import com.yugabyte.samples.tradex.api.utils.TradeXHistoryIntervalPeriod;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class StockRepo {

  @Autowired
  TradeXJdbcTemplateResolver resolver;

  public List<TradeXStock> getAllActiveTradeXStocks(TradeXDataSourceType dbType) {
    NamedParameterJdbcTemplate template = resolver.resolve(dbType);
    return template.query(Stock.ALL_ACTIVE_STOCKS, new TradeXStockRowMapper());
  }

  public TradeXStock getTradeXStock(TradeXDataSourceType dbType, String symbol) {
    NamedParameterJdbcTemplate template = resolver.resolve(dbType);
    return template.queryForObject(Stock.STOCK_BY_SYMBOL_SQL,
      Map.of("psymbol", symbol), new TradeXStockRowMapper());
  }

  public TradeXStock getStockSymbol(TradeXDataSourceType dbType, int symbolId) {
    NamedParameterJdbcTemplate template = resolver.resolve(dbType);
    try {
      return template.queryForObject(Stock.STOCK_BY_ID_SQL,
        Map.of("symbolId", symbolId), new TradeXStockRowMapper());
    } catch (EmptyResultDataAccessException e) {
      log.error("No data for symbol id:{}", symbolId);
      throw e;
    }
  }

  public List<TradeXHistoricalQuote> getTradeXHistory(TradeXDataSourceType dbType, int symbolId,
    TradeXHistoryIntervalPeriod intervalPeriod) {
    NamedParameterJdbcTemplate template = resolver.resolve(dbType);
    try {
      return template.query(Stock.STOCK_DAY_HISTORY_BY_ID_SQL,
        Map.of("symbolId", symbolId, "intervalPeriod", intervalPeriod.getValue()),
        new TradeXHistoricalQuoteRowMapper());
    } catch (EmptyResultDataAccessException e) {
      log.error("No data for symbol id:{}", symbolId);
      throw e;
    }
  }

  public List<TradeXHistoricalQuote> getTradeXHistory(TradeXDataSourceType dbType, int symbolId,
    String fromDate, String toDate) {
    NamedParameterJdbcTemplate template = resolver.resolve(dbType);
    try {
      return template.query(Stock.STOCK_HISTORY_BY_ID_BETWEEN_SQL,
        Map.of("symbolId", symbolId, "fromDate", fromDate, "toDate", toDate),
        new TradeXHistoricalQuoteRowMapper());

    } catch (EmptyResultDataAccessException e) {
      log.error("No data for symbol id:{}", symbolId);
      throw e;
    }
  }

  public List<Pair<Integer, List<Double>>> fetchStockTrends() {
    // Using single region, as stock data is common across cluster types.
    NamedParameterJdbcTemplate template = resolver.resolve(
      TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE);

    try {
      return template.query(Stock.STOCK_TREND,
        Collections.emptyMap(), new TrendCacheRowMapper());

    } catch (EmptyResultDataAccessException e) {
      log.error("Stock Trend Data is missing");
      throw e;
    }
  }
}

class TradeXStockRowMapper implements RowMapper<TradeXStock> {

  @Override
  public TradeXStock mapRow(ResultSet rs, int rowNum) throws SQLException {
    TradeXStock ts = new TradeXStock();
    ts.setId(rs.getInt("trade_symbol_id"));
    ts.setSymbol(rs.getString("symbol"));
    ts.setStockExchange(rs.getString("exchange"));
    ts.setCompany(rs.getString("company"));
    ts.setCreatedDate(rs.getTimestamp("created_date")
      .toInstant());
    ts.setClosePrice(rs.getBigDecimal("close_price"));
    ts.setOpenPrice(rs.getBigDecimal("open_price"));
    ts.setLowPrice(rs.getBigDecimal("low_price"));
    ts.setHighPrice(rs.getBigDecimal("high_price"));
    ts.setMarketCap(rs.getBigDecimal("market_cap"));
    ts.setVolume(rs.getBigDecimal("volume"));
    ts.setAvgVolume(rs.getBigDecimal("avg_volume"));
    if (rs.getTimestamp("price_time") != null) {
      ts.setPriceTime(rs.getTimestamp("price_time")
        .toInstant());
    }
    return ts;
  }
}

class TradeXHistoricalQuoteRowMapper implements RowMapper<TradeXHistoricalQuote> {

  @Override
  public TradeXHistoricalQuote mapRow(ResultSet rs, int rowNum) throws SQLException {
    TradeXHistoricalQuote historicalQuote = new TradeXHistoricalQuote();
    historicalQuote.setSymbol(rs.getString("symbol"));
    historicalQuote.setDate(rs.getTimestamp("price_time")
      .toInstant());
    historicalQuote.setHigh(rs.getBigDecimal("high_price"));
    historicalQuote.setOpen(rs.getBigDecimal("open_price"));
    historicalQuote.setLow(rs.getBigDecimal("low_price"));
    return historicalQuote;
  }

}

class TrendCacheRowMapper implements RowMapper<Pair<Integer, List<Double>>> {

  @Override
  public Pair<Integer, List<Double>> mapRow(ResultSet rs, int rowNum) throws SQLException {
    Integer stockId = rs.getInt("symbol_id");

    String[] array = rs.getObject("trend")
      .toString()
      .replace("[", "")
      .replace("]", "")
      .replace("{", "")
      .replace("}", "")
      .split(",");
    List<Double> trend = Arrays.stream(array)
      .map(Double::valueOf)
      .collect(Collectors.toList());

        return Pair.of(stockId, trend);
    }
}
