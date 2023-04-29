import { makeStyles } from "@material-ui/core/styles";
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';

import { useState, useEffect, useContext } from "react";
import getJSON from "../../services/rest";
import AppContext from "../../contexts/AppContext";
import TradeList from "../../components/trades/tradelist";
import StockPerfChart from "../../components/trades/stockPerfChart";
import Typography from '@mui/material/Typography';
import "./bottom1.css";
import { Link } from "react-router-dom";

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


function convertValue(inputVal) {
  let convertedvalue = undefined;
  if (inputVal) {
    convertedvalue = Math.abs(Number(inputVal)) >= 1.0e+9
      ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+9 + Number.EPSILON) * 100) / 100).toString() + "B"
      : Math.abs(Number(inputVal)) >= 1.0e+6
        ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+6 + Number.EPSILON) * 100) / 100).toString() + "M"
        : Math.abs(Number(inputVal)) >= 1.0e+3
          ? (Math.round((Math.abs(Number(inputVal)) / 1.0e+3 + Number.EPSILON) * 100) / 100).toString() + "K"
          : Math.abs(Number(inputVal));
  }
  return inputVal.toString().charAt(0) === '-' ? '-' + convertedvalue : convertedvalue;
}


export default function Portfolio() {
  const classes = useStyles();
  const { handleQueryLogs, currentDatabase, trafficLocation } = useContext(AppContext);
  const [trades, setTrades] = useState([]);
  const [pChartData, setPChartData] = useState(null);
  const [resultLength, setResultLength] = useState(1);
  const [portfolioTotal, setPortFolioTotal] = useState(0);
  //const [totalValue, setTotalValue] = useState(0);
  

  const loadPortfolio = async () =>{

    try {
      const resp = await getJSON('/api/portfolio');
     // console.log('resp', resp); 
      setTrades(resp.recentTrades);
      const getTradeOperation = await getJSON('/api/trades?limit=10');
     // console.log(getTradeOperation);
      handleQueryLogs(getTradeOperation.queries, getTradeOperation.explainResults, getTradeOperation.latencyMillis, getTradeOperation.connectionInfo);

      const getFavStocks = await getJSON('/api/favstocks');

      handleQueryLogs(getFavStocks.queries, getFavStocks.explainResults, getFavStocks.latencyMillis, getFavStocks.connectionInfo);
      let favStocks = getFavStocks.data.map(x => {
        return x.stockId;
      });

      localStorage.setItem('favStocks', JSON.stringify(favStocks));

      const portfolioChartData = await getJSON('/api/charts/portfolio-chart');
     // console.log('pchart', portfolioChartData);
      handleQueryLogs(portfolioChartData.queries, portfolioChartData.explainResults, portfolioChartData.latencyMillis, portfolioChartData.connectionInfo);

      setPortFolioTotal(convertValue(portfolioChartData.data.totalValue));
      setPChartData(portfolioChartData.data.chartValues);
      setResultLength(portfolioChartData.data.chartValues.length);
     
    }
    catch (e) {
      console.log("error in fetching portfolio", e);
    }
  }

  useEffect( ()=> {

    loadPortfolio().then( () => {
      //console.log('trades', trades);
    });

  }, [currentDatabase, trafficLocation]);

  return (
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>

        <Box sx={{ width: '100%'}}>

          <Grid container sx={{mx:2}}>

            <Grid item xs={10}>
              <Typography variant="h5">Portfolio</Typography>
            </Grid>


            <Grid item xs={12} sx={{mt:1}}>
              <Typography variant="p" sx={{fontSize:"12px !important",fontWeight:"400"}}>Your total balance</Typography>
            </Grid>

            <Grid item xs={3} sx={{mt:1}}>
              <Typography variant="p" sx={{fontSize:"24px !important"}}>${portfolioTotal}</Typography>
            </Grid>


            <Grid item>
              <StockPerfChart data={pChartData} resultLength={resultLength}  />
            </Grid>

            <Grid sx={{width:"330px"}}>

            <Typography variant="h6"> Top Stocks  </Typography>
            <Link to={`../exch`}>
        <Typography variant="body2"  sx={{float:"right",marginTop:"-21px",color:"#7879f1"}}>  View all   </Typography>
        </Link>
              <Card>
             
                <CardContent sx={{mt:3}}>

                  <TradeList trades={trades}  />
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Box>
        </div>
      </div>
  );
}
