import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Phone from "../../components/phone/Phone";
import { Link } from "react-router-dom";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import WestIcon from '@mui/icons-material/West';
import { Typography } from "@mui/material";
import Radio from '@mui/material/Radio';
import Button from '@mui/material/Button';
import { purple } from '@mui/material/colors';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import { putJSON } from "../../services/rest.js";
import { useContext, useState } from "react";
import AppContext from "../../contexts/AppContext";
import { styled } from '@mui/material/styles';


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
    langtext: {
      fontSize: "13px !important",
      fontWeight: "400",
      fontFamily: "Roboto, Helvetica, Arial, sans-serif",
    }

  };
});



export default function Language(props) {
  const [choose, setNone] = React.useState('');
  const handleChange = (event) => {
    setNone(event.target.value);
    setSelectedValue(event.target.value);
    setIsDisable(false);
  };
  const [selectedValue, setSelectedValue] = useState(props.langData);
  const classes = useStyles();
  const [isDisable, setIsDisable] = useState(true);
  const [show, setShow] = useState(false);
  const { handleQueryLogs} = useContext(AppContext);
  const ColorButton = styled(Button)(({ theme }) => ({
    color: theme.palette.getContrastText(purple[500]),
    backgroundColor:"#7879f1",  
    '&:hover': {
      backgroundColor: "#7879f1",
    },
  }));

  async function updateUserLanguage() {
    setIsDisable(true);
    var languageSelected = '';
    switch(selectedValue) {
      case 'a':
        languageSelected = 'English_US';
        break;
      case 'b':
        languageSelected = 'English_UK';
        break;
      case 'd':
        languageSelected = 'Mandarin';
        break;
      case 'e':
        languageSelected = 'Hindi';
        break;
      case 'f':
        languageSelected = 'Spanish';
        break;
      case 'g':
        languageSelected = 'French';
        break;
      case 'h':
        languageSelected = 'Arabic';
        break;
      case 'i':
        languageSelected = 'Indonesia';
        break;
      case 'j':
        languageSelected = 'Russian';
        break;
      default:
        break;
    }
    const putUserDataRes = await putJSON('/api/me/lang/'+languageSelected, {});
    setShow(true);
    handleQueryLogs(putUserDataRes.queries, putUserDataRes.explainResults, putUserDataRes.latencyMillis, putUserDataRes.connectionInfo);
  }

  return (
    <Phone>
      <div className={classes.landingWrapper}>
        <div className={classes.landingHeader}>
          <main className="main-content">
            <div className="container-fluid py-4">
              <Box sx={{ mx: 3 }}>
                <Grid container>
                  <Grid item xs={2}>
                    <Link to={`../profile`}><WestIcon /></Link>
                  </Grid>
                  <Grid item xs={10}>
                    <Grid>
                      <Typography variant="p" sx={{ fontSize: "18px !important", mx: 8, fontWeight: 'bold' }}>Language</Typography>
                    </Grid>
                  </Grid>
                </Grid>
              </Box>
              <Box sx={{ mx: 3 }}>
                <Grid item xs={12} sx={{ mt: 4 }}>
                  <Typography variant="p" sx={{ fontSize: "14px !important", fontWeight: 'bold' }}>Suggested</Typography><br />
                </Grid>
                <Grid container sx={{ mt: 1 }}>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext} >English (US)</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'a'}
                      onChange={handleChange}
                      value="a"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'A' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>English (UK)</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'b'}
                      onChange={handleChange}
                      value="b"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'B' }}
                    />
                  </Grid>
                </Grid>
                <Divider sx={{ my: 2 }} />
              </Box>
              <Box sx={{ mx: 3 }}>
                <Grid item xs={12} sx={{ mt: 3 }}>
                  <Typography variant="p" sx={{ fontSize: "14px !important", fontWeight: 'bold' }}>Others</Typography><br />
                </Grid>
                <Grid container sx={{ mt: 1 }}>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Mandarin</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'd'}
                      onChange={handleChange}
                      value="d"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'D' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Hindi</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'e'}
                      onChange={handleChange}
                      value="e"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'E' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Spanish</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'f'}
                      onChange={handleChange}
                      value="f"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'F' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>French</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'g'}
                      onChange={handleChange}
                      value="g"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'G' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Arabic</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'h'}
                      onChange={handleChange}
                      value="h"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'H' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Indonesia</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'i'}
                      onChange={handleChange}
                      value="i"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'I' }}
                    />
                  </Grid>
                  <Grid item xs={10} sx={{ mt: 1.5 }}>
                    <Typography variant="p" className={classes.langtext}>Russian</Typography><br />
                  </Grid>
                  <Grid item xs={2}>
                    <Radio
                      checked={selectedValue === 'j'}
                      onChange={handleChange}
                      value="j"
                      name="radio-buttons"
                      inputProps={{ 'aria-label': 'J' }}
                    />
                  </Grid>
                  <Divider sx={{ my: 2 }} />
                </Grid>
                <Grid item xs={12}  >
                  <ColorButton disabled={isDisable} variant="contained" fullWidth sx={{ borderRadius: "10px !important", marginTop:"25px !important" }} onClick={updateUserLanguage}>Update</ColorButton>
                </Grid>
              </Box>
              {show ?
                <Stack sx={{ width: '100%', visibility: { show }, marginTop: "10px !important" }} spacing={2}>
                  <Alert severity="success" onClose={() => setShow(false)}>Language Updated Successfully.</Alert>
                </Stack> : null}
            </div>
          </main>
        </div>
      </div>
    </Phone>
  );
}
