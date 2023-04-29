import List from '@mui/material/List';
import {TradeListItem} from './tradeListItem';

export function TradeList(props) {

  if ( props.trades.length === 0) {
    return (<h6 style={{textAlign:"center" }}>No data to display.</h6>)
  }

  return (
    <div id="tradelist">
    <List sx={{ width: '100%', maxWidth: 400, bgcolor: 'background.paper' }}>
     { 
      props.trades && props.trades.map( 
        function(item, idx) {
           return (
            
          <TradeListItem key={item.orderId } tradePrice={item.currentPrice}
            tradeSym={item.stockSymbol} tradeStatus={item.profit}
            //tradeImg={item.tradeOrder.stock.id} 
            tradeCompany={item.stockCompany}
            tradeProfitPercent={item.profitPercent} trend={item.trend}
          />
          );
    })
    }
    </List>
    </div>  );

}

export default TradeList;