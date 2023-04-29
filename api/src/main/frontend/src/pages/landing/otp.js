import OtpInput from "react-otp-input";
import Typography from "@material-ui/core/Typography";
import { makeStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import Box from '@mui/material/Box';
import WestIcon from '@mui/icons-material/West';
import Phone from "../../components/phone/Phone2";
import { Link } from "react-router-dom";

import { useLocation } from 'react-router-dom'
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import { useNavigate } from 'react-router';
import { useContext, useState } from "react";
import AppContext from "../../contexts/AppContext";
import { doSignUp } from "../../services/authService";

const useStyles = makeStyles(theme => ({
  grid: {
    backgroundColor: "grey",
    height: "auto",
    textAlign: "center"
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
    width:"300px",
    marginTop:"50px",
    padding:"20px"
  },
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center"
  }
}));

export default function Otp() {
  const [state,setState] =useState("");
  const classes = useStyles();
  const location = useLocation();
  const { name, email, password, phoneNumber } = location.state;
  const { handleQueryLogs } = useContext(AppContext);
  let navigate = useNavigate();
  const [show, setShow] = useState(false);
  let [error, setError] = useState(null);
  let [phase, setPhase] = useState("")
  // const theme = useTheme();

  const handleChange =  (otp) => { 
    setState( otp )
  }
  const submitUserProfile = async () =>{
    try {
      if(state.length < 4){
        setShow(true);
        return;
      }
      else{
        setShow(false);
      }


      let postUserDataReqBody = {
        "id": {
          "id": 0,
          "preferredRegion": "boston"
        },
        "email": email,
        "passkey": password,
        "enabled": true,
        "language": "English_US",
        "securityPin": 0,
        "personalDetails": {
          "fullName": name,
          "phone": phoneNumber,
          "country": "string",
          "gender": "MALE",
          "address": "Austin"
        },
        "notifications": {
          "generalNotification": "ENABLED",
          "sound": "ENABLED",
          "vibrate": "ENABLED",
          "appUpdates": "ENABLED",
          "billReminder": "ENABLED",
          "promotion": "ENABLED",
          "discountAvailable": "ENABLED",
          "paymentReminder": "ENABLED",
          "newServiceAvailable": "ENABLED",
          "newTipsAvailable": "ENABLED"
        },
        "favourites": [
          0
        ]
      };

      postUserDataReqBody["securityPin"] = parseInt(state);
      setError('');
      setPhase("")
      doSignUp(postUserDataReqBody).then(        
        (res) => {
          if (res.login){
            navigate('/success')
            setPhase("success")
          } else {
            if ( res.fieldErrors !== null ){
              setError(res.fieldErrors[0].error);
            }
            else {
              setError(res.message);
            }            
          }          
        }
      ).catch( (err) =>{
        console.error(err);
        setError(err.message);
        setPhase("");
      });

    }
    catch (e) {
      console.log('error', e);
    }
  }
  return (
  <Phone>
          
      <Box sx={{marginTop:"-130px" }}>
        <Grid container spacing={1} >
          <Grid item xs={12} >
            <Link to={`/createnew`}>  <WestIcon sx={{ marginRight: "277px" }} /></Link>
          </Grid>
        </Grid>
      </Box>
      <CssBaseline />
      <div className={classes.paper}>

  <Box         
          style={{ backgroundColor: "white" }}
          className={classes.grid}>
     <Box>
           <Grid  container >
              <Grid item xs={12} >
                 <Typography variant="h1" >Enter your PIN</Typography>
                 <Typography variant="p"  sx={{fontSize:"18px !important"}}> Please enter your PIN to confirm payment </Typography>
              </Grid>
  
           </Grid>
     </Box>

     <Box sx={{marginTop:"100px"}}>
          <Grid
            item
            xs={12}
            container
        
            alignItems="center"
            direction="column">
          <Grid item spacing={3}>
         
       <OtpInput
        value={state}
        onChange={handleChange}
        numInputs={4}
        isInputNum={true}
  
      inputStyle={{
        width: "3rem",
        height: "3rem",
        margin: "0 1rem",
        fontSize: "1.2rem",
        color:"black",
        
        borderRadius: 4,
        border: "1px solid rgba(0,0,0,0.3)" }}  />
          </Grid>
            <Grid item>
             
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={submitUserProfile}
                    className={classes.submit}>
                    SUBMIT
                  </Button>
          
            </Grid>
          </Grid>
     </Box>
  </Box>
  {show ? 
        <Stack sx={{ width: '100%',visibility: {show} }} spacing={2}>
          <Alert severity="error" onClose={() => setShow(false)}>Please Enter 4 Digit PIN.</Alert>
        </Stack> : null}
  {
     error && <>
      <Typography component="p" variant="body1" textAlign="center" color='red'>
        Sign Up Failed -  {error}
      </Typography>
    </>  
  }        
</div>
 </Phone>
  );
}

