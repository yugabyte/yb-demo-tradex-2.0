// import * as React from 'react';
// import List from '@mui/material/List';
// import ListItem from '@mui/material/ListItem';
// import ListItemText from '@mui/material/ListItemText';
// import ListItemAvatar from '@mui/material/ListItemAvatar';
// import ArrowDropDownCircleRoundedIcon from '@mui/icons-material/ArrowDropDownCircleRounded';
// import Avatar from '@mui/material/Avatar';
// import ArrowCircleDownIcon from '@mui/icons-material/ArrowCircleDown';
// import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
// import { green, red } from '@mui/material/colors';
// import { ListItemIcon,Typography } from '@mui/material';
// //import Typography from '@mui/material/Typography';


// export default function TradeList2(props) {
//   //const imgUrl = '../../assets/symbols/' + props.tradeImg + '.png';
//   const CorrectStatus = props => {
//     if (props.tradeStatus == false){
//     <ArrowCircleUpIcon />
//     }else{
//       <ArrowCircleDownIcon />
//     }
//   }
//   return (
//     <List sx={{ width: '100%', maxWidth: 380, bgcolor:'background.paper' }}>
//       <ListItem key={props.key}>
//         <ListItemAvatar>
//         {/* <Avatar key={props.key} src={require('../../assets/symbols/' + props.tradeImg + '.png')} /> */}
//         {/* <Avatar alt="Remy Sharp" src="./assets/img/microsoft.png" /> */}
//         </ListItemAvatar>
//         <ListItemText primary={props.tradeSym} secondary={props.tradeCompany} />
//         <ListItemIcon>
//         <Typography variant="h6" spacing={2}>
//       {/* {console.log (props.tradeStatus, 'hai')} */}
//         <ArrowCircleDownIcon  style={{color: 'red'}}/> 

//         {/* {props.tradeStatus}="false" ? <ArrowCircleUpIcon /> ; : <ArrowCircleDownIcon />; */}
         
//       </Typography>
//         </ListItemIcon>
//         <ListItemText primary={<Typography variant="h5" style={{fontSize:'1rem'}}>${props.tradePrice}</Typography>}  secondary={<Typography variant="p" style={{ color: 'red' }}>{props.tradeProfitPercent}</Typography>}/>
//       </ListItem>
      
//     </List>
//   );
// }

// import * as React from 'react';
// import List from '@mui/material/List';
// import ListItem from '@mui/material/ListItem';
// import ListItemText from '@mui/material/ListItemText';
// import ListItemAvatar from '@mui/material/ListItemAvatar';
// // import ArrowDropDownCircleRoundedIcon from '@mui/icons-material/ArrowDropDownCircleRounded';
// import Avatar from '@mui/material/Avatar';
// import ArrowCircleDownIcon from '@mui/icons-material/ArrowCircleDown';
// import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
// import { green, red } from '@mui/material/colors';
// import { ListItemIcon,Typography } from '@mui/material';
// import Divider from '@mui/material/Divider';
// import { makeStyles } from "@material-ui/core/styles";


// const useStyles = makeStyles((theme) => {
//   return {
//     tradeItem: {
//    marginBottom: '50px',
//     },
    
//   };
// });


// export default function TradeList2(props) {
//   const classes = useStyles();
//   // const imgUrl = '../../assets/symbols/' + props.tradeImg + '.png';
 
//   if (props.tradeStatus) {
//     return (
//       <List sx={{ width: '100%', maxWidth: 380, bgcolor:'background.paper' }}>
//         <ListItem key={props.key} className={classes.tradeItem}>
//           <ListItemAvatar>
//           <Avatar key={props.key} src={require('../../assets/symbols/' + props.tradeImg + '.png')} />
         
//           </ListItemAvatar>
//           <ListItemText primary={props.tradeSym} secondary={props.tradeCompany }  />
//           <ListItemIcon>
//           {/* if (props.tradeStatus) {
            
//           } */}
//           <ArrowCircleUpIcon sx={{ color: green[500]  }}/>
            
//           </ListItemIcon>
//           <ListItemText primary={<Typography variant="h5" style={{fontSize:'1rem'}}>${props.tradePrice}</Typography>}  secondary={<Typography variant="p" style={{ color: 'green' }}>{props.tradeProfitPercent}</Typography>}/>
//         </ListItem>
//         <Divider variant="inset" component="li" />
//       </List>
//     ); 
//   }else{

  
//   return (
//     <List sx={{ width: '100%', maxWidth: 380, bgcolor:'background.paper' }}>
//       <ListItem key={props.key}>
//         <ListItemAvatar>
//         {/* <Avatar key={props.key} src={require('../../assets/symbols/' + props.tradeImg + '.png')} /> */}
       
//         </ListItemAvatar>
//         <ListItemText primary={props.tradeSym} secondary={props.tradeCompany }  />
//         <ListItemIcon>
//         {/* if (props.tradeStatus) {
          
//         } */}
//         <ArrowCircleDownIcon sx={{ color: red[500]  }}/>
          
//         </ListItemIcon>
//         <ListItemText primary={<Typography variant="h5" style={{fontSize:'1rem'}}>${props.tradePrice}</Typography>} secondary={<Typography variant="p" style={{ color: 'red' }}>{props.tradeProfitPercent}</Typography>}/>
//       </ListItem>
    
//     </List>
//   );
//       }
// }
