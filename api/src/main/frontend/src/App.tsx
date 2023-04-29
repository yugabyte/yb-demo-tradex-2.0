import "leaflet/dist/leaflet.css";
import { Routes, Route } from "react-router-dom";
import "@fontsource/poppins";
import Main from "./pages/main/Main";
import Login from "./pages/landing/login";
import Dashboard from "./pages/main/dashboard";
import Editprofile from "./pages/landing/editprofile";
import Exch from "./pages/main/exch";
import Candle from "./pages/main/candle";
import Paymententry from "./pages/main/paymententry";
import Confirmorder2 from "./pages/main/confirmorder2";
import Userprofile from "./pages/main/profile";
import Start from "./pages/main/start";
import Success from "./pages/landing/success";
import Checkpage from "./pages/main/check";
import Otp2 from "./pages/landing/otp";
import Trans2 from "./pages/main/trans2";
import OrderHistory from "./pages/main/orderhistory";
import Profileupdate1 from "./pages/main/Profileupdate1";
import Notification2 from "./pages/main/notificationout";
import Language2 from "./pages/main/langout";
import Land from "./pages/landing/Landing";
import Enterpin1 from "./pages/main/enterpin1";
import { AppProvider } from "./contexts/AppContext";
import { CssBaseline, ThemeProvider } from '@material-ui/core';
import { tlrTheme } from './yugabyted-ui/theme/tlrTheme'
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';



const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    flexWrap: "wrap",
    height: "100vh"
  },
  container: {
    height: "100%"
  }
}));





function App() {
  const classes = useStyles();
  return (
    <div>
      <AppProvider>
      <ThemeProvider theme={tlrTheme}>
        <CssBaseline />
          <div className={classes.root}>
            <Grid container className={classes.container}>
                <Routes>
                  
                  
                  <Route path="/" element={<Land />}></Route>
                  <Route path="/login" element={<Login />}></Route>
                  <Route path="/portfolio" element={<Dashboard />}></Route>
                  <Route path="/main" element={<Main />}></Route>
                  <Route path="/dashboard" element={<Dashboard />}></Route>
                  <Route path="/createnew" element={<Editprofile />}></Route>
                  <Route path="/profile" element={<Userprofile />}></Route>
                  <Route path="/exch" element={<Exch />}></Route>
                  <Route path="/history" element={<OrderHistory />}></Route>
                  <Route path="/candlechart" element={<Candle />}></Route>
                  <Route path="/payentry" element={<Paymententry />}></Route>
                  <Route path="/confirmorder" element={<Confirmorder2 />}></Route>
                  <Route path="/otp" element={<Otp2 />}></Route>
                  <Route path="/trans" element={<Trans2 />}></Route>      
                  <Route path="/notification" element={<Notification2 />}></Route>
                  <Route path="/language" element={<Language2 />}></Route>                 
                  <Route path="/profileupdate" element={<Profileupdate1/>}></Route>
                  <Route path="/start" element={<Start />}></Route>
                  <Route path="/success" element={<Success />}></Route>
                  <Route path="/check" element={<Checkpage />}></Route>
                  <Route path="/pin" element={<Enterpin1/>}></Route>
     
                </Routes>
            </Grid>
          </div>
        </ThemeProvider>
      </AppProvider>
    </div>
  );
}

export default App;

