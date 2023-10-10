package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class TradeXDBTypeContext {



  private final TradeXDataSourceType[] dbTypes;

  TradeXDataSourceType dbType;

  public TradeXDBTypeContext(TradeXDataSourceType[] dbTypes) {
    this.dbTypes = dbTypes;
    this.dbType = dbTypes[0];
  }

  public void setDbType(TradeXDataSourceType dbType) {
    this.dbType = dbType;
  }

  public TradeXDataSourceType getDbType() {
    return dbType;
  }
}
