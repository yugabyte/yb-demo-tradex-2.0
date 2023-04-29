import React from 'react'
import { useNavigate } from "react-router-dom";
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import HomeIcon from '@mui/icons-material/Home';
import PermMediaIcon from '@mui/icons-material/PermMedia';
import RepeatIcon from '@mui/icons-material/Repeat';
import RestoreIcon from '@mui/icons-material/Restore';
import AccountCircnleIco from '@mui/icons-material/AccountCircle';
import "./bottom1.css";
import "@fontsource/poppins";


function Bottommenu(){
  const [value, setValue] = React.useState('recents');
  const navigate = useNavigate();
  const handleChange = (event, newValue) => {
    setValue(newValue);
    let path = newValue.toLowerCase();
    if (path === 'home' || path === 'portfolio' ) {
      navigate("/dashboard");
    }
    else if (path === 'profile') {
      navigate("/" + path);
    }
    else if ( path === 'exchange'){
      navigate("/exch");
    }
    else if ( path === 'history'){
      navigate("/history");
    }
  };
    return (
      <>
        <BottomNavigation showLabels value={value} onChange={handleChange} >
          <BottomNavigationAction
            sx={{ minWidth: "0 !important", height: "90 !important" }}
            label="Home"
            value="home"
            icon={<HomeIcon />}
          />

          <BottomNavigationAction
            sx={{ minWidth: "0 !important", height: "90 !important" }}
            label="Portfolio"
            value="portfolio"
            icon={<PermMediaIcon />}
          />

          <BottomNavigationAction
            sx={{
              minWidth: "0 !important",
              borderRadius: 30,
              height: "70px !important",
              boxShadow: "0px 19px 38px #bfbfbf4d, 0px 15px 12px #7272724d",
              bottom: "24px",
              padding: "0 14px !important",
              marginBottom: 0,
              backgroundColor: "#fff"
            }}
            label="Exchange"
            value="exchange"
            icon={<RepeatIcon />}
          />

          <BottomNavigationAction
            sx={{ minWidth: "0 !important", height: "90 !important" }}
            label="History"
            value="history"
            icon={< RestoreIcon />}
          />

          <BottomNavigationAction
            sx={{ minWidth: "0 !important", height: "90 !important" }}
            label="Profile"
            value="Profile"
            icon={<AccountCircnleIco />} />
        </BottomNavigation>
        </>
    )
  }

export default Bottommenu;





