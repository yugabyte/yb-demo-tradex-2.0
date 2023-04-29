import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import Candlemenu from "./candlemenu";
import CandleStick from "./Candlestick";
import { Link } from "react-router-dom";
import AppContext from "../../contexts/AppContext";
import { useContext, useState, useEffect } from "react";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Avatar from '@mui/material/Avatar';
import WestIcon from '@mui/icons-material/West';
import { CardContent, Typography } from "@mui/material";
import { useLocation } from 'react-router-dom'
import getJSON from "../../services/rest";


const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
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

export default function Candlechart() {
  const classes = useStyles();
  const location = useLocation()
  const { companyName } = location.state
  const { handleQueryLogs, currentDatabase, trafficLocation } = useContext(AppContext);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [items, setItems] = useState([]);
  const imgUrl = 'https://yb-global-tradex-imgstore.s3.amazonaws.com/img/symbols/' + items.symbol + '.png';
  const marketCap = covertvalue(items.marketCap);
  const marketVol = covertvalue(items.volume);
  const marketAvgVol = covertvalue(items.avgVolume);

  function covertvalue(inputVal) {
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
    return convertedvalue;
  }


  const fetchStockHistory = async () =>{
    
    try {
      const resp = await getJSON("/api/stocks/"+companyName+"?hist=true");
      setIsLoaded(true);
      setItems(resp.data);
      handleQueryLogs(resp.queries, resp.explainResults, resp.latencyMillis, resp.connectionInfo);
     
    }
    catch (e) {
      console.log("error in fetching database nodes", e);
    }
  }


  useEffect(() => {
   
    fetchStockHistory().then( () => {});

  }, [currentDatabase, trafficLocation]);

  if (error) {
    return <div>Error: {error.message}</div>;
  } else if (!isLoaded) {
    return(
    <>
      <Phone>
        <div>Loading...</div>
      </Phone>
    </>
    );
  } else {
    return (
      <>
        <Phone>
          <div className={classes.landingWrapper}>
            <div className={classes.landingHeader}>
              <ul>
         
              </ul>
              <main className="main-content position-relative border-radius-lg ">
                <div className="container-fluid mx-0 px-0">
                  <Box>
                    <Grid container spacing={3} >
                      <Grid item xs={2}>
                        <Link to={`../dashboard`}>  <WestIcon /></Link>
                      </Grid>

                      <Grid item xs={10} sx={{ marginTop: "-8px" }}>
                        <Grid>
                          <Avatar src={imgUrl} />
                        
                        </Grid>

                        <Grid sx={{ marginTop: "-30px", marginLeft: "50px" }}>
                          <Typography variant="p" sx={{ fontSize: "18px !important" }}> {items.symbol}<span style={{ color: "#9e9e9e", fontSize: "16px" }}> ({items.company})</span></Typography>
                        </Grid>
                      </Grid>

          
                    </Grid>
                  </Box>

                  <Box>
                    <Grid container spacing={2} sx={{ mx: 4, my: 3,marginTop:"-10px" }}>
                      <CardContent sx={{ textAlign: "center", paddingLeft: "60px !important" }}>
                        <Typography variant="h4" component="h2">${items.highPrice}</Typography>
                       
                      </CardContent>

                      <CardContent sx={{ textAlign: "center", marginTop: "-20px !important", paddingLeft: "44px !important" }}>
                        <Typography variant="p" sx={{ fontSize: "14px !important", color: "#f00" }}>  +2,02(0,90%)<span style={{ color: "#289BF6" }}> Today</span></Typography>
                      </CardContent>
                    </Grid>
                  </Box>

                  <Box sx={{ flexGrow: 2, overflow: 'hidden', alignContent: 'center',marginTop:"-30px" }}>
                    <Grid >
                      <CandleStick chartData={items.historicalQuoteList} />
                    </Grid>
                  </Box>

                  <Box sx={{ flexGrow: 2, overflow: 'hidden',marginTop:"-30px"}}>
                    <Grid container spacing={2} >
                      <Grid item xs={12} >
                        <Typography variant="h6" component="h2">Statistics</Typography>
                      </Grid>
                    </Grid>
                  </Box>

                  <Box sx={{ border: "1px solid #eee", borderRadius: "10px", padding: "20px",mt:2 }}>
                    <Grid container spacing={2} sx={{ px: 3 }}>
                      <Grid item xs={4}>
                        <Typography variant="p">Open</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ textAlign: "center" }}>High</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ textAlign: "center" }}>Low</Typography>
                      </Grid>
                    </Grid>

                    <Grid container spacing={2} sx={{ px: 3 }}>
                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ color: "#ffc307", fontSize: "16px", textAlign: "left", lineHeight: "28px" }}>{items.openPrice}</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ color: "#b706d9", fontSize: "16px", textAlign: "center", lineHeight: "28px" }}>{items.highPrice}</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ color: "#11cff2", fontSize: "16px", textAlign: "right", lineHeight: "28px" }}>{items.lowPrice}</Typography>
                      </Grid>
                    </Grid>

                    <Grid container spacing={2} sx={{ px: 3 }}>
                      <Grid item xs={4}>
                        <Typography variant="p">Volume</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ textAlign: "center" }}>Avg.Volume</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ textAlign: "center" }}>MarketCap</Typography>
                      </Grid>
                    </Grid>

                    <Grid container spacing={2} sx={{ px: 3 }}>
                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ fontSize: "16px", textAlign: "left", lineHeight: "28px" }}>{marketVol}</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p" sx={{ fontSize: "16px", textAlign: "center", lineHeight: "28px" }}>{marketAvgVol}</Typography>
                      </Grid>

                      <Grid item xs={4}>
                        <Typography variant="p"  sx={{ fontSize: "16px", textAlign: "right", lineHeight: "28px" }}>
                          {marketCap }
                          </Typography>
                      </Grid>
                    </Grid>
                  </Box>
                </div>
              </main>
            </div>
          </div>
          <Candlemenu tradeImg={imgUrl} tradeSymbol={items.symbol} tradeName={items.company} tradePrice={items.highPrice} tradeId={items.id}/>
        </Phone>
      </>
    );
  }
}