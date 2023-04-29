package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.utils.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
@Slf4j
public class TradesGenerator {

    public List<SqlParameterSource> generateTrades(int sinceDays, int userId, String prefRegion, List<Integer> stockSymbols) {
        List<SqlParameterSource> parameterSources = new ArrayList<>();

        Random random = new Random();
        LocalDateTime start = LocalDateTime.now().minusDays(sinceDays).withHour(8);
        //start.withMinute(30);

        AtomicInteger count = new AtomicInteger(0);
        try {


            IntStream.range(1, sinceDays).forEach(e -> {
                if (!isWeekend(start.plusDays(e))) {
                    IntStream.range(1, random.nextInt(2, 5)).forEach(k -> {
                        MapSqlParameterSource insertSqlParms = new MapSqlParameterSource();
                        insertSqlParms.addValue("userId", userId);
                        insertSqlParms.addValue("symbolId", stockSymbols.get(random.nextInt(stockSymbols.size())));
                        insertSqlParms.addValue("tradeType",
                                (e % 3 == 0 || e % 7 == 0 || e % 13 == 0) ? AppConstants.TradeType.SELL.name()
                                        : AppConstants.TradeType.BUY.name());
                        insertSqlParms.addValue("order_time", start.plusDays(e));
                        insertSqlParms.addValue("bidPrice",
                                (random.nextInt(5) + random.nextDouble(0.05, 1.3)));
                        insertSqlParms.addValue("preferredRegion", prefRegion);
                        insertSqlParms.addValue("stockUnits", random.nextDouble(13.354));
                        insertSqlParms.addValue("payMethod", (k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD.name()
                                : AppConstants.PayMethod.BANK_TRANSFER.name());
                        parameterSources.add(insertSqlParms);
                        //log.info("inserted rows: {}", update);
                        count.incrementAndGet();
                    });
                }

                //fill for 7 days

                if (sinceDays - count.get() <= 6) {

                    IntStream.range(1, random.nextInt(1, 5)).forEach(k -> {

                        MapSqlParameterSource insertSqlParms = new MapSqlParameterSource();
                        insertSqlParms.addValue("userId", userId);
                        insertSqlParms.addValue("symbolId", stockSymbols.get(random.nextInt(stockSymbols.size())));
                        insertSqlParms.addValue("tradeType", AppConstants.TradeType.BUY.name());
                        insertSqlParms.addValue("order_time", start.plusDays(e));
                        insertSqlParms.addValue("bidPrice",
                                (random.nextInt(5) + random.nextDouble(0.03, 1.025)));
                        insertSqlParms.addValue("preferredRegion", prefRegion);
                        insertSqlParms.addValue("stockUnits", random.nextDouble(3.354));
                        insertSqlParms.addValue("payMethod", (k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD.name()
                                : AppConstants.PayMethod.BANK_TRANSFER.name());
                        parameterSources.add(insertSqlParms);
                    });
                }

                // fill 24hours
                if (sinceDays - count.get() == 0) {

                    IntStream.range(1, random.nextInt(1, 7)).forEach(k -> {
                        MapSqlParameterSource insertSqlParms = new MapSqlParameterSource();
                        insertSqlParms.addValue("userId", userId);
                        insertSqlParms.addValue("symbolId", stockSymbols.get(random.nextInt(stockSymbols.size())));
                        insertSqlParms.addValue("order_time", start.plusDays(e).plusHours(k));
                        insertSqlParms.addValue("bidPrice",
                                (random.nextInt(7) + random.nextDouble(0.03, 1.025)));
                        insertSqlParms.addValue("preferredRegion", prefRegion);
                        //insertSqlParms.addValue("stockUnits", random.nextDouble(5.354));

                        if (k % 3 == 0 && k % 5 == 0) {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.BUY.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(7.354));
                        } else if (k % 7 == 0) {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.SELL.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(2.43));
                        } else {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.BUY.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(1.78));
                        }
                        insertSqlParms.addValue("payMethod", (k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD.name()
                                : AppConstants.PayMethod.BANK_TRANSFER.name());
                        parameterSources.add(insertSqlParms);
                    });

                    IntStream.range(1, random.nextInt(1, 5)).forEach(k -> {
                        MapSqlParameterSource insertSqlParms = new MapSqlParameterSource();
                        insertSqlParms.addValue("userId", userId);
                        insertSqlParms.addValue("symbolId", stockSymbols.get(random.nextInt(stockSymbols.size())));
                        insertSqlParms.addValue("order_time", start.plusDays(e).plusHours(k).plusMinutes(30));
                        insertSqlParms.addValue("bidPrice",
                                (random.nextInt(7) + random.nextDouble(0.03, 1.025)));
                        insertSqlParms.addValue("preferredRegion", prefRegion);
                        //insertSqlParms.addValue("stockUnits", random.nextDouble(5.354));

                        if (k % 3 == 0 && k % 5 == 0) {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.BUY.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(7.354));
                        } else if (k % 7 == 0) {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.SELL.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(2.43));
                        } else {
                            insertSqlParms.addValue("tradeType", AppConstants.TradeType.BUY.name());
                            insertSqlParms.addValue("stockUnits", random.nextDouble(1.78));
                        }
                        insertSqlParms.addValue("payMethod", (k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD.name()
                                : AppConstants.PayMethod.BANK_TRANSFER.name());
                        parameterSources.add(insertSqlParms);
                    });
                }
            });

            log.info("generated records: {} for user: {}", count.get(), userId);
            return parameterSources;
        } catch (
                DataAccessException d) {
            log.error("Caught Error while filling tradeOrders. {}", d.getMessage());
            throw d;
        }
    }


    private boolean isWeekend(LocalDateTime localDate) {
        // get Day of week for the passed LocalDate
        return (localDate.get(ChronoField.DAY_OF_WEEK) == 6)
                || (localDate.get(ChronoField.DAY_OF_WEEK) == 7);
    }

}
