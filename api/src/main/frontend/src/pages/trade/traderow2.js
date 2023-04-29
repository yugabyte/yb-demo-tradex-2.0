import * as React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
// import ArrowDropDownCircleRoundedIcon from '@mui/icons-material/ArrowDropDownCircleRounded';
import Avatar from '@mui/material/Avatar';
import ArrowCircleDownIcon from '@mui/icons-material/ArrowCircleDown';
 import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
import { red, green } from '@mui/material/colors';
import { ListItemIcon,Typography } from '@mui/material';
import Divider from '@mui/material/Divider';

export default function TradeList2(props) {
   const imgUrl = './assets/img/symbols/' + props.tradeImg + '.png';
   const re = new RegExp('^-?\\d+(?:\.\\d{0,' + (2 || -1) + '})?');
  const myProfPercent = props.tradeProfitPercent.toString().match(re)[0];

  if (props.tradeStatus) {
  return (
    <List sx={{ width: '100%', maxWidth: 380, bgcolor:'background.paper' }}>
      <ListItem key={props.key}>
        <ListItemAvatar>
        <Avatar key={props.key} src={imgUrl} />

        {console.log(imgUrl,"image")}
        {/* <Avatar alt="Remy Sharp" src="./assets/img/microsoft.png" /> */}
        </ListItemAvatar>
        <ListItemText primary={props.tradeSym} secondary={props.tradeCompany } sx={{maxWidth: 100}} />
        <ListItemIcon>

        <ArrowCircleUpIcon
         sx={{ color: "green", maxWidth: 50 }} />
      
  
        </ListItemIcon>
        <ListItemText primary={<Typography variant="h5" style={{fontSize:'1rem'}}>${props.tradePrice}</Typography>}  secondary={<Typography variant="p" style={{ color: 'green' }}>{myProfPercent}</Typography>} sx={{width:50}}/>
      </ListItem>
      <Divider variant="inset" component="li" />
     
    </List>
  );
}else{

  return (
    <List sx={{ width: '100%' }}>
      <ListItem key={props.key}>
        <ListItemAvatar>
        <Avatar key={props.key} src={imgUrl} />
       
        </ListItemAvatar>
        <ListItemText primary={props.tradeSym} secondary={props.tradeCompany } sx={{maxWidth:100}}/>
        <ListItemIcon>
        {/* if (props.tradeStatus) {
          
        } */}
   
   <ArrowCircleDownIcon sx={{ color: red[500]  }}/>
        </ListItemIcon>
        <ListItemText primary={<Typography variant="h5" style={{fontSize:'1rem'}}>${props.tradePrice}</Typography>} secondary={<Typography variant="p" style={{ color: 'red' }}>{myProfPercent}</Typography>} sx={{maxWidth: 400}}/>
      </ListItem>
      <Divider variant="inset" component="li" />
    </List>
  );
      }
}
