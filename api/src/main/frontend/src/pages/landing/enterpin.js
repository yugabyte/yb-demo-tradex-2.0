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
import { postJSON } from "../../services/rest";
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import { useNavigate } from 'react-router';
import { useContext, useState } from "react";
import AppContext from "../../contexts/AppContext";
import OTPInput  from "otp-input-react";

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
    width: "300px",
    marginTop: "50px",
    padding: "20px"
  },

}));

export default function Enterpin() {

  let navigate = useNavigate();
  const location = useLocation()
  const { confirmorderDetails, selectedValue, tradeAmount, action } = location.state
  const [show, setShow] = useState(false);
  const { handleQueryLogs } = useContext(AppContext);
  const [OTP, setOTP] = useState("");
  const classes = useStyles();

  async function postTradeData() {
    setShow(false);
    let pinVerifyReqBody = {
      "pin": OTP
    };

    const postPinVerify = await postJSON('/api/user/pin/verify', pinVerifyReqBody);
    if (postPinVerify?.data === true) {
      let reqBody = {
        "symbol": confirmorderDetails.tradeSymbol,
        "payMethod": selectedValue === 1 ? 'DEBIT_CARD' : 'BANK_TRANSFER',
        "investAmount": tradeAmount ?? 0,
        "tradeType": action.toUpperCase()
      }

      handleQueryLogs(postPinVerify.queries, postPinVerify.explainResults, postPinVerify.latencyMillis, postPinVerify.connectionInfo);

      const postTradeDetails = await postJSON('/api/trades', reqBody);
      if (postTradeDetails.data != null) {
        handleQueryLogs(postTradeDetails.queries, postTradeDetails.explainResults, postTradeDetails.latencyMillis, postTradeDetails.connectionInfo);
        navigate('/trans', { state: { confirmorderDetails: confirmorderDetails, tradeAmount: tradeAmount } })

      }
    }
    else if (postPinVerify?.data === false) {
      setShow(true);
    }
  }


  return (
    <Phone>

      <Box sx={{ mt: -20, mx: 5 }}>

        <Grid container spacing={1}>

          <Grid item xs={12}>
            <Link to={`../Confirmorder`} state={{ confirmorderDetails: confirmorderDetails, selectedValue: selectedValue, tradeAmount: tradeAmount }}>  <WestIcon sx={{ marginRight: "277px" }} /></Link>
          </Grid>

          <Grid item xs={12} >
            <Typography variant="h1">Enter your PIN</Typography>
            <Typography variant="p" sx={{ fontSize: "18px !important" }}> Please enter your PIN to confirm payment </Typography>
          </Grid>

        </Grid>

      </Box>


      <CssBaseline />

      <Box sx={{ mt: 20 }}>

        <Grid item xs={12} container alignItems="center" direction="column">

          <Grid item>

            <OTPInput className={classes.otppassword} value={OTP} onChange={setOTP} autoFocus OTPLength={4} otpType="number" disabled={false} secure />

          </Grid>

          <Grid item xs={12}>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              onClick={postTradeData}
              className={classes.submit}>
              SUBMIT
            </Button>
          </Grid>
        </Grid>
      </Box>
      {show ?
        <Stack sx={{ width: '100%', visibility: { show } }} spacing={2}>
          <Alert severity="error" onClose={() => setShow(false)}>Incorrect PIN. Please Enter Correct PIN.</Alert>
        </Stack> : null}


    </Phone>
  );
}


