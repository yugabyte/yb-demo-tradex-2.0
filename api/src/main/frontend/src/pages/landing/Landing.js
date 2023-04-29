import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone2";
import { useNavigate } from "react-router-dom";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';



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
      overflow: "scroll",
      backgroundColor:"#F1F1F7",
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

export default function Landing() {
  const classes = useStyles();
  let navigate = useNavigate();
  return (
    <Phone>
      <div className={classes.landingWrapper}>

        <div className={classes.landingHeader}>
        
        <Box sx={{mx:2,mt:16}}>

          <Grid container spacing={2}>
            
            <Grid item xs={12}>

                <Grid>

                  <img src="./assets/img/tradexlogo.png" className={classes.landing} width="175" height="236" alt="Tradex" />
                   
                </Grid>

            </Grid>

          </Grid>

        </Box>

        <Box sx={{mt:16}}>

          <Grid item xs={12}>

            <Button className="css-uyw6s9-MuiButtonBase-root-MuiButton-root" variant="contained" color="success" onClick={() => {navigate("/login");}} sx={{width:270,borderRadius:3}}>
                Get Started
            </Button>

          </Grid>

        </Box>

        </div>
        
      </div>

    </Phone>
  );
}
