import { makeStyles } from "@material-ui/core";
import TrafficLocation from "../traffic_location/TrafficLocation";


const useStyles = makeStyles((theme) => ({
  phoneWrapper: {
    background: `linear-gradient(
          ${theme.palette.background.purpleGradient1},
          ${theme.palette.background.purpleGradient2}
        )`,
    display: "flex",
    justifyContent: "center",
    flexDirection: "column",
    height: "100%",
    padding: "10px",
  },
  phone: {
    display: "flex",
    flexDirection: "column",
    alignSelf: "center",
    height: "700px",
    width: "370px",
    justifyContent: "center",
    alignItems: "center",
    boxSizing: "border-box",
    background: "white",
    overflow: "hidden",
    borderRadius: "40px",
  },
  trafficLocation: {
    minWidth: "250px",
    // boxShadow: "0px 0px 24px 0px #00000040",
    width: "100%",
    display: "flex",
    justifyContent: "center",
     paddingTop:"30px",
     paddingBottom:"30px",
    // background: linear-gradient( #050c24, #091240 );
    // background: `linear-gradient(
    //   #050c24,#091240
    // )`,
  },
}));
export default function Phone({ children }) {
  const classes = useStyles();
  return (
    <div className={classes.phoneWrapper} style={{ width: "100%" }}>
      <div className={classes.trafficLocation}>
         <TrafficLocation />
      </div>
      <div className={classes.phone}>
        {children} 
      </div>
    </div>
  );
}
