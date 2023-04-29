import { makeStyles } from "@material-ui/core/styles";
import { useNavigate } from "react-router-dom";
import { Typography } from "@material-ui/core";
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { useLocation } from 'react-router-dom'




const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
      paddingTop: "0px",
      marginTop:"-20px",
      paddingBottom: theme.spacing(4),
      paddingLeft: theme.spacing(4),
      paddingRight: theme.spacing(4),
      height: "600px",
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
    paperContainer: {
      backgroundImage: `url(${"assets/img/green.png"})`,
      width: "390px",
      height: "263px",
  },
  };
});

export default function Trans() {
  const classes = useStyles();
  let navigate = useNavigate();
  const location = useLocation()
  const { confirmorderDetails, tradeAmount } = location.state
  return (
    <>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
        <main className="main-content mt-0 px-0 mx-0">
        <div className="page-header">

            <Box>
              <Grid container>
                <Paper className={classes.paperContainer} sx={{boxShadow:"none"}}>
                  <Stack direction="column" justifyContent="center" alignItems="center">
                    <Avatar alt="done" sx={{width:"64px",height:"64px",mt:12}} src="./assets/img/alert.png"/>
                  </Stack>
                </Paper>  
              </Grid>

              <Grid item xs={12} sx={{m:5}}>
                <Stack direction="column" justifyContent="center" alignItems="center">
                    <Typography variant="h6" component="h2" sx={{fontWeight:600,textAlign:"center"}}>Transaction Complete </Typography>
                </Stack>
                <Stack direction="column" justifyContent="center" alignItems="center" sx={{textAlign:"center",mt:1}}>
                    <Typography variant="p" sx={{fontSize:"12px !important",fontWeight:300,textAlign:"center !important",marginLeft:"12px"}}>Your transaction has  been completed. You purchased ${tradeAmount} of {confirmorderDetails.tradeSymbol}. </Typography>
                </Stack>
              </Grid>

              <Grid item xs={12} sx={{mx:5,mt:25}}>

                <Button variant="outlined" color="success" onClick={() => {navigate("/dashboard");}} sx={{color:"#000",backgroundColor:"#eee",border:"1px solid #D9D9D9",width:300,borderRadius:1,marginTop:"10px"}}>
                    Back to Home
                </Button>
              </Grid>
            </Box>
    </div>
    </main>
    </div>
    </div>

     
    </>
  );
}
