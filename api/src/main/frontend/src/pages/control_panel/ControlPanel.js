import DatabaseConfig from "../../components/database_config/DatabaseConfig";
import Map from "../../components/map/Map";
import Xray from "../../components/xray/Xray";
import Paper from "@material-ui/core/Paper";
import { Grid, makeStyles } from "@material-ui/core";
import LogoutIcon from '@mui/icons-material/Logout';
import { useNavigate } from "react-router-dom";
import Typography from '@mui/material/Typography';

import { doLogout } from "../../services/authService";

const useStyles = makeStyles((theme) => {
  return {
    controlPanelWrapper: {
      boxSizing: "border-box",
      margin: "20px 10px",
      maxWidth: "calc(100% - 20px)",
    },
    controlPanelPaper: {},
  };
});

export default function DisabledTabs() {




  const classes = useStyles();
  let navigate = useNavigate();

  const performLogout = () => {
    let postBody = {
      "message": "logout"
    }

    doLogout(postBody).then( (res) => {
      navigate('/login');
    }).catch( (err)=>{
      console.error(err)
    } )

    window.localStorage.removeItem('authToken');
  }

  return (
    <Grid
      container
      direction="column"
      spacing={2}
      className={classes.controlPanelWrapper}
    >
        <Typography sx={{ fontSize: "16px", textAlign: "right", marginRight: "20px", fontWeight: "700", cursor: "pointer" }} variant="p" component="div" onClick={performLogout}>
          Logout   <LogoutIcon sx={{ fontSize: "20px", color: "#091440" }} />
        </Typography>
      <Grid item>
        <Paper className={classes.controlPanelPaper}>
          <DatabaseConfig />
          <Map />
        </Paper>
      </Grid>
      <Grid item>
        <Paper className={classes.controlPanelPaper}>
          <Xray />
        </Paper>
      </Grid>
      <Grid container spacing={2} sx={{mt:3}}>
      <Grid item xs={10}></Grid>
      <Grid item xs={2} >
      
      {/* <Link to={'./'} onClick={performLogout}> */}
      {/* <Typography primary="Logout"  secondary={  <LogoutIcon sx={{marginTop:"-1815px !important",marginLeft:"960px !important"}} onClick={performLogout} /> } sx={{lineHeight:"1.75em"}}>Bank of America</Typography> */}
      {/* <Typography sx={{ fontSize:"16px",marginTop:"-900px", fontWeight:"700"}} variant="p" component="div">
 Logout   <LogoutIcon  sx={{ fontSize:"20px",color:"#091440"}}  onClick={performLogout} />       
          </Typography>
      </Link> */}
      
      </Grid>
    </Grid>
    </Grid>
  );
}
