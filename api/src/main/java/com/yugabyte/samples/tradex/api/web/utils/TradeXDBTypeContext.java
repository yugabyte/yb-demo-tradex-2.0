package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class TradeXDBTypeContext {


  @Value("${app.datasource_types}")
  TradeXDataSourceType dbType;

  public void setDbType(TradeXDataSourceType dbType) {
    this.dbType = dbType;
  }

  public TradeXDataSourceType getDbType() {
    return dbType;
  }
}
