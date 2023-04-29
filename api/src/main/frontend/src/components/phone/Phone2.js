import { makeStyles } from "@material-ui/core";
// import Bottommenu from "../../pages/landing/Bottommenu";
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
}));
export default function Phone({ children }) {
  const classes = useStyles();
  return (
    <div className={classes.phoneWrapper} style={{ width: "100%" }}>
      <div className={classes.phone}>{children}
      {/* <Bottommenu /> */}
      </div>
    
    </div>
  );
}
