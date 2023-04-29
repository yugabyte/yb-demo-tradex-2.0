import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import { useState, useEffect } from "react";
import getJSON from "../../services/rest";
import AppContext from "../../contexts/AppContext";
import { useContext } from "react";
import StockList from "../../components/trades/stockList";
import { Link } from "react-router-dom";
import Phone from "../../components/phone/Phone";
import WestIcon from '@mui/icons-material/West';
import InputAdornment from "@material-ui/core/InputAdornment";
import TextField from "@material-ui/core/TextField";
import SearchIcon from "@material-ui/icons/Search";
import { IconButton } from "@material-ui/core";
import CancelRoundedIcon from "@material-ui/icons/CancelRounded";
import { Divider, Typography } from "@mui/material";
import Bottommenu from "../landing/Bottommenu";



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

export default function Exchange() {
  const [value, setValue] = React.useState("");

  const classes = useStyles();

  const { handleQueryLogs } = useContext(AppContext);
  const [allStocks, setAllStocks] = useState([]);  
  const [favStocks, setFavStocks] = useState([]);
  
  const [favToDisplay, setFavToDisplay] = useState([]);
  const [allToDisplay, setAllToDisplay] = useState([]);

  const loadAllStocksInfo = async () =>{

    try {
      const resp = await getJSON('/api/stocks');
      setAllStocks(resp.data);
      handleQueryLogs(resp.queries, resp.explainResults, resp.latencyMillis, resp.connectionInfo);     
      setAllToDisplay(resp.data);
    }
    catch (e) {
      console.log("error in fetching stock info", e);
    }
  }

  const loadFavStocksInfo = async () =>{

    try {
      const resp = await getJSON('/api/favstocks');
      setFavStocks(resp.data);
      handleQueryLogs(resp.queries, resp.explainResults, resp.latencyMillis, resp.connectionInfo);     
      setFavToDisplay(resp.data);
      
    }
    catch (e) {
      console.log("error in fetching stock info", e);
    }
  }

  useEffect( ()=> {
    loadAllStocksInfo();
    loadFavStocksInfo();
  }, []);


  const handleInput = (e) => {
    let searchInput = e.target.value.toLowerCase();
    setValue(searchInput);

    if ( searchInput === '' || searchInput === undefined ){
      setFavToDisplay(favStocks);
      setAllToDisplay(allStocks);
    }
    else {
      setFavToDisplay(favStocks.filter(item => ( item.stockSymbol.toLowerCase().includes(searchInput) || item.stockCompany.toLowerCase().includes(searchInput)) ));
      setAllToDisplay(allStocks.filter(item => ( item.stockSymbol.toLowerCase().includes(searchInput) || item.stockCompany.toLowerCase().includes(searchInput)) ));

    }
  }

  const clearSearchField = (e) => {
    setValue('');
    setFavToDisplay(favStocks);
    setAllToDisplay(allStocks);
  }

  return (
    <Phone>
      <div className={classes.landingWrapper}>
    
        <Box  sx={{marginRight:"153px"}}>
          <Grid container spacing={2} >
              
              <Grid item xs={4}>
                  <Link to={`../dashboard`}><WestIcon /></Link>
              </Grid>

              <Grid item xs={8}>
                  <Typography variant="h5" sx={{lineHeight: "20px"}} >Exchange</Typography>
              </Grid>

          </Grid>
         </Box>

<Box sx={{marginTop:"20px",width:"90%"}}>
  <Grid container>
    <Grid item xs={12}>
    <TextField
      placeholder="Select stock"
      type="text"
      variant="outlined"
      fullWidth
      size="small"
      onChange={handleInput}
      
      value={value}
      InputProps={{
        startAdornment: (
          <InputAdornment position="start">
            <SearchIcon />
          </InputAdornment>
        ),

        endAdornment: value && (
          <IconButton
            aria-label="toggle password visibility"
            onClick={clearSearchField}
          >
            <CancelRoundedIcon />
          </IconButton>
        )
      }}
    />
    </Grid>
  </Grid>
</Box>



<Box sx={{marginTop:"40px"}}>
          <Grid columnSpacing={0} columnGap={0} rowGap={0}>       

                  <Grid container sx={{width:"330px"}}>
                  <Grid item xs={8}>
                  <h6>My favourite</h6>
                  </Grid>
                 
                </Grid>

                  <StockList trades={favToDisplay} />
             
            </Grid>
            </Box>
            <Divider color="black" sx={{width:"88%", marginLeft:"21px"}} flexItem></Divider>
          <Box sx={{marginTop:"20px"}}>
            <Grid container>
            <Grid item xs={12}>                 

                  <Grid container sx={{width:"330px"}}>
                  <Grid item xs={8}>
                  <h6>Top trend</h6>
                  </Grid>          
           
                  
                </Grid>

                  <StockList trades={allToDisplay} />
            
            </Grid>
            </Grid>
          </Box>
  
      </div>
      <Bottommenu />
      </Phone>
  );
}
