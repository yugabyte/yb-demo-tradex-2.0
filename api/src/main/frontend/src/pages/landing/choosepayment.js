import TrafficLocation from "../../components/traffic_location/TrafficLocation";
import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone4";
import Bottommenu from "./Bottommenu";
// import Sample from "./Sample";
import TLRButton from "../../components/tlr_button/TLRButton";
import { useNavigate } from "react-router-dom";
import { Typography } from "@material-ui/core";
import { ReactComponent as AppLogo } from "../../assets/yugastore-logo.svg";
import AppContext from "../../contexts/AppContext";
import { useContext } from "react";
import { ReactComponent as LoadingCircles } from "../../yugabyted-ui/assets/Default-Loading-Circles.svg";
import Menu from "./menu";
// import Sample from "./Sample";
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
      //   justifyContent: "center",
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
  };
});

export default function Choosepayment() {
  const classes = useStyles();
  let navigate = useNavigate();
  const { loading } = useContext(AppContext);
  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
  
        <main className="main-content position-relative border-radius-lg ">
    <div className="container-fluid py-4 mx-0 px-0">

<div className="row px-0">
  <div className="col-xs-12">
    <div className="col-xs-12 d-flex flex-column float-start">
      <i className="fa fa-arrow-left me-sm-1 ml-1 text-dark"></i></div>
      <div className="col-xs-12 justify-content-center d-flex">
      <p className="porthead text-dark"><span className="font-weight-bold">Payment Method</span></p>
      </div>
  </div>
</div>

<div className="row card mt-4">

  <div className="col-xs-12 mb-lg-0">

    <div className="z-index-2 h-100">

      <div className="my-2">

        <div className="form-check mb-2 px-0">
          
          <label className="custom-control-label" for="customRadio1">

            <img src="./assets/img/debitcard.png" className="pay1"/>&nbsp;
            
            Debit Card</label>

            <input className="form-check-input" type="radio" name="flexRadioDefault" id="customRadio1"/>

        </div>

      </div>

    </div>

  </div>
  
</div>


<div className="row card my-3">

  <div className="col-xs-12 mb-lg-0">

    <div className=" z-index-2 h-100">

      <div className="my-2">

        <div className="form-check mb-2 px-0">
          
          <label className="custom-control-label" for="customRadio2">

            <img src="./assets/img/bt1.png" className="pay1"/>&nbsp;
            
            Bank Transfer</label>

            <input className="form-check-input" type="radio" name="flexRadioDefault" id="customRadio2"/>

        </div>

      </div>

    </div>

  </div>
  
</div>

<div className="row mx-4">
<div className="col-xs-12 mt-3 mx-1">

  <h6 className="mb-3">Choose payment method</h6>

</div></div>

<div className="row card my-3">

  <div className="col-xs-12 mb-lg-0">

    <div className=" z-index-2 h-100">

      <div className="my-2">

        <div className="form-check mb-2 px-0">
          
          <label className="custom-control-label" for="customRadio3">

            <img src="./assets/img/bt2.png" className="pay1"/>&nbsp;
            
            Bank Transfer</label>

            <input className="form-check-input" type="radio" name="flexRadioDefault" id="customRadio3"/>

        </div>

      </div>

    </div>

  </div>
  
</div>

<div className="row card my-3">

  <div className="col-xs-12 mb-lg-0">

    <div className=" z-index-2 h-100">

      <div className="my-2">

        <div className="form-check mb-2 px-0">
          
          <label className="custom-control-label" for="customRadio4">
            
            <img src="./assets/img/bt3.png" className="pay1"/>&nbsp;
            
            Bank Transfer</label>

            <input className="form-check-input" type="radio" name="flexRadioDefault" id="customRadio4"/>

        </div>

      </div>

    </div>

  </div>
  
</div>




    </div>

  </main>

        </div>
      </div>

     
    </Phone>
  );
}
