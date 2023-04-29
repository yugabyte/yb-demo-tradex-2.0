import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import Select from '@mui/material/Select';
import InputLabel from '@mui/material/InputLabel';
import { Link } from "react-router-dom";
import FormControl from '@mui/material/FormControl';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import Avatar from '@mui/material/Avatar';
import WestIcon from '@mui/icons-material/West';
import { CardContent, IconButton, Typography } from "@mui/material";
import MenuItem from '@mui/material/MenuItem';
import CardHeader from '@mui/material/CardHeader';
import Manu12 from "./paymenu";
import { FormLabel, RadioGroup, FormControlLabel, Radio } from "@mui/material";


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



export default function Paymentdetail(props) {
  const [choose, setNone] = React.useState('');
  const [tradeBuyAmount, setTradeBuyAmount] = React.useState(props?.tradeAmount);
  const[action,setAction] = React.useState('buy');
  const handleRadioChange = (event, value) => { setAction(value); }

  const handleChange = (event) => {
    setNone(event.target.value);
  };

  const classes = useStyles();
  const tradeDetails = props.payDetails;
  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
  
      <main className="main-content">

        <div className="container-fluid py-4">

        <Box sx={{mx:5}}>

          <Grid container spacing={2} >

            <Grid item xs={2}>

              <Link to={'../candlechart'} state={{ companyName: tradeDetails.tradeSymbol }}> <WestIcon /> </Link>

            </Grid>

            <Grid item xs={10}>

                <Grid sx={{mx:5}}>

                    <Avatar sx={{width:"90px",height:"90px",marginLeft:"9px"}} src={tradeDetails.tradeImg}/>
                   
                </Grid>

            </Grid>

          </Grid>

        </Box>

        <Box sx={{mx:5}}>

          <Grid container spacing={3} sx={{mx:5}}>

              <Grid item xs={12} sx={{mx:5}}>

                  <Typography variant="p" sx={{fontSize:"18px !important",marginLeft:"12px"}}>{tradeDetails.tradeSymbol}</Typography><br/>

                  <Typography variant="p" sx={{color:"#9e9e9e",fontSize:"16px",marginLeft:"6px"}}>{tradeDetails.tradeName}</Typography>

              </Grid>

          </Grid>

        </Box>

        <Box sx={{mx:5}}>

            <Grid container spacing={2} sx={{my:1,mt:5}}>

                  <CardContent sx={{textAlign:"center",}}>

                  <input 
                    type="number"
                    step="any"
                    min="1"
                    max="1000000"
                    placeholder="Enter Amount" 
                    className="form-control amt" 
                    value={tradeBuyAmount}
                    onChange={(e) => setTradeBuyAmount(e.target.value)}
                    
                  />
                      
                  </CardContent>
         
            </Grid>

            <Divider variant="middle" sx={{borderColor:"#000",marginTop:"-30px"}} />

        </Box>
    


              <Box sx={{padding: "30px", width: "370px", height: "150px", mt:"8" , display:"flex", justifyContent:"center"}}>
              <FormControl fullWidth sx={{mt:3}}>
                <FormLabel id="trade-action-group-label"><Typography sx={{ textTransform: "capitalize !important",fontWeight:"700",color:"#000" }}>Action :</Typography></FormLabel>
                <RadioGroup
                  aria-labelledby="trade-action-group-label"
                  defaultValue="buy"
                  name="trade-action-group" onChange={handleRadioChange} sx={{marginLeft:"50px"}}
                ><Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3}} sx={{justifyContent:"center",marginLeft:"26px",marginTop:"-41px"}}>
                  <Grid item xs={4}> <FormControlLabel value="buy" control={<Radio />} sx={{fontWeight:"700 !important"}} label="Buy" /></Grid>
                  <Grid item xs={4}><FormControlLabel value="sell" control={<Radio />} sx={{fontWeight:"700 !important"}} label="Sell" /></Grid>
                </Grid>                 
                  
                </RadioGroup>
              </FormControl>
              </Box>


              <Box sx={{ padding: "20px", width: "370px", height: "218px", marginTop: "10px" }}>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Typography variant="p" sx={{ fontSize: "18px !important", marginLeft: "12px" }}>Choose payment method</Typography><br />
                  </Grid>
                </Grid>

                <Box sx={{ minWidth: 120, mx: 2, my: 2, height: "80px" }}>
                  <FormControl fullWidth>
                    <InputLabel id="demo-simple-select-label" sx={{ textTransform: "capitalize !important" }}>Select</InputLabel>
                    <Select
                      labelId="demo-simple-select-label"
                      id="demo-simple-select"
                      value={choose}
                      label="Choose"
                      onChange={handleChange}
                    >
                      <MenuItem value={1} >
                        <CardHeader sx={{ padding: "0px !important" }}
                          avatar={
                            <Avatar sx={{ bgcolor: "#fff" }} aria-label="recipe" variant="square">
                              <svg width="52" height="52" viewBox="0 0 52 52" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M4.11026 8.38904C3.716 8.3896 3.32572 8.46793 2.96178 8.61956C2.59784 8.77119 2.26739 8.99313 1.98936 9.27267C1.71132 9.55221 1.49118 9.88386 1.34152 10.2486C1.19186 10.6134 1.11564 11.0041 1.11722 11.3983V40.4858C1.1137 41.2794 1.42517 42.0419 1.98325 42.6061C2.54134 43.1703 3.30046 43.49 4.09401 43.4951L47.8898 43.6109C48.2841 43.6104 48.6743 43.532 49.0383 43.3804C49.4022 43.2288 49.7327 43.0068 50.0107 42.7273C50.2887 42.4477 50.5089 42.1161 50.6585 41.7513C50.8082 41.3866 50.8844 40.9959 50.8828 40.6016V11.5141C50.8864 10.7206 50.5749 9.95801 50.0168 9.39383C49.4587 8.82965 48.6996 8.50992 47.906 8.50482L4.11026 8.38904Z" fill="#3AA1D9" />
                                <path d="M1.11719 15.7168H50.8828V22.9216H1.11719V15.7168Z" fill="#E7EFF1" />
                                <path d="M39.0588 39.1647C41.9293 39.1647 44.2563 36.8188 44.2563 33.925C44.2563 31.0312 41.9293 28.6853 39.0588 28.6853C36.1883 28.6853 33.8613 31.0312 33.8613 33.925C33.8613 36.8188 36.1883 39.1647 39.0588 39.1647Z" fill="#FCE444" />
                                <path d="M25.1716 31.6981H7.98694C7.27845 31.6981 6.7041 32.2725 6.7041 32.981V35.2619C6.7041 35.9704 7.27845 36.5448 7.98694 36.5448H25.1716C25.8801 36.5448 26.4545 35.9704 26.4545 35.2619V32.981C26.4545 32.2725 25.8801 31.6981 25.1716 31.6981Z" fill="#A1DAF9" />
                              </svg>

                            </Avatar>
                          }
                          action={
                            <IconButton aria-label="settings">

                            </IconButton>
                          }
                          title="Debit Card"
                          subheader="Invest small amounts"
                        />
                      </MenuItem>

                      <MenuItem value={2} >
                        <CardHeader sx={{ padding: "0px !important" }}
                          avatar={
                            <Avatar sx={{ bgcolor: "#fff" }} aria-label="recipe" variant="square" src="assets/img/bt1.png">
                            </Avatar>
                          }
                          action={
                            <IconButton aria-label="settings">

                            </IconButton>
                          }
                          title="Bank transfer"
                          subheader="Invest big amounts"
                        />
                      </MenuItem>
                      <MenuItem value={3} >
                        <CardHeader sx={{ padding: "0px !important" }}
                          avatar={
                            <Avatar sx={{ bgcolor: "#fff" }} aria-label="recipe" variant="square" src="assets/img/bt2.png">
                            </Avatar>
                          }
                          action={
                            <IconButton aria-label="settings">
                            </IconButton>
                          }
                          title="Bank transfer"
                          subheader="Invest big amounts"
                        /></MenuItem>
                      <MenuItem value={4}>
                        <CardHeader sx={{ padding: "0px !important" }}
                          avatar={
                            <Avatar sx={{ bgcolor: "#fff" }} aria-label="recipe" variant="square" src="assets/img/bt3.png">
                            </Avatar>
                          }
                          action={
                            <IconButton aria-label="settings">
                            </IconButton>
                          }
                          title="Bank transfer"
                          subheader="Invest big amounts"
                        /></MenuItem>
                    </Select>
                  </FormControl>
                </Box>
              </Box>


 



</div>

  </main>

        </div>
      </div>

        <Manu12 confirmorderDetails={tradeDetails} selectedValue={choose} tradeAmount={tradeBuyAmount} action={action} />
    </Phone>
  );
}
