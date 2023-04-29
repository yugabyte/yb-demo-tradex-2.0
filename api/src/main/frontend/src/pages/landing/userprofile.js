import { makeStyles } from "@material-ui/core/styles";
import { Link } from "react-router-dom";
import { Typography } from "@material-ui/core";
import AppContext from "../../contexts/AppContext";
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import Badge from '@mui/material/Badge';
import BorderColorOutlinedIcon from '@mui/icons-material/BorderColorOutlined';
import { styled } from '@mui/material/styles';
import Card from '@mui/material/Card';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import TranslateIcon from '@mui/icons-material/Translate';
import EditIcon from '@mui/icons-material/Edit';
import LockIcon from '@mui/icons-material/Lock';
import SecurityIcon from '@mui/icons-material/Security';
import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import HelpIcon from '@mui/icons-material/Help';
import CallIcon from '@mui/icons-material/Call';
import { useContext, useState, useEffect } from "react";
import getJSON from "../../services/rest";

const useStyles = makeStyles((theme) => {
  return {
    landingWrapper: {
      paddingTop: "0px",
      marginTop:"-20px",
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
    paperContainer: {
      backgroundImage: `url(${"assets/img/purpledark.png"})`,
      width: "390px",
      height: "263px",
    },
    textDanger:{
      color:"#1573FE",
    },
    notetext:{
      fontSize:"14px !important",
      fontWeight:"400",
      fontFamily:"Roboto, Helvetica, Arial, sans-serif",
    },
  };
});

const SmallAvatar = styled(Avatar)(({ theme }) => ({

  width: 22,

  height: 22,

  border: `2px solid ${theme.palette.background.paper}`,

}));

export default function Userprofile() {
  const classes = useStyles();
  const [isLoaded, setIsLoaded] = useState(false);
  const [items, setItems] = useState([]);
  const [lang, setLang] = useState('');
  const [userDetails, setUserDetails] = useState([]);
  const [error, setError] = useState(null);
  const { handleQueryLogs, currentDatabase, trafficLocation } = useContext(AppContext);

  const loadUserProfile = async () =>{

    try {
      const resp = await getJSON('/api/me');
      setItems(resp.data);
      switch (resp.data.language) {
        case 'English_US':
          setLang('a');
          break;
        case 'English_UK':
          setLang('b');
          break;
        case 'Mandarin':
          setLang('d');
          break;
        case 'Hindi':
          setLang('e');
          break;
        case 'Spanish':
          setLang('f');
          break;
        case 'French':
          setLang('g');
          break;
        case 'Arabic':
          setLang('h');
          break;
        case 'Indonesia':
          setLang('i');
          break;
        case 'Russian':
          setLang('j');
          break;
        default:
          setLang('');
          break;
      }

      let userData = {
        personalData: resp?.data?.personalDetails,
        id: resp?.data?.id,
        email: resp?.data?.email,
        createdDate: resp?.data?.createdDate,
        existingRegion: resp?.data?.id?.preferredRegion
      };

      localStorage.setItem('notification-data', JSON.stringify(resp?.data?.notifications));

      setUserDetails(userData);
      handleQueryLogs(resp.queries, resp.explainResults, resp.latencyMillis, resp.connectionInfo);
     
    }
    catch (e) {
      console.log("error in fetching database nodes", e);
      setIsLoaded(false);
      setError(error);
    }
  }

  useEffect(() => {
    loadUserProfile().then( () => {
      console.log('user profile loaded');
    });

}, []);
  return (
    
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>

 
 
  <main className="main-content mt-0 px-0 mx-0">
 <div className="page-header">


    <Box>
        <Grid container sx={{px:1}}>
          <Paper className={classes.paperContainer} sx={{boxShadow:"none !important"}}>
            <Stack direction="column" justifyContent="center" alignItems="center">
                  <Badge
                    overlap="circular"
                    sx={{mt:24}}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                    badgeContent={
                      <SmallAvatar alt="Remy Sharp" sx={{width:"37px",height:"37px"}}>
                        <BorderColorOutlinedIcon/>
                      </SmallAvatar>
                    }>
                    <Avatar src="/broken-image.jpg" sx={{width:"92px",height:"94px"}} />
                  </Badge>
            </Stack>
          </Paper>
          
        </Grid>

        <Grid item xs={12} sx={{m:4}}>
          <Stack direction="column" justifyContent="center" alignItems="center">
              <Typography variant="h6" component="h2" sx={{fontWeight:600,textAlign:"center"}}>{items?.personalDetails?.fullName}</Typography>  
          </Stack>
          <Stack direction="column" justifyContent="center" alignItems="center" sx={{textAlign:"center",mt:1}}>
              <Typography variant="p" sx={{fontSize:"12px !important",fontWeight:300,textAlign:"center !important",marginLeft:"12px"}}>{items?.email} | { items?.personalDetails?.phone ?? "+01 234 567 89" } </Typography>
          </Stack>
        </Grid>

        <Grid container sx={{mx:5}}>

          <Card sx={{ minWidth: 330,my:1}}>

              <Grid container sx={{px:2,pb:1,pt:2}}>

                <Grid item xs={1}>
                    < EditIcon />
                </Grid>
                      
                <Grid item xs={8}   sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                <Link to={'../profileupdate'} state={{ userDetails: userDetails }}>   <Typography className={classes.notetext}>Edit profile information</Typography></Link>
                </Grid>
                
              </Grid>


              <Grid container sx={{px:2,pb:1}}>

              <Grid item xs={1}>
                    < NotificationsNoneIcon />
                </Grid>
                      
                <Grid item xs={8} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
               <Link to={'../notification'} state={{ notificationData: items?.notifications }}>    <Typography className={classes.notetext}>Notifications</Typography> </Link> 
                </Grid>
                
                <Grid item xs={2} sx={{justifyContent: "right",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                    <Typography variant="p" className={classes.textDanger}>ON</Typography>
                </Grid>    

              </Grid>


              <Grid container sx={{px:2,pb:1}}>

                <Grid item xs={1}>
                    < TranslateIcon />
                </Grid>
                      
                <Grid item xs={7} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                <Link to={`../language`} state={{ langData: lang }}>    <Typography className={classes.notetext}>Language</Typography></Link>
                </Grid>
                
                <Grid item xs={3} sx={{justifyContent: "right",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                    <Typography variant="p" className={classes.textDanger}>{items?.language === 'English_US' ? 'English (US)' : items?.language === 'English_UK' ? 'English (UK)' : items?.language}</Typography>
                </Grid>    

              </Grid>

          </Card>

          <Card sx={{ minWidth: 330,my:1}}>

              <Grid container sx={{px:2,pb:1,pt:2}}>

                <Grid item xs={1}>
                    < LockIcon />
                </Grid>
                      
                <Grid item xs={8} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                    <Typography className={classes.notetext}>Security</Typography>
                </Grid>

              </Grid>

              <Grid container sx={{px:2,pb:1}}>

                <Grid item xs={1}>
                    < AutoFixHighIcon />
                </Grid>
                      
                <Grid item xs={7} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                    <Typography className={classes.notetext}>Theme</Typography>
                </Grid>
                
                <Grid item xs={3} sx={{justifyContent: "right",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                    <Typography variant="p" className={classes.textDanger}>Light mode</Typography>
                </Grid>    

              </Grid>

          </Card>

          <Card sx={{ minWidth: 330,my:1}}>

            <Grid container sx={{px:2,pb:1,pt:2}}>

              <Grid item xs={1}>
                  < HelpIcon />
              </Grid>
                    
              <Grid item xs={8} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                  <Typography className={classes.notetext}>Help & Support</Typography>
              </Grid>

            </Grid>

            <Grid container sx={{px:2,pb:1}}>

              <Grid item xs={1}>
                  < CallIcon />
              </Grid>
                    
              <Grid item xs={8} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                  <Typography className={classes.notetext}>Contact us</Typography>
              </Grid>

            </Grid>

            <Grid container sx={{px:2,pb:1}}>

              <Grid item xs={1}>
                  < SecurityIcon />
              </Grid>
                    
              <Grid item xs={8} sx={{justifyContent: "left",display: "flex",marginLeft:"10px",paddingTop:"4px"}}>
                  <Typography className={classes.notetext}>Privacy policy</Typography>
              </Grid>

            </Grid>

          </Card>
          
          
          </Grid>


</Box>


</div>

  </main>

        </div>
      </div>
     

  );
}
