import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import { Link } from "react-router-dom";
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import Avatar from '@mui/material/Avatar';
import WestIcon from '@mui/icons-material/West';
import { CardContent, Typography } from "@mui/material";
import Card from '@mui/material/Card';
import PaidOutlinedIcon from '@mui/icons-material/PaidOutlined';
import EqualizerOutlinedIcon from '@mui/icons-material/EqualizerOutlined';
import PieChartOutlineRoundedIcon from '@mui/icons-material/PieChartOutlineRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import { useLocation } from 'react-router-dom';


const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
      paddingTop: theme.spacing(4),
      paddingBottom: theme.spacing(4),
      paddingLeft: theme.spacing(4),
      paddingRight: theme.spacing(4),
      height: "600px",
      width: "100%",
      display: "flex",
      flex: "1 1 auto",
      flexDirection: "column",
      alignItems: "center",
      overflowY: "hidden",
      overflowX: "hidden",
      backgroundColor:"#F0F4F7",
    },
    landingHeader: {
      flexBasis: "300px",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      gap: "30px",
    },
    logo: {
      height: "88px",
      width: "88px",
    },
    appHeading: {
      color: theme.palette.text.primaryPurple,
      fontSize: "30px",
      fontWeight: "500",
    },
    landingContent: {
      display: "flex",
      flexDirection: "column",
      flex: "1 1 auto",
      alignItems: "center",
      justifyContent: "center",
      gap: "30px",
    },
    instructions: {
      textAlign: "center",
      width: "80%",
    },
    loadingCircles: {
      height: "40px",
      width: "40px",
    },
  };
});



