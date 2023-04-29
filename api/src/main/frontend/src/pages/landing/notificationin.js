import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import { styled } from '@mui/material/styles';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import WestIcon from '@mui/icons-material/West';
import { Typography } from "@mui/material";
import Switch from '@mui/material/Switch';
import { Link } from "react-router-dom";
import { putJSON } from "../../services/rest.js";
import { useContext, useState } from "react";
import AppContext from "../../contexts/AppContext";
import Button from '@mui/material/Button';
import { purple } from '@mui/material/colors';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';

const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
      paddingTop: theme.spacing(4),
      paddingBottom: theme.spacing(4),
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
    notetext: {
      fontSize: "13px !important",
      fontWeight: "400",
      fontFamily: "Roboto, Helvetica, Arial, sans-serif",
    }
  };
});



export default function Notification(props) {


  const label = { inputProps: { 'aria-label': 'Size switch demo' } };
  const { handleQueryLogs} = useContext(AppContext);
  const [isDisable, setIsDisable] = useState(true);
  const [show, setShow] = useState(false);
  const ColorButton = styled(Button)(({ theme }) => ({
    color: theme.palette.getContrastText(purple[500]),
    backgroundColor:"#7879f1",  
    '&:hover': {
      backgroundColor: "#7879f1",
    },
  }));

  var data = JSON.parse(localStorage.getItem('notification-data'));

  async function updateUserNotifications() {
    let updateNotif = JSON.parse(localStorage.getItem('notification-data'));
    let updateNotificationReqBody = {
      "generalNotification": updateNotif?.generalNotification,
      "sound": updateNotif?.sound,
      "vibrate": updateNotif?.vibrate,
      "appUpdates": updateNotif?.appUpdates,
      "billReminder": updateNotif?.billReminder,
      "promotion": updateNotif?.promotion,
      "discountAvailable": updateNotif?.discountAvailable,
      "paymentReminder": updateNotif?.paymentReminder,
      "newServiceAvailable": updateNotif?.newServiceAvailable,
      "newTipsAvailable": updateNotif?.newTipsAvailable,
    }

    const putUserDataRes = await putJSON('/api/me/notifs', updateNotificationReqBody);
    setIsDisable(true);
    setShow(true);

    handleQueryLogs(putUserDataRes.queries, putUserDataRes.explainResults, putUserDataRes.latencyMillis, putUserDataRes.connectionInfo);
  }

  const notificationChange = (event) => {
    setIsDisable(false);
    switch (event.target.id) {
      case 'GN':
        data.generalNotification = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'SOUND':
        data.sound = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'VIBRATE':
        data.vibrate = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'AU':
        data.appUpdates = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'BR':
        data.billReminder = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'PROM':
        data.promotion = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'DA':
        data.discountAvailable = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'PA':
        data.paymentReminder = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'NSA':
        data.newServiceAvailable = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      case 'NTA':
        data.newTipsAvailable = event.target.checked ? "ENABLED" : "DISABLED";
        localStorage.setItem('notification-data', JSON.stringify(data));
        data = JSON.parse(localStorage.getItem('notification-data'));
        break;
      default:
        break;
    }
  }

  const classes = useStyles();
  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
          <main className="main-content">
            <div className="container-fluid py-4">
              <Box sx={{ mx: 3 }}>
                <Grid container>
                  <Grid item xs={2}>
                    <Link to={`../profile`}>    <WestIcon /></Link>
                  </Grid>

                  <Grid item xs={10}>
                    <Grid>
                      <Typography variant="p" sx={{ fontSize: "18px !important", mx: 7, fontWeight: 'bold' }}>Notifications</Typography>
                    </Grid>
                  </Grid>
                </Grid>
              </Box>

              <Box sx={{ mx: 3 }}>
                <Grid item xs={12} sx={{ mt: 4 }}>
                  <Typography variant="p" sx={{ fontSize: "14px !important", fontWeight: 'bold' }}>Common</Typography><br />
                </Grid>

                <Grid container sx={{ mt: 1 }}>
                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>General Notification</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='GN' {...label} defaultChecked={props?.notificationData?.generalNotification?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Sound</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='SOUND' {...label} defaultChecked={props?.notificationData?.sound?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Vibrate</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='VIBRATE' {...label} defaultChecked={props?.notificationData?.vibrate?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>
                </Grid>
                <Divider sx={{ my: 2 }} />
              </Box>

              <Box sx={{ mx: 3 }}>
                <Grid item xs={12} sx={{ mt: 3 }}>
                  <Typography variant="p" sx={{ fontSize: "14px !important", fontWeight: 'bold' }}>System & services update</Typography><br />
                </Grid>

                <Grid container sx={{ mt: 1 }}>
                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>App updates</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='AU' {...label} defaultChecked={props?.notificationData?.appUpdates?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Bill Reminder</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='BR' {...label} defaultChecked={props?.notificationData?.billReminder?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Promotion</Typography><br />
                  </Grid>

                  <Grid item xs={2}> 
                    <Switch id='PROM' {...label} defaultChecked={props?.notificationData?.promotion?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Discount Available</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='DA' {...label} defaultChecked={props?.notificationData?.discountAvailable?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>Payment Request</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='PA' {...label} defaultChecked={props?.notificationData?.paymentReminder?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>
                </Grid>
                <Divider sx={{ my: 2 }} />
              </Box>

              <Box sx={{ mx: 3 }}>
                <Grid item xs={12} sx={{ mt: 3 }}>
                  <Typography variant="p" sx={{ fontSize: "14px !important", fontWeight: 'bold' }}>Others</Typography><br />
                </Grid>

                <Grid container sx={{ mt: 1 }}>
                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>New Service Available</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='NSA' {...label} defaultChecked={props?.notificationData?.newServiceAvailable?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>

                  <Grid item xs={10} sx={{ mt: 1 }}>
                    <Typography variant="p" className={classes.notetext}>New Tips Available</Typography><br />
                  </Grid>

                  <Grid item xs={2}>
                    <Switch id='NTA' {...label} defaultChecked={props?.notificationData?.newTipsAvailable?.toLowerCase() === 'enabled' ? true : false} size="small" onChange={notificationChange} />
                  </Grid>
                </Grid>

                <Grid item xs={12}  >
                  <ColorButton disabled={isDisable} variant="contained" fullWidth sx={{ borderRadius: "10px !important", marginTop:"40px !important" }} onClick={updateUserNotifications}>Update</ColorButton>
                </Grid>
              </Box>
              {show ?
                <Stack sx={{ width: '100%', visibility: { show }, marginTop:"20px !important" }} spacing={2}>
                  <Alert severity="success" onClose={() => setShow(false)}>Notification Data Updated Successfully.</Alert>
                </Stack> : null}
            </div>
          </main>
        </div>
      </div>
    </Phone>
  );
}
