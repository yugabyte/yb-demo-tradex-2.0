import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone2";
import { Typography } from "@material-ui/core";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import WestIcon from '@mui/icons-material/West';
import { styled } from '@mui/material/styles';
import MuiPhoneNumber from "material-ui-phone-number";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Avatar from '@mui/material/Avatar';
import "./bottom1.css"
import Badge from '@mui/material/Badge';
import { Link } from "react-router-dom";
import $ from "jquery";
import { useState, useEffect } from "react";
import BorderColorOutlinedIcon from '@mui/icons-material/BorderColorOutlined';
import { useNavigate } from 'react-router';

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
let isInvalid = false;

//Validation 

function Validate1(values) {
  let errors = {};
  if (!values.text) {
    errors.text = "Please enter your FullName";
    isInvalid = true;
  } else if (values.text.length < 5) {
    errors.text = "";
  }

  if (!values.email) {
    errors.email = "Please enter your email-ID";
    isInvalid = true;
  } else if (!/\S+@\S+\.\S+/.test(values.email)) {
    errors.email = "Please  Email  is invalid";
    isInvalid = true;
  }


  if (!values.password) {
    errors.password = "Please enter your Password";
    isInvalid = true;
  } else if (values.password.length < 8) {
    errors.password = "Password must be 8 or more characters";
    isInvalid = true;
  } else if (!/\d/.test(values.password)) {
    errors.password = "Password must contain atleast 1 number";
    isInvalid = true;
  } else if (!/[!@#$%&?]/g.test(values.password)) {
    errors.password = "Password must contain atleast 1 special character";
    isInvalid = true;
  } else if (!/[A-Z]/g.test(values.password)) {
    errors.password = "Password must contain atleast 1 capitol letter";
    isInvalid = true;
  }

  // if (!values.number) {
  //   errors.number = "Enter your PhoneNumber";
  //   isInvalid = true;
  // } else if (values.number.length < 5) {
  //   errors.number = "";
  // }

  // if (!values.mobileNumber) {
  //   errors.mobileNumber = "Enter your PhoneNumber";
  // } else if (values.mobileNumber.length < 5) {
  //   errors.mobileNumber = "";
  // }
  return errors;
}

const useForm1 = (callback, validate) => {
  const [values, setValues] = useState({});
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [mobileNumber, setMobileNumber] = useState("");

  let navigate = useNavigate();
  useEffect(() => {
    if (Object.keys(errors).length === 0 && isSubmitting) {
      callback();
    }
  }, [errors, callback,isSubmitting]);

  const handleSubmit = event => {
    if (event) event.preventDefault();
    isInvalid = false;
    setErrors(Validate1(values));
    setIsSubmitting(true);
    let phoneNumber = mobileNumber.replace('(','').replace(')','').replace('-','').replaceAll(' ','');
    if(!isInvalid){
      navigate('/otp', {state:{ name: values.text, email: values.email, password: values.password, phoneNumber: phoneNumber }})
    }
  };

  const handleChange = event => {
    event.persist();
    setValues(values => ({
      ...values,
      [event.target.name]: event.target.value
    }));
  };

  const handleMobileNumber = (e) => {
    setMobileNumber(e);
  }

  return {
    handleChange,
    handleSubmit,
    handleMobileNumber,
    values,
    errors
  };
};


export default function FillProfile(props) {
  const { values, errors, handleChange, handleSubmit, handleMobileNumber } = useForm1(
    login,
    Validate1
  );
  const [ setLoggedIn] = useState(false);

  function login() {
    setLoggedIn(true);
    props.parentCallback(true);
    return;
  }

  $(document).ready(function() {  
    var readURL = function(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
  
            reader.onload = function (e) {
                $('.profile-pic').attr('src', e.target.result);
            }
    
            reader.readAsDataURL(input.files[0]);
        }
    }
    
  
    $(".file-upload").on('change', function(){
        readURL(this);
    });
    
    $(".upload-button").on('click', function() {
      $(".file-upload").on('change', function(){
      readURL(this);
  });
       $(".file-upload").click();
    });
  });
  



  const classes = useStyles();
 
  const SmallAvatar = styled(Avatar)(({ theme }) => ({
    width: 22,
    height: 22,
    border: `2px solid ${theme.palette.background.paper}`,
  }));
  
// const [phoneErrorText, setPhoneErrorText]= React.useState("");
 
  return (
 <Phone>
  <div className={classes.landingWrapper} >
   <div className={classes.landingHeader} >

    <Box >
        <Grid container spacing={3} >
            <Grid item xs={2}  sx={{marginRight:"282px"}}>
                <Link to={`../login`}>
                  <WestIcon />
                </Link>
            </Grid> 
        </Grid>
    </Box>

    <Box >
       <Grid container spacing={0} sx={{marginTop:"-10px"}}>
           <Grid item xs={12}>
           <Typography variant="h1" >Fill your profile</Typography>
           <Typography variant="p" sx={{fontSize:"18px !important"}}>Don’t worry, you can always change it later</Typography>
           </Grid>
           <Grid item xs={12} sx={{marginTop:"20px",justifyContent:"center !important",display:"flex !important"}}>
           <Badge
         
         overlap="circular"
         anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
         badgeContent={
           <SmallAvatar alt="Remy Sharp" sx={{width:"37px",height:"37px"}}>
              <BorderColorOutlinedIcon/>
           </SmallAvatar>
         }
       >
               <Avatar src="/broken-image.jpg" sx={{width:"92px",height:"94px"}} />
             </Badge>
             


           </Grid>
       </Grid>
    </Box>
 
    <Box >
    <form onSubmit={handleSubmit} noValidate>
         <Grid container spacing={2} sx={{marginTop:"-20px"}}>
             <Grid item xs={12}> 
                <TextField
                  fullWidth
                  id="fullWidth"
                   label="FullName"
                    className={`TextField ${errors.text && "is-danger"}`}
                    type="text"
                    name="text"
                    onChange={handleChange}
                    value={values.text || ""}
                    error={!!errors.text}
                    helperText={errors.text}/>

             </Grid>
        
             <Grid item xs={12}>
                  <TextField
                    fullWidth
                    id="fullWidth"
                   label="Email"
                    autoComplete="off"
                    className={`TextField ${errors.email && "is-danger"}`}
                    type="email"
                    name="email"
                    onChange={handleChange}
                    value={values.email || ""}
                    error={!!errors.email}
                    helperText={errors.email} />
             </Grid>
   
             <Grid item xs={12}>
                  <TextField
                     fullWidth
                     id="fullWidth"
                   label="Password"
                    className={`TextField ${errors.password && "is-danger"}`}
                    type="password"
                    name="password"
                    onChange={handleChange}
                    value={values.password || ""}
                    error={!!errors.password}
                    helperText={errors.password}/>
             </Grid>
    
              <Grid item xs={12} sx={{ borderRadius: "10px !important" }} >
                <MuiPhoneNumber
                  fullWidth
                  label="Phone Number"
                  id="fullWidth"
                  variant="outlined"
                  defaultCountry={"us"}
                  value={values.mobileNumber || "" }
                  error={!!errors.mobileNumber}
                  helperText={errors.mobileNumber}
                  onChange={e => handleMobileNumber(e)}/>
              </Grid>
          
                {/* <Link to={`../otp`} state={{ name: values.text, email: values.email, password: values.password, phoneNumber: mobileNumber }}> */}
                  <Grid item xs={12}  >
                    <Button
                      type="submit"
                      fullWidth
                      variant="contained"
                      color="primary"
                      className="button is-block is-info is-fullwidth" sx={{ backgroundColor: "#7879f1" }}>
                      Sign In
                    </Button>
                  </Grid>
                {/* </Link> */}
    
        </Grid>
        </form>
    </Box>
   </div>
  </div> 
</Phone>
  );
}