export default function Confirmorder() {
  const location = useLocation()
  const { confirmorderDetails, selectedValue, tradeAmount, action } = location.state
  const classes = useStyles();

  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
  
      <main className="main-content">

        <div className="container-fluid py-4">

        <Box sx={{mx:5}}>
          <Grid container spacing={2} >
            <Grid item xs={2}>
              <Link to={`../payentry`} state={{ tradeDetails:  confirmorderDetails, tradeAmount: tradeAmount, action: action}}>  <WestIcon /></Link>
            </Grid>

            <Grid item xs={10}>
                <Grid sx={{mx:5}}>  
                    <Typography variant="p" sx={{fontSize:"18px !important"}}>Confirm Order</Typography>
                </Grid>
            </Grid>
          </Grid>
        </Box>

    


        <Box sx={{mx:5}}>
            <Grid container spacing={2} sx={{my:1,mt:2,justifyContent:"center"}}>  
                  <CardContent sx={{textAlign:"center",px:10}}>
                        <Typography variant="p" sx={{color:"#9e9e9e",fontSize:"16px",marginLeft:"6px"}}>Total cost</Typography>
                        <Typography variant="h4" component="h2" sx={{color:"#000"}}>${tradeAmount}</Typography>
                  
                  </CardContent>
            </Grid>
        </Box>
    
        <Box sx={{mx:4}}>
          <Card sx={{ minWidth: 330}}>
              <Grid container spacing={1}>
                  <Grid item xs={12} sx={{ml:2,mt:2}}>
                      <Typography variant="p" sx={{textAlign:"left !important",fontSize:"14px"}}>Stock you {action} </Typography>
                      <Divider sx={{marginTop:"15px"}} />
                  </Grid>
                 
                  <Grid item xs={2} sx={{ml:2,mb:2}}>
                      <Avatar  src={confirmorderDetails.tradeImg}/>
                  </Grid>

                  <Grid item xs={6}>
                      <Typography variant="p" sx={{lineHeight:"1.75em"}}>{confirmorderDetails.tradeSymbol}</Typography><br/>
                      <Typography variant="p">{confirmorderDetails.tradeName}</Typography>
                  </Grid>

                  <Grid item xs={3}>  
                      <Typography variant="p" sx={{float:"right !important",lineHeight:"2.5em"}}>${confirmorderDetails.tradePrice}</Typography>
                  </Grid>
              </Grid>
          </Card>
        </Box>

        <Box sx={{mx:4,mt:3}}>
          <Card sx={{ minWidth: 330}}>
              <Grid container spacing={1} >
                  <Grid item xs={12} sx={{ml:2,mt:2}}>
                      <Typography variant="p" sx={{textAlign:"left !important",fontSize:"14px"}}>Payment method</Typography>
                      <Divider sx={{marginTop:"15px"}} />
                  </Grid>
                

                  <Grid item xs={2} sx={{ml:2,mb:2}}>
                      <Avatar alt="spotify" src="./assets/img/bt1.png"/>
                  </Grid>

                  <Grid  item xs={6}  >
                      <Typography variant="p" sx={{lineHeight:"1.75em"}}>Bank of America</Typography><br/>
                      <Typography variant="p">*** *** *** 123</Typography>
                  </Grid>
                 
              </Grid>
          </Card>
        </Box>


        <Box sx={{backgroundColor:"#fff", padding:"20px",height:"218px",mt:5.5}}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
                <Typography variant="p" sx={{fontSize:"12px !important",marginLeft:"12px"}}>Payment details</Typography><br/>
            </Grid>
          </Grid>

          <Box sx={{m:2,mb:1}}>
            <Grid container>
                <Divider/>
                <Grid item xs={1}>
                    < PaidOutlinedIcon />
                </Grid>
                      
                <Grid item xs={8}>
                    <Typography sx={{lineHeight:"1.85",textAlign:"start !important",ml:1,fontSize:"14px"}}>Funding source</Typography>
                </Grid>
                
                <Grid item xs={3}  >
                    <Typography variant="p" sx={{float:"right !important",fontSize:"14px",color:"#1573FE"}}>Tradebase</Typography>
                </Grid>         

                <Grid item xs={1} >
                    <PieChartOutlineRoundedIcon />
                </Grid>
                        
                <Grid item xs={8}>
                    <Typography sx={{lineHeight:"1.85",textAlign:"start !important",ml:1,fontSize:"14px"}}>Approx. Share Price</Typography>
                </Grid>
                
                <Grid item xs={3}  >
                    <Typography variant="p" sx={{float:"right !important",fontSize:"14px"}}>${tradeAmount - ((tradeAmount * 4)/100)}</Typography>
                </Grid>     

                <Grid item xs={1} >
                    <EqualizerOutlinedIcon />
                </Grid>
                      
                <Grid item xs={8}>
                    <Typography sx={{lineHeight:"1.85",textAlign:"start !important",ml:1,fontSize:"14px"}}>Approx. Shares</Typography>
                </Grid>
                
                <Grid item xs={3}>
                    <Typography variant="p" sx={{float:"right !important",fontSize:"14px"}}>{Math.round((tradeAmount / confirmorderDetails.tradePrice + Number.EPSILON) * 100) / 100}</Typography>
                </Grid>         

                <Grid item xs={1} >
                    <NotificationsRoundedIcon />
                </Grid>
                        
                <Grid item xs={8} >
                    <Typography sx={{lineHeight:"1.85",textAlign:"start !important",ml:1,fontSize:"14px"}} >Fee</Typography>
                </Grid>
                
                <Grid item xs={3} >
                    <Typography variant="p" sx={{float:"right !important",fontSize:"14px"}}>${(tradeAmount * 4)/100}</Typography>
                </Grid>                  
            </Grid>
          </Box>
          
          <Grid container sx={{mx:3}} >    
                <Grid item xs={12} >
                 <Link to={`../pin`} state={{ confirmorderDetails: confirmorderDetails, selectedValue: selectedValue, tradeAmount: tradeAmount, action: action }}> <Button variant="contained" size="large" sx={{backgroundColor:"#7879F1",width:300,borderRadius:2}} >Confirm Payment</Button>
                 </Link>  
                </Grid>
          </Grid>
        </Box>
</div>
  </main>
</div>
</div>
</Phone>
  );
}
