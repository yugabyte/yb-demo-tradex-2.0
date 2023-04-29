import React from 'react'
import { useState, useEffect } from "react";
import getJSON from "../../services/rest";
// import TradePerformance from './tradePerformance';
import List from '@mui/material/List';
import TradeList2 from './traderow2';

function TradeList() {

  const [trades, setTrades] = useState([]);

  useEffect( ()=> {

    loadTrades();

  }, []);

  const loadTrades = async () =>{

    try {
      const resp = await getJSON('/api/portfolio' );
      setTrades(resp.recentTrades);
      console.log('resp', resp);
    }
    catch (e) {
      console.log("error in fetching database nodes", e);
    }
  }

  return (
    <div>
      {/* <h1>Portfolio</h1> */}
      <div id="tChart" ></div>
      
    
    <div id="tradelist">
    <List sx={{ width: '100%',padding:"0px !important"}}>
      {/* <ul> */}
     { 
     trades.map( function(item, idx) { return (

      // <TradePerformance key={item.trade.id} tradeImg={item.trade.symbol.id} tradeCompany={item.trade.symbol.company} tradeProfitPercent={item.tradeProfitPercent} />
      <TradeList2 key={item.trade.id} tradePrice={item.currentPrice} tradeSym={item.trade.symbol.symbol} tradeStatus={item.profit} tradeImg={item.trade.symbol.id} tradeCompany={item.trade.symbol.company} tradeProfitPercent={item.profitPercent} />
     );
    })
    }
    {/* </ul> */}
    </List>
    </div>

    </div>

  )
}

export default TradeList;