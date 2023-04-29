import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import { useState, useEffect } from "react";
import getJSON from "../../services/rest";
import AppContext from "../../contexts/AppContext";
import { useContext } from "react";
import { Link } from "react-router-dom";
import Phone from "../../components/phone/Phone";
import WestIcon from '@mui/icons-material/West';
import { Typography } from "@mui/material";
import Bottommenu from "./Bottommenu";
import {SlimTradeOrderList} from "../../components/trades/slimTradeOrderList";


const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
      paddingTop: theme.spacing(4),
      paddingBottom: theme.spacing(4),
      width: "100%",
      display: "flex",
      flex: "1 1 auto",
      flexDirection: "column",
      alignItems: "center",
      overflowY: "scroll",
      overflowX: "hidden",
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

export default function OrderHistoryPage() {
  const [value, setValue] = React.useState("");
  const[loaded,setLoaded] = React.useState(false);
    
    const handleInput = (e) => {
      setValue(e.target.value);
    }


  const classes = useStyles();

  const { handleQueryLogs } = useContext(AppContext);
  const [tradeOrders, setTradeOrders] = useState([]);
 

  const loadUserTrades = async () =>{

    try {
      const resp = await getJSON('/api/trades?prevId=0&limit=100');
      setTradeOrders(resp.data);
      handleQueryLogs(resp.queries, resp.explainResults, resp.latencyMillis, resp.connectionInfo);

    }
    catch (e) {
      console.log("error in fetching stock info", e);
    }
  }

  useEffect( ()=> {

    loadUserTrades().then( () => { 
      setLoaded(true);
    });

  }, []);


  const getDisplayContent = () => {

    if ( loaded && tradeOrders.length >= 1){
        return ( <SlimTradeOrderList tradeOrders={tradeOrders} />);
    }
    else if (loaded) {
      return ( <Typography variant="h6" sx={{ textAlign:"center" }} > No data to display. </Typography>    );
    }
    else {
      return ( <Typography variant="h6" sx={{ marginLeft:"40px", lineHeight: "10px"}} > Loading </Typography>    );
    }

  }

  return (
    <Phone>
      <div className={classes.landingWrapper}>   
        
        <Box  sx={{marginRight:"80px"}}>
          <Grid container spacing={2} >
              <Grid item xs={2}>
                  <Link to={`../dashboard`}><WestIcon /></Link>
              </Grid>

              <Grid item xs={10}>
                  <Typography variant="h6" sx={{ marginLeft:"40px", lineHeight: "10px"}} >Order History</Typography>         
              </Grid>
          </Grid>
        </Box>

        <Box sx={{marginTop:"40px"}}>
          <Grid columnSpacing={0} columnGap={0} rowGap={0}>       

                  <Grid container sx={{width:"330px"}}>
                    <Grid item xs={8}>
                    <h6>My Orders</h6>
                    </Grid>  
                </Grid>

               {getDisplayContent()}
             
            </Grid>
         </Box>  
      </div>
      <Bottommenu />
      </Phone>
  );
}
