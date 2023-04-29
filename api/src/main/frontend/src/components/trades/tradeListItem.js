import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import { ListItemIcon,Typography } from '@mui/material';
import { Link } from "react-router-dom";
import Trend from 'react-trend';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';

export function TradeListItem(props) {
   const imgUrl = 'https://yb-global-tradex-imgstore.s3.amazonaws.com/img/symbols/' + props.tradeSym + '.png';
   const re = new RegExp('^-?\\d+(?:\.\\d{0,' + (2 || -1) + '})?');
   const myProfPercent = props.tradeProfitPercent.toString().match(re)[0];

   function getProfitIcon() {

    let lineColor = props.tradeStatus? 'green':'blue';
      return ( <Trend
        smooth
        autoDraw
        autoDrawDuration={3000}
        autoDrawEasing="ease-out"
        data={ props.trend }
        gradient={[ lineColor ]}
        radius={5}
        strokeWidth={2}
        width={80}
        height={60}
        strokeLinecap={'butt'}
      /> );
    }

   function getProfitIcon2() {

    let arrowColor = props.tradeStatus? 'green':'red';

    return (<KeyboardArrowDownIcon sx={{ color: arrowColor ,maxWidth:15}}/>);
   }

  
  return (
    <Link to={`/candlechart`} state={{ companyName:  props.tradeSym}}>
      <List sx={{ width: '100%', maxWidth: 380, bgcolor: 'background.paper' }}>
        <ListItem key={props.key}>
          <ListItemAvatar>
            <Avatar key={props.key} src={imgUrl} />
          </ListItemAvatar>
          <ListItemText primary={<Typography variant="h5" style={{ fontSize: '1.1rem' }}>{props.tradeSym}</Typography>} secondary={<Typography variant="p" style={{ fontSize: '12px' }}>{props.tradeCompany.substring(0, 12)}</Typography>} sx={{ width: 80 }} />

          <ListItemIcon> {getProfitIcon()}</ListItemIcon>
          <ListItemText primary={<Typography variant="h5" style={{ fontSize: '1rem' }}>${props.tradePrice}</Typography>} secondary={<Typography variant="p">{getProfitIcon2()}{myProfPercent}%</Typography>} sx={{ width: 60, textAlign: 'right', }} />
        </ListItem>
      </List>
    </Link>
  );
  
}
