import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import { Typography } from '@mui/material';

import { Link } from "react-router-dom";


export default function SlimTradeOrderListItem(props) {
   const imgUrl = 'https://yb-global-tradex-imgstore.s3.amazonaws.com/img/symbols/' + props.tradeSym + '.png';  
  
  return (
    <Link to={`/candlechart`} state={{ companyName:  props.tradeSym}}>
      <List sx={{ width: '100%', maxWidth: 380, bgcolor: 'background.paper' }}>
        <ListItem key={props.key}>
          <ListItemAvatar>
            <Avatar key={props.key} src={imgUrl} />
          </ListItemAvatar>
          <ListItemText primary={<Typography variant="h5" style={{ fontSize: '1.1rem' }}>{props.tradeSym}</Typography>} secondary={<Typography variant="p" style={{ fontSize: '12px' }}>{props.tradeDt}</Typography>} sx={{ width: 80 }} />
          <ListItemText primary={<Typography variant="h5" style={{ fontSize: '1rem' }}>{props.tradeType}</Typography>} secondary={<Typography variant="p">{props.stockUnits}</Typography>} sx={{ width: 60, textAlign: 'right', }} />
        </ListItem>
      </List>
    </Link>
  );
  
}
