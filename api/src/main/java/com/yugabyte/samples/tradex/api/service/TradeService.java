package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.utils.AppConstants;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.utils.SqlQueries.TradeSql;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import com.yugabyte.samples.tradex.api.web.dto.TradeOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class TradeService {

    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;

    @Autowired
    StockInfoService stockInfoService;

    @Autowired
    SqlProvider queryProvider;

    @Transactional
    public void deleteAllUserTrades(TradeXDataSourceType dbType, int userId) {
        int update = jdbcTemplateResolver.resolve(dbType)
                .update(queryProvider.getTradeSQL(TradeSql.DEL_USER_TRADES), Map.of("userId", userId));
        log.info("delete {} trade records of user:{} from db:{}", update, userId, dbType.name());
    }


    @Transactional
    public void insertTrades(TradeXDataSourceType dbType, List<SqlParameterSource> parameterSources) {
        int[] update = jdbcTemplateResolver.resolve(dbType).batchUpdate(queryProvider.getTradeSQL(TradeSql.INSERT_TRADE),
                parameterSources.toArray(new SqlParameterSource[0]));
        log.info("inserted {} trade records into db:{}", update.length, dbType.name());
    }

    public List<TradeOrder> fetchByUserAndLimit(TradeXDataSourceType dbType, Integer userId, Integer prevId,
                                                Integer plimit, String prefRegion) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        return template.query(queryProvider.getTradeSQL(TradeSql.FETCH_USER_TRADES),
                Map.of("pUserId", userId, "prevId", prevId, "plimit", plimit, "prefRegion", prefRegion),
                (rs, rowNum) -> {
                    try {
                        TradeOrder t = new TradeOrder();
                        t.setUserId(rs.getInt("user_id"));
                        t.setId(rs.getInt("order_id"));
                        t.setStock(getSymbol(dbType, rs.getInt("symbol_id")));
                        t.setTradeType(rs.getString("trade_type"));
                        t.setOrderTime(rs.getTimestamp("order_time").toInstant());
                        t.setBidPrice(rs.getDouble("bid_price"));
                        t.setPayMethod(AppConstants.PayMethod.valueOf(rs.getString("pay_method")));
                        t.setStockUnits(rs.getBigDecimal("stock_units"));
                        return t;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
        );
    }

    private TradeXStock getSymbol(TradeXDataSourceType dbType, int symbol_id) {
        return stockInfoService.getStockSymbol(dbType, symbol_id);
    }


// --Commented out by Inspection START (24-01-2023 12:59):
//  public TradeOrder getTradeByOrderId(TradeXDataSourceType dbType, Integer tradeOrderId) {
//    NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
//    return template.queryForObject(queryProvider.getTradeSQL(TradeSql.FETCH_TRADE_BY_ID),
//            Map.of("orderId", tradeOrderId), new RowMapper<TradeOrder>() {
//              @Override
//              public TradeOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
//                try {
//                  TradeOrder t = new TradeOrder();
//                  t.setUserId(rs.getInt("user_id"));
//                  t.setId(rs.getInt("order_id"));
//                  t.setStock(getSymbol(dbType, rs.getInt("symbol_id")));
//                  t.setTradeType(rs.getString("trade_type"));
//                  t.setOrderTime(rs.getTimestamp("order_time").toInstant());
//                  t.setBidPrice(rs.getDouble("bid_price"));
//                  t.setPayMethod(AppConstants.PayMethod.valueOf(rs.getString("pay_method")));
//                  t.setStockUnits(rs.getBigDecimal("stock_units"));
//                  return t;
//                } catch (NullPointerException e) {
//                  e.printStackTrace();
//                  throw e;
//                }
//              }
//            });
//
//  }
// --Commented out by Inspection STOP (24-01-2023 12:59)

    @Transactional
    public Integer save(TradeXDataSourceType dbType, TradeOrderRequest newTradeOrder, Integer stockId,
                        BigDecimal closePrice, Integer userId, String preferredRegion) {

        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("userId", userId);
        parameterSource.addValue("symbolId", stockId);
        parameterSource.addValue("tradeType", newTradeOrder.getTradeType().name());
        parameterSource.addValue("preferredRegion", preferredRegion);
        parameterSource.addValue("bidPrice", closePrice);
        parameterSource.addValue("stockUnits",
                newTradeOrder.getInvestAmount().divide(closePrice, 3, RoundingMode.HALF_UP));
        parameterSource.addValue("payMethod", newTradeOrder.getPayMethod().name());
        parameterSource.addValue("order_time", LocalDateTime.now());


        template.update(queryProvider.getTradeSQL(TradeSql.INSERT_TRADE), parameterSource, keyHolder);

        log.info("Inserted Trade id: {}", keyHolder.getKey());
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
}
