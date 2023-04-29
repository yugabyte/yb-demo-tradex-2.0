import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import { Link } from "react-router-dom";
import { Typography } from "@material-ui/core";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import WestIcon from '@mui/icons-material/West';
import { styled } from '@mui/material/styles';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { purple } from '@mui/material/colors';
import "./bottom1.css";
import { useContext, useState } from "react";
import AppContext from "../../contexts/AppContext";
import { putJSON } from "../../services/rest.js";
import { useNavigate } from "react-router-dom";
import InputAdornment from "@material-ui/core/InputAdornment";
import AddIcon from '@mui/icons-material/Add';

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
      overflowY: "scroll",
      overflowX: "hidden",
    },
    landingHeader: {
      flexBasis: "300px",
      display: "flex",
      flexDirection: "column",
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

export default function Profileupdate(props) {
  
  const classes = useStyles();


  const [name, setName] = useState(props?.userDetails?.personalData?.fullName);
  const [email, setEmail] = useState(props?.userDetails?.email);
  const [password, setPassword] = useState("");
  const [mobileNumber, setMobileNumber] = useState(props?.userDetails?.personalData?.phone);
  const [country, setCountry] = useState(props?.userDetails?.personalData?.country);
  const [gender, setGender] = useState(props?.userDetails?.personalData?.gender);
  const [address, setAddress] = useState(props?.userDetails?.personalData?.address);
  const [createdDate, setCreatedDate] = useState(props?.userDetails?.createdDate);
  const [region, setRegion] = useState(props?.userDetails?.existingRegion);
  const navigate = useNavigate();
  
  const { handleQueryLogs} = useContext(AppContext);

  const handleMobileNumChange = (evt) => {
    const regex = /^[0-9\d]+$/;
    if (evt.target.value === "" || regex.test(evt.target.value)) {
      setMobileNumber(evt.target.value);
    }
  }

  async function updateUserData() {
    let userDataReqBody = {
      "createdDate": createdDate,
      "updatedDate": new Date().toISOString(),
      "id": {
        "id": 1,
        "preferredRegion": region
      },
      "email": email,
      "passkey": null,
      "enabled": true,
      "language": "English",
      "securityPin": 9999,
      "personalDetails": {
        "fullName": name,
        "phone": mobileNumber,
        "country": country,
        "gender": gender,
        "address": address
      },
      "notifications": JSON.parse(localStorage.getItem('notification-data')),
      "favourites": [
        0
      ]
    }

    const putUserDataRes = await putJSON('/api/user',userDataReqBody); 

      localStorage.removeItem('notification-data');
      handleQueryLogs(putUserDataRes.queries, putUserDataRes.explainResults, putUserDataRes.latencyMillis, putUserDataRes.connectionInfo);
      navigate("/profile");

  }


  const ColorButton = styled(Button)(({ theme }) => ({

    color: theme.palette.getContrastText(purple[500]),
  
    backgroundColor:"#7879f1",
  
    '&:hover': {
  
      backgroundColor: "#7879f1",
  
    },
  
  }));

  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>

    <Box>
        <Grid container  sx={{mx:1}} >
            <Grid item xs={2} >
            <Link to={`../profile`}>  <WestIcon /></Link>
            </Grid>

            <Grid item xs={8} >
                 <Grid sx={{textAlign:"center"}}>
                    <Typography variant="h5" sx={{fontSize:"18px !important"}} >Edit Profile</Typography>
                 </Grid>
            </Grid>


        </Grid>
    </Box>


         <Grid container spacing={2}>

             <Grid item xs={12}>
                 <TextField fullWidth id="name" label="Full Name" value={name} variant="outlined" onChange={(e) => setName(e.target.value)} />      
             </Grid>

             <Grid item xs={12}>
                 <TextField fullWidth type="email" label="Email" id="email" value={email}  onChange={(e) => setEmail(e.target.value)} />
             </Grid>
   
             <Grid item xs={12}>
                 <TextField fullWidth type="password" label="Password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} />
             </Grid>

             <Grid item xs={12}> 
                 <TextField InputProps={{ startAdornment: (<InputAdornment position="start"> <AddIcon /> </InputAdornment> )}}  fullWidth id="fullWidth" label="Mobile Number" value={mobileNumber} variant="outlined" onChange={(e) => handleMobileNumChange(e)} />      
             </Grid>

             <Grid item xs={6}> 
                 <TextField id="country" label="Country" value={country} variant="outlined" onChange={(e) => setCountry(e.target.value)} />      
             </Grid>

             <Grid item xs={6}> 
                 <TextField id="gender" label="Gender" value={gender} variant="outlined" onChange={(e) => setGender(e.target.value)} />      
             </Grid>

             <Grid item xs={12}> 
                 <TextField fullWidth id="address" label="Address" value={address} variant="outlined" onChange={(e) => setAddress(e.target.value)} />      
             </Grid>


             <Grid item xs={12}  >
               <ColorButton variant="contained" fullWidth sx={{    borderRadius: "10px !important" }} onClick={updateUserData}>SUBMIT</ColorButton>
             </Grid>
        </Grid>


        </div>
      </div>  
    </Phone>
  );
}
