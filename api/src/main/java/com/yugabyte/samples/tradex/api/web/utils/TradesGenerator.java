package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.utils.AppConstants;
import com.yugabyte.samples.tradex.api.utils.StockInfoCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
@Slf4j
public class TradesGenerator {

    @Autowired
    StockInfoCache stockInfoCache;

    public List<TradeOrder> generateTrades(int sinceDays, int userId, String prefRegion, List<Integer> stockSymbols) {
        List<TradeOrder> orders = new ArrayList<>(100);

        Random random = new Random();
        ZonedDateTime start = ZonedDateTime.now().minusDays(sinceDays).withHour(8).withSecond(0).withZoneSameInstant(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

        //start.withMinute(30);

        AtomicInteger count = new AtomicInteger(0);
        try {
            IntStream.range(1, sinceDays).forEach(e -> {
                if (!isWeekend(start.plusDays(e))) {
                    IntStream.range(1, random.nextInt(2, 5)).forEach(k -> {

                        TradeOrder tradeOrder = new TradeOrder();
                        tradeOrder.setUserId(userId);
                        tradeOrder.setStock(stockInfoCache.fetchAllStocksPerformance().get(random.nextInt(stockSymbols.size())).getStock());
                        tradeOrder.setTradeType((e % 3 == 0 || e % 7 == 0 || e % 13 == 0) ? AppConstants.TradeType.SELL.name()
                                : AppConstants.TradeType.BUY.name());


                        tradeOrder.setOrderTime(Instant.parse(formatter.format(start.plusDays(e))));

                        tradeOrder.setBidPrice((random.nextInt(5) + random.nextDouble(0.05, 1.3)));
                        tradeOrder.setStockUnits(random.nextDouble(13.354));
                        tradeOrder.setPayMethod((k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD
                                : AppConstants.PayMethod.BANK_TRANSFER);

                        orders.add(tradeOrder);

                        //log.info("inserted rows: {}", update);
                        count.incrementAndGet();
                    });
                }

                //fill for 7 days

                if (sinceDays - count.get() <= 6) {

                    IntStream.range(1, random.nextInt(1, 5)).forEach(k -> {

                        TradeOrder tradeOrder = new TradeOrder();
                        tradeOrder.setUserId(userId);
                        tradeOrder.setStock(stockInfoCache.fetchAllStocksPerformance().get(random.nextInt(stockSymbols.size())).getStock());
                        tradeOrder.setTradeType(AppConstants.TradeType.BUY.name());
                        tradeOrder.setOrderTime(Instant.from(start.plusDays(e)));
                        tradeOrder.setBidPrice((random.nextInt(5) + random.nextDouble(0.03, 1.025)));
                        tradeOrder.setStockUnits(random.nextDouble(3.354));
                        tradeOrder.setPayMethod((k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD
                                : AppConstants.PayMethod.BANK_TRANSFER);

                        orders.add(tradeOrder);
                    });
                }

                // fill 24hours
                if (sinceDays - count.get() == 0) {

                    IntStream.range(1, random.nextInt(1, 7)).forEach(k -> {

                        TradeOrder tradeOrder = new TradeOrder();
                        tradeOrder.setUserId(userId);
                        tradeOrder.setStock(stockInfoCache.fetchAllStocksPerformance().get(random.nextInt(stockSymbols.size())).getStock());

                        tradeOrder.setOrderTime(Instant.from(start.plusDays(e).plusHours(k)));
                        tradeOrder.setBidPrice((random.nextInt(7) + random.nextDouble(0.03, 1.025)));

                        tradeOrder.setPayMethod((k % 2 == 0) ? AppConstants.PayMethod.CREDIT_CARD
                                : AppConstants.PayMethod.BANK_TRANSFER);

                        if (k % 3 == 0 && k % 5 == 0) {
                            tradeOrder.setTradeType(AppConstants.TradeType.BUY.name());
                            tradeOrder.setStockUnits(random.nextDouble(7.354));
                        } else if (k % 7 == 0) {
                            tradeOrder.setTradeType(AppConstants.TradeType.SELL.name());
                            tradeOrder.setStockUnits(random.nextDouble(2.43));
                        } else {
                            tradeOrder.setTradeType(AppConstants.TradeType.BUY.name());
                            tradeOrder.setStockUnits(random.nextDouble(1.78));
                        }

                        orders.add(tradeOrder);
                    });

                }
            });

            log.info("generated records: {} for user: {}", count.get(), userId);
            return orders;
        } catch (
                DataAccessException d) {
            log.error("Caught Error while filling tradeOrders. {}", d.getMessage());
            throw d;
        }
    }


    private boolean isWeekend(ZonedDateTime localDate) {
        // get Day of week for the passed LocalDate
        return (localDate.get(ChronoField.DAY_OF_WEEK) == 6)
                || (localDate.get(ChronoField.DAY_OF_WEEK) == 7);
    }

}
