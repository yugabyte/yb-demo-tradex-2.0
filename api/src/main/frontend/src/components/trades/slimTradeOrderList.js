import List from '@mui/material/List';
import SlimTradeOrderListItem from './slimTradeOrderListItem';

export function SlimTradeOrderList(props) {

  return (
    <div id="tradelist">
    <List sx={{ width: '100%', maxWidth: 400, bgcolor: 'background.paper' }}>
     { 
      props.tradeOrders && props.tradeOrders.map( 
        function(item, idx) {
           return (
          <SlimTradeOrderListItem key={item.orderId} tradeDt={item.orderTime}
            tradeSym={item.stock.symbol}  stockUnits={item.stockUnits} tradeType={item.tradeType}
          />
          );
    })
    }
    </List>
    </div>  );

}

export default SlimTradeOrderList;